import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final int INITIAL_DELAY = 500; // Initial timer delay for piece movement

    // Calculate the actual width and height of the game board
    private final int WIDTH = BOARD_WIDTH * BLOCK_SIZE;
    private final int HEIGHT = BOARD_HEIGHT * BLOCK_SIZE;

    private boolean[][] grid;
    private Timer timer;
    private ArrayList<Point> currentPiece;
    private Point currentPiecePosition;
    private Random random;
    private boolean gameOver;
    private JButton pauseButton;
    private JButton restartButton;
    private boolean isPaused;
    private int score; // Score counter
    private Color currentPieceColor; // Current color of the falling piece
    private Color landedPieceColor; // Color of the landed pieces
    private int level; // Current level
    private int delay; // Timer delay for piece movement

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

    public TetrisBoardPanel() {
        grid = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(INITIAL_DELAY, this);
        currentPiece = new ArrayList<>();
        currentPiecePosition = new Point(BOARD_WIDTH / 2, 0);
        random = new Random();
        gameOver = false;
        score = 0; // Initialize score counter
        level = 1; // Initialize level
        delay = INITIAL_DELAY; // Initialize delay

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        startGame();

        // Create and initialize the pause button
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new PauseButtonListener());
        add(pauseButton);

        // Create and initialize the restart button
        restartButton = new JButton("Restart");
        restartButton.addActionListener(new RestartButtonListener());
        restartButton.setVisible(false); // Initially invisible
        add(restartButton);

        // Initialize the game as not paused
        isPaused = false;
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
        currentPieceColor = getRandomColor(); // Set initial color for the piece
        landedPieceColor = getRandomColor(); // Set color for landed pieces
        if (!isValidPosition(currentPiecePosition, currentPiece)) {
            gameOver = true;
            timer.stop();
            restartButton.setVisible(true); // Show restart button
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
            // Check for level up
            if (score >= 1000) {
                level++;
                updateDelay();
                // Reset score and clear board for new level
                score = 0;
                clearBoard();
            }
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                grid[i][j] = false;
            }
        }
    }

    private void updateDelay() {
        delay = (int) (INITIAL_DELAY * Math.pow(0.95, level - 1)); // Decrease delay by 5% for each level
        timer.setDelay(delay);
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

    private Color getRandomColor() {
        return pieceColors[random.nextInt(pieceColors.length)];
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
                    g.setColor(landedPieceColor); // Use consistent color for landed pieces
                    g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // Draw current piece
        g.setColor(currentPieceColor);
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

        // Draw level
        String levelText = "Level: " + level;
        g.drawString(levelText, WIDTH - 100, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !isPaused) {
            dropPiece();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && !isPaused) {
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

    // ActionListener for the pause button
    private class PauseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            togglePause();
        }
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            pauseButton.setText("Start");
        } else {
            timer.start();
            pauseButton.setText("Pause");
            requestFocusInWindow(); // Ensure focus is set back to the TetrisBoardPanel
        }
    }

    // ActionListener for the restart button
    private class RestartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            restartGame();
        }
    }

    private void restartGame() {
        // Store the current level
        int currentLevel = level;

        // Reset all game variables
        grid = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        gameOver = false;
        score = 0;
        delay = INITIAL_DELAY;
        level = currentLevel; // Restart from the current level
        timer.setDelay(delay);
        newPiece();
        restartButton.setVisible(false); // Hide restart button
        isPaused = false;
        pauseButton.setText("Pause");
        requestFocusInWindow(); // Ensure focus is set back to the TetrisBoardPanel
        timer.start(); // Start the timer again
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        TetrisBoardPanel tetrisBoard = new TetrisBoardPanel();
        frame.add(tetrisBoard, BorderLayout.CENTER);

        // Add pause and restart buttons to a panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tetrisBoard.pauseButton);
        buttonPanel.add(tetrisBoard.restartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
