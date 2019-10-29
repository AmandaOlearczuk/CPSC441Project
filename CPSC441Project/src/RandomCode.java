// Java program generate a random 
// UpperCase or LowerCase or Number String 
// CREDIT and code source: https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/

import java.util.*; 

public class RandomCode { 
	
	private int codeSize;
	private String code;
	
	public RandomCode(int size) {
		codeSize = size;
		code = getAlphaNumericString();
	}
	
	public String getCode() {
		return code;
	}

	public String getAlphaNumericString() 
	{ 

		// lower limit for LowerCase Letters 
		int lowerLimit = 97; 

		// lower limit for LowerCase Letters 
		int upperLimit = 122; 

		Random random = new Random(); 

		// Create a StringBuffer to store the result 
		StringBuffer r = new StringBuffer(codeSize); 

		for (int i = 0; i < codeSize; i++) { 

			// take a random value between 97 and 122 
			int nextRandomChar = lowerLimit 
								+ (int)(random.nextFloat() 
										* (upperLimit - lowerLimit + 1)); 

			// append a character at the end of bs 
			r.append((char)nextRandomChar); 
		} 

		// return the resultant string 
		return r.toString(); 
	} 

} 
