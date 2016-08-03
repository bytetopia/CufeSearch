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
 * Searcher类，用于对给定查询进行检索
 * 支持一般检索和按日期范围检索，并将结果进行了适配网页显示的处理
 */
public class Searcher {
	
	//用于动态摘要的HTML头尾，可将动态摘要在网页中以红色字显示
	private static String prefixHTML = "<font color='red'>";
	private static String suffixHTML = "</font>";
	
	

	/**
	 * main方法，测试时使用，可直接在控制台运行查询
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
   * search方法，处理一般查询
   */
  public static Map<String,Object> search(File indexDir, String queryStr, String fieldName,int currentPage)
    throws Exception {
	
	//与indexer中使用相同的IK分词器
	//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
	//Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_40); 
	Analyzer analyzer = new IKAnalyzer();
	//Analyzer analyzer =  new SmartChineseAnalyzer(Version.LUCENE_40);
	
	//创建search对象
	Directory indexLucDir = FSDirectory.open(indexDir);
	DirectoryReader reader = DirectoryReader.open(indexLucDir);
	IndexSearcher isearcher = new IndexSearcher(reader); //create the indexSearch object
	
	//解析queryStr
	QueryParser parser = new QueryParser(Version.LUCENE_40, fieldName, analyzer);
	Query query = parser.parse(queryStr);
	
	//进行检索并计时
    long start = new Date().getTime(); 
    //最大支持返回10000条文档搜索结果，在该项目应用场景下够用了
    TopDocs results = isearcher.search(query, null, 10000);
	ScoreDoc[] hits = results.scoreDocs;
    long end = new Date().getTime();
    
    //控制台后台输出信息提示
    System.err.println("Found " + hits.length +
      " document(s) (in " + (end - start) +
      " milliseconds) that matched query '" +
      queryStr + "':");
    
    
    List<ResultDoc> resultList=new ArrayList<ResultDoc>();
    Map<String,Object> resultMap=new HashMap<String,Object>();
    
    /*
     * 这段代码用于处理分页。分页实质是每次重新请求查询，并从结果集中摘取与currentPage相对应范围的一部分，作为最终返回结果。
     * 分页默认每页20条记录，使用分页可以减轻每次提取动态摘要和关键词的时间，大大提高检索速度
     */
    //查询起始记录位置
    int beginRec = 20 * (currentPage - 1);
    //查询终止记录位置
    int endRec = Math.min(beginRec + 20,hits.length);
    int totalPages=hits.length/20;
    if(hits.length%20!=0){
    	totalPages++;
    }
    
    
    
    //遍历要摘取的结果
    for (int i = beginRec; i <endRec; i++) {
      Document doc = isearcher.doc(hits[i].doc);
      Explanation explanation =  isearcher.explain(query, hits[i].doc);
      
      //调用Highlighter进行动态摘要的处理
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
      //处理后的摘要文本中，关键词两侧会加上html标签，在页面中以红色文字显示
      
      //截取标题、摘要、文件名等各字段信息，封装到 resultDoc对象中
      ResultDoc resultDoc=new ResultDoc();
      String fileName=doc.get("filename");
      resultDoc.setDocTitle(fileName.substring(fileName.indexOf("][")+13,fileName.indexOf(".txt")));
      resultDoc.setDocAbstract("..."+highLightText+"...");
      resultDoc.setDocScore(explanation.getValue());
      resultDoc.setDocUrl("http://news.cufe.edu.cn/jdxw/"+fileName.substring(fileName.indexOf("[")+1,fileName.indexOf("]"))+".htm");
      String tempKeyword="";
      
      //调用关键词KeywordUtil计算文档关键词
      String[] keywords=KeywordUtil.getKeyWords(text, 6);
      for (int j=0;j<6;j++) {
		tempKeyword=tempKeyword+keywords[j]+" ";
      }
      resultDoc.setDocKeywords(tempKeyword);
      resultDoc.setDocDate(fileName.substring(fileName.indexOf("][")+2,fileName.indexOf("][")+12));
     
      //将最终的结果文档添加到结果列表中
      resultList.add(resultDoc);
      
      
    }
    
    //最终结果以Map形式返回，包括含有结果文档集的list，以及查询结果条数、总页数等参数信息
    resultMap.put("list", resultList);
    resultMap.put("resultSize", hits.length);
    resultMap.put("totalPages", totalPages);
    return resultMap;

  }
  

  /**
   * 该方法处理按指定日期范围的检索，需要传入日期范围参数，格式是字符串，形如"2010-01-01"
   */
  
  public static Map<String,Object> searchByTimeRange(File indexDir, String queryStr, String fieldName,int currentPage,String startDateStr,String endDateStr)
		    throws Exception {
			
	  		//与indexer中使用相同的IK分词器
			//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			//Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_40); 
			Analyzer analyzer = new IKAnalyzer();
			//Analyzer analyzer =  new SmartChineseAnalyzer(Version.LUCENE_40);
			
			//创建searcher对象
			Directory indexLucDir = FSDirectory.open(indexDir);
			DirectoryReader reader = DirectoryReader.open(indexLucDir);
			IndexSearcher isearcher = new IndexSearcher(reader); 
			
			//解析queryStr
			QueryParser parser = new QueryParser(Version.LUCENE_40, fieldName, analyzer);
			Query query = parser.parse(queryStr); 
			
			//处理日期范围限制
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 		    
			Long startDate=sdf.parse(startDateStr).getTime();
			Long endDate=sdf.parse(endDateStr).getTime();
			//因为可能出现颠倒了起止日期，造成终止日期比起始日期还小的情况，这里进行修正
			Long maxDate=Math.max(startDate, endDate);
			Long minDate=Math.min(startDate, endDate);
			//对检索添加一个数字类型的范围过滤器
			Filter dateFilter=NumericRangeFilter.newLongRange("date", minDate, maxDate, true, true);
			
			//进行检索并计时
		    long start = new Date().getTime(); 
		    //第二个参数即为过滤器，null表示不过滤
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
		     * 这段代码用于处理分页。分页实质是每次重新请求查询，并从结果集中摘取与currentPage相对应范围的一部分，作为最终返回结果。
		     * 分页默认每页20条记录，使用分页可以减轻每次提取动态摘要和关键词的时间，大大提高检索速度
		     */
		    //查询起始记录位置
		    int beginRec = 20 * (currentPage - 1);
		    //查询终止记录位置
		    int endRec = Math.min(beginRec + 20,hits.length);
		    int totalPages=hits.length/20;
		    if(hits.length%20!=0){
		    	totalPages++;
		    }
		      		    
		    //遍历要摘取的结果
		    for (int i = beginRec; i <endRec; i++) {
		      Document doc = isearcher.doc(hits[i].doc);
		      Explanation explanation =  isearcher.explain(query, hits[i].doc);
		      
		      //调用Highlighter进行动态摘要的处理
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
		      
		      //截取标题、摘要、文件名等各字段信息，封装到 resultDoc对象中
		      ResultDoc resultDoc=new ResultDoc();
		      String fileName=doc.get("filename");
		      resultDoc.setDocTitle(fileName.substring(fileName.indexOf("][")+13,fileName.indexOf(".txt")));
		      resultDoc.setDocAbstract("..."+highLightText+"...");
		      resultDoc.setDocScore(explanation.getValue());
		      resultDoc.setDocUrl("http://news.cufe.edu.cn/jdxw/"+fileName.substring(fileName.indexOf("[")+1,fileName.indexOf("]"))+".htm");
		     
		    //调用关键词KeywordUtil计算文档关键词
		      String tempKeyword="";
		      String[] keywords=KeywordUtil.getKeyWords(text, 6);
		      for (int j=0;j<6;j++) {
				tempKeyword=tempKeyword+keywords[j]+" ";
		      }
		      resultDoc.setDocKeywords(tempKeyword);
		      resultDoc.setDocDate(fileName.substring(fileName.indexOf("][")+2,fileName.indexOf("][")+12));
		      
		     
		      //将最终的结果文档添加到结果列表中
		      resultList.add(resultDoc);
		      
		      
		    }
		  //最终结果以Map形式返回，包括含有结果文档集的list，以及查询结果条数、总页数等参数信息
		    resultMap.put("list", resultList);
		    resultMap.put("resultSize", hits.length);
		    resultMap.put("totalPages", totalPages);
		    return resultMap;

		  }
		  


}
