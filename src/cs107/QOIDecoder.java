package cs107;

import java.util.ArrayList;

import static cs107.Helper.Image;
import static cs107.Helper.generateImage;

/**
 * "Quite Ok Image" Decoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Third task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder() {
    }

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     *
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header) {
        assert header != null;
        assert header.length == QOISpecification.HEADER_SIZE;
        assert ArrayUtils.toInt(ArrayUtils.extract(header, 0, 4))
                == ArrayUtils.toInt(QOISpecification.QOI_MAGIC);
        assert header[12] == QOISpecification.RGB || header[12] == QOISpecification.RGBA;
        assert header[13] == QOISpecification.ALL || header[13] == QOISpecification.sRGB;

        int[] result = new int[4];
        result[0] = ArrayUtils.toInt(ArrayUtils.extract(header, 4, 4));
        result[1] = ArrayUtils.toInt(ArrayUtils.extract(header, 8, 4));
        result[2] = header[12];
        result[3] = header[13];
        return result;

    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param alpha    (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        assert buffer!=null;
        assert input!=null;
        assert position>=0 && position<buffer.length;
        assert idx>=0 && idx<input.length;
        assert idx+2<input.length;

        buffer[position]=ArrayUtils.concat(ArrayUtils.extract(input,idx,3), ArrayUtils.wrap(alpha));
        return 3;
    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx) {
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert idx + 3 < input.length;

        buffer[position]=ArrayUtils.extract(input,idx,4);
        return 4;
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk         (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk) {
        assert previousPixel != null;
        assert previousPixel.length == 4;
        assert (chunk & QOISpecification.QOI_OP_DIFF_TAG) == QOISpecification.QOI_OP_DIFF_TAG;

        byte red_tag = 0b00110000;
        byte green_tag = 0b00001100;
        byte blue_tag = 0b00000011;

        chunk = (byte) (chunk ^ QOISpecification.QOI_OP_DIFF_TAG);
        byte[] result = new byte[4];

        byte red = (byte) ((chunk & red_tag) >> 4);
        byte green = (byte) ((chunk & green_tag) >> 2);
        byte blue = (byte) ((chunk & blue_tag) >> 0);
        result[0] = (byte) (previousPixel[0] + red - 2);
        result[1] = (byte) (previousPixel[1] + green - 2);
        result[2] = (byte) (previousPixel[2] + blue - 2);
        result[3] = previousPixel[3];

        return result;

    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param data          (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data) {
        assert previousPixel != null && data != null && previousPixel.length == 4;
        assert (data[0] & QOISpecification.QOI_OP_LUMA_TAG) == QOISpecification.QOI_OP_LUMA_TAG;

        byte[] result = new byte[4];
        int vg = (data[0] & 0b00111111) - 32;
        result[0] = (byte) (previousPixel[0] + vg - 8 + ((data[1] >> 4) & 0b00001111));
        result[1] = (byte) (previousPixel[1] + vg);
        result[2] = (byte) (previousPixel[2] + vg - 8 + (data[1] & 0b00001111));
        result[3] = previousPixel[3];
        return result;
    }

    /**
     * Store the given pixel in the buffer multiple times
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param pixel    (byte[]) - The pixel to store
     * @param chunk    (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){

        assert buffer!=null;
        assert pixel!=null;
        assert pixel.length==4;
        assert position>=0 && position<buffer.length;
        for(int i=0; i<buffer.length; i++){
            assert buffer[i].length>=4;
        }

        int count = ((chunk & 0b11111111)& 0b00111111);
        for (int i = 0; i < count+1; i++) {
            buffer[position + i] = pixel;
        }
        return count;
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     *
     * @param data   (byte[]) - Data to decode
     * @param width  (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height){

        assert data!=null;
        assert width>0 && height>0;
        assert data.length>=5;
        byte[] pixel = QOISpecification.START_PIXEL;
        byte[][] hachage = new byte[64][4];
        byte [][] decoding= new byte[width*height][4];
        int idx_decode=0;
        int idx=0;

        while(idx<data.length ){
            if(data[idx]==QOISpecification.QOI_OP_RGBA_TAG){
                idx+=decodeQoiOpRGBA(decoding,data,idx_decode,idx+1);
                idx++;

            }else{
                if(data[idx]==QOISpecification.QOI_OP_RGB_TAG){
                    idx+=decodeQoiOpRGB(decoding,data,pixel[3],idx_decode,idx+1);
                    idx++;

                }else{
                    if((byte) (data[idx]& 0b11_00_00_00) == QOISpecification.QOI_OP_LUMA_TAG){
                        decoding[idx_decode]=decodeQoiOpLuma(pixel
                                ,ArrayUtils.extract(data,idx,2));
                        idx+=2;
                    }else{
                        if((byte) (data[idx]& 0b11_00_00_00) == QOISpecification.QOI_OP_DIFF_TAG){
                            decoding[idx_decode]=decodeQoiOpDiff(pixel,data[idx]);
                            idx++;
                        }else{
                            if((byte) (data[idx]& 0b11_00_00_00) == QOISpecification.QOI_OP_INDEX_TAG){
                                int h = ( data[idx]& 0x3F) ;
                                decoding[idx_decode]=hachage[h];
                                idx++;
                            }else{
                                if((byte) (data[idx]& 0b11_00_00_00) == QOISpecification.QOI_OP_RUN_TAG){
                                    idx_decode+=decodeQoiOpRun(decoding,pixel,data[idx],idx_decode);
                                    idx++;
                                }
                            }
                        }
                    }
                }
            }

            if(!ArrayUtils.equals(decoding[idx_decode],hachage[QOISpecification.hash(decoding[idx_decode])])){
                hachage[QOISpecification.hash(decoding[idx_decode])] = decoding[idx_decode];
            }

            pixel = decoding[idx_decode];
            idx_decode++;

        }
        return decoding;
    }


    /**
     * Decode a file using the "Quite Ok Image" Protocol
     *
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content) {
        assert content != null;
        assert ArrayUtils.equals(ArrayUtils.extract(content, content.length - 8, 8), QOISpecification.QOI_EOF);


        byte[][] data = ArrayUtils.partition(content, QOISpecification.HEADER_SIZE,
                content.length - QOISpecification.HEADER_SIZE - QOISpecification.QOI_EOF.length,
                QOISpecification.QOI_EOF.length);
        int[] header = decodeHeader(data[0]);
        int height = header[1];
        int width = header[0];
        byte channels = (byte) header[2];
        byte colorSpace = (byte) header[3];

        int[][] pixels = ArrayUtils.channelsToImage(decodeData(data[1], width, height), height, width);
        return generateImage(pixels, channels, colorSpace);
    }

}