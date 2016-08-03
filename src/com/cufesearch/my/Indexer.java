package com.cufesearch.my;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


/**
 * 操作lucene的indexer类，用于对本地txt文件建立索引
 */

public class Indexer {


	/**
	 * 要通过main方法尝试建立索引，需要在run config中指定两个参数
	 */
  public static void main(String[] args)  throws Exception {
		    if (args.length != 2) {
		        throw new Exception("Usage: java " + Indexer.class.getName()
		          + " <index dir> <data dir>");
		      }
		      File indexDir = new File(args[0]);
		      File dataDir = new File(args[1]);

		      long start = new Date().getTime();
		      int numIndexed = index(indexDir, dataDir);
		      long end = new Date().getTime();

		      System.out.println("Indexing " + numIndexed + " files took "
		        + (end - start) + " milliseconds");

  }



  /**
   * 创建IndexWriter和分词器Analyzer对象，调用indexDirectory()进一步处理
   */
  public static int index(File indexDir, File dataDir)
    throws Exception {

    if (!dataDir.exists() || !dataDir.isDirectory()) {
      throw new IOException(dataDir
        + " does not exist or is not a directory");
    }

    Directory indexLucDir = FSDirectory.open(indexDir);
    
    //分词器选择，这里使用了IKAnalyzer
    //并且通过src下的IKAnalyzer.cfg.xml为其指定了新词表和停用词表，以优化分词和检索
    
    //Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
    //Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_40);
    Analyzer analyzer = new IKAnalyzer();
   // Analyzer analyzer =  new SmartChineseAnalyzer(Version.LUCENE_40);
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
    config.setOpenMode(OpenMode.CREATE);//Create new indexes in the directory, removing any previously indexed documents:
    IndexWriter iwriter = new IndexWriter(indexLucDir, config);

    indexDirectory(iwriter, dataDir);

    int numIndexed = iwriter.numDocs();
    iwriter.forceMerge(1);
    iwriter.close();
    return numIndexed;
  }


  /**
   * 遍历目录下的所有文件，并逐个调用indexFile()进行索引处理
   */
  private static void indexDirectory(IndexWriter iwriter, File dataDir)
    throws Exception {

    File[] files = dataDir.listFiles();

    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (f.isDirectory()) {
        indexDirectory(iwriter, f);  // recurse
      } else if (f.getName().endsWith(".txt")) {
        indexFile(iwriter, f);
      }
    }
  }



  /**
   * 为每一个文档创建索引
   * 注：建立索引时写入了日期信息字段，以便于按日期检索 
   */
  private static void indexFile(IndexWriter iwriter, File f)
    throws Exception {

    if (f.isHidden() || !f.exists() || !f.canRead()) {
      return;
    }

    System.out.println("Indexing " + f.getCanonicalPath());

    Document doc = new Document();
    Field pathField = new StringField("filename", f.getName(), Field.Store.YES); 
   
    //因为没有使用数据库，文档的日期信息作为文件名的一部分，这段将日期从文件名字符串中解析出来
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
    String dateStr=f.getName().substring(f.getName().indexOf("][")+2,f.getName().indexOf("][")+12);
    Date dateObj=sdf.parse(dateStr);
    
    //创建一个日期字段
    Field dateField=new LongField("date", dateObj.getTime(), Field.Store.YES);
   
    //将日期和文件名字段添加到doc对象
    doc.add(pathField);
    doc.add(dateField);
    
    //读取文件，建立索引
    FileInputStream fis = new FileInputStream(f);
    Field contentField = new TextField("content", new BufferedReader(new InputStreamReader(fis, "GBK")));
    doc.add(contentField);
    iwriter.addDocument(doc);

    fis.close();
  }
}
