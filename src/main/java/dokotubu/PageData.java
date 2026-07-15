package dokotubu;

import java.io.Serializable;

public class PageData implements Serializable {
	
	private int showMenu;
	private Tubuyaki[] showList;
	private boolean hasNext;
	private String key;
	
	public PageData() {}
	

	public int getShowMenu() {
		return showMenu;
	}
	public void setShowMenu( int showMenu ) {
		this.showMenu = showMenu;
	}

	public Tubuyaki[] getShowList() {
		return showList;
	}
	public void setShowList( Tubuyaki[] showList ) {
		this.showList = showList;
	}

	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext( boolean hasNext ) {
		this.hasNext = hasNext;
	}

	public String getKey() {
		return key;
	}
	public void setKey( String key ) {
		this.key = key;
	}
	
	
	

}
