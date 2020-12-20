package com.netease.cloud.nsf.demo.stock.viewer.web.service;

import java.util.List;

import com.netease.cloud.nsf.demo.stock.viewer.web.entity.Stock;


public interface IStockService {

	public List<Stock> getStockList(int delay) throws Exception;
	
	public Stock getStockById(String stockId) throws Exception;
	
	public List<Stock> getHotStockAdvice() throws Exception;
	
	public String echoAdvisor(int times);
	
	public String echoProvider(int times);

	public String echobyecho(int times);
	
	public String deepInvoke(int times);

	public String getMaxSpreadStock();

	public String getPredictPriceById(String stockId);
}
