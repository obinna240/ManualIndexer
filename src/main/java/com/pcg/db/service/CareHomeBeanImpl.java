package com.pcg.db.service;


import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;


import com.pcg.db.daoInterface.CareHomeBeanService;
import com.pcg.db.daoInterface.CareHomeBeanDAOInterface;
import com.pcg.db.model.ClassObject;

@Service
public class CareHomeBeanImpl <T extends ClassObject> implements CareHomeBeanService<T>,ResourceLoaderAware 
{
	
	static Logger logger = Logger.getLogger(CareHomeBeanImpl.class);
	
	@Autowired private CareHomeBeanDAOInterface<T>  careHomeBeanDAO;
	private ResourceLoader resourceLoader;
	//private String scriptFilePath;
	
	//public String getScriptFilePath() 
	//{
		//return scriptFilePath;
	//}

	//public void setScriptFilePath(String scriptFilePath) {
		//this.scriptFilePath = scriptFilePath;
	//}
	
	
	public Resource getResource(String location){
		return resourceLoader.getResource(location);
	}
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<String, Object> getDataFromDb(Class<T> mappedClass,String scriptFileName)
	{
		
		logger.info("Inside Service Class -- for DB execution");
		
		Map<String, Object> resultList = null;
	
		resultList = careHomeBeanDAO.getDataFromDB(mappedClass,scriptFileName);
	
		return  resultList ;
	}
	
	@Override
	public Map<String, Object> getDataFromDb(Class<T> mappedClass)
	{
		
		logger.info("Inside Service Class -- for DB execution");
		
		Map<String, Object> resultList = null;
	
		resultList = careHomeBeanDAO.getDataFromDB(mappedClass);
	
		return  resultList ;
	}

}
