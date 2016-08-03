package com.cufesearch.my;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;


/**
 * 该类主要处理与文档关键词或热词有关的操作，均为静态方法外界可直接 类名.方法名 来调用
 *
 */
public class KeywordUtil {

	
		/**
		 * 计算关键词第 1 步：将一长串文本用分词器断开成一个词项List
		 * 注意该list中会存在很多重复词项
		 */
		private static List<String> extract(String article) {
			
			List<String> list =new ArrayList<String>();         
			IKAnalyzer analyzer = new IKAnalyzer();             
			analyzer.setUseSmart(true); 
			try{
				TokenStream tokenStream=                            //用IKanalyzer处理字符
						analyzer.tokenStream("", new StringReader(article));
				while (tokenStream.incrementToken()) {              //逐个截出分词
					CharTermAttribute charTermAttribute =           
							tokenStream.getAttribute(CharTermAttribute.class);
					String keWord= charTermAttribute.toString();    
					if (keWord.length()>1) {                     	//两个字及以上的才会被当作关键词
						list.add(keWord);                           
					}
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				analyzer.close();
			}
			return list;
		}

		
		
		/**
		 * 计算关键词第 2 步：对list集合中的各词统计出现频次，放到Map键值对中
		 */
		private static Map<String, Integer> listToMap(List<String> list){
			Map<String, Integer> map=new HashMap<String, Integer>();
			for(String key:list){                      
				
					if(map.get(key)==null){
						map.put(key,1);
					}
					else{
						map.put(key,map.get(key)+1);
					}
				                                    
			}                                          
			return map;
		}

		
		
		/**
		 * 计算关键词第 3 步：生成关键词的总方法，传入长文本，返回指定个数的关键词
		 * @param article 文章长字符串
		 * @param n  要返回多少关键词
		 * @return
		 */
		public static String[] getKeyWords(String article,Integer n){
			
			//首先将长文本分词
			List<String> keyWordsList= extract(article);         
			//然后统计词频
			Map<String, Integer> map=listToMap(keyWordsList); 
			
			//对词频进行排序
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//如果总关键词个数小于请求的关键词个数，则只返回总关键词数
			if (list.size()<n){
				n=list.size();
			}
		
			//将关键词封装到String数组中返回
			String[] keywords=new String[n];                  
			for(int i=0; i<n; i++) {                 
				keywords[i]=list.get(i).getKey();     
			}
			return keywords;
			
		}
		
		
		
		
		/**
		 * 计算关键词第 3 步：生成关键词的总方法，传入长文本，返回指定个数的关键词
		 * @param article 文章长字符串
		 * @param n  要返回多少关键词
		 * @return
		 */
		public static Map<String,Integer> getKeyWordsForHotwords(String article,Integer n){
			
			//首先将长文本分词
			List<String> keyWordsList= extract(article);         
			//然后统计词频
			Map<String, Integer> map=listToMap(keyWordsList); 
			
			//对词频进行排序
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//如果总关键词个数小于请求的关键词个数，则只返回总关键词数
			if (list.size()<n){
				n=list.size();
			}
		
			//将关键词封装到String数组中返回
			Map<String,Integer> keywords=new HashMap<String,Integer>();                  
			for(int i=0; i<n; i++) {                 
				keywords.put(list.get(i).getKey(), list.get(i).getValue());     
			}
			return keywords;
			
		}
		
		
		/**
		 * 处理热词的方法
		 */
		public static List<String[]> getHotwords(int type,int topNum){
		try{
			
			Map<String,Integer> resultMap=new HashMap<String,Integer>();
			resultMap.clear();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 	
		    
			//首先判断一周热词还是一个月热词，确定时间区间
	        long startDateLong=0;
	        long endDateLong=System.currentTimeMillis();
	        if(type==1){//类型1为一周热词
	        	startDateLong=endDateLong- 7l * 24 * 60 * 60 * 1000;
			}
			else if(type==2){//类型2为一月热词
				startDateLong=endDateLong- 30l * 24 * 60 * 60 * 1000;
			}
	        
	        //拿到所有文件
	        File fileDir = new File("D:\\cufesearch\\downloads\\");
	        File[] files = fileDir.listFiles();
	        
	        //因为文件在列表中是按时间先后正序排列，只需要取最新的文件，所以倒着从列表中拿取
	        for(int i=files.length-1;i>=0;i--){
	        	System.out.println(files[i].getName());
	        	String dateStr=files[i].getName().substring(files[i].getName().indexOf("][")+2,files[i].getName().indexOf("][")+12);
	        	long fileDateLong=sdf.parse(dateStr).getTime();
	        	if(fileDateLong<startDateLong){
	        		//如果当前文件日期比规定的起始日期早，说明已经拿到指定日期范围的所有文件，结束循环
	        		break;
	        	}
	        	else{
	        		//否则，说明需要读取解析该文件
	        		//读出文本
	        		  FileInputStream fis = new FileInputStream(new File("D:\\cufesearch\\downloads\\"+files[i].getName()));
	        		  BufferedReader breader=new BufferedReader(new InputStreamReader(fis, "GBK"));
	        		  String temp = "";
	        		  String text = "";
	        		  while((temp = breader.readLine()) != null)
	        		  {
	        		  text = text + temp;
	        		  }
	        		 //计算该文档的关键词
	        		  Map<String,Integer> oneDocKeywords=getKeyWordsForHotwords(text,15);
	        		  //将该文档的数据装入Map中
	        		  for (String keyStr : oneDocKeywords.keySet()) {
						if(!resultMap.containsKey(keyStr)){
							resultMap.put(keyStr, (int)(Math.sqrt(oneDocKeywords.get(keyStr))));
						}
						else{
							resultMap.put(keyStr, resultMap.get(keyStr)+(int)(Math.sqrt(oneDocKeywords.get(keyStr))));
						}
	        		  }
	        		  
	        	}
	        }
	        
			//对词频进行排序
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(resultMap.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//如果总关键词个数小于请求的关键词个数，则只返回总关键词数
			if (list.size()<topNum){
				topNum=list.size();
			}
			
			//需返回关键词内容和频次，所以使用一个元素为string数组的list对象返回
			List<String[]> hotwords=new ArrayList<String[]>();
			for(int i=0; i<topNum; i++) {                 
				hotwords.add(new String[]{list.get(i).getKey(),list.get(i).getValue()+""});
			}
			
			return hotwords;
	        
		}   
	     catch(Exception e){
	    	 e.printStackTrace();
	     }
		
		return null;
		}
	
}
