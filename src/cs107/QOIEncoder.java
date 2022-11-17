package cs107;
import java.util.ArrayList;

public final class QOIEncoder {



    private QOIEncoder(){}

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    public static byte[] qoiHeader(Helper.Image image){

        assert image!=null;
        assert image.channels()==QOISpecification.RGB || image.channels()==QOISpecification.RGBA;
        assert image.color_space()==QOISpecification.sRGB || image.color_space()==QOISpecification.ALL;

        return ArrayUtils.concat(
                QOISpecification.QOI_MAGIC,
                ArrayUtils.fromInt(image.data()[0].length),
                ArrayUtils.fromInt(image.data().length),
                ArrayUtils.wrap(image.channels()),
                ArrayUtils.wrap(image.color_space()));
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    public static byte[] qoiOpRGB(byte[] pixel) {
        assert pixel.length == 4;

        return ArrayUtils.concat(
                ArrayUtils.wrap(QOISpecification.QOI_OP_RGB_TAG),
                ArrayUtils.extract(pixel, 0, 3) );
    }

    public static byte[] qoiOpRGBA(byte[] pixel) {
        assert pixel.length == 4;
        return ArrayUtils.concat(ArrayUtils.wrap(QOISpecification.QOI_OP_RGBA_TAG), pixel);
    }

    public static byte[] qoiOpIndex(byte index){
        assert index>=0 && index<64;
        return ArrayUtils.wrap((byte) (QOISpecification.QOI_OP_INDEX_TAG|index));
    }

    public static byte[] qoiOpDiff(byte[] diff){

        assert diff!=null;
        assert diff.length==3;
        assert diff[0]>-3 && diff[0]<2;
        assert diff[1]>-3 && diff[1]<2;
        assert diff[2]>-3 && diff[2]<2;

        return new byte[] {(byte) (QOISpecification.QOI_OP_DIFF_TAG | (diff[0] + 2) << 4
                                               | (diff[1] + 2) << 2 | (diff[2] + 2) )    };
    }

    public static byte[] qoiOpLuma(byte[] diff){

        assert diff!=null;
        assert diff.length==3;
        assert diff[1]>-33 && diff[1]<32;
        assert (diff[0]-diff[1])>-9 && (diff[0]-diff[1])<8;
        assert (diff[2]-diff[1])>-9 && (diff[2]-diff[1])<8;

        return ArrayUtils.concat((byte) (QOISpecification.QOI_OP_LUMA_TAG | diff[1]+32),
                                  (byte) ((diff[0] - diff[1] + 8)<<4|(byte) (diff[2] - diff[1] + 8)));
    }

    public static byte[] qoiOpRun(byte count){
        assert count>=1 && count<=62;

        return new byte[] {(byte) (QOISpecification.QOI_OP_RUN_TAG | count-1)};
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    public static byte[] encodeData(byte[][] image) {

        assert image != null;
        for (int i = 0; i < image.length; i++) {
            assert image[i] != null;
            assert image[i].length == 4;
        }

        byte[] pixel = QOISpecification.START_PIXEL;
        byte[][] hachage = new byte[64][4];
        ArrayList<byte[]> encodeData = new ArrayList<>();
        int compteur = 0;

        for (int i = 0; i < image.length; i++) {
            if (ArrayUtils.equals(image[i], pixel)) {
                compteur ++;
                if (compteur == 62 || i == (image.length - 1)) {
                    encodeData.add(qoiOpRun((byte) compteur));
                    compteur = 0;
                }
            } else {
                if (compteur > 0) {
                    encodeData.add(qoiOpRun((byte) compteur));
                    compteur = 0;
                }

                int h = QOISpecification.hash(image[i]);
                if (ArrayUtils.equals(image[i], hachage[h])) {
                    encodeData.add(qoiOpIndex((byte) h));
                } else {
                    hachage[h] = image[i];

                    byte[] diff_rgb = new byte[3];
                    for (int j = 0; j < 3; j++) {
                        diff_rgb[j] = (byte) (image[i][j] - pixel[j]);
                    }

                    if (ArrayUtils.equals(ArrayUtils.wrap(image[i][3]), ArrayUtils.wrap(pixel[3]))) {
                        if (diff(diff_rgb)) {
                            encodeData.add(qoiOpDiff(diff_rgb));
                        } else {
                            if (luma(diff_rgb)) {
                                encodeData.add(qoiOpLuma(diff_rgb));
                            } else {
                                encodeData.add(qoiOpRGB(image[i]));
                            }
                        }
                    } else {
                        encodeData.add(qoiOpRGBA(image[i]));
                    }
                }
            }
            pixel = image[i];
        }

        byte[] encoding = ArrayUtils.concat(encodeData.toArray(new byte[0][]));
        return encoding;
    }

    public static boolean diff(byte[] diff_rgb) {

        boolean difference = false;

        if ((diff_rgb[0] > -3) && (diff_rgb[0] < 2)
                && (diff_rgb[1] > -3) && (diff_rgb[1] < 2)
                && (diff_rgb[2] > -3) && (diff_rgb[2] < 2)) {
            difference = true;
        }

        return difference;
    }

    public static boolean luma(byte[] diff_rgb) {

        boolean difference_luma = false;

        if ((diff_rgb[1] > -33 && diff_rgb[1] < 32)
                && ((diff_rgb[0] - diff_rgb[1]) > -9)
                && ((diff_rgb[0] - diff_rgb[1]) < 8)
                && ((diff_rgb[2] - diff_rgb[1]) > -9)
                && ((diff_rgb[2] - diff_rgb[1]) < 8)) {
            difference_luma = true;
        }

        return difference_luma;
    }

    public static byte[] qoiFile(Helper.Image image) {
        assert image != null;
        return ArrayUtils.concat(qoiHeader(image), encodeData(ArrayUtils.imageToChannels(image.data())), QOISpecification.QOI_EOF);
    }

}