<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String userName = ( String ) session.getAttribute( "userName" );
	if ( userName == null ) {
		response.sendRedirect( request.getContextPath() + "/jsp/login.jsp" );
		return;
	}
%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="dokotubu.Tubuyaki" %>
<%
	Tubuyaki[] showList = ( Tubuyaki[] )session.getAttribute( "showList" );
	Integer showMenu = ( Integer )session.getAttribute( "showMenu" );
	boolean hasNext = ( boolean )session.getAttribute( "hasNext" );
%>
<%
	String em = ( String )request.getAttribute( "em" );
	if ( em == null ) {
		em = "";
	}
%>
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>メイン画面</title>
		<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet" type="text/css">
	</head>

	<body>
	
		<div class="title">どこつぶ掲示板</div>

		<div class="lgst-area">

			<div class="lg-status">
				<%=userName %>さん　ログイン中
			</div>
		
			<div class="lg-status">
				<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
					<button type="submit" name="action" value="logout">ログアウト</button>
				</form>
			</div>
		
		</div>
		
		<div class="text-area">
			
			<div class="tweet-area">
				<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
					<input type="text" name="body">
					<button type="submit" name="action" value="tweet">つぶやく</button>		
				</form>
			</div>	
		
			<div class="search-area">
			
				<form action="<%=request.getContextPath() %>/Dokotubu" method="get">
					<input type="hidden" name="viewStatus" value="all">
					<div class="sk-box">
						キーワード：<input type="text" name="key">
					</div>
					<div class="s-button">
						<input type="submit" value="検索">
					</div>	
				</form>
				
				<div class="a-button">
					<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
						<button type="submit" name="action" value="backAllList">一覧に戻る</button>	
					</form>
				</div>
				
			</div>
		
		</div>
		
		<div class="em">
			<%=em %>
		</div>	
		
		<% if ( showList == null || showList[0] == null ) { %>
			<% if ( em.isEmpty() ) { %>
					<div class="ms">
						まだつぶやきはありません。何かつぶやいてみましょう！
					</div>
				<% }%>		
				
		<% } else {%>
		
				<div class="main-area">
				
					<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
						<input type="hidden" name="viewStatus" value="all">
						
					<table>
				
						<% for ( Tubuyaki t : showList ) {%>
							<% if ( t != null ) { %>	
									<tr>
										<td>
											<% if ( ( t.getUserName() ).equals( userName ) ) {%>
													<div class="my-tweet">
														<%=t.getUserName() %>
													</div>
											<% } else { %>
													<%=t.getUserName() %>
											<% } %>
										</td>
										<td>
											<% if ( ( t.getUserName() ).equals( userName ) ) {%>
													<div class="my-tweet">
														<%=t.getBody() %>
													</div>
											<% } else { %>
													<%=t.getBody() %>
											<% } %>
										</td>
										<td>
											<% if ( ( t.getUserName() ).equals( userName ) ) {%>
													<input type="checkbox" name="delete" value="<%=t.getId() %>">
											<% } else { %>
													<input type="checkbox" disabled>
											<% } %>		
										</td>	
									</tr>
							<% } %>			
						<% } %>
					
					</table>
				
						<div class="d-button">
							<button type="submit" name="action" value="delete">削除</button>
						</div>
							
					</form>
				
			<% } %>
		
			<div class="pb-area">
			
				<% if ( showMenu > 0 ) { %>
					<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
						<input type="hidden" name="viewStatus" value="all">
						<button type="submit" name="action" value="prevPage">前へ</button>		
					</form>
				<% } %>
		
				<% if ( hasNext ) { %>
					<form action="<%=request.getContextPath() %>/Dokotubu" method="post">
						<input type="hidden" name="viewStatus" value="all">
						<button type="submit" name="action" value="nextPage">次へ</button>		
					</form>
				<% } %>
			
			</div>

		</div>
		
	</body>

</html>