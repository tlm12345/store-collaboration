package com.tlm.storecollab.service.impl;
import cn.hutool.core.util.ObjUtil;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskResponse.Output;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.api.aliyunapi.AliYunClient;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskResponse;
import com.tlm.storecollab.api.aliyunapi.model.QueryOutPaintingTaskResponse;
import com.tlm.storecollab.api.aliyunapi.model.TaskStatusEnum;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.entity.Task;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.service.TaskService;
import com.tlm.storecollab.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 12483
* @description 针对表【task(任务)】的数据库操作Service实现
* @createDate 2025-05-31 16:12:58
*/
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Resource
    private AliYunClient aliYunClient;

    @Override
    public Task createOutPaintingTask(CreateOutPaintingTaskRequest request, User loginUser) {
//         校验用户是否登录以及是否有权限（目前只要用户登录都有权限）
        ThrowUtils.throwIf(ObjectUtil.isEmpty(loginUser), ErrorCode.NOT_LOGIN, "用户未登录");
//         服务器发送http请求到API平台，获取创建任务响应结果。
         CreateOutPaintingTaskResponse response = aliYunClient.createOutPaintingTask(request);
//         将相应结果保存到数据库中。
        Task task = createOutPaintingTaskResponseToTask(response);
        task.setUserId(loginUser.getId());
        boolean save = this.save(task);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);

        task = this.getById(task.getId());

        return task;
    }

    @Override
    public Task queryOutPaintingResult(String taskId) {
        // 校验参数不为空
//         服务器查询数据库，判断状态，如果为pending(任务排队中)或者Running(任务处理中)，则下一步；否则，直接返回给用户数据库结果。
        Task task = this.lambdaQuery().eq(Task::getTaskId, taskId).one();
        ThrowUtils.throwIf(ObjUtil.isEmpty(task),  ErrorCode.PARAMS_ERROR, "任务Id不存在");
        String oldTaskStatus = task.getTaskStatus();
        if  (!(TaskStatusEnum.PENDING.getValue().equals(oldTaskStatus) || TaskStatusEnum.RUNNING.getValue().equals(oldTaskStatus))){
            // 不是 在 排队 或者 处理, 说明任务已经处理完了，或者因为不可知原因无法处理
            return task;
        }
//         发送查询请求到api平台，获取最新结果
        QueryOutPaintingTaskResponse queryOutPaintingTaskResponse = null;
        try {
            queryOutPaintingTaskResponse = aliYunClient.queryOutPaintingTask(taskId);

        }catch (Exception e){
            log.error("查询任务状态出错. task_id : {} ,error_msg: {}", taskId, e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 校验响应结果
        ThrowUtils.throwIf(ObjUtil.isEmpty(queryOutPaintingTaskResponse), ErrorCode.SYSTEM_ERROR);
        TaskStatusEnum taskStatus = queryOutPaintingTaskResponse.getOutput().getTaskStatus();

//      判断和当前数据库的task_status是否一致，不一致，更新数据库后返回结果；一致，直接返回响应结果，对数据库不做更新。
        // 状态没有发生变化
        if (ObjUtil.equals(oldTaskStatus, taskStatus)){
            return task;
        }
        Task newTask = new Task();
        newTask.setId(task.getId());
        // 状态发生变化，更改数据库
        newTask.setTaskStatus(taskStatus.getValue());
        // 如果状态变为成功
        if (TaskStatusEnum.SUCCEEDED.equals(taskStatus)){
            // 设置 图片地址
            newTask.setImageUrl(queryOutPaintingTaskResponse.getOutput().getOutputImageUrl());
        }
        // 如果状态为失败
        if (TaskStatusEnum.FAILED.equals(taskStatus)) {
            // 设置code和message字段
            newTask.setCode(queryOutPaintingTaskResponse.getOutput().getCode());
            newTask.setMessage(queryOutPaintingTaskResponse.getOutput().getMessage());
        }
        // 更改数据库
        boolean b = this.updateById(newTask);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);

        // 再次查询数据库
        Task t = this.lambdaQuery().eq(Task::getTaskId, taskId).one();
        return t;
    }

    @Override
    public Task createOutPaintingTaskResponseToTask(CreateOutPaintingTaskResponse response) {
        if (response == null) return null;

        Output output = response.getOutput();
        String requestId = response.getRequestId();
        String code = response.getCode();
        String message = response.getMessage();

        Task task = new Task();
        task.setRequestId(requestId);
        // 判断请求是否失败 (如果失败，code和message不为空： 如果成功， 则没有code和message
        if (ObjectUtil.isEmpty(code) || ObjectUtil.isEmpty(message)){
             task.setTaskStatus(output.getTaskStatus());
             task.setTaskId(output.getTaskId());
        }else {
            task.setCode(response.getCode());
            task.setMessage(response.getMessage());
        }

        return task;
    }
}




