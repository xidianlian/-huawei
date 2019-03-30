package com.huawei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
public class Road {
	
	private int id, length, speed, chanNum, from, to, isDuplex;
	
	// 方向为 from -> to
	private ArrayList<Channel> fromChannels = new ArrayList<Channel>();
	// 方向为 to -> from
	private ArrayList<Channel> toChannels = new ArrayList<Channel>();
	
//	private HashMap<String, Integer> fromDirMap = new HashMap<String, Integer>();
//	
//	private HashMap<String, Integer> toDirMap = new HashMap<String, Integer>();
	
	public Road(int id, int length, int speed, int chanNum, int from, int to, int isDuplex) {
		this.id = id;
		this.length = length;
		this.speed = speed;
		this.chanNum = chanNum;
		this.from = from;
		this.to = to;
		this.isDuplex = isDuplex;
		for (int i = 0; i < chanNum; i++) {
			fromChannels.add(new Channel(i));
		}
		if (isDuplex == 1) {
			for (int i = 0; i < chanNum; i++) {
				toChannels.add(new Channel(i));
			}
		}
	}
	public void clearChannels() {
		for (int i = 0; i < chanNum; i++) {
			Channel channel = fromChannels.get(i);
			LinkedList<Car> carList = channel.getCarList();
			carList.clear();
		}
		if (isDuplex == 1) {
			for (int i = 0; i < chanNum; i++) {
				Channel channel = toChannels.get(i);
				LinkedList<Car> carList = channel.getCarList();
				carList.clear();
			}
		}
	}
//	public HashMap<String, Integer> getFromDirMap() {
//		return fromDirMap;
//	}
//
//	public void setFromDirMap(HashMap<String, Integer> fromDirMap) {
//		this.fromDirMap = fromDirMap;
//	}
//
//	public HashMap<String, Integer> getToDirMap() {
//		return toDirMap;
//	}
//
//	public void setToDirMap(HashMap<String, Integer> toDirMap) {
//		this.toDirMap = toDirMap;
//	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
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
	public int getIsDuplex() {
		return isDuplex;
	}
	public void setIsDuplex(int isDuplex) {
		this.isDuplex = isDuplex;
	}
	public int getChanNum() {
		return chanNum;
	}
	public void setChanNum(int chanNum) {
		this.chanNum = chanNum;
	}
	public ArrayList<Channel> getFromChannels() {
		return fromChannels;
	}
	public void setFromChannels(ArrayList<Channel> fromChannels) {
		this.fromChannels = fromChannels;
	}
	public ArrayList<Channel> getToChannels() {
		return toChannels;
	}
	public void setToChannels(ArrayList<Channel> toChannels) {
		this.toChannels = toChannels;
	}

	@Override
	public String toString() {
		return "Road [id=" + id + ", length=" + length + ", speed=" + speed + ", chanNum=" + chanNum + ", from=" + from
				+ ", to=" + to + ", isDuplex=" + isDuplex + "]";
	}

	
}
