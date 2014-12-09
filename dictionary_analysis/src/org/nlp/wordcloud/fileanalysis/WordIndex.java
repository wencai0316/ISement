package org.nlp.wordcloud.fileanalysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.nlp.wordcloud.util.MyStaticValue;



public class WordIndex implements TableObject{

	//拼音是否已经处理
	public int iDeal;
	//INDEX_ID
	public String strIndexId;
	//首字
	public String firstChar;
	
	//拼音
	public String strAlphabet;
	
	//拼音缩写
	public String strShortAlphabet;
	
	//词组数目
	public int iCount;
	
	//NOTES
	public String strNotes;
	//Status
	public String strStatus;
	//SortNumber
	public int iSortNumber;
	
	public WordIndex() {
		// TODO Auto-generated constructor stub
		iDeal = 0;
		strIndexId = "";
		firstChar = "";
		strAlphabet = "";
		strShortAlphabet = "";
		strNotes = "";
		strStatus = "1";
		iSortNumber = 0;
		iCount = 0;		
	}
	
	//Insert 语句
	private static String m_SqlString = "INSERT INTO WORD_INDEXS (INDEX_ID, INDEX_CHAR,NOTES,ALPHABET,SHORT_ALPHABET,WORD_COUNTS,STATUS,SORT_NUMBER,CREATE_TIME,UPDATE_TIME,VERSION_NUMBER) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	
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
	public int BindInfo(PreparedStatement pStat)throws SQLException {

		//INDEX_ID 
		pStat.setString(1, strIndexId);
		//INDEX_CHAR
		pStat.setString(2, firstChar);
		//notes
		pStat.setString(3, strNotes);
		//ALPHABET
		pStat.setString(4, strAlphabet);
		//SHORT_ALPHABET
		pStat.setString(5, strShortAlphabet);		
		//WORD_COUNTS 
		pStat.setInt(6, iCount); 
		//STATUS
		pStat.setString(7,strStatus);
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
 
		return 1;		
	}
	
	public boolean equals(Object obj) {   
        if (obj instanceof WordIndex) {  
        	WordIndex u = (WordIndex) obj;
            return this.firstChar.equals(u.firstChar);  
        }   
        return super.equals(obj);  
	}

}
