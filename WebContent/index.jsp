<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE HTML>
<html>
<head>
<title>今天搜神马 - 中财新闻早知道</title>
<!-- Custom Theme files -->
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
<!-- Custom Theme files -->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
<meta name="keywords" content="Flat Search Box Responsive, Login form web template, Sign up Web Templates, Flat Web Templates, Login signup Responsive web template, Smartphone Compatible web template, free webdesigns for Nokia, Samsung, LG, SonyErricsson, Motorola web design" />
<!--Google Fonts-->
<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
<!--Google Fonts-->
</head>
<body>

<div>
		<div class="search2">
			<a href="index.jsp"><img src="images/logo.png" /></a>
		</div>
		
		<!--search start here-->
		<div class="search">
			<i> </i>
			
			<div class="s-bar">
			   <form  action="<c:url value='/CoreServlet'/>" method="post" >
				<input type="text" name="queryString" value="输入关键词" onfocus="this.value = '';" onblur="if (this.value == '') {this.value = '输入关键词';}">
				<input type="submit"  value="Search"/>
				<input type="hidden" name="method" value="query" />
				<input type="hidden" name="currentPage" value="1" />
			  </form>
			  
			</div>
		
		</div>
		<div class="below-search">
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