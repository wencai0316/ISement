package org.nlp.wordcloud.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;  
import java.util.List; 

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis; 

public class SpiltWordDemo {
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		// TODO Auto-generated method stub
		String inPathString  = "E:\\pku_test.utf8";
		String outPathString = "E:\\Out.txt";
		NlpAnalysis.parse("你好"); 
		long startTime=System.currentTimeMillis();   //获取开始时间
		
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inPathString),"utf-8"));
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPathString),"utf-8"));
        String temp=new String();  
        try {
			while((temp = br.readLine()) != null){         	
				List<Term> parse = NlpAnalysis.parse(temp); 
				//parse = ToAnalysis.parse(temp);
				System.out.println(parse);
			    wr.write(parse.toString()); 
			    wr.newLine();
			}
			br.close();
			wr.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        long endTime=System.currentTimeMillis();   //获取开始时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        
       
	}

}
