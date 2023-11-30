package cn.xwplay.member.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public String sayHello(String mobile) {
        return mobile + " Hello world";
    }

    @PostMapping("1")
    public String sayHello1(@RequestBody Person person) {
        return person.getName() + " Hello world";
    }

    @Data
    public static class Person {

        private String mobile;
        private String name;
    }

}
