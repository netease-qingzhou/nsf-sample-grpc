package com.netease.cloud.nsf.demo.stock.viewer.web.service;

import com.netease.cloud.nsf.demo.stock.viewer.web.entity.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "nsf-demo-stock-provider", url = "http://127.0.0.1:9001")
@FeignClient(name = "nsf-demo-stock-provider")
public interface ProviderFeignStockService {

    @RequestMapping(method = RequestMethod.GET, value = "/stocks")
    public List<Stock> getStockList(@RequestParam("delay") int delay);

    @RequestMapping(method = RequestMethod.GET, value = "/stocks/{stockId}")
    public Stock getStockById(@PathVariable String stockId);

    @RequestMapping(method = RequestMethod.GET, value = "/echo")
    public String echoProvider(@RequestParam("p") int p);

    @RequestMapping(method = RequestMethod.GET, value = "/echobyecho")
    public String echobyecho(@RequestParam("p") int p);
}