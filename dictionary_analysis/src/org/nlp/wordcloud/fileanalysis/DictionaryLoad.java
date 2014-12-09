package org.nlp.wordcloud.fileanalysis; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException; 
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap; 
import java.util.HashSet;
import java.util.Map;
import java.util.Set; 
import java.util.List;


public class DictionaryLoad {
	public static final String posString ="名动副连助代介数量叹" ;
	public static final String SpecialNumber ="①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯" ;
	public static final String MathNumber ="1234567890" ;
	public Map<String, String> mapPos;  

	/**
	 * 	读取文件
	 */ 
	public void FileAnalysis(String path, List<Word> listWord,List<Word> listWordIdiom,List<WordIndex> listWordIndex,boolean bIdiom) throws IOException{
	    File file=new File(path);
	    if(!file.exists()||file.isDirectory())
	        throw new FileNotFoundException();
	      
       // BufferedReader br=new BufferedReader(new FileReader(file));
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
        String temp=new String();
        temp = br.readLine();
        
		//String strPath = "E:\\dictionary\\shouzi.txt";
		//File file1 = new File(strPath);
		//BufferedWriter bw= new BufferedWriter(new FileWriter(file1));
        
    	String strAlphabet = null;
    	String strContent  = null;
    	String strSimpleMean = null;
    	String strSource 	 = null; 
    	int iLineNumber = 1;
	    int iCount = 0;  
	    while(null != temp){ 
        	if(!bIdiom)
        	{
            	char[] chars = temp.toCharArray();
            	if (chars.length>1 && '[' != chars[0] && ( '另' != chars[0] && '见' != chars[1] )
            			&& isChineseChar(temp)&& (isChineseChar(String.valueOf(chars[0])))) {
            		//首字识别
    	        	WordIndex wordIndex = new WordIndex();
    	        	boolean bIsExist = false;
    	        	this.FileWordIndexAnalysis(temp,wordIndex); 
                	for (WordIndex wordTemp: listWordIndex) {            		
                		if (wordTemp.firstChar.equals(wordIndex.firstChar) && wordTemp.strAlphabet.equals(wordIndex.strAlphabet)) {
                			bIsExist = true; 
                		 	this.FileWordIndexAnalysis(temp,wordTemp);
                		 	break;
    					} 
    				}
                	if (!bIsExist) {
                		listWordIndex.add(wordIndex);
					}         	
            	}
        		//假如此行不包含词 继续下一行
        		if (!temp.contains("[") && !temp.contains("]")) { 
        	        temp = br.readLine();
        	        continue ;
        		}
        		
        		
        		//词识别
            	Word word = new Word();
            	this.FileWordAnalysis(temp,word);
              	boolean bIsExist = false;
            	for (Word wordTemp: listWord) {            		
            		if (wordTemp.strContent.equals(word.strContent)  && wordTemp.strAlphabet.equals(word.strAlphabet)) {
            			bIsExist = true; 
            		 	this.FileWordAnalysis(temp,wordTemp);
            		 	break;
					} 
				}
            	if (!bIsExist) {
                 	listWord.add(word);  
				}
               		
        	}
        	else {
        		/*if (null == temp || temp.length() == 0 ) {
                    temp = br.readLine();
                    iLineNumber++;
					continue;					
				} */
        		int n = iLineNumber%3;
        		
        		//第一行是拼音
        		if (1 == n) {
        			strAlphabet = temp;
				}else if(2 == n) {
					strContent = temp;
				}else if (0 == n) {					
        			String[] strArry = temp.split("∥");
        			strSimpleMean  = strArry[0];
        			if(strArry.length>1){
        				strSource = strArry[1];
        			}               
				}
        		iCount++;
        		if (3 == iCount) { 
                	Word word = new Word();    
                	word.strAlphabet = strAlphabet;
                	word.strContent = strContent;
                	word.strSource = strSource;
                	word.strSimpleMean = strSimpleMean;   
                	listWordIdiom.add(word);    
                	iCount = 0;
                	strAlphabet = "";
                	strContent = "";
                	strSource = "" ;
                	strSimpleMean = "";
				}
        		
			}        	
            temp = br.readLine();
            iLineNumber++;
        }
        br.close(); 
	}
	
	/**
	 * 	读取成语文件
	 */ 
	public void idiomFileAnalysis(String path, List<Word> listIdiomWord) throws IOException{
	    File file=new File(path);
	    if(!file.exists()||file.isDirectory())
	        throw new FileNotFoundException();
	      
       // BufferedReader br=new BufferedReader(new FileReader(file));
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
        String temp=new String();
        temp = br.readLine();   
 
        while(null != temp){ 
            char[] chars = temp.toCharArray();
        	if (chars.length<=0 || (chars.length>=1 && '[' != chars[0])) {
        		temp = br.readLine();  
				continue;
			}
        	
    		//词识别
        	Word word = new Word();
    		//]后就是拼音，取拼音
    		//取词		
    		String strRegex1 = new String();
    		//strRegex1 = "\\[([\\u4e00-\\u9fa5]+)\\]";
    		strRegex1 = "\\[(.*?)\\]";
    		word.strContent = this.MatchDealFunc(strRegex1,temp); 
    	  
			//去拼音后面的第一个字位置 
			int iPos=0; 
			iPos = word.strContent.length()+2;	

			String strMean = new String();
			String strPos  = new String();
			//词性 
			if(iPos < temp.length() ){
				strPos = ChinesePosToEnglishPos(temp.substring(iPos, iPos+1));	
				strMean = temp.substring(iPos+1) ;
			}else {
				strPos = "CY";
			} 

			if (null == strPos || "DEF" == strPos) {
				//成语
				strPos = "CY";
				//词释义
				strMean = temp.substring(iPos);
			}  
			word.strPos = strPos;
			word.strSimpleMean = strMean.trim(); 
    		  		
        	listIdiomWord.add(word);  
        	temp = br.readLine();
        } 
        br.close();        	
	}
		
	//解析词内容 (一行一个词)
	public void FileWordAnalysis(String LineData,Word word){
		
		String strTemp = LineData;
		strTemp.trim();
		
		char[] chars = strTemp.toCharArray();
		
		//]后就是拼音，取拼音
		String strRegex = new String();
		strRegex="\\]([^\\u4e00-\\u9fa5]+)[\\u4e00-\\u9fa5]";
		String py = new String();
		py = this.MatchDealFunc(strRegex,LineData); 
		word.strAlphabet = deleteSpecialChar(py); 
		
		
		//取词		
		String strRegex1 = new String();
		//strRegex1 = "\\[([\\u4e00-\\u9fa5]+)\\]";
		strRegex1 = "\\[(.*?)\\]";
		word.strContent = this.MatchDealFunc(strRegex1,LineData); 
	
		
		boolean bIsMutilPos = false; 
		//多词性集合 
		int[] iLocal = new int[16]; 		
		int a=0; 
		int iPos=0;
		boolean bfirst = false;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];			
			if (SpecialNumber.contains(String.valueOf(c))) {
				//记录词性的位置
				bIsMutilPos = true;
				iLocal[a] = i+1; 
				a++;				
			}
			if (c=='[' && !bfirst) {
				iPos = i;
				bfirst = true;
			}
		}
		
		//取词性及释义
		if (bIsMutilPos) {
			splitMutilPosAndMean(word.mapPosAndMean,strTemp,iLocal);
		}else {//单个词性
			//去拼音后面的第一个字位置 
			iPos = iPos+word.strAlphabet.length()+word.strContent.length()+2;	

			String strMean = new String();
			
			//词性 
			String strPos = ChinesePosToEnglishPos( strTemp.substring(iPos, iPos+1));	
			if (null == strPos || "DEF" == strPos) {
				//成语
				if (4 == word.strContent.length()) {
					strPos = "CY";
				}else if(3 == word.strContent.length()){//三个字的词
					strPos = "SA";
				}
				//词释义
				strMean = strTemp.substring(iPos); 
			}else {
				strMean = strTemp.substring(iPos+1);
			}	
			Set<String> setMean = new HashSet<String>(); 
			setMean.add(strMean.trim());
			word.strPos = strPos;
			word.strSimpleMean = strMean.trim();
			word.mapPosAndMean.put(strPos, setMean);
		}
	}
	
	//词首字识别
	public void FileWordIndexAnalysis(String LineData, WordIndex wordIndex ){		
		String temp = LineData;
		if (0 == LineData.length() || null == wordIndex) {
			return ;
		}
		
    	char[] chars = temp.toCharArray();    		
    		
		//首字    		
		wordIndex.firstChar = String.valueOf(chars[0]);		
		wordIndex.firstChar.trim();
		
		String strRegex = new String();
		//包含繁体字处理
		if ('（' == chars[1]) {
			//首字或者）后就是拼音，取拼音
			strRegex="\\）([^\\u4e00-\\u9fa5]+)[\\u4e00-\\u9fa5①]?";    			
		}else if (MathNumber.contains(String.valueOf(chars[1]))) {
			if ('（' == chars[2]) {
				strRegex="\\）([^\\u4e00-\\u9fa5]+)[\\u4e00-\\u9fa5①]?";
				temp= temp.substring(2);
			}else {
				strRegex="[0-9]([^\\u4e00-\\u9fa5]+)[\\u4e00-\\u9fa5]"; 
			}
		}else{  
			strRegex="[\\u4e00-\\u9fa5]([^\\u4e00-\\u9fa5]+)[\\u4e00-\\u9fa5]?";		
		}
		String py = new String();
		py = this.MatchDealFunc(strRegex,temp); 
		wordIndex.strAlphabet = deleteSpecialChar(py);  
		wordIndex.strAlphabet.trim();
		int iNotesPos = 0;
		iNotesPos = temp.indexOf(py); 
		wordIndex.strNotes = temp.substring(iNotesPos+py.length());
	}
	
	//正则表达式处理函数
	public String MatchDealFunc(String strRegex , String strSrc){
		String temp = "";
		Pattern p = Pattern.compile(strRegex);
		Matcher m = p.matcher(strSrc);		
		if(m.find()){			
			temp = m.group(1);
		}		
		return temp;
	}
		
	//词性映射函数 汉字转字符
	public String ChinesePosToEnglishPos(String strPos) {
		String temp = mapPos.get(strPos);
		if (null == temp) {
			temp="DEF";
		}
		return temp;
	}
	
	//数字 转 字符
	public String  StringPosToIntPos(String strPos) {
		String temp = mapPos.get(strPos);
		return temp;
	}
	
	//去出拼音中的特殊字符
	public String deleteSpecialChar(String str){
		String temp = new String();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length;i++) {
			char c = chars[i];
			if('[' == c ||']' == c ||'（' == c||'）' == c ||'<'==c || '>' ==c || '“'== c || '”'==c  || '〈'==c || '《' ==c 
					|| SpecialNumber.contains(String.valueOf(c))||MathNumber.contains(String.valueOf(c))){
				continue;
			} 
			temp += String.valueOf(c);			
		}
		return temp;
	}
	
	//分条截取 多个词性及解释
	public void splitMutilPosAndMean(Map<String,Set<String>> mapStr, String lineData, int[] iLocal){
		String temp = lineData;
		for (int i = 0; i < iLocal.length; i++) {
			int j = iLocal[i];
			//等于0 数组结束
			if (0 == j) {
				break;
			}
			String tempPos = new String();
			String tempMean = new String();
			
			int iMean = j+1;
			tempPos = temp.substring(j, iMean);
			char[] chars = tempPos.toCharArray();
			if ('拟' == chars[0] ) {
				iMean += 1;
				tempPos = temp.substring(j, iMean);
			} 
			tempPos = ChinesePosToEnglishPos(tempPos);
			
			//如果没有词性
			if ("DEF" == tempPos) {
				iMean -= 1;
			}
			
			//最后一个词性
			if ( iLocal[i+1] == 0 ) {
				tempMean = temp.substring(iMean);
			}else {
				tempMean = temp.substring(iMean,iLocal[i+1]-1);
			}
			
			//放入容器
			if (mapStr.keySet().contains(tempPos)) {
				mapStr.get(tempPos).add(tempMean.trim());				
			}else{
				Set<String> setStr = new HashSet<String>();
				setStr.add(tempMean.trim());
				mapStr.put(tempPos, setStr);
			}			
		}		 
	} 
	
	/*
	 * 是否是汉字
	 */
	public boolean isChineseChar(String str){
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
	
	/*
	 * constructor
	 */
	public DictionaryLoad()throws IOException{
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
    
}
