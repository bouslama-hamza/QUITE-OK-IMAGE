package cs107;

/**
 * "Quite Ok Image" Encoder
 * @apiNote Second task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */

import java.util.ArrayList;

public final class QOIEncoder {


    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder(){}

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     * @param image (Helper.Image) - Image to use
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *  (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     */
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

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     * @param pixel (byte[]) - The Pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     */
    public static byte[] qoiOpRGB(byte[] pixel) {
        assert pixel.length == 4;

        return ArrayUtils.concat(
                ArrayUtils.wrap(QOISpecification.QOI_OP_RGB_TAG),
                ArrayUtils.extract(pixel, 0, 3) );
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     * @param pixel (byte[]) - The pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     */
    public static byte[] qoiOpRGBA(byte[] pixel) {
        assert pixel.length == 4;
        return ArrayUtils.concat(ArrayUtils.wrap(QOISpecification.QOI_OP_RGBA_TAG), pixel);
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     * @param index (byte) - Index of the pixel
     * @throws AssertionError if the index is outside the range of all possible indices
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     */
    public static byte[] qoiOpIndex(byte index){
        assert index>=0 && index<64;
        return ArrayUtils.wrap((byte) (QOISpecification.QOI_OP_INDEX_TAG|index));
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpDiff(byte[] diff){

        assert diff!=null;
        assert diff.length==3;
        assert diff[0]>-3 && diff[0]<2;
        assert diff[1]>-3 && diff[1]<2;
        assert diff[2]>-3 && diff[2]<2;

        return new byte[] {(byte) (QOISpecification.QOI_OP_DIFF_TAG | (diff[0] + 2) << 4
                                               | (diff[1] + 2) << 2 | (diff[2] + 2) )    };
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints
     * or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpLuma(byte[] diff){

        assert diff!=null;
        assert diff.length==3;
        assert diff[1]>-33 && diff[1]<32;
        assert (diff[0]-diff[1])>-9 && (diff[0]-diff[1])<8;
        assert (diff[2]-diff[1])>-9 && (diff[2]-diff[1])<8;

        return ArrayUtils.concat((byte) (QOISpecification.QOI_OP_LUMA_TAG | diff[1]+32),
                                  (byte) ((diff[0] - diff[1] + 8)<<4|(byte) (diff[2] - diff[1] + 8)));
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     * @param count (byte) - Number of similar pixels
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     * @return (byte[]) - Encoding of count
     */
    public static byte[] qoiOpRun(byte count){
        assert count>=1 && count<=62;

        return new byte[] {(byte) (QOISpecification.QOI_OP_RUN_TAG | count-1)};
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
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

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     *
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     */

    public static byte[] qoiFile(Helper.Image image) {
        assert image != null;
        return ArrayUtils.concat(qoiHeader(image), encodeData(ArrayUtils.imageToChannels(image.data())), QOISpecification.QOI_EOF);
    }

}