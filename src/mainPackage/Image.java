package mainPackage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Image {
    public static int height;
    public static int width;
    
    public static int[][] readImage(String path) {
        // return matrix of the original image
        BufferedImage img;
        try {
            img = ImageIO.read(new File(path));
            height = img.getHeight();
            width = img.getWidth();
            int[][] imagePixels= new int[height][width];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color pixel = new Color(img.getRGB(x, y));
                    int red =pixel.getRed();
                    imagePixels[y][x] = red;
                }
            }
            return imagePixels;
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage getBufferedImage(int[][] imagePixels, int width, int height) {
        // return image from a given matrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = imagePixels[y][x] ;
                Color c = new Color(value , value , value , 255);
                image.setRGB(x, y,c.getRGB());
            }
        }
        return image;
    }

    public static void writeImage(int[][] imagePixels, int width, int height, String outPath) {
        BufferedImage image = getBufferedImage(imagePixels, width, height);
        File ImageFile = new File(outPath);
        try {
            ImageIO.write(image, "jpg", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
