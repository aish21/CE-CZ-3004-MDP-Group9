package algorithm;
import constant.Constants;
import entity.Map1;
import entity.Cell;
import entity.Cell1;
import java.util.*;

public class NearestNeighbour {
	private Map1 map;
	private List<Cell1> tarCells;
	private Hashtable<Cell1, Integer> startToTarDist;
	private Hashtable<Cell1, Integer[]> tarToTarDist;
	
	public NearestNeighbour(Map1 map) {
		this.map = map;
		for (int i=0; i<map.getMap().length; i++) {
			for(int j=0; j<map.getMap()[i].length; j++) {
				if (map.getMap()[i][j].getIsTargetCell()) {
					this.tarCells.add(map.getMap()[i][j]);
				}
			}
		}
		
		for(int i=0; i<tarCells.size(); i++) {
			Cell1 currCell = tarCells.get(i);
			this.startToTarDist.put(currCell, Math.abs(currCell.getRow()-this.map.getStartRow())+Math.abs(currCell.getCol()-this.map.getStartCol()));
		}
		
		
	}
	
	public Cell1 getFirstTarCell(Hashtable<Cell1, Integer> startToTarDist, List<Cell1> tarCells) {
		int temp = 99999999;
		Cell1 tempCell = null;
		for (int i=0; i<startToTarDist.size(); i++) {
			if(startToTarDist.get(tarCells.get(i))<temp){
				temp = startToTarDist.get(tarCells.get(i));
				tempCell = tarCells.get(i);
			}
		}
		return tempCell;
	}
	
	
}
