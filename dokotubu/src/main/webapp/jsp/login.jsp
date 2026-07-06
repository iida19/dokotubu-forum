<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String em = ( String )request.getAttribute( "em" );
	if ( em == null ) {
		em = "";
	}
%>
<%
	String userName = ( String )request.getAttribute( "userName" );
	if ( userName == null ) {
		userName = "";
	}
%>
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>どこつぶログイン</title>
		<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet" type="text/css">
	</head>

	<body>
	
		<div class="title">どこつぶ掲示板へようこそ</div>
		
		<div class="lg-area">
			<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
				<div class="lg-tbox">
					ユーザー名：<input type="text" name="userName" value="<%=userName %>">
				</div>
				<div class="lg-tbox">	
					パスワード：<input type="password" name="password">
				</div>	
				<input type="submit" value="ログイン">
			</form>
		</div>
		
		<div class="em">
			<%=em %>
		</div>	
		
		<div class="rg-button">
			<form action="<%=request.getContextPath() %>/jsp/register.jsp" method="get">
				<input type="submit" value="新規登録">
			</form>
		</div>
		
	</body>
	
</html>