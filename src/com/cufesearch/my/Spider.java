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
 * 该类为爬虫，自动从网页上爬取链接和文本信息，并将解析后的正文存放到txt文件中
 * @author dox19
 *
 */
public class Spider {
	
	//指定保存路径
	private static String savePath="D:\\cufesearch\\downloads\\";
	//保存所有要下载页面的list
	public static List<String> urlList=new ArrayList<String>();

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		spiderMain();
	}
	
	
	/**
	 * 爬取中财新闻网综合要闻栏目下的网页，解析出网页的新闻正文，并将正文在指定路径下保存为txt文件
	 */
	public static void spiderMain(){

		System.out.println("开始爬取网页...");
		
		//先从综合要闻的新闻列表里解析出每个新闻详细页面的地址，存放到urlList里
		System.out.println("正在解析页面地址...");
		parseLinks("http://news.cufe.edu.cn/jdxw/index.htm");
		for(int i=1;i<10;i++){
			parseLinks("http://news.cufe.edu.cn/jdxw/index"+i+".htm");
		}
		
		
		//遍历urlList，对其中每个网页调用parseOneText解析出正文并保存
		
		System.out.println("开始下载网页正文...");
		int j=0;
		for (String oneUrl : urlList) {
			parseOneText(oneUrl);
			j++;
			if(j%20==0){
				System.out.println("正在下载第"+j+"个网页...");
			}

		}
		System.out.println("下载完成");
		urlList.clear();
		
		
	}
	
	/**
	 * 解析指定地址的页面，并将其正文下载到txt文件里
	 * @param url
	 */
	public static void parseOneText(String url){
		Document doc;
		String title="",date="";
		StringBuffer article=new StringBuffer();
		try {
			doc = Jsoup.connect(url).get();
			
			//使用jsoup解析该页面新闻的标题信息
			Elements titops=doc.getElementsByClass("titop");
			for (Element element : titops) {
				Elements h3s=element.getElementsByTag("h3");
				for (Element h3 : h3s) {
					title=h3.text();
					title=title.replaceAll("/", "-");
				}
			}
			
			//使用jsoup解析该页面新闻的发表日期信息
			Elements showDate=doc.getElementsByClass("nrmid03");
			for (Element element : showDate) {
					date=element.text();
					date=date.substring(7, 17);
			}
			
			//使用jsoup解析该页面新闻的正文信息，并去除正文中可能还有的<br/>等html标签
			Elements texts=doc.getElementsByClass("neir");
			for (Element div : texts) {
				article.append(Jsoup.parse(div.text()).text());
			}
			
			//以该页面地址中的数字+日期+页面标题作为txt文件的名称
			String txtFileName=url.substring(url.indexOf("jdxw")+5);
			txtFileName=txtFileName.substring(0,txtFileName.indexOf(".htm"));
			txtFileName="["+txtFileName+"]["+date+"]"+title+".txt";
			
			//将页面内容保存到本地txt文件中
			File fp=new File(savePath+txtFileName);
			PrintWriter pfp= new PrintWriter(fp);
			pfp.print(article.toString());
			pfp.close();
					
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		
	
	/**
	 * 从新闻列表页html中解析出各个子页面的地址
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
	 * 将html文件下载到本地
	 * 这个没有用到
	 * @param urlStr 网址
	 * @param fileName 文件名（包括后缀）
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


