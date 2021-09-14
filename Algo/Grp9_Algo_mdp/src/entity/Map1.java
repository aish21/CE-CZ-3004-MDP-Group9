package entity;
import constant.Constants;
import java.util.*;

public class Map1 {
	private Cell1[][] map;
	private int robPosRow; //start pos of rob
	private int robPosCol; //start pos of rob
	private int targetRow;
	private int targetCol;
	private Hashtable<Cell1[][], Double> tarsNDist;
	
	public Map1(){
		this.map = new Cell1[Constants.MAX_ROW][Constants.MAX_COL];
		//setting all box with 0 to indicate new map
		for (int i=0; i< Constants.MAX_ROW; i++) {
			for (int j=0; j<Constants.MAX_COL; j++) {
				this.map[i][j] = new Cell1(i, j);
			}
		}
		// setting start position of robot (center of robot)
//		for (int i=robPosRow-1; i<=robPosRow+1; i++) {
//			for (int j=robPosCol-1; j<=robPosCol+1; j++) {
//				this.map[i][j] = -1;
//			}
//		}
		this.tarsNDist = new Hashtable<Cell1[][], Double>();
		this.robPosRow = 1;
		this.robPosCol = 1;
	}
	
	//return arrays of map
	public Cell1[][] getMap() {
		return this.map;
	}
	
	public int getStartRow() {
		return this.robPosRow;
	}
	
	public int getStartCol() {
		return this.robPosCol;
	}
	
	public void setStartRowCol(int startRow, int startCol) {
		this.robPosRow = startRow;
		this.robPosCol = startCol;
	}
	
	public void setObstaclesNTargetPos(int obsRow, int obsCol, int dir) { //1=N, 2=S, 3=E, 4=W
		this.map[obsRow][obsCol].setIsObstacleNDirection(true,  dir);;
		switch (dir) {
			case 1:
				this.targetRow = obsRow + 3;
				this.targetCol = obsCol;
				this.map[obsRow+3][obsCol].setIsTargetCell(true);
				//this.tarsNDist.put(this.map[obsRow+3][obsCol], 0);
				break;
			case 2:
				this.targetRow = obsRow - 3;
				this.targetCol = obsCol;
				this.map[obsRow-3][obsCol].setIsTargetCell(true);
				break;
			case 3:
				this.targetRow = obsRow;
				this.targetCol = obsCol + 3;
				this.map[obsRow][obsCol+3].setIsTargetCell(true);
				break;
			case 4:
				this.targetRow = obsRow;
				this.targetCol = obsCol - 3;
				this.map[obsRow][obsCol-3].setIsTargetCell(true);
				break;
		}
	}
	
//	public void updateRobPos(Constants.MOVEMENT goingDir, Constants.DIRECTION facingDir) {
//		switch (facingDir) {
//			case NORTH:
//				if(goingDir == Constants.MOVEMENT.FORWARD) {
//					this.robPosRow += 1;
//					for(int col=this.robPosCol-1; col<=this.robPosCol+1; col++) {
//						this.map[this.robPosRow+1][this.robPosCol] = -1;
//						this.map[this.robPosRow-2][this.robPosCol] = 0;
//					}
//				}
//				else if (goingDir == Constants.MOVEMENT.BACKWARD) {
//					this.robPosRow -= 1;
//					for(int col=robPosCol-1; col<=robPosCol+1; col++) {
//						this.map[this.robPosRow-1][this.robPosCol] = -1;
//						this.map[this.robPosRow+2][this.robPosCol] = 0;
//					}
//				}
//			case SOUTH:
//				if(goingDir == Constants.MOVEMENT.FORWARD) {
//					this.robPosRow -= 1;
//					for(int col=this.robPosCol-1; col<=this.robPosCol+1; col++) {
//						this.map[this.robPosRow-1][this.robPosCol] = -1;
//						this.map[this.robPosRow+2][this.robPosCol] = 0;
//					}
//				}
//				else if (goingDir == Constants.MOVEMENT.BACKWARD) {
//					this.robPosRow += 1;
//					for(int col=robPosCol-1; col<=robPosCol+1; col++) {
//						this.map[this.robPosRow+1][this.robPosCol] = -1;
//						this.map[this.robPosRow-2][this.robPosCol] = 0;
//					}
//				}
//			case EAST:
//				if(goingDir == Constants.MOVEMENT.FORWARD) {
//					this.robPosCol += 1;
//					for(int row=this.robPosRow-1; row<=this.robPosRow+1; row++) {
//						this.map[this.robPosRow][this.robPosCol+1] = -1;
//						this.map[this.robPosRow][this.robPosCol-2] = 0;
//					}
//				}
//				else if (goingDir == Constants.MOVEMENT.BACKWARD) {
//					this.robPosRow -= 1;
//					for(int row=robPosRow-1; row<=robPosCol+1; row++) {
//						this.map[this.robPosRow][this.robPosCol-1] = -1;
//						this.map[this.robPosRow][this.robPosCol+2] = 0;
//					}
//				}
//			case WEST:
//				if(goingDir == Constants.MOVEMENT.FORWARD) {
//					this.robPosCol -= 1;
//					for(int row=this.robPosRow-1; row<=this.robPosRow+1; row++) {
//						this.map[this.robPosRow][this.robPosCol-1] = -1;
//						this.map[this.robPosRow][this.robPosCol+2] = 0;
//					}
//				}
//				else if (goingDir == Constants.MOVEMENT.BACKWARD) {
//					this.robPosCol += 1;
//					for(int row=robPosRow-1; row<=robPosRow+1; row++) {
//						this.map[this.robPosRow][this.robPosCol+1] = -1;
//						this.map[this.robPosRow][this.robPosCol-2] = 0;
//					}
//				}
//		}
//	}
	
	
}
