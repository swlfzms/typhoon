package excel;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.MainDataReduce;
import map.Point;
import model.SQLStatement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import service.ExcelOperateService;
import service.TyphoonYearService;
import dao.DataDao;

public class ExcelOperate {

	private String[][] result;
	private String[][] serialNumberResult;
	private int rowCount = 0;

	private String path = ExcelOperate.class.getClassLoader().getResource("")
			.getPath();

//	public static void main(String[] args) throws Exception {
//		File file = new File("./data/20112014bk.xls");
//		if (!file.exists()) {
//			System.out.println("文件不存在");
//			return;
//		}
//		long startTime = System.currentTimeMillis();
//		ExcelOperate excelOperate = new ExcelOperate();
//		file = new File("./data/20112014bk.xls");
//		excelOperate.reduce(file);
//		long endTime = System.currentTimeMillis();
//		System.out.println("记录数： " + excelOperate.rowCount + ", 耗时："
//				+ (endTime - startTime));
//	}

	private List<Point> cloneList(List<Point> beRetrievedList) {
		List<Point> list = new ArrayList<Point>();
		for (int i = 0; i < beRetrievedList.size(); i++) {
			list.add(beRetrievedList.get(i));
		}
		return list;
	}

	public void getFamiliarSNIntTmpDir() throws IOException, SQLException {
		List<Point> beRetrievedList = new ArrayList<Point>();
		List<Point> dataList = new ArrayList<Point>();
		Map<String, List<Point>> map = new LinkedHashMap<String, List<Point>>();

		this.serialNumberResult = new String[this.result.length][6];

		String tmpSN = "";
		String serialNumber = "";
		Point tmpPoint = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
		int first = 0;

		for (int i = 0; i < this.result.length; i++) {
			serialNumber = this.result[i][0];
			Point point = new Point(this.result[i][3], this.result[i][2]);

			if (point.getLat() == tmpPoint.getLat()
					&& point.getLng() == tmpPoint.getLng()) {
				continue;
			} else {
				tmpPoint.setLat(point.getLat());
				tmpPoint.setLng(point.getLng());
			}
			beRetrievedList.add(point);

			if (first != 0) {
				if (!serialNumber.equals(tmpSN)) {
					Point lastPoint = beRetrievedList.get(beRetrievedList
							.size() - 1);
					beRetrievedList = beRetrievedList.subList(0,
							beRetrievedList.size() - 1);
					map.put(tmpSN, cloneList(beRetrievedList));

					beRetrievedList.clear();
					beRetrievedList.add(lastPoint);
					tmpSN = serialNumber;
				}
			} else {
				tmpSN = serialNumber;
				first = 1;
			}
		}
		map.put(serialNumber, cloneList(beRetrievedList));

		MainDataReduce mainDataReduce = new MainDataReduce();
		Set<String> set = map.keySet();
		this.rowCount = set.size();

		ExcelOperateService excelOperateService = new ExcelOperateService();
		Map<String, List<Point>> dataInDB = excelOperateService.getData();
		Map<String, List<Point>> allData = new LinkedHashMap<String, List<Point>>();
		allData.putAll(map);
		allData.putAll(dataInDB);

		int row = 0;
		for (String key : set) {
			dataList = map.get(key);

			Map<String, Double> similarSNMap = new LinkedHashMap<String, Double>();
			Set<String> iteratorSet = allData.keySet();

			for (String tmp : iteratorSet) {
				if (!key.equals(tmp)) {
					beRetrievedList = allData.get(tmp);
					if (beRetrievedList.size() < 10) {
						continue;
					}

					// 清除缓存数据
					mainDataReduce.clear();
					mainDataReduce
							.getBeRetrievedData(cloneList(beRetrievedList));
					double value = mainDataReduce
							.preReduceSelectData(cloneList(dataList));

					if (similarSNMap.size() < 3) {
						similarSNMap.put(tmp, value);
					} else {
						similarSNMap.put(tmp, value);
						Set<String> allSet = similarSNMap.keySet();
						List<Object[]> sortList = new ArrayList<Object[]>();

						for (String str : allSet) {
							Object[] keyValue = new Object[2];
							keyValue[0] = str;
							keyValue[1] = similarSNMap.get(str);
							sortList.add(keyValue);
						}
						for (int j = 0; j < sortList.size() - 1; j++) {
							for (int k = j + 1; k < sortList.size(); k++) {

								Object[] pre = sortList.get(j);
								Object[] next = sortList.get(k);

								if (Double.parseDouble(pre[1].toString()) > Double
										.parseDouble(next[1].toString())) {
									sortList.set(j, next);
									sortList.set(k, pre);
								}
							}
						}
						similarSNMap.clear();
						for (int j = 0; j < sortList.size() - 1; j++) {
							Object[] tmpKeyValue = sortList.get(j);
							similarSNMap.put(tmpKeyValue[0].toString(), Double
									.parseDouble(tmpKeyValue[1].toString()));
						}
					}
				}
			}

			int column = 0;
			Set<String> resultSet = similarSNMap.keySet();
			for (String resultString : resultSet) {
				this.serialNumberResult[row][column] = resultString;
				this.serialNumberResult[row][column + 1] = ""
						+ similarSNMap.get(resultString);
				column += 2;
			}
			row++;
		}
	}

	public void reduce(File file) throws Exception {

		File direction = new File(path + "tmp/");
		System.out.println(path + "tmp/");
		if (!direction.exists()) {
			direction.mkdir();
		}

		this.result = getData(file, 1);
		this.getFamiliarSNIntTmpDir();

		int rowLength = result.length;
		BufferedWriter bufferedWriter = null;
		BufferedWriter snBufferedWriter = null;

		String tmpYear = "";
		List<String> snList = new ArrayList<String>();
		List<String[]> snNameList = new ArrayList<String[]>();

		int snNumber = 0;
		for (int i = 0; i < rowLength; i++) {
			if (!snList.contains(result[i][0])) {
				snList.add(result[i][0]);
				String[] name = new String[2];
				name[0] = result[i][10];
				name[1] = result[i][11];
				snNameList.add(name);
			}

			String year = result[i][0].substring(0, 4);

			if (!year.equals(tmpYear)) {
				if (bufferedWriter != null) {
					bufferedWriter.close();
					snBufferedWriter = new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(new File(path
											+ "tmp/sn" + tmpYear + ".txt")),
									"utf8"));

					for (int j = 0; j < snList.size() - 1; j++) {
						snBufferedWriter.write(snList.get(j) + ","
								+ snNameList.get(j)[0] + ","
								+ snNameList.get(j)[1] + ",");
						for (int k = 0; k < 5; k++) {
							snBufferedWriter
									.write(this.serialNumberResult[snNumber][k]
											+ ",");
						}
						snBufferedWriter
								.write(this.serialNumberResult[snNumber][5]
										+ "\r\n");
						snNumber++;
					}
					snBufferedWriter.flush();
					snBufferedWriter.close();
					snList.clear();
					snNameList.clear();
				}
				tmpYear = year;
				bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File(path
										+ "tmp/data" + tmpYear + ".txt")),
								"utf8"));

			}
			for (int j = 0; j < result[i].length - 3; j++) {
				bufferedWriter.write(result[i][j] + ",");
			}
			bufferedWriter.write(result[i][result[i].length - 3] + "\r\n");
		}
		
		
		if (bufferedWriter != null) {
			// just for the last file sn1945.
			snBufferedWriter = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(new File(path
									+ "tmp/sn" + tmpYear + ".txt")),
							"utf8"));
			for (int j = 0; j < snList.size(); j++) {
				snBufferedWriter.write(snList.get(j) + ","
						+ snNameList.get(j)[0] + "," + snNameList.get(j)[1]
						+ ",");
				for (int k = 0; k < 5; k++) {
					snBufferedWriter.write(this.serialNumberResult[snNumber][k]
							+ ",");
				}
				snBufferedWriter.write(this.serialNumberResult[snNumber][5]
						+ "\r\n");
				snNumber++;
			}
			snBufferedWriter.flush();
			snBufferedWriter.close();
			bufferedWriter.flush();
			bufferedWriter.close();
		}
		System.out.println("data reduce success!!!");
		createTableAndImportData();
	}

	public void createTableAndImportData() throws SQLException {

		TyphoonYearService typhoonYearService = new TyphoonYearService();
		List<String> yearList = typhoonYearService.getTyphoonYear();
		List<String> insertYearList = new ArrayList<String>();

		File directory = new File(path + "tmp");
		File[] files = directory.listFiles();
		DataDao dataDao = new DataDao();

		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			if (fileName.contains("s")) {
				String year = fileName.substring(2, 6);
				String sql = SQLStatement.CREATETYPHOONSNTABLE.replace(
						"TABLENAME", year);
				dataDao.excute(sql);
				sql = SQLStatement.INSERTTYPHOONSN.replace("FILENAME",
						"'" + path + "tmp/" + fileName + "'").replace(
						"TABLENAME", year);
				System.out.println(sql);
				dataDao.excute(sql);
			} else if (fileName.contains("data")) {
				String year = fileName.substring(4, 8);

				// table中已经保存了当前年份
				if (yearList.contains(year)) {
				} else {
					insertYearList.add(year);
				}
				String sql = SQLStatement.CREATETYPHOONDATATABLE.replace(
						"TABLENAME", year);
				dataDao.excute(sql);
				sql = SQLStatement.INSERTTYPHOONDATA.replace("FILENAME",
						"'" + path + "tmp/" + fileName + "'").replace(
						"TABLENAME", year);

				System.out.println(sql);
				dataDao.excute(sql);
			}
			// files[i].delete();
		}
		String sqlString = "";
		String year;
		for (int i = 0; i < insertYearList.size(); i++) {
			year = insertYearList.get(i);
			sqlString = SQLStatement.INSERTTYPHOONYEARNUMBER;
			dataDao.insert(sqlString, year);
		}
		dataDao.release();

		System.out.println("over");
	}

	/**
	 * 
	 * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
	 * 
	 * @param file
	 *            读取数据的源Excel
	 * 
	 * @param ignoreRows
	 *            读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
	 * 
	 * @return 读出的Excel中数据的内容
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @throws IOException
	 */

	public static String[][] getData(File file, int ignoreRows)
			throws FileNotFoundException, IOException {
		List<String[]> result = new ArrayList<String[]>();
		int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));

		// 打开HSSFWorkbook
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFCell cell = null;
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet st = wb.getSheetAt(sheetIndex);
			// 第一行为标题，不取
			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
				HSSFRow row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				int tempRowSize = row.getLastCellNum();
				if (tempRowSize > rowSize) {
					rowSize = tempRowSize;
				}
				String[] values = new String[rowSize];
				Arrays.fill(values, "NULL");
				boolean hasValue = false;
				for (short columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
					String value = "NULL";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// 注意：一定要设成这个，否则可能会出现乱码
						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd")
											.format(date);
								} else {
									value = "";
								}
							} else {
								value = new DecimalFormat("0").format(cell
										.getNumericCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							// 导入时如果为公式生成的数据则无值
							if (!cell.getStringCellValue().equals("")) {
								value = cell.getStringCellValue();
							} else {
								value = cell.getNumericCellValue() + "";
							}
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							value = "NULL";
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							value = (cell.getBooleanCellValue() == true ? "Y"
									: "N");
							break;
						default:
							value = "";
						}
					}
					if (columnIndex == 0 && value.trim().equals("")) {
						break;
					}
					if (value.length() > 2)
						value = value.trim();

					if ((columnIndex == 2 || columnIndex == 3)
							&& (value.equals("NULL") || value.trim().equals(""))) {
						hasValue = false;
						break;
					}
					values[columnIndex] = value.equals("") ? "NULL" : value;
					hasValue = true;
				}
				if (hasValue) {
					result.add(values);
				}
			}
		}
		in.close();
		String[][] returnArray = new String[result.size()][rowSize];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (String[]) result.get(i);
		}
		return returnArray;
	}

	/**
	 * 
	 * 去掉字符串右边的空格
	 * 
	 * @param str
	 *            要处理的字符串
	 * 
	 * @return 处理后的字符串
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}
}