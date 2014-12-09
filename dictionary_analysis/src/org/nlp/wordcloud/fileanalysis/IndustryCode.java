package org.nlp.wordcloud.fileanalysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.nlp.wordcloud.util.MyStaticValue;

public class IndustryCode implements TableObject{
	//
	public String strIndustryCode;
	public String strParentCode;
	public String strName;
	public String strNotes;
	public int    iDepth;
	public int 	  iSortNumber;
	private static String strInsertSQL = "INSERT INTO INDUSTRY_CODE (INDUSTRY_CODE, PARENT_CODE,INDUSTRY_NAME,NOTES,INDUSTRY_DEPTH,STATUS,SORT_NUMBER,CREATE_TIME,UPDATE_TIME,VERSION_NUMBER) VALUES(?,?,?,?,?,?,?,?,?,?)";
	
	public IndustryCode(){
		strIndustryCode = "";
		strParentCode   = "";
		strName         = "";
		iDepth 			= 0;
		iSortNumber     = 0;
	}
	@Override
	public int BindInfo(PreparedStatement pStat) throws SQLException {
		
		//INDUSTRY_CODE
		pStat.setString(1,strIndustryCode);
		
		//PARENT_CODE
		pStat.setString(2, strParentCode); 
		
		//INDUSTRY_NAME
		pStat.setString(3, strName); 
		
		//NOTES
		pStat.setString(4, strNotes);  
		
		//INDUSTRY_DEPTH
		pStat.setInt(5, iDepth); 
		
		//STATUS
		pStat.setString(6, "1");
		
		//SORT_NUMBER 
		pStat.setInt(7, iSortNumber); 
		
		//CREATE_TIME
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式 
		java.util.Date utilDate=new java.util.Date(); 
		df.format(utilDate);// new Date()为获取当前系统时间
		java.sql.Timestamp sqlTimeDate = new java.sql.Timestamp(utilDate.getTime());
				 
		pStat.setTimestamp(8, sqlTimeDate);			
		//UPDATE_TIME
		pStat.setTimestamp(9, sqlTimeDate);
		
		//VERSION_NUMBER
		pStat.setString(10, MyStaticValue.versionNumber);
		return 0;
	}

	@Override
	public String GetSql() {
		return strInsertSQL;
	} 
}
