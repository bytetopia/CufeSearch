package com.cufesearch.my;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * ����Ϊ���棬�Զ�����ҳ����ȡ���Ӻ��ı���Ϣ����������������Ĵ�ŵ�txt�ļ���
 * @author dox19
 *
 */
public class Spider {
	
	//ָ������·��
	private static String savePath="D:\\cufesearch\\downloads\\";
	//��������Ҫ����ҳ���list
	public static List<String> urlList=new ArrayList<String>();

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		spiderMain();
	}
	
	
	/**
	 * ��ȡ�в��������ۺ�Ҫ����Ŀ�µ���ҳ����������ҳ���������ģ�����������ָ��·���±���Ϊtxt�ļ�
	 */
	public static void spiderMain(){

		System.out.println("��ʼ��ȡ��ҳ...");
		
		//�ȴ��ۺ�Ҫ�ŵ������б��������ÿ��������ϸҳ��ĵ�ַ����ŵ�urlList��
		System.out.println("���ڽ���ҳ���ַ...");
		parseLinks("http://news.cufe.edu.cn/jdxw/index.htm");
		for(int i=1;i<10;i++){
			parseLinks("http://news.cufe.edu.cn/jdxw/index"+i+".htm");
		}
		
		
		//����urlList��������ÿ����ҳ����parseOneText���������Ĳ�����
		
		System.out.println("��ʼ������ҳ����...");
		int j=0;
		for (String oneUrl : urlList) {
			parseOneText(oneUrl);
			j++;
			if(j%20==0){
				System.out.println("�������ص�"+j+"����ҳ...");
			}

		}
		System.out.println("�������");
		urlList.clear();
		
		
	}
	
	/**
	 * ����ָ����ַ��ҳ�棬�������������ص�txt�ļ���
	 * @param url
	 */
	public static void parseOneText(String url){
		Document doc;
		String title="",date="";
		StringBuffer article=new StringBuffer();
		try {
			doc = Jsoup.connect(url).get();
			
			//ʹ��jsoup������ҳ�����ŵı�����Ϣ
			Elements titops=doc.getElementsByClass("titop");
			for (Element element : titops) {
				Elements h3s=element.getElementsByTag("h3");
				for (Element h3 : h3s) {
					title=h3.text();
					title=title.replaceAll("/", "-");
				}
			}
			
			//ʹ��jsoup������ҳ�����ŵķ���������Ϣ
			Elements showDate=doc.getElementsByClass("nrmid03");
			for (Element element : showDate) {
					date=element.text();
					date=date.substring(7, 17);
			}
			
			//ʹ��jsoup������ҳ�����ŵ�������Ϣ����ȥ�������п��ܻ��е�<br/>��html��ǩ
			Elements texts=doc.getElementsByClass("neir");
			for (Element div : texts) {
				article.append(Jsoup.parse(div.text()).text());
			}
			
			//�Ը�ҳ���ַ�е�����+����+ҳ�������Ϊtxt�ļ�������
			String txtFileName=url.substring(url.indexOf("jdxw")+5);
			txtFileName=txtFileName.substring(0,txtFileName.indexOf(".htm"));
			txtFileName="["+txtFileName+"]["+date+"]"+title+".txt";
			
			//��ҳ�����ݱ��浽����txt�ļ���
			File fp=new File(savePath+txtFileName);
			PrintWriter pfp= new PrintWriter(fp);
			pfp.print(article.toString());
			pfp.close();
					
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		
	
	/**
	 * �������б�ҳhtml�н�����������ҳ��ĵ�ַ
	 * @param url 
	 */
	public static void parseLinks(String url){
		Document doc;
		Elements links;
		try {
			doc = Jsoup.connect(url).get();
			Elements linksDiv=doc.getElementsByClass("lb1");
			for (Element element : linksDiv) {
				links=element.getElementsByTag("a");
				for (Element link : links) {
					String tempUrl=link.attr("href");
					if(!tempUrl.contains("/")){
						urlList.add("http://news.cufe.edu.cn/jdxw/"+link.attr("href"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
	/**
	 * ��html�ļ����ص�����
	 * ���û���õ�
	 * @param urlStr ��ַ
	 * @param fileName �ļ�����������׺��
	 */
	public static void downloadOnePage(String urlStr,String fileName){
		
			HttpURLConnection connect=null;
	        BufferedInputStream in=null;
	        FileOutputStream file=null;
	        byte[] buf=new byte[1024];
	        int size=0;
	        
	        try {
	               URL url=new URL(urlStr);  
	               connect=(HttpURLConnection) url.openConnection();
	               connect.connect();   
	               in=new BufferedInputStream(connect.getInputStream());
	               file=new FileOutputStream(savePath+fileName);
		               while((size=in.read(buf))!=-1)
	               {
	                   file.write(buf,0,size);
	               }           
	        }catch (Exception e) {
	            e.printStackTrace();
	        }finally
	        {
	            try {
	                  file.close();
	                  in.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            connect.disconnect();
	        }
	        
	    }
	
	

}


