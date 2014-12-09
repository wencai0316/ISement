package org.nlp.wordcloud.demo; 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;  
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.nlp.wordcloud.fileanalysis.DictionaryLoad;
import org.nlp.wordcloud.fileanalysis.Word;
import org.nlp.wordcloud.fileanalysis.WordIndex;
import org.nlp.wordcloud.fileanalysis.WordNotes;
import org.nlp.wordcloud.util.MyStaticValue;
import org.nlp.wordcloud.util.PinyinFormatter;
import org.nlp.wordcloud.util.JdbcUtils;

import java.util.Set;
import java.util.Collections;  
import java.util.Comparator;

public class DictionaryAnalysisDemo {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
		// TODO Auto-generated method stub		
		List<Word> listWord = new ArrayList<Word>();
		List<Word> listWordIdiom = new ArrayList<Word>();
		List<WordIndex> listWordIndex = new ArrayList<WordIndex>();
		List<Word> listWordIdiom2 = new ArrayList<Word>();
		List<Word> listWordErr= new ArrayList<Word>();
		
		//词释义
		/*List<WordNotes> listWordNotes1 =  new ArrayList<WordNotes>();
		WordNotes wordNotes1 = new WordNotes();
		wordNotes1.strWordID="879";
		wordNotes1.strWordNotes="百般思索也无法理解。";
		wordNotes1.strWordPos="CY";
		wordNotes1.iSortNumber=49;
		WordNotes wordNotesSql1 = new WordNotes();
		listWordNotes1.add(wordNotes1);
		JdbcUtils.getInstance().executeBatch(wordNotesSql1, listWordNotes1);*/
		
		try {
			DictionaryLoad termFileLoad = new DictionaryLoad();
			termFileLoad.FileAnalysis("E:\\dictionary\\word.txt",listWord,listWordIdiom,listWordIndex,false);
			termFileLoad.FileAnalysis("E:\\dictionary\\chengyu.txt",listWord,listWordIdiom,listWordIndex,true);
			termFileLoad.idiomFileAnalysis("E:\\dictionary\\chengyu2.txt",listWordIdiom2);
			
			String strPath = "E:\\dictionary\\wordResult.txt";
			File file = new File(strPath);
		    //没有 则创建
		    if(!file.exists()||file.isDirectory()){
		    	file.createNewFile();
		    }	    
			BufferedWriter bw= new BufferedWriter(new FileWriter(file));
			//jia3cheng2zhen1
			PinyinFormatter pinyinFormatter = new PinyinFormatter();
  
			
			//String str11 =  pinyinFormatter.convertToneMark2ToneNumber("mànɡuàn");	
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		    format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
		    format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		    
			boolean bIsHere = false;
			String temp = new String();
			for (Word word : listWordIdiom2) {
				 bIsHere = false;
				for (Word word2 : listWordIdiom) {
					if (word.strContent.equals(word2.strContent)) {
						word = word2;
						bIsHere = true;
						break;
					}
				} 
				if (!bIsHere) {
					word.strAlphabet = PinyinHelper.toHanyuPinyinString(word.strContent, format, temp);
					word.strShortAlphabet = GetShortAlphabet(word.strAlphabet).toLowerCase();
					word.iDeal = 1;
					if (word.strContent.isEmpty()) {
						listWordErr.add(word);
						continue;
					}
					listWordIdiom.add(word); 				
				} 
			}
			
			//成语和词语合并，成语与词语重复 取成语解释和出处
			for (int i = 0; i < listWordIdiom.size(); i++) {
				Word wordIdiom = listWordIdiom.get(i);
				boolean bNeedAdd = false;
				int j = 0; 
				for (; j < listWord.size(); j++) {
					Word word = listWord.get(j);
					if (word.strContent.equals(wordIdiom.strContent)) {
						word.strSimpleMean = wordIdiom.strSimpleMean;
						word.strAlphabet   = wordIdiom.strAlphabet;		
						word.strSource     = wordIdiom.strSource;
						word.iDeal         = wordIdiom.iDeal;
						word.mapPosAndMean.clear();
						bNeedAdd = true; 
						break;
					} 
				}
				if (!bNeedAdd) {
					wordIdiom.strPos = "CY";
					listWord.add(wordIdiom); 
				}
			}

			//合并后List排序
			//Collections.sort(listWord);
		    //根据Collections.sort重载方法来实现  
	        Collections.sort(listWord,new Comparator<Word>(){  
	            @Override  
	            public int compare(Word b1, Word b2) {  
	            	if(0==b1.strContent.compareTo(b2.strContent)){
	            		if (0 == b1.strAlphabet.compareTo(b2.strAlphabet)) {
							return 0;
						}else {
							return 1;
						}
	            	}else {
						return 1;
					}	                 
	            } 	              
	        });			

	        for (WordIndex wordIndex : listWordIndex) {		 
				wordIndex.strAlphabet = pinyinFormatter.convertToneMark2ToneNumber(wordIndex.strAlphabet);
				wordIndex.strShortAlphabet = GetShortAlphabet(wordIndex.strAlphabet).toLowerCase();
				wordIndex.iDeal = 1; 
			}	        
	        
	        int wordid=0;
	        for (Word word : listWord) {
				wordid++;	
				if (word.iDeal != 1) {
					word.strAlphabet = pinyinFormatter.convertToneMark2ToneNumber(word.strAlphabet);	
					word.strShortAlphabet = GetShortAlphabet(word.strAlphabet).toLowerCase();
					word.iDeal = 1;
				}
				word.strWordID = String.valueOf(wordid);
				
				if (word.strFirstWordId.isEmpty()) {
					WordIndex wordIndex = new WordIndex();
					wordIndex.firstChar = word.strContent.substring(0,1);
					wordIndex.strAlphabet = word.strAlphabet.substring(0, findFirstPosInDestStr(MyStaticValue.mathNumber,word.strAlphabet)+1);
					wordIndex.strShortAlphabet = GetShortAlphabet(wordIndex.strAlphabet).toLowerCase();
					wordIndex.iDeal = 1;
					if (!listWordIndex.contains(wordIndex)) {
						listWordIndex.add(wordIndex); 
					}				
				}
			}
	        
		    //根据Collections.sort重载方法来实现  
	        Collections.sort(listWordIndex,new Comparator<WordIndex>(){  
	            @Override  
	            public int compare(WordIndex b1, WordIndex b2) {
	            	if (0 == b1.strAlphabet.compareTo(b2.strAlphabet)) {
							return 0;
						}else {
							return 1;
						}  
	            }
	        });			
	        
	        //统计首字词语个数
	        CountFirstWords(listWordIndex,listWord);
	        
			//首字打印
			/*String FirstTitle = FillBlankSpace("字")+FillBlankSpace("PinYin")+FillBlankSpace("词个数")+"counts="+listWordIndex.size();
		    bw.write(FirstTitle); 
		    bw.newLine();
		    
		    for (WordIndex wordIndex : listWordIndex) {
				String index =FillBlankSpace(wordIndex.firstChar) + FillBlankSpace(wordIndex.strAlphabet)+FillBlankSpace(String.valueOf(wordIndex.iCount))+ wordIndex.strShortAlphabet;
			    bw.write(index); 
			    bw.newLine();
			}*/
		    
		    
			//sortNumber 赋值
			int iSortNumber = 0;
			Word preWord = listWord.get(0); 
			preWord.iSortNumber = iSortNumber;
			for (int i=1;i<listWord.size();i++) {
			
				Word word2 = listWord.get(i);   
				String strTemp = new String();
				boolean bIsFirst1 = true;
				for (String key : word2.mapPosAndMean.keySet()) {
					if (bIsFirst1) {
						strTemp = key;
						bIsFirst1 = false;
					}
					else {
						strTemp = ","+key ;
					}
					word2.strPos += strTemp;
				}
							
				
				preWord = listWord.get(i-1);
				if (word2.strContent.substring(0, 1).compareTo(preWord.strContent.substring(0, 1)) == 0) {
					iSortNumber ++ ;
					word2.iSortNumber = iSortNumber;				
				}
				else {
					iSortNumber = 0;
					continue;
				}			
			}
			
			
			List<WordNotes> listWordNotes =  new ArrayList<WordNotes>();
			//词语打印
			String title = FillBlankSpace("SQE")+FillBlankSpace("词")+FillBlankSpace("PinYin")+FillBlankSpace("Short")+FillBlankSpace("SORT_NUMBER")+FillBlankSpace("POS")+FillBlankSpace("释义")+"counts="+listWord.size();
			bw.write(title); 
		    bw.newLine();
			
		    for (Word word : listWord) {
				if (null == word) {
					continue;
				} 
				
				String contents = word.strContent;
				String Alphabet = word.strAlphabet;
				contents = FillBlankSpace(contents);
				Alphabet = FillBlankSpace(Alphabet); 
				String lineData = FillBlankSpace(word.strWordID)+contents+Alphabet+FillBlankSpace(word.strShortAlphabet)+"  "+FillBlankSpace(String.valueOf(word.iSortNumber));
				
				//词性和释义遍历 
				for (String key : word.mapPosAndMean.keySet()) {		
					Set<String> value = word.mapPosAndMean.get(key);
					key = FillBlankSpace(key);
					String strlineData = lineData + key;
					for (String str : value) {
						String strline = strlineData + str;  
					    bw.write(strline); 
					    bw.newLine();
					    
						WordNotes wordNotes = new WordNotes();
						wordNotes.strWordID = word.strWordID;
						wordNotes.strIndustryCode = word.strIndustryCode;
						wordNotes.strWordNotes = str;
						wordNotes.strWordPos = key;
						wordNotes.strStatus = "1";
						wordNotes.iWordFre = 0;
						wordNotes.iSortNumber = word.iSortNumber;
						listWordNotes.add(wordNotes);
					}
				}
				if (word.mapPosAndMean.size() == 0) {
					String strline = lineData + FillBlankSpace(word.strPos)+word.strSimpleMean; 
				    bw.write(strline); 
				    bw.newLine();
					WordNotes wordNotes = new WordNotes();
					wordNotes.strWordID = word.strWordID;
					wordNotes.strIndustryCode = word.strIndustryCode;
					wordNotes.strWordNotes = word.strSimpleMean;
					wordNotes.strWordPos = word.strPos;
					wordNotes.strStatus = "1";
					wordNotes.iWordFre = 0;
					wordNotes.iSortNumber = word.iSortNumber;
					listWordNotes.add(wordNotes);
				}
			} 
			
			//词表
			Word wordSql = new Word();
			JdbcUtils.getInstance().executeBatch(wordSql, listWord); 
			
			//词索引
			WordIndex wordIndexSql = new WordIndex();
			JdbcUtils.getInstance().executeBatch(wordIndexSql, listWordIndex); 
			
			//词释义
			WordNotes wordNotesSql = new WordNotes();
			JdbcUtils.getInstance().executeBatch(wordNotesSql, listWordNotes);
			
						
			//成语打印
			/*String FirstTitle = FillBlankSpace("SQE")+ FillBlankSpace("词")+FillBlankSpace("PinYin")+FillBlankSpace("释义")+FillBlankSpace("出处")+"counts="+listWord.size();
		    bw.write(FirstTitle); 
		    bw.newLine();
		    
		    for (Word word : listWordIdiom) {
				if (null == word) {
					continue;
				}
				String index = FillBlankSpace(word.strWordID) +FillBlankSpace(word.strContent) + FillBlankSpace(word.strAlphabet) + FillBlankSpace(word.strSimpleMean) + word.strSource;
			    bw.write(index); 
			    bw.newLine();
			}*/
			bw.close(); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	} 

	//添加空格
	public static String FillBlankSpace(String str){ 

		String temp = str;
		if (null == temp || "" == temp) {
			return temp;
		}
		
		if(15 > str.length()){
			int n =15 - temp.length();
			char[] chars= temp.toCharArray();
			if (0 == chars.length) {
				return temp;
			}
			while(0 != n){
				//中文字符
				if(chars[0] >= 0x0391 && chars[0] <= 0xFFE5){
					temp+="  ";
				}
				else {
					temp+=" ";
				}

				n--;
			}		
		}
		return temp;
	}
	
	//统计首字对应的词个数
	public static void CountFirstWords(List<WordIndex> listWordIndex,List<Word> listWord){
		//求wordIndex中的wordcounts 
		int indexId =0;
		for (WordIndex wordIndex : listWordIndex){
			int iCount = 0;		
			wordIndex.strIndexId = String.valueOf(indexId);
			indexId++;
			for (int i=0;i<listWord.size();i++) {
				Word word = listWord.get(i);
				if (word.strContent.substring(0, 1).equals(wordIndex.firstChar) 
				    && word.strAlphabet.substring(0, findFirstPosInDestStr(MyStaticValue.mathNumber,word.strAlphabet)+1).equals(wordIndex.strAlphabet)) {
					iCount++; 	
					word.strFirstWordId = wordIndex.strIndexId;
					word.strFirstWord   = wordIndex.firstChar;  
				}
			} 
			wordIndex.iCount = iCount;
		}
	} 
	
	//拼音缩写
	public static String GetShortAlphabet(String strAlphabet){
		char[] chars = strAlphabet.toCharArray();
		if (chars.length<=0) {
			return "";
		}
		char firstChar = chars[0];
		String strShort = new String();
		strShort += String.valueOf(firstChar);
		
		for (int i = 1; i < chars.length; i++) {
			char c = chars[i];
			//数字
			if (c>='1' && c<='9') {
				if (i+1 == chars.length) {
					continue;
				}
				strShort += String.valueOf(chars[i+1]);
			}
		}
		return strShort;
	}
	
	/*
	 * 返回字符串中特殊字符第一次出现的位置
	 */
	public static int findFirstPosInDestStr(String strSource, String strDest){
		if (strDest.isEmpty() || strSource.isEmpty()) {
			return -1;			
		}
		int iPos =0 ;
		char[] chars = strDest.toCharArray();
		for (int i=0 ;i<chars.length;i++) {
			if (strSource.contains(String.valueOf(chars[i]))) {
				iPos = i;
				break;
			}
		}
		return iPos;
	}
}

