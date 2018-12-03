package mainPackage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 *
 * @author Eldeeb
 */
public class Encodeer {
    static boolean Compress(int vectorHeight, int vectorWidth, int codeBlockSize, String Path) throws IOException{

        //Read Image
        int[][] image = Image.readImage(Path);

        //Calculate new dimensions to vectorSizes ratio.
        int originalHeight = Image.height;
        int originalWidth  = Image.width;
        int scaledHeight = originalHeight + originalHeight % vectorHeight;
        int scaledWidth  = originalWidth  + originalWidth % vectorWidth;

        //Scale Image (Adding Padding)
        int[][] scaledImage = new int[scaledHeight][scaledWidth];
        for (int i = 0; i < scaledHeight; i++) {
            int x = (i >= originalHeight) ? originalHeight - 1 : i;
            for (int j = 0; j < scaledWidth; j++) {
                int y = (j >= originalWidth) ? originalWidth - 1 : j;
                scaledImage[i][j] = image[x][y];
            }
        }

        //Create Array Of Vectors
        Vector<Vector<Integer>> Vectors = new Vector<>();

        //Divide into Vectors and fill The Array Of Vectors
        for (int i = 0; i < scaledHeight; i+= vectorHeight) {
            for (int j = 0; j < scaledWidth; j+= vectorWidth) {
                Vectors.add(new Vector<>());
                for (int x = i; x < i + vectorHeight; x++) {
                    for (int y = j; y < j + vectorWidth; y++) {
                        Vectors.lastElement().add(scaledImage[x][y]);
                    }
                }
            }
        }

        //Create Array to hold Quantized Vectors
        Vector<Vector<Integer>> Quantized = new Vector<>();

        //Fill Quantized Vector (The recursive part)
        Quantize(codeBlockSize, Vectors, Quantized);

        //Optimize
        Vector<Integer> VectorsToQuantizedIndices = Optimize(Vectors, Quantized);

        //Write using Java's Object Serialization
        FileOutputStream fileOutputStream = new FileOutputStream(getCompressedPath(Path));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        //Write To Compressed File
        objectOutputStream.writeObject(originalWidth);
        objectOutputStream.writeObject(originalHeight);
        objectOutputStream.writeObject(scaledWidth);
        objectOutputStream.writeObject(scaledHeight);
        objectOutputStream.writeObject(vectorWidth);
        objectOutputStream.writeObject(vectorHeight);
        objectOutputStream.writeObject(VectorsToQuantizedIndices);
        objectOutputStream.writeObject(Quantized);
        objectOutputStream.close();

        return true;
    }

    private static void Quantize(int Level, Vector<Vector<Integer>> Vectors, Vector<Vector<Integer>> Quantized){
        if(Vectors.size() == 0)
            return;
        if(Level == 1 ){
            Quantized.add(vectorAverage(Vectors));
            return;
        }
        
        //Split
        Vector<Vector<Integer>> leftVectors = new Vector<>();
        Vector<Vector<Integer>> rightVectors =  new Vector<>();

        //Calculate Average Vector
        Vector<Integer> mean = vectorAverage(Vectors);

        //Calculate Euclidean Distance
        for (Vector<Integer> vec : Vectors ) {
            int eDistance1 = EuclidDistance(vec, mean,  1);
            int eDistance2 = EuclidDistance(vec, mean, -1);
            //Add To Right OR Left Vector
            if(eDistance1 >= eDistance2)
                leftVectors.add(vec);
            else
                rightVectors.add(vec);
        }

        //Recurse
        Quantize(Level / 2, leftVectors, Quantized);
        Quantize(Level / 2, rightVectors, Quantized);
    }
    
    private static Vector<Integer> Optimize(Vector<Vector<Integer>> Vectors, Vector<Vector<Integer>> Quantized){
        Vector<Integer> VectorsToQuantizedIndices = new Vector<>();

        for (Vector<Integer> vector : Vectors ) {
            int smallestDistance = EuclidDistance(vector, Quantized.get(0));
            int smallestIndex = 0;

            //Find the minimum Distance
            for (int i = 1; i < Quantized.size(); i++) {
                int tempDistance = EuclidDistance(vector, Quantized.get(i));
                if(tempDistance < smallestDistance)
                {
                    smallestDistance = tempDistance;
                    smallestIndex = i;
                }
            }

            //Map the i'th Vector to the [i] in Quantized
            VectorsToQuantizedIndices.add(smallestIndex);
        }
        return VectorsToQuantizedIndices;
    }

    static String getCompressedPath(String path){
        return path.substring(0, path.lastIndexOf('.'))+".VQ";
    }
    
    private static Vector<Integer> vectorAverage(Vector<Vector<Integer>> Vectors){
        int[] summation = new int[Vectors.get(0).size()];
        // get the summation of all associated indeces
        for (Vector<Integer> vector : Vectors )
            for (int i = 0; i < vector.size(); i++)
                summation[i] += vector.get(i);
        // divide all summition into number of them
        Vector<Integer> returnVector = new Vector<>();
        for (int i = 0; i < summation.length; i++)
            returnVector.add(summation[i] / Vectors.size());
        // vector with avg of all vectors
        return returnVector;
    }

    private static int EuclidDistance(Vector<Integer> x, Vector<Integer> y, int incrementFactor){
        int distance = 0;
        for (int i = 0; i < x.size(); i++)
            distance += Math.pow(x.get(i) - y.get(i) + incrementFactor, 2);
        return (int) Math.sqrt(distance);
    }
   
    private static int EuclidDistance(Vector<Integer> x, Vector<Integer> y){
        return EuclidDistance(x, y, 0);
    }

}
