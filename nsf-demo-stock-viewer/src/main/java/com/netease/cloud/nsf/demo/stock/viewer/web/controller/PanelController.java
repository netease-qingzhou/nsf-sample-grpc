package com.netease.cloud.nsf.demo.stock.viewer.web.controller;

import com.netease.cloud.nsf.demo.stock.viewer.web.entity.HttpResponse;
import com.netease.cloud.nsf.demo.stock.viewer.web.manager.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PanelController {
    @GetMapping("/logs")
    @ResponseBody
    public HttpResponse getHttpLog() {
        return new HttpResponse(LogManager.logs());
    }

}
