import javax.swing.*;
import java.awt.*;

enum Flag {
    NONE, MINE, GUESS
}

public class Square extends JButton {
    private boolean mine;
    private boolean status; // false == not touched; true == uncovered/touched
    private int neighbors;
    private Flag flag;
    protected int x;
    protected int y;

    Square(int x, int y) {
        //super("1");

        this.x = x;
        this.y = y;
        status = false;
        mine = false;
        flag = Flag.NONE;

        // Color and style
        setForeground(Color.BLACK);
        setBackground(new Color(200, 200, 200));
    }

    public void reset() {
        status = false;
        mine = false;
        neighbors = 0;
        flag = Flag.NONE;

        // Color and style
        setForeground(Color.BLACK);
        setBackground(new Color(200, 200, 200));
        setText("");
        setIcon(null);
    }

    public void uncover() {
        setBackground(new Color(170, 170, 170));
        setText(Integer.toString(neighbors));
        status = true;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean s) {
        status = s;
    }

    public boolean hasMine() {
        return mine;
    }

    public void setMine() {
        mine = true;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag f) {
        flag = f;
    }

    public int getNeighbors() {
        return neighbors;
    }

    public void incNeighbors() {
        neighbors++;
    }
}
