package cs107;

import static cs107.Helper.Image;
import static cs107.Helper.generateImage;

public final class QOIDecoder {

    private QOIDecoder() {
    }

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

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

    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        assert buffer!=null;
        assert input!=null;
        assert position>=0 && position<buffer.length;
        assert idx>=0 && idx<input.length;
        assert idx+2<input.length;

        buffer[position]=ArrayUtils.concat(ArrayUtils.extract(input,idx,3), ArrayUtils.wrap(alpha));
        return 3;
    }

    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx) {
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert idx + 3 < input.length;

        buffer[position]=ArrayUtils.extract(input,idx,4);
        return 4;
    }

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