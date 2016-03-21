/*
 * used Primality Test to check if the number is prime
 * sudo code can be found here
 * https://en.wikipedia.org/wiki/Primality_test
 */

public class PrimeNumber {
	public HugeInt p;
	
	// constructor
	PrimeNumber(String in){
		p = new HugeInt(in);
	}
	
	/*
	 * checks to see if a number is prime
	 * creating an new class to make it easy for Java Garbage Collector
	 */
	public boolean isAPrime(){
		HugeInt denominator = new HugeInt(this.p);
		return denominator.isAPrime();
	}
	
	/*
	 * checks to see if the number is prime or not
	 */
	public boolean isSame(){
		HugeInt mod2 = new HugeInt(this.p);
		mod2.modByHugeInt(new HugeInt("2"));
		mod2.printHugeInt();
		
		HugeInt mod3 = new HugeInt(this.p);
		mod3.modByHugeInt(new HugeInt("3"));
		
		if(this.p.compareHugeInts(this.p, new HugeInt("1")) != -1)
			System.out.println("less than or equal to 1");
		else if(this.p.compareHugeInts(this.p, new HugeInt("3")) != -1)
			System.out.println("greater than 1 but less than 3");
		else if(mod2.areAllZeros() || mod3.areAllZeros())
			System.out.println("mod by 2 or 3");
		
		return true;
	}
	
	/*
	 * returns the HugeInt object associated with this object
	 */
	public HugeInt getPrime()
	{
		return p;
	}
	
	/*
	 * main class used for internal testing
	 */
	public static void main(String[] args){
		PrimeNumber pm = new PrimeNumber("179424691");
		if(pm.isAPrime())
			System.out.println("yes");
		else
			System.out.println("no");
		System.out.println("huh: " + pm.isSame());
	}
}
