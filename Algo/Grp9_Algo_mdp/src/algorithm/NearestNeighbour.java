package algorithm;

import constant.Constants.DIRECTION;
import entity.Robot;
import entity.Cell;
import java.util.*;
import java.lang.Math;


/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2020-10-19
 */


public class NearestNeighbour {

	
	/**
	 * @param list of obstacles
	 * @return return sorted list of obstacles based on their nearest neighbour
	 */
	
	public static ArrayList<Cell> findNearestNeighbour(List<Cell> obstacles) {
		ArrayList<Cell> results = new ArrayList<Cell>();
		results.addAll(obstacles);
		Collections.sort(results);
		return results;
	}

	/**
	 * @param list of targets
	 * @param r robot
	 * @return updated list of target with their heuristic cost
	 */
	public static List<Cell> calculateDistance(List<Cell> targets, Robot r) {
		//System.out.println("-----nearestNeighbour.calculateDist----");
		for (int i = 0; i < targets.size(); i++) {
			int robotRowVal = r.getPosRow();
			int robotColVal = r.getPosCol();
			int tarRow = targets.get(i).getRow();
			int tarCol = targets.get(i).getCol();

			// double heuristicCost =
			// Math.sqrt(Math.pow(Math.abs(obsRow-robotRowVal),2)+Math.pow(Math.abs(obsCol-robotColVal),2));
			double heuristicCost = Math.abs(robotRowVal - tarRow) + Math.abs(robotColVal - tarCol);
			targets.get(i).setHeuristicCost(heuristicCost);
//			System.out.println("Cell: [" + targets.get(i).getRow() + "][" + targets.get(i).getCol() + "] cost: "
//					+ targets.get(i).getHeuristicCost());
		}
		System.out.println("-----End----");
		return targets;
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
		obsList = calculateDistance(obsList, r);

		// get nearest Neighbour
		ArrayList<Cell >nnList = findNearestNeighbour(obsList);
		
		System.out.println("main: " + nnList);

		
	}

}
