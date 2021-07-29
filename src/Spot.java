import java.util.ArrayList;
import java.awt.*;

public class Spot {

    public double f, g, h;
    public int x, y;
    public ArrayList<Spot> neighbors;
    public Spot previous;
    private int width = 800 / Main.cols;
    private int height = 600 / Main.rows;

    public Spot(int i, int j) {
        this.x = i;
        this.y = j;
        this.f = 0;
        this.g = 0;
        this.h = 0;
        this.neighbors = new ArrayList<>();
        this.wall = false;
    }

    public void draw(Color color, Graphics g) {
        if(color == null) {
            g.drawRect(this.x * width, this.y * height, width - 1, height - 1);
        }
        else {
            g.setColor(color);
            g.fillRect(this.x * width, this.y * height, width - 1, height - 1);
        }
    }

    public String toString() {
        return x + " " + y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addNeighbors(Spot[][] grid) {
        if(x < Main.cols - 1) {
            this.neighbors.add(grid[this.x + 1][this.y]);
        }
        if(x > 0) {
            this.neighbors.add(grid[this.x - 1][this.y]);
        }
        if(y < Main.rows - 1) {
            this.neighbors.add(grid[this.x][this.y + 1]);
        }
        if(y > 0) {
            this.neighbors.add(grid[this.x][this.y - 1]);
        }
    }
}