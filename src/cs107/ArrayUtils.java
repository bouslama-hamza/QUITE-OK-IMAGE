package cs107;

/**
 * Utility class to manipulate arrays.
 * @apiNote First Task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils(){}

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2){
        assert (a1==null && a2==null) || (a1!=null && a2!=null);
        if(a1==null && a2==null){
            return true;
        }else if(a1.length!=a2.length){
            return false;
        }else{
            for(int i=0; i<a1.length; i++){
                if(a1[i]!=a2[i]){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2){

        assert (a1==null && a2==null) || (a1!=null && a2!=null);
        if(a1==null && a2==null){
            return true;
        }else if(a1.length!=a2.length){
            return false;
        }else{
            for(int i=0; i<a1.length; i++){
                if(a1[i].length!=a2[i].length){
                    return false;
                }else{
                    for(int j=0; j<a1[i].length; j++){
                        if(a1[i][j]!=a2[i][j]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value) {
        return new byte[]{value};
    }


    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */

    public static int toInt(byte[] bytes) {
        assert bytes != null;
        assert bytes.length == 4;
        return ((bytes[0]&0b11111111) << 24)   | ((bytes[1]&0b11111111) << 16)
                | ((bytes[2]&0b11111111) << 8) | (bytes[3]&0b11111111);
    }


    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16),(byte) (value >>> 8),(byte) value};
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte... bytes) {
        assert bytes != null;
        return bytes;
    }


    /**
     * Concatenate a given sequence of arrays into one array
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[]... tabs) {
        assert tabs != null;
        for (int i = 0; i < tabs.length; i++) {
            assert tabs[i] != null;
        }

        int somme = 0;
        for (byte[] tab : tabs) {
            somme += tab.length;
        }

        byte[] result = new byte[somme];
        int index = 0;
        for (byte[] tab : tabs) {
            for (byte b : tab) {
                result[index++] = b;
            }
        }
        return result;
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     * @param input (byte[]) - Array to extract from
     * @param start (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     * start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length) {
        assert (input != null) && (start >= 0 && start < input.length);
        assert (length >= 0) && (start + length <= input.length);

        byte[] extracted = new byte[length];
        for (int i = 0; i < length; i++) {
            extracted[i] = input[start + i];
        }
        return extracted;
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     * or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int... sizes) {
        int somme = 0;
        for (int size : sizes) {
            somme += size;
        }
        assert (input != null) && (sizes != null) && (somme == input.length);


        byte[][] partitions = new byte[sizes.length][];
        int index = 0;

        for (int i = 0; i < sizes.length; i++) {
            partitions[i] = extract(input, index, sizes[i]);
            index += sizes[i];
        }
        return partitions;
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input) {

        assert input != null;
        for (int i = 0; i < input.length; i++) {
            assert input[i] != null;
        }
        for (int i = 1; i < input.length; i++) {
            assert input[i].length == input[i - 1].length;
        }


        byte[][] output = new byte[input.length * input[0].length][4];
        int index = 0;

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                byte[][] a_rgb= partition (fromInt(input[i][j]),1,3);
                output[index++] = concat(a_rgb[1],a_rgb[0]);
            }
        }
        return output;
    }

    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     * @param input (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     * or input's length differs from width * height
     * or height is invalid
     * or width is invalid
     */

    public static int[][] channelsToImage(byte[][] input, int height, int width) {

        assert input != null && input.length == height * width;

        for (int i = 0; i < input.length; i++) {
            assert (input[i] != null) && (input[i].length == 4);
        }

        int[][] output = new int[height][width];
        int index = 0;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                byte[][] rgb_a= partition(input[index],3,1);
                output[i][j]=toInt(concat(rgb_a[1],rgb_a[0]));
                index+=1;
            }
        }
        return output;
    }
}