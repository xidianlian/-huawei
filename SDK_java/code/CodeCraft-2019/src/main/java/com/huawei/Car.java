package com.huawei;

import java.util.ArrayList;

public class Car {
	
	private int id, from, to, speed, planTime, realTime;
	
	private  ArrayList<Integer> roadList;
	// state :
	// (0, 在车库没出发) (1, 路上,准备出发) (2, 等待) (3, 终止) (4, 到达)
	private int state;
	
	// position 坐标从1开始
	private int roadId, cid, position;
	
	private String dir;
	
	// 如果为负一，
	private int nextRoadIndex;
	
	public Car(int id, int from, int to, int speed, int planTime) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.speed = speed;
		this.planTime = planTime;
		this.realTime = planTime;
		this.state = 0;
	
	}
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getRealTime() {
		return realTime;
	}

	public void setRealTime(int realTime) {
		this.realTime = realTime;
	}

	public ArrayList<Integer> getRoadList() {
		return roadList;
	}

	public void setRoadList(ArrayList<Integer> roadList) {
		this.roadList = roadList;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getRoadId() {
		return roadId;
	}

	public void setRoadId(int roadId) {
		this.roadId = roadId;
	}

	

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}


	public int getNextRoadIndex() {
		return nextRoadIndex;
	}

	public void setNextRoadIndex(int nextRoadIndex) {
		this.nextRoadIndex = nextRoadIndex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getPlanTime() {
		return planTime;
	}
	public void setPlanTime(int planTime) {
		this.planTime = planTime;
	}
	
	
	@Override
	public String toString() {
		return "Car [id=" + id + ", from=" + from + ", to=" + to + ", speed=" + speed + ", planTime=" + planTime + "]";
	}
	
}
