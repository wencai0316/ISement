package org.nlp.wordcloud.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class FileUtils {

	private static File file; 
	private static BufferedReader bfReader; 
	private static BufferedWriter bfWriter;
	
	public FileUtils() {
		// TODO Auto-generated constructor stub
		file = null;
		bfReader = null;
		bfWriter = null;
	}
	
	public FileUtils(String path) throws IOException{
		// TODO Auto-generated constructor stub
		file = new File(path); 
	    if(!file.exists()||file.isDirectory())
	        throw new FileNotFoundException();
		bfReader = new BufferedReader(new FileReader(file)); 
		bfWriter = new BufferedWriter(new FileWriter(file));  
	}
	
	
	/*
	 * 一行一行读取文文件
	 */
	public static String ReadFileLine() throws IOException{
		String temp=new String();  
		temp = bfReader.readLine();  
	    return temp;
	}
	
	/*
	 * 一行一行写文件
	 */
	public static void WriteFileLine(String lineData) throws IOException{ 
		bfWriter.write(lineData); 
		bfWriter.newLine();
	}
	
	protected void finalize()throws IOException{
		if (null != bfReader) {
			bfReader.close();
		}
		if (null != bfWriter) {
			bfWriter.close();
		}
	}
}
