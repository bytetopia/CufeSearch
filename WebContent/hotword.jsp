<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE HTML>
<html>
<head>
<title>中财热词 - 今天搜神马</title>
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
	margin-top:20px;
}

</style>


<body>




<div>

		<div class="search2" >
			<a  href="http://localhost:8080/CufeSearch/index.jsp"><img src="images/logo.png"/></a>
		</div>
		
		
<div class="whitebox">
		<div class="textprompt">
			<p id="promptText" style="color:#113E67;">为您发掘中财新闻网一周/一月内热点关键词！</p>
		</div>

		
		
		<!--search start here-->
		<div class="search">
			<div class="s-bar">
			   <form  onsubmit=" return update()" action="<c:url value='/CoreServlet'/>" method="post" >
			  	 <select name="hotwordType" style="height:50px;width:100px;margin-right:40px;">
						<option value="WEEK">一周热词</option>
						<option value="MONTH">一月热词</option>
					</select>
				<input id="button" type="submit" onclick="" value="开始计算"/>
				<input type="hidden" name="method" value="hotword" />
			  </form>
			</div>
		</div>
</div>
		<!--search end here-->	
		<div class="below-search">
		<br/>
		<br/>
		 <p><a href="boolsearch.jsp" target="_blank"> 布尔检索 </a>|
		 <a href="datesearch.jsp" target="_blank"> 按时间检索 </a>|
		 <a href="hotword.jsp" target="_blank"> 中财热词 </a></p>
		</div>	
</div>




<div class="copyright">
	 <p>2016 &copy 信管13 辛东兴 束磊 田浩翔 &nbsp;&nbsp;  |&nbsp;  <a href="manage.jsp" target="_blank"> 索引维护 </a></p>
</div>	
</body>

<script>
function update() {
	document.getElementById('promptText').innerText= '正在计算热词，请耐心等待！';
	document.getElementById('button').value= '计算中...';
	document.getElementById('button').disabled='disabled';
	return true;
}

</script>


</html>