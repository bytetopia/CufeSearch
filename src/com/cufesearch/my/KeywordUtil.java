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
 * ������Ҫ�������ĵ��ؼ��ʻ��ȴ��йصĲ�������Ϊ��̬��������ֱ�� ����.������ ������
 *
 */
public class KeywordUtil {

	
		/**
		 * ����ؼ��ʵ� 1 ������һ�����ı��÷ִ����Ͽ���һ������List
		 * ע���list�л���ںܶ��ظ�����
		 */
		private static List<String> extract(String article) {
			
			List<String> list =new ArrayList<String>();         
			IKAnalyzer analyzer = new IKAnalyzer();             
			analyzer.setUseSmart(true); 
			try{
				TokenStream tokenStream=                            //��IKanalyzer�����ַ�
						analyzer.tokenStream("", new StringReader(article));
				while (tokenStream.incrementToken()) {              //����س��ִ�
					CharTermAttribute charTermAttribute =           
							tokenStream.getAttribute(CharTermAttribute.class);
					String keWord= charTermAttribute.toString();    
					if (keWord.length()>1) {                     	//�����ּ����ϵĲŻᱻ�����ؼ���
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
		 * ����ؼ��ʵ� 2 ������list�����еĸ���ͳ�Ƴ���Ƶ�Σ��ŵ�Map��ֵ����
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
		 * ����ؼ��ʵ� 3 �������ɹؼ��ʵ��ܷ��������볤�ı�������ָ�������Ĺؼ���
		 * @param article ���³��ַ���
		 * @param n  Ҫ���ض��ٹؼ���
		 * @return
		 */
		public static String[] getKeyWords(String article,Integer n){
			
			//���Ƚ����ı��ִ�
			List<String> keyWordsList= extract(article);         
			//Ȼ��ͳ�ƴ�Ƶ
			Map<String, Integer> map=listToMap(keyWordsList); 
			
			//�Դ�Ƶ��������
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//����ܹؼ��ʸ���С������Ĺؼ��ʸ�������ֻ�����ܹؼ�����
			if (list.size()<n){
				n=list.size();
			}
		
			//���ؼ��ʷ�װ��String�����з���
			String[] keywords=new String[n];                  
			for(int i=0; i<n; i++) {                 
				keywords[i]=list.get(i).getKey();     
			}
			return keywords;
			
		}
		
		
		
		
		/**
		 * ����ؼ��ʵ� 3 �������ɹؼ��ʵ��ܷ��������볤�ı�������ָ�������Ĺؼ���
		 * @param article ���³��ַ���
		 * @param n  Ҫ���ض��ٹؼ���
		 * @return
		 */
		public static Map<String,Integer> getKeyWordsForHotwords(String article,Integer n){
			
			//���Ƚ����ı��ִ�
			List<String> keyWordsList= extract(article);         
			//Ȼ��ͳ�ƴ�Ƶ
			Map<String, Integer> map=listToMap(keyWordsList); 
			
			//�Դ�Ƶ��������
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//����ܹؼ��ʸ���С������Ĺؼ��ʸ�������ֻ�����ܹؼ�����
			if (list.size()<n){
				n=list.size();
			}
		
			//���ؼ��ʷ�װ��String�����з���
			Map<String,Integer> keywords=new HashMap<String,Integer>();                  
			for(int i=0; i<n; i++) {                 
				keywords.put(list.get(i).getKey(), list.get(i).getValue());     
			}
			return keywords;
			
		}
		
		
		/**
		 * �����ȴʵķ���
		 */
		public static List<String[]> getHotwords(int type,int topNum){
		try{
			
			Map<String,Integer> resultMap=new HashMap<String,Integer>();
			resultMap.clear();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 	
		    
			//�����ж�һ���ȴʻ���һ�����ȴʣ�ȷ��ʱ������
	        long startDateLong=0;
	        long endDateLong=System.currentTimeMillis();
	        if(type==1){//����1Ϊһ���ȴ�
	        	startDateLong=endDateLong- 7l * 24 * 60 * 60 * 1000;
			}
			else if(type==2){//����2Ϊһ���ȴ�
				startDateLong=endDateLong- 30l * 24 * 60 * 60 * 1000;
			}
	        
	        //�õ������ļ�
	        File fileDir = new File("D:\\cufesearch\\downloads\\");
	        File[] files = fileDir.listFiles();
	        
	        //��Ϊ�ļ����б����ǰ�ʱ���Ⱥ��������У�ֻ��Ҫȡ���µ��ļ������Ե��Ŵ��б�����ȡ
	        for(int i=files.length-1;i>=0;i--){
	        	System.out.println(files[i].getName());
	        	String dateStr=files[i].getName().substring(files[i].getName().indexOf("][")+2,files[i].getName().indexOf("][")+12);
	        	long fileDateLong=sdf.parse(dateStr).getTime();
	        	if(fileDateLong<startDateLong){
	        		//�����ǰ�ļ����ڱȹ涨����ʼ�����磬˵���Ѿ��õ�ָ�����ڷ�Χ�������ļ�������ѭ��
	        		break;
	        	}
	        	else{
	        		//����˵����Ҫ��ȡ�������ļ�
	        		//�����ı�
	        		  FileInputStream fis = new FileInputStream(new File("D:\\cufesearch\\downloads\\"+files[i].getName()));
	        		  BufferedReader breader=new BufferedReader(new InputStreamReader(fis, "GBK"));
	        		  String temp = "";
	        		  String text = "";
	        		  while((temp = breader.readLine()) != null)
	        		  {
	        		  text = text + temp;
	        		  }
	        		 //������ĵ��Ĺؼ���
	        		  Map<String,Integer> oneDocKeywords=getKeyWordsForHotwords(text,15);
	        		  //�����ĵ�������װ��Map��
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
	        
			//�Դ�Ƶ��������
			ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(resultMap.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o2.getValue() - o1.getValue());
				}
			});
			
			//����ܹؼ��ʸ���С������Ĺؼ��ʸ�������ֻ�����ܹؼ�����
			if (list.size()<topNum){
				topNum=list.size();
			}
			
			//�践�عؼ������ݺ�Ƶ�Σ�����ʹ��һ��Ԫ��Ϊstring�����list���󷵻�
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
