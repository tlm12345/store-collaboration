package com.tlm.storecollab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * store-collaboration Project
 */
@SpringBootApplication
@MapperScan("com.tlm.storecollab.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
public class StoreCollabApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreCollabApplication.class, args);
    }
}
