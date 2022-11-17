package cs107;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public final class Helper {

    private static final String res_folder = "res";

    static {
        var file = new File(res_folder);
        if(file.exists()){
            if (!file.isDirectory()){
                fail("File %s is not a directory.", res_folder);
            }
        }else{
            var b = file.mkdir();
            if(!b)
                fail("Cannot create directory '%s'", res_folder);
        }
    }

    private Helper(){}

    public record Image(int[][] data, byte channels, byte color_space){
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Image im){
                return Arrays.deepEquals(data, im.data) && (channels == im.channels) && (color_space == im.color_space);
            }else
                return false;
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(data);
        }
    }

    // ==================================================================================
    // ========================== IMAGE MANIPULATION METHODS ============================
    // ==================================================================================

    public static Image generateImage(int[][] data, byte channels, byte colorSpace){
        assert data != null;
        assert data.length > 0;
        assert data[0] != null;
        var width = data[0].length;
        assert width > 0;
        for (var p : data){
            assert p != null;
            assert p.length == width;
        }
        return new Image(data, channels, colorSpace);
    }

    public static Image readImage(String path) {
        try{
            var io = ImageIO.read(new File(path));
            var width  = io.getWidth();
            var height = io.getHeight();
            var array = new int[height][width];
            for(var x = 0; x < height;++x){
                for(var y = 0 ;y < width; ++y){
                    array[x][y] = io.getRGB(y, x);
                }
            }
            var nbrChannels = (byte) (io.getColorModel().hasAlpha() ? 4 : 3);
            return new Image(array, nbrChannels, (byte) 0);
        }catch (IOException e){
            return fail("An error occurred while trying to read from : \"%s\"%n", path);
        }

    }

    public static void writeImage(String path, Image image) {
        int type = switch (image.channels){
            case 3 -> BufferedImage.TYPE_3BYTE_BGR;
            case 4 -> BufferedImage.TYPE_4BYTE_ABGR;
            default -> fail("Cannot write this image, image.channels() == %d", image.channels);
        };
        var buffer = new BufferedImage(image.data[0].length, image.data.length, type);
        for(var x = 0; x < buffer.getHeight(); ++x){
            for(var y = 0 ; y < buffer.getWidth(); ++y){
                buffer.setRGB(y, x, image.data[x][y]);
            }
        }
        var abs_path = res_folder + File.separator + path;
        try {
            ImageIO.write(buffer, "png", new File(abs_path));
        }catch (IOException e){
            fail("An error occurred while trying to write to : \"%s\"%n", abs_path);
        }
    }

    // ==================================================================================
    // ======================== BINARY FILE MANIPULATION METHODS ========================
    // ==================================================================================

    public static byte[] read(String path) {
        try(var input = new FileInputStream(path)){
            return input.readAllBytes();
        } catch (IOException e){
            return fail("An error occurred while trying to read from : \"%s\"%n", path);
        }
    }

    public static void write(String path, byte[] content){
        var abs_path = res_folder + File.separator + path;
        try(var output = new FileOutputStream(abs_path)){
            //for (var b : content){
                //output.write(b);
            //}
            output.write(content);

        }catch (IOException e){
            fail("An error occurred while trying to write to : \"%s\"%n", abs_path);
        }
    }

    // ==================================================================================
    // ============================= ERROR MANAGEMENT METHODS ===========================
    // ==================================================================================

    public static <T> T fail(String fmt, Object ... params){
        throw new RuntimeException(String.format(fmt, params));
    }

}