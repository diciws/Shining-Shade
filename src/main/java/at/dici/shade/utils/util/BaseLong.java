package at.dici.shade.utils.util;

import java.util.HashMap;

/**
 * Convert numbers to custom Strings and vice versa
 * @author doof
 */
public class BaseLong {

    public final static String RADIX_36 = "0123456789abcdefghijklmnopqrstuvwxyz";
    public final static String RADIX_62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public final static String RADIX_52_NO_VOWELS = "0123456789bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    public final static String RADIX_42_NO_VOWELS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    public final static String RADIX_21_NO_VOWELS_LOWERCASE = "bcdfghjklmnpqrstvwxyz";

    /**
     * Converts a positive numeric value to a String of given characters. <br>
     * Radix = characters.length
     * @param characters The characters that represent the new digits
     * @param value The value to be converted
     * @return The value converted to a String representation
     * @throws IllegalArgumentException If value is negative
     */
    public static String toString(String characters, long value){
        if (value < 0) throw new IllegalArgumentException("Value must be positive: " + value);
        if (value == 0) return characters.substring(0,1);
        StringBuilder outputBuilder = new StringBuilder();
        for(long v = value; v != 0; v = v/characters.length()){
            outputBuilder.append(characters.charAt((int)(v % characters.length())));
        }
        return outputBuilder.reverse().toString();
    }

    /**
     * Converts a String to a long value assuming it used the given characters as digits.
     * @param characters The "digits" of the encrypted long value
     * @param encodedLong The String to be interpreted as an encrypted long value
     * @return The long value represented by the String
     * @throws IllegalArgumentException If given characters are less than 2 <br>
     * If a symbol appears more than once in characters <br>
     * If encodedLong contains a character not present in characters
     */
    public static long parseLong(String characters, String encodedLong){
        HashMap<Character,Integer> charIndexMap = readSymbols(characters);
        long value = 0L;
        for(int i = 0; i < encodedLong.length(); i++){
            Integer index = charIndexMap.get(encodedLong.charAt(i));
            if (index == null) {
                throw new IllegalArgumentException("Value cannot be parsed using given characters.");
            }
            value = value * characters.length() + index;
        }
        return value;
    }

    private static HashMap<Character,Integer> readSymbols(String characters){
        HashMap<Character,Integer> charIndexMap = new HashMap<>();

        if(characters.length() < 2) {
            throw new IllegalArgumentException("Provided only " +
                    characters.length() +
                    " of minimum 2 characters!");
        }

        for(int i = 0; i < characters.length(); i++){
            Integer firstIndex = charIndexMap.putIfAbsent(characters.charAt(i), i);
            if(firstIndex != null) {
                throw new IllegalArgumentException("Character \"" +
                        characters.charAt(i) +
                        "\" appears twice at: " +
                        firstIndex + " & " + i);
            }
        }
        return charIndexMap;
    }
}
