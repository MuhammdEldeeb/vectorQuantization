package mainPackage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Vector;

/**
 *
 * @author Eldeeb
 */
public class Decoder {
    static boolean Decompress(String Path) throws IOException, ClassNotFoundException {

        InputStream file = new FileInputStream(Path);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        //Read Saved Tags
        int width = (int) input.readObject();
        int height = (int) input.readObject();
        int scaledWidth = (int) input.readObject();
        int scaledHeight = (int) input.readObject();
        int vectorWidth = (int) input.readObject();
        int vectorHeight = (int) input.readObject();
        Vector<Integer> VectorsToOptimizeIndices = (Vector<Integer>)input.readObject();
        Vector<Vector<Integer>> Quantized = (Vector<Vector<Integer>>) input.readObject();


        //retrive the image
        int[][] newImg = new int[scaledHeight][scaledWidth];

        //MAP
        for (int i = 0; i < VectorsToOptimizeIndices.size(); i++) {
            int x = i / (scaledWidth / vectorWidth);
            int y = i % (scaledWidth / vectorWidth);
            x *= vectorHeight;
            y *= vectorWidth;
            int v = 0;
            for (int j = x; j < x + vectorHeight; j++) {
                for (int k = y; k < y + vectorWidth; k++) {
                    newImg[j][k] = Quantized.get(VectorsToOptimizeIndices.get(i)).get(v++);
                }
            }
        }

        //Write image with Original Width/Height
        Image.writeImage(newImg, width, height, getDecompressedPath(Path));

        return true;
    }
    
    static String getDecompressedPath(String path)    {
        return path.substring(0,path.lastIndexOf('.')) + "_Compressed.jpg";
    }
}
