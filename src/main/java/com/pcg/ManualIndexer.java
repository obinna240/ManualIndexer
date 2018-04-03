package com.pcg;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.pcg.util.ApplicationMailer;


/**
 * 
 * @author oonyimadu
 *
 */
public class ManualIndexer 
{
	@Autowired ApiIndexer apiIndexer;
	@Autowired ApplicationMailer applicationMailer;
	static Logger logger = Logger.getLogger(ManualIndexer.class);
	
	StringBuffer strBuffer;
	
	public void init()
	{
		logger.info("Starting initialization");
		ApplicationContext context = new FileSystemXmlApplicationContext("C:/config/HampshireIndexer/bean.xml");
		apiIndexer = context.getBean(ApiIndexer.class);
		applicationMailer = context.getBean(ApplicationMailer.class);
		strBuffer = new StringBuffer();
		String message = "============== INDEX REPORT FOR "+new Date()+" ========================";
		strBuffer.append(message);
		strBuffer.append("\n");
	
		//applicationMailer.sendPreConfiguredMail(strBuffer.toString());
	}
	
	
	
	static Object checkCell(HSSFCell cell)
	{
		Object obj = null;
		
			switch (cell.getCellType()) 
	        {
	            case Cell.CELL_TYPE_NUMERIC:
	               // System.out.print(cell.getNumericCellValue() + "t");
	                obj = cell.getNumericCellValue();
	                break;
	            case Cell.CELL_TYPE_STRING:
	              //  System.out.print(cell.getStringCellValue() + "t");
	                obj = cell.getStringCellValue();
	                break;
	        }
		
		return obj;
	}
	
	public void doIndex(String host, String port, String solrCore, String fileName, int start, int end) throws IOException, SolrServerException 
	{
		
		FileInputStream file = new FileInputStream(new File(fileName));
		POIFSFileSystem fs = new POIFSFileSystem(file);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
		
        if(StringUtils.isNotBlank(host)&&StringUtils.isNotBlank(port)&&StringUtils.isNotBlank(solrCore))
        {
	        if((start>=2) && (end <= 609))
	        {
	        for(int i=start;i<=end;i++)
	        {
	        	HSSFRow row = sheet.getRow(i);
	        	if(row!=null)
	        	{
	        		CareHomeBean cbean = new CareHomeBean();
	        		
	        		//String urlString =  "http://sctest1.cloudapp.net/solr/live";
	        		String urlString = "http://"+host+":"+port+"/solr/"+solrCore;//localhost:8984/solr/live";
	        		
	        		SolrClient solr = new HttpSolrClient(urlString);
	        		SolrInputDocument document = new SolrInputDocument();
	        		
	        		HSSFCell cell = row.getCell(0);
	        		if(cell!=null)
	        		{
	        			String id = (String) checkCell(cell);
	        			id = StringUtils.normalizeSpace(id);
		        		cbean.setId(id);
		        		document.addField("id", id);
	        		}
	        		
	        		cell = row.getCell(1);
	        		String name = (String) checkCell(cell);
	        		cbean.setName(name);
	        		document.addField("name", name);
	        		
	        		
	        		        		
	        		cell = row.getCell(2);
	        		if(cell!=null)
	        		{
	        			String address = (String) checkCell(cell);
	        			address = StringUtils.normalizeSpace(address);
	        			cbean.setAddress(address);
	        			document.addField("address", address);
	        		}
	        		
	        		String pcode1 = "";
	        		String pcode2 = "";
	        		String fullPostCode = "";
	        		
	        		cell = row.getCell(3);
	        		if(cell!=null)
	        		{
	        			pcode1 = (String) checkCell(cell);
	        			pcode1 = StringUtils.normalizeSpace(pcode1);
	        			cbean.setPostcode1(pcode1);
	        			document.addField("postcode1", pcode1);
	        			fullPostCode = pcode1+" ";
	        			
	        			
	        		}
	        		
	        		cell = row.getCell(4);
	        		if(cell!=null)
	        		{
	        			pcode2 = (String) checkCell(cell);
	        			pcode2 = StringUtils.normalizeSpace(pcode2);
	        			cbean.setPostcode2(pcode2);
	        			document.addField("postcode2", pcode2);
	        			fullPostCode = fullPostCode+pcode2;
	        		}
	        		
	        		if(StringUtils.isNotBlank(fullPostCode))
	        		{
	        			cbean.setFull_postcode(fullPostCode);
	        			document.addField("full_postcode", fullPostCode );
	        		}
	        		
	        		cell = row.getCell(5);
	        		if(cell!=null)
	        		{
	        			String phone = (String) checkCell(cell);
	        			phone = StringUtils.normalizeSpace(phone);
	        			cbean.setPhone(phone);
	        			document.addField("phone", phone);
	        		}
	        		
	        		cell = row.getCell(6);
	        		if(cell!=null)
	        		{
	        			String website = (String) checkCell(cell);
	        		
	        			cbean.setWebsite(website);
	        			document.addField("website", website);
	        		}
	        		
	        		cell = row.getCell(7);
	        		if(cell!=null)
	        		{
	        			String publicEmail = (String) checkCell(cell);
	        		
	        			cbean.setPublicEmail(publicEmail);
	        			document.addField("publicEmail", publicEmail);
	        		}
	        		
	        		cell = row.getCell(8);
	        		if(cell!=null)
	        		{
	        			String homeType = (String) checkCell(cell);
	        			homeType = StringUtils.normalizeSpace(homeType);
	        			if(homeType.equalsIgnoreCase("Care Home without nursing"))
	        			{
	        				homeType = "chwtn";
	        				cbean.setHomeType(homeType);
	        			}
	        			else if(homeType.equalsIgnoreCase("Care Home with nursing"))
	        			{
	        				homeType = "chwn";
	        				cbean.setHomeType(homeType);
	        			}
	        			else if(homeType.equalsIgnoreCase("Care Home offering both types of care"))
	        			{
	        				homeType = "chb";
	        				cbean.setHomeType(homeType);
	        			}
	        				
	        			document.addField("homeType", homeType);
	        		}
	        		List<String> admissions = new ArrayList<String>();
	        		String shortStay = null;
	        		String formalDayCare = null;
	        		String informalDayCare = null;
	        		String longStay = null;
	        		
	        		cell = row.getCell(9);
	        		if(cell!=null)
	        		{
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
		        			val = StringUtils.normalizeSpace(val);		
		        			if(val.equalsIgnoreCase("YES"))
		        			{
		        				shortStay = "ss";
		        				admissions.add(shortStay);
		        			}
	        			}
	        		}
	        		
	        		cell = row.getCell(10);
	        		if(cell!=null)
	        		{
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				formalDayCare = "fd";
	        				admissions.add(formalDayCare);
	        			}
	        			}
	        		}
	        		
	        		cell = row.getCell(11);
	        		if(cell!=null)
	        		{
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				informalDayCare = "ifd";
	        				admissions.add(informalDayCare);
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(12);
	        		if(cell!=null)
	        		{
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				longStay = "ls";
	        				admissions.add(longStay);
	        			}		
	        			}
	        		}
	        		
	        		
	        	
	        		
	        		
	        		
	        	
	        		cbean.setAdmissions(admissions);
	        		document.addField("admissions", admissions);
	        		
	        		String dementia = null;
	        		String mentalHealthConditions = null;
	        		String learningDisabilities = null;
	        		String oldAge = null;
	        		String physicalDisabilities = null;
	        		String sensoryImpairment = null;
	        		String ppAlcoholDependence = null;
	        		String ppDrugDependence = null;
	        		List<String> careProvided = new ArrayList<String>();
	        		
	        		cell = row.getCell(13);
	        		if(cell!=null)
	        		{
	        				
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				dementia = "de";
	        				careProvided.add(dementia);
	        			}
	        			}
	        		}
	        		
	        		cell = row.getCell(14);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				mentalHealthConditions = "mhc";
	        				careProvided.add(mentalHealthConditions );
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(15);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				learningDisabilities = "ld";
	        				careProvided.add(learningDisabilities );
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(16);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				oldAge = "oa";
	        				careProvided.add(oldAge);
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(16);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				physicalDisabilities = "pd";
	        				careProvided.add(physicalDisabilities);
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(17);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				sensoryImpairment = "si";
	        				careProvided.add(sensoryImpairment);
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(18);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				ppAlcoholDependence = "ppa";
	        				careProvided.add(ppAlcoholDependence);
	        			}	
	        			}
	        		}
	        		
	        		cell = row.getCell(19);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				ppDrugDependence = "ppd";
	        				careProvided.add(ppDrugDependence);
	        			}	
	        			}
	        		}
	        				
	        		
	        		
	        		       	
	        		
	        		cbean.setCareProvided(careProvided);
	        		document.addField("careProvided", careProvided);
	        		
	        		String location1 = "";
	        		String location2 = "";
	        		String location = "";
	        		
	        		cell = row.getCell(22);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			Double val = (Double) checkCell(cell);
	        				
	        			if(val!=null)
	        			{
	        				location1 = val.toString();
	        				location1 = location1+",";
	        			}	
	        		}
	        		
	        		cell = row.getCell(23);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			Double val = (Double) checkCell(cell);
	        				
	        			if(val!=null)
	        			{
	        				location2 = val.toString();
	        				
	        			}	
	        		}
	        		
	        		location = location1+location2;
	        		cbean.setLocation(location);
	        		document.addField("location", location);
	        		
	        		cell = row.getCell(25);
	        		if(cell!=null)
	        		{
	        	
	        			
	        			String val = (String) checkCell(cell);
	        			if(StringUtils.isNotBlank(val))
	        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			
	        			cbean.setTown(val);
	        			document.addField("town", val);
	        			}
	        		}
	        	
	        		
	        		document.addField("dateOfIndex",new Date());
	        		
	        		solr.add(document);
	        		solr.commit();
	        		solr.close();
	        		
	        		
	
	        	}
	        	}
	       }
      }
 
}
	
/**
 * 
 * @param returnedObject
 * @throws IOException
 * @throws SolrServerException
 */
public void doIndex() throws IOException, SolrServerException 
{
		init();
		logger.info("Preparing to start query");
		
		strBuffer.append("["+new Date()+"] Preparing to retrieve data from DB indexing ...");
		strBuffer.append("\n");
		Map<String, Object> returnedObject = apiIndexer.prepareToIndex(); //setup email
		logger.info("Start indexing");
		
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("C:/config/HampshireIndexer/db.properties");
		prop.load(input);
		String urlString = prop.getProperty("solrServer");
		logger.info("Retrieving solr url = "+urlString);
		
		if(StringUtils.isNotBlank(urlString))
		{
			//String urlString =  "http://sctest1.cloudapp.net/solr/live";
		
	        if(MapUtils.isNotEmpty(returnedObject))
	        {
	        	
	        	List listOfResults = (List) returnedObject.get("#result-set-1");
	        	Integer numberOfDocuments = (Integer) returnedObject.get("#update-count-1");
	        	
	        	strBuffer.append("["+new Date()+"] DB data retrieval was successful.");
	        	strBuffer.append("\n");
	        	strBuffer.append(numberOfDocuments+" "+"retrieved from DB");
	    		strBuffer.append("\n");
	        	
	        	if(CollectionUtils.isNotEmpty(listOfResults))
	        	{
	        		strBuffer.append("["+new Date()+"] Preparing to Delete previous Index ...");
	        		strBuffer.append("\n");
	        		SolrClient solr = new HttpSolrClient(urlString);
	        		
					solr.deleteByQuery("*:*");
					solr.commit();
					
					strBuffer.append("["+new Date()+"] Index deleted...");
					strBuffer.append("\n");
					strBuffer.append("\n");
					strBuffer.append("["+new Date()+"] Preparing to start Index ...");
					strBuffer.append("\n");
	        		for(Object result:listOfResults)
	        		{
	        			if(result!=null)
	        			{
	        				SolrInputDocument document = new SolrInputDocument();
	        				Map<String, Object> map = (Map<String, Object>) result;
	        				
	        				String id = (String) map.get("id");
	        				if(StringUtils.isNotBlank(id))
	        				{
	        					id= StringUtils.normalizeSpace(id);
	        					document.addField("id", id);
	        				}
	        				
	        				strBuffer.append("["+new Date()+"] Indexing document with id "+id);
	        				strBuffer.append("\n");
	        	    		       				
	        				
	        				String address = (String) map.get("address");
	        				if(StringUtils.isNotBlank(address))
	        				{
	        					address = StringUtils.normalizeSpace(address);
	        					document.addField("address", address);
	        				}
	        				
	        		
	        				
	        				String name = (String) map.get("name");
	        				if(StringUtils.isNotBlank(name))
	        				{
	        					name= StringUtils.normalizeSpace(name);
	        					document.addField("name", name);
	        				}
	        				
	        				String postcode1 = (String) map.get("postcode1");
	        				String postcode2 = (String) map.get("postcode2");
	        				
	        				if(StringUtils.isNotBlank(postcode1) && StringUtils.isNotBlank(postcode2))
	        				{
	        					postcode1= StringUtils.normalizeSpace(postcode1);
	        					document.addField("postcode1", postcode1);
	        					postcode2= StringUtils.normalizeSpace(postcode2);
	        					document.addField("postcode2", postcode2);
	        					String fullPostCode = postcode1+" "+postcode2;
	        					document.addField("full_postcode", fullPostCode);
	        				}
	        				
	        				String phone = (String) map.get("phone");
	        				if(StringUtils.isNotBlank(phone))
	        				{
	        					phone = StringUtils.normalizeSpace(phone);
	        					document.addField("phone", phone);
	        				}
	        				
	        				String website = (String) map.get("website");
	        				if(StringUtils.isNotBlank(website) || website.equalsIgnoreCase("#"))
	                		{
	                			         		
	        					website = StringUtils.normalizeSpace(website);
	                			document.addField("website", website);
	                		}
	        				
	        				String publicEmail= (String) map.get("publicEmail");
	        				if(StringUtils.isNotBlank(publicEmail))
	                		{
	                			         		
	        					publicEmail = StringUtils.normalizeSpace(publicEmail);
	                			document.addField("publicEmail", publicEmail);
	                		}
	        				
	        				//Category of home
	        				String homeType= (String) map.get("homeType");
	        				if(StringUtils.isNotEmpty(homeType))
	        				{
	        					homeType = StringUtils.normalizeSpace(homeType);
	        					if(homeType.equalsIgnoreCase("Care Home without nursing"))
	                			{
	                				homeType = "chwtn";
	                				
	                			}
	                			else if(homeType.equalsIgnoreCase("Care Home with nursing"))
	                			{
	                				homeType = "chwn";
	                				
	                			}
	                			else if(homeType.equalsIgnoreCase("Care Home offering both types of care"))
	                			{
	                				homeType = "chb";
	                			
	                			}
	                				
	                			document.addField("homeType", homeType);
	        				}
	        				
	        				List<String> admissions = new ArrayList<String>();
	        				String shortStay = (String) map.get("shortStay");
	                		String formalDayCare = (String) map.get("formalDayCare");
	                		String informalDayCare = (String) map.get("informalDayCare");
	                		String longStay = (String) map.get("longStay");
	                		if(StringUtils.isNotBlank(shortStay))
	            			{
	                			shortStay = StringUtils.normalizeSpace(shortStay);		
	    	        			if(shortStay.equalsIgnoreCase("YES"))
	    	        			{
	    	        				shortStay = "ss";
	    	        				admissions.add(shortStay);
	    	        			}
	            			}
	
	                		if(StringUtils.isNotBlank(formalDayCare))
	                		{
	                			formalDayCare = StringUtils.normalizeSpace(formalDayCare);		
	                			if(formalDayCare.equalsIgnoreCase("YES"))
	                			{
	                				formalDayCare = "fd";
	                				admissions.add(formalDayCare);
	                			}
	                		}
	                		if(StringUtils.isNotBlank(longStay))
	            			{
	                			longStay = StringUtils.normalizeSpace(longStay);		
		            			if(longStay.equalsIgnoreCase("YES"))
		            			{
		            				longStay = "ls";
		            				admissions.add(longStay);
		            			}		
	            			}
	                		
	                		if(StringUtils.isNotBlank(informalDayCare))
	            			{
	                			informalDayCare = StringUtils.normalizeSpace(informalDayCare);		
			        			if(informalDayCare.equalsIgnoreCase("YES"))
			        			{
			        				informalDayCare = "ifd";
			        				admissions.add(informalDayCare);
			        			}	
			        		}
	        				
	                		document.addField("admissions", admissions);
	                	
	                		String dementia = (String)map.get("dementia");
	                		String mentalHealth = (String)map.get("mentalHealth");
	                		String learningDisabilities = (String)map.get("learningDisabilities");
	                		String oldAge = (String)map.get("oldAge");
	                		String physicalDisabilities = (String)map.get("physicalDisabilities");
	                		String sensoryImpairment = (String)map.get("sensoryImpairment");
	                		String ppAlcoholDependence = (String)map.get("ppad");
	                		String ppDrugDependence = (String)map.get("ppdd");
	                		List<String> careProvided = new ArrayList<String>();
	                		
	                		
	            			if(StringUtils.isNotBlank(dementia ))
	            			{
	            				dementia  = StringUtils.normalizeSpace(dementia );		
		            			if(dementia.equalsIgnoreCase("YES"))
		            			{
		            				dementia = "de";
		            				careProvided.add(dementia);
		            			}
	            			}
	            			
	            			if(StringUtils.isNotBlank(mentalHealth))
	            			{
	            				mentalHealth = StringUtils.normalizeSpace(mentalHealth);		
		            			if(mentalHealth.equalsIgnoreCase("YES"))
		            			{
		            				mentalHealth = "mhc";
		            				careProvided.add(mentalHealth);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(learningDisabilities))
	            			{
	            				learningDisabilities = StringUtils.normalizeSpace(learningDisabilities);		
		            			if(learningDisabilities.equalsIgnoreCase("YES"))
		            			{
		            				learningDisabilities = "ld";
		            				careProvided.add(learningDisabilities);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(oldAge))
	            			{
	            				oldAge = StringUtils.normalizeSpace(oldAge);		
		            			if(oldAge.equalsIgnoreCase("YES"))
		            			{
		            				oldAge = "oa";
		            				careProvided.add(oldAge);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(physicalDisabilities))
	            			{
	            				physicalDisabilities = StringUtils.normalizeSpace(physicalDisabilities);		
		            			if(physicalDisabilities.equalsIgnoreCase("YES"))
		            			{
		            				physicalDisabilities = "pd";
		            				careProvided.add(physicalDisabilities);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(sensoryImpairment))
	            			{
	            				sensoryImpairment = StringUtils.normalizeSpace(sensoryImpairment);		
		            			if(sensoryImpairment.equalsIgnoreCase("YES"))
		            			{
		            				sensoryImpairment = "si";
		            				careProvided.add(sensoryImpairment);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(ppAlcoholDependence))
	            			{
	            				ppAlcoholDependence = StringUtils.normalizeSpace(ppAlcoholDependence);		
		            			if(ppAlcoholDependence.equalsIgnoreCase("YES"))
		            			{
		            				ppAlcoholDependence = "ppa";
		            				careProvided.add(ppAlcoholDependence);
		            			}	
	            			}
	            			
	            			if(StringUtils.isNotBlank(ppDrugDependence))
	            			{
	            				ppDrugDependence = StringUtils.normalizeSpace(ppDrugDependence);		
		            			if(ppDrugDependence.equalsIgnoreCase("YES"))
		            			{
		            				ppDrugDependence = "ppd";
		            				careProvided.add(ppDrugDependence);
		            			}	
	            			}
	            			
	            			document.addField("careProvided", careProvided);
	            			
	            			String longitude = (String)map.get("longitude");
	            			String latitude = (String)map.get("latitude");
	                		if(StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude))
	                		{
	                			String location = latitude+","+longitude;
	                			document.addField("location", location);
	                		}
	                		
	                		String town = (String)map.get("town");
	                		if(StringUtils.isNotBlank(town))
	            			{
		            			town = StringUtils.normalizeSpace(town);		
		            					            			
		            			document.addField("town", town);
	            			}
	
	                		document.addField("dateOfIndex",new Date());
	                		solr.add(document);
	                		solr.commit();
	                		//solr.close();
	                		logger.info("Data with "+id+" indexed");
	                		strBuffer.append("["+new Date()+"] Indexed document with id - "+id);
	                		strBuffer.append("\n");
	        			}
	        		}
	        		strBuffer.append("["+new Date()+"] Indexing completed");
	        		applicationMailer.sendPreConfiguredMail(strBuffer.toString(), "M");
	        		solr.close();
	        	}
	        	else
	        	{
	        		//send email saying application failed
	            	strBuffer.append("["+new Date()+"] DB Data retrieval failed. See logs for more details");
	            	applicationMailer.sendPreConfiguredMail(strBuffer.toString(), null);
	        	}
							        	
	        }
	        else
	        {
	        	//send email saying application failed
	        	strBuffer.append("["+new Date()+"] DB Data retrieval failed. See logs for more details");
	        	applicationMailer.sendPreConfiguredMail(strBuffer.toString(), null);
	        }
        		
		}
		else
		{
			strBuffer.append("["+new Date()+"] DB Data retrieval failed. Failed to retrieve solr url address from db.properties");
        	applicationMailer.sendPreConfiguredMail(strBuffer.toString(), null);
		}
		input.close();
 
}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SolrServerException
	 *
	public static void main(String[] args) throws IOException, SolrServerException 
	{//(String host, String port, String solrCore, String fileName, int start, int end)
		System.out.println("Arguments -- Host e.g localhost, port e.g 8984, solrCore e.g live, fileName e.g excel.xls, start 2, end 609");
		
		if(args.length == 6)
		{
			ManualIndexer manual = new ManualIndexer();
			manual.doIndex(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]));
		}
		else
		{
			System.out.println("Invalid number of parameters");
			
			
		}
		
	}*/
	
	public static void main(String[] args) throws IOException, SolrServerException
	{
		if(args.length==0)
		{
			ManualIndexer  m = new ManualIndexer();
		
			m.doIndex();
		}
		else if(args.length == 6)
		{
			ManualIndexer manual = new ManualIndexer();
			manual.doIndex(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]));
		}
			
	}



	
}