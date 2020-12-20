package com.netease.cloud.nsf.demo.stock.viewer.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netease.cloud.nsf.demo.stock.api.WallService;
import com.netease.cloud.nsf.demo.stock.viewer.web.entity.HttpResponse;
import com.netease.cloud.nsf.demo.stock.viewer.web.entity.Stock;
import com.netease.cloud.nsf.demo.stock.viewer.web.manager.LogManager;
import com.netease.cloud.nsf.demo.stock.viewer.web.service.IStockService;
import com.netease.cloud.nsf.demo.stock.viewer.web.service.impl.StockFeignServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;


/**
 * @author Chen Jiahan | chenjiahan@corp.netease.com
 */
@Controller()
@RequestMapping("/feign")
public class PanelFeignController {

    private static Logger log = LoggerFactory.getLogger(PanelFeignController.class);

    @Qualifier("StockFeignServiceImpl")
    @Autowired
    StockFeignServiceImpl stockService;

    @Reference
    WallService wallService;
    
    @Value("${dubbo:false}")
    String dubbo_switch;


    @GetMapping("/exception")
    @ResponseBody
    public String exception(String msg) {
        if(!StringUtils.isEmpty(msg)){
            throw new RuntimeException(msg);
        }
        return "no exception";
    }


    @GetMapping(value = "/stocks", produces = "application/json")
    @ResponseBody
    public HttpResponse getStockList(@RequestParam(name = "delay", required = false, defaultValue = "0") int delay) {

        List<Stock> stocks;
        try {
            stocks = stockService.getStockList(delay);
        } catch (Exception e) {
            log.warn("get stock list failed ...");
            log.warn("", e);
            return handleExceptionResponse(e);
        }
        return new HttpResponse(stocks);
    }

    @GetMapping(value = "/advices/hot", produces = "application/json")
    @ResponseBody
    public HttpResponse getHotAdvice() {

        List<Stock> stocks;
        try {
            stocks = stockService.getHotStockAdvice();
        } catch (Exception e) {
        	e.printStackTrace();
            log.warn("get hot stock advice failed ...");
            log.warn("", e);
            return handleExceptionResponse(e);
        }
        return new HttpResponse(stocks);
    }

    @GetMapping("/stocks/{stockId}")
    @ResponseBody
    public Stock getStockById(@PathVariable String stockId) {

        Stock stock = null;
        try {
            stock = stockService.getStockById(stockId);
        } catch (Exception e) {
        	e.printStackTrace();
            log.warn("get stock[{}] info failed ...", stockId);
        }
        return stock;
    }

    @GetMapping("/logs")
    @ResponseBody
    public HttpResponse getHttpLog() {
    	return new HttpResponse(LogManager.logs());
    }
    
    @GetMapping("/logs/clear")
    @ResponseBody
    public HttpResponse clearLogs() {
    	LogManager.clear();
    	return new HttpResponse("clear logs success");
    }
    
    @GetMapping("/echo/advisor")
    @ResponseBody
    public HttpResponse echoAdvisor(HttpServletRequest request,
    		@RequestParam(name = "time", defaultValue = "10", required = false) int time) {
    	String result = stockService.echoAdvisor(time);
    	LogManager.put(UUID.randomUUID().toString(), result);
    	return new HttpResponse(result);
    }
    
    @GetMapping("/echo/provider")
    @ResponseBody
    public HttpResponse echoProvider(HttpServletRequest request,
    		@RequestParam(name = "time", defaultValue = "10", required = false) int time) {
    	String result = stockService.echoProvider(time);
    	LogManager.put(UUID.randomUUID().toString(), result);
    	return new HttpResponse(result);
    }

    @GetMapping("/echobyecho")
    @ResponseBody
    public HttpResponse echobyecho(HttpServletRequest request,
                                   @RequestParam(name = "time", defaultValue = "10", required = false) int time) {
        String result = stockService.echobyecho(time);
        LogManager.put(UUID.randomUUID().toString(), result);
        return new HttpResponse(result);
    }

    
    @GetMapping("/health")
    @ResponseBody
    public String health() {
    	return "I am good!";
    }
    
    @RequestMapping("/deepInvoke")
    @ResponseBody
    public String deepInvoke(@RequestParam int times) {
    	return stockService.deepInvoke(times);
    }
    
    private HttpResponse handleExceptionResponse(Exception e) {
        NsfExceptionUtil.NsfExceptionWrapper nsfException = NsfExceptionUtil.parseException(e);
        log.error(nsfException.getThrowable().getMessage());
        if(nsfException.getType() == NsfExceptionUtil.NsfExceptionType.NORMAL_EXCEPTION){
            return new HttpResponse(nsfException.getThrowable().getMessage());
        }
        return new HttpResponse(nsfException.getType().getDesc());
    }

    //最大/最小差价
    @GetMapping("/spread")
    @ResponseBody
    public String getMaxSpreadStock() {
        String MaxSpreadStock=null;
        try {
            MaxSpreadStock=stockService.getMaxSpreadStock();
        }
        catch (Exception e){
            log.warn("get max stock spread failed ...");
        }
        return MaxSpreadStock;
    }

    //预测股票数据
    @GetMapping("/predictPrice/{stockId}")
    @ResponseBody
    public String getPredictPriceById(@PathVariable String stockId) {
        String PredictPrice=null;
        try {
            PredictPrice=stockService.getPredictPriceById(stockId);
        }
        catch (Exception e){
            log.warn("get predict stock price failed ...");
        }
        return PredictPrice;
    }
}