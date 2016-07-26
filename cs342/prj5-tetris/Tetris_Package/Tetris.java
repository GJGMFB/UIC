/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris_Package;
import java.util.*;
import java.math.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

// https://github.com/shayde2/cs342_proj5.git

/**
 * @author DennisLeancu
 * @author SarahHayden
 */
public class Tetris extends JPanel {
    int score;
    int curLevel;
    int linesCleared;
        
    Tetromino curTetro;            // Tetro currently in play
    Tetromino nextTetro;            // Tetro up next
    
    public int rows;
    public int columns;
    int squareSize;    
    public Square[][] grid;
    boolean isMoveOver, gameOver;
    Point curPos;

    
    public Tetris() {
        rows = 20;
        columns = 10;
        squareSize = 30;
        // so we see the grid in full always
        setPreferredSize(new Dimension(columns*squareSize, rows*squareSize));
        grid = new Square[columns][rows];       
        curPos = new Point();
        curTetro = new Tetromino();
        
        score = 0;
        curLevel = 1;
        isMoveOver = false;
        gameOver = false;
        linesCleared = 0;
        init_Grid();
        
    }

    public void restart() {
        rows = 20;
        columns = 10;
        squareSize = 30;
        // so we see the grid in full always
        setPreferredSize(new Dimension(columns*squareSize, rows*squareSize));
        grid = new Square[columns][rows];
        curPos = new Point();
        curTetro = new Tetromino();

        score = 0;
        curLevel = 1;
        isMoveOver = false;
        gameOver = false;
        linesCleared = 0;
        init_Grid();
    }

    void init_Grid() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new Square();
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x;
        int y;
        
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                x = i*squareSize;
                y = j*squareSize;
                g.setColor(grid[i][j].color);
                g.fill3DRect(x, y, squareSize, squareSize, true);
                
                if (grid[i][j].color==Color.BLACK) {
                    g.setColor(Color.GRAY.darker().darker());
                    g.drawLine(x, y, x, y + squareSize);
                    g.drawLine(x, y, x + squareSize, y);
                 }
            }
        }
        
        // Paint the current tetro in play
        ArrayList<Point> blocks = curTetro.getBlockArray();
        Point tempPos = curPos;
        g.setColor(curTetro.getColor());
        
        // Loop through tetro block array
        for (Point p : blocks) {
            x = tempPos.x + p.x;
            y = tempPos.y - p.y;    
            g.fill3DRect(x*squareSize, y*squareSize, squareSize, squareSize, true);
        }
        
    }

    
    void moveDown() {
        if (canMoveDown()) {
            moveDownLine();
        } else {
            isMoveOver = true;
            drawToBoard();
            clearRows();
            // moved this to UI so UI can set 
            // nextTetro and new thumbnail tetro first
//            beginNextMove();  
        }
    }
    
    
    private void drawToBoard() {
        // Store current tetro in board
        ArrayList<Point> blocks = curTetro.getBlockArray();
        Point tempPos = curPos;

        // Loop through tetro block array
        for (Point p : blocks) {
            int x = tempPos.x + p.x;
            int y = tempPos.y - p.y;

            grid[x][y].color = curTetro.getColor();
            grid[x][y].occupied = true;
        }
    }
    
    private boolean canMoveDown() {
        ArrayList<Point> blocks = curTetro.getBlockArray();
        Point tempPos = curPos;
        for (Point p : blocks) {
            int x = tempPos.x + p.x;
            int y = tempPos.y - p.y;

            // Ignore if above board
            if (y < 0) {
                continue;
            }
            // Check if this block is below the board
            if (y+1 >= rows) {
                return false;
            }
            // Check if this block overlaps with corresponding block on the board below it
            if (grid[x][y+1].occupied) {
                if (y <= 0) {
                    gameOver = true;
                }
                return false;
            }
        }
        return true;
    }


/**
 * Section for how the UI interacts with the current Tetro.
 */    
    void moveDownLine() {       
        curPos.y += 1;
    }    
    void moveLeft() {
        if (!doesCollide(-1, 0)) {
            curTetro.move_Left();   
            curPos.x -= 1;
        }
    }
    void moveRight() {
        if (!doesCollide(1, 0)) {
            curTetro.move_Right();  
            curPos.x += 1;
        }
    }
    void rotateRight(){
        curTetro.rotate_Right();
        if (doesCollide(0, 0)) {
            curTetro.rotate_Left();
        }
    }   
    void rotateLeft(){
        curTetro.rotate_Left();
        if (doesCollide(0, 0)) {
            curTetro.rotate_Right();
        }
    } 
    boolean softDrop() {
        if (canMoveDown()) {
            curTetro.drop();    
            curPos.y += 1;
            return true;
        }
        return false;
    }
    void hardDrop() {
        while (softDrop()) {    // :)
            // Hahahahaha
        }
    }
/** End of UI ActionListener Tetro interaction section. */   
    
    
    
    /**
     * Collision Checking
     * @param dx
     * @param dy
     * @return 
     */
    private boolean doesCollide(int dx, int dy) {
        ArrayList<Point> blocks = curTetro.getBlockArray();
        Point tempPos = curPos;

        for (Point p : blocks) {
            int x = tempPos.x + p.x;
            int y = tempPos.y - p.y;

            // Check if out of bounds
            // Above board
            if (y+dy <= -1) {
                return true;
            }
            // Below board
            if (y+dy+1 >= rows) {
                return true;
            }
            // Outside left and right boundaries
            if (x+dx < 0 || x+dx >= columns) {
                return true;
            }
            if (grid[x+dx][y+dy].occupied) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates and returns the next Tetro to display in a window in the 
     * TetrisUI frame window. The tetro returned to UI becomes thumbnail
     * tetro, which becomes the nextTetro to drop in the game. 
     * @return 
     */
    public Tetromino get_nextTetro(){
        TetroFactory tetroFactory = new TetroFactory();
        return tetroFactory.getRandShape();
    }
    
    /**
     * Calling function moves the next Tetro out of the thumbnail and into
     * nextTetro. This function pulls nextTetro into curTetro and places
     * it in the board area.
     */
    void beginNextMove() {
        isMoveOver = false;
//        TetroFactory tetroFactory = new TetroFactory();
        curTetro = nextTetro;
        curPos.x = (columns) / 2;
        curPos.y = 0;
    }
    
       
    
    // start_Game and pause_Game are now
    // in the TetrisUI part :)
    void start_Game(){
//        beginNextMove();
//        tetroTimer.start();
    }   
    void pause_Game(){
//        if (tetroTimer.isRunning())
//            tetroTimer.stop();  
//        else 
//            tetroTimer.start();
    } 
    
    
    private void clearRows() {
        int numCleared = 0;
        boolean isRowFull;

        for (int i = 0; i < rows; i++) {
            isRowFull = true;

            for (int j = 0; j < columns; j++) {
                // Stop checking this row if the check fails
                if (isRowFull) {
                    // Check if this square is filled
                    if (!grid[j][i].occupied) {
                        isRowFull = false;
                    }
                } else {
                    break;
                }
            }

            // Increment numCleared and clear the row
            if (isRowFull) {
                numCleared++;

                // Move everything above this row down
                dropChunkDown(i);
            }
        }
        set_Score(numCleared);
        linesCleared+=numCleared;
    }

    /**
     * Adds to score of the game depending on how many rows were 
     * successfully cleared in that turn.
     * @param numCleared the number of lines cleared
     */
    void set_Score(int numCleared){
        if (numCleared == 1) {
            score += 40 * curLevel;
        } else if (numCleared == 2) {
            score += 100 * curLevel;
        } else if (numCleared == 3) {
            score += 300 * curLevel;
        } else if (numCleared == 4) {
            score += 1200 * curLevel;
        }        
    }
    
    /**
     * Given a row number, drop it and everything above it, down by one
     * @param row 
     */
    private void dropChunkDown(int row) {
        for (int i = row; i >= 0; i--) {
            for (int j = 0; j < columns; j++) {
                // First row is unique because there is no space above it
                if (i == 0) {
                    grid[j][i].color = Color.BLACK;
                    grid[j][i].occupied = false;
                } else {
                    grid[j][i].color = grid[j][i - 1].color;
                    grid[j][i].occupied = grid[j][i - 1].occupied;
                }
            }
        }
    }

    /**
     * Increases the level of the game each time 10 lines are cleared.
     */
    public void level_Up(){
        curLevel++;
        if (curLevel <= 24)
            ;
//            tetroTimer.setDelay(calc_Delay());
    }
    
    /**
     * Calculate the delay that will occur each new Tetromino drop
     * @return 
     */
    int calc_Delay(){
        return ((50-2*curLevel)/60*1000);
    }


    

    
    
    /**
     * Inner class for grid
     */
    public class Square {
        Color color;
        boolean occupied;

        Square() {
            color = Color.BLACK;
            occupied = false;
        }
    }
    
}
