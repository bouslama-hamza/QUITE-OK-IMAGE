package cs107;

public final class ArrayUtils {

    private ArrayUtils(){}

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

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

    public static byte[] wrap(byte value) {
        return new byte[]{value};
    }


    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    public static int toInt(byte[] bytes) {
        assert bytes != null;
        assert bytes.length == 4;
        return ((bytes[0]&0b11111111) << 24)   | ((bytes[1]&0b11111111) << 16)
                | ((bytes[2]&0b11111111) << 8) | (bytes[3]&0b11111111);
    }


    public static byte[] fromInt(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16),(byte) (value >>> 8),(byte) value};
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    public static byte[] concat(byte... bytes) {
        assert bytes != null;
        return bytes;
    }

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

    public static byte[] extract(byte[] input, int start, int length) {
        assert (input != null) && (start >= 0 && start < input.length);
        assert (length >= 0) && (start + length <= input.length);

        byte[] extracted = new byte[length];
        for (int i = 0; i < length; i++) {
            extracted[i] = input[start + i];
        }
        return extracted;
    }

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