package JUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import intermediate.MahJongHands;
import junit.framework.Assert;

public class MahJongHandsTest {

	@Test
	public void sampleStandardInput1() {
		MahJongHands mjh = new MahJongHands();
		
		assertTrue( mjh.process( "sampleStandardInput1.txt" ) );
	}

	@Test
	public void sampleStandardInput2() {
		MahJongHands mjh = new MahJongHands();
		
		assertTrue( mjh.process( "sampleStandardInput2.txt" ) );
	}
	
	@Test
	public void sampleStandardInput3() {
		MahJongHands mjh = new MahJongHands();

		assertTrue( mjh.process( "sampleStandardInput3.txt" ) );
	}
	
	@Test
	public void sampleBonus1Input1() {
		MahJongHands mjh = new MahJongHands();
		
		assertTrue( mjh.process( "sampleBonus1Input1.txt" ) );
	
	}

	@Test
	public void sampleBonus1Input2() {
		MahJongHands mjh = new MahJongHands();
		
		assertFalse( mjh.process( "sampleBonus1Input2.txt" ) );

	}
	
	@Test
	public void sampleBonus2Input1() {
		MahJongHands mjh = new MahJongHands();
		
		assertTrue( mjh.process( "sampleBonus2Input1.txt" ) );
		
	}
	
	@Test
	public void sampleBonus2Input2() {
		MahJongHands mjh = new MahJongHands();
		
		assertFalse( mjh.process( "sampleBonus2Input2.txt" ) );
	}
}
