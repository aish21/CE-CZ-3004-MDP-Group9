package algorithm;

import java.util.PriorityQueue;
import constant.Constants;
import entity.Map1;
import entity.Cell1;

public class AStar {
	private Map1 mapArena; 
	private PriorityQueue<Cell1> openCells; // set of nodes to be evaluated
	private boolean[][] closedCells;// set of nodes evaluated
	private int startRow, startCol;
	private int targetRow, targetCol;
	
	public AStar(int startRow, int startCol, int tarRow, int tarCol, int[][] blocks) { //blocks is an array of obsatcles
		this.mapArena = new Map1();
		this.closedCells = new boolean[Constants.MAX_ROW][Constants.MAX_COL];
		this.openCells = new PriorityQueue<Cell1>((Cell1 c1, Cell1 c2) -> {
			return c1.getFinalCost()<c2.getFinalCost() ? -1 : c1.getFinalCost() > c2.getFinalCost() ? 1 : 0;
		});
		
		this.startRow = startRow;
		this.startCol = startCol;
		this.mapArena.setStartRowCol(startRow, startCol);
		this.targetRow = tarRow;
		this.targetCol = tarCol;	
		
		for (int i=0; i<this.mapArena.getMap().length; i++) {
			for(int j=0;j<this.mapArena.getMap()[i].length; j++) {
				double heuristicCost = Math.abs(i - tarRow) + Math.abs(j-tarCol);
				mapArena.getMap()[i][j].setHeuristicCost(heuristicCost);
				mapArena.getMap()[i][j].setIsSolution(false);
			}
		}
		
		this.mapArena.getMap()[startRow][startCol].setFinalCost(0);
		for(int i=0; i<blocks.length; i++) {
			this.mapArena.setObstaclesNTargetPos(blocks[i][0], blocks[i][1], blocks[i][2]);
		}
	}
	
	public void updateCostIfNeeded(Cell1 current, Cell1 tar, double cost) {
		if(tar==null || closedCells[tar.getRow()][tar.getCol()]) {
			return;
		}
		
		double tarFinalCost = tar.getHeuristicCost() + cost;
		boolean isOpen = this.openCells.contains(tar);
		
		if(!isOpen || tarFinalCost < tar.getFinalCost()) {
			tar.setFinalCost(tarFinalCost);
			tar.setParent(current);
			if(!isOpen) {
				this.openCells.add(tar);
			}
		}
	}
	
	public void process() {
		//we add the start location to open list
		openCells.add(this.mapArena.getMap()[this.startRow][this.startCol]);
		Cell1 current;
		
		while(true) {
			current = openCells.poll();
			if(current == null) {
				break;
			}
			closedCells[current.getRow()][current.getCol()] = true;
			
			if(current.equals(this.mapArena.getMap()[this.targetRow][this.targetCol])) {
				return;
			}
			
			Cell1 t;
			//backward
			if(current.getRow()-1 >= 0) {
				t = this.mapArena.getMap()[current.getRow()-1][current.getCol()];
				updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST);
				
//				//backward left
//				if(current.getCol()-1 >= 0) {
//					t = this.mapArena.getMap()[current.getRow()-1][current.getCol()-1];
//					updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
//				}
//				
//				//backward right
//				if(current.getCol()+1 < this.mapArena.getMap()[0].length) {
//					t = this.mapArena.getMap()[current.getRow()-1][current.getCol()+1];
//					updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
//				}
			}
			
			//left
			if(current.getCol() -1 >= 0) {
				t = this.mapArena.getMap()[current.getRow()][current.getCol()-1];
				updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST);
			}
			//right
			if(current.getCol()+1 < this.mapArena.getMap()[0].length) {
				t = this.mapArena.getMap()[current.getRow()][current.getCol()+1];
				updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
			}
			//front
			if(current.getRow()+1 < this.mapArena.getMap().length) {
				t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
				updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
				
				//front left
				if(current.getCol()-1>=0) {
					t = this.mapArena.getMap()[current.getRow()+1][current.getCol()-1];
					updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
				}
				//front right
				if(current.getCol()+1<this.mapArena.getMap()[0].length) {
					t = this.mapArena.getMap()[current.getRow()+1][current.getCol()+1];
					updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
				}
			}
		}
	}
	
	public void display() {
		System.out.println("Map Arena :");
		
		for(int i=0; i<this.mapArena.getMap().length; i++) {
			for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
				if(i == this.startRow && j == this.startCol)
					System.out.print("SO  "); //Source Cell
				else if(i==this.targetRow && j == this.targetCol)
					System.out.print("DE  "); //destination Cell
				else if(!this.mapArena.getMap()[i][j].getIsObstacle()) 
					System.out.printf("%-3d ", 0);				
				else
					System.out.print("BL  "); //obstacle cell
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void displayScores() {
		System.out.println("\nScores for Cells : ");
		for (int i=0; i<this.mapArena.getMap().length; i++) {
			for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
				if(!this.mapArena.getMap()[i][j].getIsObstacle()) {
					System.out.printf("%-3s ", this.mapArena.getMap()[i][j].getFinalCost());
				}
				else {
					System.out.print("BL  ");
				}
				
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void displaySolution() {
		if(closedCells[this.targetRow][this.targetCol]) {
			System.out.println("Path: ");
			Cell1 current = this.mapArena.getMap()[this.targetRow][this.targetCol];
			System.out.println(current);
			this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setIsSolution(true);
			
			while(!current.getParent().getIsObstacle()) {
				System.out.println(" -> " + current.getParent());
				this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setIsSolution(true);;
				current = current.getParent();
				if(current.equals(this.mapArena.getMap()[this.startRow][this.startCol])) {
					break;
				}
			}
			
			System.out.println("\n");
			
//			for (int i=0; i<this.mapArena.getMap().length; i++) {
//				for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
//					if(!this.mapArena.getMap()[i][j].getIsObstacle()) {
//						System.out.printf("%-3d ", this.mapArena.getMap()[i][j].getFinalCost());
//					}
//					else {
//						System.out.print("BL  ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
			for(int i=0; i<this.mapArena.getMap().length; i++) {
				for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
					if(i == this.startRow && j == this.startCol)
						System.out.print("SO  "); //Source Cell
					else if(i==this.targetRow && j == this.targetCol)
						System.out.print("DE  "); //destination Cell
					else if(!this.mapArena.getMap()[i][j].getIsObstacle()) 
						System.out.printf("%-3s ", this.mapArena.getMap()[i][j].getIsSolution() ? "X" : "0");				
					else
						System.out.print("BL  "); //obstacle cell
				}
				System.out.println();
			}
			System.out.println();
		}
		else {
			System.out.println("No possible path");
		}
	}
	
	public static void main(String[] args) {
		AStar astar = new AStar(1, 1, 15, 19, new int[][] {
			{10,4,1}, {7,4,1}, {15,18,1},{3,7,1}, {4,10,1},{13,8,1}
		});
		astar.display();
		astar.process();
		astar.displayScores();
		astar.displaySolution();
		
	}
}
