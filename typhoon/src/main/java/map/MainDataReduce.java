package map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainDataReduce {
	
	// 被对比的路径
	private List<Point> beRetrievedList;
	
	// 控制点划分间隙
	private double splitDistance = 0.05;
	
	// 间距的数学期望
	private double aij = 0;
	
	// 间距的总体平均
	private double dij = 0;
	
	// 形态差异的计算值
	private double sij = 0;
	
	// 相似离度
	private double cij = 0;
	
	// M值的统计
	private int countM = 0;
	
	public MainDataReduce() {
		this.beRetrievedList = new ArrayList<Point>();
	}
	
	@SuppressWarnings("unused")
	private double getDistance(String preLng, String preLat, String nextLng, String nextLat) {
		return distanceResult(Double.parseDouble(preLng), Double.parseDouble(preLat), Double.parseDouble(nextLng),
				Double.parseDouble(nextLat));
	}
	
	private double getDistance(Point prePoint, Point nextPoint) {
		return distanceResult(prePoint.getLng(), prePoint.getLat(), nextPoint.getLng(), nextPoint.getLat());
	}
	
	private double distanceResult(double preLng, double preLat, double nextLng, double nextLat) {
		// TODO Auto-generated method stub
		return Math.sqrt((preLng - nextLng) * (preLng - nextLng) + (preLat - nextLat) * (preLat - nextLat));
	}
	
	private Point getPoint(String lng, String lat) {
		return new Point(Double.parseDouble(lng), Double.parseDouble(lat));
	}
	
	private Point getPoint(double lng, double lat) {
		return new Point(lng, lat);
	}
	
	public void getBeRetrievedData(List<Point> beRetrievedData) {
		this.beRetrievedList = beRetrievedData;
	}
	
	private void getBeRetrievedData(String fileName) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
		String line;
		String[] data = null;
		while ((line = bufferedReader.readLine()) != null) {
			data = line.split("\t");
			this.beRetrievedList.add(getPoint(data[1], data[0]));
		}
		bufferedReader.close();
	}
	
	public double preReduceSelectData(List<Point> list) throws IOException {
		Point data = null;
		Point preData = null;
		
		for (int i = 0; i < list.size(); i++) {
			data = list.get(i);
			if (i != 0) {
				// 过滤重复的点
				if (data.getLng() == preData.getLng() && data.getLat() == preData.getLat()) {
					continue;
				}
				List<Point> originalList = getOriginalList(preData.getLng(), preData.getLat(), data.getLng(),
						data.getLat());
				getOriginalAndPedalData(originalList);
				preData = data;
			} else {
				preData = data;
			}
		}
		this.dij = this.dij / this.countM;
		this.sij = this.sij / this.countM;
		this.cij = this.dij + this.sij;
		return this.cij;
	}
	
	public double preReduceSelectData(String fileName) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
		String line;
		String[] data = null;
		String[] tmp = null;
		int num = 0;
		
		while ((line = bufferedReader.readLine()) != null) {
			data = line.split("\t");
			if (num == 0) {
				tmp = data;
				num++;
			} else {
				if (data[0].equals(tmp[0]) && data[1].equals(tmp[1])) {
					continue;
				}
				List<Point> originalList = getOriginalList(tmp[1], tmp[0], data[1], data[0]);
				getOriginalAndPedalData(originalList);
				tmp = data;
			}
		}
		bufferedReader.close();
		this.dij = this.dij / this.countM;
		this.sij = this.sij / this.countM;
		this.cij = this.dij + this.sij;
		
		System.out.println(this.countM + "," + this.cij);
		return this.cij;
	}
	
	/**
	 * 
	 * getGradient(计算两点之间的斜率) TODO(这里描述这个方法适用条件 – 可选) TODO(这里描述这个方法的执行流程 – 可选) TODO(这里描述这个方法的使用方法 – 可选)
	 * TODO(这里描述这个方法的注意事项 – 可选)
	 * 
	 * @Title: getGradient
	 * @Description: TODO
	 * @param @param prePoint
	 * @param @param nextPoint
	 * @param @return 设定文件
	 * @return double 返回类型
	 * @throws
	 */
	private double getGradient(Point prePoint, Point nextPoint) {
		return (nextPoint.getLat() - prePoint.getLat()) / (nextPoint.getLng() - prePoint.getLng());
	}
	
	private double getHeightDistance(Point point, Point pedalPoint) {
		return Math.abs(point.getLat() - pedalPoint.getLat());
	}
	
	private void getOriginalAndPedalData(List<Point> originalList) {
		// List<Point> list = new ArrayList<Point>();
		for (int i = 0; i < originalList.size() - 1; i++) {
			Point point = (Point) originalList.get(i);
			Point pedalPoint = getSpecificPedalByPoint(point);
			// int result = getVectorValue(pedalPoint, point, (Point) originalList.get(i + 1));
			
			double tmp = getDistance(point, pedalPoint);
			// double tmp = getHeightDistance(point, pedalPoint);
			
			this.countM++;
			
			this.aij += tmp;
			this.dij += tmp;
			this.sij += Math.abs(tmp - (this.aij / this.countM));
		}
	}
	
	public int getVectorValue(Point pedalPoint, Point retrievePoint, Point nextPoint) {
		Point result;
		
		if (retrievePoint.getLat() - nextPoint.getLat() == 0) {
			result = new Point(pedalPoint.getLng(), retrievePoint.getLat());
		} else if (retrievePoint.getLng() - nextPoint.getLng() == 0) {
			result = new Point(retrievePoint.getLng(), pedalPoint.getLat());
		} else {
			double k = getGradient(retrievePoint, nextPoint);
			double b_bc = retrievePoint.getLat() - k * retrievePoint.getLng();
			double b_a = pedalPoint.getLat() + k * pedalPoint.getLng();
			
			double y = (b_bc + b_a) / 2;
			double x = (y - b_bc) / k;
			
			result = new Point(x, y);
		}
		if (result.getLng() < pedalPoint.getLng())
			return -1;
		else
			return 1;
	}
	
	/***
	 * 
	 * getDirectionVectorValue(类似于求角B的acos值) TODO(这里描述这个方法适用条件 – 可选) TODO(这里描述这个方法的执行流程 – 可选) TODO(这里描述这个方法的使用方法 – 可选)
	 * TODO(这里描述这个方法的注意事项 – 可选)
	 * 
	 * @Title: getDirectionVectorValue
	 * @Description: TODO
	 * @param @param pedalPoint/aPoing
	 * @param @param retrievePoint/bPoing
	 * @param @param nextPoint/cPoing
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	private int getDirectionVectorValue(Point pedalPoint, Point retrievePoint, Point nextPoint) {
		
		double bcDistance = getDistance(retrievePoint, nextPoint);
		double acDistance = getDistance(pedalPoint, nextPoint);
		double abDistance = getDistance(pedalPoint, retrievePoint);
		
		double angleASquare = bcDistance * bcDistance;
		double angleBSquare = acDistance * acDistance;
		double angleCSquare = abDistance * abDistance;
		
		double cosBValue = (angleASquare + angleCSquare - angleBSquare) / (2 * bcDistance * abDistance);
		double k = Math.acos(cosBValue) * (180 / Math.PI);
		
		if (k > 90)
			return -1;
		else
			return 1;
	}
	
	private Point getPedalPoint(Point aPoint, Point bPoint, Point cPoint) {
		
		if (getDirectionVectorValue(bPoint, cPoint, aPoint) < 0 || getDirectionVectorValue(cPoint, bPoint, aPoint) < 0) {
			return getDistance(aPoint, bPoint) < getDistance(aPoint, cPoint) ? bPoint : cPoint;
		}
		
		if (bPoint.getLat() - cPoint.getLat() == 0) {
			return new Point(aPoint.getLng(), bPoint.getLat());
		} else if (bPoint.getLng() - cPoint.getLng() == 0) {
			return new Point(bPoint.getLng(), aPoint.getLat());
		} else {
			double k = getGradient(bPoint, cPoint);
			double b_bc = bPoint.getLat() - k * bPoint.getLng();
			double b_a = aPoint.getLat() + k * aPoint.getLng();
			
			double y = (b_bc + b_a) / 2;
			double x = (y - b_bc) / k;
			
			Point result = new Point(x, y);
			return result;
		}
	}
	
	private Point cloneObject(Point point) {
		Point tmpPoint = new Point();
		tmpPoint.setLat(point.getLat());
		tmpPoint.setLng(point.getLng());
		return tmpPoint;
	}
	
	private Point getSpecificPedalByPoint(Point originalPoint) {
		Point pedalPoint = null;
		Point tmpPoint = null;
		
		double value;
		double distance = Double.MAX_VALUE;
		
		for (int i = 0; i < this.beRetrievedList.size() - 1; i++) {
			tmpPoint = getPedalPoint(originalPoint, this.beRetrievedList.get(i), this.beRetrievedList.get(i + 1));
			value = getDistance(originalPoint, tmpPoint);
			if (value < distance) {
				pedalPoint = cloneObject(tmpPoint);
				distance = value;
			}
		}
		return pedalPoint;
	}
	
	private void print(Point point) {
		System.out.print("(" + point.getLng() + "," + point.getLat() + "), ");
	}
	
	public List<Point> getOriginalList(double preLng, double preLat, double nextLng, double nextLat) {
		
		Point prePoint = getPoint(preLng, preLat);
		Point nextPoint = getPoint(nextLng, nextLat);
		
		double distance = getDistance(prePoint, nextPoint);
		// 按照splitDistance的间隔来划分线段
		int count = (int) (distance / this.splitDistance);
		List<Point> list = new ArrayList<Point>();
		list.add(prePoint);
		
		double k = getGradient(prePoint, nextPoint);
		int complexorValue = k > 0 ? 1 : -1;
		
		double average_distance_x = Math.abs(prePoint.getLng() - nextPoint.getLng()) / count;
		double average_distance_y = Math.abs(prePoint.getLat() - nextPoint.getLat()) / count;
		
		for (int i = 1; i < count; i++) {
			Point point = new Point();
			double lng = prePoint.getLng() + complexorValue * average_distance_x * i;
			double lat = prePoint.getLat() + complexorValue * average_distance_y * i;
			
			DecimalFormat df = new DecimalFormat("#.##");
			point.setLng(Double.parseDouble(df.format(lng)));
			point.setLat(Double.parseDouble(df.format(lat)));
			list.add(point);
		}
		return list;
	}
	
	private List<Point> getOriginalList(String preLng, String preLat, String nextLng, String nextLat) {
		Point prePoint = getPoint(preLng, preLat);
		Point nextPoint = getPoint(nextLng, nextLat);
		
		double distance = getDistance(prePoint, nextPoint);
		// 按照splitDistance的间隔来划分线段
		int count = (int) (distance / this.splitDistance);
		List<Point> list = new ArrayList<Point>();
		list.add(prePoint);
		
		double k = getGradient(prePoint, nextPoint);
		int complexorValue = k > 0 ? 1 : -1;
		
		double average_distance_x = Math.abs(prePoint.getLng() - nextPoint.getLng()) / count;
		double average_distance_y = Math.abs(prePoint.getLat() - nextPoint.getLat()) / count;
		
		for (int i = 1; i < count; i++) {
			Point point = new Point();
			double lng = prePoint.getLng() + complexorValue * average_distance_x * i;
			double lat = prePoint.getLat() + complexorValue * average_distance_y * i;
			
			DecimalFormat df = new DecimalFormat("#.##");
			point.setLng(Double.parseDouble(df.format(lng)));
			point.setLat(Double.parseDouble(df.format(lat)));
			list.add(point);
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		MainDataReduce mainDataReduce = new MainDataReduce();
		String fileName1 = "./data/201120.txt";
		String fileName2 = "./data/201121.txt";
		
		List<Point> tmpList = new ArrayList<Point>();
		List<Point> dataList = new ArrayList<Point>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName1)));
		String line;
		String[] data;
		String[] tmp = new String[] { "-1", "-1" };
		while((line = bufferedReader.readLine()) != null){
			data = line.split("\t");
			if(data[1].equals(tmp[1]) && data[0].equals(tmp[0])){
				continue;
			}else{
				tmp = data;
				tmpList.add(mainDataReduce.getPoint(data[1], data[0]));
			}
		}
		bufferedReader.close();
		
		tmp = new String[] { "-1", "-1" };
		bufferedReader = new BufferedReader(new FileReader(new File(fileName2)));
		while((line = bufferedReader.readLine()) != null){
			data = line.split("\t");
			if(data[1].equals(tmp[1]) && data[0].equals(tmp[0])){
				continue;
			}else{
				tmp = data;
				dataList.add(mainDataReduce.getPoint(data[1], data[0]));
			}
		}
		bufferedReader.close();
		
		
		mainDataReduce.getBeRetrievedData(tmpList);
		double value = mainDataReduce.preReduceSelectData(dataList);
		System.out.println(value);
	}
	public void print(List<Point> list) {
		for (int i = 0; i < list.size(); i++) {
			Point point = list.get(i);
			System.out.println("("+point.getLng()+", "+point.getLat()+")");
		}
	}
	public void clear(){
		this.aij = 0;
		this.cij = 0;
		this.sij = 0;
		this.dij = 0;
		this.countM = 0;
	}
}
