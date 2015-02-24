package service;

import java.sql.SQLException;

import model.SQLStatement;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mysql.jdbc.ResultSet;

import dao.DataDao;

public class DataRouteService {
	private DataDao dataDao;

	public DataRouteService() {
		// TODO Auto-generated constructor stub
		dataDao = new DataDao();
	}

	public JSONArray getDataBySerialNumber(JSONObject jsonObject)
			throws JSONException, SQLException {
		String serialNumber = jsonObject.getString("serialNumber");
		String year = serialNumber.substring(0, 4);
		String sql = SQLStatement.GETTYPHOONDATABYSN;
		sql = sql.replace("TABLENAME", year);

		JSONArray jsonArray = new JSONArray();		
		
		ResultSet resultSet = (ResultSet) dataDao.query(sql, Integer.parseInt(serialNumber));
		while(resultSet.next()){
			
			String time = resultSet.getString("time");
			double lng = resultSet.getDouble("lng");
			double lat = resultSet.getDouble("lat");
						
			JSONObject pointObject = new JSONObject();
			pointObject.put("time", time);
			pointObject.put("lng", lng);
			pointObject.put("lat", lat);
			
			jsonArray.put(pointObject);
		}
		
		resultSet.close();
		release();
		return jsonArray;
	}

	public void release() throws SQLException {
		dataDao.release();
	}
}
