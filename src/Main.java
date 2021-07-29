import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class Main extends JPanel {

	public static int cols = 25;
	public static int rows = 25;
	public static Spot[][] grid = new Spot[cols][rows];
	public static ArrayList<Spot> openSet = new ArrayList<>();
	public static ArrayList<Spot> closedSet = new ArrayList<>();
	public static ArrayList<Spot> path;
	public static ArrayList<Spot> walls = new ArrayList<>();;
	private static boolean done = false;

	public static void main(String[] args) {
		Main mainPanel  = new Main();
		JFrame frame = new JFrame("Pathfinder");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(mainPanel);
		frame.setVisible(true);

		//Creating spots
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
				grid[i][j] = new Spot(i, j);
			}
		}

		//Creating neighbors
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
				grid[i][j].addNeighbors(grid);
			}
		}

		Spot start = grid[1][1];
		Spot end = grid[3][7];

		//Adding starting spot to openSet
		openSet.add(start);
	}

	public void paintComponent (Graphics g) {
		Spot current = null;
		super.paintComponent(g);
		Spot start = grid[1][1];
		Spot end = grid[3][7];

		walls.add(grid[2][2]);
		walls.add(grid[2][1]);
		walls.add(grid[2][3]);
		walls.add(grid[2][4]);
		walls.add(grid[2][5]);
		walls.add(grid[2][6]);
		walls.add(grid[2][7]);

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				grid[i][j].draw(null, g);
			}
		}

		if(openSet.size() > 0) {
			int lowestCost = 0;

			for(int i = 0; i < openSet.size(); i++) {
				if(openSet.get(i).f < openSet.get(lowestCost).f) {
					lowestCost = i;
				}
			}
			current = openSet.get(lowestCost);

			if(current.getX() == end.getX() && current.getY() == end.getY()) {			
				System.out.println("done");
				done = true;
			}

			removeSpot(current);
			closedSet.add(current);

			ArrayList<Spot> neighbors = current.neighbors;
			for(int i = 0; i < neighbors.size(); i++) {
				Spot neighbor = neighbors.get(i);
				if(walls.contains(neighbor)){
					continue;
				}
				else if(!closedSet.contains(neighbor)){
					double tempG = current.g + 1;
					if(openSet.contains(neighbor)) {
						if(tempG < neighbor.g) {
							neighbor.g = tempG;
						}
					}
					else {
						neighbor.g = tempG;
						openSet.add(neighbor);
					}
					neighbor.previous = current;
					neighbor.h = heurisitic(neighbor, end);
					neighbor.f = neighbor.g + neighbor.h;
				}
			}
		}

		for (int i = 0; i < walls.size(); i++) {
			walls.get(i).draw(Color.BLACK, g);
		}

		for (int i = 0; i < closedSet.size(); i++) {
			closedSet.get(i).draw(Color.RED, g);
		}

		for (int i = 0; i < openSet.size(); i++) {
			openSet.get(i).draw(Color.GREEN, g);
		}

		path = new ArrayList<>();
		Spot temp = current;
		path.add(temp);
		while(temp.previous != null) {
			path.add(temp.previous);
			temp = temp.previous;
		}

		if(path.size() > 0) {
			for(int i = 0; i < path.size(); i++) {
				path.get(i).draw(Color.BLUE, g);
			}
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(done){}
		else{
			repaint();
		}

		start.draw(Color.PINK, g);
		end.draw(Color.PINK, g);
	}

	public static void removeSpot(Spot elem) {
		Iterator<Spot> iter = openSet.iterator();
		while(iter.hasNext()) {
			Spot s = iter.next();
			if(s.getX() == elem.getX() && s.getY() == elem.getY()) {
				iter.remove();
			}
		}
	}

	public static double heurisitic(Spot a, Spot b) {
		double d = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
		return d;
	}
}