/***
 * This application is designed to compute legal reversi moves from a file, and mark them with an '*'
 * 
 * Once done it will display the computed moves in its original grid format.
 * 
 * The main method does this for three files, the three located within this project.
 *
 * @author Colin
 */
package intermediate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;

public class Reversi {
	// All loaded from the properties file.
	private int  REVERSICOLUMNS;
	private int  REVERSIROWS;
	private char MOVEMARKER;
	private char BLANKTILE;

	
	private char[][] reversiBoard = null;
	private char     targetPiece;
	private int      legalMoves;
	
	public Reversi() {
		getProperties( "reversi.properties" );
	}
	
	/**
	 * Loads the challenge input into the character array reversiBoard ready for processing. 
	 * 
	 * @param fileLoad - String containing the name of the file to load
	 * @return
	 */
	private boolean loadFile( String fileLoad ) {
		File          file             = new File( fileLoad );
		Charset       charset          = Charset.forName("US-ASCII");
		int           fileLine         = 0;
		int           reversiLineCount = 0;
		
		reversiBoard = new char[REVERSIROWS][];
		
		// Try resource block, which will invoke the close() method when exiting the block ( which is nice )
		try ( BufferedReader reader = Files.newBufferedReader(file.toPath(), charset ) ) 
		{ 
		    String line = null;
		    
		    // More checking could be done in here, but for this simple example this should suffice.
		    while ((line = reader.readLine()) != null) {
		    	if ( fileLine == 0 ) {
		    		targetPiece = line.charAt(0);
		    	}
		    	else if ( reversiLineCount < REVERSIROWS ) {
		    		if( line.length() <= REVERSICOLUMNS )
		    		{
			    		reversiBoard[ reversiLineCount ] = line.toCharArray();
			    		reversiLineCount++;
		    		}
		    	}
		    	fileLine++;
		    }

		    if ( reversiLineCount == REVERSIROWS )
		    	return true;
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
		}
		return false;
	}
	
	/**
	 * Method to load all properties from the properties file.
	 * 
	 * @param filename - String, name of the properties file.
	 * @return
	 */
	private boolean getProperties( String filename) {
		try {
			FileInputStream fis = new FileInputStream( filename );
			Properties      p   = new Properties();
			
			p.load( fis );
			
			// p.list( System.out );
			REVERSICOLUMNS = Integer.parseInt( p.getProperty("REVERSI.COLUMNS") );
			REVERSIROWS    = Integer.parseInt( p.getProperty("REVERSI.ROWS") );
			MOVEMARKER     = p.getProperty("REVERSI.MOVEMARKER").charAt(0);
			BLANKTILE      = p.getProperty("REVERSI.BLANKTILE" ).charAt(0);
			
			fis.close();
			
			return true;
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
		}
		
		return false;
	}
	
	/**
	 * Simple method to display the completed reversi board
	 */
	private void displayCompletedReversiBoard() {
		System.out.println( legalMoves + " legal moves for " + targetPiece );
		
		for( int i = 0; i < REVERSIROWS; i++ ) {
			for( int j = 0; j < REVERSICOLUMNS; j++ ) {
				System.out.print( reversiBoard[i][j]);
			}
			System.out.print( '\n' );
		}
	}

	/**
	 * Enum class used to compute direction
	 * There are 8 directions to go out from, makes iterating through directions a bit nicer.
	 * 
	 * Also used to compute the next X and Y coordinates from an instance of this enum. 
	 *
	 */
	private enum moveDirection {
		LEFT      ( -1,  0 )
	  ,	RIGHT     (  1,  0 )
	  ,	UP        (  0, -1 )
	  ,	DOWN      (  0,  1 )
	  ,	LEFTUP    ( -1, -1 )
	  ,	RIGHTUP   (  1, -1 )
	  ,	LEFTDOWN  ( -1,  1 )
	  ,	RIGHTDOWN (  1,  1 );
	  
	  private int deltaX;
	  private int deltaY;
	  
	  private moveDirection( int dx, int dy ) {
		// TODO Auto-generated constructor stub
		deltaX = dx;
		deltaY = dy;
	  }
	  
	  public int getNextX( int x ) {
		  return x + deltaX;
	  }
	  
	  public int getNextY( int y ) {
		  return y + deltaY;
	  }
	}	
	
	/**
	 * Iterates though the pieces in the required direction
	 * 
	 * @param x         - x coordinate adjacent to the target piece
	 * @param y         - y coordinate adjacent to the target piece
	 * @param direction - Instance of the moveDirection enum class
	 */
	private void checkPiece( int x, int y, moveDirection direction ) {
		int nx = x
		  , ny = y;
		
		boolean foundOtherPiece = false;
	
		// Loop around till we are no longer in the bounds of the board.
		while( ( nx >= 0 && nx < REVERSIROWS    ) &&
			   ( ny >= 0 && ny < REVERSICOLUMNS ) ) {
			
			if ( reversiBoard[nx][ny] == MOVEMARKER  || 
				 reversiBoard[nx][ny] == targetPiece ) {
				break;
			}
			else if ( reversiBoard[nx][ny] == BLANKTILE ) {
				//When a blank tile is found check to be sure the other players tile was encountered before marking the board.
				if( foundOtherPiece ) {
					reversiBoard[nx][ny] = MOVEMARKER;
					legalMoves++;
				}
				break;
			}
			else {
				foundOtherPiece = true;
			}
			
			nx = direction.getNextX( nx );
			ny = direction.getNextY( ny );
		}
	}
	
	/**
	 * This method iterates through all the movement directions for the target piece.
	 * 
	 * @param x - target pieces x coordinate
	 * @param y - target pieces y coordinate
	 */
	private void findMoves ( final int x, final int y ) {
		int nx
		  , ny;
		
		for( moveDirection md :  moveDirection.values() ) {
			// Generate the position.
			nx = md.getNextX( x );
			ny = md.getNextY( y );
			
			// Check the location of nx, ny is ok.
			if( ( nx >= 0 && nx < REVERSIROWS    ) &&
				( ny >= 0 && ny < REVERSICOLUMNS ) )
			{
				checkPiece( nx, ny, md );
			}
		}
	}
	
	/**
	 * This method loads in the reversi board file, computes all legal moves the player can make, then outputs them to STDOUT.
	 * 
	 * @param fileIn - Name of the file to load in.
	 */
	public void findLegalMoves( String fileIn ) {
		legalMoves = 0;
		
		if( loadFile( fileIn ) ) {
//			displayReversiBoard();
			
			for( int i = 0; i < REVERSIROWS; i++ ) {
				for( int j = 0; j < REVERSICOLUMNS; j++ ) {
					if( reversiBoard[i][j] == targetPiece ) {
						findMoves( i,j );
					}
				}
			}
			
			displayCompletedReversiBoard();
		}
	}
	
	public static void main(String[] args) {
		Reversi rev = new Reversi();
		
		long startTime = System.nanoTime();
		
		rev.findLegalMoves("reversiInputBoard1");
		rev.findLegalMoves("reversiInputBoard2");
		rev.findLegalMoves("reversiInputBoard3");
		
		long endTime = System.nanoTime();
		
		System.out.println( "Time taken " + (endTime - startTime ) / 1e6 + " ms");
	}

}
