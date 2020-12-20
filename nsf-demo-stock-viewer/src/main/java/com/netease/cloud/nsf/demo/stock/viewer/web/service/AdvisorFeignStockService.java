package com.netease.cloud.nsf.demo.stock.viewer.web.service;

import com.netease.cloud.nsf.demo.stock.viewer.web.entity.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


//@FeignClient(name = "nsf-demo-stock-advisor", url = "http://127.0.0.1:9002")
@FeignClient(name = "nsf-demo-stock-advisor")
public interface AdvisorFeignStockService {

    @RequestMapping(method = RequestMethod.GET, value = "/advices/hot")
    public List<Stock> getHotStockAdvice();

    @RequestMapping(method = RequestMethod.GET, value = "/echo")
    public String echoAdvisor(@RequestParam("p") int p);

    @RequestMapping(method = RequestMethod.GET, value = "/echobyecho")
    public String echobyecho(@RequestParam("p") int p);
}
