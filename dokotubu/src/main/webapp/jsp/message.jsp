<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String ms = ( String )request.getAttribute( "ms" );
	if ( ms == null ) {
		ms = "";
	}
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>メッセージ</title>
		<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet" type="text/css">
	</head>

	<body>

		<div class="ms-page">
		
			<div class="ms">
				<%=ms %>
			</div>
			
			<div class="ms-blank"></div>
		
			<div class="to-l-button">
				<form action="<%=request.getContextPath() %>/jsp/login.jsp" method="get">
        			<input type="submit" value="ログイン画面へ">
        		</form>
        	</div>
        	
        </div>

	</body>

</html>