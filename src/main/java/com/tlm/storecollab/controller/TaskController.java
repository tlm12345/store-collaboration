package com.tlm.storecollab.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.model.entity.Task;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.TaskVO;
import com.tlm.storecollab.service.TaskService;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 任务接口
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private UserService userService;

    @Resource
    private TaskService taskService;


    @PostMapping("/create")
    public BaseResponse<TaskVO> createOutPaintingTask(@RequestBody CreateOutPaintingTaskRequest createOutPaintingTaskRequest, HttpServletRequest request){
        // 校验请求参数不为空
         ThrowUtils.throwIf(ObjectUtil.isEmpty(createOutPaintingTaskRequest), ErrorCode.PARAMS_ERROR, "创建任务请求参数不能为空");
        // 获取当前登录用户,并校验已经登录
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN);
        // 发送请求, 获取task实体类
         Task task = taskService.createOutPaintingTask(createOutPaintingTaskRequest, loginUser);
        // 转换为 TaskVO并返回
        TaskVO taskVO = TaskVO.objToVO(task);
        return ResultUtils.success(taskVO);
    }
    @GetMapping("/get")
    public BaseResponse<TaskVO> getTaskByTaskId(String taskId){
        // 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(taskId), ErrorCode.PARAMS_ERROR);
        // 查询任务状态
        Task task = taskService.queryOutPaintingResult(taskId);
        // 返回响应结果
        return  ResultUtils.success(TaskVO.objToVO(task));
    }
}