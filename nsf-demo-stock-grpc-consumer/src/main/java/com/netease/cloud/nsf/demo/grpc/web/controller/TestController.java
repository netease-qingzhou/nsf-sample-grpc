package com.netease.cloud.nsf.demo.grpc.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    private String test(){
        return "test";
    }
}
