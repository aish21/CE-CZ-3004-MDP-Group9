package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import constant.Constants.DIRECTION;
import entity.Cell;
import entity.Map;
import entity.Robot;
import gui.main;

/**
 * @author Lau Zhen Jie
 * @author Goh Cheng Guan, Clive
 * @version 1.0
 * @since 2020-10-19
 */

public class MainConnect {
	//private char[] fullPath;

	/**
	 * @param mGui main
	 * @return full path of the astar algorithm with nearest neighbor
	 */
	public String fullPath(main mGui) {
		Map m = new Map();
		Robot r = new Robot(1, 1, DIRECTION.NORTH);
		System.out.println(mGui.getObsList().toString());
		Queue<Cell> q = mGui.getObstacleQueue();
		m.setMapObstacle(mGui.getObsList());
		List<Cell> tarList = new ArrayList<Cell>();
		for(int i=0; i<mGui.getObsList().size(); i++) {
			Cell c1 = m.setMapTargetCell(mGui.getObsList().get(i).getRow(), mGui.getObsList().get(i).getCol(), mGui.getObsList().get(i).getObsDir());
			tarList.add(c1);
		}
		
		
		System.out.println("Line hahaha" + tarList.toString());
//		for (int i=0; i<m.getMap().length; i++) {
//			for (int j=0; j<m.getMap()[i].length; j++) {
//				if(m.getMap()[i][j].isTargetCell()) {
//					Cell c = new Cell(i, j);
//					c.setHeadDir(m.getMap()[i][j].getHeadDir());
//					tarList.add(c);
//				}
//			}
//		}

		String movementDir = "";
		for(int i=0; i<mGui.getObsList().size(); i++ ) {
			//AStar astar;
			
			tarList = NearestNeighbour.calculateDistance(tarList, r);
			
			
			// get nearest Neighbour
			ArrayList<Cell> nnList = NearestNeighbour.findNearestNeighbour(tarList);
			q.add(m.targetToObstacle(nnList.get(0)));
			System.out.println("nnList is: " + nnList);
			int k = 1;
			AStar astar = new AStar(m, r, nnList.get(0).getRow(), nnList.get(0).getCol(), nnList.get(0).getHeadDir());

			astar.process();
			String currMoveDir = astar.displaySolution();
			
			while(currMoveDir == "") {
				DIRECTION rStartHeadDir = r.getCurrDir();
				String turnDir = "";
				switch(rStartHeadDir) {
				case NORTH:
					if(k == 1) {
						r.setCurrDir(DIRECTION.EAST);
						turnDir = ",D";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = ",A";
					}
					break;
				case SOUTH:
					if(k == 1) {
						r.setCurrDir(DIRECTION.EAST);
						turnDir = ",A";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = ",D";
					}
					break;
				case EAST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = ",A";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = ",D";
					}
					break;
				case WEST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = ",D";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = ",A";
					}
					break;
				}
				astar = new AStar(m, r, nnList.get(0).getRow(), nnList.get(0).getCol(), nnList.get(0).getHeadDir());
				astar.process();
				currMoveDir = astar.displaySolution() + turnDir;
				k += 1;
			}	
			movementDir =  "V," + currMoveDir + "," + movementDir;
			r.setPosRow(nnList.get(0).getRow());
			r.setPosCol(nnList.get(0).getCol());
			r.setCurrDir(r.intDirToConstantDir(nnList.get(0).getHeadDir()));
			System.out.println("Line 111 MC");
			if(nnList.size() > 1) {
				for(int p=0; p<nnList.size()-1; p++) {
					if(nnList.get(p).getRow() != nnList.get(p+1).getRow() || nnList.get(p).getCol() != nnList.get(p+1).getCol()) {
						break;
					}
					if(nnList.get(p).getRow() == nnList.get(p+1).getRow() && nnList.get(p).getCol() == nnList.get(p+1).getCol()) {
						System.out.println("line 112 MC enter if");
						switch(nnList.get(p).getHeadDir()) {
						case 1:
							if(nnList.get(p+1).getHeadDir() == 2) {
								movementDir = "V,D,D," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 3) {
								movementDir = "V,D," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 4) {
								movementDir = "V,A," + movementDir;
							}
							break;
						case 2:
							if(nnList.get(p+1).getHeadDir() == 1) {
								movementDir = "V,D,D," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 3) {
								movementDir = "V,A," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 4) {
								movementDir = "V,D," + movementDir;
							}
							break;
						case 3:
							if(nnList.get(p+1).getHeadDir() == 1) {
								movementDir = "V,A," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 2) {
								movementDir = "V,D," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 4) {
								movementDir = "V,D,D," + movementDir;
							}
							break;
						case 4:
							if(nnList.get(p+1).getHeadDir() == 1) {
								movementDir = "V,D," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 2) {
								movementDir = "V,A," + movementDir;
							}
							else if(nnList.get(p+1).getHeadDir() == 3) {
								movementDir = "V,D,D," + movementDir;
							}
							break;
						}
						i ++;
						System.out.println("MC line 160 i is" + i);
						r.setCurrDir(r.intDirToConstantDir(nnList.get(p+1).getHeadDir()));
						q.add(m.targetToObstacle(nnList.get(p+1)));
						tarList.remove(nnList.get(p+1));
					}
				}
			}	
			System.out.println("MC line 167 "+ tarList.toString());
			tarList.remove(nnList.get(0));
			
		}
		
		System.out.println(movementDir);
		if(movementDir.charAt(movementDir.length()-1) == ',') {
			movementDir = movementDir.substring(0,movementDir.length()-1);
		}
		mGui.setObstacleQueue(q);
		return movementDir;
	}
	
	public String fullPathForCompVisArdAObs(Cell obsPos) {
		Map m = new Map();
		Robot r = new Robot(1, 1, DIRECTION.NORTH);
		ArrayList<Cell> obsList = new ArrayList<Cell>();
		int obsRow = obsPos.getRow();
		int obsCol = obsPos.getCol();
		
		//adding of Cells
		Cell c1 = new Cell(obsRow, obsCol);
		c1.setObsDir(1);
		c1.setObstacle(true);

		Cell c2 = new Cell(obsRow, obsCol);
		c2.setObsDir(2);
		c2.setObstacle(true);

		Cell c3 = new Cell(obsRow, obsCol);
		c3.setObsDir(3);
		c3.setObstacle(true);

		Cell c4 = new Cell(obsRow, obsCol);
		c4.setObsDir(4);
		c4.setObstacle(true);
		
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
		
		int[] tarHeadRArr = new int[nnList.size()]; 
		int[] tarHeadCArr = new int[nnList.size()]; 
		int[] tarHeadDirArr = new int[nnList.size()+1]; 
		
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
						turnDir = ",D";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = ",A";
					}
					break;
				case SOUTH:
					if(k == 1) {
						r.setCurrDir(DIRECTION.EAST);
						turnDir = ",A";
					}
					else {
						r.setCurrDir(DIRECTION.WEST);
						turnDir = ",D";
					}
					break;
				case EAST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = ",A";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = ",D";
					}
					break;
				case WEST:
					if(k == 1) {
						r.setCurrDir(DIRECTION.NORTH);
						turnDir = ",D";
					}
					else {
						r.setCurrDir(DIRECTION.SOUTH);
						turnDir = ",A";
					}
					break;
				}
				astar = new AStar(m, r, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i+1]);
				astar.process();
				currMoveDir = astar.displaySolution() + turnDir;
				k += 1;
			}
			movementDir =  "V," + currMoveDir + "," + movementDir;
			r.setPosRow(tarHeadRArr[i]);
			r.setPosCol(tarHeadCArr[i]);
			r.setCurrDir(r.intDirToConstantDir(tarHeadDirArr[i+1]));
		}
		System.out.println(movementDir);
		return movementDir;
	}
	
//	public static void main(String arg[]) {
//		MainConnect mc = new MainConnect();
//		//System.out.println(mc.fullPath());
//		System.out.println(mc.fullPathForCompVisArdAObs(new Cell (10, 10)));
//	}
}
