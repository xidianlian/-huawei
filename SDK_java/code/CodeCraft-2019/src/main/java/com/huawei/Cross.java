package com.huawei;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

public class Cross {
	
	private int id;
	
	private int[] roadIds;
	
	private TreeMap<Integer, Integer> dirMap = new TreeMap<Integer, Integer>();
	
	private HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
	
	public Cross(int id, int[] roadIds) {
		this.id = id;
		this.roadIds = roadIds;
		for (int i = 0; i < 4; i++) {
			dirMap.put(roadIds[i], i);
			indexMap.put(i, roadIds[i]);
		}
	}
	
	public HashMap<Integer, Integer> getIndexMap() {
		return indexMap;
	}

	public void setIndexMap(HashMap<Integer, Integer> indexMap) {
		this.indexMap = indexMap;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int[] getRoadIds() {
		return roadIds;
	}
	public void setRoadIds(int[] roadIds) {
		this.roadIds = roadIds;
	}
	
	
	public TreeMap<Integer, Integer> getDirMap() {
		return dirMap;
	}

	public void setDirMap(TreeMap<Integer, Integer> dirMap) {
		this.dirMap = dirMap;
	}

	@Override
	public String toString() {
		return "Cross [id=" + id + ", roadIds=" + Arrays.toString(roadIds) + ", dirMap=" + dirMap + "]";
	}

	
	
	
}
