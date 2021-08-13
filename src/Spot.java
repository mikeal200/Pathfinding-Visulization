import java.util.ArrayList;
import java.awt.*;

public class Spot {

    public double f, g, h;
    public int x, y;
    public ArrayList<Spot> neighbors;
    public Spot previous;
    private int width = 800 / Main.cols;
    private int height = 600 / Main.rows;
    private static int cols = Main.cols;
    private static int rows = Main.rows;

    public Spot(int i, int j) {
        this.x = i;
        this.y = j;
        this.f = 0;
        this.g = 0;
        this.h = 0;
        this.neighbors = new ArrayList<>();
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

    public void removeNeighbor(Spot current){
        this.neighbors.remove(current);
    }

    public void addNeighbors(Spot[][] grid) {
        if(x < cols - 1) {
            this.neighbors.add(grid[this.x + 1][this.y]);
        }
        if(x > 0) {
            this.neighbors.add(grid[this.x - 1][this.y]);
        }
        if(y < rows - 1) {
            this.neighbors.add(grid[this.x][this.y + 1]);
        }
        if(y > 0) {
            this.neighbors.add(grid[this.x][this.y - 1]);
        }
        if(x < cols - 1 && y < rows - 1) {
            this.neighbors.add(grid[this.x + 1][this.y + 1]);
        }
        if(x > 0 && y > 0) {
            this.neighbors.add(grid[this.x - 1][this.y - 1]);
        }
        if(x > 0 && y < rows - 1) {
            this.neighbors.add(grid[this.x - 1][this.y + 1]);
        }
        if(x < cols - 1 && y > 0) {
            this.neighbors.add(grid[this.x + 1][this.y - 1]);
        }
    }

    public static void diagonalWallCheck(Spot current) {
		int x = current.getX();
		int y = current.getY();
        Spot xR = null, xL = null, yD = null, yU = null;
        Spot[][] grid = Main.grid;
        ArrayList<Spot> walls = Main.walls;

		if(x < cols - 1) {
			xR = grid[x + 1][y];
		}
		if(x > 0) {
			xL = grid[x - 1][y];
		}
		if(y < rows - 1) {
			yD = grid[x][y + 1];
		}
		if(y > 0) {
			yU = grid[x][y - 1];
		}
		
		if(walls.contains(xR) && walls.contains(yD)) {
			current.removeNeighbor(grid[x + 1][y + 1]);
		}
		if(walls.contains(xL) && walls.contains(yU)) {
			current.removeNeighbor(grid[x - 1][y - 1]);
		}
		if(walls.contains(xR) && walls.contains(yU)) {
			current.removeNeighbor(grid[x + 1][y - 1]);
		}
		if(walls.contains(xL) && walls.contains(yD)) {
			current.removeNeighbor(grid[x - 1][y + 1]);
		}
	}
}