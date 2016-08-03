<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE HTML>
<html>
<head>
<title>今天搜神马 - 中财新闻早知道</title>
<!-- Custom Theme files -->
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
<link href="css/bootstrap.css" rel="stylesheet" type="text/css" media="all"/>
<!-- Custom Theme files -->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
<meta name="keywords" content="Flat Search Box Responsive, Login form web template, Sign up Web Templates, Flat Web Templates, Login signup Responsive web template, Smartphone Compatible web template, free webdesigns for Nokia, Samsung, LG, SonyErricsson, Motorola web design" />
<!--Google Fonts-->
<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
<!--Google Fonts-->
</head>


<style type="text/css">
.whitebox {
	padding:20px;
	width:60%;
	background: rgba(255, 255, 255, 0.8);
	margin:0 auto;
}

</style>

<body>

<div>
		<div class="search2">
			<a href="index.jsp"><img src="images/logo2.png" /></a>
		</div>
		
		<!--search start here-->
	<div class="whitebox">
		<div class="search">
		<h4 style="color:#113E67">按日期搜索</h4>
		<br/>
		
			
			<form  action="<c:url value='/CoreServlet'/>" method="post" >
				<table style="text-align:left;margin:0px auto;">
				<tr><td style="width:70px;">
				 关键词 </td>
					<td>&nbsp;<input style="width:410px;" type="text" name="queryString"></td>
				</tr>
				<tr><td style="color:#FFFFFF">|</td></tr>
				<tr><td>
				 日期范围&nbsp;&nbsp;</td>
					<td><input type="radio" name="dateType" value="oneWeek" />最近一周</td></tr>
					<tr><td></td><td>
					<input type="radio" name="dateType" value="oneMonth" />最近一月</td></tr>
					<tr><td></td><td>
					<input type="radio" name="dateType" value="otherDate" />指定范围 &nbsp;&nbsp;
					从
					<input type="date" name="fromDate" value="" /> 
					到
					<input type="date" name="toDate" value="" />
					</td></tr>
					<tr></tr>
					<tr><td>
					<input type="hidden" name="method" value="dateQuery" />
					<input type="hidden" name="currentPage" value="1" />
			</table>	
			<br/>
					<input type="submit" style="height:35px;width:80px;" value="搜索"/>
			</form>	
		</div>
		<br/>
		<br/>
	</div>
		<div class="below-search">
		<br/>
		<br/>
		 <p><a href="boolsearch.jsp" target="_blank"> 布尔检索 </a>|
		 <a href="datesearch.jsp" target="_blank"> 按时间检索 </a>|
		 <a href="hotword.jsp" target="_blank"> 中财热词 </a></p>
		</div>	
		<!--search end here-->	
</div>


<div class="copyright">
	 <p>2016 &copy 信管13 辛东兴 束磊 田浩翔 &nbsp;&nbsp;  |&nbsp;  <a href="manage.jsp" target="_blank"> 索引维护 </a></p>
</div>	
</body>
</html>