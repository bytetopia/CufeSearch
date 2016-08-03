package com.cufesearch.my;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Searcher�࣬���ڶԸ�����ѯ���м���
 * ֧��һ������Ͱ����ڷ�Χ�������������������������ҳ��ʾ�Ĵ���
 */
public class Searcher {
	
	//���ڶ�̬ժҪ��HTMLͷβ���ɽ���̬ժҪ����ҳ���Ժ�ɫ����ʾ
	private static String prefixHTML = "<font color='red'>";
	private static String suffixHTML = "</font>";
	
	

	/**
	 * main����������ʱʹ�ã���ֱ���ڿ���̨���в�ѯ
	 */
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new Exception("Usage: java " + Searcher.class.getName()
        + " <index dir>");
    }

    Scanner input = new Scanner(System.in);
    System.out.println("Enter a query string:");
    String queryStr = input.nextLine();    
    System.out.println(queryStr);
    File indexDir = new File(args[0]);

    if (!indexDir.exists() || !indexDir.isDirectory()) {
      throw new Exception(indexDir +
        " does not exist or is not a directory.");
    }

    search(indexDir, queryStr, "content",1);
  }



  /**
   * search����������һ���ѯ
   */
  public static Map<String,Object> search(File indexDir, String queryStr, String fieldName,int currentPage)
    throws Exception {
	
	//��indexer��ʹ����ͬ��IK�ִ���
	//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
	//Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_40); 
	Analyzer analyzer = new IKAnalyzer();
	//Analyzer analyzer =  new SmartChineseAnalyzer(Version.LUCENE_40);
	
	//����search����
	Directory indexLucDir = FSDirectory.open(indexDir);
	DirectoryReader reader = DirectoryReader.open(indexLucDir);
	IndexSearcher isearcher = new IndexSearcher(reader); //create the indexSearch object
	
	//����queryStr
	QueryParser parser = new QueryParser(Version.LUCENE_40, fieldName, analyzer);
	Query query = parser.parse(queryStr);
	
	//���м�������ʱ
    long start = new Date().getTime(); 
    //���֧�ַ���10000���ĵ�����������ڸ���ĿӦ�ó����¹�����
    TopDocs results = isearcher.search(query, null, 10000);
	ScoreDoc[] hits = results.scoreDocs;
    long end = new Date().getTime();
    
    //����̨��̨�����Ϣ��ʾ
    System.err.println("Found " + hits.length +
      " document(s) (in " + (end - start) +
      " milliseconds) that matched query '" +
      queryStr + "':");
    
    
    List<ResultDoc> resultList=new ArrayList<ResultDoc>();
    Map<String,Object> resultMap=new HashMap<String,Object>();
    
    /*
     * ��δ������ڴ����ҳ����ҳʵ����ÿ�����������ѯ�����ӽ������ժȡ��currentPage���Ӧ��Χ��һ���֣���Ϊ���շ��ؽ����
     * ��ҳĬ��ÿҳ20����¼��ʹ�÷�ҳ���Լ���ÿ����ȡ��̬ժҪ�͹ؼ��ʵ�ʱ�䣬�����߼����ٶ�
     */
    //��ѯ��ʼ��¼λ��
    int beginRec = 20 * (currentPage - 1);
    //��ѯ��ֹ��¼λ��
    int endRec = Math.min(beginRec + 20,hits.length);
    int totalPages=hits.length/20;
    if(hits.length%20!=0){
    	totalPages++;
    }
    
    
    
    //����Ҫժȡ�Ľ��
    for (int i = beginRec; i <endRec; i++) {
      Document doc = isearcher.doc(hits[i].doc);
      Explanation explanation =  isearcher.explain(query, hits[i].doc);
      
      //����Highlighter���ж�̬ժҪ�Ĵ���
	  SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");    
	  Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));    
	  FileInputStream fis = new FileInputStream(new File("D:\\cufesearch\\downloads\\"+doc.get("filename")));
	  BufferedReader breader=new BufferedReader(new InputStreamReader(fis, "GBK"));
	  String temp = "";
	  String text = "";
	  while((temp = breader.readLine()) != null)
	  {
	  text = text + temp;
	  }
      String highLightText = highlighter.getBestFragment(analyzer,"content",text);
      //������ժҪ�ı��У��ؼ�����������html��ǩ����ҳ�����Ժ�ɫ������ʾ
      
      //��ȡ���⡢ժҪ���ļ����ȸ��ֶ���Ϣ����װ�� resultDoc������
      ResultDoc resultDoc=new ResultDoc();
      String fileName=doc.get("filename");
      resultDoc.setDocTitle(fileName.substring(fileName.indexOf("][")+13,fileName.indexOf(".txt")));
      resultDoc.setDocAbstract("..."+highLightText+"...");
      resultDoc.setDocScore(explanation.getValue());
      resultDoc.setDocUrl("http://news.cufe.edu.cn/jdxw/"+fileName.substring(fileName.indexOf("[")+1,fileName.indexOf("]"))+".htm");
      String tempKeyword="";
      
      //���ùؼ���KeywordUtil�����ĵ��ؼ���
      String[] keywords=KeywordUtil.getKeyWords(text, 6);
      for (int j=0;j<6;j++) {
		tempKeyword=tempKeyword+keywords[j]+" ";
      }
      resultDoc.setDocKeywords(tempKeyword);
      resultDoc.setDocDate(fileName.substring(fileName.indexOf("][")+2,fileName.indexOf("][")+12));
     
      //�����յĽ���ĵ���ӵ�����б���
      resultList.add(resultDoc);
      
      
    }
    
    //���ս����Map��ʽ���أ��������н���ĵ�����list���Լ���ѯ�����������ҳ���Ȳ�����Ϣ
    resultMap.put("list", resultList);
    resultMap.put("resultSize", hits.length);
    resultMap.put("totalPages", totalPages);
    return resultMap;

  }
  

  /**
   * �÷�������ָ�����ڷ�Χ�ļ�������Ҫ�������ڷ�Χ��������ʽ���ַ���������"2010-01-01"
   */
  
  public static Map<String,Object> searchByTimeRange(File indexDir, String queryStr, String fieldName,int currentPage,String startDateStr,String endDateStr)
		    throws Exception {
			
	  		//��indexer��ʹ����ͬ��IK�ִ���
			//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			//Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_40); 
			Analyzer analyzer = new IKAnalyzer();
			//Analyzer analyzer =  new SmartChineseAnalyzer(Version.LUCENE_40);
			
			//����searcher����
			Directory indexLucDir = FSDirectory.open(indexDir);
			DirectoryReader reader = DirectoryReader.open(indexLucDir);
			IndexSearcher isearcher = new IndexSearcher(reader); 
			
			//����queryStr
			QueryParser parser = new QueryParser(Version.LUCENE_40, fieldName, analyzer);
			Query query = parser.parse(queryStr); 
			
			//�������ڷ�Χ����
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 		    
			Long startDate=sdf.parse(startDateStr).getTime();
			Long endDate=sdf.parse(endDateStr).getTime();
			//��Ϊ���ܳ��ֵߵ�����ֹ���ڣ������ֹ���ڱ���ʼ���ڻ�С������������������
			Long maxDate=Math.max(startDate, endDate);
			Long minDate=Math.min(startDate, endDate);
			//�Լ������һ���������͵ķ�Χ������
			Filter dateFilter=NumericRangeFilter.newLongRange("date", minDate, maxDate, true, true);
			
			//���м�������ʱ
		    long start = new Date().getTime(); 
		    //�ڶ���������Ϊ��������null��ʾ������
		    TopDocs results = isearcher.search(query, dateFilter, 10000);
			ScoreDoc[] hits = results.scoreDocs;
		    long end = new Date().getTime();
		    
		    System.err.println("Found " + hits.length +
		      " document(s) (in " + (end - start) +
		      " milliseconds) that matched query '" +
		      queryStr + "':");
		    
		    
		    List<ResultDoc> resultList=new ArrayList<ResultDoc>();
		    Map<String,Object> resultMap=new HashMap<String,Object>();
		    
		    /*
		     * ��δ������ڴ����ҳ����ҳʵ����ÿ�����������ѯ�����ӽ������ժȡ��currentPage���Ӧ��Χ��һ���֣���Ϊ���շ��ؽ����
		     * ��ҳĬ��ÿҳ20����¼��ʹ�÷�ҳ���Լ���ÿ����ȡ��̬ժҪ�͹ؼ��ʵ�ʱ�䣬�����߼����ٶ�
		     */
		    //��ѯ��ʼ��¼λ��
		    int beginRec = 20 * (currentPage - 1);
		    //��ѯ��ֹ��¼λ��
		    int endRec = Math.min(beginRec + 20,hits.length);
		    int totalPages=hits.length/20;
		    if(hits.length%20!=0){
		    	totalPages++;
		    }
		      		    
		    //����Ҫժȡ�Ľ��
		    for (int i = beginRec; i <endRec; i++) {
		      Document doc = isearcher.doc(hits[i].doc);
		      Explanation explanation =  isearcher.explain(query, hits[i].doc);
		      
		      //����Highlighter���ж�̬ժҪ�Ĵ���
			  SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");    
			  Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));    
			  FileInputStream fis = new FileInputStream(new File("D:\\cufesearch\\downloads\\"+doc.get("filename")));
			  BufferedReader breader=new BufferedReader(new InputStreamReader(fis, "GBK"));
			  String temp = "";
			  String text = "";
			  while((temp = breader.readLine()) != null)
			  {
			  text = text + temp;
			  }
		      String highLightText = highlighter.getBestFragment(analyzer,"content",text);
		      
		      //��ȡ���⡢ժҪ���ļ����ȸ��ֶ���Ϣ����װ�� resultDoc������
		      ResultDoc resultDoc=new ResultDoc();
		      String fileName=doc.get("filename");
		      resultDoc.setDocTitle(fileName.substring(fileName.indexOf("][")+13,fileName.indexOf(".txt")));
		      resultDoc.setDocAbstract("..."+highLightText+"...");
		      resultDoc.setDocScore(explanation.getValue());
		      resultDoc.setDocUrl("http://news.cufe.edu.cn/jdxw/"+fileName.substring(fileName.indexOf("[")+1,fileName.indexOf("]"))+".htm");
		     
		    //���ùؼ���KeywordUtil�����ĵ��ؼ���
		      String tempKeyword="";
		      String[] keywords=KeywordUtil.getKeyWords(text, 6);
		      for (int j=0;j<6;j++) {
				tempKeyword=tempKeyword+keywords[j]+" ";
		      }
		      resultDoc.setDocKeywords(tempKeyword);
		      resultDoc.setDocDate(fileName.substring(fileName.indexOf("][")+2,fileName.indexOf("][")+12));
		      
		     
		      //�����յĽ���ĵ���ӵ�����б���
		      resultList.add(resultDoc);
		      
		      
		    }
		  //���ս����Map��ʽ���أ��������н���ĵ�����list���Լ���ѯ�����������ҳ���Ȳ�����Ϣ
		    resultMap.put("list", resultList);
		    resultMap.put("resultSize", hits.length);
		    resultMap.put("totalPages", totalPages);
		    return resultMap;

		  }
		  


}
