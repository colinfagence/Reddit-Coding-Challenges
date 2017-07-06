package intermediate;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

public class MirrorEncryption {
	private static final int LETTERGRIDXSIZE = 15;
	private static final int LETTERGRIDYSIZE = 15;
	
	char[][] letterGrid        = null;
	char[][] overlayMirrors    = null;
	char[][] substitutionChars = null;
	
	String encryptedWord;
	
	/**
	 * constructLetterGrid
	 * 
	 * Constructs the grid layout of the alphabet into letterGrid ready for the mirrors to be overlaid.<br>
	 * <br>
	 * The output constructed within the letterGrid character array, will look like the following:<br>
	 * <br>
	 * <pre>
	 *  abcdefghijklm
     * A             n
     * B             o
     * C             p
     * D             q
     * E             r
     * F             s
     * G             t
     * H             u
     * I             v
     * J             w
     * K             x
     * L             y
     * M             z
     *  NOPQRSTUVWXYZ 
	 * </pre>
	 */
	private void constructLetterGrid() {
		int charLocation;
		
		String[] endsLetters = { "abcdefghijklm", "NOPQRSTUVWXYZ" };
		String[] sideLetters = { "ABCDEFGHIJKLM", "nopqrstuvwxyz" };
		
		letterGrid = new char[LETTERGRIDYSIZE][LETTERGRIDXSIZE];
		
		// First pass fill the array with space
		for( int i = 0; i < LETTERGRIDYSIZE; i++ ) {
			Arrays.fill( letterGrid[i], ' ');
		}
		
		// Second pass
		// First fill up the first ( top ) line
		charLocation = 0;
		
		for( int i = 1; i < LETTERGRIDXSIZE - 1; i++ ) {
			letterGrid[0][i] = endsLetters[0].charAt( charLocation );
			
			charLocation++;
		}
		
		// Now iterate through the rest of the lines stopping before the last.
		charLocation = 0;
		
		for( int i = 1; i < LETTERGRIDYSIZE - 1; i++ ) {
			letterGrid[i][ 0] = sideLetters[0].charAt( charLocation );
			letterGrid[i][14] = sideLetters[1].charAt( charLocation );
			charLocation++;
		}
		
		// Finally fill the last line.
		charLocation = 0;
		
		for( int i = 1; i < LETTERGRIDXSIZE - 1; i++ ) {
			letterGrid[14][i] = endsLetters[1].charAt( charLocation );
			
			charLocation++;
		}		
	}
	
	/**
	 * Simple method to take the standard emtpy grid and overlay the one loaded from the input file.
	 */
	private void overlayGrid( ) {
		for( int i = 0; i < LETTERGRIDYSIZE - 2; i++ ) {
			for( int j = 0; j < LETTERGRIDXSIZE - 2; j++ ) {
				letterGrid[i + 1][j + 1] = overlayMirrors[i][j];
				
			}
		}		
	}
	
	/**
	 * loadInputFile
	 * 
	 * Load an input file containing 13 lines of 13 characters each that represents the mirror configuration.
	 * 14th line contains the encrypted word.
	 * 
	 * @param fileToLoad - String, the name of the file to load.
	 * @return
	 */
	private boolean loadInputFile( String fileToLoad ) {
		File          file             = new File( fileToLoad );
		Charset       charset          = Charset.forName("US-ASCII");
		
		// Try resource block, which will invoke the close() method when exiting the block ( which is nice )
		try ( BufferedReader reader = Files.newBufferedReader(file.toPath(), charset ) ) {
			String line       = null;
			int    lineNumber = 0;
			
			// Create the overlay mirrors storage area being 2 lines less than the grid to account for the alphabet characters at each end.
			overlayMirrors = new char[(LETTERGRIDYSIZE - 2)][0];
			
			while ((line = reader.readLine()) != null) {
				if( lineNumber < LETTERGRIDYSIZE - 2 ) {
					overlayMirrors[ lineNumber ] = Arrays.copyOf( line.toCharArray(), LETTERGRIDXSIZE - 2 );
					lineNumber++;
				}
				else {
					encryptedWord = line;
					return true;
				}
			}
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
		}
		
		return false;		
	}
	
	/**
	 * displayLetterGrid
	 * 
	 * Simple method to display the grid to stdout
	 */
	private void displayLetterGrid() {
		if( letterGrid == null ) {
			System.out.println("displayLetterGrid: Nothing to display!");
			return;
		}
		
		for( int i = 0; i < LETTERGRIDXSIZE; i++ ) {
			System.out.println( letterGrid[i] );
		}
	}
	
	/**
	 * computeSingleLetterSubstitution
	 * This method effectively draws a line between the letter it has been given on the 
	 * grid and the letter the mirrors reflect too.
	 * 
	 * The actual lines can be turned on by uncommenting that part of the case statement for debugging
	 * 
	 * @param xPos   - position of the character to draw from on the x-axis
	 * @param yPos   - position of the character to draw from on the y-axis
	 * @param xDelta - the direction of travel on the x-axis in number of characters per loop ( 1, -1 ).
	 * @param yDelta - the direction of travel on the y-axis in number of characters per loop ( 1, -1 ).
	 * @return
	 */
	private char computeSingleLetterSubstitution( int xPos, int yPos, int xDelta, int yDelta ) {
		// Can't run this procedure without a letterGrid thats been filled out, so check if its null.
		if( letterGrid == null ) {
			System.out.println("computeSingleLetterSubstitution: main letter grid has not been created.");
			return ' ';
		}
		
//		System.out.println( letterGrid[yPos][xPos] );
		
		// Using a while true here as the algorithm exits when it finds a character that isn't space ( default clause of the switch statement )
		while( true ) {
			// Every time add the deltas to move current position on the grid.
			xPos += xDelta;
			yPos += yDelta;
			
			// Switch statement to handle the different character combinations.
			switch ( letterGrid[yPos][xPos] ) {
				// Mirror type 1 ( backslash )
				case '\\' :
					if ( xDelta == 0 ) {
						// Was moving vertically, now will be moving horizontally.
						// Going to use the shorter if statement syntax for compactness ( boolean expression ? true statement : false statement )
						xDelta = ( yDelta == 1 ? 1 : -1 );
						yDelta = 0;						
					}
					else {
						// Was moving horizontally, now moving vertically.
						yDelta = ( xDelta == 1 ? 1 : -1 );
						xDelta = 0;
					}
						
					break;
				// Mirror type 2 ( forward slash )
				case '/':
					if ( xDelta == 0 ) {
						// Was moving vertically, now will be moving horizontally.
						xDelta = ( yDelta == 1 ? -1 : 1 );
						yDelta = 0;
					}
					else {
						// Was moving horizontally, now moving vertically.
						yDelta = ( xDelta == 1 ? -1 : 1 );
						xDelta = 0;
					}
						
					break;
				// Space character as a clause to make sure we just keep traveling ( or draw a character if you wish )
				case ' ' : 
					// letterGrid[yPos][xPos] = ( xDelta == 0 ? '|' : '-');
					
					break;
					
				// Anything that isn't a mirror or space = end, return what we found.
				default:
//					System.out.println( "End char = " + letterGrid[yPos][xPos] );
					return letterGrid[yPos][xPos];
			}
		}
	}
	
	/**
	 * projectFromLetter
	 * 
	 * When given a letter on the grid, this procedure will:
	 *   find the source character in the substitution cipher
	 *   check to make sure the cipher character isn't already filled in 
	 *   compute the cipher character
	 *   find the cipher character in the substitution cipher and fill in its cipher character with the source character.
	 * 
	 * @param x  - x coordinate in the letterGrid where the letter is located.
	 * @param y  - y coordinate in the letterGrid where the letter is located.
	 * @param dx - direction of movement in the x axis ( 1 - -1 )
	 * @param dy - direction of movement in the y axis ( 1 - -1 )
	 */
	private void projectFromLetter( int x, int y, int dx, int dy ) {
		int characterPosition = 0;
		
		// Check the current substitution cypher to see if its already solved.
		for( int j = 0; j < substitutionChars[0].length; j++ ) {
			if( letterGrid[y][x] == substitutionChars[0][j] ) {  
				characterPosition = j;
				break;
			}
		}

		// if empty, compute the letter
		if( substitutionChars[1][characterPosition] == ' ' ) {
			substitutionChars[1][characterPosition] = computeSingleLetterSubstitution( x,y,dx,dy );
			
			// Find the reverse of this substitution and fill it in.
			for( int j = 0; j < substitutionChars[0].length; j++ ) {
				if( substitutionChars[1][characterPosition] == substitutionChars[0][j] ) {  
					substitutionChars[1][j] = substitutionChars[0][characterPosition];
					break;
				}
			}			
		}		
	}
	
	/**
	 * computeSubstitutions
	 * 
	 * Compute the substitution cipher by running along the characters within the grid
	 */
	private void computeSubstitutions() {
		substitutionChars = new char[2][0];
		
		substitutionChars[0] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		substitutionChars[1] = new char[ substitutionChars[0].length ];
		
		Arrays.fill( substitutionChars[1], ' ' );
		
		// Iterate through the 13 characters on each side of the grid.
		for( int i = 1; i < 13; i++ ) {
			// project out from the top line going down.
			projectFromLetter( i, 0, 0, 1 );
			
			// left side characters toward the right 
			projectFromLetter( 0, i, 1, 0 );
			
			// right side characters toward the left
			projectFromLetter( 14, i, -1, 0 );
			
			// bottom line going up.
			projectFromLetter( i, 14, 0, -1 );
		}
		
		System.out.println( "-- Computed substitution cypher --" );
		System.out.println( substitutionChars[0] );
		System.out.println( substitutionChars[1] );
		
		// displayLetterGrid();
	}
	
	/**
	 * Decode the string passed with the current substitution cipher
	 * 
	 * @param text - the text string to be decoded with the computed cipher.
	 * @return - the deciphered text or the word "null" if no substitution cipher has been computed before this method was called.
	 */
	private String decodeText( String text ) {
		if( substitutionChars == null ) {
			System.out.println( "-- No substition cipher computed! --" );
			return "null";
		}
		
		char[] decodedText = text.toCharArray();
		
		for( int i = 0; i < decodedText.length; i++ ) {
			for( int j = 0; j < substitutionChars[0].length; j++ ) {
				if( decodedText[i] == substitutionChars[0][j] ) {  
					decodedText[i] = substitutionChars[1][j];
					break;
				}
			}
		}
		
		return new String( decodedText );
	}
	
	/**
	 * The only public method available in this class, this procedure will:<br>
	 * - Create an empty letter grid<br>
	 * - load the mirrors input and encrypted text<br>
	 * - overlay the mirrors into the empty letter grid<br>
	 * - compute the substitution cipher by iterating through all the letters in the grid<br>
	 * - decode the encrypted text with the substitution cipher and display it to stdout.<br>
	 * 
	 * @param fileName - file containing 13 lines of mirror input, and a 14th with the encrypted word.
	 */
	public void process( String fileName ) {
		constructLetterGrid();
		
//		displayLetterGrid();
		
		if( loadInputFile( fileName ) ) {
			System.out.println( "Encrypted word = " + encryptedWord );
			
			overlayGrid();
			
			displayLetterGrid();
			
			computeSubstitutions();
			
			System.out.println( "Decrypted word = " + decodeText( encryptedWord ) );			
		}
	}
	
	public static void main(String[] args) {
		MirrorEncryption me = new MirrorEncryption();
		
		me.process( "challengeInput1.txt" );
		
	}

}
