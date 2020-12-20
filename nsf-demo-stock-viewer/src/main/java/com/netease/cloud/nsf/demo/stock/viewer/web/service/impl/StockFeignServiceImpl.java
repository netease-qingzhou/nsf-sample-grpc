package com.netease.cloud.nsf.demo.stock.viewer.web.service.impl;

import com.netease.cloud.nsf.demo.stock.viewer.web.entity.Stock;
import com.netease.cloud.nsf.demo.stock.viewer.web.service.AdvisorFeignStockService;
import com.netease.cloud.nsf.demo.stock.viewer.web.service.IStockService;
import com.netease.cloud.nsf.demo.stock.viewer.web.service.ProviderFeignStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("StockFeignServiceImpl")
public class StockFeignServiceImpl implements IStockService {

    @Autowired
    private AdvisorFeignStockService advisorFeignStockService;
    @Autowired
    private ProviderFeignStockService providerFeignStockService;

    @Override
    public List<Stock> getStockList(int delay) throws Exception {
        return providerFeignStockService.getStockList(delay);
    }

    @Override
    public Stock getStockById(String stockId) throws Exception {
        return providerFeignStockService.getStockById(stockId);
    }

    @Override
    public List<Stock> getHotStockAdvice() throws Exception {
        return advisorFeignStockService.getHotStockAdvice();
    }

    @Override
    public String echoAdvisor(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(advisorFeignStockService.echoAdvisor(times));
        }
        return sBuilder.toString();
    }

    @Override
    public String echoProvider(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(providerFeignStockService.echoProvider(times));
        }
        return sBuilder.toString();
    }


    @Override
    public String echobyecho(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(advisorFeignStockService.echobyecho(times));
        }
        return sBuilder.toString();
    }


    @Override
    public String deepInvoke(int times) {
        return null;
    }

    @Override
    public String getMaxSpreadStock() {
        return null;
    }

    @Override
    public String getPredictPriceById(String stockId) {
        return null;
    }
}
