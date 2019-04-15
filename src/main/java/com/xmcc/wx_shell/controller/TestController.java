package com.xmcc.wx_shell.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/hello")
    public String test_1(){
        System.out.println("hello spring boot");
        return "Hello Spring-Boot";
    }
}
