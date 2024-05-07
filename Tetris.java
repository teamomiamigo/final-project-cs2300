import javax.swing.JFrame;

public class Tetris extends JFrame {
    public Tetris() {
        initUI();
    }

    private void initUI() {
        TetrisBoardPanel board = new TetrisBoardPanel();
        add(board);

        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(315, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Tetris();
    }
}
