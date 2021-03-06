package entity;

import constant.Constants;
import java.util.*;
/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
 */
public class Map {
	private Cell[][] map;
	private int robPosRow; // start pos of rob
	private int robPosCol; // start pos of rob

	private Cell startGoal;

	public Map() {
		this.map = new Cell[Constants.MAX_ROW][Constants.MAX_COL];

		// setting all box with 0 to indicate new map
		for (int i = 0; i < Constants.MAX_ROW; i++) {
			for (int j = 0; j < Constants.MAX_COL; j++) {
				this.map[i][j] = new Cell(i, j);
			}
		}

		startGoal = this.map[1][1];

		// setting start position of robot (center of robot)
		this.robPosRow = 1;
		this.robPosCol = 1;
	}

	/**
	 * @return Map 2D array of cell object which represent the arena
	 */
	public Cell[][] getMap() {
		return this.map;
	}

	/**
	 * @return StartGoal cell
	 */
	public Cell getStartGoalPosition() {
		return this.startGoal;
	}
	
	/**
	 * @return robot's row
	 */
	public int getStartRow() {
		return this.robPosRow;
	}

	/**
	 * @return robot's column
	 */
	public int getStartCol() {
		return this.robPosCol;
	}

	/**
	 * This method sets the robot's row and column
	 * @param startRow
	 * @param startCol
	 */
	public void setStartRowCol(int startRow, int startCol) {
		this.robPosRow = startRow;
		this.robPosCol = startCol;
	}

	/**
	 * This method sets the target cell in the map
	 * @param obsRow The obstacle's row
	 * @param obsCol The obstacle's column
	 * @param dir The obstacle's direction
	 * @return
	 */
	public Cell setMapTargetCell(int obsRow, int obsCol, int dir) { // 1=Top, 2=Bottom, 3=Left, 4=Right
		Cell newCell;
		this.map[obsRow][obsCol].setObstacle(dir);
		switch (dir) {
		case 1:
			newCell = new Cell(obsRow + 4, obsCol);
			newCell.setHeadDir(2);
			this.map[obsRow + 4][obsCol].setTargetCell(true);
			this.map[obsRow + 4][obsCol].setHeadDir(2);
			return newCell;
		case 2:
			newCell = new Cell(obsRow - 4, obsCol);
			newCell.setHeadDir(1);
			this.map[obsRow - 4][obsCol].setTargetCell(true);
			this.map[obsRow - 4][obsCol].setHeadDir(1);
			return newCell;
		case 3:
			newCell = new Cell(obsRow, obsCol + 4);
			newCell.setHeadDir(4);
			this.map[obsRow][obsCol + 4].setTargetCell(true);
			this.map[obsRow][obsCol + 4].setHeadDir(4);
			return newCell;
		case 4:
			newCell = new Cell(obsRow, obsCol - 4);
			newCell.setHeadDir(3);
			this.map[obsRow][obsCol - 4].setTargetCell(true);
			this.map[obsRow][obsCol - 4].setHeadDir(3);
			return newCell;
		}
		return null;
	}
	
	/**
	 * This method set the target cell based on the obstacle's cell
	 * @param c The obstacle cell
	 * @return
	 */
	public Cell targetToObstacle(Cell c) {
		int row = c.getRow();
		int col = c.getCol();
		switch (c.getHeadDir()) {
		case 1:
			row = row + 4;
			break;
		case 2:
			row = row - 4;
			break;
		case 3:
			col = col + 4;
			break;
		case 4:
			col = col - 4;
			break;
		}
		return this.map[row][col];
	}

	/**
	 * This method set the obstacles on the map
	 * @param obstacles The obstacle list
	 */
	public void setMapObstacle(ArrayList<Cell> obstacles) {
		for (int i = 0; i < obstacles.size(); i++) {
			try {
				this.getMap()[obstacles.get(i).getRow()][obstacles.get(i).getCol()] = (Cell) obstacles.get(i).clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method set the h cost for the cells
	 * @param targetRow The target cell's row
	 * @param targetCol The target cell's col
	 */
	public void setMapHeuristicCost(int targetRow, int targetCol) {
		for (int i = 0; i < this.getMap().length; i++) {
			for (int j = 0; j < this.getMap()[i].length; j++) {
				double heuristicCost = Math.abs(i - targetRow) + Math.abs(j - targetCol);
				this.getMap()[i][j].setHeuristicCost(heuristicCost);
				this.getMap()[i][j].setSolution(false);
			}
		}
	}

	/**
	 * This method set the robot's position in map
	 * @param r The robot
	 */
	public void setRobotMap(Robot r) {
		// final cost
		for (int i = r.getPosRow() - 1; i < r.getPosRow() + 2; i++) {
			for (int j = r.getPosCol() - 1; j < r.getPosCol(); j++) {
				this.getMap()[i][j].setFinalCost(0);
			}
		}
		// set start head
		int dirHead = r.ToDirectionHead(r.getCurrDir());
		this.getMap()[r.getPosRow()][r.getPosCol()].setHeadDir(dirHead);
		System.out.println("-----map.setrobotmap---");
		System.out.print(r.getCurrDir());
		System.out.println(this.getMap()[r.getPosRow()][r.getPosCol()].getHeadDir());
		System.out.println("----End-------");
	}

	/**
	 * This method set the obstacles in the map
	 * @param row The obstacle's row
	 * @param col The obstacle's column
	 * @param obsDir The obstacle's direction
	 */
	public void setObstacle(int row, int col, int obsDir) {
		this.getMap()[row][col].setObstacle(true);
		this.getMap()[row][col].setObsDir(obsDir);
	}


	


}
