package com.huawei;

import java.util.Arrays;
import java.util.TreeMap;

public class Cross {
	
	private int id;
	
	private int[] roadIds;
	
	private TreeMap<Integer, Integer> dirMap = new TreeMap<Integer, Integer>();
	
	public Cross(int id, int[] roadIds) {
		this.id = id;
		this.roadIds = roadIds;
		for (int i = 0; i < 4; i++) {
				dirMap.put(roadIds[i], i);
		}
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
