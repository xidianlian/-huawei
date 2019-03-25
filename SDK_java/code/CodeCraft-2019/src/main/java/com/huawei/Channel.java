package com.huawei;

import java.util.LinkedList;

public class Channel{
	int cid;
	LinkedList<Car> carList;
	Channel(int cid){
		this.cid = cid;
		carList = new LinkedList<Car>();
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public LinkedList<Car> getCarList() {
		return carList;
	}
	public void setCarList(LinkedList<Car> carList) {
		this.carList = carList;
	}
	
}