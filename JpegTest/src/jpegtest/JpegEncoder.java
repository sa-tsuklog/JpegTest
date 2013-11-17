/*
 * To change this template, choose Tools } Templates
 * and open the template in the editor.
 */
package jpegtest;

import MyLib.FFT.DCT;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author sa
 */
public class JpegEncoder {
    public static final int BLOCKSIZE = 8;
    
    public static final int[][] QUANTIZATION_TABLE_Y = {
        { 16,  11,  10,  16,  24,  40,  51,  61 },
        { 12,  12,  14,  19,  26,  58,  60,  55 },
        { 14,  13,  16,  24,  40,  57,  69,  56 },
        { 14,  17,  22,  29,  51,  87,  80,  62 },
        { 18,  22,  37,  56,  68, 109, 103,  77 },
        { 24,  35,  55,  64,  81, 104, 113,  92 },
        { 49,  64,  78,  87, 103, 121, 120, 101 },
        { 72,  92,  95,  98, 112, 100, 103,  99 }
    };
    public static final int[][] QUANTIZATION_TABLE_UV = {
        { 17 ,  18 ,  24 ,  47 ,  99 ,  99 ,  99 ,  99 , },
        { 18 ,  21 ,  26 ,  66 ,  99 ,  99 ,  99 ,  99 , },
        { 24 ,  26 ,  56 ,  99 ,  99 ,  99 ,  99 ,  99 , },
        { 47 ,  66 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 , },
        { 99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 , },
        { 99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 , },
        { 99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 , },
        { 99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 ,  99 , },
    };
    
    
    
    public static void encode(BufferedImage source){
        try {
            double[][][] yuvArray = imageToYuvArray(source);
            
            double[][] yBlock;
            
            double[][] yRestoredArray = new double[source.getWidth()][source.getHeight()];
            
            for (int i = 0; i < source.getWidth(); i+=BLOCKSIZE) {
                for (int j = 0; j < source.getHeight(); j+=BLOCKSIZE) {
                    yBlock = getBlock(yuvArray, i, j, 0, JpegEncoder.BLOCKSIZE,1);
                    double[][] spec = DCT.dct2d(yBlock);

                    double[][] quantized = quantize(spec, QUANTIZATION_TABLE_Y, 2);
                    double[][] dequantized = dequantize(quantized, QUANTIZATION_TABLE_Y, 2);


                    double[][] restore = DCT.idct2d(dequantized);
                    
                    for (int k = 0; k < BLOCKSIZE; k++) {
                        for(int l=0;l<BLOCKSIZE;l++){
                            yRestoredArray[i+k][j+l] = restore[k][l];
                        }
                    }
                    
                }
            }
            
            
            
            BufferedImage out = ArrayToGrayscaleImage(yRestoredArray, 0);
            
            
            
            ImageIO.write((RenderedImage)out,"bmp",new File("output.bmp"));
        } catch (IOException ex) {
            Logger.getLogger(JpegEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    static double[][] quantize(double[][] spec,int[][] quantizationTable,double quality){
        double[][] quantizedSpec = new double[BLOCKSIZE][BLOCKSIZE];
        for (int i = 0; i < BLOCKSIZE; i++) {
            for (int j = 0; j < BLOCKSIZE; j++) {
                quantizedSpec[i][j] = (int)(spec[i][j]/(quantizationTable[i][j]/quality));
            }
        }
        return quantizedSpec;
    }
    static double[][] dequantize(double[][] spec,int[][] quantizationTable,double quality){
        double[][] dequantizedSpec = new double[BLOCKSIZE][BLOCKSIZE];
        for (int i = 0; i < BLOCKSIZE; i++) {
            for (int j = 0; j < BLOCKSIZE; j++) {
                dequantizedSpec[i][j] = (int)(spec[i][j]*(quantizationTable[i][j]/quality));
            }
        }
        return dequantizedSpec;
    }
    
    
    static double[] rgbIntToRgbArray(int rgb){
        int r = (rgb&0x00FF0000)>>16;
        int g = (rgb&0x0000FF00)>>8;
        int b = (rgb&0x000000FF)>>0;
        
        return new double[] {r,g,b};
    }
    static int rgbArrayToRgbInt(double[] rgb){
        for (int i = 0; i < rgb.length; i++) {
            if(rgb[i]<0){
                rgb[i]=0.0;
            }else if(rgb[i]>255){
                rgb[i]=255;
            }
        }
        
        int r = (int)rgb[0];
        int g = (int)rgb[1];
        int b = (int)rgb[2];
        
        return r<<16 | g <<8 | b ;
        
    }
    
    //returns double[y,u,v][x][y]. 0<= y <=255, -128 <=u<127, -128<=v<127.
    static double[][][] imageToYuvArray(BufferedImage im){
        double[][][] rgbArray = new double[3][im.getWidth()][im.getHeight()];
        
        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
                
                double[] rgb = rgbIntToRgbArray(im.getRGB(i, j));
                for (int k = 0; k < 3; k++) {
                    rgbArray[k][i][j] = rgb[k];
                }
            }
        }
        
        
        double[][][] yuvArray = new double[3][im.getWidth()][im.getHeight()];
        
        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
                double r = rgbArray[0][i][j];
                double g = rgbArray[1][i][j];
                double b = rgbArray[2][i][j];
                
                double[] yuv = JpegEncoder.rgbToYuv(new double[]{r,g,b});
                
                yuvArray[0][i][j] = yuv[0];
                yuvArray[1][i][j] = yuv[1];
                yuvArray[2][i][j] = yuv[2];
            }
        }
        
        return yuvArray;
    }
    static double[][] getBlock(double[][][] yuvArray,int topLeftX,int topLeftY, int yuvSelect,int blockSize,int decimation){
        if(decimation<1){
            decimation = 1;
        }
        
        double[][] block = new double[blockSize][blockSize];
        
        for (int x = 0; x < blockSize; x++) {
            for (int y = 0; y < blockSize; y++) {
                try{
                    block[x][y] = yuvArray[yuvSelect][topLeftX+x*decimation][topLeftY+y*decimation];
                }catch(ArrayIndexOutOfBoundsException e){
                    block[x][y] = 0.0;
                }
            }
        }
        
        return block;
    }
    
    //intensityOffset = 0 for Y, intensityOffset = 128 for U and V
    static BufferedImage ArrayToGrayscaleImage(double[][] imageArray,double intensityOffset){
        BufferedImage im = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                int grayscale = (int)(imageArray[i][j]+intensityOffset);
                im.setRGB(i, j, rgbArrayToRgbInt(new double[]{grayscale,grayscale,grayscale}));
            }
        }
        
        return im;
    }
    
    static double[] rgbToYuv(double[] rgb){
        double r=rgb[0];
        double g=rgb[1];
        double b=rgb[2];
        
        double y =  0.2990*r + 0.5870*g + 0.1140*b;
        double u = -0.1684*r - 0.3316*g + 0.5000*b;
        double v =  0.5000*r - 0.4187*g - 0.0813*b;
        
        return new double[] {y,u,v};
    }
}
