package main;

import java.io.BufferedReader;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class processor {
	char[] alphabet       = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	String integerValue;
	
	ArrayList<String> resultList = new ArrayList<String>();
	
	/**
	 * Constuctor used to set the initial input text.
	 * 
	 * @param in - an integer containing the sequence of numbers to decode.
	 */
	public processor( int in ) {
		Integer inInteger = new Integer( in );
		
		integerValue = inInteger.toString();
	}

	/**
	 * Constuctor used to set the initial input text.
	 * 
	 * @param in - an string containing the sequence of numbers to decode.
	 * @throws NumberFormatException
	 */
	public processor ( String in ) throws NumberFormatException {
		integerValue = new BigInteger( in ).toString();
	}
	
	/**
	 * Recursive method used to process the values and all gather a list of all derivatives 
	 * 
	 * @param inValue        - the number string that is to be processed.
	 * @param compositeValue - the String that has been built so far
	 */
	private void processInteger( String inValue, String compositeValue ) {
		// No more numbers to process, add what we have into the result list.
		if( inValue.length() == 0 ) {
			resultList.add( compositeValue );
			
			return;
		}
		
		// More numbers to process run through the alphabet looking for all values that could be represented.
		for ( int i = 0; i < alphabet.length; i++ ) {		
			String tempInt = new Integer( i + 1 ).toString();
			
			// Each time we find an option, set up for another recursion of this method
			if ( inValue.startsWith( tempInt ) ) {
				String newInValue        = inValue.substring( tempInt.length() );
				String newCompositeValue = compositeValue + alphabet[i];				
				
				processInteger( newInValue, newCompositeValue );
			}
		}
	}
	
	/**
	 * Root of the recursive processing, setup for a call of processInteger by giving 
	 * the numbers string, and a empty composite string.
	 */
	public void process() {
		processInteger( integerValue, new String());
	}
	
	public ArrayList<String> getResultList() {
		return resultList;
	}
	
	public static void main(String[] args) {
		WordListFinder wlf = new WordListFinder();
		long startTime     = System.nanoTime();
		
		processor pr = new processor("81161625815129412519419122516181571811313518");
		
		// Process the string above into all the potential strings that could represent this number
		pr.process();
		
		long endTime = System.nanoTime();
		
		System.out.println( "Time taken " + (endTime - startTime ) / 1e6 + " ms" );
		
		startTime     = System.nanoTime();
		
		// Now try to figure out from the dictionary the appropriate string text.
		for ( String l : pr.getResultList() ) {
			wlf.recursiveStringFind( l, new singleResult() );
		}
		
		// Sort by word number.
		Collections.sort( wlf.getResultList() );
		
		// The list is now least number of words to greatest, the least number of words for the given string seems to be the correct one.
		singleResult sr = wlf.getResultList().get(0);
		
		System.out.println( sr.resultString );

		endTime = System.nanoTime();
		
		System.out.println( "Time taken " + (endTime - startTime ) / 1e6 + " ms" );
	}
}

// 
/**
 * WordListFinder handles the search for real words within the enable1.txt dictionary.
 * 
 */
class WordListFinder {
	HashSet  <String>       wordList   = new HashSet  <String>();
	ArrayList<singleResult> resultList = new ArrayList<singleResult>();
	
	public WordListFinder() {
		loadWordList();
	}
	
	/** 
	 * Loads the enable1.txt dictionary, invoked in the constructor.
	 */
	private void loadWordList () {
		File file       = new File("enable1.txt");
		Charset charset = Charset.forName("US-ASCII");
		
		try ( BufferedReader reader = Files.newBufferedReader(file.toPath(), charset) ) { 
		    String line = null;
		    
		    while ((line = reader.readLine()) != null) {
		    	if ( line.length() >= 2 ) {
		    		wordList.add(line);
		    	}
		    }
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
		}
	}
	
	private boolean findString( String word ) {
		return wordList.contains( word.toLowerCase() );
	}
	
	/**
	 * Recursively look for words for an option contained within workingString
	 * 
	 * @param workingString - the string to search within for dictionary words
	 * @param composite     - used for recursion, builds up the dictionary word result.
	 */
	public void recursiveStringFind( String workingString, singleResult composite  ) {
		for ( int i = 0; i <= workingString.length(); i++ ) {
			String findString = workingString.substring(0, i);

			if ( findString( findString ) ) {
				singleResult comp = new singleResult( composite.resultString + findString
											        , composite.wordNumber + 1 );
				
				if ( i < workingString.length() ) {
					recursiveStringFind( workingString.substring( i ), comp );
				}
				else if ( i == workingString.length() ) {
					resultList.add( comp );
				}
			}
		}
	}
	
	public ArrayList<singleResult> getResultList() {
		return resultList;
	}
}

/**
 * Class used to store the results and the number of real words it contains.
 * Allows the list to be sorted by word count.
 * 
 * @author Colin
 *
 */
class singleResult implements Comparable<singleResult> {
	public int    wordNumber;
	public String resultString;
	
	public singleResult( String rs, int wn) {
		this.wordNumber   = wn;
		this.resultString = rs;
	}
	
	public singleResult() {
		this.wordNumber   = 0;
		this.resultString = new String();
	}

	@Override
	public int compareTo( singleResult o) {
		// TODO Auto-generated method stub
		return this.wordNumber - o.wordNumber;
	}
}
