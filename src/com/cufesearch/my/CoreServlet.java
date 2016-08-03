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
 * 核心servlet，用于处理jsp网页与后台程序的交互
 *
 */
public class CoreServlet extends BaseServlet {

	/**
	 * 处理一般查询功能
	 */
	public String query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//获取查询字符串
			String queryString=request.getParameter("queryString");
			//为实现分页，每次查询均需指定当前页码
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			//计时器
			long start = new Date().getTime(); 
			//调用Search的普通search方法完成一般查询
			Map<String,Object> resultMap=Searcher.search(new File("D:\\cufesearch\\index"), queryString, "content",currentPage);
			//查询结果每个文档是一个ResultDoc对象，封装到List中
			List<ResultDoc> result=(List<ResultDoc>)resultMap.get("list");
			//获取其他参数用于显示
			int resultSize=(int)(resultMap.get("resultSize"));
			int totalPages=(int)(resultMap.get("totalPages"));
			long end = new Date().getTime();
			//设置用于jsp显示的各参数
			request.setAttribute("result", result);
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
			request.setAttribute("queryString", queryString);
			request.setAttribute("resultSize", resultSize);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("currentPage", currentPage);
			//转发页面到结果页
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";
	}
		
	
	/**
	 * 实现布尔检索的方法
	 */
	public String boolQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//布尔检索为两个词项中间用and or或not连接，首先获取到这些参数并拼成查询字符串
			String queryString1=request.getParameter("queryString1");
			String queryString2=request.getParameter("queryString2");
			String boolConnector=request.getParameter("boolConnector");
			String queryString=queryString1+" "+boolConnector+" "+queryString2;
			//获取分页数
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			long start = new Date().getTime(); 
			//调用Searcher的search方法践行查询
			Map<String,Object> resultMap=Searcher.search(new File("D:\\cufesearch\\index"), queryString, "content",currentPage);
			//返回结果放到ResultDoc的list里
			List<ResultDoc> result=(List<ResultDoc>)resultMap.get("list");
			int resultSize=(int)(resultMap.get("resultSize"));
			int totalPages=(int)(resultMap.get("totalPages"));
			long end = new Date().getTime();
			//设置用于jsp显示的各参数
			request.setAttribute("result", result);
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
			request.setAttribute("queryString", queryString);
			request.setAttribute("resultSize", resultSize);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("currentPage", currentPage);
			//转发页面到结果页
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";

	}
		
	
	
	/**
	 * 实现按日期查询的方法
	 */
	public String dateQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			
			String startDateStr,endDateStr;	
			//SimpleDateFormatter用于 形如“2010-01-01”的日期字符串、Date对象 和 表示时间的长毫秒值 之间的相互转换
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 		  
			
			//从网页请求中获取按日期查询的类型
			String dateType=request.getParameter("dateType");
			if(dateType.equals("oneWeek")){
				//如果是查询近一周，则起止时间是 一周前~今天
				startDateStr=sdf.format(new Date(System.currentTimeMillis()- 7l * 24 * 60 * 60*1000));
				endDateStr=sdf.format(new Date(System.currentTimeMillis()));
			}
			else if(dateType.equals("oneMonth")){
				//如果是查询近一月，则起止时间是 一月前~今天
				startDateStr=sdf.format(new Date(System.currentTimeMillis() - 30l * 24 * 60 * 60*1000));
				//编程tip：30*24*60*60*1000默认为int，超出了int的取值范围，造成溢出，所以需要以long型来计算。30天会溢出，7天不会。
				endDateStr=sdf.format(new Date(System.currentTimeMillis()));
			}
			else{
				//否则是按自定义日期查询，先获取用户指定的起止日期参数
				startDateStr=request.getParameter("fromDate");
				endDateStr=request.getParameter("toDate");
				//如果开始日期为空，只指定了结束日期，则默认指定开始日期为2000-1-1
				if(startDateStr.isEmpty() && !endDateStr.isEmpty()){
					startDateStr="2000-01-01";
				}
				//如果结束日期为空，只指定了开始日期，则默认指定结束日期为今天
				else if(!startDateStr.isEmpty() && endDateStr.isEmpty()){
					endDateStr=sdf.format(new Date(System.currentTimeMillis()));
				}
				//如果两者都为空，则转为不按日期查询
				if(startDateStr.isEmpty() || endDateStr.isEmpty()){
					return query(request,response);
				}
				//这里无需在意两个日期谁大谁小，在Searcher里会有检验和修正
			}
			
			String queryString=request.getParameter("queryString");
			int currentPage=Integer.parseInt(request.getParameter("currentPage"));
			long start = new Date().getTime(); 
			System.out.println("按照日期来检索：from="+startDateStr+"&end="+endDateStr);
			
			//调用Searcher里的searchByTimeRange方法进行按日期范围检索，并指定上下限范围
			Map<String,Object> resultMap=Searcher.searchByTimeRange(new File("D:\\cufesearch\\index"), queryString, "content",currentPage,startDateStr,endDateStr);
			//结果的封装和显示，代码同上
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
			//注意：按时间段的查询，在做分页时与一般查询不同，需要单独添加queryType该字段
			request.setAttribute("queryType", new String[]{"dateQuery",startDateStr,endDateStr});
			return "f:/result.jsp";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return "f:/result.jsp";

	}
	
	
	/**
	 * 自动更新索引的方法
	 */
	public String update(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//调用IndexingHandler处理更新索引的操作
			long start = new Date().getTime(); 
			if(IndexingHandler.handleIndexing()){
				request.setAttribute("updateMsg", "索引更新成功！");
			}
			else{
				request.setAttribute("updateMsg", "索引更新失败，请重试！");
			}
			long end = new Date().getTime();
			request.setAttribute("timespan", ((int)(end-start))/1000.0);
		}
		catch(Exception e){
			
		}
		
		return "f:/manage.jsp";
	}
	
	
	/**
	 * 处理热词的方法
	 */
	public String hotword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException,IOException{
		
		try{
			//判断是一周热词还是一月热词
			int type= request.getParameter("hotwordType").equals("WEEK")? 1:2;
			//调用KeywordUtil，拿到前20关键词
			List<String[]> hotwords=KeywordUtil.getHotwords(type, 20);
			//设置相关参数用于web显示
			request.setAttribute("hotwords", hotwords);
			request.setAttribute("hotType", type==1?"一周热词":"一月热词");
		}
		catch(Exception e){
			
		}
		return "f:/hotwordResult.jsp";

	}
	
	
	
}
