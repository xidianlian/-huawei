package com.huawei;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class Main {
	
	 private static class Node implements Comparable<Node>{
    	int crossIndex; // cross
    	int len;
    	Node (int id, int len){
    		this.crossIndex = id;
    		this.len = len;
    	}
		@Override
		public int compareTo(Node o) {
			return this.len - o.len;
		}
    }
    private static final Logger logger = Logger.getLogger(Main.class);
    
    private static final int INF = 0x7f7f7f7f;
    
    private static HashMap<Integer, Integer> crossMp = new HashMap<Integer, Integer>();
    
    private static HashMap<Integer, Integer> roadsMp = new HashMap<Integer, Integer>();
    
    
    
    private static ArrayList<Object> crosses = new ArrayList<Object>();
    
    private static ArrayList<Object> roads = new ArrayList<Object>();
    
    // 在路上的车数量
    private static int runCarsNum;
    
    // 终止状态的车
    private static int endNum;
    
    // 到达终点的车
    private static int arrivedNum;
    
    private static int time;
    /**
     * 
     * @param car
     * @description ( 利用堆优化的迪杰斯特拉算法计算某辆车到目的路口的最短时间花费)
     * @return 车经过的车道编号
     */
    private static ArrayList<Integer> dij(Car car) {
    	ArrayList<Integer> ansList = new ArrayList<Integer>();
    	ArrayList<Integer> reverList = new ArrayList<Integer>();
     	int crossSize = crosses.size();
     	// dis记录此车到每个cross的最短距离
    	int[] dis = new int[crossSize];
    	// vis标记此cross在不在优先队列中
    	int[] vis = new int[crossSize];
    	// 如果此cross的最短距离的到更新（dis值变小），fa记录此点（cross）的前驱
    	int[] fa = new int[crossSize];
    	int[] answer = new int[crossSize];
    	for (int i = 0; i < crossSize; i++) {
    		dis[i] = INF;
    		vis[i] = 0;
    	}
    	PriorityQueue<Node> que = new PriorityQueue<Node>();
    	int index = crossMp.get(car.getFrom());
    	que.add(new Node(index, 0));
    	dis[index] = 0;
    	while (!que.isEmpty()) {
    		Node node = que.poll();
    		if (vis[node.crossIndex] == 1) {
    			continue;
    		}
    		vis[node.crossIndex] = 1;
    		Cross cro = (Cross)crosses.get(node.crossIndex);
    		int[] roadIds = cro.getRoadIds();
    		int[] flag = new int[4];
    		for (int i = 0 ; i < 4; i++) {
    			flag[i] = 0;
    		}
    		for (;;) {
    			int yes = 0;
    			for (int i = 0 ; i < 4; i++) {
        			if (flag[i] != 1) {
        				yes = 1;
        			}
        		}
    			if (yes == 0) {
    				break;
    			}
    			int randomI = random(0, 4);
    			if (flag[randomI] == 0) {
    				flag[randomI] = 1;
    			} else {
    				continue;
    			}
    			int roadId = roadIds[randomI];
    			if (roadId == -1) {
    				continue;
    			}
    			Road road = (Road)roads.get(roadsMp.get(roadId));
    			int toIndex = -1;
    			if (road.getFrom() == cro.getId()) {
    				toIndex = crossMp.get(road.getTo());
    			} else if (road.getIsDuplex() == 1 && road.getTo() == cro.getId()){
    				toIndex = crossMp.get(road.getFrom());
    			}
    			if (toIndex == -1) {
    				continue;
    			}
    			// 这个道路最小花费为 路长 / 最大速度
    			int roadCost = 0;
    			if (road.getLength() % Math.min(car.getSpeed(), road.getSpeed()) == 0) {
    				roadCost = road.getLength() / Math.min(car.getSpeed(), road.getSpeed());
    			} else {
    				roadCost = (int)(road.getLength() / Math.min(car.getSpeed(), road.getSpeed()));
    				roadCost += 1;
    			}
    			// 松弛
    			if (vis[toIndex] == 0 && dis[toIndex] > dis[node.crossIndex] + roadCost) {
    				dis[toIndex] = dis[node.crossIndex] + roadCost;
    				que.add(new Node(toIndex, dis[toIndex]));
    				fa[toIndex] = node.crossIndex;
    				answer[toIndex] = roadId;
    			}
    		}
    	}
    	int curIndex = crossMp.get(car.getTo());
    	int endIndex = crossMp.get(car.getFrom());
    	
    	while (curIndex != endIndex) {	
    		reverList.add(answer[curIndex]);
    		curIndex = fa[curIndex];
    	}
    	for (int i = 0; i < reverList.size(); i++) {
    		ansList.add(reverList.get(reverList.size() - i - 1));
    	}
    	endIndex = crossMp.get(car.getTo());
    	car.setTime(dis[endIndex]);
    	return ansList;
    }
    /**
     * @param road
     * @param channel
     * 搜索一个车道
     */
    private static void checkRoadsChannel(Road road, Channel channel) {
		LinkedList<Car> carList = channel.getCarList();
		int carListSize = carList.size();
		int cnt = 0;
		Car lastCar = null;
		while(!carList.isEmpty()) {
			Car car = carList.poll();
			if (car.getState() == 0) {
				logger.info("car.state == 0");
//				logger.info(car);
			}
			if (car.getState() == 4) {
				logger.info("car.state == 0");
			}
			if (car.getState() != 1 && car.getState() != 2) {
				lastCar = car;
				carList.add(car);
				cnt++;
				if (cnt == carListSize) {
					break;
				}
				continue;
			}
			int maxSpeed = Math.min(car.getSpeed(), road.getSpeed());
			int length;
			if (lastCar == null) {
				length = car.getPosition() - 1;
				if (length >= maxSpeed) {
					car.setPosition(car.getPosition() - maxSpeed);
					endNum++;
					car.setState(3); 
				} else {
					car.setState(2);
				}
				
			} else {
				length = car.getPosition() - lastCar.getPosition() - 1;
				if (length >= maxSpeed) {
					car.setPosition(car.getPosition() - maxSpeed);
					endNum++;
					car.setState(3);
				} else {
					if (lastCar.getState() == 3) {
						car.setPosition(lastCar.getPosition() + 1);
						car.setState(3);
						endNum++;
					} else if (lastCar.getState() == 2) {
						car.setState(2);
						
					}
				}
			}
			lastCar = car;
			carList.add(car);
			cnt++;
			if (cnt == carListSize) {
				break;
			}
		}
    }
    
    /**
   	 * 先调度路上的车
     */
    public static void checkRoads() {

    	for (int i = 0; i < roads.size(); i++) {
			Road road = (Road) roads.get(i);
			ArrayList<Channel> fromChannels = road.getFromChannels();
			for (int j = 0; j < fromChannels.size(); j++) {
				checkRoadsChannel(road, fromChannels.get(j));
			}
			
			if (road.getIsDuplex() == 1) {
				ArrayList<Channel> toChannels = road.getToChannels();
				for (int j = 0; j < toChannels.size(); j++) {
					checkRoadsChannel(road, toChannels.get(j));
				}
			}
		}
    	
    }
    
    /******222********/
    public static Car getChannelFirstCar(ArrayList<Channel> channels) {
    	// 同一道路，不管在哪个车道上，前面的优先。不同车道上的车，如果在同一位置，那么，车道号小的优先
    	Car car = null;
    	for (int i = 0; i < channels.size(); i++) {
    		Channel channel = channels.get(i);
    		LinkedList<Car> carList = channel.getCarList();
    		if (carList.size() == 0) {
    			continue;
    		}
    		Car first = carList.getFirst();
    		if (first.getState() == 3) {
    			continue;
    		} else {
    			if (first.getState() != 2) {
    				logger.info("first car state != 2 equals = " + first.getState());
    			}
    			if (car == null) {
    				car = first;
    			} else {
    				if (first.getPosition() < car.getPosition()) {
    					car = first;
    				}
    			}
    		}
    	}
    	return car;
    }
    public static int getIndexByDir(int curDirIndex, String dir) {
    	if (curDirIndex == 0) {
    		if ("D".equals(dir)) {
    			return 2;
    		} else if ("L".equals(dir)) {
    			return 1;
    		} else {
    			return 3;
    		}
    	} else if (curDirIndex == 1) {
    		if ("D".equals(dir)) {
    			return 3;
    		} else if ("L".equals(dir)) {
    			return 2;
    		} else {
    			return 0;
    		}
    	} else if (curDirIndex == 2) {
    		if ("D".equals(dir)) {
    			return 0;
    		} else if ("L".equals(dir)) {
    			return 3;
    		} else {
    			return 1;
    		}
    	} else {
    		if ("D".equals(dir)) {
    			return 1;
    		} else if ("L".equals(dir)) {
    			return 0;
    		} else {
    			return 2;
    		}
    	}
    }
    public static String judgeDir(int curDirIndex, int nextDirIndex) {
    	if (curDirIndex == 0) {
    		if (nextDirIndex == 0) {
    			logger.info("dir err!!");
    		}
			if (nextDirIndex == 1) {
				return "L";
			} else if (nextDirIndex == 2) {
				return "D";
			} else  {
				return "R";
			} 
		} else if (curDirIndex == 1) {
			if (nextDirIndex == 1) {
    			logger.info("dir err!!");
    		}
			if (nextDirIndex == 0) {
				return "R";
			} else if (nextDirIndex == 2) {
				return "L";
			} else  {
				return "D";
			} 
		} else if (curDirIndex == 2) {
			if (nextDirIndex == 2) {
    			logger.info("dir err!!");
    		}
			if (nextDirIndex == 0) {
				return "D";
			} else if (nextDirIndex == 1) {
				return "R";
			} else {
				return "L";
			}
		} else if (curDirIndex == 3){
			if (nextDirIndex == 3) {
    			logger.info("dir err!!");
    		}
			if (nextDirIndex == 0) {
				return "L";
			} else if (nextDirIndex == 1) {
				return "D";
			} else {
				return "R";
			}
		} else {
			logger.info("curDirIndex ERRRRRR!!");
			return null;
		}
    }
    public static String getCarDir(Car car, int curIndex, TreeMap<Integer, Integer> dirMap) {
    	int nextRoadIndex =  car.getNextRoadIndex();
    	if (car.isEnd()) {
    		return "D";
    	}
    	// 注意数组越界
		Integer nextRoadId = car.getRoadList().get(nextRoadIndex);
//		logger.info(car.getRoadId() + " : " + nextRoadId);
		if (nextRoadId == null) {
			logger.info("nextRoadId == null !!??");
		}
		Integer nextIndex = dirMap.get(nextRoadId);
		if (nextIndex == null) {
			logger.error("nextIndex == null");
		}
		return judgeDir(curIndex, nextIndex);
    }
    // 判断当前车能不能进入下一道路
    public static boolean checkNextChannels(Car car, Road curRoad, Road nextRoad, ArrayList<Channel> nextChannels, int nextForward) {
    	int v1 = Math.min(car.getSpeed(), curRoad.getSpeed());
    	int v2 = Math.min(car.getSpeed(), nextRoad.getSpeed());
    	Channel curChannel;
    	if (car.getForward() == 1) {
    		ArrayList<Channel> fromChannels = curRoad.getFromChannels();
    		curChannel = fromChannels.get(car.getCid());
    	} else {
    		ArrayList<Channel> toChannels = curRoad.getToChannels();
    		curChannel = toChannels.get(car.getCid());
    	}
    	
    	// 当前道路可行使最大距离
    	int s1 = car.getPosition() - 1;
    	if (s1 >= v1) {
    		logger.error("s1 >= v1!!");
    		
    	}
    	for (int i = 0; i < nextChannels.size(); i++) {
    		Channel channel = nextChannels.get(i);
    		LinkedList<Car> carList = channel.getCarList();
    		int s2 = v2 - s1 < 0 ? 0 : v2 - s1;
    		if (carList.isEmpty()) {
    			if (s2 == 0) {
    				car.setPosition(1);
        			endNum++;
        			car.setState(3);
        			return true;
    			} else {
    				car.setCarInfo(nextRoad.getId(), nextForward, i, nextRoad.getLength() - s2 + 1);
	    			car.setState(3);
	    			endNum++;
	    			car.nextRoadIndexPlus();
	    			channel.getCarList().add(car);
					curChannel.getCarList().poll();
					return true;
    			}
    		}
    		Car endCar = carList.getLast();
    		int couldRun = nextRoad.getLength() - endCar.getPosition();
    		if (endCar.getPosition() > nextRoad.getLength()) {
    			logger.error("endCar.getPosition > nextRoad.getLength !!!");
    		}
    		
    		if (s2 == 0) {
    			car.setPosition(1);
    			endNum++;
    			car.setState(3);
    			return true;
    		} else {
    			if (s2 <= couldRun) {
    				car.setCarInfo(nextRoad.getId(), nextForward, i, nextRoad.getLength() - s2 + 1);
    				endNum++;
    				car.setState(3);
    				car.nextRoadIndexPlus();
    				channel.getCarList().add(car);
    				curChannel.getCarList().poll();
    				return true;
    			} else {
    				if (endCar.getState() == 3) {
    					if (couldRun == 0) {
    						continue;
    					}
    					car.setCarInfo(nextRoad.getId(), nextForward, i, endCar.getPosition() + 1);
        				car.setState(3);
        				endNum++;
        				car.nextRoadIndexPlus();
        				channel.getCarList().add(car);
        				curChannel.getCarList().poll();
        				return true;
    				} else {
    					car.setState(2);
    					if (endCar.getState() != 2) {
    						logger.info("endCar.State != 2 ??");
    					}
    					return false;
    				}
    			}
    		}
    	}
    	// 所有道路都不能走，且最后一辆车都为终止态，那么此车也为终止
    	car.setPosition(1);
    	car.setState(3);
    	endNum++;
//    	checkRoadsChannel(curRoad, curChannel);
    	return true;
    }
    public static boolean leave(Road curRoad, Cross curCross, Car car) {
    	int nextRoadIndex =  car.getNextRoadIndex();
    	ArrayList<Channel> channels = null;
    	if (curRoad.getTo() == curCross.getId()) {
			channels = curRoad.getFromChannels();
		} else if (curRoad.getIsDuplex() == 1 && curRoad.getFrom() == curCross.getId()) {
			channels = curRoad.getToChannels();
		}
    	if (channels == null) {
    		logger.error("leave channels == null");
    	}
    	int channelId = car.getCid();
    	Channel channel = channels.get(channelId);
    	LinkedList<Car> carList = channel.getCarList();
    	// 到达目的地
    	if (car.isEnd()) {
    		arrivedNum++;
    		car.setState(4);
    		Car poll = carList.poll();
    		if (poll.getId() != car.getId()) {
    			logger.error("poll.id != car.id");
    		}
    		checkRoadsChannel(curRoad, channel);
    		return true;
    	}
    	// 注意数组越界
		int nextRoadId = car.getRoadList().get(nextRoadIndex);
		Road nextRoad = (Road) roads.get(roadsMp.get(nextRoadId));
		int nextForward = 0;
		ArrayList<Channel> nextChannels = null;
		if (nextRoad.getFrom() == curCross.getId()) {
			nextChannels = nextRoad.getFromChannels();
			nextForward = 1; 
		} else if (nextRoad.getIsDuplex() == 1 && nextRoad.getTo() == curCross.getId()) {
			nextChannels = nextRoad.getToChannels();
			nextForward = -1;
		}
		if (nextChannels == null) {
    		logger.error("leave nextChannels == null");
    	}
		if (nextForward == 0) {
			logger.error("nextForward == 0");
		}
		boolean isLeave = checkNextChannels(car, curRoad, nextRoad, nextChannels, nextForward);
		if (isLeave == true) {
			checkRoadsChannel(curRoad, channel);
		}
    	return isLeave;
    }
    public static boolean checkCrosses() {
    	int lastWaitNum = runCarsNum - endNum - arrivedNum;
    	// 出现死锁 或者 全部车到达终止状态
    	while(true) {
    		for (int i = 0; i < crosses.size(); i++) {
    			Cross cross = (Cross) crosses.get(i);
    			// 映射为<roadId, index>
	    		TreeMap<Integer, Integer> dirMap = cross.getDirMap();
	    		// <index, roadId>
	    		HashMap<Integer, Integer> indexMap = cross.getIndexMap();
    				for (Map.Entry<Integer, Integer> entry : dirMap.entrySet()) {
    					int roadId = entry.getKey();
    					int curIndex = entry.getValue();
		    			if (roadId == -1) {
		    				continue;
		    			}
		    			Road road = (Road) roads.get(roadsMp.get(roadId));
		    			// 如果这条路的第一优先级 每次都有，那么可以一直走
		    			while(true) {
		    				boolean isRun = false;
		    				// 如果没有车出发，那么会跳出循环，让下一road获得权限
		    				Car car = null;
		    				// 第一优先级不能走，那么其他channel的也不能走
			    			if (road.getTo() == cross.getId()) {
			    				car = getChannelFirstCar(road.getFromChannels());
			    			} else if (road.getIsDuplex() == 1 && road.getFrom() == cross.getId()) {
			    				car = getChannelFirstCar(road.getToChannels());
			    			}
			    			// 没发现第一优先级的车，那么就不调度，换下一条路
			    			if (car == null) {
			    				break;
			    			}
			    			
			    			String carDir = getCarDir(car, curIndex ,dirMap);
			    			if (carDir == null) {
			    				logger.error("carDir return value == null");
			    			}
			    			car.setDir(carDir);
			    			
			    			if ("D".equals(carDir)) {
			    				boolean isLeave = leave(road, cross, car);
			    				if (isLeave) {
			    					isRun = true;
			    				} 
			    			} else if ("L".equals(carDir)) {
			    				int rIndex = getIndexByDir(curIndex, "R");
			    				Integer rRoadId = indexMap.get(rIndex);
			    				if (rRoadId == null) {
			    					logger.error("rRoadId == null");
			    				} 
		    					Car rCar = null;
		    					String rDir = null;
		    					if (rRoadId.intValue() != -1)
		    					{
		    						Road rRoad = (Road) roads.get(roadsMp.get(rRoadId));
		    						if (rRoad.getTo() == cross.getId()) {
			    						rCar = getChannelFirstCar(rRoad.getFromChannels());
					    			} else if (rRoad.getIsDuplex() == 1 && rRoad.getFrom() == cross.getId()) {
					    				rCar = getChannelFirstCar(rRoad.getToChannels());
					    			}
		    						if (rCar != null) {
		    							rDir = getCarDir(rCar, rIndex ,dirMap);
		    						}
		    					}
		    					if (rCar != null && rDir == null) {
			    					logger.error("rDir == null");
			    				}
			    				if (rCar == null || !"D".equals(rDir)) {
			    					boolean isLeave = leave(road, cross, car);
			    					if (isLeave) {
				    					isRun = true;
				    				}
			    				}
			    			} else if ("R".equals(carDir)) {
			    				/*********看左边的是否直行**********/
			    				int lIndex = getIndexByDir(curIndex, "L");
			    				Integer lRoadId = indexMap.get(lIndex);
			    				Car lCar = null;
			    				String lDir = null;
			    				if (lRoadId.intValue() != -1) {
			    					Road lRoad = (Road) roads.get(roadsMp.get(lRoadId));
		    						if (lRoad.getTo() == cross.getId()) {
			    						lCar = getChannelFirstCar(lRoad.getFromChannels());
					    			} else if (lRoad.getIsDuplex() == 1 && lRoad.getFrom() == cross.getId()) {
					    				lCar = getChannelFirstCar(lRoad.getToChannels());
					    			}
		    						if (lCar != null) {
		    							lDir = getCarDir(lCar, lIndex, dirMap);
		    						}
			    				}
			    				/*****对面的是否左转******/
			    				int dIndex = getIndexByDir(curIndex, "D");
			    				Integer dRoadId = indexMap.get(dIndex);
			    				Car dCar = null;
			    				String dDir = null;
			    				if (dRoadId.intValue() != -1) {
			    					Road dRoad = (Road) roads.get(roadsMp.get(dRoadId));
		    						if (dRoad.getTo() == cross.getId()) {
			    						dCar = getChannelFirstCar(dRoad.getFromChannels());
					    			} else if (dRoad.getIsDuplex() == 1 && dRoad.getFrom() == cross.getId()) {
					    				dCar = getChannelFirstCar(dRoad.getToChannels());
					    			}
		    						if (dCar != null) {
		    							dDir = getCarDir(dCar, dIndex, dirMap);
		    						}
			    				}
			    				if ((lCar == null || !"D".equals(lDir)) && (dCar == null || !"L".equals(dDir))) {
			    					boolean isLeave = leave(road, cross, car);
			    					if (isLeave) {
				    					isRun = true;
				    				} 
			    				}
			    			} else {
			    				logger.error("car dir error!");
			    			}
			    			if (isRun == false) {
			    				break;
			    			}
		    			}// one road
		    			
    				}// four road
    		} // all crosses
    		int waitNum = runCarsNum - endNum - arrivedNum;
//    		logger.info("lastWait = " + lastWaitNum);
//    		logger.info("runCar = " + runCarsNum + "  arrived = " + arrivedNum + "  endNum=" + endNum + "  waitNum=" + waitNum);
    		
    		if (waitNum == lastWaitNum) {
    			return false;
    		}
    		if (waitNum == 0) {
    			break;
    		}
    		lastWaitNum = waitNum;
    	}
    	return true;
    }
    public static void driveCarInGarage(int time, ArrayList<Object> cars) {
    	for (int i = 0 ; i < cars.size(); i++) {
    		Car car = (Car) cars.get(i);
    		if (car.getState() != 0 || car.getRealTime() > time) {
    			continue;
    		}
    		int nextRoadId = car.getRoadList().get(0);
    		Road road = (Road) roads.get(roadsMp.get(nextRoadId));
    		ArrayList<Channel> channels = null;
    		int forward = 0;
    		if (car.getFrom() == road.getFrom()) {
    			channels = road.getFromChannels();
    			forward = 1;
    		} else if(road.getIsDuplex() == 1 && car.getFrom() == road.getTo()) {
    			channels = road.getToChannels();
    			forward = -1;
    		}
    		if (channels == null ) {
    			logger.error("driveCarInGarage channles == null!");
    		}
    		int maxSpeed = Math.min(car.getSpeed(), road.getSpeed());
    		for (int j = 0 ; j < channels.size(); j++) {
    			Channel channel = channels.get(j);
    			LinkedList<Car> carList = channel.getCarList();
    			if (carList.isEmpty()) {
    				car.setState(3);
    				car.setCarInfo(nextRoadId, forward, j, road.getLength() - maxSpeed + 1);
    				car.nextRoadIndexPlus();
    				carList.add(car);
    				runCarsNum++;
    				
    				break;
    			}
    			Car lastCar = carList.getLast();
    			if (lastCar.getState() != 3) {
    				logger.error("driveCarInGarage lastCar.state != 3 equals :" + lastCar.getState());
    				logger.error("lastCar : " + lastCar);
    			}
    			if (lastCar.getPosition() == road.getLength()) {
    				continue;
    			}
    			int couldRun = road.getLength() - lastCar.getPosition();
    			if (couldRun == 0)
    			{
    				continue;
    			}
    			if (maxSpeed >= couldRun) {
    				car.setState(3);
    				car.setCarInfo(nextRoadId, forward, j, lastCar.getPosition() + 1);
    				car.nextRoadIndexPlus();
    				carList.add(car);
    				runCarsNum++;
    				break;
    			} else {
    				car.setState(3);
    				car.setCarInfo(nextRoadId, forward, j, road.getLength() - maxSpeed + 1);
    				carList.add(car);
    				car.nextRoadIndexPlus();
    				runCarsNum++;
    				break;
    			}
    		}
    	}
    }
    // 判断器 包含三大步
    public static boolean judge(int startTime, ArrayList<Object> cars) {
    	// 不能单独计算等待的车辆，因为，在checkRoads函数会复用，
    	// 在第一步和第二步会用，按照setStatus==2 加减 waitNum 不可取
    	// 然而，终止的车辆是不会有变化的
    	// 所以 waitNum = runCarsNum(在路上的车) - endNum 
    	runCarsNum = 0;
    	arrivedNum = 0;
    	for (time = startTime; ; time++) {
    		// 终止状态的车每次置零
    		endNum = 0;
    		checkRoads();
    		int waitNum = runCarsNum - endNum - arrivedNum; 
    		
    		if (waitNum != 0) {
    			
    			boolean success = checkCrosses();	
    			if (success == false) {
    				logger.info("waitNum = " + (runCarsNum - endNum- arrivedNum));

    				return false;
        		}
    		}
    		if (arrivedNum == cars.size()) {
    			logger.info("time : " + time);
    			break;
    		}
    		driveCarInGarage(time, cars);
    		// init state
    		for (int i = 0 ; i < cars.size(); i++) {
    			Car car = (Car) cars.get(i);
    			if (car.getState() == 2) {
    				logger.error("调度错误，diao du wan ");
    			}
    			if (car.getState() == 3) {
    				car.setState(1);
    			} 
    		}
    	}
    	return true;
    }
    
    public static int random(int min, int max) {
    	return new Random().nextInt(max - min) + min;
    }
    public static void main(String[] args)
    {
    	 logger.info("Start...");
//        if (args.length != 4) {
//            logger.error("please input args: inputFilePath, resultFilePath");
//            return;
//        }
//        String carPath = args[0];
//        String roadPath = args[1];
//        String crossPath = args[2];
//        String answerPath = args[3];
        
        String carPath = "SDK_java\\bin\\config\\car.txt";
        String roadPath = "SDK_java\\bin\\config\\road.txt";
        String crossPath = "SDK_java\\bin\\config\\cross.txt";
        String answerPath = "SDK_java\\bin\\config\\answer.txt";
        
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);
        // TODO:read input files
        logger.info("start read input files");
        
        ArrayList<Object> cars = new ArrayList<Object>();
        
        PriorityQueue<Object> priCars = new PriorityQueue<Object>(new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Car car1 = (Car) o1;
				Car car2 = (Car) o2;
				if (car1.getRealTime() == car2.getRealTime()) {
					return car1.getId() - car2.getId();
				} else {
					return car1.getRealTime() - car2.getRealTime();
				}
			}
        });
        ArrayList<Object> runCars = new ArrayList<Object>();
        try {
			FileOpt.readFile(carPath, cars, "Car");
			FileOpt.readFile(crossPath, crosses, "Cross");
			FileOpt.readFile(roadPath, roads, "Road");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        crosses.sort(new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Cross cross1 = (Cross)o1;
				Cross cross2 = (Cross)o2;
				return cross1.getId() - cross2.getId();
			}
        });
       
        // TODO: calc
        // init
        for (int i = 0; i < roads.size(); i++) {
        	Road road = (Road)roads.get(i);
        	roadsMp.put(road.getId(), i);
        	road.clearChannels();
        }
        for (int i = 0; i < crosses.size(); i++) {
        	Cross cross = (Cross)crosses.get(i);
        	crossMp.put(cross.getId(), i);
        }
        
        for (int i = 0; i < cars.size(); i++) {
        	Car car = (Car)cars.get(i);
        	ArrayList<Integer> list = dij(car);
        	car.setRoadList(list);
        	priCars.add(car);
        }
        
    	cars.sort(new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Car car1  = (Car)o1;
				Car car2  = (Car)o2;
				if (car1.getRealTime() == car2.getRealTime()) {
					return car1.getId() - car2.getId();
				} else {
					return car1.getRealTime() - car2.getRealTime();
				}
				
			}
		});
    	
    	
    	
    	
    	
    	
    	
    	
//    	int lastTime = 0;
//        int posTime = 0;
//        int minTime = INF;
//    	for (int i = 0; i < cars.size(); i++) {
//    		Car car = (Car) cars.get(i);
//    		int runCarsSize = runCars.size();
//    		if (runCarsSize == 0) {
//    			car.setRealTime(Math.max(posTime, car.getPlanTime()));
//    			minTime = Math.min(car.getRealTime(), minTime);
//    			runCars.add(car);
//    		} else {
//    			Car lastCar = (Car) runCars.get(runCarsSize -1);
//    			car.setRealTime(car.getPlanTime() - lastCar.getPlanTime() + lastCar.getRealTime());
//    			minTime = Math.min(car.getRealTime(), minTime);
//    			runCars.add(car);
//    		}
//    		for (int j = 0; j < roads.size(); j++) {
//        		Road road = (Road) roads.get(j);
//        		road.clearChannels();
//        	}
//    		for (int j = 0; j < runCars.size(); j++) {
//    			Car runCar = (Car) runCars.get(j);
//    			runCar.setState(0);
//    			runCar.setNextRoadIndex(0);
//    			runCar.setCarInfo(-1, 0, -1, 0);
//    		}
//    		if (judge(minTime, runCars)) {
//    			lastTime = time;
//    		} else {
//    			logger.info("runCarsSize = " + runCars.size());
//    			logger.info("time : " + time);
//    			logger.info("i: " + i);
//    			runCars.clear();
//    			posTime = lastTime + 1;
//    			car.setRealTime(Math.max(posTime, car.getPlanTime()));
//    			runCars.add(car);
//    			
//    		}
//    	}
//       
        int range = 400;
        int step = 20;
        int cnt = 0;
        int mod = 2;
        while(true) {
        	int startTime = INF;
        	int maxRealTime = 0;
        	// 初始化每辆车
        	for (int i = 0; i < cars.size(); i++) {
        		Car car = (Car) cars.get(i);
        		int randNum = random(0, range);
        		//randNum = randNum - car.getTime()   > 0 ? randNum - car.getTime()  : 0;
        		int realTime = car.getPlanTime() + randNum;
        		startTime = Math.min(startTime, realTime);
        		maxRealTime = Math.max(maxRealTime, realTime);
        		car.setRealTime(realTime);
        		// 所有车都没出发
        		car.setState(0);
        		car.setNextRoadIndex(0);
        		car.setCarInfo(-1, 0, -1, 0);
        	}
        	cars.sort(new Comparator<Object>() {
    			@Override
    			public int compare(Object o1, Object o2) {
    				Car car1  = (Car)o1;
    				Car car2  = (Car)o2;
    				if (car1.getRealTime() == car2.getRealTime()) {
    					return car1.getId() - car2.getId();
    				} else {
    					return car1.getRealTime() - car2.getRealTime();
    				}
    				
    			}
    		});
        	for (int i = 0; i < roads.size(); i++) {
        		Road road = (Road) roads.get(i);
        		road.clearChannels();
        	}
        	if (judge(startTime, cars) == true) {
        		logger.info(range + " success!" + " maxRealTime = " + maxRealTime);
        		for (int i = 0; i < cars.size(); i++) {
        			Car car = (Car) cars.get(i);
        			if (car.getState() != 4) {
        				logger.info(car.toString() + "not arriaved!!!!!!!!!!!!!!!!!!!");
        			}
        		}
        		break;
        	} else {
        		logger.info(range + " dead lock!" + " maxRealTime = " + maxRealTime);
        	}
        	cnt++;
        	if (cnt % mod == 0) {
        		cnt = 0;
        		range += step;
        	}
        	
        }
        
        
        // TODO: write answer.txt
        logger.info("Start write output file");
        try {
			FileOpt.writeFile(answerPath, cars);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        logger.info("End...");
    }
}