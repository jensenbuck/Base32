package com.tarandrus.base32;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Stand-alone Base32 string encoder and decoder.
 */
public class Base32 {

  // padding character 
  private static final char PADDING_CHAR = '=';
  
  // Lookup table for base32 characters to int equivalents
  private static final char[] LOOKUP_TABLE = new char[] {
    'A','B','C','D','E','F','G','H',   // 0-7
    'I','J','K','L','M','N','O','P',   // 8-15
    'Q','R','S','T','U','V','W','X',   // 16-23
    'Y','Z','2','3','4','5','6','7'    // 24-31
  };
  
  
  /**
   * Encodes the input byte[] to a Base32 string.
   * 
   * @param bytes
   * @return
   */
  public String encode(byte[] bytes) {
    String binaryString = convertByteArrayToBinaryString(bytes);
    String[] stringChunks = convertBinaryStringToFiveBitStringChunks(binaryString); 
    int[] intArray = convertFiveBitStringChunksToIntArray(stringChunks);
    char[] charArray = convertIntArrayToCharArray(intArray);
    
    return String.valueOf(charArray);
  }
  
  
  /**
   * Decodes a Base32 encoded string and returns the equivalent byte[].
   * 
   * @param encodedString
   * @return
   */
  public byte[] decode(String encodedString) {
    String cleansedString = getStringWithoutPadding(encodedString);
    char[] charArray = cleansedString.toCharArray();
    int[] intArray = convertCharArrayToIntArray(charArray);
    String[] stringChunks = convertIntArrayToFiveBitStringChunks(intArray);
    String binaryString = convertFiveBitStringChunksToBinaryStringForDecoding(stringChunks);
    
    return convertBinaryStringToByteArray(binaryString);
  }
  
  
  /**
   * ßConverts a binary string to a byte[].
   * 
   * @param binaryString
   * @return
   */
  private byte[] convertBinaryStringToByteArray(String binaryString) {
    List<Byte> bytes = new ArrayList<>();
    
    for (int i=0; i<binaryString.length()/8+1; i++) {
      int beginning = i*8;
      if (beginning >= binaryString.length()) {
        break;
      }
      
      int ending = beginning + 8;
      String subString = binaryString.substring(beginning, ending);
      bytes.add((byte) Integer.parseInt(subString, 2));
    }
    
    byte[] byteArray = new byte[bytes.size()];
    for (int i=0; i<bytes.size(); i++) {
      byteArray[i] = bytes.get(i);
    }
    
    return byteArray;
  }
  
  
  /**
   * Converts a String[] with each member being a 5bit binary string to a concatenated binary string
   * representation. As per base32 standard, any trailing '0's will be trimmed to ensure the 
   * resulting binary string is a factor of 8.
   * 
   * @param stringChunks
   * @return
   */
  private String convertFiveBitStringChunksToBinaryStringForDecoding(String[] stringChunks) {
    StringBuilder sb = new StringBuilder();
    for(String s : stringChunks) {
      sb.append(s);
    }
    
    if (sb.length()%8 == 0) {
      sb.toString();
    }
    
    return sb.substring(0, sb.length()-sb.length()%8);
  }
  
  
  /**
   * Converts a base32 int[] to a String[] with each member being a 5bit binary string equivalent.
   * 
   * @param intArray
   * @return
   */
  private String[] convertIntArrayToFiveBitStringChunks(int[] intArray) {
    String[] stringChunks = new String[intArray.length];
    
    for (int i=0; i<intArray.length; i++) {
      String binaryString = Integer.toBinaryString(intArray[i]).replace(' ', '0');
      int diff = 5-binaryString.length();
      if (diff > 0) {
        binaryString = IntStream.range(0, diff)
          .boxed()
          .map(num -> "0")
          .collect(Collectors.joining()) + binaryString;
      }

      stringChunks[i] = leftPadString(binaryString, 5, '0');
    }
    
    return stringChunks;
  }
  
  
  /**
   * Converts a char[] to a base32 int equivalent array.
   * 
   * @param charArray
   * @return
   */
  private int[] convertCharArrayToIntArray(char[] charArray) {
    int[] intArray = new int[charArray.length];

    for (int i=0; i<charArray.length; i++) {
      for (int j=0; j<LOOKUP_TABLE.length; j++) {
        if (charArray[i] == LOOKUP_TABLE[j]) {
          intArray[i] = j;
          break;
        }
      }
    }
    
    return intArray;
  }
  
  
  /**
   * Takes in a base32 encoded string and trims any padding and returns.
   * 
   * @param encodedString
   * @return
   */
  public String getStringWithoutPadding(String encodedString) {
    List<Character> chars = new ArrayList<>();
    
    for (int i=0; i<encodedString.length(); i++) {
      if (PADDING_CHAR != encodedString.charAt(i)) {
        chars.add(encodedString.charAt(i));
      }
    }
    
    char[] charArray = new char[chars.size()];
    for (int i=0; i<chars.size(); i++) {
      charArray[i] = chars.get(i);
    }
    return String.valueOf(charArray);
  }
  
  
  /**
   * Converts a byte[] to a concatenated binary string.
   * 
   * @param bytes
   * @return
   */
  private String convertByteArrayToBinaryString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    
    for (byte b : bytes) {
      sb.append(convertCharToEightBitBinaryString(b));
    }
    
    return sb.toString();
  }
  
  
  /**
   * Converts a character to an 8bit binary string equivalent.
   * 
   * @param b
   * @return
   */
  private String convertCharToEightBitBinaryString(byte b) {
    return leftPadString(Integer.toBinaryString(b), 8, '0');
  }
  
  
  /**
   * Separates a binary string into 5 bit chunks. If the input binary string is not a factor of 5, will
   * add trailing '0's as needed to the last member of the array to make up the difference.
   * 
   * @param binaryString
   * @return
   */
  private String[] convertBinaryStringToFiveBitStringChunks(String binaryString) {
    char[] chars = binaryString.toCharArray();
    
    String[] chunks = new String[chars.length/5];
    for (int i=0; i<chunks.length; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j=i*5; j<(i+1)*5 && j<chars.length; j++) {
        sb.append(chars[j]);
      }
      
      chunks[i] = rightPadString(sb.toString(), 5, '0');
    }
    
    return chunks;
  }
  
  
  /**
   * Converts a String[] where each member is a 5bit binary string.
   *  
   * @param stringChunks
   * @return
   */
  private int[] convertFiveBitStringChunksToIntArray(String[] stringChunks) {
    int[] intChunks = new int[stringChunks.length];
    for (int i=0; i<intChunks.length; i++) {
      intChunks[i] = convertBinaryStringToDecimal(stringChunks[i]);
    }
    
    return intChunks;
  }
  
  
  /**
   * Converts a binary string into its int equivalent. 
   * 
   * @param binaryString
   * @return
   */
  private int convertBinaryStringToDecimal(String binaryString) {
    return Integer.parseInt(binaryString, 2);
  }
  
  
  /**
   * Converts an array on ints into an array of base 32 chars. As per the Base32 standard, if the total number of 
   * characters in the return array is not factor of 8, will pad with '='.
   * 
   * @param intArray
   * @return
   */
  private char[] convertIntArrayToCharArray(int[] intArray) {
    int paddingCount = intArray.length%8 > 0 ? 8-intArray.length%8 : 0;
    char[] charArray = new char[intArray.length + paddingCount];
    for (int i=0; i<intArray.length; i++) {
      charArray[i] = LOOKUP_TABLE[intArray[i]];
    }
    
    for (int i=intArray.length; i<charArray.length; i++) {
      charArray[i] = PADDING_CHAR;
    }
    
    return charArray;
  }
  
  private String leftPadString(String baseString, int count, char pad) {
    return String.format("%1$" + count + "s", baseString).replace(' ', pad);
  }
  
  private String rightPadString(String baseString, int count, char pad) {
    return String.format("%1$-" + count + "s", baseString).replace(' ', pad);
  }
  
}
