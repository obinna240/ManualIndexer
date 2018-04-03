package com.pcg.db.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.pcg.db.daoInterface.CareHomeBeanDAOInterface;
import com.pcg.db.model.ClassObject;

public class CareHomeBeanDAO<T extends ClassObject> implements CareHomeBeanDAOInterface<T>,ResourceLoaderAware
{
	static Logger logger = Logger.getLogger(CareHomeBeanDAO.class);
	private ResourceLoader resourceLoader;
	private JdbcTemplate jdbcTemplate;
	
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	public Resource getResource(String location){
		return resourceLoader.getResource(location);
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<String,Object> getDataFromDB(Class mappedClass, String scriptFileName) //throws SQLException, IOException
	{
		Connection conn;
		Map<String,Object> ret  = null;
		//List<T> result = null;
		try{
			logger.info("Inside Dao Class getDataFromDB");
		
			conn = jdbcTemplate.getDataSource().getConnection();
			
			if (conn != null) {
				DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
				logger.debug("Driver name: " + dm.getDriverName());
				logger.debug("Driver version: " + dm.getDriverVersion());
				logger.debug("Product name: " + dm.getDatabaseProductName());
				logger.debug("Product version: " + dm.getDatabaseProductVersion());
			}
			
			Properties prop = new Properties();
			InputStream input = null;
			input = new FileInputStream("C:/config/HampshireIndexer/db.properties");
			prop.load(input);
			String databaseName = prop.getProperty("solrServer");
			String procedureName = prop.getProperty("procedureName");
			//Resource resource = getResource(scriptFileName);
			//InputStream is = resource.getInputStream();
			//File initialFile = new File(scriptFileName);
			//InputStream is = FileUtils.openInputStream(initialFile);
			
			//BufferedReader in = new BufferedReader(new InputStreamReader(is));
			//LineNumberReader lineNumberReader = new LineNumberReader(in);
			//String query =  ScriptUtils.readScript(lineNumberReader, "--", ";");
			//SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withCatalogName("bm2").withProcedureName("dbo.sp_HampsCareHomes");//.returningResultSet(parameterName, rowMapper);
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withCatalogName(databaseName).withProcedureName(procedureName);//.returningResultSet(parameterName, rowMapper);
			//logger.debug("Query to be executed:" + query);
			ret = jdbcCall.execute();
			
			//result = jdbcTemplate.query(query,new BeanPropertyRowMapper(mappedClass));
			//logger.info("Number of records returned from DB:" + result.size());
			logger.info("Number of records returned from DB:" + ret.size());
			input.close();
		}
		catch(SQLException e)
		{
			logger.error("Error occurred when executing the Query in db :" + e.getMessage());		
			//throw e;
		
		}
		catch(IOException ex)
		{
			
		}
		//catch(IOException e)
		//{
		//	logger.error("Error occurred when executing the Query in db :" + e.getMessage());	
			//throw e;
		//}

		//return  result;
		return ret;
	}
	
	/**
	 * 
	 * @param mappedClass
	 * @return Map<String,Object>
	 */
	public Map<String,Object> getDataFromDB(Class mappedClass) 
	{
		Connection conn;
		Map<String,Object> ret  = null;
	
		try{
			logger.info("Inside Dao Class getDataFromDB");
		
			conn = jdbcTemplate.getDataSource().getConnection();
			
			if (conn != null) {
				DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
				logger.info("Driver name: " + dm.getDriverName());
				logger.info("Driver version: " + dm.getDriverVersion());
				logger.info("Product name: " + dm.getDatabaseProductName());
				logger.info("Product version: " + dm.getDatabaseProductVersion());
			}

		
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withCatalogName("bm2").withProcedureName("dbo.sp_HampsCareHomes");//.returningResultSet(parameterName, rowMapper);
			//logger.debug("Query to be executed:" + query);
			ret = jdbcCall.execute();
			
			//List x = (List) ret.get("#result - set - 1");
			//Integer y = (Integer) ret.get("#update-count-1");
			//List x = (List) ret.get("#result-set-1");
			//String yyy = (String) ((Map<String, Object>) x.get(0)).get("Address");
			logger.info("Number of records returned from DB:" + ret.size());
		}
		catch(SQLException e)
		{
			logger.error("Error occurred when executing the Query in db :" + e.getMessage());		
				
		}

		return ret;
	}
	
}
