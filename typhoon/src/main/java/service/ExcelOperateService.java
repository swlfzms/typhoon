package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import map.Point;
import model.SQLStatement;

import com.mysql.jdbc.ResultSet;

import dao.DataDao;

public class ExcelOperateService {

	private DataDao dataDao;
	private List<String> tableNameList;

	public ExcelOperateService() {
		// TODO Auto-generated constructor stub
		this.dataDao = new DataDao();
	}

	public Map<String, List<Point>> getData() throws SQLException {
		List<String> tableName = getTableName();
		List<Integer> snName = getSNByTableName(tableName);
		Map<String, List<Point>> result = getFullDataBySN(snName);
		
		dataDao.release();
		return result;
	}

	/***
	 * 根据序列号列表获取序列号对应数据
	 * 
	 * @param snList
	 * @return
	 * @throws SQLException
	 */
	public Map<String, List<Point>> getFullDataBySN(List<Integer> snList)
			throws SQLException {
		Map<String, List<Point>> map = new LinkedHashMap<String, List<Point>>();

		for (int i = 0; i < snList.size(); i++) {
			String serialNumber = snList.get(i).toString();
			String year = serialNumber.substring(0, 4);

			String sql = SQLStatement.GETTYPHOONDATABYSN;
			sql = sql.replace("TABLENAME", year);

			ResultSet resultSet = (ResultSet) dataDao.query(sql, snList.get(i));
			List<Point> list = new ArrayList<Point>();
			double preLng = -1;
			double preLat = -1;
			while (resultSet.next()) {
				double lng = resultSet.getDouble("lng");
				double lat = resultSet.getDouble("lat");
				if (lng == preLng && lat == preLat) {
					continue;
				} else {
					Point point = new Point(lng, lat);
					list.add(point);
					preLat = lat;
					preLng = lng;
				}
			}
			resultSet.close();
			map.put(serialNumber, list);
		}
		return map;
	}

	public List<Integer> getSNByTableName(List<String> tableNameList)
			throws SQLException {

		List<Integer> snList = new ArrayList<Integer>();
		for (int i = 0; i < tableNameList.size(); i++) {
			String tableName = tableNameList.get(i);
			if (!tableName.contains("sn")) {
				continue;
			}

			String sql = SQLStatement.GETSNBYTABLENAME;
			sql = sql.replace("TABLENAME", tableName);

			ResultSet rs = (ResultSet) dataDao.query(sql);
			while (rs.next()) {
				snList.add(rs.getInt("serialNumber"));
			}
			rs.close();
		}

		return snList;
	}

	public List<String> getTableName() throws SQLException {
		String sql = SQLStatement.GETTYPHOONTABLENAME;
		ResultSet resultSet = (ResultSet) dataDao.query(sql);
		tableNameList = new ArrayList<String>();

		while (resultSet.next()) {
			String tableName = resultSet.getString(1);
			if (tableName.startsWith("sn")) {
				tableNameList.add(tableName);
			}
		}
		resultSet.close();
		return tableNameList;
	}

	public void release() throws SQLException {
		dataDao.release();
	}
}
