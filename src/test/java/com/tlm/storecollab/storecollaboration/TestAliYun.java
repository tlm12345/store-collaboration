package com.tlm.storecollab.storecollaboration;

import cn.hutool.json.JSONUtil;
import com.tlm.storecollab.api.aliyunapi.AliYunClient;
import com.tlm.storecollab.api.aliyunapi.OutPaintingTaskApi;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskResponse;
import com.tlm.storecollab.api.aliyunapi.model.QueryOutPaintingTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
@Slf4j
public class TestAliYun {

    @Resource
    private AliYunClient aliYunClient;

    @Test
    public void test() {

        CreateOutPaintingTaskRequest request = new CreateOutPaintingTaskRequest();
        request.setInput(new CreateOutPaintingTaskRequest.Input());
        request.getInput().setImageUrl("https://tse4-mm.cn.bing.net/th/id/OIP-C.kYXplC128hQqAwYJ3oR-4wHaE7?rs=1&pid=ImgDetMain");
        request.setModel("image-out-painting");
        // 设置请求的参数parameters
        request.setParameters(new CreateOutPaintingTaskRequest.Parameters());
        request.getParameters().setXScale(2.0f);
        request.getParameters().setYScale(2.0f);
        request.getParameters().setBestQuality(false);
        request.getParameters().setLimitImageSize(true);
        // 参数设置完毕，调用发送任务请求函数
        CreateOutPaintingTaskResponse outPaintingTask = aliYunClient.createOutPaintingTask(request);
        log.info("任务创建请求发送，响应结果为: {}", JSONUtil.toJsonStr(outPaintingTask));
        log.info("任务创建成功，任务ID：{}", outPaintingTask.getOutput().getTaskId());
    }

    @Test
    public void test2(){
        QueryOutPaintingTaskResponse queryOutPaintingTaskResponse = aliYunClient.queryOutPaintingTask("138484dd-a3d6-4d11-989d-2408026f3e83");
        log.info("查询结果如下: {}", JSONUtil.toJsonStr(queryOutPaintingTaskResponse));
    }
}
