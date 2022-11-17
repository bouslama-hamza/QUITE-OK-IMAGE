package cs107;

@SuppressWarnings("unused")


public final class SignatureChecks {

    private SignatureChecks(){}

    private static int[][]  int_2_dim  = new int[0][0];
    private static byte[][] byte_2_dim = new byte[0][0];

    private static int[]  int_1_dim    = new int[0];
    private static byte[] byte_1_dim   = new byte[0];
    private static int      int_value  = 0;
    private static byte     byte_value = 0;

    private static boolean boolean_value = false;

    private static Helper.Image image_value = null;


    // ==================================================================================
    // ============================= ArrayUtils METHODS =================================
    // ==================================================================================

    private static void task_1_signature_checks(){

        byte_2_dim = ArrayUtils.imageToChannels(int_2_dim);

        int_2_dim = ArrayUtils.channelsToImage(byte_2_dim, int_value, int_value);

        byte_1_dim = ArrayUtils.concat(byte_2_dim);
        byte_1_dim = ArrayUtils.concat(byte_1_dim, byte_1_dim, byte_1_dim);

        byte_1_dim = ArrayUtils.concat(byte_1_dim);
        byte_1_dim = ArrayUtils.concat(byte_value, byte_value, byte_value);

        byte_1_dim = ArrayUtils.fromInt(int_value);

        int_value = ArrayUtils.toInt(byte_1_dim);

        byte_1_dim = ArrayUtils.extract(byte_1_dim, int_value, int_value);

        byte_2_dim = ArrayUtils.partition(byte_1_dim, int_1_dim);
        byte_2_dim = ArrayUtils.partition(byte_1_dim, int_value, int_value);

        boolean_value = ArrayUtils.equals(byte_1_dim, byte_1_dim);

        boolean_value = ArrayUtils.equals(byte_2_dim, byte_2_dim);

        byte_1_dim = ArrayUtils.wrap(byte_value);

    }

    // ==================================================================================
    // ============================= QOIEncoder METHODS =================================
    // ==================================================================================


    private static void task_2_signature_checks(){

        byte_1_dim = QOIEncoder.qoiHeader(image_value);

        byte_1_dim = QOIEncoder.qoiOpRGB(byte_1_dim);

        byte_1_dim = QOIEncoder.qoiOpRGBA(byte_1_dim);

        byte_1_dim = QOIEncoder.qoiOpIndex(byte_value);

        byte_1_dim = QOIEncoder.qoiOpDiff(byte_1_dim);

        byte_1_dim = QOIEncoder.qoiOpLuma(byte_1_dim);

        byte_1_dim  =QOIEncoder.qoiOpRun(byte_value);

        byte_1_dim = QOIEncoder.encodeData(byte_2_dim);

        byte_1_dim = QOIEncoder.qoiFile(image_value);

    }

    // ==================================================================================
    // ============================= QOIDecoder METHODS =================================
    // ==================================================================================

    private static void task_3_signature_checks(){

        int_1_dim = QOIDecoder.decodeHeader(byte_1_dim);

        byte_1_dim = QOIDecoder.decodeQoiOpDiff(byte_1_dim, byte_value);

        byte_1_dim = QOIDecoder.decodeQoiOpLuma(byte_1_dim, byte_1_dim);

        int_value = QOIDecoder.decodeQoiOpRGB(byte_2_dim, byte_1_dim, byte_value, int_value, int_value);

        int_value = QOIDecoder.decodeQoiOpRGBA(byte_2_dim, byte_1_dim, int_value, int_value);

        int_value = QOIDecoder.decodeQoiOpRun(byte_2_dim, byte_1_dim, byte_value, int_value);

        byte_2_dim = QOIDecoder.decodeData(byte_1_dim, int_value, int_value);

        image_value = QOIDecoder.decodeQoiFile(byte_1_dim);

    }

}