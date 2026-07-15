package dokotubu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TubuyakiDAO {

	
	public static List<Tubuyaki> findAll() {
		
		List<Tubuyaki> list = new ArrayList<Tubuyaki>();
		String sql = "SELECT * FROM tubuyaki ORDER BY postdate DESC";
		
		try (
				Connection con = DBManager.getConnection();
	            PreparedStatement pstmt = con.prepareStatement( sql );
	            ResultSet rs = pstmt.executeQuery()
		) {
				
			while ( rs.next() ) {
					
				Tubuyaki t = new Tubuyaki(	rs.getInt( "id" ),
														rs.getString( "userName" ),
														rs.getString( "body" ),
														rs.getTimestamp( "postdate" ) );
				list.add( t );
			}
			
		} catch ( Exception e ) {
			e.printStackTrace( System.out );
			throw new RuntimeException( e );
		}
		return list;
		
	}
	
	
	public static void insert( Tubuyaki t ) {
		
		String userName = t.getUserName();
		String body = t.getBody();
		
		String sql =	"INSERT INTO tubuyaki( userName, body ) " +
							"VALUES( ?,? )";
										// id INT AUTO_INCREMENT PRIMARY KEY,
										// postdate DEFAULT CURRENT_TIMESTAMP
										// → H2が自動で入れてくれるので渡す必要なし
		
		try (
			Connection con = DBManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement( sql );
		) {
			pstmt.setString( 1, userName );
			pstmt.setString( 2, body );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			e.printStackTrace( System.out );
			throw new RuntimeException( e );
		}
		
	}
	
	
	public static List<Tubuyaki> findByKeyword( String key ) {
		
		List<Tubuyaki> list = new ArrayList<Tubuyaki>();
		String sql =	"SELECT * FROM tubuyaki " + 
							"WHERE userName LIKE ? OR body LIKE ? " +
							"ORDER BY postdate DESC";
		String keyword = "%" + key + "%";
		
		try (
			Connection con = DBManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement( sql );
		) {
			
			pstmt.setString( 1, keyword );
			pstmt.setString( 2, keyword );
			
			try ( ResultSet rs = pstmt.executeQuery() ) {
				
				while ( rs.next() ) {
					
					Tubuyaki t = new Tubuyaki(	rs.getInt( "id" ),
															rs.getString( "userName" ),
															rs.getString( "body" ),
															rs.getTimestamp( "postdate" ) );
					list.add( t );
				}
			}
			
		} catch ( Exception e ) {
			e.printStackTrace( System.out );
			throw new RuntimeException( e );
		}
		return list;
		
	}
	
	
	public static void delete( int id ) {
		
		String sql =	"DELETE FROM tubuyaki WHERE id = ?";
		
		try (
			Connection con = DBManager.getConnection();
			PreparedStatement pstmt = con.prepareStatement( sql );
		) {
			pstmt.setInt( 1, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			e.printStackTrace( System.out );
			throw new RuntimeException( e );
		}
		
	}

}
