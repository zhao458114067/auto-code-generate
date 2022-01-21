package com.zxcode.generatecode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 * @author zhaoxu
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AutoGenerateCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoGenerateCodeApplication.class, args);
    }

}
