package dokotubu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	
	
	public static List<User> findAll() {
		
		List<User> list = new ArrayList<User>();
		String sql = "SELECT * FROM users";
		
		try (
			Connection con = DBManager.getConnection();
            PreparedStatement pstmt = con.prepareStatement( sql );
            ResultSet rs = pstmt.executeQuery()
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
			Connection con = DBManager.getConnection();
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
