/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris_Package;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author SarahHayden
 * @author DennisLeancu
 */

public class TetrisUI extends JFrame 
{
    Tetris board;               // extends JPanel - game board
    NextTetroPanel thumbnail;   // extends JPanel - shows next Tetro to drop
    JPanel gameStatsPanel;  // elapsed time, score, lines cleared, etc.
    JPanel btnPanel;        // buttons for moving Tetro piece
    static Timer elapsedTime; 
    javax.swing.Timer tetroTimer;   // timer between new Tetro drop
    
    long timeStarted, timeEnded, timePaused;
    JLabel timerText, scoreText, levelText, linesClearedText;
    JButton moveLeftBtn, moveRightBtn, rotateLeftBtn, rotateRightBtn, 
            dropSoftBtn, dropHardBtn, newTetroBtn;
    JMenuItem startMenuItem, pauseMenuItem;
    
    public TetrisUI() {
        super("Tetris");
        
        setLayout(new BorderLayout());
        getContentPane().setForeground(Color.BLACK);
        
        init_Components();
        init_Menus();
        init_Timers();
        
        setPreferredSize(new Dimension(600,680));      
//        setResizable(true);   
        
        addKeyListener(new TetrisKeyAdapter());        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }
    
    
    void init_Components() {
        // board goes in boardPanel to make the layout prettier
        board = new Tetris();   // 20x10 tetris board       
        JPanel boardPanel = new JPanel();//new BorderLayout());
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.PAGE_AXIS));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        boardPanel.add(board);
                  
        init_GameStats(); // panel for game clock, score, level, etc.
        
        // at start of game, thumbnail window should be empty
        thumbnail = new NextTetroPanel();           
        thumbnail.tetro = board.get_nextTetro();
        
        init_btnPanel();          
        JPanel rightPanel = new JPanel();
        init_rightPanel(rightPanel);
                
        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.add(gameStatsPanel);
        box.add(btnPanel);
        box.setFocusable(false);
        rightPanel.add(box);
        rightPanel.setPreferredSize(new Dimension(300,650));
                
        // add each panel to the frame
        getContentPane().add(boardPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.CENTER);    
        pack();     
    }
    
    /**
     * Initializes right panel (thumbnail tetro etc).
     */
    void init_rightPanel(JPanel p) {
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        newTetroBtn = new JButton("New Tetro");
        newTetroBtn.setFocusable(false);
        newTetroBtn.addMouseListener(new TetrisMouseAdapter());
        thumbnail.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Box b = new Box(BoxLayout.PAGE_AXIS);
        b.add(thumbnail);
//        b.add(newTetroBtn);
        p.add(b);
       
    }

    /**
     * Initializes buttons to move Tetro around on the board. 
     */
    void init_btnPanel() {
        btnPanel = new JPanel(new GridLayout(3,2));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        moveLeftBtn = new JButton();
        moveLeftBtn.setIcon(new ImageIcon(getClass()
                .getResource("/images/moveLeft.png")));    
        moveRightBtn = new JButton();
        moveRightBtn.setIcon(new ImageIcon(getClass()
                .getResource("/images/moveRight.png")));  
        rotateLeftBtn = new JButton();        
        rotateLeftBtn.setIcon(new ImageIcon(getClass()
                .getResource("/images/rotateLeft.png")));
        
        rotateRightBtn = new JButton();
        rotateRightBtn.setIcon(new ImageIcon(getClass()
                .getResource("/images/rotateRight.png")));    
        
        dropSoftBtn = new JButton("Drop 1");
        dropHardBtn = new JButton("DROP");
        
        // row 0
//        btnPanel.add(new JLabel());
        btnPanel.add(rotateLeftBtn);
        rotateLeftBtn.setFocusable(false);
        rotateLeftBtn.addMouseListener(new TetrisMouseAdapter());   

        btnPanel.add(rotateRightBtn);  
        rotateRightBtn.setFocusable(false);
        rotateRightBtn.addMouseListener(new TetrisMouseAdapter());
               
//        btnPanel.add(new JLabel());
        
        // row 1
        btnPanel.add(moveLeftBtn);
        moveLeftBtn.setFocusable(false);
        moveLeftBtn.addMouseListener(new TetrisMouseAdapter());
        
        btnPanel.add(moveRightBtn);
        moveRightBtn.setFocusable(false);
        moveRightBtn.addMouseListener(new TetrisMouseAdapter());
        
        // row 2
//        btnPanel.add(new JLabel());
        btnPanel.add(dropHardBtn);
        dropHardBtn.setFocusable(false);
        dropHardBtn.addMouseListener(new TetrisMouseAdapter());
        
        btnPanel.add(dropSoftBtn);
        dropSoftBtn.setFocusable(false);
        dropSoftBtn.addMouseListener(new TetrisMouseAdapter());

//        btnPanel.add(new JLabel());
        
    }
    
    /**
     * Initializes Game Stats Panel with Elapsed Time, Score, Level, and
     * Lines Cleared.
     */
    void init_GameStats(){
        
        gameStatsPanel = new JPanel(new GridLayout(0,2,0,5));
        gameStatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(10,10,10,10,Color.BLACK), 
                BorderFactory.createEmptyBorder(0,10,0,0)));
        gameStatsPanel.add(new JLabel("Elapsed Time: "));
        timerText = new JLabel("0"); 
        gameStatsPanel.add(timerText);       
        gameStatsPanel.add(new JLabel("Score: "));
        scoreText = new JLabel("0");
        gameStatsPanel.add(scoreText);
        gameStatsPanel.add(new JLabel("Level: "));
        levelText = new JLabel("0");
        gameStatsPanel.add(levelText);
        gameStatsPanel.add(new JLabel("Lines Cleared: "));
        linesClearedText = new JLabel("0");
        gameStatsPanel.add(linesClearedText);
        
    }
    

    /**
     * Initializes Menu Bar/Menu Items. Self explanatory.
     */
    void init_Menus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        
        // New Game
        JMenuItem item = new JMenuItem("New Game");
        item.addActionListener((ActionEvent)-> {
            board.restart();
            elapsedTime.restart();
            pauseMenuItem.setEnabled(false);
            startMenuItem.setEnabled(true);
           /*   1. stop game (if in progress)
                2. clear board for new game but do not begin yet
            */
        });
        menu.add(item);
        
        // Start
        menu.addSeparator();
        startMenuItem = new JMenuItem("Start"); // add icon        
        startMenuItem.addActionListener((ActionEvent)-> {
            begin_Game();
        });
        menu.add(startMenuItem);
        
        
        // Pause/Unpause
        pauseMenuItem = new JMenuItem("Pause"); // add icon
        pauseMenuItem.setEnabled(false);
        pauseMenuItem.addActionListener((ActionEvent)-> {
            pause_Game();
        });
        menu.add(pauseMenuItem);
        
        // About
        menu.addSeparator();
        item = new JMenuItem("About");
        item.addActionListener((ActionEvent)-> {
            String msg = "Developer Names:\tSarah Hayden and Dennis Leancu\n"
                    + "Project Information:\tCS 342 Project #5, UIC Spring 2016\n";                  
            JOptionPane.showMessageDialog(null, msg, "About the Game", 
                    JOptionPane.INFORMATION_MESSAGE);
        });
        menu.add(item);
        
        // Help
        item = new JMenuItem("Help");
        item.addActionListener((ActionEvent)-> {
            String msg = 
            "Tetris is a is a tile-matching puzzle video game, originally designed\n"
            + " and programmed by Russian game designer Alexey Pajitnov.\n\n"
            + "\"Tetriminos\" are game pieces shaped like geometric shapes composed\n"
            + " of four square blocks each. A random sequence of Tetriminos fall down\n"
            + " the playing field (a rectangular vertical shaft, called the \"well\" or\n"
            + " \"matrix\"). The objective of the game is to manipulate these Tetriminos by\n"
            + " moving each one sideways (if the player feels the need) and rotating it by\n"
            + " 90 degree units, with the aim of creating a horizontal line of blocks without gaps.\n"
            + "When such a line is created, it disappears, and any block above the\n"
            + "deleted line will fall. When a certain number of lines are cleared,\n"
            + "the game enters a new level. As the game progresses, each level causes\n"
            + "the Tetriminos to fall faster, and the game ends when the stack of\n"
            + "Tetriminos reaches the top of the playing field and no new Tetriminos\n"
            + "are able to enter.\n-Wikipedia"; 
            JOptionPane.showMessageDialog(null, msg, "Help", JOptionPane.INFORMATION_MESSAGE);            
        });
        menu.add(item);
        
        // Quit
        menu.addSeparator();
        item = new JMenuItem("Exit");
        item.addActionListener((ActionEvent)-> {
            System.exit(0);
        });
        menu.add(item);
        
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }
    
    
    
    
    /**
     * Begins game
     */
    void begin_Game() {       
        startMenuItem.setEnabled(false);
        pauseMenuItem.setEnabled(true);

        board.nextTetro = thumbnail.tetro;
        thumbnail.tetro = board.get_nextTetro();
        board.beginNextMove();
        thumbnail.repaint();
        start_Clock();  // starts elapsed time and tetroTimer
    }
    
    /**
     * Pauses or unpauses game, depending on game's current status
     */
    void pause_Game() {
        pause_Clock();
    }
    
    /**
     * Updates game clock (elapsed time).
     */
    private class TimerEventHandler implements ActionListener
    {            
        @Override
        public void actionPerformed(ActionEvent e){
            long secsRunning = (System.currentTimeMillis() - timeStarted)/1000;  
            // show timer in UI
            timerText.setText(secsRunning+"");           
        }
    }
    
    /**
     * Handles the timer that drops the tetro within the board.
     */
    private class TetrisTimerEventHandler implements ActionListener 
    {        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (board.isMoveOver == true) {
                set_Stats();
                
                if (board.gameOver){
                    stop_Clock();
                    return;
                }                  
                board.isMoveOver = false;
                board.nextTetro = thumbnail.tetro;
                thumbnail.tetro = board.get_nextTetro();
                thumbnail.repaint();
                board.beginNextMove();
            } else {
                board.moveDown();
            }
            board.repaint();
        }
    }
    
    /**
     * Updates game stats panel once player move is over.
     */
    private void set_Stats(){
        scoreText.setText(board.score+"");
        levelText.setText(board.curLevel+"");
        linesClearedText.setText(board.linesCleared+"");
    }
    
    private void init_Timers() {
        tetroTimer = new javax.swing.Timer(board.calc_Delay(), new TetrisTimerEventHandler());        
        tetroTimer.setInitialDelay(500);
        tetroTimer.setDelay(500);
//        tetroTimer.setInitialDelay(board.calc_Delay());
    }
    
    /**
     * Menu Action for Start Game. Starts Game Clock and tetroTimer.
     */
    public void start_Clock() {
        // ElapsedTimer is null when the game has yet to start
        if (elapsedTime != null) return;
        
        timeStarted = System.currentTimeMillis();
        timeEnded = 0;
        timePaused = 0;
        elapsedTime = new Timer(1000, new TimerEventHandler());
        elapsedTime.setRepeats(true);
        elapsedTime.start();
        tetroTimer.start();
    }
    
    /**
     * Called when user clicks SPACE bar or selects menu item
     */
    public void pause_Clock(){
        
        if (elapsedTime.isRunning()){
            elapsedTime.stop();
            pauseMenuItem.setText("Continue");
            tetroTimer.stop();
        }
        else {
            elapsedTime.start();
            pauseMenuItem.setText("Pause");
            tetroTimer.start();
        }
    }
    
    /**
     * Stops Game Clock and tetroTimer. Called only when game over.
     */
    public void stop_Clock() {
        // leave alone if clock is stopped
        if (timeEnded != 0) return;
        
        timeEnded = System.currentTimeMillis();
        elapsedTime.stop();
        tetroTimer.stop();
        new TimerEventHandler().actionPerformed(null);
        new TetrisTimerEventHandler().actionPerformed(null);
        
        
    }
    
    
    /**
     * Inner class - extends MouseAdapter class to handle mouse clicks 
     * to move the Tetro pieces around or to pause the game, etc.
     * When pieces are rotated, calling Timer.restart() allows the game 
     * to be "paused".
     */
    class TetrisMouseAdapter extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e){
                // No additional movements while paused
                if (!elapsedTime.isRunning()){
                    return;
                }

                if (e.getSource() == newTetroBtn){
                    board.nextTetro = thumbnail.tetro;
                    thumbnail.tetro = board.get_nextTetro();
                    thumbnail.repaint();
                    
                    board.beginNextMove();
                } else if (e.getSource() == moveLeftBtn){
//                    thumbnail.tetro.move_Left();
                    board.moveLeft();
                } else if (e.getSource() == moveRightBtn){
//                        thumbnail.tetro.move_Right();
                    board.moveRight();
                } else if (e.getSource() == rotateLeftBtn){
//                        thumbnail.tetro.rotate_Left();
                    board.rotateLeft();
                    tetroTimer.restart();
                } else if (e.getSource() == rotateRightBtn){
//                    thumbnail.tetro.rotate_Right();
                    board.rotateRight();
                    tetroTimer.restart();
                } else if (e.getSource() == dropSoftBtn){
//                    thumbnail.tetro.drop();
                    board.softDrop();
                } else if (e.getSource() == dropHardBtn){
//                thumbnail.tetro.drop();
                    board.hardDrop();
                }
                // reset delay for drop timer
//                tetroTimer.setDelay(board.calc_Delay());
//                tetroTimer.setDelay(1000);
                                       
            board.repaint();
        }
    } // end TetrisMouseAdapter class
    
    
    /**
     * Inner class - extends KeyAdapter class to handle key presses 
     * to move the Tetro pieces around or to pause the game, etc.
     */
    class TetrisKeyAdapter extends KeyAdapter 
    {    
        @Override
        public void keyPressed(KeyEvent e) {
            if (timerText.getText().equals("0") 
                    || board.curTetro.tetroColor == Color.BLACK) {  
                return;
            }        
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                pause_Game();
                return;
            }

            // No additional movements while paused
            if (!elapsedTime.isRunning()){
                return;
            }

            switch (key) {
                case KeyEvent.VK_LEFT:
                    board.moveLeft();
//                    thumbnail.tetro.move_Left(); 
                    break;
                case KeyEvent.VK_RIGHT:
                    board.moveRight();
//                    thumbnail.tetro.move_Right();
                    break;
                case KeyEvent.VK_DOWN:
                    board.rotateRight();
                    tetroTimer.restart();
//                    thumbnail.tetro.rotate_Right();
                    break;
                case KeyEvent.VK_UP:
                    board.rotateLeft();
                    tetroTimer.restart();
//                    thumbnail.tetro.rotate_Left();                   
                    break; 
                case KeyEvent.VK_D:
                    board.softDrop();
                    break;
                case KeyEvent.VK_ENTER:
                    board.hardDrop();
//                    thumbnail.tetro.drop();
                    break;
                    
                default:
                    break;
            }
//            thumbnail.repaint();   
            board.repaint();
        }
    } // end inner TetrisKeyAdapter class 

    
   /**
     * @param args the command line arguments
     */
    public static void main(String args[]) 
    {
        /* Create and display the game */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TetrisUI().setVisible(true);                 
            }            
        });       
    }    
}


/**
 * Overrides methods in JPanel class so we can easily paint component
 * that shows Thumbnail of next Tetromino to drop
 */
class NextTetroPanel extends JPanel {
    Tetromino tetro;    // the Tetro showing
    
    public final int rows = 4;
    public final int cols = 4;
    public final int sqSz = 30;

    
    public NextTetroPanel(){
        tetro = new Tetromino();
        setPreferredSize(new Dimension(cols*sqSz, rows*sqSz));
    }
    
    public NextTetroPanel(Tetromino t) {
        tetro = t;
        setPreferredSize(new Dimension(cols*sqSz, rows*sqSz));        
    }

    
    /**
     * Paints empty 4x4 grid that displays the next Tetromino.
     * Called by overridden function paintComponent()
     * @param g the graphics panel
     */
    public void paint_emptyPanel(Graphics g) {   
        int x, y;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                x=i*sqSz; y=j*sqSz;
                g.setColor(Color.BLACK);
                g.fill3DRect(x, y, sqSz, sqSz, true);
                g.setColor(Color.GRAY.darker().darker());
                g.drawRect(x, y, sqSz, sqSz);                    
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint_emptyPanel(g);
        draw_Tetro(g, tetro);  
    }
    
    
    /**
     * Draws the Tetromino in the tetro thumbnail panel.
     * Called by overridden function paintComponent()
     * @param g the graphics of the panel
     * @param t the next Tetro shown in the panel
     */
    void draw_Tetro(Graphics g, Tetromino t){
        // set paint color to Tetro-specific color        
        g.setColor(t.tetroColor);  
        
        // translate origin so we don't have to do more math
        g.translate(t.dx*sqSz, t.dy*sqSz);  
        
        // draw/fill each Tetro square
        for (Point p : t.tetroSquares){           
            g.fill3DRect(p.x*sqSz, -1*(p.y*sqSz), sqSz, sqSz, true);
        }
    }    

} // end class NextTetroPanel
   