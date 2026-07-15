package dokotubu;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/Dokotubu")
public class Dokotubu extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	//排他制御用 LOCKオブジェクト
	private static final Object LOCK = new Object();
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		System.out.println("GET開始");
		
		
		//====================================
		//キャッシュを禁止する
		//→ログアウト後に「戻る」ボタンが押されても再表示されない対策
		//====================================
	    response.setHeader( "Cache-Control","no-cache, no-store, must-revalidate" );
	    response.setHeader( "Pragma", "no-cache" );
	    response.setDateHeader( "Expires", 0 );
		
		
		HttpSession session = request.getSession();
		request.setCharacterEncoding( "UTF-8" );
		
	    String userName = ( String ) session.getAttribute( "userName" );
	    if ( userName == null ) {
	        response.sendRedirect( request.getContextPath() + "/jsp/login.jsp" );
	        return;
	    }
	    
	    
	    TubuyakiLogic tl = new TubuyakiLogic();
	    
		// Getデータ受け取り
		String key = request.getParameter( "key" );
		String viewStatus = request.getParameter( "viewStatus" );
		
		String targetJsp = "/jsp/search.jsp";
		
		if ( key == null || key.isEmpty() ) {
			
			if ( ( "all" ).equals( viewStatus ) ) {
				targetJsp = "/jsp/main.jsp";
			}
			
			String errorMessage = "検索キーワードが入力されていません！";
			request.setAttribute( "em", errorMessage );
			RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
			rd.forward( request, response );
			return;
			
		}
		
		PageData pd = tl.makeSearchedPage( key );
		
		session.setAttribute( "pageData", pd );
		RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
		rd.forward( request, response );
		return;
		
		
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		//====================================
		//キャッシュを禁止する
		//→ログアウト後に「戻る」ボタンが押されても再表示されない対策
		//====================================
	    response.setHeader( "Cache-Control","no-cache, no-store, must-revalidate" );
	    response.setHeader( "Pragma", "no-cache" );
	    response.setDateHeader( "Expires", 0 );
		
		
		HttpSession session = request.getSession();
		request.setCharacterEncoding( "UTF-8" );
		
		TubuyakiLogic tl = new TubuyakiLogic();
		
		String userName = ( String ) session.getAttribute( "userName" );
		
		
		if ( userName == null ) {
			
			String action = request.getParameter( "action" );
			
			
			if ( action == null || action.isEmpty() ) {
				
				String un = request.getParameter( "userName" );
				String pw = request.getParameter( "password" );
			
				User u = new User( un, pw );
				UserLogic ul = new UserLogic();
				int status = ul.loginCheck( u );
						// 0でログイン成功、1は入力内容違い、2は空欄あり
			
			
				if ( status == 0 ) {
				
					PageData pd = tl.makeDefaultPage();
				
					session.setAttribute( "userName", u.getUserName() );
					session.setAttribute( "pageData", pd );
					response.sendRedirect( request.getContextPath() + "/jsp/main.jsp" );
					return;
				
				} else if ( status == 1 ) {
				
					String errorMessage = "ユーザー名かパスワードが間違っています";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/login.jsp" );
					rd.forward( request, response );
					return;
			
				} else if ( status == 2 ) {
				
					String errorMessage = "入力されていない項目があるようです";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/login.jsp" );
					rd.forward( request, response );
					return;
		
				} else if ( status == -1 ) {
					
					System.out.println( "login statusが未判定です" );
					String errorMessage = "処理に問題が発生しました。すみませんがもう一度お試しください。";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/login.jsp" );
					rd.forward( request, response );
					return;
					
				}
			
				
			} else if ( ( "register" ).equals( action ) ) {
				
				String un = request.getParameter( "userName" );
				String pw = request.getParameter( "password" );
			
				User u = new User( un, pw );
				UserLogic ul = new UserLogic();
				int status = ul.registerUser( u );
						// 0で登録成功、1はユーザー名重複、2は空欄あり
				
				
				if ( status == 0 ) {
					
					String ms = "登録しました。トップページからログインをお願いします";
					request.setAttribute( "ms", ms );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/message.jsp" );
					rd.forward( request, response );
					return;
					
				} else if ( status == 1 ) {
					
					String errorMessage = "ユーザー名\"" + u.getUserName() + "\"はすでに使われています";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/register.jsp" );
					rd.forward( request, response );
					return;
					
				} else if ( status == 2 ) {
							
					String errorMessage = "入力されていない項目があるようです";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/register.jsp" );
					rd.forward( request, response );
					return;
				
				} else if ( status == -1 ) {
					
					System.out.println( "login statusが未判定です" );
					String errorMessage = "処理に問題が発生しました。すみませんがもう一度お試しください。";
					request.setAttribute( "em", errorMessage );
					request.setAttribute( "userName", u.getUserName() );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/register.jsp" );
					rd.forward( request, response );
					return;
					
				}
				
			}
			
			
		} else if ( !userName.isEmpty() ) {
			
			String action = ( String )request.getParameter( "action" );
			String viewStatus = ( String )request.getParameter( "viewStatus" );
			PageData p = ( PageData )session.getAttribute( "pageData" );
			
			
			if ( ( "tweet" ).equals( action ) ) {
				
				String body = ( String ) request.getParameter( "body" );
				
				if ( body == null || body.isEmpty() ) {
					
					String errorMessage = "つぶやき内容が入力されていません！";
					request.setAttribute( "em", errorMessage );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/main.jsp" );
					rd.forward( request, response );
					return;
					
				}
				
				PageData pd = tl.makeTweetedPage( userName, body );
				
				session.setAttribute( "pageData", pd );
				RequestDispatcher rd = request.getRequestDispatcher( "/jsp/main.jsp" );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "prevPage" ).equals( action ) ) {
				
				PageData pd = tl.makePrevPage( p, viewStatus );
				
				String targetJsp = this.getTargetJsp( viewStatus );
				
				session.setAttribute( "pageData", pd );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "nextPage" ).equals( action ) ) {
			
				PageData pd = tl.makeNextPage( p, viewStatus );
				
				String targetJsp = this.getTargetJsp( viewStatus );

				session.setAttribute( "pageData", pd );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "delete" ).equals( action ) ) {
				
				String[] deleteId = request.getParameterValues( "delete" );
				
				String targetJsp = this.getTargetJsp( viewStatus );
				
				if ( deleteId == null ) {
					
					String errorMessage = "削除するつぶやきが選択されていません！";
					request.setAttribute( "em", errorMessage );
					RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
					rd.forward( request, response );
					return;
					
				}
				
				PageData pd = tl.makeDeletedPage( p, deleteId, viewStatus );
				
				session.setAttribute( "pageData", pd );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
			
			} else if ( ( "backAllList" ).equals( action ) ) {
				
				PageData pd = tl.makeDefaultPage();
				
				session.setAttribute( "pageData", pd );
				response.sendRedirect( request.getContextPath() + "/jsp/main.jsp" );
				return;
				
				
			} else if ( ( "logout" ).equals( action ) ) {
				
				session.invalidate();
				String ms = "ログアウトしました";
				request.setAttribute( "ms", ms );
				RequestDispatcher rd = request.getRequestDispatcher( "/jsp/message.jsp" );
				rd.forward( request, response );
				return;
				
			}
			
			
		}
		
		
		System.out.println("doPost最後まで来た");
		
	}
	
	
	private String getTargetJsp( String viewStatus ) {
		
		String targetJsp;
		
		if ( ( "searching" ).equals( viewStatus ) ) {
			targetJsp = "/jsp/search.jsp";
		} else {
			targetJsp = "/jsp/main.jsp";
		}
		
		return targetJsp;
		
	}

	
}
