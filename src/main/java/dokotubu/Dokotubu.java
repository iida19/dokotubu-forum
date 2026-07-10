package dokotubu;

import java.io.IOException;
import java.util.List;

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
	    
	    
		// Getデータ受け取り
		String key = request.getParameter( "key" );
		String viewStatus = request.getParameter( "viewStatus" );
		
		String targetJsp = null;
		
		if ( key == null || key.isEmpty() ) {
			
			if ( ( "all" ).equals( viewStatus ) ) {
				targetJsp = "/jsp/main.jsp";
			} else if ( ( "searching" ).equals( viewStatus ) ) {
				targetJsp = "/jsp/search.jsp";
			}
			
			String errorMessage = "検索キーワードが入力されていません！";
			request.setAttribute( "em", errorMessage );
			RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
			rd.forward( request, response );
			return;
			
		}
		
		List<Tubuyaki> searchTubuyakiList = TubuyakiDAO.findByKeyword( key );
		int showSize = 10;
		Tubuyaki[] showList = new Tubuyaki[ showSize ];
		int showMenu = 0;
		
		cutTubuyaki( searchTubuyakiList, showList, showMenu );
		
		boolean hasNext = false;
		if ( !searchTubuyakiList.isEmpty() ) {
			hasNext = searchTubuyakiList.size()-showMenu*10 > 10;
		}
		
		session = request.getSession();
		session.setAttribute( "showList", showList );
		session.setAttribute( "hasNext", hasNext );
		session.setAttribute( "key", key );
		RequestDispatcher rd = request.getRequestDispatcher( "/jsp/search.jsp" );
		rd.forward( request, response );
		return;
		
		
	}


	@SuppressWarnings("unchecked")
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
		
		String userName = ( String ) session.getAttribute( "userName" );
		
		int showSize = 10;
		Tubuyaki[] showList = new Tubuyaki[ showSize ];
		
		
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
				
					List<Tubuyaki> tubuyakiList = TubuyakiDAO.findAll();
					int showMenu = 0;
					
					cutTubuyaki( tubuyakiList, showList, showMenu );
					
					boolean hasNext = false;
					if ( !tubuyakiList.isEmpty() ) {
						hasNext = tubuyakiList.size()-showMenu*10 > 10;
					}
				
					session = request.getSession();
					session.setAttribute( "userName", u.getUserName() );
					session.setAttribute( "showList", showList );
					session.setAttribute( "showMenu", showMenu );
					session.setAttribute( "hasNext", hasNext );
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
			List<Tubuyaki> tubuyakiList = TubuyakiDAO.findAll();
			Integer showMenu = ( Integer )session.getAttribute( "showMenu" );
			
			String viewStatus = ( String )request.getParameter( "viewStatus" );
			
			List<Tubuyaki> searchTubuyakiList = null;
			String key = ( String )session.getAttribute( "key" );
			if ( key != null ) {
				searchTubuyakiList = TubuyakiDAO.findByKeyword( key );
			}
			
			
			if ( ( "tweet" ).equals( action ) ) {
				
				String body = ( String ) request.getParameter( "body" );
				
				if ( body == null || body.isEmpty() ) {
					
					String errorMessage = "つぶやき内容が入力されていません！";
					request.setAttribute( "em", errorMessage );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/main.jsp" );
					rd.forward( request, response );
					return;
					
				}
				
				Tubuyaki t = new Tubuyaki( userName, body );
				TubuyakiDAO.insert( t );
				tubuyakiList = TubuyakiDAO.findAll();
				cutTubuyaki( tubuyakiList, showList, showMenu );
				
				boolean hasNext = false;
				if ( !tubuyakiList.isEmpty() ) {
					hasNext = tubuyakiList.size()-showMenu*10 > 10;
				}
				
				session.setAttribute( "showList", showList );
				session.setAttribute( "hasNext", hasNext );
				RequestDispatcher rd = request.getRequestDispatcher( "/jsp/main.jsp" );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "prevPage" ).equals( action ) ) {
				
				if ( showMenu > 0 ) {
					showMenu --;
				}
				
				List<Tubuyaki> targetList = null;
				String targetJsp = null;
				
				if ( ( "all" ).equals( viewStatus ) ) {
					
					targetList = tubuyakiList;
					targetJsp = "/jsp/main.jsp";
					
				} else if ( ( "searching" ).equals( viewStatus ) ) {
					
					targetList = searchTubuyakiList;
					targetJsp = "/jsp/search.jsp";
					
				}
				
				cutTubuyaki( targetList, showList, showMenu );
				
				boolean hasNext = false;
				if ( !tubuyakiList.isEmpty() ) {
					hasNext = tubuyakiList.size()-showMenu*10 > 10;
				}
				
				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
				session.setAttribute( "hasNext", hasNext );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "nextPage" ).equals( action ) ) {
			
				int nextOrNot = 0;
				List<Tubuyaki> targetList = null;
				String targetJsp = null;
				
				if ( ( "all" ).equals( viewStatus ) ) {
					
					nextOrNot = tubuyakiList.size()-showMenu*10;
					targetList = tubuyakiList;
					targetJsp = "/jsp/main.jsp";
					
				} else if ( ( "searching" ).equals( viewStatus ) ) {
					
					nextOrNot = searchTubuyakiList.size()-showMenu*10;
					targetList = searchTubuyakiList;
					targetJsp = "/jsp/search.jsp";
					
				}
				
				if ( nextOrNot > 10 ) {
					showMenu ++;
				}
				
				cutTubuyaki( targetList, showList, showMenu );
				
				boolean hasNext = false;
				if ( !tubuyakiList.isEmpty() ) {
					hasNext = tubuyakiList.size()-showMenu*10 > 10;
				}

				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
				session.setAttribute( "hasNext", hasNext );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "delete" ).equals( action ) ) {
				
				String[] deleteId = request.getParameterValues( "delete" );
				List<Tubuyaki> targetList = null;
				String targetJsp = null;
				
				if ( deleteId == null ) {
					
					if ( ( "all" ).equals( viewStatus ) ) {
						targetJsp = "/jsp/main.jsp";
					} else if ( ( "searching" ).equals( viewStatus ) ) {
						targetJsp = "/jsp/search.jsp";
					}
					
					String errorMessage = "削除するつぶやきが選択されていません！";
					request.setAttribute( "em", errorMessage );
					RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
					rd.forward( request, response );
					return;
					
				}
				
				removeFromDB( deleteId );
				
				if ( ( "all" ).equals( viewStatus ) ) {
					tubuyakiList = TubuyakiDAO.findAll();
					targetList = tubuyakiList;
					targetJsp = "/jsp/main.jsp";
				} else if ( ( "searching" ).equals( viewStatus ) ) {
					searchTubuyakiList = TubuyakiDAO.findByKeyword( key );
					targetList = searchTubuyakiList;
					targetJsp = "/jsp/search.jsp";
				}
				
				if ( showMenu > 0 && targetList.size() <= showMenu*10 ) {
					showMenu --;
					session.setAttribute( "showMenu", showMenu );
				}
				
				boolean hasNext = false;
				if ( !tubuyakiList.isEmpty() ) {
					hasNext = tubuyakiList.size()-showMenu*10 > 10;
				}
				
				cutTubuyaki( targetList, showList, showMenu );
				session.setAttribute( "showList", showList );
				session.setAttribute( "hasNext", hasNext );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
			
			} else if ( ( "backAllList" ).equals( action ) ) {
				
				showMenu = 0;
				
				cutTubuyaki( tubuyakiList, showList, showMenu );
				
				boolean hasNext = false;
				if ( !tubuyakiList.isEmpty() ) {
					hasNext = tubuyakiList.size()-showMenu*10 > 10;
				}
				
				session = request.getSession();
				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
				session.setAttribute( "hasNext", hasNext );
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

	
	
	public void cutTubuyaki( List<Tubuyaki> tubuyakiList, Tubuyaki[] showList, int showMenu ) {
		
		if ( !tubuyakiList.isEmpty() ) {
			
			int start = showMenu*10;
			for ( int i = 0; i < showList.length && start < tubuyakiList.size(); i ++ ) {
				
				if ( tubuyakiList.get( start ) != null ) {
					showList[ i ] = tubuyakiList.get( start );
					start ++;
				} else {
					break;
				}
			}
		}
	}
	
	
	public void removeFromDB( String[] deleteId ) {
		
		for ( String s : deleteId ) {
			
			int id = Integer.parseInt( s );
			TubuyakiDAO.delete( id );
			
		}
	}
	
	
}
