import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class Main extends JPanel {

	public static int cols = 25;
	public static int rows = 25;
	public static int tileWidth = 800 / cols;
	public static int tileHeight = 600 / rows;
	public static Spot[][] grid = new Spot[cols][rows];
	public static ArrayList<Spot> openSet = new ArrayList<>();
	public static ArrayList<Spot> closedSet = new ArrayList<>();
	public static ArrayList<Spot> path;
	public static ArrayList<Spot> walls = new ArrayList<>();
	private static boolean done = false;
	private static int col = 0;
	private static int row = 0;
	public static Spot start = null;
	public static Spot end = null;
	private static boolean startB = false;
	private static boolean endB = false;
	private static boolean wallB = false;
	private static int clicks = 0;
	private static JButton goButton;
	private static boolean chosen = false;

	public static void main(String[] args) {
		Main mainPanel  = new Main();
		JFrame frame = new JFrame("Pathfinder");
		goButton = addNewButton();
		frame.getContentPane().add(new Main());
		frame.setSize(816, 639);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(goButton);
		goButton.setVisible(false);
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
	}

	//Create button and action listener:
    public static JButton addNewButton(){
        JButton button = new JButton("Next");
		button.setBounds(700, 550, 60, 40);
		
        button.addActionListener(new java.awt.event.ActionListener(){
        	@Override
        	public void actionPerformed(java.awt.event.ActionEvent evt){
            	buttonAction(evt);
        	}
		});
		
        return button;
	}
	
	//Action for each button:
    public static void buttonAction(ActionEvent e) {
		goButton.setVisible(false);
		clicks++;
		
		if(clicks == 1) { 
			startB = true;
			endB = true;
			//Adding starting spot to openSet
			openSet.add(start);
		}
		if(clicks == 2) {
			endB = false;
			wallB = true;
		}
		if(clicks == 3) {
			wallB = false;
			chosen = true;
		}
	}
	
	public void paintComponent (Graphics g) {
		Spot current = null;
		super.paintComponent(g);

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				grid[i][j].draw(null, g);
			}
		}

		if(!chosen) {
			if(start != null) {
				start.draw(Color.PINK, g);
			}
			if(end != null) {
				end.draw(Color.PINK, g);
			}
			if(walls.size() > 0) {
				for (int i = 0; i < walls.size(); i++) {
					walls.get(i).draw(Color.BLACK, g);
				}
			}
		}
		else if(chosen) {
			if(openSet.size() > 0) {
				int lowestCost = 0;

				for(int i = 0; i < openSet.size(); i++) {
					if(openSet.get(i).f < openSet.get(lowestCost).f) {
						lowestCost = i;
					}
				}
				current = openSet.get(lowestCost);

				//check if end of path is found
				//make a seperate method to check
				if(current.getX() == end.getX() && current.getY() == end.getY()) {			
					System.out.println("done");
					done = true;
				}

				openSet.remove(current);
				closedSet.add(current);

				diagonalWallCheck(current);

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
	}

	public static double heurisitic(Spot a, Spot b) {
		final double D = 1;
		final double D2 = 1.414;
		double dx = Math.abs(a.x - b.x); 
		double dy = Math.abs(a.y - b.y);
		return D * Math.max(dx, dy) + (D2 - D) * Math.min(dx, dy);
	}

	public static void diagonalWallCheck(Spot current) {
		Spot xR = grid[current.getX() + 1][current.getY()];
		Spot xL = grid[current.getX() - 1][current.getY()];
		Spot yD = grid[current.getX()][current.getY() + 1];
		Spot yU = grid[current.getX()][current.getY() - 1];

		if(walls.contains(xR) && walls.contains(yD) && current != end) {
			current.removeNeighbor(grid[current.x+1][current.y+1]);
		}
		else if(walls.contains(xL) && walls.contains(yU) && current != end) {
			current.removeNeighbor(grid[current.x-1][current.y-1]);
		}
	}

	public Main() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x=e.getX();
				int y=e.getY();
				col = x / tileWidth;
				row = y / tileHeight;

				if(e.getButton() == MouseEvent.BUTTON1) {
					if(wallB) {
						walls.add(grid[col][row]);
						goButton.setVisible(true);
					}
					if(endB) {
						end = grid[col][row];
						goButton.setVisible(true);
					}
					if(!startB) {
						start = grid[col][row];
						goButton.setVisible(true);
					}
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					walls.remove(grid[col][row]);
				}

				repaint();
			}
		});
	}
}
