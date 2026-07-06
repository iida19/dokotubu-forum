package dokotubu;

import java.io.Serializable;

public class Tubuyaki implements Serializable {
	
	
	private int id;
	private String userName;
	private String body;
	
	
	public Tubuyaki() {}
	public Tubuyaki( int id, String userName, String body ) {
		
		this.setId( id );
		this.setUserName( userName );
		this.setBody( body );
		
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
	
	public String getBody() {
		return body;
	}
	public void setBody( String body ) {
		this.body = body;
	}
	
	
	

}
