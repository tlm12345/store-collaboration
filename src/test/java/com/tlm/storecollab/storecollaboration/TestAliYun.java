package com.tlm.storecollab.storecollaboration;

import cn.hutool.json.JSONUtil;
import com.tlm.storecollab.api.aliyunapi.OutPaintingTaskApi;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.api.aliyunapi.model.QueryOutPaintingTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
@Slf4j
public class TestAliYun {

    @Resource
    private OutPaintingTaskApi outPaintingTaskApi;

    @Test
    public void test() {

        CreateOutPaintingTaskRequest request = new CreateOutPaintingTaskRequest();
        request.setInput(new CreateOutPaintingTaskRequest.Input());
        request.getInput().setImageUrl("https://img.shetu66.com/2023/04/25/1682391069844152.png");
        request.setModel("image-out-painting");
        // 设置请求的参数parameters
        request.setParameters(new CreateOutPaintingTaskRequest.Parameters());
        request.getParameters().setXScale(2.0f);
        request.getParameters().setYScale(2.0f);
        request.getParameters().setBestQuality(false);
        request.getParameters().setLimitImageSize(true);
        // 参数设置完毕，调用发送任务请求函数
        QueryOutPaintingTaskResponse queryOutPaintingTaskResponse = outPaintingTaskApi.testCreateAndQueryOutPaintingTask(request);
        log.info("任务创建成功，任务ID：{}", queryOutPaintingTaskResponse.getTaskId());
    }

    @Test
    public void test2(){
        OutPaintingTaskApi outPaintingTaskApi = new OutPaintingTaskApi();
        QueryOutPaintingTaskResponse queryOutPaintingTaskResponse = outPaintingTaskApi.queryOutPaintingTask("395aa0d3-53f1-4aec-a6bc-7e141c9ad85e");
        log.info("查询结果如下: {}", JSONUtil.toJsonStr(queryOutPaintingTaskResponse));
    }
}
