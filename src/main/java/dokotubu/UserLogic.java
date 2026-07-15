package dokotubu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class UserLogic {
	
	public UserLogic() {}
	
	
	public int loginCheck( User u ) {
		
		List<User> userList = UserDAO.findAll();
		
		String userName = u.getUserName();
		String password = u.getPassword();
		int status = -1;			// 0でログイン成功、1は入力内容違い、2は空欄あり
		
		if ( userName != null && !userName.isEmpty() && password != null && !password.isEmpty() ) {
			
			status = 1;
			for ( User us : userList ) {
				if ( userName.equals( us.getUserName() ) && password.equals( us.getPassword() ) ) {
					status = 0;
					break;
				}
			}
			
		} else {
			status = 2;
		}
		
		return status;
		
	}
	
	
	public int registerUser( User u ) {
		
		List<User> userList = UserDAO.findAll();
		
		String userName = u.getUserName();
		String password = u.getPassword();
		int status = -1;			// 0で登録成功、1はユーザー名重複、2は空欄あり
		boolean exists = false;
		
		if ( userName != null && !userName.isEmpty() && password != null && !password.isEmpty() ) {
		
			status = 1;
			for ( User us : userList ) {
				if ( userName.equals( us.getUserName() ) ) {
					exists = true;
					break;
				}
			}
			
			if ( exists == false ) {
				UserDAO.insert( u );
				status = 0;
			}	
			
		} else {
			status = 2;
		}
		
		return status;
		
	}
	
	
	public void readDocument( List<User> userList, String userFile ) {
		
		
		File fn = new File( userFile );
		BufferedReader br = null;
		
		if ( fn != null ) {
			
			try {
			
				br = new BufferedReader( new FileReader( fn ) );
				
				String line;
				while ( ( line = br.readLine() ) != null ) {
					
					if ( line.contains( "," ) ) {
						String[] lines = line.split( "," );
						User u = new User( lines[0], lines[1] );
						userList.add( u );
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
	
	
	public String makeUserIndex( User u ) {
		
		String s = u.getUserName() + "," + u.getPassword();
		return s;
		
	}
	
	
	public void writeDocument( List<User> userList, String userFile ) {
		
		
		File file = new File( userFile );
		file.getParentFile().mkdirs();

		PrintWriter pw = null;
		
		try {
			
			pw = new PrintWriter( new FileWriter( file ) );
			
			for ( User u : userList ) {
				String s = makeUserIndex( u );
				
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
