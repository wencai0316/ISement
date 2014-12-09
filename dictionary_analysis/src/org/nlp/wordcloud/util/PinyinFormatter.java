package org.nlp.wordcloud.util;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Contains logic to format given Pinyin string
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
public class PinyinFormatter
{
    /**
     * @param pinyinStr
     *            unformatted Hanyu Pinyin string
     * @param outputFormat
     *            given format of Hanyu Pinyin
     * @return formatted Hanyu Pinyin string
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String formatHanyuPinyin(String pinyinStr,
            HanyuPinyinOutputFormat outputFormat)
            throws BadHanyuPinyinOutputFormatCombination
    {
        if ((HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType())
                && ((HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType()) || (HanyuPinyinVCharType.WITH_U_AND_COLON == outputFormat.getVCharType())))
        {
            throw new BadHanyuPinyinOutputFormatCombination("tone marks cannot be added to v or u:");
        }

        if (HanyuPinyinToneType.WITHOUT_TONE == outputFormat.getToneType())
        {
            pinyinStr = pinyinStr.replaceAll("[1-5]", "");
        } else if (HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType())
        {
            pinyinStr = pinyinStr.replaceAll("u:", "v");
            pinyinStr = convertToneNumber2ToneMark(pinyinStr);
        }
        

        if (HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType())
        {
            pinyinStr = pinyinStr.replaceAll("u:", "v");
        } else if (HanyuPinyinVCharType.WITH_U_UNICODE == outputFormat.getVCharType())
        {
            pinyinStr = pinyinStr.replaceAll("u:", "ü");
        }

        if (HanyuPinyinCaseType.UPPERCASE == outputFormat.getCaseType())
        {
            pinyinStr = pinyinStr.toUpperCase();
        }
        return pinyinStr;
    }

    /**
     * Convert tone numbers to tone marks using Unicode <br/><br/>
     * 
     * <b>Algorithm for determining location of tone mark</b><br/>
     * 
     * A simple algorithm for determining the vowel on which the tone mark
     * appears is as follows:<br/>
     * 
     * <ol>
     * <li>First, look for an "a" or an "e". If either vowel appears, it takes
     * the tone mark. There are no possible pinyin syllables that contain both
     * an "a" and an "e".
     * 
     * <li>If there is no "a" or "e", look for an "ou". If "ou" appears, then
     * the "o" takes the tone mark.
     * 
     * <li>If none of the above cases hold, then the last vowel in the syllable
     * takes the tone mark.
     * 
     * </ol>
     * 
     * @param pinyinStr
     *            the ascii represention with tone numbers
     * @return the unicode represention with tone marks
     */
    public static String convertToneNumber2ToneMark(final String pinyinStr)
    {
        String lowerCasePinyinStr = pinyinStr.toLowerCase();

        if (lowerCasePinyinStr.matches("[a-z]*[1-5]?"))
        {
            final char defautlCharValue = '$';
            final int defautlIndexValue = -1;

            char unmarkedVowel = defautlCharValue;
            int indexOfUnmarkedVowel = defautlIndexValue;

            final char charA = 'a';
            final char charE = 'e';
            final String ouStr = "ou";
            final String allUnmarkedVowelStr = "aeiouv";
            final String allMarkedVowelStr = "āáăàaēéĕèeīíĭìiōóŏòoūúŭùuǖǘǚǜü";

            if (lowerCasePinyinStr.matches("[a-z]*[1-5]"))
            {

                int tuneNumber = Character.getNumericValue(lowerCasePinyinStr.charAt(lowerCasePinyinStr.length() - 1));

                int indexOfA = lowerCasePinyinStr.indexOf(charA);
                int indexOfE = lowerCasePinyinStr.indexOf(charE);
                int ouIndex = lowerCasePinyinStr.indexOf(ouStr);

                if (-1 != indexOfA)
                {
                    indexOfUnmarkedVowel = indexOfA;
                    unmarkedVowel = charA;
                } else if (-1 != indexOfE)
                {
                    indexOfUnmarkedVowel = indexOfE;
                    unmarkedVowel = charE;
                } else if (-1 != ouIndex)
                {
                    indexOfUnmarkedVowel = ouIndex;
                    unmarkedVowel = ouStr.charAt(0);
                } else
                {
                    for (int i = lowerCasePinyinStr.length() - 1; i >= 0; i--)
                    {
                        if (String.valueOf(lowerCasePinyinStr.charAt(i)).matches("["
                                + allUnmarkedVowelStr + "]"))
                        {
                            indexOfUnmarkedVowel = i;
                            unmarkedVowel = lowerCasePinyinStr.charAt(i);
                            break;
                        }
                    }
                }

                if ((defautlCharValue != unmarkedVowel)
                        && (defautlIndexValue != indexOfUnmarkedVowel))
                {
                    int rowIndex = allUnmarkedVowelStr.indexOf(unmarkedVowel);
                    int columnIndex = tuneNumber - 1;

                    int vowelLocation = rowIndex * 5 + columnIndex;

                    char markedVowel = allMarkedVowelStr.charAt(vowelLocation);

                    StringBuffer resultBuffer = new StringBuffer();

                    resultBuffer.append(lowerCasePinyinStr.substring(0, indexOfUnmarkedVowel).replaceAll("v", "ü"));
                    resultBuffer.append(markedVowel);
                    resultBuffer.append(lowerCasePinyinStr.substring(indexOfUnmarkedVowel + 1, lowerCasePinyinStr.length() - 1).replaceAll("v", "ü"));

                    return resultBuffer.toString();

                } else
                // error happens in the procedure of locating vowel
                {
                    return lowerCasePinyinStr;
                }
            } else
            // input string has no any tune number
            {
                // only replace v with ü (umlat) character
                return lowerCasePinyinStr.replaceAll("v", "ü");
            }
        } else
        // bad format
        {
            return lowerCasePinyinStr;
        }
    }
    public PinyinFormatter() {
		// TODO Auto-generated constructor stub
	}
    
 
    
    /*
     * 将音调格式转换成数字格式
     */
    public  String convertToneMark2ToneNumber(final String pinyinStr){

        String lowerCasePinyinStr = pinyinStr.toLowerCase();
        lowerCasePinyinStr = lowerCasePinyinStr.replace('’', ' ');
        lowerCasePinyinStr = lowerCasePinyinStr.replace("'", " ");   
        lowerCasePinyinStr = lowerCasePinyinStr.replace('-', ' ');   
        lowerCasePinyinStr = lowerCasePinyinStr.replace('，', ' ');    
        lowerCasePinyinStr = lowerCasePinyinStr.replace(',', ' ');  
        lowerCasePinyinStr = lowerCasePinyinStr.replace(' ', ' '); 
        lowerCasePinyinStr = lowerCasePinyinStr.replace('。', ' '); 
        lowerCasePinyinStr = lowerCasePinyinStr.replace('*', ' '); 
        lowerCasePinyinStr = lowerCasePinyinStr.replace('、', ' '); 
        lowerCasePinyinStr = lowerCasePinyinStr.replace('》', ' '); 
        String[] pinyinArrayStrings = lowerCasePinyinStr.split(" "); 
        
        final String allMarkedVowelStr = "āáǎàaēéěèeīíǐìiōóǒòoūúǔùuǖǘǚǜü";
        final String[] allInitialStr = {"b","p","m","f","d","t","n","l","ɡ","k","h","j","q","x","zh","ch","sh","r","z","c","s","y","w"};  
        final String[] allVowelStr = {"i","u","ü","a","ia","ua","o","uo","e","ie","üe","ai","uai","ei","ui","ao","iao","ou","iu","an","ian"
        		,"uan","üan","en","in","un","ün","anɡ","ianɡ","uanɡ","enɡ","inɡ","onɡ","ionɡ","on","ue","er"};  //on 最大匹配ong,ue 匹配yue
 
        StringBuffer tempPinyinBuffer =  new StringBuffer();
        
        for (String pinyin : pinyinArrayStrings) {
        	//记录 韵母的位置很声调
            String  tempPinyinString =  new String();
            Map<Integer, String> mapVowel = new HashMap<Integer, String>();
            char[] chars = pinyin.toCharArray();
    		for (int i = 0; i<chars.length; i++)
    		{
    			char c =  chars[i];  
    			int columnIndex = 0;
    			int iIndex = 0;
    			iIndex = allMarkedVowelStr.indexOf(c); 
    			columnIndex = iIndex%5;
    			int iToneNumber = columnIndex+1;
    			//存在韵母
    		    if (iIndex>=0 && iToneNumber<5){ 
    		    	mapVowel.put(i, String.valueOf(iToneNumber));
    		    }		    
    		}
    		
    		pinyin = pinyin.replaceAll("[āáǎà]", "a");
    		pinyin = pinyin.replaceAll("[ēéěè]", "e");
    		pinyin = pinyin.replaceAll("[īíǐì]", "i");
    		pinyin = pinyin.replaceAll("[ōóǒò]", "o");
    		pinyin = pinyin.replaceAll("[ūúǔù]", "u");
    		pinyin = pinyin.replaceAll("[ǖǘǚǜ]", "ü");
    		
    		pinyin = pinyin.replaceAll("[g]", "ɡ"); 
    		
    		String initialString = new String();
    		String vowelsString  = new String();
 
    		//分出单个字拼音
    		chars = pinyin.toCharArray();
    		int i =0;
    		while(i<chars.length){
    			//拼音首字母位置     			
    			int j = i;      			
    			for (; j<chars.length; j++){
    				//首字母
    				   String tempString = pinyin.substring(i,j+1);
    		    	if (MatchStringIndex(tempString,allInitialStr)) {
    		    		initialString = tempString;
    					continue;
    				}else {	 
    					break;
    				}
    			}
    			
    			//韵母开始的位置
    			int m = j;
    			for (; j<chars.length; j++){    				
    				//检查韵母：
    				String tempString = pinyin.substring(m,j+1);
    		    	if (MatchStringIndex(tempString,allVowelStr)) {
    		    		vowelsString = tempString; 
    					continue;
    				}else {
    					break;
    				}
    			} 
    			
    			String oneWordPinyinString = initialString + vowelsString;
    			initialString = "";
    			vowelsString  = "";
    			i+=oneWordPinyinString.length();
    			tempPinyinString += oneWordPinyinString+"$";
    		}
    		for (Integer key:mapVowel.keySet()) {
				String iToneNumber = mapVowel.get(key);
				tempPinyinString = tempPinyinString.replaceFirst("[$]", iToneNumber);
			}
    		tempPinyinBuffer.append(tempPinyinString);
    		tempPinyinString="";
 		}        
        
        return tempPinyinBuffer.toString().replace("$","");
    }
	
    //
    public  boolean MatchStringIndex(String tempString,String[] stringArray){
    	boolean bMatch = false;
    	for (int i = 0; i < stringArray.length; i++) {
			if (0 == stringArray[i].compareTo(tempString)) {				
				bMatch = true;
				return bMatch;
			}
		}
    	return bMatch;    	
    }
    
 
}


