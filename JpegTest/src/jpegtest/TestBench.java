/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpegtest;

import MyLib.FFT.DCT;
import MyLib.FFT.FftInvalidValueException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sa
 */
public class TestBench {
    TestBench(){
        
    }
    static void dct2dTest(){
        double[][] data = new double[][]{
            {126, 138, 135, 118, 118, 126, 126, 130},
            {150, 168, 161, 122, 105, 109, 100, 118},
            {150, 150, 126, 150, 142, 126, 126, 117},
            {150, 161, 168, 130, 134, 150, 138, 130},
            {130, 118, 134, 142, 157, 142, 117, 126},
            {115, 117, 108, 117, 101,  99, 117, 126},
            {122, 130, 130, 138, 117, 108, 108, 138},
            {142, 118, 134, 117, 109,  91, 126, 109}
        };
        DCT dct = new DCT();

        double[][] spec = dct.dct2d(data);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf("%04.0f,",spec[i][j]);
            }
            System.out.println("");
        }
        System.out.println("--------------");
        double[][] restored = dct.idct2d(spec);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf("%04.0f,",restored[i][j]);
            }
            System.out.println("");
        }
    }
}
