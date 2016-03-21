// This class will hold a huge integer of (virtually) unlimited size that can be grown and have operations performed on it

public class HugeInt {
	public int[] num;
	public int arrayLength;
	public int numSize;
	
	/*
	 * creates a new HugeInt class 
	 * array is of val long
	 */
	public HugeInt(int val){
		numSize = 0;
		arrayLength = val;
		num = new int[arrayLength];
		
		for(int i = 0; i < val; i++)
			num[i] = 0;
	}
	
	/*
	 * creates a new HugeInt class
	 * takes in one parameter of class HugeInt
	 * performs a deep copy on the HugeInt class in param
	 */
	public HugeInt(HugeInt in){
		numSize = in.numSize;
		arrayLength = numSize;
		num = new int[arrayLength];
		
		for(int i = 0; i < arrayLength; i++)
			num[i] = in.num[i];
	}
	
	/*
	 * creates a new HugeInt object
	 * by taking in a String as its parameter
	 * converts the String into an Array of Integers
	 */
	public HugeInt(String input){
		numSize = 0;
		arrayLength = input.length();
		num = new int[arrayLength];
		
		char temp;
		for(int i =0, numberIndex =0; i < arrayLength; i++){
			temp = input.charAt(arrayLength-i-1);
			
			if(Character.isDigit(temp)){				//Error checking in the string
				num[numberIndex] = Character.getNumericValue(temp);			//Convert the character value read to an int
				numberIndex++;							//Only update numberIndex if the digit was read.
				numSize++;
			}
		}
	}
	
	/*
	 * main is used for internal testing
	 */
	public static void main(String args[]){
		HugeInt x = new HugeInt("1001");
		HugeInt y = new HugeInt("100");
		
		//x.makeThemEqual(y);
		
		//x.addHugeInt(y);
		//x.printHugeInt();
		
		//x.subtractHugeInt(y);
		//x.addHugeInt(y);exit
		//x.multiplyByHugeInt(y);
		//x.divideByHugeInt(y);
		x.modByHugeInt(y);
		
		x.printHugeInt();
	}
	
	/*
	 * performs an addition mathematical operation
	 * adds two HugeInt objects together
	 */
	public void addHugeInt(HugeInt x2){
		int boundary = this.numSize;
		if(x2.numSize > boundary) boundary = x2.numSize;

		
		for(int i = 0; i < boundary; i++){
			if(this.arrayLength < x2.numSize)
				this.grow();
			
			if(i < x2.numSize)
				this.num[i] += x2.num[i];
			
			if(this.num[i] > 9){
					
					if(this.arrayLength == i+1)
						this.grow();
						
					if(this.numSize == i+1){
						this.numSize++;
				}
				
				this.num[i+1]++;							//carry the 1 to the next significant bit
				this.num[i] %= 10;						//get rid of the 10 from the previous bit
			}		
		}
		if(this.numSize < x2.numSize)
			this.numSize = x2.numSize;
	}
	
	/*
	 * performs subtraction operation
	 * Subtracts one HugeInt object from another
	 */
	private void subtractHugeIntHelper(HugeInt x2){
		//this.makeThemEqual(x2);
		for(int i = 0; i < this.numSize; i++){
			if(i < x2.numSize){
				if(this.num[i] < x2.num[i]){
					
					/*int nextNonZero = i+1;
					while(true){
						if(nextNonZero >= this.numSize)
							return;
						if(this.num[nextNonZero] == 0)
							nextNonZero++;
						else 
							break;
					}*/
					int nextNonZero = i+1;
					while(this.num[nextNonZero] == 0)
						nextNonZero++;
					
					while(nextNonZero > i){										//Carry the digit down the number back to the original place.
						this.num[nextNonZero]--;
						this.num[nextNonZero-1] += 10;
						nextNonZero--;
					}
					
					if(this.num[numSize-1] == 0){
						this.numSize--;
					}
				}
					this.num[i] -= x2.num[i];									//Always subtract normally after the digits have been fixed
			}
		}
		this.removeExtraZeros();
	}
	
	/*
	 * this is the method that gets called when the user wants to perform the subtraction operation
	 * performs priliminary checks to make sure that the value being subtraced is not greater than 'this' value
	 * if anything fails, it prints a standard out error
	 */
	public int subtractHugeInt(HugeInt x2){
		if(compareHugeInts(this, x2) == 1){								//Error checking for negative results
			System.out.println("Error subtracting HugeInts,");
			this.printHugeInt();
			System.out.println("\n cannot be subtracted from");
			x2.printHugeInt();
			return -1;
		}
		
		this.subtractHugeIntHelper(x2);
		return 1;
	}
	
	/*
	 * multiplies two HugeInt object toegether
	 */
	public void multiplyByHugeInt(HugeInt x2){
		
		if(x2.areAllZeros() || this.areAllZeros()){
			HugeInt ret = new HugeInt("0");
			this.copyAllElem(ret);
			return;
		}
		
		// multiply 2 numbers if they are both less than 9
		if(this.numSize == 1 && x2.numSize == 1){
			this.num[0] = this.num[0] * x2.num[0];
			
			if(this.num[0] > 9){
				this.grow();
				this.numSize = this.arrayLength;
				
				this.num[1] = this.num[0];
				this.num[0] %= 10;
				this.num[1] /= 10;
			}
			
			return;
		}
		
		// else they are 2x2 or higher
		
		// padding in extra 0s to avoid having NullPointerExceptions
		this.makeThemEqual(x2);
		int size = x2.numSize;
		if(this.numSize > x2.numSize) size = this.numSize; 
		
		HugeInt result = new HugeInt(size*2);
		
		for(int i = 0; i < this.numSize; i++){			//top number in mult
			for(int j = 0; j < x2.numSize; j++){		//bottom number in mult
				int temp = this.num[i] * x2.num[j];		//
				
				if(temp > 9)
					result.num[j+1+i] += (temp/10);
				result.num[j+i] += (temp%10);
			}
		}
		
		result.fixCarry();
		
		this.copyAllElem(result);
		this.removeExtraZeros();
	}
	
	private void fixCarry(){
		
		int i = arrayLength-1;
		while(i > -1 && this.num[i] == 0){
			i--;
		}
		this.numSize = i+1;
		
		for(int j = 0; j < this.numSize; j++){
			if(this.num[j] > 9){
				
				if(this.arrayLength == j+1)
					this.grow();
					
				if(this.numSize == j+1){
					this.numSize++;
				}
			this.num[j+1] += this.num[j]/10;							//carry the 10s place to the next significant bit
			this.num[j] %= 10;						//get rid of the 10 from the previous bit
			}
		}
	}
	
	/*
	 * divides two HugeInt objects
	 */
	public void divideByHugeInt(HugeInt x2){
		if(x2.areAllZeros()){
			System.out.println("Divide by zero error");
			return;
		}
		if(compareHugeInts(this,x2) == 1){
			this.copyAllElem(new HugeInt("0"));
			return;
		}	
		
		int count = 1;
		HugeInt temp = new HugeInt(x2); // creating a new HugeInt that will get multiplied
		
		while(true){
			count++;
			temp.addHugeInt(x2); // multiplies newly created HugeInt class with count by converting it to String
			if(compareHugeInts(temp, this) != 1){
				if(compareHugeInts(temp, this) == 0)
					break;
				else
					count--;
				break;
			}
		}
		HugeInt ret = new HugeInt(Integer.toString(count));
		this.copyAllElem(ret);
	}
	
	/*
	 * performs a check to see if the HugeInt class is prime or not
	 * Algorithim idea taken from Wikipedia: Primality Test
	 * this is the link for it: https://en.wikipedia.org/wiki/Primality_test
	 */
	public boolean isAPrime(){
		HugeInt counter = new HugeInt("3");
		HugeInt half = new HugeInt(this);
		half.divideByHugeInt(new HugeInt("2"));
		half.increment(1);
		
		HugeInt temp = new HugeInt(this);
		temp.modByHugeInt(new HugeInt("2"));
		if(!(temp.areAllZeros()))
			return false;
		
		while(compareHugeInts(counter, half) != -1){
			temp = new HugeInt(this);
			temp.modByHugeInt(counter);
			
			if(!(temp.areAllZeros()))
				return false;
			
			counter.increment(2);
		}
		return true;
	}
	
	//increment the HugeInt by an amount < 10 without creating a whole new hugeInt to add to it
	public void increment(int amount){
		
		this.num[0]+= amount;
		int i = 0;
		while(this.num[i] > 9){
			if(this.numSize == i+1){
				this.numSize++;
				
				if(this.arrayLength == i+1);
					this.grow();
			}
			
			this.num[i+1]++;							//carry the 1 to the next significant bit
			this.num[i] %= 10;						//get rid of the 10 from the previous bit
		}
	}
	
	/*
	 * performs a mod on two HugeInt objects
	 */
	public void modByHugeInt(HugeInt x2){
		HugeInt temp = new HugeInt(x2); 										// creating a new HugeInt that will get multiplied
		HugeInt temp3000 = new HugeInt(x2);
		temp3000.multiplyByHugeInt(new HugeInt("3000"));
		
		while(true){
			temp.addHugeInt(temp3000); 							// doubles to work good for large numbers, similar to binary search
			if(compareHugeInts(temp, this) == -1){							
				
				temp.subtractHugeInt(temp3000);									//Once exceeded go back to when it was less than the number
				while(compareHugeInts(temp, this) != -1){						//now do normaly modding
					temp.addHugeInt(x2);
				}
				temp.subtractHugeInt(this);
				if(!temp.areAllZeros()){
					x2.subtractHugeInt(temp);
					this.copyAllElem(x2);
					return;
				}
				this.copyAllElem(temp);
				return;
			}
		}
	}
	
//	public void modByHugeInt(HugeInt x2){
//		long mod = 0;
//		for(int i = 0; i < x2.numSize; i ++){
//			mod += x2.num[i]*(int)Math.pow(10, i);
//		}
//		
//		long result = this.num[this.numSize-1]%mod;
//		for(int j = this.numSize-1; j >=0; j --){
//			result %= (this.num[j]*10)%mod;
//		}
//		this.copyAllElem(new HugeInt(""+result));
//	}
	
	/*
	 * performs a check on the HugeInt object to see if it zero or not
	 * returns a boolean value it is all 0s 
	 * else false otherwise
	 */
	public boolean areAllZeros(){
		for(int i = 0; i < this.numSize; i++){
			if(this.num[i] > 0)
				return false;
		}
		return true;
	}
	
	// makes a deep copy of x2 into this
	private void copyAllElem(HugeInt x2){
		this.numSize = x2.numSize;
		this.arrayLength = x2.arrayLength;
		this.num = new int[this.arrayLength];
		
		for(int i = 0; i < this.arrayLength; i++)
			this.num[i] = x2.num[i];
	}
	
	/*
	 * grows the size of HugeInt by doubling it
	 */
	public void grow(){
		int newLength = arrayLength*2;					//double the length of the array
		int newArray[] = new int[newLength];		
		
		int i;
		
		for(i =0; i < numSize; i++)						//copy the digits
			newArray[i] = num[i];
		
		num = newArray;
		arrayLength = newLength;
	}
	
	/*
	 * prints out the HugeInt in the standard out
	 */
	public void printHugeInt(){
		for(int i = this.numSize-1; i >= 0; i--)
			System.out.print(this.num[i]+" ");
		System.out.println();
	}
	
	/*
	 * compares two HugeInt classes
	 * returns 1 if x1 is greater than x2
	 * returns -1 if x1 is less than x2
	 * returns 0 if they are same
	 */
	public int compareHugeInts(HugeInt x1, HugeInt x2){
		
		if(x1.numSize > x2.numSize)
			return -1;
		else if(x1.numSize < x2.numSize)
			return 1;
		
		
		int index = x1.numSize-1;
		
		while(index >=0 ){
			if(x1.num[index] > x2.num[index])
				return -1;
			else if(x1.num[index] < x2.num[index])
				return 1;
			else
				index--;
		}
		return 0; // this means they are both equal to each other
	}
	
	// pads the HugeInt class with extra zero
	private void padWithZero(int size){
		int[] tempArr = new int[size];
		
		for(int i = 0; i < size; i++)
			tempArr[i] = 0;
		
		for(int i = 0; i < this.numSize; i++)
			tempArr[i] = this.num[i];
		
		this.arrayLength = 	size;
		this.num = 			tempArr;
	}
	
	// makes both HugeInt to have equal numSize
	private void makeThemEqual(HugeInt x2){
		if(this.numSize == x2.numSize)
			return;
		else if(this.numSize > x2.numSize)
			x2.padWithZero(this.numSize);
		else if(this.numSize < x2.numSize)
			this.padWithZero(x2.numSize);
	}
	
	/*
	 * removes all the extra zeros to make the output much nicer to look at
	 */
	private void removeExtraZeros(){
		while(numSize > 1 && this.num[numSize-1] == 0)
			this.numSize--;
	}
	
	public String toString(){
		
		String result = "";
		for(int i = 0;i < this.numSize; i ++)
			result = this.num[i] + result;
		
		return result;
	}
}
