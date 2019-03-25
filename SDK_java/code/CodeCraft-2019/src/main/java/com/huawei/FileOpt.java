package com.huawei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class FileOpt {
	private static final Logger logger = Logger.getLogger(Main.class);
	
	public static void readFile(String path, ArrayList<Object> obj, String type) throws IOException {
		FileInputStream fis=new FileInputStream(path);
		InputStreamReader isr=new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
        for (String line = br.readLine(); line != null; line = br.readLine()){
        	if (line.startsWith("#")) {
        		continue;
        	}
        	line = line.trim();
        	if (line.length() == 0) {
        		continue;
        	}
        	String strs[] = line.split(",");
        	int[] number = new int[strs.length];
        	for (int i = 0; i < strs.length; i++) {
        		if (i != strs.length - 1) {
        			strs[i] = strs[i].substring(1, strs[i].length()).trim();
        		} else {
        			strs[i] = strs[i].substring(1, strs[i].length() - 1).trim();
        		}
        		
        		number[i] = Integer.parseInt(strs[i]);
        	}
        	if ("Car".equals(type) ) {
        		Car car = new Car(number[0], number[1], number[2], number[3], number[4]);
        		obj.add(car);
        	} else if ("Cross".equals(type)) {
        		int[] roadIds = new int[] {number[1], number[2], number[3], number[4]};
        		Cross cross = new Cross(number[0], roadIds);
        		obj.add(cross);
        	} else if ("Road".equals(type)) {
        		Road road = new Road(number[0], number[1], number[2], number[3], number[4], number[5], number[6]);
        		obj.add(road);
        		
        	}
        }
        br.close();
	}
	
	public static void writeFile(String path, ArrayList<Object> cars) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0 ; i < cars.size(); i++) {
			Car car = (Car) cars.get(i);
			String str = "(" + car.getId() + ", " + car.getRealTime() ;
			ArrayList<Integer> list = car.getRoadList();
			if (list.size() != 0) {
				str += ", ";
			} else {
				str += ")";
			}
			for (int j = 0; j < list.size(); j++) {
				str += list.get(j);
				if (j != list.size() - 1) {
					str += ", ";
				} else {
					str += ")";
				}
			}
			bw.write(str);
			
			bw.newLine();
		}
		bw.close();
		
	}
	
}
