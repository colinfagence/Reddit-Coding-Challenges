package countdown;
/**
 * This program meets the requirements of "It's just the first count down (tedudu do)" and "Its the final count down" 
 * within the reddit daily programmer challenge "Challenge #253 [Intermediate] Countdown (numbers game)"
 * 
 * This program recursively finds all possible solutions for the numbers that are given to it, these numbers are 
 * contained in an array declared within the main() method. And it will produce a string containing one solution for that set of numbers.
 * The array of numbers can be any length, but with each additional number the difficulty increase exponentially.
 * 
 * This particular cut revolves around the rules of countdown without using brackets to create a solution.
 * 
 * @author Colin
 */

public class NumbersTest1 {
	char[] mathOperators = {'+','-','/','*'};
	
	/**
	 * Recursive search for solutions
	 * 
	 * This method brute forces the problem by recursively generating all possible combinations of the numbers given, while also generating all
	 * combinations of math operators that could go between them
	 * 
	 * Once a solution is found, the recursion collapses and the method returns true.
	 * 
	 * The solution itself can be printed out by the method, but isn't returned for the moment.
	 * 
	 * @param recurseDepth     - Internal integer used to track how many times this method has been recursively called.
	 * @param sourceArray      - short[] array of numbers
	 * @param workingEquation  - A string that contains the recursively generated equation, this can be printed out once a solution is found.
	 * @param workingTotal     - The sum / product of all the numbers this method has recursively operated on.
	 * @param targetTotal      - The target sum / product
	 * 
	 * @return String          - null if no solution found, a string containing the successful equation.
	 */
	private String recurseSolution( int recurseDepth, short[] sourceArray, String workingEquation, int workingTotal, final int targetTotal ) {
		if( workingTotal == targetTotal ) {
//			System.out.println( workingEquation + " = " + targetTotal );
			return workingEquation;
		}
		// Still working on the solution...
		else if ( recurseDepth < sourceArray.length ) {
			String recursiveSolution = null;
			
			for( int i = 0; i < sourceArray.length; i++ ) {
				if( sourceArray[i] != -1 ) {
					// Save off the current value, and mark it as no longer there.
					short workingNumber = sourceArray[i];
					sourceArray [i]     = -1;
					
					int newTotal  = 0
					  , remainder = 0;
					
					if( recurseDepth == 0 ) {
						// First call of the recursive method, there have no number in the workingEquation, so place the first number in there.
						// Also there is no total, the total should be just the first number we encountered at this point.
						newTotal               = workingNumber;
						String newCompEquation = new String() + newTotal;
						
						// Recurse to for the first time ( second call of this method )
						if( ( recursiveSolution = recurseSolution( recurseDepth + 1, sourceArray, newCompEquation, newTotal, targetTotal ) ) != null ) {
							sourceArray [i] = workingNumber;
							return recursiveSolution;
						}
					}
					else {
						// Iterate through the operators
						for( char mathOperator : mathOperators ) {						
							String newCompEquation = new String( workingEquation );

							newCompEquation += " " + mathOperator + " " + workingNumber;
							
							// Use a switch statement to perform the math dictated by the individual characters.
							// This switch statement should contain all the characters in the mathOperators array.
							switch ( mathOperator ) {							
								case '+': newTotal = workingTotal + workingNumber;
										  break;
								case '-': newTotal = workingTotal - workingNumber;
									
										  if( newTotal < 0 ) {
											  remainder = 1;
										  }
									
										  break;
								case '/': remainder = workingTotal % workingNumber;
											
										  if( remainder == 0 ) {
											  newTotal = workingTotal / workingNumber;
										  }
											
										  break;
								case '*': newTotal = workingTotal * workingNumber;
									      break;
								default:  break;
							}
							
							// Having a remainder indicates there is a division that resulted in a fraction, this is not OK in countdowns rule set, so circumvent the recursion.
							// Also used to circumvent additional recursion with negative total.
							if ( remainder > 0 ) {
								remainder = 0;
							}
							else {
								if ( ( recursiveSolution = recurseSolution( recurseDepth + 1, sourceArray, newCompEquation, newTotal, targetTotal ) ) != null ) {
									sourceArray [i] = workingNumber; 
									return recursiveSolution;
								}
							}
						}
					}
					
					// Replace the value.
					sourceArray [i] = workingNumber; 
				}
			}
		}
		return null;
	}
	
	/**
	 * Small public method to expose the recurseSolution method, to give the user a shorter sweeter version, 
	 *   Checks should go in here if required, and then setup recurseSolution properly while still returning the result.
	*/
	/**
	 * This method brute forces the problem by recursively generating all possible combinations of the numbers given, while also generating all
	 * combinations of math operators that could go between them
	 * 
	 * Once a solution is found, the recursion collapses and the method returns true.
	 * 
	 * @param sourceArray - short[] array of numbers to create an equation for.
	 * @param targetTotal - The sum/product to look for 
	 * @return            - null if no solution found, a string containing the successful equation.
	 */
	public String findSolution( short[] sourceArray, final int targetTotal ) {
		return recurseSolution( 0, sourceArray, null, 0, targetTotal );
	}
	

	public static void main(String[] args) {
		NumbersTest1 nt1          = new NumbersTest1();
		String       noSolutions  = new String();
		short[]      numbersArray = { 100, 50, 25, 75, 9, 4 };
		
		long startTime = System.nanoTime();
		
		for( int i = 100; i <= 999; i++ ) {
			if( nt1.findSolution( numbersArray, i ) == null ) {
				noSolutions += i + " ";
			}
		}
		
		long endTime   = System.nanoTime();
		
		System.out.println( "No solutions for: " + noSolutions );
		
		System.out.println( "Time taken: " + ( endTime - startTime ) / 1e6 + " ms");
	}

}
