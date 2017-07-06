package countdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NumbersTest2 {
	private static final Logger                log = LogManager.getLogger( NumbersTest2.class );
	private        final ReversePolishNotation rpn = new ReversePolishNotation();
	
	char[] mathOperators = {'+','-','/','*',' '};
	Integer resultRPN;
	
	private String recurseRPNEquation( int recurseDepth, short[] sourceArray, String workingEquation, int numNoOperator, final int targetTotal ) {
		String newEquation = null;

		if( recurseDepth >= 1 ) {
			log.info( "WorkingEquation: " + workingEquation );
			if ( ( resultRPN = rpn.doCalculation( workingEquation ) ) != null ) {
				if ( targetTotal == resultRPN.intValue() ) {
					log.info( "We have a winner: " + workingEquation );
				}
			}
		}
		
		if ( recurseDepth >= sourceArray.length ) {
			if ( numNoOperator == 0 ) {

			}
			else if ( numNoOperator > 0 ) {
				for( char mathOp : mathOperators ) {
					if ( mathOp != ' ' ) {
						newEquation = new String() + workingEquation + ' ' + mathOp;
						
						recurseRPNEquation( recurseDepth + 1, sourceArray, newEquation, numNoOperator - 1, targetTotal );
					}
				}
			}
		}
		else {
			for( int i = 0; i < sourceArray.length; i++ ) {
				if( sourceArray[ i ] != -1 ) {
					short workingNumber = sourceArray[ i ];
					
					sourceArray[ i ] = -1;
					
					if( recurseDepth == 0 ) {
						newEquation = new String() + workingNumber;
						
						recurseRPNEquation( recurseDepth + 1, sourceArray, newEquation, 0, targetTotal );
					}
					else {
						int newNumNoOperator = numNoOperator;
						
						for( char mathOp : mathOperators ) {
/*							if ( recurseDepth == sourceArray.length && newNumNoOperator > 0 && mathOp != ' ' ) {
								newEquation = new String() + workingEquation + ' ' + mathOp;
							}
							else*/ 
							if( mathOp == ' ' ) {
								newEquation = new String() + workingEquation + ' ' + workingNumber;
								
								newNumNoOperator++;
							}
							else {
								newEquation = new String() + workingEquation + ' ' + workingNumber + ' ' + mathOp;
							}
							
							recurseRPNEquation( recurseDepth + 1, sourceArray, newEquation, newNumNoOperator, targetTotal );
						}
					}
					
					sourceArray[ i ] = workingNumber;
				}
			}
		}
		return null;
	}
	
	public String findSolution( short[] sourceArray, final int targetTotal ) {
		return recurseRPNEquation( 0, sourceArray, null, 0, targetTotal );
	}
	

	public static void main(String[] args) {
		NumbersTest2 nt1          = new NumbersTest2();
		short[]      numbersArray = { 100, 50, 25, 75, 9, 4 };
		
		long startTime = System.nanoTime();
		
		nt1.findSolution( numbersArray, 952 );
		
		long endTime   = System.nanoTime();
		
		System.out.println( "Time taken: " + ( endTime - startTime ) / 1e6 + " ms");
	}

}
