package dokotubu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DokotubuListener implements ServletContextListener {
	public void contextInitialized( ServletContextEvent sce ) {
		ServletContext application = sce.getServletContext();
		
		try {
		
            Class.forName( "org.h2.Driver" );

            Connection con = DriverManager.getConnection(
                    "jdbc:h2:file:C:/pleiades/workspace/dokotubu/db/dokotubu",
                    "sa",
                    "");

            Statement st = con.createStatement();
            
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
            
            st.close();
            con.close();
            
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
	}

}
