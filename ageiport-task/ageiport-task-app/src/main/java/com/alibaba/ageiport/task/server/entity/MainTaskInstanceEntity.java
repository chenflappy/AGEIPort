package com.alibaba.ageiport.task.server.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 主任务
 *
 * @author lingyi
 */
@Entity
@Table(name = "agei_main_task_instance",
        indexes = {
                @Index(name = "uk_mainTaskId", columnList = "mainTaskId", unique = true),
                @Index(name = "idx_main_task", columnList = "tenant,namespace,app,env,code"),
                @Index(name = "idx_bizUser", columnList = "bizUserTenant,bizUserOrg,bizUserId,bizUserKey"),
                @Index(name = "idx_gmt_create", columnList = "gmtCreate"),
                @Index(name = "idx_flowTaskId", columnList = "flowTaskId")
        }
)
@Getter
@Setter
public class MainTaskInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    /**
     * 租户
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String tenant;

    /**
     * 命名空间
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String namespace;

    /**
     * 应用
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String app;

    /**
     * 环境
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String env;

    /**
     * 业务任务名称
     */
    @Column(length = 64)
    private String bizTaskName;

    /**
     * 业务Key
     */
    @Column(length = 64)
    private String bizKey;

    /**
     * 业务用户查询参数
     */
    @Column(columnDefinition = "text default null")
    private String bizQuery;

    /**
     * 业务用户ID
     */
    @Column(length = 64)
    private String bizUserId;

    /**
     * 业务用户姓名
     */
    @Column(length = 64)
    private String bizUserName;

    /**
     * 业务用户所属租户
     */
    @Column(length = 64)
    private String bizUserTenant;

    /**
     * 业务用户所属组织
     */
    @Column(length = 64)
    private String bizUserOrg;

    /**
     * 业务用户key
     */
    @Column(length = 64)
    private String bizUserKey;

    /**
     * 业务用户自定义扩展属性
     */
    @Column(length = 512)
    private String bizUserFeature;

    /**
     * 任务组ID
     */
    @Column(length = 64)
    private String flowTaskId;

    /**
     * 流程顺序
     */
    private Integer flowOrder;

    /**
     * 任务ID
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String mainTaskId;

    /**
     * 任务编码
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String code;

    /**
     * 任务名称
     */
    @Column(length = 64)
    private String name;

    /**
     * 任务类型
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String type;

    /**
     * 任务执行类型
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String executeType;

    /**
     * 子任务处理总量
     */
    private Integer subTotalCount;

    /**
     * 子任务已处理量
     */
    private Integer subFinishedCount;

    /**
     * 子任务处理成功量
     */
    private Integer subSuccessCount;

    /**
     * 子任务处理失败量
     */
    private Integer subFailedCount;

    /**
     * 数据处理总量
     */
    private Integer dataTotalCount;

    /**
     * 数据已处理量
     */
    private Integer dataProcessedCount;

    /**
     * 数据处理成功量
     */
    private Integer dataSuccessCount;

    /**
     * 数据处理失败量
     */
    private Integer dataFailedCount;

    /**
     * 任务状态
     */
    @Column(length = 64, nullable = false)
    private String status;

    /**
     * 任务执行节点
     */
    @Column(length = 64)
    private String host;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 任务开始时间
     */
    private Date gmtStart;

    /**
     * 任务开始分发时间
     */
    private Date gmtDispatch;

    /**
     * 任务开始执行时间
     */
    private Date gmtExecute;

    /**
     * 任务结束时间
     */
    private Date gmtFinished;

    /**
     * 任务过期时间
     */
    private Date gmtExpired;

    /**
     * Trace
     */
    @Column(length = 64)
    private String traceId;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 错误Code
     */
    @Column(length = 64)
    private String resultCode;

    /**
     * 错误信息
     */
    @Column(length = 128)
    private String resultMessage;

    /**
     * rowStatus
     */
    @Column(length = 64)
    private String rowStatus;

    /**
     * rowVersion
     */
    private Integer rowVersion;

    /**
     * 任务日志
     */
    @Column(length = 4096)
    private String log;

    /**
     * 扩展字段，JSON格式
     */
    @Column(columnDefinition = "text default null")
    private String feature;

    /**
     * 任务运行时参数
     */
    @Column(columnDefinition = "text default null")
    private String runtimeParam;
}