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
		<title>どこつぶ新規登録</title>
		<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet" type="text/css">
	</head>

	<body>
		
		<div class="rg-page">
		
			<div class="ms">すでに使われているユーザー名は使用できません。</div>
		
			<div class="rg-area">
				<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
					<input type="hidden" name="action" value="register">
				
					<div class="rg-tbox">
						ユーザー名：<input type="text" name="userName" value="<%=userName %>">
					</div>
					<div class="rg-tbox">
						パスワード：<input type="password" name="password">
					</div>
				
					<input type="submit" value="登録">
				</form>
			</div>
		
			<div class="em">
				<%=em %>
			</div>
		
		</div>
		
		<div class="to-l-button">
			<form action="<%=request.getContextPath() %>/jsp/login.jsp" method="get">
        		<input type="submit" value="ログイン画面へ">
        	</form>
        </div>

	</body>

</html>