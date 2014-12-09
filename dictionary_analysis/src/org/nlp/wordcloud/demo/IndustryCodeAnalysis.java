package org.nlp.wordcloud.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.nlp.wordcloud.fileanalysis.IndustryCode;
import org.nlp.wordcloud.util.JdbcUtils;
import org.nlp.wordcloud.util.MyStaticValue;

public class IndustryCodeAnalysis {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException { 
		
		String inPathString  = "E:\\dictionary\\industryCode.txt";

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inPathString),"utf-8"));
        
        List<IndustryCode> listIndustry = new ArrayList<IndustryCode>();
        String strParent = new String();
	    String temp=new String();  
	    int iTopSort = 1;
	    try {
			while((temp = br.readLine()) != null){
				
				IndustryCode industryCode = new IndustryCode();
				String[] strings = temp.split(" ");		
				if (strings.length == 0) {
					continue;
				}
				
				
				//第一层
				char[] chars = strings[0].toCharArray();
				if (!strings[0].isEmpty() && (chars[0] >= 'A' && chars[0] <= 'Z' )) {
					industryCode.strIndustryCode = strings[0];
					industryCode.strParentCode = "0";
					industryCode.strName  = strings[1];
					if(strings.length>=3){
						industryCode.strNotes = strings[2]; 
					}
					industryCode.iDepth   = 0;
					industryCode.iSortNumber = iTopSort;
					iTopSort++;
					listIndustry.add(industryCode);
					strParent = strings[0];
				}else if(strings[2].length() == 2){//第二层
					industryCode.iDepth   = 1;
					industryCode.iSortNumber = Integer.parseInt(strings[2].substring(1,2));
					industryCode.strIndustryCode = strings[2];
					industryCode.strParentCode = strParent;
					industryCode.strName  = strings[3];
					if (strings.length>=5) {
						industryCode.strNotes = strings[4]; 
					} 
					listIndustry.add(industryCode);
				}else if(strings[2].length() == 3){//第三层
					//没有第四层
					if (!MyStaticValue.isChineseChar(strings[3])) {
						industryCode.iDepth   = 2;
						industryCode.iSortNumber = Integer.parseInt(strings[2].substring(2,3));
						industryCode.strIndustryCode = strings[2];
						industryCode.strParentCode = strings[2].substring(0,2);
						industryCode.strName  = strings[4];
						if (strings.length>=6) { 
							industryCode.strNotes = strings[5];  
						}
						listIndustry.add(industryCode);
					}else {
						industryCode.iDepth   = 2;
						industryCode.iSortNumber = Integer.parseInt(strings[2].substring(2,3));
						industryCode.strIndustryCode = strings[2];
						industryCode.strParentCode = strings[2].substring(0,2);
						industryCode.strName  = strings[3];
						if (strings.length>=5) {
							industryCode.strNotes = strings[4];  
						}
						listIndustry.add(industryCode);
					}					
				}else if (strings[2].length() == 4) {//第四层
					industryCode.iDepth   = 3;
					industryCode.iSortNumber = Integer.parseInt(strings[2].substring(3,4));
					industryCode.strIndustryCode = strings[2];
					industryCode.strParentCode = strings[2].substring(0,3);
					industryCode.strName  = strings[3]; 
					if (strings.length>=5) {
						industryCode.strNotes = strings[4]; 
					} 	 
					listIndustry.add(industryCode);
				}
			}
			br.close(); 
			IndustryCode tempCode = new IndustryCode();
			JdbcUtils.getInstance().executeBatch(tempCode, listIndustry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
