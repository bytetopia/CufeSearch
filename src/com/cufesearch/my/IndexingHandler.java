package com.cufesearch.my;

import java.io.File;


/**
 * 处理一键自动重新爬取和建立索引
 *
 */
public class IndexingHandler {
	
	/**
	 * main方法为测试使用，可在非web页面下自动建立索引
	 */
	public static void main(String[] args){

		try{
			
			File dir=new File("D://cufesearch//downloads");
			if(deleteDir(dir)){
				dir=new File("D://cufesearch//downloads");
				if(dir.mkdir()){
					//重新爬取
					Spider.spiderMain();
					//重新索引
					Indexer.main(new String[]{"D:\\cufesearch\\index","D:\\cufesearch\\downloads"});
				}
			};
		}
		catch(Exception e){
			e.printStackTrace();
		}

		
	}
	
	/**
	 * 递归删除指定目录及其子目录，以及它们当中的文件
	 */
	public static boolean deleteDir(File dir){

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        return dir.delete();
	}
	
	/**
	 * 该方法将被servlet调用用于处理重新爬取和建立索引的操作
	 * @return 返回true时说明重建成功
	 */
	public static boolean handleIndexing(){
		try{
			
			//删除原有索引和文件
			File dir=new File("D://cufesearch//index");
			deleteDir(dir);
			dir=new File("D://cufesearch//downloads");
			deleteDir(dir);

			dir=new File("D://cufesearch//index");
			dir.mkdir();
			dir=new File("D://cufesearch//downloads");
			dir.mkdir();
			
			//调用Spider，重新爬取
			Spider.spiderMain();
			
			//调用Indexer，重新索引
			Indexer.main(new String[]{"D:\\cufesearch\\index","D:\\cufesearch\\downloads"});
			//建立索引成功
			return true;
			
		}
		catch(Exception e){
			e.printStackTrace();
			//输出异常信息，并告知建立索引失败
			return false;
		}
	}
	
	

	
	
	
}
