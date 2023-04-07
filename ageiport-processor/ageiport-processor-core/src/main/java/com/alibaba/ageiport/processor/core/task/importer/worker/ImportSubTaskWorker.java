package com.alibaba.ageiport.processor.core.task.importer.worker;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ConstValues;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.task.AbstractSubTaskWorker;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.adapter.ImportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportSubTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResultImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;

import java.util.*;

/**
 * @author lingyi
 */
public class ImportSubTaskWorker<QUERY, DATA, VIEW> extends AbstractSubTaskWorker {

    public static Logger log = LoggerFactory.getLogger(ImportMainTaskWorker.class);

    @Override
    public void doMappingProcess() {
        AgeiPort ageiPort = this.getAgeiPort();
        SubTask subTask = getSubTask();
        try {
            String subTaskId = subTask.getSubTaskId();
            String executeType = subTask.getExecuteType();
            String taskType = subTask.getType();
            String taskCode = subTask.getCode();

            TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
            SubTaskContextFactory contextFactory = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskContextFactory.class);
            SubTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskStageProvider.class);

            EventBus eventBus = ageiPort.getEventBusManager().getEventBus(executeType);
            eventBus.post(TaskStageEvent.subTaskEvent(subTaskId, stageProvider.subTaskDispatchedOnNode()));

            ImportSubTaskContext<QUERY, DATA, VIEW> context = (ImportSubTaskContext) contextFactory.create(ageiPort, subTaskId);
            context.setStage(stageProvider.subTaskStart());

            ImportTaskSpecification<QUERY, DATA, VIEW> importTaskSpec = context.getImportTaskSpec();
            ImportProcessor<QUERY, DATA, VIEW> processor = importTaskSpec.getImportProcessor();
            ImportProcessorAdapter<QUERY, DATA, VIEW> adapter = (ImportProcessorAdapter) processor.getConcreteAdapter();

            BizUser bizUser = context.getBizUser();
            QUERY query = context.getQuery();

            context.goNextStageEventNew();
            BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(executeType);
            String key = subTaskId + ConstValues.CACHE_SUFFIX_INPUT_DATA_SLICE;
            DataGroup dataGroup = cache.get(key, DataGroup.class);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            BizDataGroup<VIEW> bizDataGroup = adapter.getBizDataGroup(bizUser, query, dataGroup, processor, context);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            List<VIEW> views = adapter.flat(bizUser, query, bizDataGroup, processor, context);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            BizImportResult<VIEW, DATA> convertAndCheckResult = adapter.convertAndCheck(bizUser, query, views, processor, context);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            List<DATA> data = convertAndCheckResult.getData();
            BizImportResult<VIEW, DATA> writeResult = adapter.write(bizUser, query, data, processor, context);
            context.goNextStageEventNew();

            BizImportResultImpl<VIEW, DATA> importResult = new BizImportResultImpl<>();
            List<VIEW> resultView = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(convertAndCheckResult.getView())) {
                resultView.addAll(convertAndCheckResult.getView());
            }
            if (CollectionUtils.isNotEmpty(writeResult.getView())) {
                resultView.addAll(writeResult.getView());
            }
            importResult.setView(resultView);

            context.goNextStageEventNew();
            BizDataGroup<VIEW> viewBizDataGroup = adapter.group(bizUser, query, resultView, processor, context);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            DataGroup outputDataGroup = adapter.getDataGroup(bizUser, query, viewBizDataGroup, processor, context);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            List<DataGroup.Data> groupData = outputDataGroup.getData();

            if (CollectionUtils.isNotEmpty(groupData)) {
                boolean hasItems = false;
                for (DataGroup.Data groupDatum : groupData) {
                    if (CollectionUtils.isNotEmpty(groupDatum.getItems())) {
                        hasItems = true;
                    }
                }
                if (hasItems) {
                    cache.put(subTaskId, outputDataGroup);
                }
            }
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            context.assertCurrentStage(stageProvider.subTaskFinished());
            int subCountTotal = 0;
            if(Objects.nonNull(writeResult)){
                List<DATA> writeData = writeResult.getData();
                if(CollectionUtils.isNotEmpty(writeData)){
                    int len = writeData.size();
                    subCountTotal+=len;
                    subTask.setDataSuccessCount(len);
                }
                List<VIEW> errorData = writeResult.getView();
                if(CollectionUtils.isNotEmpty(errorData)){
                    int len = errorData.size();
                    subCountTotal+=len;
                    subTask.setDataFailedCount(len);
                }
            }
            subTask.setDataTotalCount(subCountTotal);
            onFinished(context,subTask);
        } catch (Throwable e) {
            log.info("doMappingProcess failed, main:{}, sub:{}", subTask.getMainTaskId(), subTask.getSubTaskId(), e);
            subTask.setStatus(TaskStatus.ERROR).setResultMessage(e.getMessage()).setGmtFinished(new Date());
            ageiPort.getTaskServerClient().updateSubTask(subTask);
            MainTask mainTask = new MainTask().setMainTaskId(subTask.getMainTaskId())
                    .setStatus(TaskStatus.ERROR).setGmtFinished(new Date()).setResultMessage(e.getMessage());
            ageiPort.getTaskServerClient().updateMainTask(mainTask);

            EventBus eventBus = ageiPort.getEventBusManager().getEventBus(subTask.getExecuteType());
            eventBus.post(TaskStageEvent.subTaskEvent(subTask.getSubTaskId(), CommonStage.ERROR));
            eventBus.post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), CommonStage.ERROR));
        }

    }
}
