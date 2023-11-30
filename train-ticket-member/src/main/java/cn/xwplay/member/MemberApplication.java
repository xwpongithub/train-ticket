package cn.xwplay.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Indexed;

@SpringBootApplication
@Indexed
@Slf4j
public class MemberApplication {
    public static void main(String[] args) {
        var app = SpringApplication.run(MemberApplication.class,args);
        var envs = app.getEnvironment();
        log.info("会员模块启动成功并监听在{}端口,测试地址:http://127.0.0.1:{}{}!",
                envs.getProperty("server.port"),
                envs.getProperty("server.port"),
                envs.getProperty("server.servlet.context-path"));
    }

}
