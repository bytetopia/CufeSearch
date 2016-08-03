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
		<div class="search2" >
			<a  href="http://localhost:8080/CufeSearch/index.jsp"><img src="images/logo.png"/></a>
		</div>
		
		
		<c:choose>
				<c:when test="${empty updateMsg }">
					<div class="textprompt">
						<p id="promptText" style="color:#113E67;">索引维护将调用后台爬虫，重新爬取最新网页并更新索引<br/>以便您搜索到最新的信息</p>
					</div>
				</c:when>
				<c:otherwise>
					<div class="textprompt">
						<p id="promptText" style="color:#113E67;"> ${updateMsg } 耗时 ${timespan } 秒</p>
					</div>
				</c:otherwise>
		</c:choose>
		
		
		<!--search start here-->
		<div class="search">
			<div class="s-bar">
			   <form  onsubmit=" return update()" action="<c:url value='/CoreServlet'/>" method="post" >
				<input id="button" type="submit"  value="开始维护"/>
				<input type="hidden" name="method" value="update" />
			  </form>
			</div>
		
		</div>
		<!--search end here-->	
</div>


<div class="copyright">
	 <p>2016 &copy 信管13 辛东兴 束磊 田浩翔 &nbsp;&nbsp;  |&nbsp;  <a href="manage.jsp" target="_blank"> 索引维护 </a></p>
</div>	
</body>

<script>
function update() {
	document.getElementById('promptText').innerText= '正在更新索引，可能耗时较长，请不要刷新或退出网页';
	document.getElementById('button').value= '更新中,请等待';
	return true;
}

</script>


</html>