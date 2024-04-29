import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class TetrisBoardPanel extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int BLOCK_SIZE = 30;
    private final int DELAY = 500;

    private boolean[][] grid;
    private Timer timer;
    private ArrayList<Point> currentPiece;
    private Point currentPiecePosition;
    private Random random;
    private boolean gameOver;

    public TetrisBoardPanel() {
        grid = new boolean[HEIGHT][WIDTH];
        timer = new Timer(DELAY, this);
        currentPiece = new ArrayList<>();
        currentPiecePosition = new Point(WIDTH / 2, 0);
        random = new Random();
        gameOver = false;

        setPreferredSize(new Dimension(WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE));
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
        currentPiece.clear(); // Clear the previous piece
    
        switch (shape) {
            case 0: // I
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(2, 0));
                currentPiece.add(new Point(3, 0));
                break;
            case 1: // J
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
            case 2: // L
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                currentPiece.add(new Point(2, 0));
                break;
            case 3: // O
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                break;
            case 4: // S
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(2, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                break;
            case 5: // T
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(0, 1));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
            case 6: // Z
                currentPiece.add(new Point(0, 0));
                currentPiece.add(new Point(1, 0));
                currentPiece.add(new Point(1, 1));
                currentPiece.add(new Point(2, 1));
                break;
        }
        currentPiecePosition.setLocation(WIDTH / 2, 0);
    
        if (!isValidPosition(currentPiecePosition, currentPiece)) {
            gameOver = true;
            timer.stop();
        }
    }
    private boolean isValidPosition(Point position, ArrayList<Point> piece) {
        for (Point block : piece) {
            int x = position.x + block.x;
            int y = position.y + block.y;
            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || grid[y][x])
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
        for (int i = HEIGHT - 1; i >= 0; i--) {
            boolean lineFull = true;
            for (int j = 0; j < WIDTH; j++) {
                if (!grid[i][j]) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                for (int k = i; k > 0; k--) {
                    System.arraycopy(grid[k - 1], 0, grid[k], 0, WIDTH);
                }
                linesCleared++;
                i++;
            }
        }
        if (linesCleared > 0) {
            // todo: score
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
        g.drawRect(0, 0, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
    
        // Draw gridlines
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(i * BLOCK_SIZE, 0, i * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(0, i * BLOCK_SIZE, WIDTH * BLOCK_SIZE, i * BLOCK_SIZE);
        }
    
        // Draw grid
        g.setColor(Color.GRAY);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (grid[i][j]) {
                    g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    
        // Draw current piece
        g.setColor(Color.WHITE);
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
            int x = (getWidth() - fontMetrics.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }
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
