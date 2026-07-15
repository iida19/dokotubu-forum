package dokotubu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DokotubuListener implements ServletContextListener {
	
	public void contextInitialized( ServletContextEvent sce ) {

	    try {
	    	
	        Class.forName( "org.h2.Driver" );
	        
	        String path = sce.getServletContext().getRealPath( "/WEB-INF/db/dokotubu" );
	        String url = "jdbc:h2:file:" + path;
	        DBManager.setUrl( url );

	        try (
	            Connection con = DBManager.getConnection();
	            Statement st = con.createStatement()
	        ) {
	            st.execute("""
	                CREATE TABLE IF NOT EXISTS users(
	                    id INT AUTO_INCREMENT PRIMARY KEY,
	                    userName VARCHAR(50),
	                    password VARCHAR(50)
	                )
	            """);

	            st.execute("""
	                CREATE TABLE IF NOT EXISTS tubuyaki(
	                    id INT AUTO_INCREMENT PRIMARY KEY,
	                    userName VARCHAR(50),
	                    body VARCHAR(280),
	                    postdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	                )
	            """);

	            initUsers( con );
	            initTubuyaki( con );
	        }

	    } catch ( Exception e ) {
	        e.printStackTrace();
	        throw new RuntimeException( e );
	    }
	}
	
	private void initUsers( Connection con ) throws Exception {

	    String countSql = "SELECT COUNT(*) FROM users";

	    try (
	        PreparedStatement pstmt = con.prepareStatement( countSql );
	        ResultSet rs = pstmt.executeQuery()
	    ) {
	        rs.next();
	        int count = rs.getInt(1);

	        if ( count == 0 ) {
	        	
	        	String setSampleSql =	"INSERT INTO users( userName, password ) " +
	        										"VALUES( ?, ? )";
	            
	        	try (
	        		PreparedStatement pstmt2 = con.prepareStatement( setSampleSql );
	        	) {
	        	
	        		List<User> sampleList = List.of(
	        			new User( "アオイ", "1234" ),
	        			new User( "ミナト", "1234" ),
	        			new User( "ユズ", "1234" )
	        			);
	        	
	        		for ( User u : sampleList ) {
	        			pstmt2.setString( 1, u.getUserName() );
	        			pstmt2.setString( 2, u.getPassword() );
	        			pstmt2.executeUpdate();
	        		}
	        	
	        	}
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    }
	}
	
	private void initTubuyaki( Connection con ) throws Exception {

	    String countSql = "SELECT COUNT(*) FROM tubuyaki";

	    try (
	        PreparedStatement pstmt = con.prepareStatement( countSql );
	        ResultSet rs = pstmt.executeQuery()
	    ) {
	        rs.next();
	        int count = rs.getInt(1);

	        if ( count == 0 ) {
	        	
	        	String setSampleSql =	"INSERT INTO tubuyaki( userName, body ) " +
													"VALUES( ?, ? )";

	        	try (
	        		PreparedStatement pstmt2 = con.prepareStatement( setSampleSql );
	        	) {
	            
	        		List<Tubuyaki> sampleList = List.of(
	        				new Tubuyaki( "アオイ", "どこつぶ一行掲示板のサンプルの書き込みです" ),
	        				new Tubuyaki( "ミナト", "どこつぶ掲示板はデータベースに対応中です" ),
	        				new Tubuyaki( "ユズ", "CSVファイルの読み込み・書き込みから移行中です" ),
	        				new Tubuyaki( "アオイ", "どこつぶ掲示板のコードをリファクタリング中です" ),
	        				new Tubuyaki( "ミナト", "皆も一緒に、レッツどこつぶ！" ),
	        				new Tubuyaki( "ユズ", "DAOを取り入れています" ),
	        				new Tubuyaki( "アオイ", "背景はどこつぶ掲示板の雰囲気に合うドット絵を描いてみました" ),
	        				new Tubuyaki( "ミナト", "ログイン・ログアウト・ユーザー新規登録ができます" ),
	        				new Tubuyaki( "ユズ", "CSSを使って平成感のあるどこつぶ掲示板にしています" ),
	        				new Tubuyaki( "アオイ", "どこつぶ掲示板は10件ごとのページ送りができます" ),
	        				new Tubuyaki( "ミナト", "キーワードを入力してどこつぶ内容を検索することもできます" ),
	        				new Tubuyaki( "ユズ", "メソッドやクラスで分けるとコードがスッキリします～" ),
	        				new Tubuyaki( "アオイ", "右のチェックボックスからどこつぶを削除することもできます" ),
	        				new Tubuyaki( "ミナト", "複数のつぶやきの同時削除もできますよ！" ),
	        				new Tubuyaki( "ユズ", "どこつぶ掲示板はのほほんとした雰囲気の一行掲示板です" ),
	        				new Tubuyaki( "アオイ", "エラーメッセージが出てもレイアウトがズレません" ),
	        				new Tubuyaki( "ミナト", "どこつぶで気軽にコミュニケーションしましょう" ),
	        				new Tubuyaki( "ユズ", "どこつぶ一行掲示板のサンプル書き込み、完了します" )
	        				);
	        	
	        		for ( Tubuyaki t : sampleList ) {
	        			pstmt2.setString( 1, t.getUserName() );
	        			pstmt2.setString( 2, t.getBody() );
	        			pstmt2.executeUpdate();
	        		}
	        		
	        	}
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    } 
	}
}
