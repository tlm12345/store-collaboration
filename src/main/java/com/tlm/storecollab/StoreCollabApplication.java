package com.tlm.storecollab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tlm.storecollab.mapper")
public class StoreCollabApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreCollabApplication.class, args);
    }
}
