package entity;
//import constant.Constants;

import constant.Constants;

public class Cell implements Comparable<Cell>, Cloneable {
	private int row, col;
	private Cell parent;
	private double heuristicCost; //hcost
	private double finalCost; //fCost = g+h
	private boolean obstacle;
	private int obsDir; //0= notObstacle 1=Top, 2=Bottom, 3=Right, 4=Left
	private boolean targetCell;
	private boolean solution; //check if cell is part of the solution
	private int headDir; //0= notObstacle 1=Top, 2=Bottom, 3=Right, 4=Left
	
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.obstacle = false;
		this.solution = false;
		this.targetCell = false;
		this.headDir = 1;
		this.obsDir = 0;
	}
	
	
	public void isNotObstacle() {
		this.obsDir = 0;
		this.obstacle = false;
	}
	
	public void setObstacle(int dir) {
		this.obstacle = true;
		this.obsDir = dir;
	}
	
    /**
	 * @return the cell row
	 */
	public int getRow() {
		return row;
	}


	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}



	/**
	 * @return the cell column
	 */
	public int getCol() {
		return col;
	}



	/**
	 * @param col the cell column to set
	 */
	public void setCol(int col) {
		this.col = col;
	}



	/**
	 * @return the parent
	 */
	public Cell getParent() {
		return parent;
	}



	/**
	 * @param parent the parent to set
	 */
	public void setParent(Cell parent) {
		this.parent = parent;
	}



	/**
	 * @return the heuristicCost
	 */
	public double getHeuristicCost() {
		return heuristicCost;
	}



	/**
	 * @param heuristicCost the heuristicCost to set
	 */
	public void setHeuristicCost(double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}



	/**
	 * @return the finalCost
	 */
	public double getFinalCost() {
		return finalCost;
	}



	/**
	 * @param finalCost the finalCost to set
	 */
	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}



	/**
	 * @return the obstacle
	 */
	public boolean isObstacle() {
		return obstacle;
	}



	/**
	 * @param obstacle the obstacle to set
	 */
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}



	/**
	 * @return the obsDir
	 */
	public int getObsDir() {
		return obsDir;
	}



	/**
	 * @param obsDir the obsDir to set
	 */
	public void setObsDir(int obsDir) {
		this.obsDir = obsDir;
	}



	/**
	 * @return the targetCell
	 */
	public boolean isTargetCell() {
		return targetCell;
	}



	/**
	 * @param targetCell the targetCell to set
	 */
	public void setTargetCell(boolean targetCell) {
		this.targetCell = targetCell;
	}



	/**
	 * @return the solution
	 */
	public boolean isSolution() {
		return solution;
	}



	/**
	 * @param solution the solution to set
	 */
	public void setSolution(boolean solution) {
		this.solution = solution;
	}



	/**
	 * @return the headDir
	 */
	public int getHeadDir() {
		return headDir;
	}



	/**
	 * @param headDir the headDir to set
	 */
	public void setHeadDir(int headDir) {
		this.headDir = headDir;
	}



	/**
     * @return true if cell is within the width and length of arena
     */
    public boolean isCellValid() {
        if (this.row >= Constants.MAX_ROW) {
            return false;
        }
        if (this.col >= Constants.MAX_COL) {
            return false;
        }
        if (this.row < 0) {
            return false;
        }
        if (this.col < 0) {
            return false;
        }

        return true;
    }

	@Override
	public String toString() {
		return "{" + this.row + ", " + this.col + ", " + this.headDir + "}";
	}


	@Override
	public int compareTo(Cell o) {
		if(this.getHeuristicCost() > o.getHeuristicCost()) {
			return 1;
		}
		else if(this.getHeuristicCost() < o.getHeuristicCost()){
			return -1;
		}
		else {
			return 0;
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Cell c = null;
		c = (Cell) super.clone();
		return c;
	}
	
}
