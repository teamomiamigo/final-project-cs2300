import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class TetrisBoardPanel extends JPanel implements ActionListener, KeyListener {
    // Constants for piece colors
    private final Color CYAN_COLOR = Color.CYAN;      // I piece
    private final Color BLUE_COLOR = Color.BLUE;      // J piece
    private final Color ORANGE_COLOR = Color.ORANGE;  // L piece
    private final Color YELLOW_COLOR = Color.YELLOW;  // O piece
    private final Color GREEN_COLOR = Color.GREEN;    // S piece
    private final Color MAGENTA_COLOR = Color.MAGENTA;// T piece
    private final Color RED_COLOR = Color.RED;        // Z piece

    private final int BOARD_WIDTH = 10; // Number of columns
    private final int BOARD_HEIGHT = 20; // Number of rows
    private final int BLOCK_SIZE = 30; // Size of each block
    private final int DELAY = 500; // Timer delay for piece movement

    // Calculate the actual width and height of the game board
    private final int WIDTH = BOARD_WIDTH * BLOCK_SIZE;
    private final int HEIGHT = BOARD_HEIGHT * BLOCK_SIZE;

    private boolean[][] grid;
    private Timer timer;
    private ArrayList<Point> currentPiece;
    private Point currentPiecePosition;
    private Random random;
    private boolean gameOver;

    // Piece type constants
    private final int I_PIECE = 0;
    private final int J_PIECE = 1;
    private final int L_PIECE = 2;
    private final int O_PIECE = 3;
    private final int S_PIECE = 4;
    private final int T_PIECE = 5;
    private final int Z_PIECE = 6;

    // Array to store colors for each piece type
    private final Color[] pieceColors = {
            CYAN_COLOR,     // I piece
            BLUE_COLOR,     // J piece
            ORANGE_COLOR,   // L piece
            YELLOW_COLOR,   // O piece
            GREEN_COLOR,    // S piece
            MAGENTA_COLOR,  // T piece
            RED_COLOR       // Z piece
    };

    private int score; // Score counter

    public TetrisBoardPanel() {
        grid = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(DELAY, this);
        currentPiece = new ArrayList<>();
        currentPiecePosition = new Point(BOARD_WIDTH / 2, 0);
        random = new Random();
        gameOver = false;
        score = 0; // Initialize score counter

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        startGame();
    }

    private void startGame() {
        timer.start();
        newPiece();
    }

    private void newPiece() {
        int shape = random.nextInt(7);
        currentPiece.clear();

        switch (shape) {
            case I_PIECE:
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(2, 0));
                currentPiece.add(new Point(3, 0));
                break;
            case J_PIECE:
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
            case L_PIECE:
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                currentPiece.add(new Point(2, 0));
                break;
            case O_PIECE:
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                break;
            case S_PIECE:
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(2, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                break;
            case T_PIECE:
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
            case Z_PIECE:
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
        }
        currentPiecePosition.setLocation(BOARD_WIDTH / 2, 0);
        if (!isValidPosition(currentPiecePosition, currentPiece)) {
            gameOver = true;
            timer.stop();
        }
    }

    private boolean isValidPosition(Point position, ArrayList<Point> piece) {
        for (Point block : piece) {
            int x = position.x + block.x;
            int y = position.y + block.y;
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT || grid[y][x])
                return false;
        }
        return true;
    }

    private void dropPiece() {
        Point newPosition = new Point(currentPiecePosition.x, currentPiecePosition.y + 1);
        if (isValidPosition(newPosition, currentPiece)) {
            currentPiecePosition.setLocation(newPosition.x, newPosition.y);
        } else {
            mergePiece();
            clearLines();
            newPiece();
        }
    }

    private void mergePiece() {
        for (Point block : currentPiece) {
            int x = currentPiecePosition.x + block.x;
            int y = currentPiecePosition.y + block.y;
            grid[y][x] = true;
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        int consecutiveCleared = 0; // Track consecutive line clears
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (!grid[i][j]) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                for (int k = i; k > 0; k--) {
                    System.arraycopy(grid[k - 1], 0, grid[k], 0, BOARD_WIDTH);
                }
                linesCleared++;
                consecutiveCleared++;
                i++;
            }
        }
        if (linesCleared > 0) {
            // Update score based on lines cleared and consecutive clears
            score += linesCleared * 100;
            if (consecutiveCleared > 1) {
                score += 200 * (consecutiveCleared - 1);
            }
        }
    }

    private void rotatePiece() {
        ArrayList<Point> rotatedPiece = new ArrayList<>();
        for (Point block : currentPiece) {
            rotatedPiece.add(new Point(-block.y, block.x));
        }
        if (isValidPosition(currentPiecePosition, rotatedPiece)) {
            currentPiece = rotatedPiece;
        }
    }

    private void movePiece(int dx) {
        Point newPosition = new Point(currentPiecePosition.x + dx, currentPiecePosition.y);
        if (isValidPosition(newPosition, currentPiece)) {
            currentPiecePosition.setLocation(newPosition.x, newPosition.y);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw red boundary
        g.setColor(Color.RED);
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // Draw gridlines
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            g.drawLine(i * BLOCK_SIZE, 0, i * BLOCK_SIZE, HEIGHT);
        }
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            g.drawLine(0, i * BLOCK_SIZE, WIDTH, i * BLOCK_SIZE);
        }

        // Draw grid
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (grid[i][j]) {
                    int x = j * BLOCK_SIZE;
                    int y = i * BLOCK_SIZE;
                    Color pieceColor = pieceColors[random.nextInt(pieceColors.length)];
                    g.setColor(pieceColor);
                    g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // Draw current piece
        Color pieceColor = pieceColors[random.nextInt(pieceColors.length)]; // Random color
        g.setColor(pieceColor);
        for (Point block : currentPiece) {
            int x = (currentPiecePosition.x + block.x) * BLOCK_SIZE;
            int y = (currentPiecePosition.y + block.y) * BLOCK_SIZE;
            g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        }

        // Draw game over message
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fontMetrics = g.getFontMetrics();
            String message = "GAME OVER";
            int x = (WIDTH - fontMetrics.stringWidth(message)) / 2;
            int y = HEIGHT / 2;
            g.drawString(message, x, y);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String scoreText = "Score: " + score;
        g.drawString(scoreText, 20, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            dropPiece();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    movePiece(-1);
                    break;
                case KeyEvent.VK_RIGHT:
                    movePiece(1);
                    break;
                case KeyEvent.VK_DOWN:
                    dropPiece();
                    break;
                case KeyEvent.VK_UP:
                    rotatePiece();
                    break;
            }
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
