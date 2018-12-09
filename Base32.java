package com.tarandrus.base32;

public class Base32 {

  private static final char PADDING = '=';
  
	private static final char[] LOOKUP_TABLE = new char[] {
	  'A','B','C','D','E','F','G','H',   // 0-7
	  'I','J','K','L','M','N','O','P',   // 8-15
	  'Q','R','S','T','U','V','W','X',   // 16-23
	  'Y','Z','2','3','4','5','6','7'    // 24-31
	};
	
	public static void main(String[] args) {
	  String s = "Base";
	  byte[] bytes = s.getBytes();
	  
	  System.out.println(new Base32().encode(bytes));
	}
	
	public String encode(byte[] bytes) {
	  String binaryString = convertByteArrayToEightBitBinaryString(bytes);
	  String[] stringChunks = convertBinaryStringToStringChunks(binaryString); 
	  int[] intChunks = convertStringChunksToIntChunks(stringChunks);
	  char[] charArray = convertIntChunksToCharArray(intChunks);
	  
	  return String.valueOf(charArray);
	}
	
	
	private String convertByteArrayToEightBitBinaryString(byte[] bytes) {
	  StringBuilder sb = new StringBuilder();
    
    for (byte b : bytes) {
      sb.append(convertCharToEightBitBinaryString(b));
    }
    
    return sb.toString();
	}
	
	private String convertCharToEightBitBinaryString(byte b) {
	  return String.format("%1$8s", Integer.toBinaryString(b)).replace(' ', '0');
	}
	
	/**
	 * Separates a binary string into 5 bit chunks.
	 * 
	 * @param binaryString
	 * @return
	 */
	private String[] convertBinaryStringToStringChunks(String binaryString) {
	  char[] chars = binaryString.toCharArray();
	  
	  String[] chunks = new String[chars.length/5 + 1];
	  for (int i=0; i<chunks.length; i++) {
	    StringBuilder sb = new StringBuilder();
	    for (int j=i*5; j<(i+1)*5 && j<chars.length; j++) {
	      sb.append(chars[j]);
	    }
	    
	    chunks[i] = String.format("%1$-5s", sb.toString()).replace(' ', '0');
	  }
	  
	  return chunks;
	}
	
	private int[] convertStringChunksToIntChunks(String[] stringChunks) {
	  int[] intChunks = new int[stringChunks.length];
	  for (int i=0; i<intChunks.length; i++) {
	    intChunks[i] = convertBinaryStringToDecimal(stringChunks[i]);
	  }
	  
	  return intChunks;
	}
	
	private int convertBinaryStringToDecimal(String binaryString) {
	  return Integer.parseInt(binaryString, 2);
	}
	
	private char[] convertIntChunksToCharArray(int[] intChunks) {
	  int paddingCount = intChunks.length%8 > 0 ? 8-intChunks.length%8 : 0;
	  char[] charArray = new char[intChunks.length + paddingCount];
	  for (int i=0; i<intChunks.length; i++) {
	    charArray[i] = LOOKUP_TABLE[intChunks[i]];
	  }
	  
	  for (int i=intChunks.length; i<charArray.length; i++) {
	    charArray[i] = PADDING;
	  }
	  
	  return charArray;
	}
	
}
