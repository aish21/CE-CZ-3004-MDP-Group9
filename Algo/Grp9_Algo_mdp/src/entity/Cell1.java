package entity;
//import constant.Constants;

public class Cell1 {
	private int row, col;
	private Cell1 parent;
	private double heuristicCost; //hcost
	private double finalCost; //fCost = g+h
	private boolean isObstacle;
	private int obsDir; ////1=N, 2=S, 3=E, 4=W
	private boolean isTargetCell;
	private boolean isSolution; //check if cell is part of the solution
	private int headDir;
	
	public Cell1(int row, int col) {
		this.row = row;
		this.col = col;
		this.isObstacle = false;
		this.isSolution = false;
		this.isTargetCell = false;
		this.headDir = 1;
	}
	
	public int getHeadDir() {
		return this.headDir;
	}
	
	public void setHeadDir(int headDir) {
		this.headDir = headDir;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public void setParent(Cell1 parent) {
		this.parent = parent;
	}
	
	public Cell1 getParent() {
		return this.parent;
	}
	
	public void setHeuristicCost(double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}
	
	public double getHeuristicCost() {
		return this.heuristicCost;
	}
	
	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}
	
	public double getFinalCost() {
		return this.finalCost;
	}
	
	public void setIsObstacleNDirection(boolean isObstacle, int dir) {
		this.isObstacle = isObstacle;
		this.obsDir = dir;
	}
	
	public boolean getIsObstacle() {
		return this.isObstacle;
	}
	
	public int getObsDir(){
		return this.obsDir;
	}
	
	public void setIsTargetCell(boolean isTargetCell) {
		this.isTargetCell = isTargetCell;
	}
	
	public boolean getIsTargetCell() {
		return this.isTargetCell;
	}
	
	public void setIsSolution(boolean isSolution) {
		this.isSolution = isSolution;
	}
	
	public boolean getIsSolution() {
		return this.isSolution;
	}
	
	@Override
	public String toString() {
		return "{" + this.row + ", " + this.col + "}";
	}
}
