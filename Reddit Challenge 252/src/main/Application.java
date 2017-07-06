package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
	// Character array used to enumerate for widest_leftmost_pair_helper, each time its run, it runs on one character at a time.
	char[]                   alphabet              = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','_'};
	
	// Challenge text stored here, either by loading a file with the text, or by manually setting it.
	String                   inputText;

	// Global array list used for storing the relative character positions 
	// when executing widest_leftmost_pair_helper
	int[]                    characterIndexesArray = new int[512];
	
	// Array used for checking for duplicated characters in testForDuplicateChars
	int[]                    charCounting          = new int[256];
	
	/**
	 * Loads the challenge input into the String inputText ready for processing. 
	 * 
	 * @param fileLoad - String containing the name of the file to load
	 * @return
	 */
	private boolean loadFile( String fileLoad ) {
		File          file    = new File( fileLoad );
		Charset       charset = Charset.forName("US-ASCII");
		StringBuilder sb      = new StringBuilder();
		
		inputText = new String();
		
		// Try resource block, which will invoke the close() method when exiting the block ( which is nice )
		try ( BufferedReader reader = Files.newBufferedReader(file.toPath(), charset ) ) 
		{ 
		    String line = null;
		    
		    while ((line = reader.readLine()) != null) {
		    	sb.append(line);
		    }
		    
		    inputText = sb.toString();
		    
		    return true;
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
			
			return false;
		}
	}
	
	/**
	 * Tests the given piece of text for the duplication of characters
	 * 
	 * Returns true  - duplicate characters found
	 *         false - no duplicate characters
	 */
	private boolean testForDuplicateChars( String testString ) {
		// Blank the array used globally.
        for( int j = 0; j < charCounting.length; j++ ) {
        	charCounting[j] = 0;
        }
		
        // Search through the fragment for duplicated characters. 
		for( int j = 0; j < testString.length(); j++ ) {
			charCounting [ (int)testString.charAt( j ) ]++;
			
			if( charCounting [ (int)testString.charAt( j ) ] >= 2 ) {
				return true;
			}
		} 
		
		return false;
	}
	
	/**
	 * Attempts to find the longest fragment of text between the character this method has been assigned 
	 * looking for the longest fragment of text without repeating characters in it.
	 *    This algorithm searches between the start char too two more indexes along, anything further than that 
	 *    and we know we have at least one duplicated character, the one this method was assigned!
	 *    
	 * Returns the widest pair of the assigned character that have no duplicate characters contained within.
	 */
	public stringSelector widest_leftmost_pair_helper( char assignedChar ) {
		int            characterArrayIndex = 0;
		stringSelector longestStringFound  = null;
		
		// Using a global array rather than an ArrayList saves a bit of time, no need to 
		// initialise as we set up a specific number of these values here.
		for( int i = 0; i < inputText.length(); i++ ) {
			if( inputText.charAt( i ) == assignedChar ) {
				characterIndexesArray[ characterArrayIndex ] = i;
				characterArrayIndex++;
			}
		}

		for( int i = 0; i < characterArrayIndex; i++ ) {
				// Not the most readable for loop, but start at the next index of this character, and test for 
			    // duplicates till we have reached a point where at least two of the assigned character are within the text 
			    // fragment, or to the final assigned character, whichever is first.
				for( int j = i + 1; j < (( i + 4 ) > characterArrayIndex ? characterArrayIndex : i + 4); j++ ) {			
						// Check between the two character positions for characters that aren't duplicated.
						String searchString = inputText.substring( characterIndexesArray[ i ] + 1, characterIndexesArray[ j ] );
					
						if ( !testForDuplicateChars( searchString ) ) {
					
							if( longestStringFound == null ) {
								longestStringFound = new stringSelector( characterIndexesArray[ i ], assignedChar, searchString );
							}
							else if ( longestStringFound.str.length() < ( characterIndexesArray[ j ] - ( characterIndexesArray[ i ] + 1 ) ) ) {							
								longestStringFound = new stringSelector( characterIndexesArray[ i ], assignedChar, searchString );								
							}
						}
				}
		}	
		
		
//		System.out.println( wlp.str + " " + wlp.character + " " + wlp.left + " " + wlp.right );
		return longestStringFound;
	}		
	
	/**
	 * widest_leftmost_pair
	 * 
	 * Finds the widest leftmost text fragment that contains no repeating characters to meet the following requirement
	 * 
	 *     1. Find the pair of identical characters that are farthest apart, and contain no pairs of identical characters between them. 
	 *        (e.g. for "abcbba" the chosen characters should be the first and last "b")
     *        In the event of a tie, choose the left-most pair. (e.g. for "aabcbded" the chosen characters should be the first and second "b")
	 */
	private stringSelector widest_leftmost_pair() {
		stringSelector stringsSs = null;
		
		// Create a list of threads required and assign each a character
		for( int i = 0; i < alphabet.length; i++ ) {
			stringSelector wlp = widest_leftmost_pair_helper( alphabet[i] );
			
			// This logic finds the longest string thats the left most.
			if ( wlp != null ) {
				if ( stringsSs == null ) {
					stringsSs = wlp;
				}
				else if ( wlp.str.length() == stringsSs.str.length() 
					   && wlp.left          < stringsSs.left         ) {
//					System.out.println( "Adding: " + wlp.str );
					stringsSs = wlp;
				}
				else if ( wlp.str.length()  > stringsSs.str.length() ) {
//					System.out.println( "Adding: " + wlp.str );
					stringsSs = wlp;
				}				
			}
		}
		
		// Return null, or the appropriate string.
		if ( stringsSs == null ) {
//			System.out.println( "Returning: null" );
			return null;
		}
		else {
//			System.out.println( "Returning: " + stringsSs.str + " char: " + stringsSs.character );
			return stringsSs;
		}
	}
		
	/**
	 * Uses the stringSelector given, to remove the two characters from the challenge text, and add one onto the end.
	 * 
	 * @param ss
	 */
	private void removeCharacters ( stringSelector ss ) {
		String sBegin  = inputText.substring( 0          , ss.left );
		String sMiddle = inputText.substring( ss.left + 1, ss.right - 1 );
		String sEnd    = inputText.substring( ss.right   );
		
//		System.out.println( "ss.left: " + ss.left + " ss.right: " + ss.right );
				
//		System.out.println( "Begin  |" + sBegin  + "|" );
//		System.out.println( "Middle |" + sMiddle + "|" );
//		System.out.println( "End    |" + sEnd    + "|" );
		
		inputText = sBegin + sMiddle + sEnd + ss.character;
	}	
	
	/**
	 * effectively the main method, which performs all steps required to decode the challenge text. 
	 */
	public void performUnencode() {
		stringSelector ss     = null;
//		int            rounds = 0;
		
		if ( loadFile("ChallengeInput.txt") ) {
			System.out.println( inputText );
		}		
		
//		inputText = "ttvmswxjzdgzqxotby_lslonwqaipchgqdo_yz_fqdagixyrobdjtnl_jqzpptzfcdcjjcpjjnnvopmh";
		
		System.out.println( inputText );		
		
		long startTime = System.nanoTime();
		
		while ( ( ss = widest_leftmost_pair( ) ) != null ) {
/*			if( ( rounds % 100 ) == 0 ) {
			   System.out.println( inputText.length() + " " + inputText );
			}*/
			
			removeCharacters( ss );

//			rounds++;
//			System.out.println( inputText );
		}

//		System.out.println( inputText );
		
		String answer = inputText.substring( 0, inputText.indexOf('_') );
		
		System.out.println(answer);
		
		long endTime = System.nanoTime();
		
		System.out.println( "Time taken " + (endTime - startTime ) / 1e6 );
	}
	
	public static void main(String[] args) {
		Application t2 = new Application();
		
		t2.performUnencode();
	}
}

/**
 * stringSelector
 * 
 * Simple class used to make storing the locations of text, and removing the letters easier across the program
 *
 * Constructor accepts three parameters
 * @param l - location of the start of this text within the source string
 * @param c - the character that surrounds this piece of text
 * @param s - the string itself
 * 
 */
class stringSelector {
	String str;
	int    left
         , right;
	char   character;
	
	// Constructor makes creating these a bit easier.
	public stringSelector( int l, char c, String s ) {
		this.left      = l;
		this.right     = l + s.length() + 2;
		this.str       = s;
		this.character = c;
	}
}

