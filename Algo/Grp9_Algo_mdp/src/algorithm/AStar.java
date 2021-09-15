package Algorithm;

import java.util.PriorityQueue;
import constant.Constants;
import entity.Map1;
import entity.Cell1;

public class AStar {
	private Map1 mapArena; 
	private PriorityQueue<Cell1> openCells; // set of nodes to be evaluated
	private boolean[][] closedCells;// set of nodes evaluated
	private int startRow, startCol, startHead;
	private int targetRow, targetCol, targetHead;
	
	public AStar(int startRow, int startCol, int tarRow, int tarCol, int[][] blocks, int startHead, int tarHead) { //blocks is an array of obstacles //startHead and tarHead not yet
		this.mapArena = new Map1();
		this.closedCells = new boolean[Constants.MAX_ROW][Constants.MAX_COL];
		this.openCells = new PriorityQueue<Cell1>((Cell1 c1, Cell1 c2) -> {
			return c1.getFinalCost()<c2.getFinalCost() ? -1 : c1.getFinalCost() > c2.getFinalCost() ? 1 : 0;
		});
		
		this.startRow = startRow;
		this.startCol = startCol;
		this.startHead = startHead;
		this.mapArena.setStartRowCol(startRow, startCol);
		this.targetRow = tarRow;
		this.targetCol = tarCol;	
		this.targetHead = tarHead;
		
		for (int i=0; i<this.mapArena.getMap().length; i++) {
			for(int j=0;j<this.mapArena.getMap()[i].length; j++) {
				double heuristicCost = Math.abs(i - tarRow) + Math.abs(j-tarCol);
				mapArena.getMap()[i][j].setHeuristicCost(heuristicCost);
				mapArena.getMap()[i][j].setIsSolution(false);
			}
		}
		for(int i=startRow-1; i<startRow+2; i++) {
			for(int j=startCol-1; j<startCol+2; j++) {
				this.mapArena.getMap()[i][j].setFinalCost(0);
			}
		}
		//this.mapArena.getMap()[startRow][startCol].setFinalCost(0);
		
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
			
			if (current.getRow()+1>=Constants.MAX_ROW || current.getRow()-1<0 || current.getCol()+1>=Constants.MAX_COL || current.getCol()<0) {
				return;
			}
			
			Cell1 t;
			
			switch (current.getHeadDir()) {
				case 1://north
					//backward
					if(current.getRow()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()-2][current.getCol()+i].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()-1][current.getCol()];
							t.setHeadDir(1);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//front
					if(current.getRow()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+2][current.getCol()+i].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
							t.setHeadDir(1);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//turning right
					if(current.getRow()+4 < this.mapArena.getMap().length) {
						if(current.getCol()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].getIsObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()+3];
								t.setHeadDir(3);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					
					//turning left
					if(current.getRow()+4 < this.mapArena.getMap().length) {
						if(current.getCol()-4 >=0) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].getIsObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()-3];
								t.setHeadDir(4);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					break;
				case 2: //south
					//forward
					if(current.getRow()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()-2][current.getCol()+i].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()-1][current.getCol()];
							t.setHeadDir(2);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//backward
					if(current.getRow()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+2][current.getCol()+i].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
							t.setHeadDir(2);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//turning left
					if(current.getRow()-4 < this.mapArena.getMap().length) {
						if(current.getCol()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=-2; i<-5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].getIsObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()-3];
								t.setHeadDir(3);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					
					//turning right
					if(current.getRow()-4 < this.mapArena.getMap().length) {
						if(current.getCol()-4 >=0) {
							boolean noObst = true;
							for(int i=-2; i<-5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].getIsObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()+3];
								t.setHeadDir(4);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					break;
				case 3: //east
					//backward
					if(current.getCol()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()-2].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()-1];
							t.setHeadDir(3);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//front
					if(current.getCol()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+2].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()+1];
							t.setHeadDir(3);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//turning left
					if(current.getCol()+4 < this.mapArena.getMap().length) {
						if(current.getRow()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].getIsObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()+3];
								t.setHeadDir(1);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					
					//turning right
					if(current.getCol()+4 < this.mapArena.getMap().length) {
						if(current.getRow()-4 >=0) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].getIsObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()+3];
								t.setHeadDir(2);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					break;
				case 4:// west
					//forward
					if(current.getCol()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()-2].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()-1];
							t.setHeadDir(4);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//backward
					if(current.getCol()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+2].getIsObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()+1];
							t.setHeadDir(4);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST);
						}
//						else {
//							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//						}	
					}
					
					//turning right
					if(current.getCol()-4 < this.mapArena.getMap().length) {
						if(current.getRow()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=-2; i<-5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+j].getIsObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()-3];
								t.setHeadDir(1);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					
					//turning left
					if(current.getCol()-4 < this.mapArena.getMap().length) {
						if(current.getRow()-4 >=0) {
							boolean noObst = true;
							for(int i=-2; i<-5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].getIsObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()-3];
								t.setHeadDir(2);
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST);
							}
						}
					}
					break;
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
		AStar astar = new AStar(1, 1, 13, 15, new int[][] {
			{10,4,1}, {7,4,1}, {15,18,1},{3,7,1}, {4,10,1},{13,8,1}
		}, 1, 4);
		astar.display();
		astar.process();
		astar.displayScores();
		astar.displaySolution();
		
	}
}
