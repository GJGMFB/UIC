/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris_Package;

import java.awt.*;
import java.util.*;

/**
 *  Tetromino - the base class that each individual Tetromino 
 *  piece will derive from.  
 *  - pivotPoint is the point in which we rotate around
 *  - tetroSquares are the 4 squares in the grid that the tetromino occupies
 *  Implements methods in TetrominoMoves.
 */
public class Tetromino implements TetrominoMoves 
{
    int dx, dy;
    Point pivotPoint;
    ArrayList<Point> tetroSquares;
    Color tetroColor;
    
    public Tetromino() {
        dx = 2;  dy = 1;
        tetroSquares = new ArrayList<>(4);
        pivotPoint = new Point(0,0);
        tetroColor = Color.BLACK;
    }
    
    @Override
    public void rotate_Left() {
        int offset = pivotPoint.x;
        for (Point p : tetroSquares){
            int x = -1*p.y+offset;
            int y = p.x-offset; 
            p.x = x;  p.y = y;
        }       
    }

    @Override
    public void rotate_Right() {
        int offset = pivotPoint.x;
        for (Point p : tetroSquares){
            int x = p.y + offset;
            int y = -1*p.x + offset; 
            p.x = x;  p.y = y;
        }   
    }

    @Override
    public void move_Left() { 
        dx--;   
        for (Point p : tetroSquares){
            p.setLocation(p.x--, p.y);
        }
    }

    @Override
    public void move_Right() {       
        dx++;
        for (Point p : tetroSquares){
            p.setLocation(p.x++, p.y);
        }
    }

    @Override
    public void drop() {
        dy++;
        for (Point p : tetroSquares){
            p.setLocation(p.x, p.y++);
        }
    }

    @Override
    public Point getPivot() {
        return pivotPoint;
    }

    @Override
    public Color getColor() {
        return tetroColor;
    }

    @Override
    public ArrayList<Point> getBlockArray() {
        return tetroSquares;
    }
    
    
    
    
} // end Tetromino class


class Tetro_I extends Tetromino {
    
    public Tetro_I() {
        super();
        tetroSquares.add(pivotPoint);
        tetroSquares.add(new Point(-2,0));
        tetroSquares.add(new Point(-1,0));
        tetroSquares.add(new Point(1,0));
        tetroColor = Color.CYAN;
        
    }

    @Override
    public void rotate_Left() {
        for (Point p : tetroSquares){
            int x = p.y, y = p.x; 
            p.x = x;  p.y = y;
        }        
    }

    @Override
    public void rotate_Right() {
        this.rotate_Left();
    }
  
}

class Tetro_T extends Tetromino {
    
    public Tetro_T() {
        super();
        tetroSquares.add(pivotPoint);
        tetroSquares.add(new Point(0,-1));
        tetroSquares.add(new Point(1,0));        
        tetroSquares.add(new Point(-1,0));
        tetroColor = Color.YELLOW;
    }

    @Override
    public void rotate_Left() {
        super.rotate_Left();
    }

    @Override
    public void rotate_Right() {
        super.rotate_Right();
    }
  
}

class Tetro_O extends Tetromino {
    
    public Tetro_O() {
        super();
        tetroSquares.add(pivotPoint);
        tetroSquares.add(new Point(-1,0));
        tetroSquares.add(new Point(-1,-1));
        tetroSquares.add(new Point(0,-1));
        tetroColor = Color.MAGENTA;
    }

    @Override
    public void rotate_Left() {
        return;
    }

    @Override
    public void rotate_Right() {
        return;
    }
  
}

class Tetro_L extends Tetromino {
    
    public Tetro_L() {
        super();
        tetroSquares.add(pivotPoint);        
        tetroSquares.add(new Point(-1,0));
        tetroSquares.add(new Point(-1,-1));
        tetroSquares.add(new Point(1,0));
        tetroColor = Color.GREEN;
    }

    @Override
    public void rotate_Left() {
        super.rotate_Left();
    }

    @Override
    public void rotate_Right() {
        super.rotate_Right();
    }
  
}

class Tetro_S extends Tetromino {
    
    public Tetro_S() {
        super();
        this.pivotPoint = new Point(0,0);
        tetroSquares.add(pivotPoint);        
        tetroSquares.add(new Point(0,-1));
        tetroSquares.add(new Point(1,0));
        tetroSquares.add(new Point(-1,-1));
        tetroColor = Color.RED;
    }

    @Override
    public void rotate_Left() {
        super.rotate_Left();
    }

    @Override
    public void rotate_Right() {
        super.rotate_Right();
    }
    
//    @Override
//    public void move_Left(){
//        super.move_Left();
//    }
    
//    @Override
//    public void move_Right(){
//        super.move_Right();
//    }
  
}

class Tetro_J extends Tetromino {
    
    public Tetro_J() {
        super();
        tetroSquares.add(pivotPoint);        
        tetroSquares.add(new Point(-1,0));
        tetroSquares.add(new Point(1,-1));
        tetroSquares.add(new Point(1,0));
        tetroColor = Color.ORANGE;
    }

    @Override
    public void rotate_Left() {
        super.rotate_Left();
    }

    @Override
    public void rotate_Right() {
        super.rotate_Right();
    }
  
}

class Tetro_Z extends Tetromino {
    
    public Tetro_Z() {
        super();
        tetroSquares.add(pivotPoint);        
        tetroSquares.add(new Point(-1,0));
        tetroSquares.add(new Point(1,-1));
        tetroSquares.add(new Point(0,-1));
        tetroColor = new Color(132, 102, 214);
    }

    @Override
    public void rotate_Left() {
        super.rotate_Left();
    }

    @Override
    public void rotate_Right() {
        super.rotate_Right();
    }
  
}


interface TetrominoMoves {
    
    void rotate_Left();
    
    void rotate_Right();
    
    void move_Left();
    
    void move_Right();
    
    void drop();
    
    Point getPivot();
    
    ArrayList<Point> getBlockArray();
    
    Color getColor();
    
}