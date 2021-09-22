package algorithm;

import java.util.PriorityQueue;
import constant.Constants;
import constant.Constants.DIRECTION;
import entity.Map;
import entity.Robot;
import entity.Cell;

import java.util.ArrayList;
import java.util.List;

public class AStar {
	private Map mapArena; 
	private PriorityQueue<Cell> openCells; // set of nodes to be evaluated
	private boolean[][] closedCells; // set of nodes evaluated
	private int targetRow, targetCol, targetHead;
	private Robot robot;
	
	public AStar(Map m, Robot r, int targetRow, int targetCol, int targetHead) { 
		this.mapArena = m;
		this.robot = r;
		this.closedCells = new boolean[Constants.MAX_ROW][Constants.MAX_COL];
		this.openCells = new PriorityQueue<Cell>((Cell c1, Cell c2) -> {
			return c1.getFinalCost()<c2.getFinalCost() ? -1 : c1.getFinalCost() > c2.getFinalCost() ? 1 : 0;
		});
		this.mapArena.setStartRowCol(r.getPosRow(), r.getPosCol());
		this.targetRow = targetRow;
		this.targetCol = targetCol;	
		this.targetHead = targetHead;
		
		for (int i=0; i<this.mapArena.getMap().length; i++) {
			for(int j=0;j<this.mapArena.getMap()[i].length; j++) {
				double heuristicCost = Math.abs(i - targetRow) + Math.abs(j-targetCol);
				mapArena.getMap()[i][j].setHeuristicCost(heuristicCost);
				mapArena.getMap()[i][j].setSolution(false);
			}
		}
		this.mapArena.getMap()[r.getPosRow()][r.getPosCol()].setHeadDir(r.ToDirectionHead(r.getCurrDir()));	
	}
	
	public void updateCostIfNeeded(Cell current, Cell tar, double cost, int tarHead) {
		if(tar==null || closedCells[tar.getRow()][tar.getCol()]) {
			return;
		}
		
		double tarFinalCost = tar.getHeuristicCost() + cost;
		boolean isOpen = this.openCells.contains(tar);
		
		if(!isOpen || tarFinalCost < tar.getFinalCost()) {
			tar.setFinalCost(tarFinalCost);
			tar.setParent(current);
			tar.setHeadDir(tarHead);
			if(!isOpen) {
				this.openCells.add(tar);
			}
		}
	}
	
	public void process() {
		//we add the start location to open list
		openCells.add(this.mapArena.getMap()[this.robot.getPosRow()][this.robot.getPosCol()]);
		Cell current;
		
		while(true) {
			current = openCells.poll();
			if(current == null) {
				break;
			}
			closedCells[current.getRow()][current.getCol()] = true;

			//this part still got problem
			if(current.equals(this.mapArena.getMap()[this.targetRow][this.targetCol]) && current.getHeadDir() == this.targetHead) {
				return;
			}
			
			if (current.getRow()+1>=Constants.MAX_ROW || current.getRow()-1<0 || current.getCol()+1>=Constants.MAX_COL || current.getCol()<0) {
				return;
			}
			
			Cell t;
			
			switch (current.getHeadDir()) {
				case 1://north
					//backward
					if(current.getRow()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()-2][current.getCol()+i].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()-1][current.getCol()];
							//t.setHeadDir(1);
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 1);
						}
					}
					
					//front
					if(current.getRow()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+2][current.getCol()+i].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 1);
						}
					}
					
					//turning right
					if(current.getRow()+4 < this.mapArena.getMap().length) {
						if(current.getCol()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].isObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()+3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 3);
							}
						}
					}
					
					//turning left
					if(current.getRow()+4 < this.mapArena.getMap().length) {
						if(current.getCol()-4 >=0) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].isObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()-3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 4);
							}
						}
					}
					break;
				case 2: //south
					//forward
					if(current.getRow()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()-2][current.getCol()+i].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()-1][current.getCol()];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 2);
						}
					}
					
					//backward
					if(current.getRow()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+2][current.getCol()+i].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 2);
						}
					}
					
					//turning left
					if(current.getRow()-4 >= 0) {
						if(current.getCol()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=-4; i<-1; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].isObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()+3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 3);
							}
						}
					}
					
					//turning right
					if(current.getRow()-4 >= 0) {
						if(current.getCol()-4 >=0) {
							boolean noObst = true;
							for(int i=-4; i<-1; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+j].isObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()-3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 4);
							}
						}
					}
					break;
				case 3: //east
					//backward
					if(current.getCol()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()-2].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()-1];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 3);
						}
					}
					
					//front
					if(current.getCol()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+2].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()+1];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 3);
						}
					}
					
					//turning left
					if(current.getCol()+4 < this.mapArena.getMap().length) {
						if(current.getRow()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].isObstacle()) {
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()+3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 1);
							}
						}
					}
					
					//turning right
					if(current.getCol()+4 < this.mapArena.getMap().length) {
						if(current.getRow()-4 >=0) {
							boolean noObst = true;
							for(int i=2; i<5; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].isObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()+3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 2);
							}
						}
					}
					break;
				case 4:// west
					//forward
					if(current.getCol()-2 >= 0) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()-2].isObstacle()) {
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()-1];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 4);
						}	
					}
					
					//backward
					if(current.getCol()+2 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for(int i=-1; i<2; i++) {
							if(this.mapArena.getMap()[current.getRow()+i][current.getCol()+2].isObstacle()) {
								System.out.println("Obs: {" + (current.getRow()+i) + ", "+ (current.getCol()+2) + "}");
								noObst = false;
							}
						}
						if(noObst) {
							t = this.mapArena.getMap()[current.getRow()][current.getCol()+1];
							updateCostIfNeeded(current, t, current.getFinalCost()+Constants.MOVE_COST, 4);
						}
					}
					
					//turning right
					if(current.getCol()-4 >= 0) {
						if(current.getRow()+4 < this.mapArena.getMap().length) {
							boolean noObst = true;
							for(int i=-4; i<-1; i++) {
								for(int j=-1; j<5; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].isObstacle()) {
										
										noObst = false;
									}
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()+3][current.getCol()-3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 1);
							}
						}
					}
					
					//turning left
					if(current.getCol()-4 >= 0) {
						if(current.getRow()-4 >=0) {
							boolean noObst = true;
							for(int i=-4; i<-1; i++) {
								for(int j=-4; j<2; j++) {
									if(this.mapArena.getMap()[current.getRow()+j][current.getCol()+i].isObstacle()) {
										noObst = false;
									}
									
								}		
							}
							if(noObst) {
								t = this.mapArena.getMap()[current.getRow()-3][current.getCol()-3];
								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.RIGHT_LEFT_COST, 2);
							}
						}
					}
					break;
			}
			
		}
	}
	
	public String displaySolution() {
		String movementStr = "";
		String instructionStr = "";
		if(closedCells[this.targetRow][this.targetCol]) {
			System.out.println("Path: ");
			Cell current = this.mapArena.getMap()[this.targetRow][this.targetCol];
			System.out.println(current);
			movementStr = Integer.toString(current.getHeadDir());
			instructionStr = getInstructionStr(current.getParent().getHeadDir(), current.getRow(), current.getCol(), current.getParent().getRow(), current.getParent().getCol());
			this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setSolution(true);
			
			while(!current.getParent().isObstacle()) {
				System.out.println(" -> " + current.getParent());
				this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setSolution(true);;
				current = current.getParent();
				if(current.equals(this.mapArena.getMap()[this.robot.getPosRow()][this.robot.getPosCol()])) {
					break;
				}
				movementStr = movementStr + Integer.toString(current.getHeadDir());
				instructionStr = instructionStr + "|" + getInstructionStr(current.getParent().getHeadDir(), current.getRow(), current.getCol(), current.getParent().getRow(), current.getParent().getCol());
			}
			
			System.out.println("\n");
			
			int finalExpectedHead = this.targetHead;
			int finalActualHead = -1;
			for(int i=0; i<this.mapArena.getMap().length; i++) {
				for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
					if(i == this.robot.getPosRow() && j == this.robot.getPosCol())
						System.out.print("SO  "); //Source Cell
					else if(i==this.targetRow && j == this.targetCol) {
						System.out.print("DE  "); //destination Cell
						finalActualHead = this.mapArena.getMap()[i][j].getHeadDir();
					}
					else if(!this.mapArena.getMap()[i][j].isObstacle()) 
						System.out.printf("%-3s ", this.mapArena.getMap()[i][j].isSolution() ? "X" : "0");				
					else
						System.out.print("BL  "); //obstacle cell
				}
				System.out.println();
			}
			if(finalActualHead != finalExpectedHead) {
				switch (finalActualHead) {
				case 1: 
					if(finalExpectedHead == 3) {
						movementStr = "D|" + movementStr ;
						instructionStr = "D|" + instructionStr;
					}
					else if (finalExpectedHead == 4) {
						movementStr = "A|" + movementStr;
						instructionStr = "A|" + instructionStr;
					}
					break;
				case 2: 
					if(finalExpectedHead == 3) {
						movementStr = "A|" + movementStr;
						instructionStr = "A|" + instructionStr;
					}
					else if (finalExpectedHead == 4) {
						movementStr = "D|" + movementStr ;
						instructionStr = "D|" + instructionStr;
					}
					break;
				case 3: 
					if(finalExpectedHead == 1) {
						movementStr = "A|" + movementStr;
						instructionStr = "A|" + instructionStr;
					}
					else if (finalExpectedHead == 2) {
						movementStr = "D|" + movementStr;
						instructionStr = "D|" + instructionStr;
					}
					break;
				case 4: 
					if(finalExpectedHead == 1) {
						movementStr = "D|" + movementStr;
						instructionStr = "D|" + instructionStr;
					}
					else if (finalExpectedHead == 2) {
						movementStr = "A|" + movementStr;
						instructionStr = "A|" + instructionStr;
					}
					break;
				}
					
			}
			System.out.println();
		}
//		else {
//			movementStr = "";
//		}
		//System.out.println(instructionStr);
		return instructionStr;
	}
	
	public String getInstructionStr(int parentHeadDir, int currRow, int currCol, int parentRow, int parentCol) {
		String instrDir = null;
		switch (parentHeadDir){
		case 1: 
			if(currCol == parentCol) {
				if(currRow>parentRow) {
					instrDir = "W";
				}
				else {
					instrDir = "S";
				}
			}
			else {
				if(currCol > parentCol) {
					instrDir = "E";
				}
				else {
					instrDir = "Q";
				}
			}
			break;
		case 2: 
			if(currCol == parentCol) {
				if(currRow>parentRow) {
					instrDir = "S";
				}
				else {
					instrDir = "W";
				}
			}
			else {
				if(currCol > parentCol) {
					instrDir = "Q";
				}
				else {
					instrDir = "E";
				}
			}
			break;
		case 3:
			if(currRow == parentRow) {
				if(currCol>parentCol) {
					instrDir = "W";
				}
				else {
					instrDir = "S";
				}
			}
			else {
				if(currRow > parentRow) {
					instrDir = "Q";
				}
				else {
					instrDir = "E";
				}
			}
			break;
		case 4:
			if(currRow == parentRow) {
				if(currCol>parentCol) {
					instrDir = "S";
				}
				else {
					instrDir = "W";
				}
			}
			else {
				if(currRow > parentRow) {
					instrDir = "E";
				}
				else {
					instrDir = "Q";
				}
			}
			break;
		}
		return instrDir;
	}
	
	public static void main(String[] args) {
		Map m = new Map();
		Robot r = new Robot(1, 1, DIRECTION.NORTH);
		
		ArrayList<Cell> obsList = new ArrayList<Cell>();
		
		//adding of Cells
		Cell c1 = new Cell(5, 9);
		c1.setObsDir(4);
		c1.setObstacle(true);

		Cell c2 = new Cell(7, 14);
		c2.setObsDir(3);
		c2.setObstacle(true);

		Cell c3 = new Cell(12, 9);
		c3.setObsDir(2);
		c3.setObstacle(true);

		Cell c4 = new Cell(15, 4);
		c4.setObsDir(2);
		c4.setObstacle(true);

		Cell c5 = new Cell(15, 15);
		c5.setObsDir(2);
		c5.setObstacle(true);
		
		obsList.add(c5);
		obsList.add(c2);
		obsList.add(c1);
		obsList.add(c4);
		obsList.add(c3);
		
		m.setMapObstacle(obsList);
		for(int i=0; i<obsList.size(); i++) {
			m.setMapTargetCell(obsList.get(i).getRow(), obsList.get(i).getCol(), obsList.get(i).getObsDir());
		}
		
		List<Cell> tarList = new ArrayList<Cell>();
		
		for (int i=0; i<m.getMap().length; i++) {
			for (int j=0; j<m.getMap()[i].length; j++) {
				if(m.getMap()[i][j].isTargetCell()) {
					tarList.add(m.getMap()[i][j]);
				}
			}
		}
		tarList = NearestNeighbour.calculateDistance(tarList, r);

		// get nearest Neighbour
		ArrayList<Cell> nnList = NearestNeighbour.findNearestNeighbour(tarList);
		
		int[] tarHeadRArr = new int[5]; 
		int[] tarHeadCArr = new int[5]; 
		int[] tarHeadDirArr = new int[6]; 
		
		for (int i=0; i<=nnList.size(); i++) {
			if(i==0) {
				tarHeadDirArr[0] = 1;
			}
			else {
				tarHeadRArr[i-1] = nnList.get(i-1).getRow();
				tarHeadCArr[i-1] = nnList.get(i-1).getCol();
				tarHeadDirArr[i] = nnList.get(i-1).getHeadDir();
			}
		}
		String movementDir = "";
		for(int i=0; i<tarHeadRArr.length; i++ ) {
			int k = 1;
			AStar astar = new AStar(m, r, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i+1]);
			astar.process();
			String currMoveDir = astar.displaySolution();
			
			while(currMoveDir == "") {
				DIRECTION rStartHeadDir = r.getCurrDir();
				String turnDir = "";
				switch(rStartHeadDir) {
				case NORTH:
					if(k == 1) {
						r.setCurrDir(DIRECTION.EAST);
						turnDir = "|D";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = "|A";
					}
					break;
				case SOUTH:
					if(k == 1) {
						r.setCurrDir(DIRECTION.EAST);
						turnDir = "|A";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = "|D";
					}
					break;
				case EAST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = "|A";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = "|D";
					}
					break;
				case WEST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = "|D";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = "|A";
					}
					break;
				}
				astar = new AStar(m, r, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i+1]);
				astar.process();
				currMoveDir = astar.displaySolution() + turnDir;
				k += 1;
			}
			movementDir =  "V" + currMoveDir + movementDir;
			r.setPosRow(tarHeadRArr[i]);
			r.setPosCol(tarHeadCArr[i]);
			r.setCurrDir(r.intDirToConstantDir(tarHeadDirArr[i+1]));
		}
		System.out.println(movementDir);
	}
}
