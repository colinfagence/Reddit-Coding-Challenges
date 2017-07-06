package countdown;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReversePolishNotation {
	private static final Logger log = LogManager.getLogger( ReversePolishNotation.class );
	Stack<Integer>  numbersStack           = new Stack<Integer>();
	
	/**
	 * This method attempts to solve the RPN equation given as a string
	 * 
	 * @param calculation - String containing the RPN formatted string.
	 * 
	 * @return An Integer object containing the result, or null if something is wrong.
	 */
	public Integer doCalculation( String calculation ) {
		String[] calculationSplit = calculation.split(" ");
		
		for( String str : calculationSplit ) {
			log.debug( str );

			if( str.matches("\\d.*")) {
				// Number, push it onto the stack.
				numbersStack.push( new Integer( str ) );
			}
			else if ( str.length() == 1 ) {
				if ( numbersStack.size() >= 2 ) {
					Integer n3 = null;
					Integer n2 = numbersStack.pop();
					Integer n1 = numbersStack.pop();
	
					switch ( str.charAt(0) ) {
					   case '-': n3 = n1 - n2;
						   	     break;
					   case '+': n3 = n1 + n2;
						   		 break;
					   case '*': n3 = n1 * n2;
						   		 break;
					   case '/': if ( n2 == 0 ) return null;
						   		 n3 = n1 / n2;
						   		 break;
					   // For some reason we got an operator that there is no logic for
					   default : return null;
					}
					
					if( n3 != null ) {
						numbersStack.push( n3 );
					}
				}
				else { // There aren't enough numbers to operate on, this means the RPN equation is illegal/incorrect
					return null;
				}
			}
			else {
				return null;
			}
		}
		
		// Check to see how many numbers are left in the stack...
		if( numbersStack.size() > 1 ) {
			return null;
		}
		else {
			int returnResult = numbersStack.pop();
			
			log.info( "Returning result: " + returnResult );
			return new Integer( returnResult );			
		}

	}

	public static void main(String[] args) {
//		String calculation = new String("100 21 3 / 4 * /");
		String calculation = new String("4 50 25 100 - - -");
		
		Integer result;
		
		ReversePolishNotation n = new ReversePolishNotation();
		
		if( ( result = n.doCalculation( calculation ) ) != null ) {
			System.out.println( "Success: " + result );
		}
	}

}
