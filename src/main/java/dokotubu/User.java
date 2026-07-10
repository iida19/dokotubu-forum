package dokotubu;

import java.io.Serializable;

public class User implements Serializable {
	
	
	private int id;
	private String userName;
	private String password;
	
	public User() {}
	
	// 新規登録のタイミング（IDなし）
	public User( String userName, String password ) {
		
		this.setUserName( userName );
		this.setPassword( password );
		
	}
	
	// データベースから読み込んだタイミング
	public User( int id, String userName, String password ) {
		
		this.setId( id );
		this.setUserName( userName );
		this.setPassword( password );
		
	}
	
	
	public int getId() {
		return id;
	}
	public void setId( int id ) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName( String userName ) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword( String password ) {
		this.password = password;
	}
	
}
