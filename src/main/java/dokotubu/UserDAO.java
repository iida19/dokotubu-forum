package dokotubu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDAO {
	
	private static final String URL = "jdbc:h2:file:C:/pleiades/workspace/dokotubu/db/dokotubu";
	private static final String USER = "sa";
	private static final String PASSWORD = "";
	
	
	public static ArrayList<User> findAll() {
		
		ArrayList<User> list = new ArrayList<User>();
		String sql = "SELECT * FROM users";
		
		try (
			Connection con = DriverManager.getConnection( URL, USER, PASSWORD );
            PreparedStatement pSt = con.prepareStatement( sql );
            ResultSet rs = pSt.executeQuery()
		) {
			
			while ( rs.next() ) {
				
				User u = new User(	rs.getInt( "id" ),
												rs.getString( "userName" ),
												rs.getString( "password" ) );
				list.add( u );
			}
																	// close()いらない（tryの引数にすると閉じてくれる）
			
		} catch ( Exception e ) {
			e.printStackTrace( System.out );			// eqlipseでは統一してくれるので分からないが
																	// 標準出力に流す
			
			throw new RuntimeException( e );		// CLIアプリと違って"行き場がある"ため
																	// Servlet（呼び出し元）に例外を伝えるために
																	// エラーを投げ直さないといけない
		}
		return list;
	}
	
	
	public static void insert( User u ) {
		
		String userName = u.getUserName();
		String password = u.getPassword();
		
		String sql =	"INSERT INTO users( userName, password ) " +			// 最後に空白を！連結すると隙間がなくなる
							"VALUES( ?, ? )";
		
		try (
			Connection con = DriverManager.getConnection( URL, USER, PASSWORD );
			PreparedStatement pstmt = con.prepareStatement( sql );
		) {
			pstmt.setString( 1, userName );
			pstmt.setString( 2, password );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			e.printStackTrace( System.out );
			throw new RuntimeException( e );
		}
		
	}

}
