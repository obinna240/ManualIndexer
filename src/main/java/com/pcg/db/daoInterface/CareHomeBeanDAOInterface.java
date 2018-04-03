package com.pcg.db.daoInterface;

import java.util.Map;

import com.pcg.db.model.ClassObject;

public interface CareHomeBeanDAOInterface<T extends ClassObject>  {
	
	public Map<String, Object> getDataFromDB(Class<T> mappedClass,String scriptFileName);
	public Map<String, Object> getDataFromDB(Class<T> mappedClass);
}


