package algorithm;

import constant.Constants;
import constant.Constants.DIRECTION;
import entity.Map;
import entity.Robot;
import entity.Cell;
import java.util.*;
import java.lang.Math;

public class NearestNeighbour {

	// trying to implement
	public static ArrayList<Cell> findNearestNeighbour(List<Cell> obstacles) {
		ArrayList<Cell> results = new ArrayList<Cell>();
		results.addAll(obstacles);
		Collections.sort(results);
		return results;
	}

	public static List<Cell> calcualteDistance(List<Cell> obstacles, Robot r) {
		System.out.println("-----nearestNeighbour.calculateDist----");
		for (int i = 0; i < obstacles.size(); i++) {
			int robotRowVal = r.getPosRow();
			int robotColVal = r.getPosCol();
			int obsRow = obstacles.get(i).getRow();
			int obsCol = obstacles.get(i).getCol();

			// double heuristicCost =
			// Math.sqrt(Math.pow(Math.abs(obsRow-robotRowVal),2)+Math.pow(Math.abs(obsCol-robotColVal),2));
			double heuristicCost = Math.abs(robotRowVal - obsRow) + Math.abs(robotColVal - obsCol);
			obstacles.get(i).setHeuristicCost(heuristicCost);
			System.out.println("Cell: [" + obstacles.get(i).getRow() + "][" + obstacles.get(i).getCol() + "] cost: "
					+ obstacles.get(i).getHeuristicCost());
		}
		System.out.println("-----End----");
		return obstacles;
	}

	public static void main(String[] args) {
		Robot r = new Robot(1, 1, DIRECTION.NORTH);

		// cell
		Cell c1 = new Cell(1, 9);
		c1.setObstacle(true);

		Cell c2 = new Cell(12, 1);
		c2.setObstacle(true);

		Cell c3 = new Cell(7, 15);
		c3.setObstacle(true);

		Cell c4 = new Cell(15, 9);
		c4.setObstacle(true);

		Cell c5 = new Cell(18, 18);
		c5.setObstacle(true);

		// add cell to list
		List<Cell> obsList = new ArrayList<Cell>();
		obsList.add(c5);
		obsList.add(c2);
		obsList.add(c1);
		obsList.add(c4);
		obsList.add(c3);

		// get distance
		obsList = calcualteDistance(obsList, r);

		// get nearest Neighbour
		ArrayList<Cell >nnList = findNearestNeighbour(obsList);
		
		System.out.println("main: " + nnList);

		
	}

}
