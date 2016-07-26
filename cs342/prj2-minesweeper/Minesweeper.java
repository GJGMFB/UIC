import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Minesweeper extends JFrame implements ActionListener {
    private int r = 10;
    private int c = 10;
    private int mines = 10;
    private int squaresLeft = r * c - mines;
    private Square[][] grid = new Square[r][c];
    private boolean gameOver = false;
    private boolean hasStartedGame = false;
    private JLabel labelMinesLeft;
    private JButton buttonReset;
    private JLabel labelTimer;
    private int time = 0;
    private Timer timer;
    private JMenuItem itemReset;
    private JMenuItem itemTopTen;
    private JMenuItem itemExit;
    private JMenuItem itemHelp;
    private JMenuItem itemAbout;
    private ArrayList<Score> topTen;

    Minesweeper() {
        // Initial set-up
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setTitle("Minesweeper");

        // Top panel containing current game statistics
        JPanel statPane = new JPanel(new GridLayout(1, 3));

        // Mines left
        labelMinesLeft = new JLabel(Integer.toString(mines));
        statPane.add(labelMinesLeft);

        // Reset button
        buttonReset = new JButton(new ImageIcon(getClass().getResource("/images/smile_button.gif")));
        buttonReset.setBorderPainted(false);
        buttonReset.setFocusPainted(false);
        buttonReset.setContentAreaFilled(false);
        buttonReset.addMouseListener(new listenerResetButton());
        statPane.add(buttonReset);

        // Timer
        labelTimer = new JLabel("0");
        labelTimer.setHorizontalAlignment(JLabel.RIGHT);
        statPane.add(labelTimer);

        // Create 10x10 grid of buttons for mines
        JPanel minePane = new JPanel(new GridLayout(r, c));

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                grid[i][j] = new Square(i, j);
                grid[i][j].addMouseListener(new listenerGrid());
                minePane.add(grid[i][j]);
            }
        }

        initField(); // Initialize the mine field

        //debug
//        for (int i = 0; i < r; i++) {
//            for (int j = 0; j < c; j++) {
//                grid[i][j].setText(Integer.toString(grid[i][j].getNeighbors()));
//                if (grid[i][j].hasMine())
//                    grid[i][j].setText("M");
//            }
//        }

        // Add panels to GUI
        add(statPane, BorderLayout.NORTH); // Add statPane to GUI
        add(minePane, BorderLayout.CENTER); // Add minePane to GUI

        // Create timer
        timer = new Timer(1000, this);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu menuGame = new JMenu("Game");
        itemReset = new JMenuItem("Reset");
        itemReset.addActionListener(this);
        itemTopTen = new JMenuItem("Top Ten");
        itemTopTen.addActionListener(this);
        itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(this);
        menuGame.add(itemReset);
        menuGame.add(itemTopTen);
        menuGame.add(itemExit);

        JMenu menuHelp = new JMenu("Help");
        itemHelp = new JMenuItem("Help");
        itemHelp.addActionListener(this);
        itemAbout = new JMenuItem("About");
        itemAbout.addActionListener(this);
        menuHelp.add(itemHelp);
        menuHelp.add(itemAbout);

        menuBar.add(menuGame);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        readFile();

        setSize(900, 900);
        setVisible(true);
    }

    // Generates random mines for the mine field
    private void initField() {
        Random random = new Random();
        int row, col;

        // Set mines randomly
        for (int i = 0; i < mines; i++) {
            // Generate random coordinate
            row = random.nextInt(r);
            col = random.nextInt(c);

            // Make sure this coordinate is empty, otherwise do another loop
            if (grid[row][col].hasMine()) {
                i--; // Go for another loop
            } else {
                grid[row][col].setMine(); // Set mine here
            }
        }

        // Determine number of neighbors for each mine
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (grid[i][j].hasMine()) {
                    if(i - 1 >= 0 && j - 1 >= 0) // upper left
                        grid[i - 1][j - 1].incNeighbors();
                    if(i - 1 >= 0 && j >= 0) // upper center
                        grid[i - 1][j].incNeighbors();
                    if(i - 1 >= 0 && j + 1 < c) // upper right
                        grid[i - 1][j + 1].incNeighbors();
                    if(i >= 0 && j - 1 >= 0) // middle left
                        grid[i][j - 1].incNeighbors();
                    if(i >= 0 && j + 1 < c) // middle right
                        grid[i][j + 1].incNeighbors();
                    if(i + 1 < r && j - 1 >= 0) // lower left
                        grid[i + 1][j - 1].incNeighbors();
                    if(i + 1 < r && j >= 0) // lower center
                        grid[i + 1][j].incNeighbors();
                    if(i + 1 < r && j + 1 < c) // lower right
                        grid[i + 1][j + 1].incNeighbors();
                }
            }
        }
    }

    private void writeFile() {
        Collections.sort(topTen); // Sort the hash table from highest to lowest

        // Overwrite data to file
        try {
            // Create a file if it does not exist
            File scores = new File(".", "scores.txt");
            PrintWriter output = new PrintWriter(new FileWriter(scores));

            // Write the scores to the file
            for (Score s : topTen) {
                output.println(s.name + "," + s.score);
            }

            output.close();
        }
        catch( IOException ex )
        {
            ex.printStackTrace();
        }
    }

    private boolean readFile() {
        String line;

        try {
            File scores = new File(".", "scores.txt");

            // Does the file exist?
            if (!scores.exists()) {
                topTen = new ArrayList<>();
                return false;
            }

            // Read highest scores
            BufferedReader in = new BufferedReader(new FileReader(scores));
            line = in.readLine();
            topTen = new ArrayList<>();
            while (line != null) {
                String[] s = line.split(","); // Separate by comma
                topTen.add(new Score(s[0], Integer.parseInt(s[1]))); // Add score to hash table
                line = in.readLine(); // Read next line
            }

            in.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    // Action method performer for menu based items & timer
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == itemReset) {
            if (hasStartedGame) {
                timer.stop();
                reset();
            }

            return;
        }

        if(o == itemTopTen) {
            if (topTen.isEmpty()) {
                JOptionPane.showMessageDialog(itemTopTen, "No fastest time", "High Scores", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String scoreText = "";

                // Construct high score list
                for (Score s : topTen) {
                    scoreText += s.name + " \t" + s.score + "\n";
                }

                JOptionPane.showMessageDialog(itemTopTen, scoreText, "High Scores", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if(o == itemExit)
            System.exit(0);

        if(o == itemHelp)
            JOptionPane.showMessageDialog(itemHelp, "Left Click: Reveal one or multiple fields\nRight Click: Marks space with a flag if you think a mine is there\nWinning: Clear all spaces not containing mines", "Help", JOptionPane.INFORMATION_MESSAGE );
        
        if(o == itemAbout)
            JOptionPane.showMessageDialog(itemAbout, "Programmers:\nDennis Aurelian Leancu (leancu2)\nDavid Halverson (dhalve2)\nProgrammed for CS 342 for University of Illinois at Chicago", "About", JOptionPane.INFORMATION_MESSAGE);

        if (o == timer) {
            labelTimer.setText(Integer.toString(++time));
        }
    }

    // Used to reset the game to an inital state
    private void reset() {
        mines = 10;
        squaresLeft = r * c - mines;
        gameOver = false;
        hasStartedGame = false;
        time = 0;

        labelMinesLeft.setText(Integer.toString(mines));
        labelTimer.setText(Integer.toString(time));

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                grid[i][j].reset();
            }
        }

        initField(); // Initialize the mine field
    }

    // Logic to end the game if the player has won or lost
    private void endGame(boolean won) {
        gameOver = true; // Set game over flag
        timer.stop(); // Stop timer

        // Specific events for won/lost
        // Did they win?
        if (won) {
            buttonReset.setIcon(new ImageIcon(getClass().getResource("/images/head_glasses.gif")));

            String name = JOptionPane.showInputDialog("You win!\nPlease enter your name\n"); // Prompt for name

            topTen.add(new Score(name, time)); // Add score to hash table scores

            writeFile(); // Store new high scores
        }

        // Did they lose?
        else {
            buttonReset.setIcon(new ImageIcon(getClass().getResource("/images/head_dead.gif")));
        }
    }

    // Any neighbors without nearby mines will be auto uncovered
    private void uncoverNeighbors(int x, int y) {
        if (x < r && y < c && x >= 0 && y >= 0 && !grid[x][y].hasMine() && !grid[x][y].getStatus()) {
            grid[x][y].uncover();
            squaresLeft--;

            if (grid[x][y].getNeighbors() == 0) {
                uncoverNeighbors(x - 1, y - 1);
                uncoverNeighbors(x, y - 1);
                uncoverNeighbors(x + 1, y - 1);
                uncoverNeighbors(x - 1, y);
                uncoverNeighbors(x + 1, y);
                uncoverNeighbors(x - 1, y + 1);
                uncoverNeighbors(x, y + 1);
                uncoverNeighbors(x + 1, y + 1);
            }
        }
    }

    // Score class used with ArrayList to keep track of high scores
    class Score implements Comparable<Score> {
        public String name;
        public int score;

        public Score(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public int compareTo(Score other) {
            return other.score - this.score;
        }
    }

    // Reset listener
    class listenerResetButton extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (hasStartedGame) {
                timer.stop();
                reset();
            }
        }
    }

    // Grid listener
    class listenerGrid extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            Square clickedButton = (Square) e.getSource();

            // Left button clicked
            if (e.getButton() == MouseEvent.BUTTON1) {
                // Check if the game timer has started
                if (!hasStartedGame) {
                    hasStartedGame = true;
                    timer.start();
                }

                // Left click is only valid if not flagged and not uncovered and not game over
                if (clickedButton.getFlag() == Flag.NONE && !clickedButton.getStatus() && !gameOver) {
                    // Set new status
                    //clickedButton.setStatus(true);

                    // If there IS a mine
                    if (clickedButton.hasMine()) {
                        endGame(false);
                        clickedButton.setStatus(true);

                        // Show all other mines
                        for (int i = 0; i < r; i++) {
                            for (int j = 0; j < c; j++) {
                                if (grid[i][j].hasMine()) {
                                    // Set image to fit button size
                                    ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/button_bomb_pressed.gif")); // Load the image to a imageIcon
                                    Image image = imageIcon.getImage(); // Transform it
                                    Image newimg = image.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_DEFAULT);
                                    imageIcon = new ImageIcon(newimg);  // Transform it back
                                    grid[i][j].setIcon(imageIcon);
                                }
                            }
                        }

                        // "Blow up" this mine
                        // Set image to fit button size
                        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/button_bomb_blown.gif")); // Load the image to a imageIcon
                        Image image = imageIcon.getImage(); // Transform it
                        Image newimg = image.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_DEFAULT);
                        imageIcon = new ImageIcon(newimg);  // Transform it back
                        clickedButton.setIcon(imageIcon);
                    }

                    // If there is NOT a mine
                    else {
                        uncoverNeighbors(clickedButton.x, clickedButton.y);

                        // Have we won the game yet?
                        if (squaresLeft == 0) {
                            endGame(true);
                        }
                    }
                }
            }

            // Right button clicked
            if (e.getButton() == MouseEvent.BUTTON3) {
                // Flagging is only valid if button has not been uncovered and not game over and mines > 0
                if (!clickedButton.getStatus() && !gameOver && mines > 0 || clickedButton.getFlag() == Flag.MINE && !gameOver) {
                    if (clickedButton.getFlag() == Flag.NONE) {
                        clickedButton.setFlag(Flag.MINE);

                        // Decrement number of mines
                        labelMinesLeft.setText(Integer.toString(--mines));

                        // Set image to fit button size
                        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/button_flag.gif")); // Load the image to a imageIcon
                        Image image = imageIcon.getImage(); // Transform it
                        Image newimg = image.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_DEFAULT);
                        imageIcon = new ImageIcon(newimg);  // Transform it back
                        clickedButton.setIcon(imageIcon);
                    } else if (clickedButton.getFlag() == Flag.MINE) {
                        clickedButton.setFlag(Flag.GUESS);

                        // Increment number of mines
                        labelMinesLeft.setText(Integer.toString(++mines));

                        // Set image to fit button size
                        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/button_question.gif")); // Load the image to a imageIcon
                        Image image = imageIcon.getImage(); // Transform it
                        Image newimg = image.getScaledInstance(clickedButton.getWidth(), clickedButton.getHeight(), Image.SCALE_DEFAULT);
                        imageIcon = new ImageIcon(newimg);  // Transform it back
                        clickedButton.setIcon(imageIcon);
                    } else if (clickedButton.getFlag() == Flag.GUESS) {
                        clickedButton.setFlag(Flag.NONE);
                        clickedButton.setIcon(null);
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
        Minesweeper game = new Minesweeper();
    }
}