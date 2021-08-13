import java.util.ArrayList;
import java.awt.*;

public class AStar {
    public Main main = new Main();
    public static ArrayList<Spot> path;

    public void search(ArrayList<Spot> openSet, ArrayList<Spot> closedSet, ArrayList<Spot> walls, Spot end, Spot current, Graphics g) {
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
                main.done = true;
                main.timer.stop();
            }

            openSet.remove(current);
            closedSet.add(current);

            main.diagonalWallCheck(current);

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
    }

    public static double heurisitic(Spot a, Spot b) {
		final double D = 1;
		final double D2 = 1.414;
		double dx = Math.abs(a.x - b.x); 
		double dy = Math.abs(a.y - b.y);
		return D * Math.max(dx, dy) + (D2 - D) * Math.min(dx, dy);
	}
}