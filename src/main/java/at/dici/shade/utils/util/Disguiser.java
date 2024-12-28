package at.dici.shade.utils.util;

import java.util.Random;

/**
 * This utility class contains static methods for fast and easy encoding.
 * Used to disguise values or
 */
public class Disguiser {
    private static final Random random = new Random(System.currentTimeMillis());
    public static final int MIN_KEY_SIZE = 4;
    public static final int MAX_KEY_SIZE = 32;

    /**
     * Simple encoding - not super safe but safe enough to make a value unreadable for masses.
     * It's recommended to mask the returned value via a Long-to-String algorithm
     * @param value The value to encode
     * @param keySize The number of bits being encoded with a maximum of 32. Fewer encoded bits can shorten the returned value.
     * @return The encoded value which also contains the random key
     * @throws IllegalArgumentException If the keySize is not between 4 and 32.
     */
    public static long encode(int value, int keySize){
        if (keySize < MIN_KEY_SIZE || keySize > MAX_KEY_SIZE) {
            throw new IllegalArgumentException("Key size out of range! Key size must be withing 4 and 32!");
        }
        long key = random.nextInt(1 << (keySize - 2));
        if (key == value) key = (1L << keySize) - 1 - value;
        key |= (key << keySize);
        return (((long) value) << keySize) ^ key;
    }

    /**
     * The counterpart of the encode() method
     * @param encodedValue The value to decode
     * @param keySize The number of bits that have been encoded.
     * @return The decoded value
     * @throws IllegalArgumentException If the keySize does not lie between 4 and 32.
     */
    public static int decode(long encodedValue, int keySize){
        if (keySize < 4 || keySize > 32) throw new IllegalArgumentException("Key size out of range! Key size must be withing 4 and 32!");
        long key = (encodedValue << (Long.SIZE - keySize)) >>> (Long.SIZE - keySize);
        long value = (encodedValue >>> keySize) ^ key;
        return (int) value;
    }
}
