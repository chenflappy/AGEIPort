package com.alibaba.ageiport.processor.core.spi.api;

import com.alibaba.ageiport.common.function.Handler;
import com.alibaba.ageiport.processor.core.spi.api.model.*;

/**
 * @author lingyi
 */
public interface ApiServer {

    void executeTask(ExecuteMainTaskRequest request, Handler<ExecuteMainTaskResponse> asyncResponseHandler);

    void getTaskProgress(GetMainTaskProgressRequest request, Handler<GetMainTaskProgressResponse> asyncResponseHandler);

    void executeSyncExtension(SyncExtensionApiRequest request, Handler<SyncExtensionApiResponse> syncExtensionResponseHandler);

}
