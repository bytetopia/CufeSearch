package com.cufesearch.my;

import java.io.File;


/**
 * ����һ���Զ�������ȡ�ͽ�������
 *
 */
public class IndexingHandler {
	
	/**
	 * main����Ϊ����ʹ�ã����ڷ�webҳ�����Զ���������
	 */
	public static void main(String[] args){

		try{
			
			File dir=new File("D://cufesearch//downloads");
			if(deleteDir(dir)){
				dir=new File("D://cufesearch//downloads");
				if(dir.mkdir()){
					//������ȡ
					Spider.spiderMain();
					//��������
					Indexer.main(new String[]{"D:\\cufesearch\\index","D:\\cufesearch\\downloads"});
				}
			};
		}
		catch(Exception e){
			e.printStackTrace();
		}

		
	}
	
	/**
	 * �ݹ�ɾ��ָ��Ŀ¼������Ŀ¼���Լ����ǵ��е��ļ�
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
	 * �÷�������servlet�������ڴ���������ȡ�ͽ��������Ĳ���
	 * @return ����trueʱ˵���ؽ��ɹ�
	 */
	public static boolean handleIndexing(){
		try{
			
			//ɾ��ԭ���������ļ�
			File dir=new File("D://cufesearch//index");
			deleteDir(dir);
			dir=new File("D://cufesearch//downloads");
			deleteDir(dir);

			dir=new File("D://cufesearch//index");
			dir.mkdir();
			dir=new File("D://cufesearch//downloads");
			dir.mkdir();
			
			//����Spider��������ȡ
			Spider.spiderMain();
			
			//����Indexer����������
			Indexer.main(new String[]{"D:\\cufesearch\\index","D:\\cufesearch\\downloads"});
			//���������ɹ�
			return true;
			
		}
		catch(Exception e){
			e.printStackTrace();
			//����쳣��Ϣ������֪��������ʧ��
			return false;
		}
	}
	
	

	
	
	
}
