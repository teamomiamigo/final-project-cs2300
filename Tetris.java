import javax.swing.*;
import java.awt.*;

public class Tetris extends JFrame {
    public Tetris() {
        initUI();
    }

    private void initUI() {
        TetrisBoardPanel board = new TetrisBoardPanel();
        add(board);

        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Tetris();
    }
}
