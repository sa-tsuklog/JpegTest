/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpegtest;

import MyLib.FFT.DCT;
import MyLib.FFT.FftInvalidValueException;
import java.awt.Color;
import java.awt.Graphics2D;
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
public class JpegTest {

    /**
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        
        try {
            BufferedImage source = ImageIO.read(new File("source.jpg"));
            
            JpegEncoder.encode(source);
            
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(JpegTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test(){
        TestBench.dct2dTest();
    }
    
}
