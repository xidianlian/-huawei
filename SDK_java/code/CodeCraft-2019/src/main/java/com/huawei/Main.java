package com.huawei;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

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
    
    private static ArrayList<Object> cars = new ArrayList<Object>();
    
    private static ArrayList<Object> crosses = new ArrayList<Object>();
    
    private static ArrayList<Object> roads = new ArrayList<Object>();
    
    private static int runCarsNum;
    
    private static int waitNum;
    
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
    	LinkedList<Node> que = new LinkedList<Node>();
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
    		for (int i = 0 ; i < roadIds.length; i++) {
    			int roadId = roadIds[i];
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
					car.setState(3); 
				} else {
					car.setState(2);
				}
				
			} else {
				length = car.getPosition() - lastCar.getPosition() - 1;
				if (length >= maxSpeed) {
					car.setPosition(car.getPosition() - maxSpeed);
					car.setState(3);
				} else {
					if (lastCar.getState() == 3) {
						car.setPosition(car.getPosition() - length);
						car.setState(3);
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
    		Car first = carList.getFirst();
    		if (first.getState() == 3) {
    			continue;
    		} else {
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
    public static String judgeDir(int curDirIndex, int nextDirIndex) {
    	if (curDirIndex == 0) {
			if (nextDirIndex == 1) {
				return "L";
			} else if (nextDirIndex == 2) {
				return "D";
			} else  {
				return "R";
			} 
		} else if (curDirIndex == 1) {
			if (nextDirIndex == 0) {
				return "R";
			} else if (nextDirIndex == 2) {
				return "L";
			} else  {
				return "D";
			} 
		} else if (curDirIndex == 2) {
			if (nextDirIndex == 0) {
				return "D";
			} else if (nextDirIndex == 1) {
				return "R";
			} else {
				return "L";
			}
		} else {
			if (nextDirIndex == 0) {
				return "L";
			} else if (nextDirIndex == 1) {
				return "D";
			} else {
				return "R";
			}
		} 
    }
    public static String getCarDir(int curDirIndex, Car car, TreeMap<Integer, Integer> dirMap) {
    	int nextRoadIndex =  car.getNextRoadIndex();
    	if (nextRoadIndex == -1) {
    		return "D";
    	}
    	// 注意数组越界
		int nextRoadId = car.getRoadList().get(nextRoadIndex);
		int nextDirIndex = dirMap.get(nextRoadId);
		return judgeDir(curDirIndex,  nextDirIndex);
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
    		return false;
    	}
    	for (int i = 0; i < nextChannels.size(); i++) {
    		Channel channel = nextChannels.get(i);
    		LinkedList<Car> carList = channel.getCarList();
    		Car endCar = carList.getLast();
    		int couldRun = nextRoad.getLength() - endCar.getPosition();
    		int s2 = v2 - s1 < 0 ? 0 : v2 - s1;
    		if (s2 == 0) {
    			car.setPosition(1);
    			car.setState(3);
    			return true;
    		} else {
    			if (s2 <= couldRun) {
    				car.setCarInfo(nextRoad.getId(), nextForward, i, nextRoad.getLength() - s2 + 1);
    				
    				car.setState(3);
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
        				channel.getCarList().add(car);
        				curChannel.getCarList().poll();
        				return true;
    				} else {
    					car.setState(2);
    					return false;
    				}
    			}
    		}
    	}
    	
    	return false;
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
    	if (nextRoadIndex == -1) {
    		
    		car.setState(4);
    		carList.poll();
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
    	int lastWaitNum = waitNum;
    	// 出现死锁 或者 全部车到达终止状态
    	while(true) {
    		
    		for (int i = 0; i < crosses.size(); i++) {
    			Cross cross = (Cross) crosses.get(i);
    			// 映射为<roaId, index>
	    		TreeMap<Integer, Integer> dirMap = cross.getDirMap();
	    		
    			while (true) {
    				// 先把四个方向第一优先级的车存下来 <roadId, Car>
		    		TreeMap<Integer, Car> carMap = new TreeMap<Integer, Car>();
		    		// 每个路口按道路id升序
		    		for (Map.Entry<Integer, Integer> entry : dirMap.entrySet()) {
		    			int roadId = entry.getKey().intValue();
		    			int curDirIndex = entry.getValue();
		    			if (roadId == -1) {
		    				continue;
		    			}
		    			Road road = (Road) roads.get(roadsMp.get(roadId)); 
		    			Car car = null;
		    			// 第一优先级不能走，那么其他channel的也不能走
		    			if (road.getTo() == cross.getId()) {
		    				car = getChannelFirstCar(road.getFromChannels());
		    			} else if (road.getIsDuplex() == 1 && road.getFrom() == cross.getId()) {
		    				car = getChannelFirstCar(road.getToChannels());
		    			}
		    			if (car == null) {
		    				continue;
		    			}
		    			String carDir = getCarDir(curDirIndex, car, dirMap);
		    			car.setDir(carDir);
		    			carMap.put(roadId, car);
		    		}
		    		// 四个方向的车，只要出发了一辆，就标记为true
		    		boolean flag = false;
		    		// 注意：即使车快要到达终点，也会受到路口其他车的影响（按照交通规则）
	    			for (Map.Entry<Integer, Car> entry : carMap.entrySet()) {
		    			int roadId = entry.getKey();
		    			Car car = entry.getValue();
		    			Road road = (Road) roads.get(roadsMp.get(roadId));
		    			HashMap<String, Integer> roadDirMap = null;
		    			if (road.getTo() == cross.getId()) {
		    				roadDirMap = road.getToDirMap();
		    			} else if (road.getIsDuplex() == 1 && road.getFrom() == cross.getId()) {
		    				roadDirMap = road.getFromDirMap();
		    			}
		    			
		    			if (car.getDir() == "D") {
		    				boolean isLeave = leave(road, cross, car);
		    				if (isLeave) {
		    					flag = true;
		    				}
		    			} else if (car.getDir() == "L") {
		    				Integer rRoadId = roadDirMap.get("R");
		    				Car rCar = carMap.get(rRoadId);
		    				// 右边车为直行，那么它等待
		    				if (rCar == null || rCar.getDir() != "D") {
		    					boolean isLeave = leave(road, cross, car);
		    					if (isLeave) {
			    					flag = true;
			    				}
		    				} else {
		    					car.setState(2);
		    				}
		    			} else if (car.getDir() == "R") {
		    				Integer lRoadId = roadDirMap.get("L");
		    				Car lCar = carMap.get(lRoadId);
		    				Integer dRoadId = roadDirMap.get("D");
		    				Car dCar = carMap.get(dRoadId);
		    				// 先看左边车是否直行，再看前方车是否左转
		    				if ((lCar == null || lCar.getDir() != "D") && (dCar == null || dCar.getDir() != "R")) {
		    					boolean isLeave = leave(road, cross, car);
		    					if (isLeave) {
			    					flag = true;
			    				}
		    				} else {
		    					car.setState(2);
		    				}
		    			}
		    			
	    			}
	    			if (flag == false) {
	    				break;
	    			}
	    		}
    		}
    		if (waitNum == lastWaitNum) {
    			return false;
    		}
    		if (waitNum == 0) {
    			break;
    		}
    	}
    	return true;
    }
    public static void driveCarInGarage(int time) {
    	for (int i = 0 ; i < cars.size(); i++) {
    		Car car = (Car) cars.get(i);
    		if (car.getState() != 0 || car.getRealTime() <= time) {
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
    			Car lastCar = carList.getLast();
    			if (lastCar.getState() != 3) {
    				logger.error("driveCarInGarage lastCar.state != 3");
    				return;
    			}
    			if (lastCar.getPosition() == road.getLength()) {
    				continue;
    			}
    			int couldRun = road.getLength() - lastCar.getPosition();
    			if (maxSpeed >= couldRun) {
    				car.setState(3);
    				car.setCarInfo(nextRoadId, forward, j, lastCar.getPosition() + 1);
    				carList.add(car);
    				runCarsNum++;
    				break;
    			} else {
    				car.setState(3);
    				car.setCarInfo(nextRoadId, forward, j, road.getLength() - maxSpeed + 1);
    				carList.add(car);
    				runCarsNum++;
    				break;
    			}
    		}
    	}
    }
    // 判断器 包含三大步
    public static boolean judge(int startTime) {
    	waitNum = 0;
    	runCarsNum = 0;
    	for (int time = startTime; ; time++) {
    		checkRoads();
    		boolean isConflict = checkCrosses();
    		if (isConflict) {
    			return false;
    		}
    		if (runCarsNum == cars.size() && waitNum == 0) {
    			break;
    		}
    		driveCarInGarage(time);
    	}
    	return true;
    }
    
    public static int random(int min, int max) {
    	return new Random().nextInt(max - min) + min;
    }
    public static int gussRandom(int u, int v) {
    	return (int) (new Random().nextGaussian() * v + u);
    }
    
    public static void main(String[] args)
    {
//        if (args.length != 4) {
//            logger.error("please input args: inputFilePath, resultFilePath");
//            return;
//        }

        logger.info("Start...");
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
        }
        for (int i = 0; i < crosses.size(); i++) {
        	Cross cross = (Cross)crosses.get(i);
        	crossMp.put(cross.getId(), i);
        	int[] roadIds = cross.getRoadIds();
        	for (int j = 0; j < roadIds.length; j++) {
        		int roadId1 = roadIds[j];
        		if (roadId1 == -1) {
        			continue;
        		}
        		Road road = (Road) roads.get(roadsMp.get(roadId1));
        		HashMap<String, Integer> dirMap = new HashMap<String, Integer>();
        		
        		for (int k = 0; k < roadIds.length; k++) {
        			int roadId2 = roadIds[k];
        			if (k == j || roadId2 == -1) {
        				continue;
        			}
        			String dir = judgeDir(j, k);
        			dirMap.put(dir, roadId2);
        		}
        		if (road.getFrom() == cross.getId()) {
        			road.setFromDirMap(dirMap);
        		} 
        		if (road.getTo() == cross.getId() && road.getIsDuplex() == 1) {
        			road.setFromDirMap(dirMap);
        		}
        	}
        }
        
        
        for (int i = 0; i < cars.size(); i++) {
        	Car car = (Car)cars.get(i);
        	ArrayList<Integer> list = dij(car);
//        	// 车的出发时间，都向后推迟一个随机数，避免堵塞
//        	int randNum =   random(1, cars.size() / 4 ) / 3;
//        	car.setRealTime(car.getPlanTime() + randNum);
        	car.setRoadList(list);
        }
        int range = cars.size() / 5;
        int step = cars.size() / 10;
        while(true) {
        	int startTime = INF;
        	// 初始化每辆车
        	for (int i = 0; i < cars.size(); i++) {
        		Car car = (Car) cars.get(i);
        		int randNum = random(1, range);
        		int realTime = car.getPlanTime() + randNum;
        		startTime = Math.min(startTime, realTime);
        		car.setRealTime(realTime);
        		// 所有车都没出发
        		car.setState(0);
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
        	
        	if (judge(startTime) == true) {
        		break;
        	} 
        	range += step;
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