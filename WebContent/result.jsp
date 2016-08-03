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

<style>
.iheader{
	paddig-left:50px;
	paddig-top:50px;
	height:90px;
	background: rgba(255, 255, 255, 0.3);
	width:100%
}
.iheaderlogo{
	float:left;
	padding:20px;
	height:20px;
	width:150px;
}
.isearchbar{
	float:left;
	height:20px;
	margin-top:10px;
}
.iresults{
	background: rgba(255, 255, 255, 0.8);
	paddng:50px;
}
.resultBlock{
	margin-left:20px;
	padding:10px;
	width:80%;
}

.resultPages{
	margin-left:20px;
	padding:10px;
	width:80%;
}

.resultPages a{
	text-decoration:none;
	color:#2966B0;
}

.ititle {
	color:#113E67;
	font-weight:bold;
}

.ititle hover{
	color:#2966B0;
}

.ikeywords{
	color:#616161;
	font-size:14px;
}

.iabstract{
	color:#545960;
	font-size:15px;
}
.resultPageBlock {
	margin-left:15px;
	margin-right:15px;
	margin-bottom:15px;
	float:left;
	text-align:center;
}

</style>

<body>

<div>

<div class="iheader">
	<div class="iheaderlogo">
	<a href="http://localhost:8080/CufeSearch/index.jsp"><img src="images/logo2.png" /></a>
	</div>
			<div class="s-bar3">
			   <form  style="width:100%;"action="<c:url value='/CoreServlet'/>" method="post" >
				<input class="isearchtext" type="text" name="queryString" value="${queryString }" onfocus="this.value = '';" onblur="if (this.value == '') {this.value = '输入关键词';}">
				<input type="submit"  value="Search"/>
				<input type="hidden" name="method" value="query" />
				<input type="hidden" name="currentPage" value="1" />
			  </form>
			</div>
</div>

<div class="iresults">
	<div class="resultBlock">
		<h5 color="#113E67">共检索出 ${resultSize } 条结果，用时 ${timespan } 秒</h5>
	</div>
	
	
	  <c:forEach items="${result }" var="elem">
		  <div class="resultBlock">
		 	<a class="ititle" href="${elem.docUrl }" target="_blank">${elem.docTitle }</a>
		 	<p class="ikeywords">关键词：  ${elem.docKeywords }</p>
		 	<p class="ikeywords">发布时间：  ${elem.docDate }</p>
		 	<p class="iabstract">摘要：  ${elem.docAbstract }</p>
		  </div>
	  </c:forEach>
</div>


<div class="iresults">
<br/>
	<div class="resultPages">
	<div class="resultPageBlock">第</div>
	
	<c:choose>
	
	<c:when test="${queryType!=null }">
	  <c:forEach var="i" begin="1" end="${totalPages }" step="1"> 
	   <div class="resultPageBlock">
	     <c:choose>
	     	<c:when test="${i == currentPage }">
	     		<span>${i }</span>
	     	</c:when>
	     	<c:otherwise>
	     		<a href="http://localhost:8080/CufeSearch/CoreServlet?method=dateQuery&dateType=otherDate&fromDate=${queryType[1] }&toDate=${queryType[2]}&queryString=${queryString }&currentPage=${i}">${i }</a>
	     	</c:otherwise>
	     </c:choose>
	     </div>
	   </c:forEach>
	</c:when>
	
	<c:otherwise>
	   <c:forEach var="i" begin="1" end="${totalPages }" step="1"> 
	   <div class="resultPageBlock">
	     <c:choose>
	     	<c:when test="${i == currentPage }">
	     		<span>${i }</span>
	     	</c:when>
	     	<c:otherwise>
	     		<a href="http://localhost:8080/CufeSearch/CoreServlet?method=query&queryString=${queryString }&currentPage=${i}">${i }</a>
	     	</c:otherwise>
	     </c:choose>
	     </div>
	   </c:forEach>
	   
	 </c:otherwise>
	 </c:choose>
	   
	   
	<div class="resultPageBlock">页</div>
	</div><br/>
	<br/><br/><br/><br/><br/>
</div>



<div class="copyright" style="margin:3em 0em 0em 3em;">
	 <p>2016 &copy 信管13 辛东兴 束磊 田浩翔 &nbsp;&nbsp;  |&nbsp;  <a href="manage.jsp" target="_blank"> 索引维护 </a></p>
</div>	
<br/>
<br/>
<br/>

</body>
</html>