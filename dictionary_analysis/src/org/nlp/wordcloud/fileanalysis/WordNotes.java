package org.nlp.wordcloud.fileanalysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.nlp.wordcloud.util.MyStaticValue;

public class WordNotes implements TableObject {

	//WORD_ID
	public String strWordID ;
	
	//INDUSTRY_CODE
	public String strIndustryCode ;
	
	//WORD_NOTES
	public String  strWordNotes;
	
	//WORD_POS
	public String  strWordPos;
	
	//WORD_FREQUENCY
	public int  iWordFre;
	
	//WORD_EMOTIONS
	public String strWordEmotion;
	
	//STATUS
	public String  strStatus;
	
	//SORT_NUMBER
	public int  iSortNumber;
	
	public WordNotes() { 
		// TODO Auto-generated constructor stub
		strWordID = "";
		strIndustryCode = "";
		strWordNotes = "";
		strWordPos = "";
		iWordFre = 0;
		strWordEmotion = "";
		strStatus = "1";
		iSortNumber = 0; 	
	}
	
	private static String m_SqlString ="INSERT INTO WORD_NOTES (WORD_ID, INDUSTRY_CODE,WORD_NOTES,WORD_POS,WORD_FREQUENCY,WORD_EMOTIONS,STATUS,SORT_NUMBER,CREATE_TIME,UPDATE_TIME,VERSION_NUMBER) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	
	@Override
	public int BindInfo(PreparedStatement pStat)throws SQLException {
		
		//WORD_ID
		pStat.setString(1,strWordID);
		
		//INDUSTRY_CODE
		pStat.setString(2, strIndustryCode); 
		
		//WORD_NOTES
		pStat.setString(3, strWordNotes); 
		
		//WORD_POS
		pStat.setString(4, strWordPos);  
		
		//WORD_FREQUENCY
		pStat.setInt(5, iWordFre);
		
		//WORD_EMOTIONS
		pStat.setString(6, strWordEmotion);
		
		//STATUS
		pStat.setString(7, strStatus);
		
		//SORT_NUMBER 
		pStat.setInt(8, iSortNumber); 
		
		//CREATE_TIME
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式 
		java.util.Date utilDate=new java.util.Date(); 
		df.format(utilDate);// new Date()为获取当前系统时间
		java.sql.Timestamp sqlTimeDate = new java.sql.Timestamp(utilDate.getTime());
				 
		pStat.setTimestamp(9, sqlTimeDate);			
		//UPDATE_TIME
		pStat.setTimestamp(10, sqlTimeDate);
		
		//VERSION_NUMBER
		pStat.setString(11, MyStaticValue.versionNumber);
		return 0;
	}
 
	public void SetSql(String Sql) { 
		m_SqlString = Sql;
	}

	@Override
	public String GetSql() { 
		return m_SqlString;
	}
	
	//WORD_EMOTIONS
}
