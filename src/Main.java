import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel {

	public static int cols = 25;
	public static int rows = 25;
	public static Spot[][] grid = new Spot[cols][rows];
	public static ArrayList<Spot> openSet = new ArrayList<>();
	public static ArrayList<Spot> closedSet = new ArrayList<>();
	public static ArrayList<Spot> walls = new ArrayList<>();
	public static boolean done = false;
	public static Spot start = null;
	public static Spot end = null;
	private static boolean startB = false;
	private static boolean endB = false;
	private static boolean wallB = false;
	public static JButton nextButton, pauseButton, clearWalls, restartButton;
	private static boolean chosen = false;
	private static boolean paused = false;
	private static Main mainPanel  = new Main();
	private static Spot current = null;
	public static Timer timer = new Timer(5,  new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			mainPanel.repaint();
		}
	});

	public static void main(String[] args) {
		JPanel container = new JPanel();
		JPanel controlPanel = new JPanel();
		JPanel nextPanel = new JPanel();
		JPanel pausePanel = new JPanel();
		JPanel clearPanel = new JPanel();
		JPanel restartPanel = new JPanel();
		JFrame frame = new JFrame("Pathfinder");

		restartButton = addNewButton("Restart");
		restartButton.setVisible(true);
		restartPanel.setLayout(new BoxLayout(restartPanel, BoxLayout.PAGE_AXIS));
		restartButton.setAlignmentX(CENTER_ALIGNMENT);
		restartPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		restartPanel.add(restartButton);
		restartPanel.setBorder(BorderFactory.createTitledBorder("Restart"));

		clearWalls = addNewButton("Clear");
		clearWalls.setVisible(true);
		clearPanel.setLayout(new BoxLayout(clearPanel, BoxLayout.PAGE_AXIS));
		clearWalls.setAlignmentX(CENTER_ALIGNMENT);
		clearPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		clearPanel.add(clearWalls);
		clearPanel.setBorder(BorderFactory.createTitledBorder("Clear Walls"));

		pauseButton = addNewButton("Pause");
		pauseButton.setVisible(true);
		pausePanel.setLayout(new BoxLayout(pausePanel, BoxLayout.PAGE_AXIS));
		pauseButton.setAlignmentX(CENTER_ALIGNMENT);
		pausePanel.add(Box.createRigidArea(new Dimension(0, 50)));
		pausePanel.add(pauseButton, BorderLayout.CENTER);
		pausePanel.setBorder(BorderFactory.createTitledBorder("Pause"));

		nextButton = addNewButton("Next");
		nextButton.setVisible(true);
		nextPanel.setLayout(new BoxLayout(nextPanel, BoxLayout.PAGE_AXIS));
		nextButton.setAlignmentX(CENTER_ALIGNMENT);
		nextPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		nextPanel.add(nextButton, BorderLayout.CENTER);
		nextPanel.setBorder(BorderFactory.createTitledBorder("Next"));

		controlPanel.setPreferredSize(new Dimension(139, 639));
		controlPanel.setLayout(new GridLayout(4, 1));
		controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		controlPanel.add(nextPanel);
		controlPanel.add(pausePanel);
		controlPanel.add(clearPanel);
		controlPanel.add(restartPanel);

		mainPanel.setPreferredSize(new Dimension(829, 639));

		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(controlPanel);
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
				else if(bName.equals("Clear")) {
					clearAction(evt);
				}
				else if(bName.equals("Restart")) {
					restartAction(evt);
				}
        	}
		});
		
        return button;
	}

	//Action for restart button:
    public static void restartAction(ActionEvent e) {
		try{
			openSet.clear();
			closedSet.clear();
			walls.clear();
			AStar.path.clear();
			current = null;
			start = null;
			end = null;
			startB = false;
			endB = false;
			wallB = false;
			chosen = false;
			done = false;

			//clear each spots previous property
			//so that a new path can be found
			//without this a memory exception occurs
			for(int i = 0; i < cols; i++) {
				for(int j = 0; j < rows; j++) {
					grid[i][j].previous = null;
				}
			}

			timer.stop();
			timer.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	//Action for pause button:
    public static void clearAction(ActionEvent e) {
		if(!done) walls.clear();
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
			restartButton.setEnabled(false);
			new AStar().search(openSet, closedSet, walls, end, current, g);

			for (int i = 0; i < walls.size(); i++) {
				walls.get(i).draw(Color.BLACK, g);
			}

			//sleeps to slow down the pathfiding animation
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			start.draw(Color.PINK, g);
			end.draw(Color.PINK, g);
		}
	}

	//Draw method to determine what mouse button is clicked
	//then it draws or removes spots on the grid
	public void draw(MouseEvent e) {
		int tileWidth = 800 / cols;
		int tileHeight = 600 / rows;
		int x=e.getX();
		int y=e.getY();
		int col = x / tileWidth;
		int row = y / tileHeight;

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
			//only removes walls
			if(SwingUtilities.isRightMouseButton(e)) {
				walls.remove(grid[col][row]);
			}
		}
	}

	public Main() {
		//Motion listener for a single click
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				draw(e);
			}
		});
		//Motion listener for a click and drag
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				draw(e);
			}
		});
	}
}