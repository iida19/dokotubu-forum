package dokotubu;

import java.util.List;

public class TubuyakiLogic {
	
	public TubuyakiLogic() {}
	
	
	public PageData makeDefaultPage() {
		
		List<Tubuyaki> tubuyakiList = TubuyakiDAO.findAll();
		int showMenu = 0;
		
		PageData pd = createPageData( tubuyakiList, showMenu, null );
	
		return pd;
		
	}
	
	public PageData makeTweetedPage( String userName, String body ) {
		
		Tubuyaki t = new Tubuyaki( userName, body );
		TubuyakiDAO.insert( t );
		List<Tubuyaki> tubuyakiList = TubuyakiDAO.findAll();
		int showMenu = 0;
		
		PageData pd = createPageData( tubuyakiList, showMenu, null );
	
		return pd;
		
	}
	
	public PageData makeSearchedPage( String key ) {
		
		List<Tubuyaki> searchTubuyakiList = TubuyakiDAO.findByKeyword( key );
		int showMenu = 0;
		
		PageData pd = createPageData( searchTubuyakiList, showMenu, key );
	
		return pd;
		
	}
	
	public PageData makePrevPage( PageData p, String viewStatus ) {
		
		int showMenu = p.getShowMenu();
		String key = p.getKey();
		
		if ( showMenu > 0 ) {
			showMenu --;
		}
		
		List<Tubuyaki> targetList = this.getTargetList( viewStatus, key );
		
		PageData pd = createPageData( targetList, showMenu, key );
		
		return pd;
		
	}
	
	public PageData makeNextPage( PageData p, String viewStatus ) {
		
		int showMenu = p.getShowMenu();
		String key = p.getKey();
		
		List<Tubuyaki> targetList = this.getTargetList( viewStatus, key );
		boolean hasNext = this.hasNext( targetList, showMenu );
		
		if ( hasNext ) {
			showMenu ++;
		}
		
		PageData pd = createPageData( targetList, showMenu, key );
		
		return pd;
		
	}
	
	public PageData makeDeletedPage( PageData p, String[] deleteId, String viewStatus ) {
		
		int showMenu = p.getShowMenu();
		String key = p.getKey();
		
		removeFromDB( deleteId );
		List<Tubuyaki> targetList = this.getTargetList( viewStatus, key );
		
		if ( showMenu > 0 && targetList.size() <= showMenu*10 ) {
			showMenu --;
		}
		
		PageData pd = createPageData( targetList, showMenu, key );
		
		return pd;
		
	}
	
	
	private Tubuyaki[] cutTubuyaki( List<Tubuyaki> tubuyakiList, int showMenu ) {
		
		int showSize = 10;
		Tubuyaki[] showList = new Tubuyaki[ showSize ];
		
		if ( !tubuyakiList.isEmpty() ) {
			
			int start = showMenu*10;
			for ( int i = 0; i < showList.length && start < tubuyakiList.size(); i ++ ) {
				
				if ( tubuyakiList.get( start ) != null ) {
					showList[ i ] = tubuyakiList.get( start );
					start ++;
				} else {
					break;
				}
			}
		}
		return showList;
		
	}
	
	private boolean hasNext( List<Tubuyaki> tubuyakiList, int showMenu ) {
		
		boolean hasNext = false;
		if ( !tubuyakiList.isEmpty() ) {
			hasNext = tubuyakiList.size()-showMenu*10 > 10;
		}
		
		return hasNext;
		
	}
	
	private List<Tubuyaki> getTargetList( String viewStatus, String key ) {
		
		List<Tubuyaki> targetList = null;
		
		if ( ( "searching" ).equals( viewStatus ) ) {
			targetList = TubuyakiDAO.findByKeyword( key );
		} else {
			targetList = TubuyakiDAO.findAll();	
		}
		
		return targetList;
		
	}
	
	private void removeFromDB( String[] deleteId ) {
		
		for ( String s : deleteId ) {
			
			int id = Integer.parseInt( s );
			TubuyakiDAO.delete( id );
			
		}
	}
	
		
	private PageData createPageData( List<Tubuyaki> targetList,  int showMenu, String key ) {
		
		PageData pd = new PageData();
		
		pd.setShowMenu( showMenu );
		pd.setShowList( this.cutTubuyaki( targetList, showMenu ) );
		pd.setHasNext( this.hasNext( targetList, showMenu ) );
		pd.setKey( key );
		
		return pd;
		
	}
}
