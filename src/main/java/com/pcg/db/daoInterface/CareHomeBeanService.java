package com.pcg.db.daoInterface;


//import java.util.List;
import java.util.Map;

import com.pcg.db.model.ClassObject;


public interface CareHomeBeanService<T extends ClassObject> {
	
	public Map<String, Object> getDataFromDb(Class<T> mappedClass,String scriptFileName);
		
	public Map<String, Object> getDataFromDb(Class<T> mappedClass);
	
	
}
