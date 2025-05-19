package com.tlm.storecollab.storecollaboration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.tlm.storecollab.manager.FileManager;

@SpringBootTest
public class TestURLUpload {

    @Test
    public void test(){
        FileManager.validPicture("https://pic1.zhimg.com/v2-a58fa2ab84be291418da2652805f8270_b.jpg");
    }
}
