import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel implements Runnable {

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
	private static JButton nextButton, pauseButton;
	private static boolean chosen = false;
	private static Main mainPanel  = new Main();
	private static Thread thread = new Thread(mainPanel);

	public static void main(String[] args) {
		thread.start();
		JPanel container = new JPanel();
		JPanel panel1 = new JPanel();
		JFrame frame = new JFrame("Pathfinder");

		pauseButton = addNewButton("Pause");
		pauseButton.setVisible(true);

		nextButton = addNewButton("Next");
		nextButton.setVisible(true);

		panel1.setPreferredSize(new Dimension(139, 639));
		//panel1.setLayout(new GridLayout(4, 1));
		panel1.setBorder(BorderFactory.createTitledBorder("Controls"));
		panel1.add(nextButton);
		panel1.add(pauseButton);

		mainPanel.setPreferredSize(new Dimension(829, 639));

		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(panel1);
		container.add(mainPanel);

		frame.getContentPane().add(new Main());
		frame.setSize(950, 639);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(container);
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
    public static JButton addNewButton(String bName){
        JButton button = new JButton(bName);
		button.setBounds(0, 0, 60, 40);
		
        button.addActionListener(new java.awt.event.ActionListener(){
        	@Override
        	public void actionPerformed(java.awt.event.ActionEvent evt){
				if(bName.equals("Next")) {
					nextAction(evt);
				}
				else if(bName.equals("Pause")) {
					pauseAction(evt);
				}
        	}
		});
		
        return button;
	}

	//Action for pause button:
    public static void pauseAction(ActionEvent e) {
		
		if(!done) {
			try{
				Thread.sleep(5000);
				//thread.suspend();
				//thread.wait();
			}
			catch(Exception ex) {

			}
		}
	}
	
	//Action for next button:
    public static void nextAction(ActionEvent e) {
		
		if(!done) {
			if(start != null && end == null) { 
				startB = true;
				endB = true;
				//clears openset so starting node is the only node
				openSet.clear();
				//Adding starting spot to openSet
				openSet.add(start);
			}
			if(start != null && end != null && !wallB) {
				endB = false;
				wallB = true;
			}
			else if(wallB) {
				wallB = false;
				chosen = true;
				mainPanel.run();
			}
		}
	}

	public void run() {
		repaint();
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
					System.out.println("done: " + path);
					done = true;
				}

				openSet.remove(current);
				closedSet.add(current);

				diagonalWallCheck(current);

				ArrayList<Spot> neighbors = current.neighbors;
				for(int i = 0; i < neighbors.size(); i++) {
					Spot neighbor = neighbors.get(i);
					double tempG = current.g + 1;

					if(walls.contains(neighbor)){
						continue;
					}

					if(!openSet.contains(neighbor) && !closedSet.contains(neighbor)) {
						neighbor.previous = current;
						neighbor.g = tempG;
						neighbor.f = neighbor.g + heurisitic(neighbor, end);
						openSet.add(neighbor);
					}
					else {
						if(tempG < neighbor.g) {
							neighbor.previous = current;
							neighbor.g = tempG;
							neighbor.f = neighbor.g + heurisitic(neighbor, end);

							if(closedSet.contains(neighbor)) {
								closedSet.remove(neighbor);
								openSet.add(neighbor);
							}
						}
					}

					openSet.remove(current);
					closedSet.add(current);
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

			if(done){
			}
			else{
				run();
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
		int x = current.getX();
		int y = current.getY();
		Spot xR = null, xL = null, yD = null, yU = null;

		if(x < cols - 1) {
			xR = grid[current.getX() + 1][current.getY()];
		}
		if(x > 0) {
			xL = grid[current.getX() - 1][current.getY()];
		}
		if(y < rows - 1) {
			yD = grid[current.getX()][current.getY() + 1];
		}
		if(y > 0) {
			yU = grid[current.getX()][current.getY() - 1];
		}
		
		if(walls.contains(xR) && walls.contains(yD)) {
			current.removeNeighbor(grid[current.getX() + 1][current.getY() + 1]);
		}
		if(walls.contains(xL) && walls.contains(yU)) {
			current.removeNeighbor(grid[current.getX() - 1][current.getY() - 1]);
		}
		if(walls.contains(xR) && walls.contains(yU)) {
			current.removeNeighbor(grid[current.getX() + 1][current.getY() - 1]);
		}
		if(walls.contains(xL) && walls.contains(yD)) {
			current.removeNeighbor(grid[current.getX() - 1][current.getY() + 1]);
		}
	}

	public void draw(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		col = x / tileWidth;
		row = y / tileHeight;

		if(!done) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				if(wallB) {
					if(!walls.contains(grid[col][row]) 
						&& start != grid[col][row]
						&& end != grid[col][row]) {
						walls.add(grid[col][row]);
					}
				}
				if(endB) {
					Spot temp = grid[col][row];

					if(temp != start) {
						end = temp;
					}
				}
				if(!startB) {
					start = grid[col][row];
				}
			}
			if(SwingUtilities.isRightMouseButton(e)) {
				walls.remove(grid[col][row]);
			}
			run();
		}
	}

	public Main() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				draw(e);
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				draw(e);
			}
		});
	}
}
