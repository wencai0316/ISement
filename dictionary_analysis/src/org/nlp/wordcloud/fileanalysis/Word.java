package org.nlp.wordcloud.fileanalysis;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.sql.SQLException; 
import java.text.SimpleDateFormat;

import org.nlp.wordcloud.util.MyStaticValue;

/*
 * 每个词的信息
 */
public class Word implements TableObject {
	//
	public int iDeal;
	//词ID
	public String strWordID;
	
	//词内容
	public String strContent ; 
	
	//首字
	public String strFirstWord;
	
	//首字编码
	public String strFirstWordId ;
	
	//拼音
	public String strAlphabet;
	
	//拼音缩写
	public String strShortAlphabet;
	
	//领域编号
	public String strIndustryCode ;
	
	public String strSimpleMean;
	public String strSource;
	
	//词性：
	public String strPos;
	
	//词频
	public int  ifre ;	
	
	//词感
	public String strEmotion;
	
	//SORT_NUMBER
	public int iSortNumber;
	
	//Status
	public String strStatus;
	
	//词性及词释义
	public  Map<String,Set<String>> mapPosAndMean;
	
	
	public Word(){
		strWordID 		= "";
		strContent 	 	= "";
		strFirstWord 	= "";
		strFirstWordId 	= "";
		strAlphabet     = "";
		strShortAlphabet= "";
		strIndustryCode = "";
		strSimpleMean   = "";
		strSource       = "";
		strPos 			= "";
		ifre   			= 0;
		strEmotion		= "";
		iSortNumber 	= 0;
		strStatus       = "1";
		mapPosAndMean = new HashMap<String, Set<String>>();
	}

	
	//Insert 
	private static String m_SqlString = "INSERT INTO WORDS (WORD_ID, WORD,NOTES,ALPHABET,SHORT_ALPHABET,WORD_POS,WORD_FREQUENCY,WORD_EMOTIONS,INDEX_ID,INDUSTRY_CODE,STATUS,SORT_NUMBER,CREATE_TIME,UPDATE_TIME,VERSION_NUMBER) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	//设置sql语句 
	public void SetSql(String sql){
		m_SqlString = sql;
	}
	
	//获取sql
	@Override
	public String GetSql() {
		// TODO Auto-generated method stub
		return m_SqlString;
	}
	
	//绑定变量
	@Override
	public int BindInfo(PreparedStatement pStat)throws SQLException{
 
		//wordid
		pStat.setString(1,strWordID);
		//words
		pStat.setString(2, strContent);
		//notes
		pStat.setString(3, "");
		//ALPHABET
		pStat.setString(4, strAlphabet);
		//SHORT_ALPHABET
		pStat.setString(5, strShortAlphabet);		
		//WORD_POS 
		pStat.setString(6, strPos);
		//WORD_FREQUENCY
		pStat.setInt(7, ifre);
		//WORD_EMOTIONS
		pStat.setString(8, strEmotion);
		//INDEX_ID
		pStat.setString(9, strFirstWordId);
		//INDUSTRY_CODE
		pStat.setString(10, strIndustryCode);
		//STATUS
		pStat.setString(11, strStatus);
		//SORT_NUMBER
		pStat.setInt(12, iSortNumber);
			
		//CREATE_TIME
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式 
		java.util.Date utilDate=new java.util.Date(); 
		df.format(utilDate);// new Date()为获取当前系统时间
		java.sql.Timestamp sqlTimeDate = new java.sql.Timestamp(utilDate.getTime());
					 
		pStat.setTimestamp(13, sqlTimeDate);			
		//UPDATE_TIME
		pStat.setTimestamp(14, sqlTimeDate);
			
		//VERSION_NUMBER
		pStat.setString(15, MyStaticValue.versionNumber);
	  
		return 1;		
	}


}
