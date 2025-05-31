package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskResponse;
import com.tlm.storecollab.api.aliyunapi.model.QueryOutPaintingTaskResponse;
import com.tlm.storecollab.model.entity.Task;
import com.tlm.storecollab.model.entity.User;

/**
* @author 12483
* @description 针对表【task(任务)】的数据库操作Service
* @createDate 2025-05-31 16:12:58
*/
public interface TaskService extends IService<Task> {

    /**
     * 创建扩图任务
     * @param request
     * @param loginUser
     * @return
     */
    Task createOutPaintingTask(CreateOutPaintingTaskRequest request, User loginUser);

    /**
     * 查询扩图任务结果
     * @param taskId
     * @return
     */
    Task queryOutPaintingResult(String taskId);

    /**
     * 将CreateOutPaintingTaskResponse转换为Task对象
     */
    Task createOutPaintingTaskResponseToTask(CreateOutPaintingTaskResponse response);

}
