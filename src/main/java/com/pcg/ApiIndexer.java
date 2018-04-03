package com.pcg;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.pcg.db.service.CareHomeBeanImpl;



@Component
public class ApiIndexer 
{
	@Autowired private CareHomeBeanImpl careHomeBeanService;
	static Logger logger = Logger.getLogger(ApiIndexer .class);
	
	/**
	 * 
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> prepareToIndex()
	{
		logger.info("ApiIndexer.class --preparing to index");
		Map<String, Object> queryObject = careHomeBeanService.getDataFromDb(CareHomeBean.class);
		return queryObject;
	}
	
	
	/**
	public static void main(String[] args)
	{
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
				
		ApiIndexer apiIndexer = context.getBean(ApiIndexer.class);
		apiIndexer.prepareToIndex();
		
	}
	*/
}
