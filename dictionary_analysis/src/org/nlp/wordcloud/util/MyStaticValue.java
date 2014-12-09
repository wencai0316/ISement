package org.nlp.wordcloud.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyStaticValue {

	public static String versionNumber = "1.0.0" ;
	public static String mathNumber = "1234567890" ;
	public static final String posString ="名动副连助代介数量叹" ;
	public static final String SpecialNumber ="①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯" ;
	public static final String MathNumber ="1234567890" ;
	public Map<String, String> mapPos;   
	{
		// TODO Auto-generated constructor stub	    
		mapPos=new HashMap<String, String>();
		mapPos.put("名", "n");
		mapPos.put("动", "v");
		mapPos.put("形", "ag");
		mapPos.put("副", "ad");
		mapPos.put("连", "c");
		mapPos.put("助", "u");
		mapPos.put("代", "r");
		mapPos.put("介", "p"); 
		mapPos.put("数", "m");	    
		mapPos.put("量", "q");	
		mapPos.put("叹", "e");
		mapPos.put("拟声", "o");		
	}
	
	/*
	 * 是否是汉字
	 */
	public static boolean isChineseChar(String str){
	       boolean temp = false;
	       Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
	       Matcher m=p.matcher(str); 
	       if(m.find()){ 
	           temp =  true;
	       }
	       return temp;
	   }
	
	//是否是汉字
	public static boolean isChinese(char a) {  
	    int v = (int)a;  
	    return (v >= 19968 && v <= 171941);  
	} 
	//是否包含汉字
	public static boolean chontainsChinese(String s) {  
	    if (null == s || "".equals(s.trim())) return false;  
	    for (int i = 0; i < s.length(); i++) {  
	        if (isChinese(s.charAt(i))) return true;  
	    }  
	    return false;  
	} 
	
}
