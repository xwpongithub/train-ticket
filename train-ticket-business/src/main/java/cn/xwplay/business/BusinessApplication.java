package cn.xwplay.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Indexed;

@SpringBootApplication(
        scanBasePackages = {
                "cn.xwplay.common",
                "cn.xwplay.business"
        }
)
@Indexed
@EnableFeignClients("cn.xwplay.business.feign")
@Slf4j
public class BusinessApplication {
    public static void main(String[] args) {
        var app = SpringApplication.run(BusinessApplication.class,args);
        var envs = app.getEnvironment();
        log.info("买票模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
    }

}
