/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris_Package;

/**
 *
 * @author SarahHayden
 */
import java.util.Random;

public class TetroFactory {
    private static final int numShapes = 7;

    public Tetromino getShape(int shapeType) {
        if (shapeType == 0) {
            return new Tetro_I();
        } else if (shapeType == 1) {
            return new Tetro_J();
        } else if (shapeType == 2) {
            return new Tetro_L();
        } else if (shapeType == 3) {
            return new Tetro_O();
        } else if (shapeType == 4) {
            return new Tetro_S();
        } else if (shapeType == 5) {
            return new Tetro_T();
        } else if (shapeType == 6) {
            return new Tetro_Z();
        }

        return null;
    }

    public Tetromino getRandShape() {
        Random r = new Random();
        int i = Math.abs(r.nextInt()) % numShapes; // Random integer from 0 to 6
        return getShape(i);
    }
}