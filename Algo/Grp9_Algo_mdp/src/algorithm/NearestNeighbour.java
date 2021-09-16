package algorithm;
import constant.Constants;
import entity.Map;
import entity.Cell;
import entity.Cell;
import java.util.*;

public class NearestNeighbour {
	private Map map;
	private List<Cell> tarCells;
	private Hashtable<Cell, Integer> startToTarDist;
	private Hashtable<Cell, Integer[]> tarToTarDist;
	
	public NearestNeighbour(Map map) {
		this.map = map;
		for (int i=0; i<map.getMap().length; i++) {
			for(int j=0; j<map.getMap()[i].length; j++) {
				if (map.getMap()[i][j].isTargetCell()) {
					this.tarCells.add(map.getMap()[i][j]);
				}
			}
		}
		
		for(int i=0; i<tarCells.size(); i++) {
			Cell currCell = tarCells.get(i);
			this.startToTarDist.put(currCell, Math.abs(currCell.getRow()-this.map.getStartRow())+Math.abs(currCell.getCol()-this.map.getStartCol()));
		}
		
		
	}
	
	public Cell getFirstTarCell(Hashtable<Cell, Integer> startToTarDist, List<Cell> tarCells) {
		int temp = 99999999;
		Cell tempCell = null;
		for (int i=0; i<startToTarDist.size(); i++) {
			if(startToTarDist.get(tarCells.get(i))<temp){
				temp = startToTarDist.get(tarCells.get(i));
				tempCell = tarCells.get(i);
			}
		}
		return tempCell;
	}
	
	
	// trying to implement
	public ArrayList<Integer> nearestNeighbour(List<Cell> tarCells){
		ArrayList<Integer> results =  new ArrayList<Integer>();
		int i = 0;
		if (i == 0) {
			
		}
		else {
			
		}
		i += 1;
		return results;
	}
	
	
}
