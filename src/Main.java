import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel {

	public static int cols = 25;
	public static int rows = 25;
	public static int tileWidth = 800 / cols;
	public static int tileHeight = 600 / rows;
	public static Spot[][] grid = new Spot[cols][rows];
	public static ArrayList<Spot> openSet = new ArrayList<>();
	public static ArrayList<Spot> closedSet = new ArrayList<>();
	public static ArrayList<Spot> walls = new ArrayList<>();
	public static boolean done = false;
	private static int col = 0;
	private static int row = 0;
	public static Spot start = null;
	public static Spot end = null;
	private static boolean startB = false;
	private static boolean endB = false;
	private static boolean wallB = false;
	private static JButton nextButton, pauseButton, clearWalls;
	private static boolean chosen = false;
	private static boolean paused = false;
	private static Main mainPanel  = new Main();
	public static Timer timer = new Timer(5,  new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			mainPanel.repaint();
		}
	});

	public static void main(String[] args) {
		JPanel container = new JPanel();
		JPanel panel1 = new JPanel();
		JFrame frame = new JFrame("Pathfinder");

		clearWalls = addNewButton("Clear Walls");
		clearWalls.setVisible(true);

		pauseButton = addNewButton("Pause");
		pauseButton.setVisible(true);

		nextButton = addNewButton("Next");
		nextButton.setVisible(true);

		panel1.setPreferredSize(new Dimension(139, 639));
		//panel1.setLayout(new GridLayout(4, 1));
		panel1.setBorder(BorderFactory.createTitledBorder("Controls"));
		panel1.add(nextButton);
		panel1.add(pauseButton);
		panel1.add(clearWalls);

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

		timer.start();
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
				else if(bName.equals("Clear Walls")) {
					clearAction(evt);
				}
        	}
		});
		
        return button;
	}

	//Action for pause button:
    public static void clearAction(ActionEvent e) {
		if(!done) {
			try{
				walls.clear();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//Action for pause button:
    public static void pauseAction(ActionEvent e) {
		if(!done) {
			try{
				if(!paused) {
					timer.stop();
					paused = true;
					pauseButton.setText("Resume");
				}
				else {
					timer.start();
					paused = false;
					pauseButton.setText("Pause");
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
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
				clearWalls.setEnabled(false);
			}
		}
	}
	
	public void paintComponent (Graphics g) {
		Spot current = null;
		super.paintComponent(g);

		//draws base blank grid
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				grid[i][j].draw(null, g);
			}
		}

		//if start and end have not been set draw 
		//walls, start, and end nodes
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
			new AStar().search(openSet, closedSet, walls, end, current, g);

			for (int i = 0; i < walls.size(); i++) {
				walls.get(i).draw(Color.BLACK, g);
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			start.draw(Color.PINK, g);
			end.draw(Color.PINK, g);
		}
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