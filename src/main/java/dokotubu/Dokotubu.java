package dokotubu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
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
		
		String tubuyakiFile = getServletContext().getRealPath( "/WEB-INF/data/tubuyaki.csv" );
		List<Tubuyaki> searchTubuyakiList = new ArrayList<Tubuyaki>();
		int showSize = 10;
		Tubuyaki[] showList = new Tubuyaki[ showSize ];
		int showMenu = 0;
		
		readAndSearch( searchTubuyakiList, tubuyakiFile, key );
		cutTubuyaki( searchTubuyakiList, showList, showMenu );
		
		session = request.getSession();
		session.setAttribute( "showList", showList );
		session.setAttribute( "searchTubuyakiList", searchTubuyakiList );
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
		ServletContext application = this.getServletContext();
		
		String userName = ( String ) session.getAttribute( "userName" );
		
		String userFile = getServletContext().getRealPath( "/WEB-INF/data/users.csv" );
		String tubuyakiFile = getServletContext().getRealPath( "/WEB-INF/data/tubuyaki.csv" );
		
		int showSize = 10;
		Tubuyaki[] showList = new Tubuyaki[ showSize ];
		
		
		if ( userName == null ) {
			
			String action = request.getParameter( "action" );
			
			
			if ( action == null || action.isEmpty() ) {
				
				String un = request.getParameter( "userName" );
				String pw = request.getParameter( "password" );
			
				User u = new User( un, pw );
				UserLogic ul = new UserLogic( userFile );
				int status = ul.loginCheck( u );
					// 0でログイン成功、1は入力内容違い、2は空欄あり
			
			
				if ( status == 0 ) {
				
					List<Tubuyaki> tubuyakiList = new LinkedList<Tubuyaki>();
					int showMenu = 0;
				
					readDocument( tubuyakiList, tubuyakiFile );
					cutTubuyaki( tubuyakiList, showList, showMenu );
				
					session = request.getSession();
					session.setAttribute( "userName", u.getUserName() );
					session.setAttribute( "showList", showList );
					session.setAttribute( "showMenu", showMenu );
					application.setAttribute( "tubuyakiList", tubuyakiList );
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
				UserLogic ul = new UserLogic( userFile );
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
			List<Tubuyaki> tubuyakiList = ( List<Tubuyaki> )application.getAttribute( "tubuyakiList" );
			List<Tubuyaki> searchTubuyakiList = ( List<Tubuyaki> )session.getAttribute( "searchTubuyakiList" );
			Integer showMenu = ( Integer )session.getAttribute( "showMenu" );
			String viewStatus = ( String )request.getParameter( "viewStatus" );
			
			
			if ( ( "tweet" ).equals( action ) ) {
				
				String body = ( String ) request.getParameter( "body" );
				
				if ( body == null || body.isEmpty() ) {
					
					String errorMessage = "つぶやき内容が入力されていません！";
					request.setAttribute( "em", errorMessage );
					RequestDispatcher rd = request.getRequestDispatcher( "/jsp/main.jsp" );
					rd.forward( request, response );
					return;
					
				}
				
				int maxNumber = countTubuyaki( tubuyakiList );
				Tubuyaki t = new Tubuyaki( maxNumber+1, userName, body );
				tubuyakiList.add( t );
				writeDocument( tubuyakiList, tubuyakiFile );
				cutTubuyaki( tubuyakiList, showList, showMenu );
				
				application.setAttribute( "tubuyakiList", tubuyakiList );
				session.setAttribute( "showList", showList );
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
				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
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
				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
				
			} else if ( ( "delete" ).equals( action ) ) {
				
				String[] di = request.getParameterValues( "delete" );
				String targetJsp = null;
				
				if ( di == null ) {
					
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
				
				removeTubuyaki( tubuyakiList, searchTubuyakiList, di );
				writeDocument( tubuyakiList, tubuyakiFile );
				cutTubuyaki( tubuyakiList, showList, showMenu );
				
				application.setAttribute( "tubuyakiList", tubuyakiList );
				session.setAttribute( "showList", showList );
				
				if ( ( "all" ).equals( viewStatus ) ) {
					targetJsp = "/jsp/main.jsp";
				} else if ( ( "searching" ).equals( viewStatus ) ) {
					targetJsp = "/jsp/search.jsp";
				}
				
				RequestDispatcher rd = request.getRequestDispatcher( targetJsp );
				rd.forward( request, response );
				return;
				
			
			} else if ( ( "backAllList" ).equals( action ) ) {
				
				showMenu = 0;
				
				cutTubuyaki( tubuyakiList, showList, showMenu );
				
				session = request.getSession();
				session.setAttribute( "showList", showList );
				session.setAttribute( "showMenu", showMenu );
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
	
	
	
	public void readDocument( List<Tubuyaki> tubuyakiList, String tubuyakiFile ) {
		
		
		File fn = new File( tubuyakiFile );
		BufferedReader br = null;
		
		if ( fn != null ) {
			
			try {
			
				br = new BufferedReader( new FileReader( fn ) );
				
				String line;
				while ( ( line = br.readLine() ) != null ) {
					
					if ( line.contains( "," ) ) {
						String[] lines = line.split( "," );						
						int num = Integer.parseInt( lines[0] );					
						Tubuyaki t = new Tubuyaki( num, lines[1], lines[2] );
						tubuyakiList.add( t );
					}
					
				}
				
			} catch ( IOException e ) {
				e.printStackTrace();
				
			} finally {
				
				try {
					if ( br != null ) {
						br.close();
					}
				} catch ( IOException e ) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		
	}
	
	
	public void readAndSearch( List<Tubuyaki> searchTubuyakiList, String tubuyakiFile, String key ) {
		
		
		File fn = new File( tubuyakiFile );
		BufferedReader br = null;
		
		if ( fn != null ) {
			
			try {
			
				br = new BufferedReader( new FileReader( fn ) );
				
				String line;
				while ( ( line = br.readLine() ) != null ) {
					
					if ( line.contains( "," ) ) {
						
						String[] lines = line.split( "," );
						
						if ( lines[1].contains( key ) || lines[2].contains( key ) ) {
							int num = Integer.parseInt( lines[0] );
							Tubuyaki t = new Tubuyaki( num, lines[1], lines[2] );
							searchTubuyakiList.add( t );
						}	
					}
				}
				
			} catch ( IOException e ) {
				e.printStackTrace();
				
			} finally {
				
				try {
					if ( br != null ) {
						br.close();
					}
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	
	public void cutTubuyaki( List<Tubuyaki> tubuyakiList, Tubuyaki[] showList, int showMenu ) {
		
		if ( !tubuyakiList.isEmpty() ) {
			
			int i = 0;
			int start = tubuyakiList.size()-( showMenu*10+1 );
			int end = tubuyakiList.size()-( showMenu+1 )*10;
			while ( i < showList.length && start >= end && start >= 0 ) {
				
				if ( tubuyakiList.get( start ) != null ) {
					showList[ i ] = tubuyakiList.get( start );
					i ++;
					start --;
				} else {
					break;
				}
				
			}
		}	
		
	}
	
	
	public int countTubuyaki( List<Tubuyaki> tubuyakiList ) {
		
		int maxNumber = 0;
		
		if ( !tubuyakiList.isEmpty() ) {
			for ( Tubuyaki t : tubuyakiList ) {
				if ( t.getId() > maxNumber ) {
					maxNumber = t.getId();
				}
			}
		}	
		
		return maxNumber;
		
	}
	
	
	public void prevPage() {
		
		
		
	}
	
	
	public void removeTubuyaki( List<Tubuyaki> tubuyakiList, List<Tubuyaki> searchTubuyakiList, String[] di ) {
		
		
		for ( String s : di ) {
			
			int id = Integer.parseInt( s );
			
			// 全件リストからの削除
			for ( int i = 0; i < tubuyakiList.size(); i ++ ) {
				if ( id == tubuyakiList.get( i ).getId() ) {
					tubuyakiList.remove( i );
					break;
				}
			}
		
			// 検索結果リストからの削除
			if ( searchTubuyakiList != null && !searchTubuyakiList.isEmpty() ) {
				for ( int i = 0; i < searchTubuyakiList.size(); i ++ ) {
					if ( id == searchTubuyakiList.get( i ).getId() ) {
						searchTubuyakiList.remove( i );
						break;
					}	
				}	
			}
		
		
		}
	}
	
	
	public String makeTubuyakiIndex( Tubuyaki t ) {
		
		String s = String.valueOf( t.getId() );
		String k = s + "," + t.getUserName() + "," + t.getBody();
		return k;
		
	}
	
	
	public void writeDocument( List<Tubuyaki> tubuyakiList, String tubuyakiFile ) {
		
		
		File file = new File( tubuyakiFile );
		file.getParentFile().mkdirs();

		PrintWriter pw = null;
		
		try {
			
			pw = new PrintWriter( new FileWriter( file ) );
			
			for ( Tubuyaki t : tubuyakiList ) {
				String s = makeTubuyakiIndex( t );
				
				if ( s != null ) {
					pw.println( s );
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if ( pw != null ) {
				pw.close();
			}	
		}
		
		
	}


}
