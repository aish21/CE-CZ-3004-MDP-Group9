/**
 * 
 */
package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import constant.Constants;
import constant.Constants.DIRECTION;
import entity.Cell;
import entity.Map;
import entity.Robot;

/**
 * @author Jett
 * @author Goh Cheng Guan,Clive
 *
 */
public class AStar2 {
	private Map mapArena;
	private PriorityQueue<Cell> openCells; // set of nodes to be evaluated
	private boolean[][] closedCells;// set of nodes evaluated
	private Robot robot;
	private int targetRow, targetCol, targetHead;

	public AStar2(Map m, Robot r) {
		this.mapArena = m;
		this.robot = r;
		this.closedCells = new boolean[Constants.MAX_ROW][Constants.MAX_COL];
		this.openCells = new PriorityQueue<Cell>((Cell c1, Cell c2) -> {
			return c1.getFinalCost() < c2.getFinalCost() ? -1 : c1.getFinalCost() > c2.getFinalCost() ? 1 : 0;
		});

	}

	public void initiate(ArrayList<Cell> obstacles) {
		mapArena.setMapHeuristicCost(targetRow, targetCol);
		mapArena.setRobotMap(robot);
		this.mapArena.setStartRowCol(robot.getPosRow(), robot.getPosCol());
		setMapObstacle(obstacles);
	}

	public void setMapObstacle(ArrayList<Cell> obstacles) {
		for (int i = 0; i < obstacles.size(); i++) {
			mapArena.setMapTargetCell(obstacles.get(i).getRow(), obstacles.get(i).getCol(),
					obstacles.get(i).getObsDir());
		}
		mapArena.setMapObstacle(obstacles);
	}

	public void updateCostIfNeeded(Cell current, Cell tar, double cost, int tarHead) {
		if (tar == null || closedCells[tar.getRow()][tar.getCol()]) {
			return;
		}

		double tarFinalCost = tar.getHeuristicCost() + cost;
		boolean isOpen = this.openCells.contains(tar);

		if (!isOpen || tarFinalCost < tar.getFinalCost()) {
			tar.setFinalCost(tarFinalCost);
			tar.setParent(current);
			tar.setHeadDir(tarHead);
			if (!isOpen) {
				this.openCells.add(tar);
			}
			// System.out.println(tar);
		}
	}

	public static ArrayList<Cell> getTarget(ArrayList<Cell> obstacles) {
		ArrayList<Cell> target = new ArrayList<Cell>();
		Iterator<Cell> iterator = obstacles.iterator();
		while (iterator.hasNext()) {
			try {
				target.add((Cell) iterator.next().clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// manipulate
		for (int i = 0; i < target.size(); i++) {
			int col = target.get(i).getCol();
			int row = target.get(i).getRow();

			/*
			 * switch (target.get(i).getObsDir()) { // north case 1:
			 * target.get(i).setCol(col+3); break; //south case 2:
			 * target.get(i).setCol(col-3); break; //east case 3:
			 * target.get(i).setRow(row+3); break; //west case 4:
			 * target.get(i).setRow(row-3); break; default: break; }
			 */

			switch (target.get(i).getHeadDir()) {
			// north
			case 1:
				target.get(i).setRow(row + 3);
				//target.get(i).setCol(col + 3);
				break;
			// south
			case 2:
				target.get(i).setRow(row - 3);
				//target.get(i).setCol(col - 3);
				break;
			// east
			case 3:
				target.get(i).setCol(col + 3);
				//target.get(i).setRow(row + 3);
				break;
			// west
			case 4:
				target.get(i).setCol(col - 3);
				//target.get(i).setRow(row - 3);
				break;
			default:
				break;
			}

		}
		return target;
	}

	public void process() {
		// we add the start location to open list
		openCells.add(mapArena.getMap()[robot.getPosCol()][robot.getPosCol()]);
		Cell current;

		while (true) {
			current = openCells.poll();
			if (current == null) {
				break;
			}
			closedCells[current.getRow()][current.getCol()] = true;

//				if(current.equals(this.mapArena.getMap()[this.targetRow][this.targetCol])) {
//					switch (current.getHeadDir()){
//						case 1: 
//							if(this.targetHead == 2) {
//								System.out.println("Hi1");
//								return;
//							}
//						case 2: 
//							if(this.targetHead == 1) {
//								System.out.println("Hi2");
//								return;
//							}
//						case 3: 
//							if(this.targetHead == 4) {
//								System.out.println("Hi3");
//								return;
//							}
//						case 4: 
//							if(this.targetHead == 3) {
//								System.out.println("Hi4");
//								return;
//							}
//					}
//					
//				}
			if (current.equals(this.mapArena.getMap()[this.targetRow][this.targetCol])
					&& current.getHeadDir() == this.targetHead) {
				System.out.println("TarHead: " + this.targetHead);
				System.out.println("CurrHead: " + current.getHeadDir());
				return;
			}

			if (current.getRow() + 1 >= Constants.MAX_ROW || current.getRow() - 1 < 0
					|| current.getCol() + 1 >= Constants.MAX_COL || current.getCol() < 0) {
				return;
			}

			Cell t;

			switch (current.getHeadDir()) {
			case 1:// north
					// backward
				if (current.getRow() - 2 >= 0) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() - 2][current.getCol() + i].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow() - 1][current.getCol()];
						// t.setHeadDir(1);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 1);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// front
				if (current.getRow() + 2 < this.mapArena.getMap().length) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + 2][current.getCol() + i].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow() + 1][current.getCol()];
						// t.setHeadDir(1);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 1);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// turning right
				if (current.getRow() + 4 < this.mapArena.getMap().length) {
					if (current.getCol() + 4 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for (int i = 2; i < 5; i++) {
							for (int j = -1; j < 5; j++) {
								if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + j].isObstacle()) {
									noObst = false;
								}
							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() + 3][current.getCol() + 3];
							// t.setHeadDir(3);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 3);
						}
					}
				}

				// turning left
				if (current.getRow() + 4 < this.mapArena.getMap().length) {
					if (current.getCol() - 4 >= 0) {
						boolean noObst = true;
						for (int i = 2; i < 5; i++) {
							for (int j = -4; j < 2; j++) {
								if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + j].isObstacle()) {
									noObst = false;
								}

							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() + 3][current.getCol() - 3];
							// t.setHeadDir(4);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 4);
						}
					}
				}
				break;
			case 2: // south
				// forward
				if (current.getRow() - 2 >= 0) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() - 2][current.getCol() + i].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow() - 1][current.getCol()];
						// t.setHeadDir(2);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 2);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// backward
				if (current.getRow() + 2 < this.mapArena.getMap().length) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + 2][current.getCol() + i].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow() + 1][current.getCol()];
						// t.setHeadDir(2);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 2);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// turning left
				if (current.getRow() - 4 >= 0) {
					if (current.getCol() + 4 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for (int i = -4; i < -1; i++) {
							for (int j = -1; j < 5; j++) {
								if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + j].isObstacle()) {
									noObst = false;
								}
							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() - 3][current.getCol() + 3];
							// t.setHeadDir(3);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 3);
						}
					}
				}

				// turning right
				if (current.getRow() - 4 >= 0) {
					if (current.getCol() - 4 >= 0) {
						boolean noObst = true;
						for (int i = -4; i < -1; i++) {
							for (int j = -4; j < 2; j++) {
								if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + j].isObstacle()) {
									noObst = false;
								}

							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() - 3][current.getCol() - 3];
							// t.setHeadDir(4);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 4);
						}
					}
				}
				break;
			case 3: // east
				// backward
				if (current.getCol() - 2 >= 0) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + i][current.getCol() - 2].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow()][current.getCol() - 1];
						// t.setHeadDir(3);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 3);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// front
				if (current.getCol() + 2 < this.mapArena.getMap().length) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + 2].isObstacle()) {
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow()][current.getCol() + 1];
						// t.setHeadDir(3);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 3);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// turning left
				if (current.getCol() + 4 < this.mapArena.getMap().length) {
					if (current.getRow() + 4 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for (int i = 2; i < 5; i++) {
							for (int j = -1; j < 5; j++) {
								if (this.mapArena.getMap()[current.getRow() + j][current.getCol() + i].isObstacle()) {
									noObst = false;
								}
							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() + 3][current.getCol() + 3];
							// t.setHeadDir(1);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 1);
						}
					}
				}

				// turning right
				if (current.getCol() + 4 < this.mapArena.getMap().length) {
					if (current.getRow() - 4 >= 0) {
						boolean noObst = true;
						for (int i = 2; i < 5; i++) {
							for (int j = -4; j < 2; j++) {
								if (this.mapArena.getMap()[current.getRow() + j][current.getCol() + i].isObstacle()) {
									noObst = false;
								}

							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() - 3][current.getCol() + 3];
							// t.setHeadDir(2);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 2);
						}
					}
				}
				break;
			case 4:// west
					// forward
				if (current.getCol() - 2 >= 0) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + i][current.getCol() - 2].isObstacle()) {
							System.out.println("Obs: {" + (current.getRow() + i) + ", " + (current.getCol() - 2) + "}");
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow()][current.getCol() - 1];
						// t.setHeadDir(4);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 4);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// backward
				if (current.getCol() + 2 < this.mapArena.getMap().length) {
					boolean noObst = true;
					for (int i = -1; i < 2; i++) {
						if (this.mapArena.getMap()[current.getRow() + i][current.getCol() + 2].isObstacle()) {
							System.out.println("Obs: {" + (current.getRow() + i) + ", " + (current.getCol() + 2) + "}");
							noObst = false;
						}
					}
					if (noObst) {
						t = this.mapArena.getMap()[current.getRow()][current.getCol() + 1];
						// t.setHeadDir(4);
						updateCostIfNeeded(current, t, current.getFinalCost() + Constants.MOVE_COST, 4);
					}
//							else {
//								t = this.mapArena.getMap()[current.getRow()+1][current.getCol()];
//								updateCostIfNeeded(current, t, current.getFinalCost()+Constants.INFINITE_COST);
//							}	
				}

				// turning right
				if (current.getCol() - 4 >= 0) {
					if (current.getRow() + 4 < this.mapArena.getMap().length) {
						boolean noObst = true;
						for (int i = -4; i < -1; i++) {
							for (int j = -1; j < 5; j++) {
								if (this.mapArena.getMap()[current.getRow() + j][current.getCol() + i].isObstacle()) {

									noObst = false;
								}
							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() + 3][current.getCol() - 3];
							// t.setHeadDir(1);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 1);
						}
					}
				}

				// turning left
				if (current.getCol() - 4 >= 0) {
					if (current.getRow() - 4 >= 0) {
						boolean noObst = true;
						for (int i = -4; i < -1; i++) {
							for (int j = -4; j < 2; j++) {
								if (this.mapArena.getMap()[current.getRow() + j][current.getCol() + i].isObstacle()) {
									noObst = false;
								}

							}
						}
						if (noObst) {
							t = this.mapArena.getMap()[current.getRow() - 3][current.getCol() - 3];
							// t.setHeadDir(2);
							updateCostIfNeeded(current, t, current.getFinalCost() + Constants.RIGHT_LEFT_COST, 2);
						}
					}
				}
				break;
			}

		}
	}

	public void display() {
		System.out.println("Map Arena :");

		for (int i = 0; i < this.mapArena.getMap().length; i++) {
			for (int j = 0; j < this.mapArena.getMap()[i].length; j++) {
				if (i == robot.getPosRow() && j == robot.getPosCol())
					System.out.print("SO  "); // Source Cell
				else if (i == this.targetRow && j == this.targetCol)
					System.out.print("DE  "); // destination Cell
				else if (!this.mapArena.getMap()[i][j].isObstacle())
					System.out.printf("%-3d ", 0);
				else
					System.out.print("BL  "); // obstacle cell
			}
			System.out.println();
		}
		System.out.println();
	}

	public void displayScores() {
		System.out.println("\nScores for Cells : ");
		for (int i = 0; i < this.mapArena.getMap().length; i++) {
			for (int j = 0; j < this.mapArena.getMap()[i].length; j++) {
				if (!this.mapArena.getMap()[i][j].isObstacle()) {
					System.out.printf("%-3s \t", this.mapArena.getMap()[i][j].getFinalCost());
				} else {
					System.out.print("BL  \t");
				}

			}
			System.out.println();
		}
		System.out.println();
	}

	public void displaySolution() {
		if (closedCells[this.targetRow][this.targetCol]) {
			System.out.println("Path: ");
			Cell current = this.mapArena.getMap()[this.targetRow][this.targetCol];
			System.out.println(current);
			this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setSolution(true);

			while (!current.getParent().isObstacle()) {
				System.out.println(" -> " + current.getParent());
				this.mapArena.getMap()[current.getParent().getRow()][current.getParent().getCol()].setSolution(true);
				;
				current = current.getParent();
				if (current.equals(this.mapArena.getMap()[robot.getPosRow()][robot.getPosCol()])) {
					break;
				}
			}

			System.out.println("\n");

//				for (int i=0; i<this.mapArena.getMap().length; i++) {
//					for(int j=0; j<this.mapArena.getMap()[i].length; j++) {
//						if(!this.mapArena.getMap()[i][j].getIsObstacle()) {
//							System.out.printf("%-3d ", this.mapArena.getMap()[i][j].getFinalCost());
//						}
//						else {
//							System.out.print("BL  ");
//						}
//						System.out.println();
//					}
//					System.out.println();
//				}
			for (int i = 0; i < this.mapArena.getMap().length; i++) {
				for (int j = 0; j < this.mapArena.getMap()[i].length; j++) {
					if (i == robot.getPosRow() && j == robot.getPosCol())
						System.out.print("SO  "); // Source Cell
					else if (i == this.targetRow && j == this.targetCol)
						System.out.print("DE  "); // destination Cell
					else if (!this.mapArena.getMap()[i][j].isObstacle())
						System.out.printf("%-3s ", this.mapArena.getMap()[i][j].isSolution() ? "X" : "0");
					else
						System.out.print("BL  "); // obstacle cell
				}
				System.out.println();
			}
			System.out.println();
		} else {
			System.out.println("No possible path");
		}
	}

	public static void main(String[] args) {
		
		Map m = new Map();
		Robot r = new Robot(1, 1, DIRECTION.NORTH);
		
		// int arrIdx = 0;
		List<Cell> obsList = new ArrayList<Cell>();

		// adding of Cells
		Cell c1 = new Cell(5, 6);
		c1.setHeadDir(3);
		c1.setObsDir(3);
		c1.setObstacle(true);

		Cell c2 = new Cell(7, 17);
		c2.setHeadDir(4); // if no possible path, try on spot turn?
		c2.setObsDir(4);
		c2.setObstacle(true);

		Cell c3 = new Cell(9, 9);
		c3.setHeadDir(1);
		c3.setObsDir(1);
		c3.setObstacle(true);

		Cell c4 = new Cell(12, 4);
		c4.setHeadDir(1);
		c4.setObsDir(1);
		c4.setObstacle(true);

		Cell c5 = new Cell(12, 15);
		c5.setHeadDir(1);
		c5.setObsDir(1);
		c5.setObstacle(true);

		obsList.add(c5);
		obsList.add(c2);
		obsList.add(c1);
		obsList.add(c4);
		obsList.add(c3);
		
		//set map obstacle
		for(int i = 0 ; i < obsList.size(); i++)
		{
			int row = obsList.get(i).getRow();
			int col = obsList.get(i).getCol();
			m.getMap()[row][col].setObsDir(obsList.get(i).getObsDir());
			m.getMap()[row][col].setHeadDir(obsList.get(i).getHeadDir());
			m.getMap()[row][col].setObstacle(true);
		}

		obsList = NearestNeighbour.calcualteDistance(obsList, r);

		// get nearest Neighbour & target list
		ArrayList<Cell> nnList = NearestNeighbour.findNearestNeighbour(obsList);
		ArrayList<Cell> targetList =  (ArrayList<Cell>) nnList.clone();

		
		AStar2 astar = new AStar2(m,r);
		astar.initiate(nnList);
		astar.process();
		astar.displayScores();
		astar.displaySolution();


	}

}
