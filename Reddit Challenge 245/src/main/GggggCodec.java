/**
 * Reddit coding challenge
 * This program implements a codec for the challenge given, and can operate in two ways.
 * 
 * - Given a key on the first line, it can decode messages on the following lines.
 * - Given a piece of text, it will use huffman encoding to create a key, then encode a message
 * 
 * The message can come from a file, or just be given as arguments to the class itself, the main method
 * has a number of example of use.
 * 
 * This is a monolithic source file, everything is included, helper classes are at the bottom.
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

class GggggCodec {
	ArrayList<GggggCode> coderKey  = new ArrayList<>();
	ArrayList<GggggCode> stdKey    = new ArrayList<>();
	
	public GggggCodec() {
		// Build up the standard key once.
		buildStandardKey();
	}
	
	public GggggCodec( String coderLine ) {
		// Build up the standard key once.
		buildStandardKey();
		initialiseCodecWithKey( coderLine );
	}
	
	/**
	 * Initialises the codec with a key given as a parameter.
	 * 
	 * @param coderLine - key in letter <space> code pairs on a single line.
	 * @return
	 */
	public boolean initialiseCodecWithKey( String coderLine ) {
		// Look for letter " " code pairs
		// letter - 1 char
		// " "    - space char 
		// code   - n characters.
		coderKey.clear();
		String[] splitLine = coderLine.split(" ");
		
		// Verify step.
		for( int i = 0; i < splitLine.length; i+=2 ) {
			if ( splitLine[i].length() > 1 ) {
				System.out.println("Key is a bit off");
				return false;
			}
		}
		
		// Build array step
		for( int i = 0; i < splitLine.length; i+=2 ) {
			if ( splitLine[i].length() == 1 ) {
				coderKey.add(new GggggCode( splitLine[i].charAt(0), splitLine[i+1] ) ); 
			}
		}		
		
		return true;
	}
	
	/**
	 * Debug function to list the letter + code pairs that have been found.	
	 */
	public void showTheKey() {
		System.out.println( "--- Coder Key (" + coderKey.size() + ") ---" );
		
		for ( GggggCode Gg : coderKey ) {
			System.out.println( Gg.letter + " = " + Gg.code );
		}
	}
	
	/**
	 * Build a key used for some standard characters that aren't encoded at all.
	 * Separated solely to setup once, use many times.
	 */
	private void buildStandardKey () {
		// Add all the standard grammar characters these are not included in the key.
		stdKey.add(new GggggCode('!', "!"));
		stdKey.add(new GggggCode(',', ","));
		stdKey.add(new GggggCode(' ', " "));
		stdKey.add(new GggggCode('/', "/"));
		stdKey.add(new GggggCode('\"', "\""));
		stdKey.add(new GggggCode('_', "_"));
		stdKey.add(new GggggCode('\'', "\'"));
		stdKey.add(new GggggCode('.', "."));
	}
	
	/**
	 * Tests the character given against the key above ( standard key ) to see if 
	 * it exists in there
	 * 
	 * @param letter - character to test against.
	 * @return true  - character exists in the standard key.
	 *         false - character does not exist in the standard key.
	 */
	private boolean testStandardKey( char letter ) {
		for( GggggCode gG : stdKey ) {
			if( gG.letter == letter ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * After the key has been processed, this function will decode the message.
	 *  
	 * @param msg - the message to be decoded
	 * @return the decoded message
	 */
	public String decodeLine( String msg ) {
		String outputString = new String();
		
		// Looking for the longest possible match first
		ArrayList<GggggCode> matchList = new ArrayList<>();
		
		// Loop until we find no more matches.
		while ( true )
		{
			matchList.clear();
			
			// User given key.
			for ( GggggCode Gg : coderKey ) {
				if ( msg.startsWith(Gg.code) ) {
					matchList.add( Gg );
				}
			}
			// If we didn't find something from the user generated key, look through the standard one which is simply char mappings.
			if ( matchList.size() == 0 ) {
				// Standard key.
				for ( GggggCode Gg : stdKey ) {
					if ( msg.startsWith(Gg.code) ) {
						matchList.add( Gg );
					}
				}			
			}
			
			// Found at least one match
			if ( matchList.size() > 0 ) {
				// Chose the longest value that matches.
				Collections.sort( matchList );
				
				msg = msg.substring( matchList.get(0).getCodeLength() );
				outputString += matchList.get(0).letter;
			}
			// Didn't find a match at all.
			else {
				break;
			}
		}
		
		return outputString;
	}
	
	/**
	 * Encode a message from plain text to codes using the key processed earlier.
	 * 
	 * @param msg - the message to be encoded
	 * @return the encoded message.
	 */
	public String encodeLine( String msg ) {
		String outputMessage = new String();
		
		GggggCode outputGg;
		
		for ( int i=0; i < msg.length(); i++ ) {
			outputGg = null;
			// User given key.
			for ( GggggCode Gg : coderKey ) {
				if ( msg.charAt(i) == Gg.letter ) {
					outputGg = Gg;
				}
			}
			// If we didn't find something from the user generated key, look through the standard one which is simply char mappings.
			if ( outputGg == null ) {
				// Standard key.
				for ( GggggCode Gg : stdKey ) {
					if ( msg.charAt(i) == Gg.letter ) {
						outputGg = Gg;
					}
				}			
			}
			
			// Found a match.
			if ( outputGg != null ) {
				outputMessage += outputGg.code;
			}
			// No match.
			else {
				break;
			}
		}		
		
		return outputMessage;
	}
	
	/** 
	 * Decodes a message in the standard format
	 * First line is the key in character code pairs
	 * Lines after that are the message to be decoded.
	 * 
	 * @param encodedStringAL - Message to be decoded.
	 * @return decoded message stored as an ArrayList&ltString&gt
	 */
	public ArrayList<String> decodeMessage( ArrayList<String> encodedStringAL ) {
		ArrayList<String> decodedMessage = new ArrayList<String>();
		
		for( int i=0; i < encodedStringAL.size(); i++ ) {
			if( i == 0 ) {
				// First line contains the key.
				initialiseCodecWithKey( encodedStringAL.get( i ) );
			}
			else {
				decodedMessage.add( decodeLine( encodedStringAL.get( i ) ) );
			}
		}
		
		return decodedMessage;
	}
	
	/**
	 * Implements a huffman encoding algorithm to find the smallest 
	 * amount of text required to generate an encoded message.
	 * 
	 * @param decodedStringAL - the String to be encoded in an ArrayList<String> of lines.
	 * @return
	 */
	public ArrayList<String> encodeMessageWithoutKey( ArrayList<String> decodedStringAL ) {
		PriorityQueue<HuffAbstract> encodingQueue = new PriorityQueue<HuffAbstract>();
		
		int[] charFreqArray = new int[256];
		
		// First step find character frequencies
		for( String msg : decodedStringAL ) {
			for( int i=0; i < msg.length(); i++ ) {
				// There are some characters that don't need to be encoded, don't increment the counter for them
				if( !testStandardKey( msg.charAt( i ) ) ) {
					charFreqArray[ (int)msg.charAt( i )]++;
				}
			}
		}
		
//		System.out.println("---Frequency Table---");
		// Next step create all the leaf objects required to store all the character frequencies
		for( int i=0; i < charFreqArray.length; i++ ) {
			if( charFreqArray[i] > 0 ) {
//				System.out.println( "char: " + (char)i + " freq: " + charFreqArray[i] );
				
				// Add a new Huffman leaf object into a queue for processing later.
				if( !encodingQueue.add( new HuffLeaf( (char)i, charFreqArray[i] ) ) ) {
//					System.out.println("Not added!");
					return null;
				}
			}
		}
		
//		System.out.println("---Huffman Table (" + encodingQueue.size() + ")---");
		if( encodingQueue.size() == 0 )
			return null;
		
		// Keep joining leafs together until there is one object left, this should be the root object.
		while( encodingQueue.size() > 1 ) {
			HuffAbstract left  = encodingQueue.poll();
			HuffAbstract right = encodingQueue.poll();
			
			encodingQueue.add( new HuffNode( left, right ));
		}
		
		HuffAbstract rootNode = encodingQueue.poll();
		
		// Clear out the current key, and fill in a new one.
		coderKey.clear();
		
		recursiveFillCodes( rootNode, new String() );

		// Clear out the queue, cleaning up the tree thats been created.
		encodingQueue.clear();
//		showTheKey();
		
		// Now encode the message with this key.
		ArrayList<String> returnedMessage = new ArrayList<String>();
		
		String keyMessage = new String(); 
		
		// Place the key into the first line of output.
		for( int i=0; i < coderKey.size(); i++ ) {
			keyMessage += coderKey.get(i).letter + " " + coderKey.get(i).code + " "; 
		}
		
		returnedMessage.add( keyMessage );
		
		// Encode the message
		for( String msg : decodedStringAL ) {
			returnedMessage.add( encodeLine( msg ) );
		}
		
		return returnedMessage;
	}
	
	/**
	 * Recursive method used to create the character codes from the Huffman coded tree
	 * and fills in the coderKey with the new key.
	 * 
	 * @param huffNode  - Root node of the Huffman coding tree
	 * @param composite - The composite string built recursively to assign codes to the characters.
	 */
	public void recursiveFillCodes( HuffAbstract huffNode, String composite ) {
		// If its a node, recurse again, otherwise assign the generated code to the leaf and add it into the current key.
		if( huffNode instanceof HuffNode ) {
			// There will always be a left and right object here
			recursiveFillCodes( ((HuffNode)huffNode).left , new String( composite + "g" ));
			recursiveFillCodes( ((HuffNode)huffNode).right, new String( composite + "G" ));
		}
		else {
			// Assign then display the char + code.
			((HuffLeaf)huffNode).code = composite;
			
//			System.out.println( "char: " + ((HuffLeaf)huffNode).character + " freq: " + ((HuffLeaf)huffNode).frequency + " code=" + ((HuffLeaf)huffNode).code );
			
			coderKey.add( new GggggCode( ((HuffLeaf)huffNode).character, ((HuffLeaf)huffNode).code ) );
		}
	}
	
	/**
	 * Loads the challenge input ready for processing. 
	 * 
	 * @param fileLoad - String containing the name of the file to load
	 * @return ArrayList&ltString&gt of the file contents line by line.
	 */
	private ArrayList<String> loadFile( String fileLoad ) {
		File              file      = new File( fileLoad );
		Charset           charset   = Charset.forName("US-ASCII");
		ArrayList<String> fileLines = new ArrayList<String>();
		
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) { 
			String line = null;
		    
		    while ((line = reader.readLine()) != null) {
	    		fileLines.add( line );
		    }

			return fileLines;
		}
		catch ( Exception e ) {
			System.out.println( e.toString() );
			return null;
		}
	}
	
	public static void main( String[] args ) {
		String result;
		
		ArrayList<String> encodedAL = new ArrayList<String>()
                        , decodedAL;
		
		// Process the challenge sample input 1
		System.out.println("--- Part 1 ( Decoding ) ---");

		GggggCodec gCodec = new GggggCodec("H GgG d gGg e ggG l GGg o gGG r Ggg w ggg");
		
		result = gCodec.decodeLine("GgGggGGGgGGggGG, ggggGGGggGGggGg!");
		
		System.out.println( " --- Sample decoder output 1 ---");
		System.out.println( " " + result );
		
		// Process the challenge sample input 2
		if ( gCodec.initialiseCodecWithKey("a GgG d GggGg e GggGG g GGGgg h GGGgG i GGGGg l GGGGG m ggg o GGg p Gggg r gG y ggG") ) {
//			gCodec.showTheKey();
			result = gCodec.decodeLine("GGGgGGGgGGggGGgGggG /gG/GggGgGgGGGGGgGGGGGggGGggggGGGgGGGgggGGgGggggggGggGGgG!");

			System.out.println( " --- Sample decoder output 2 ---");
			System.out.println( " " + result );
		}
		
		System.out.println("--- Part 2 ( Encoding ) ---");
		
		if ( gCodec.initialiseCodecWithKey("H ggg d ggG e gGg l gGG o Ggg r GgG w GGg") ) {
			result = gCodec.encodeLine( "Hello World!" );
			
			System.out.println( " --- Sample encoder output 1 ---");
			System.out.println( " " + result );
		}
		
		System.out.println("--- Part 2.1 ( Compression ) ---");
		
		System.out.println( " --- Sample encoded output ---");
		
		// Process the challenge input.
		decodedAL = gCodec.loadFile("Challenge.txt");

		if( ( encodedAL = gCodec.encodeMessageWithoutKey( decodedAL ) ) != null ) {
			for( String s : encodedAL ) {
				System.out.println( s );
			}
		}		
		
		System.out.println( " --- Sample decoded output ---");
		
		decodedAL = gCodec.decodeMessage( encodedAL );
		
		for( String s : decodedAL ) {
			System.out.println( s );
		}	
		

	}
}

/**
 * Service class that stores the letter + code pairs
 *
 */
class GggggCode implements Comparable<GggggCode> {
	public char   letter;
	public int    codeLength;
	public String code;
	
	public GggggCode ( char L, String C ) {
		this.letter = L;
		this.code   = C;
		
		codeLength = this.code.length();
	}

	@Override
	public int compareTo(GggggCode o) {
		// TODO Auto-generated method stub
		return o.codeLength - this.codeLength;
	}

	public int getCodeLength() {
		return codeLength;
	}
}

/**
 * Huffman encoding helper classes go here...
 */

/**
 * HuffAbstract super type for the leaf, and node objects so they can be represented easily
 * Implements the Comparable interface to allow this object to be inserted easily into
 * a PriorityQueue, gets ordered on the frequency stored in here.
 *
 */
abstract class HuffAbstract implements Comparable<HuffAbstract>  {
	// This is to store the frequency of the characters either: 
	// - at the leave level - The frequency of occurrences of the character itself.
	// - at the node  level - The frequency of the underlying nodes and leaves.
	int frequency;
	
	public HuffAbstract( int f ) {
		this.frequency = f;
	}
	
	// This comparator will allow the PriorityQueue that we insert concrete version of this class into to 
	// order based on frequency whether its a leaf or node object.
	public int compareTo(HuffAbstract o) {
		// TODO Auto-generated method stub
		return (int)Math.signum( this.frequency - o.frequency );
	}	
}

/**
 * Leaf contains the character, and code sequence for the key.
 * This is separated from the GggggCode as both objects would 
 * be implementing the Comparable interface and they use 
 * that in different ways.
 *
 */
class HuffLeaf extends HuffAbstract {
	char   character;
	String code;
	
	public HuffLeaf( char c, int f ) {
		super( f );
		this.character = c;
	}
}

/**
 * Node connects two other objects together.
 * Frequencies get added of the objects get added together too.
 *
 */
class HuffNode extends HuffAbstract {
	HuffAbstract left;
	HuffAbstract right;
	
	public HuffNode( HuffAbstract l, HuffAbstract r ) {
		super( l.frequency + r.frequency );
		this.left  = l;
		this.right = r;
	}
}