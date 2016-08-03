package com.cufesearch.my;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;



/**
 * ����servlet�����ڴ���jsp��ҳ���̨����Ľ���
 *
 */
public class CoreServlet extends BaseServlet {

	/**
	 * ����һ���ѯ����
	 */
	public String query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//��ȡ��ѯ�ַ���
			String queryString=request.getParameter("queryString");
			//Ϊʵ�ַ�ҳ��ÿ�β�ѯ����ָ����ǰҳ��
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			//��ʱ��
			long start = new Date().getTime(); 
			//����Search����ͨsearch�������һ���ѯ
			Map<String,Object> resultMap=Searcher.search(new File("D:\\cufesearch\\index"), queryString, "content",currentPage);
			//��ѯ���ÿ���ĵ���һ��ResultDoc���󣬷�װ��List��
			List<ResultDoc> result=(List<ResultDoc>)resultMap.get("list");
			//��ȡ��������������ʾ
			int resultSize=(int)(resultMap.get("resultSize"));
			int totalPages=(int)(resultMap.get("totalPages"));
			long end = new Date().getTime();
			//��������jsp��ʾ�ĸ�����
			request.setAttribute("result", result);
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
			request.setAttribute("queryString", queryString);
			request.setAttribute("resultSize", resultSize);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("currentPage", currentPage);
			//ת��ҳ�浽���ҳ
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";
	}
		
	
	/**
	 * ʵ�ֲ��������ķ���
	 */
	public String boolQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//��������Ϊ���������м���and or��not���ӣ����Ȼ�ȡ����Щ������ƴ�ɲ�ѯ�ַ���
			String queryString1=request.getParameter("queryString1");
			String queryString2=request.getParameter("queryString2");
			String boolConnector=request.getParameter("boolConnector");
			String queryString=queryString1+" "+boolConnector+" "+queryString2;
			//��ȡ��ҳ��
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			long start = new Date().getTime(); 
			//����Searcher��search�������в�ѯ
			Map<String,Object> resultMap=Searcher.search(new File("D:\\cufesearch\\index"), queryString, "content",currentPage);
			//���ؽ���ŵ�ResultDoc��list��
			List<ResultDoc> result=(List<ResultDoc>)resultMap.get("list");
			int resultSize=(int)(resultMap.get("resultSize"));
			int totalPages=(int)(resultMap.get("totalPages"));
			long end = new Date().getTime();
			//��������jsp��ʾ�ĸ�����
			request.setAttribute("result", result);
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
			request.setAttribute("queryString", queryString);
			request.setAttribute("resultSize", resultSize);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("currentPage", currentPage);
			//ת��ҳ�浽���ҳ
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";

	}
		
	
	
	/**
	 * ʵ�ְ����ڲ�ѯ�ķ���
	 */
	public String dateQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			
			String startDateStr,endDateStr;	
			//SimpleDateFormatter���� ���硰2010-01-01���������ַ�����Date���� �� ��ʾʱ��ĳ�����ֵ ֮����໥ת��
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 		  
			
			//����ҳ�����л�ȡ�����ڲ�ѯ������
			String dateType=request.getParameter("dateType");
			if(dateType.equals("oneWeek")){
				//����ǲ�ѯ��һ�ܣ�����ֹʱ���� һ��ǰ~����
				startDateStr=sdf.format(new Date(System.currentTimeMillis()- 7l * 24 * 60 * 60*1000));
				endDateStr=sdf.format(new Date(System.currentTimeMillis()));
			}
			else if(dateType.equals("oneMonth")){
				//����ǲ�ѯ��һ�£�����ֹʱ���� һ��ǰ~����
				startDateStr=sdf.format(new Date(System.currentTimeMillis() - 30l * 24 * 60 * 60*1000));
				//���tip��30*24*60*60*1000Ĭ��Ϊint��������int��ȡֵ��Χ����������������Ҫ��long�������㡣30��������7�첻�ᡣ
				endDateStr=sdf.format(new Date(System.currentTimeMillis()));
			}
			else{
				//�����ǰ��Զ������ڲ�ѯ���Ȼ�ȡ�û�ָ������ֹ���ڲ���
				startDateStr=request.getParameter("fromDate");
				endDateStr=request.getParameter("toDate");
				//�����ʼ����Ϊ�գ�ָֻ���˽������ڣ���Ĭ��ָ����ʼ����Ϊ2000-1-1
				if(startDateStr.isEmpty() && !endDateStr.isEmpty()){
					startDateStr="2000-01-01";
				}
				//�����������Ϊ�գ�ָֻ���˿�ʼ���ڣ���Ĭ��ָ����������Ϊ����
				else if(!startDateStr.isEmpty() && endDateStr.isEmpty()){
					endDateStr=sdf.format(new Date(System.currentTimeMillis()));
				}
				//������߶�Ϊ�գ���תΪ�������ڲ�ѯ
				if(startDateStr.isEmpty() || endDateStr.isEmpty()){
					return query(request,response);
				}
				//��������������������˭��˭С����Searcher����м��������
			}
			
			String queryString=request.getParameter("queryString");
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			long start = new Date().getTime(); 
			System.out.println("����������������from="+startDateStr+"&end="+endDateStr);
			
			//����Searcher���searchByTimeRange�������а����ڷ�Χ��������ָ�������޷�Χ
			Map<String,Object> resultMap=Searcher.searchByTimeRange(new File("D:\\cufesearch\\index"), queryString, "content",currentPage,startDateStr,endDateStr);
			//����ķ�װ����ʾ������ͬ��
			List<ResultDoc> result=(List<ResultDoc>)resultMap.get("list");
			int resultSize=(int)(resultMap.get("resultSize"));
			int totalPages=(int)(resultMap.get("totalPages"));
			long end = new Date().getTime();
			request.setAttribute("result", result);
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
			request.setAttribute("queryString", queryString);
			request.setAttribute("resultSize", resultSize);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("currentPage", currentPage);
			//ע�⣺��ʱ��εĲ�ѯ��������ҳʱ��һ���ѯ��ͬ����Ҫ�������queryType���ֶ�
			request.setAttribute("queryType", new String[]{"dateQuery",startDateStr,endDateStr});
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";

	}
	
	
	/**
	 * �Զ����������ķ���
	 */
	public String update(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//����IndexingHandler������������Ĳ���
			long start = new Date().getTime(); 
			if(IndexingHandler.handleIndexing()){
				request.setAttribute("updateMsg", "�������³ɹ���");
			}
			else{
				request.setAttribute("updateMsg", "��������ʧ�ܣ������ԣ�");
			}
			long end = new Date().getTime();
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
		}
		catch(Exception e){
			
		}
		
		return "f:/manage.jsp";
	}
	
	
	/**
	 * �����ȴʵķ���
	 */
	public String hotword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//�ж���һ���ȴʻ���һ���ȴ�
			int type= request.getParameter("hotwordType").equals("WEEK")? 1:2;
			//����KeywordUtil���õ�ǰ20�ؼ���
			List<String[]> hotwords=KeywordUtil.getHotwords(type, 20);
			//������ز�������web��ʾ
			request.setAttribute("hotwords", hotwords);
			request.setAttribute("hotType", type==1?"һ���ȴ�":"һ���ȴ�");
		}
		catch(Exception e){
			
		}
		return "f:/hotwordResult.jsp";

	}
	
	
	
}
