package com.alibaba.ageiport.processor.core.task;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author lingyi
 */
@Getter
@Setter
public abstract class AbstractSubTaskWorker implements SubTaskWorker {

    private AgeiPort ageiPort;

    private SubTask subTask;



}
