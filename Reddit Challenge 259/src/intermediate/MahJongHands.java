package intermediate;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public class MahJongHands {
	private int numPieces;
	ArrayList<SingleTile> mahJongHand;
	
	ArrayList<TypeTile> standardTiles = new ArrayList<TypeTile>();
	ArrayList<TypeTile> honourTiles   = new ArrayList<TypeTile>();
	
	/**
	 * Simple constructor to set up the lists of standard vs honour tiles, this makes it 
	 * easier later on to detect one or the other
	 */
	public MahJongHands() {
		// Keep a list of each type of tile.
		// Standard tiles...
		standardTiles.add( TypeTile.BAMBOO    );
		standardTiles.add( TypeTile.CIRCLE    );
		standardTiles.add( TypeTile.CHARACTER );
		
		// Honour tiles...
		honourTiles.add( TypeTile.EAST  );
		honourTiles.add( TypeTile.WEST  );
		honourTiles.add( TypeTile.SOUTH );
		honourTiles.add( TypeTile.NORTH );
		honourTiles.add( TypeTile.GREEN );
		honourTiles.add( TypeTile.RED   );
		honourTiles.add( TypeTile.WHITE );
		
		/* Why not do this as two enum types with a common interface? well, enums implement 
		 * comparable, and trying to stick those together gave me problems, I feel this way is simpler. */
	}
	
	/**
	 * Enum type that implements all the different tiles defined by the challenge.
	 * 
	 * For the honour tiles, I'm implementing the first word here, then implementing its second word as the tileValue
	 */
	enum TypeTile { 
		CIRCLE    ("CIRCLE"   )
	  , BAMBOO    ("BAMBOO"   )
	  , CHARACTER ("CHARACTER")
	  , GREEN     ("GREEN"    )
	  , RED       ("RED"      )
	  , WHITE     ("WHITE"    )
	  , NORTH     ("NORTH"    )
	  , EAST      ("EAST"     )
	  , SOUTH     ("SOUTH"    )
	  , WEST      ("WEST"     ); 
		
		String typeString;
		
		private TypeTile( String typeStr ) {
			this.typeString = typeStr;
		}
		
		public String toString() {
			return this.typeString;
		}
	}
	
	/**
	 * SingleTile
	 * 
	 * Defines a tile face, and value so they are easier to work with.
	 * 
	 * Standard tiles are face + value:
	 *   CIRCLE 3
	 *   
	 * Honour tiles are face + word:
	 *   WHITE DRAGON
	 *   
	 *   With this there should be additional checks to make sure the face and word go together.
	 *   parseHonourTileValue() would be the place to do this.
	 * 
	 * @author Colin
	 *
	 */
	class SingleTile implements Comparable<SingleTile> {
		public TypeTile tileType;
		public int      tileValue;
		
		public SingleTile( TypeTile tt, int value ) {
			tileType  = tt;
			tileValue = value;
		}
		
		public String toString() {
			if( standardTiles.contains( this.tileType ) ) {
				return tileType.toString() + " : " + tileValue;
			}
			else {
				if      ( tileValue == 1 ) {
					return tileType.toString() + " DRAGON";
				}
				else if ( tileValue == 2 ) {
					return tileType.toString() + " WIND";
				}
				
				return tileType.toString() + " : null";
			}
		}

		@Override
		public int compareTo(SingleTile o) {
			// First compare on face value.
			int comp = tileType.compareTo( o.tileType );
			
			// Are these tiles the same face value?
			if ( comp == 0 ) {
				// Sort by tile face value
				comp = tileValue - o.tileValue;
			}
			
			return comp;
		}
		
		// Simple equals method to avoid object == object checking.
		public boolean equals( SingleTile o ) {
			if ( this.tileValue == o.tileValue &&
				 this.tileType.compareTo( o.tileType ) == 0 ) {
				return true;
			}
			
			return false;
		}
	}
	
	/**
	 * Remove sequences of the same face value tile.
	 * 
	 * @return int - the number of sequences removed from the hand.
	 */
	private int removeSequences () {
		int returnValue = 0;
		ArrayList<SingleTile> handCopy = new ArrayList<SingleTile>( );

		handCopy.addAll( mahJongHand );
		
		while( handCopy.size() >= 3) {
			if( standardTiles.contains( handCopy.get(1).tileType ) ) {			
				if( handCopy.get(1).tileValue -      handCopy.get(0).tileValue == 1 && 
					handCopy.get(1).tileType.equals( handCopy.get(0).tileType )	) {
					if( handCopy.get(2).tileValue -      handCopy.get(1).tileValue == 1 && 
						handCopy.get(2).tileType.equals( handCopy.get(1).tileType )	) {
							// Remove the object references from the main hand.
							mahJongHand.remove( handCopy.get(0) );
							mahJongHand.remove( handCopy.get(1) );
							mahJongHand.remove( handCopy.get(2) );
							
							// Remove them from our hand copy, remove two here, and let the end of this iteration remove the third.
							handCopy.remove(0);
							handCopy.remove(0);
							
							returnValue++;
					}
				}
			}

			handCopy.remove(0);
		}
		
		return returnValue;
	}
	
	/** 
	 * Remove pairs with same face and value 
	 * 
	 * @return int - the number of pairs removed from the hand.
	 */
	private int removePairs () {
		int returnValue = 0;
		
		for ( int i = 1; i < mahJongHand.size(); i++ ) {
			// Compare this one with the next one.
			if( mahJongHand.get( i ).equals( mahJongHand.get( i - 1 ) ) ) {
				// Remove them
				mahJongHand.remove( i );
				mahJongHand.remove( i - 1 );
			
				returnValue++;
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Remove sets of three with same face and value.
	 * 
	 * @return int - the number of sets removed from the hand.
	 */
	private int removeSets () {
		int returnValue = 0;
		
		for ( int i = 2; i < mahJongHand.size(); i++ ) {
			// Compare this one with the next one.
			if( mahJongHand.get( i     ).equals( mahJongHand.get( i - 1 ) ) && 
				mahJongHand.get( i - 1 ).equals( mahJongHand.get( i - 2 ) ) ) {
				// Remove them
				mahJongHand.remove( i );
				mahJongHand.remove( i - 1 );
				mahJongHand.remove( i - 2 );
			
				i--;
				returnValue++;
				
//				System.out.println( mahJongHand.toString() );
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Remove sets of three with same face and value.
	 * 
	 * @return int - the number of quads removed from the hand.
	 */
	private int removeQuads () {
		int returnValue = 0;
		
		for ( int i = 3; i < mahJongHand.size(); i++ ) {
			// Compare this one with the next one.
			if( mahJongHand.get( i     ).equals( mahJongHand.get( i - 1 ) ) && 
				mahJongHand.get( i - 1 ).equals( mahJongHand.get( i - 2 ) ) && 
				mahJongHand.get( i - 2 ).equals( mahJongHand.get( i - 3 ) )) {
				// Remove them
				mahJongHand.remove( i );
				mahJongHand.remove( i - 1 );
				mahJongHand.remove( i - 2 );
				mahJongHand.remove( i - 3 );
			
				i--;
				returnValue++;
				
//				System.out.println( mahJongHand.toString() );
			}
		}
		
		return returnValue;
	}
	
	/**
	 * This method is used to create the rest of the face for the honour tiles, these values 
	 * are really just a lookup for the full name of the tile.
	 * 
	 * @param honourTileValue
	 * @return
	 */
	private int parseHonourTileValue ( String honourTileValue ) {
		if      ( honourTileValue.equalsIgnoreCase( "DRAGON" ) ) {
			return 1;
		}
		else if ( honourTileValue.equalsIgnoreCase( "WIND" ) ) {
			return 2;
		}
		return -1;
	}
	
	/**
	 * Loads the challenge input... 
	 * 
	 * @param fileLoad - String containing the name of the file to load
	 * @return
	 */
	private boolean loadFile( String fileLoad ) {
		File          file             = new File( fileLoad );
		Charset       charset          = Charset.forName("US-ASCII");
		int           fileLine         = 0;
		boolean       foundMatch;
		
		mahJongHand = new ArrayList<SingleTile>();
		
		// Try resource block, which will invoke the close() method when exiting the block ( which is nice )
		try ( BufferedReader reader = Files.newBufferedReader(file.toPath(), charset ) ) 
		{ 
		    String line = null;
		    
		    // More checking could be done in here, but for this simple example this should suffice.
		    while ((line = reader.readLine()) != null) {
		    	foundMatch = false;
		    	
		    	if ( fileLine == 0 ) {
		    		// Looking for a single integer right here.
		    		numPieces = Integer.parseInt( line );
		    	}
		    	else {
		    		// Each of the following lines contains a <single word>,<value> pair
		    		String[] splitString = line.split("[, ]");
		    		
		    		/*System.out.println( splitString[0] + " : " + splitString[1] );*/
		    		
		    		// Iterate through the standard tiles looking for a match.
		    		for( TypeTile tt : standardTiles ) {
		    			if( tt.typeString.equalsIgnoreCase( splitString[0] )) {
		    				mahJongHand.add( new SingleTile( tt, Integer.parseInt(splitString[1] )));
		    				foundMatch = true;
		    			}
		    		}

		    		// Only if we didn't find a match above...
		    		if ( !foundMatch ) {
		    			// now Iterate through the honour tiles looking for a match.
			    		for( TypeTile tt : honourTiles ) {
			    			if( tt.typeString.equalsIgnoreCase( splitString[0] )) {
			    				mahJongHand.add( new SingleTile( tt, parseHonourTileValue(splitString[1] )));
			    			}
			    		}		    		
		    		}
		    	}
		    	fileLine++;
		    }
		    
		    return true;
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
		}
		return false;
	}	
	
	/**
	 * process
	 * Process the file given to find it its a winning hand or not.
	 * 
	 * @param filename
	 * @return boolean - true = winning hand.
	 */
	public boolean process( String filename ) {
		if ( loadFile( filename ) ) {
			Collections.sort( mahJongHand );
			
			System.out.println("--- For input " + filename + " ---" );
			
/*			for( SingleTile st : mahJongHand ) {
				System.out.println( st.tileType.toString() + " : " + st.tileValue );
			}*/
			
			System.out.println( mahJongHand.toString() );

			int quadsRemoved = removeQuads();
			System.out.println( "Quads    : " + quadsRemoved);
			
			int setsRemoved  = removeSets();
			System.out.println( "Sets     : " + setsRemoved );
			
			int seqsRemoved  = removeSequences();
			System.out.println( "Sequences: " + seqsRemoved );		
			
			int pairsRemoved = removePairs();
			System.out.println( "Pairs    : " + pairsRemoved);		
			
//			System.out.println( mahJongHand.toString() );
			
			if( mahJongHand.size() == 0 ) {
				if ( quadsRemoved == 0 ) {
					System.out.println( "Winning Hand!" );
					return true;
				}
				else if( quadsRemoved >  0 && 
					     pairsRemoved == 1 ) {
					System.out.println( "Winning Hand!" );
					return true;
				}
				else  {
					System.out.println( "Not a winning hand." );
				}					
				
			}
			else {
/*				for( SingleTile st : mahJongHand ) {
					System.out.println( st.tileType.toString() + " : " + st.tileValue );
				}*/
				System.out.println( "Not a winning hand." );
			}
		};
		
		return false;		
	}
	
	public static void main(String[] args) {
		MahJongHands mjh = new MahJongHands();
		
		mjh.process( args[0] );
	}

}
