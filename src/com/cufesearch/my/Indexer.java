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
 * ����lucene��indexer�࣬���ڶԱ���txt�ļ���������
 */

public class Indexer {


	/**
	 * Ҫͨ��main�������Խ�����������Ҫ��run config��ָ����������
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
   * ����IndexWriter�ͷִ���Analyzer���󣬵���indexDirectory()��һ������
   */
  public static int index(File indexDir, File dataDir)
    throws Exception {

    if (!dataDir.exists() || !dataDir.isDirectory()) {
      throw new IOException(dataDir
        + " does not exist or is not a directory");
    }

    Directory indexLucDir = FSDirectory.open(indexDir);
    
    //�ִ���ѡ������ʹ����IKAnalyzer
    //����ͨ��src�µ�IKAnalyzer.cfg.xmlΪ��ָ�����´ʱ��ͣ�ôʱ����Ż��ִʺͼ���
    
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
   * ����Ŀ¼�µ������ļ������������indexFile()������������
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
   * Ϊÿһ���ĵ���������
   * ע����������ʱд����������Ϣ�ֶΣ��Ա��ڰ����ڼ��� 
   */
  private static void indexFile(IndexWriter iwriter, File f)
    throws Exception {

    if (f.isHidden() || !f.exists() || !f.canRead()) {
      return;
    }

    System.out.println("Indexing " + f.getCanonicalPath());

    Document doc = new Document();
    Field pathField = new StringField("filename", f.getName(), Field.Store.YES); 
   
    //��Ϊû��ʹ�����ݿ⣬�ĵ���������Ϣ��Ϊ�ļ�����һ���֣���ν����ڴ��ļ����ַ����н�������
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
    String dateStr=f.getName().substring(f.getName().indexOf("][")+2,f.getName().indexOf("][")+12);
    Date dateObj=sdf.parse(dateStr);
    
    //����һ�������ֶ�
    Field dateField=new LongField("date", dateObj.getTime(), Field.Store.YES);
   
    //�����ں��ļ����ֶ���ӵ�doc����
    doc.add(pathField);
    doc.add(dateField);
    
    //��ȡ�ļ�����������
    FileInputStream fis = new FileInputStream(f);
    Field contentField = new TextField("content", new BufferedReader(new InputStreamReader(fis, "GBK")));
    doc.add(contentField);
    iwriter.addDocument(doc);

    fis.close();
  }
}
