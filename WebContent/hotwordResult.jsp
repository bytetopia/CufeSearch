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
	width:500px;
	height:500px;
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



.echartsBlock{
	width:800px;
	height:600px;
	margin:0 auto;
}

</style>

<body>

<div>

<div class="iheader">
	<div class="iheaderlogo">
	<a href="http://localhost:8080/CufeSearch/index.jsp"><img src="images/logo2.png" /></a>
	</div>
</div>

<div class="iresults">
	<div class="echartsBlock" id="echartsBlock">
	</div>
</div>




<div class="copyright" style="margin:3em 0em 0em 3em;">
	 <p>2016 &copy 信管13 辛东兴 束磊 田浩翔 &nbsp;&nbsp;  |&nbsp;  <a href="manage.jsp" target="_blank"> 索引维护 </a></p>
</div>	
<br/>
<br/>
<br/>

<!-- Echarts引入 -->
 <script src="js/esl.js" > </script>
 <script src="js/echarts.js" > </script>

<!-- Echats使用 -->
    <script type="text/javascript">
    // 路径配置
    require.config({
        paths: {
            echarts: 'js'
        }
    });
    
    // 使用
    require(
        [
            'echarts',
            'echarts/chart/wordCloud' // 使用柱状图就加载bar模块，按需加载
        ],
        
            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(document.getElementById('echartsBlock')); 
                
                function createRandomItemStyle() {
                    return {
                        normal: {
                            color: 'rgb(' + [
                                Math.round(Math.random() * 160),
                                Math.round(Math.random() * 160),
                                Math.round(Math.random() * 160)
                            ].join(',') + ')'
                        }
                    };
                }

                
                var option = {
                			    title: {
                			        text: '${hotType }'
                			    },
                			    tooltip: {
                			        show: false
                			    },
                			    series: [{
                			        name: '${hotType }',
                			        type: 'wordCloud',
                			        size: ['100%', '100%'],
                			        textRotation : [0, 0, 0, 0],
                			        textPadding: 0,
                			        autoSize: {
                			            enable: true,
                			            minSize: 30,
                			        },
                			        data: [
                			               
									<c:forEach items="${hotwords }" var="elem">  
                			            {
                			                name: "${elem[0] }",
                			                value: ${elem[1]*100 },
                			                itemStyle: createRandomItemStyle()
                			            },
                				   </c:forEach>
                			        ]
                			    }]
                			};
        
                // 为echarts对象加载数据 
                myChart.setOption(option); 
                myChart.on('click', function (params) {
                    window.open('http://localhost:8080/CufeSearch/CoreServlet?method=query&queryString=' + encodeURIComponent(params.name)+'&currentPage=1');
                });
            }
        );
        
    </script>
</body>
</html>