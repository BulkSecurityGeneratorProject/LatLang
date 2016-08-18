package com.tomar.app.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.tomar.app.domain.Entry;

public class FileReadService {
	
	public static List<Entry> getUpdatedData(FileInputStream fsa){
		List<Entry> entryList = new ArrayList<Entry>();
		try {
		    POIFSFileSystem fs = new POIFSFileSystem(fsa);
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(0);
		    HSSFRow row;
		    HSSFCell cell;

		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 0; // No of columns
		    int tmp = 0;

		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }

		    for(int r = 0; r < rows; r++) {
		        row = sheet.getRow(r);
		        Entry e = new Entry();
		        if(row != null) {
        	
		        	StringBuilder address = new StringBuilder(); 
		        	  e.setAddress((row.getCell(0)).getStringCellValue());
		        	  e.setCity((row.getCell(1)).getStringCellValue());
		        	  e.setCountry((row.getCell(2)).getStringCellValue());
		        	  e.setPostal_code((row.getCell(3)).getStringCellValue());
		        	  e.setState_province((row.getCell(4)).getStringCellValue());
		        	  e.setCountry1((row.getCell(5)).getStringCellValue());
		        	  
		        	  //form address string 
		        	  address.append((row.getCell(0)).getStringCellValue());
		        	  address.append(",");
		        	  address.append((row.getCell(1)).getStringCellValue());
		        	  address.append(",");
		        	  address.append((row.getCell(4)).getStringCellValue());
		        	  address.append(",");
		        	  address.append((row.getCell(5)).getStringCellValue());
		        	  address.append(",");
		        	  address.append((row.getCell(3)).getStringCellValue());

		        	  String[] res = GeoEncodingService.getValue(address);
		        	  e.setGoogle_verified_address(res[0]);
		        	  e.setLatitude(res[1]);
		        	  e.setLongitude(res[2]);
		        	  entryList.add(e);

		        	
		        }
		    }
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
		return entryList;
	}

}
