package service;

import java.sql.SQLException;
import java.util.logging.Logger;

import model.SQLStatement;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mysql.jdbc.ResultSet;

import dao.DataDao;

public class RouteDataInformationService {

	private DataDao dataDao;
	
	public RouteDataInformationService() {
		// TODO Auto-generated constructor stub
		dataDao = new DataDao();		
	}
	
	public JSONArray getFullDataBySN(JSONObject jsonObject) throws JSONException, SQLException {
		
		String serialNumber = jsonObject.getString("serialNumber");
		System.out.println(serialNumber);
		String year = serialNumber.substring(0, 4);
		String sql = SQLStatement.GETTYPHOONFULLDATABYSN;
		sql = sql.replace("TABLENAME", year);
				
		JSONArray jsonArray = new JSONArray();		
		
		ResultSet resultSet = (ResultSet) dataDao.query(sql, Integer.parseInt(serialNumber));
		while(resultSet.next()){
								
			String time = resultSet.getString("time");
			double lng = resultSet.getDouble("lng");
			double lat = resultSet.getDouble("lat");
			double pressure = resultSet.getDouble("pressure");
			double centerSpeed = resultSet.getDouble("centerSpeed");
			double moveSpeed = resultSet.getDouble("moveSpeed");
			String direction = resultSet.getString("direction");
			double sevenSolarHalo = resultSet.getDouble("sevenSolarHalo");
			double tenSolarHalo = resultSet.getDouble("tenSolarHalo");
			
						
			JSONObject pointObject = new JSONObject();
			pointObject.put("serialNumber", serialNumber);			
			pointObject.put("time", time);
			pointObject.put("lng", lng);
			pointObject.put("lat", lat);
			pointObject.put("pressure", pressure);
			pointObject.put("centerSpeed", centerSpeed);
			pointObject.put("moveSpeed", moveSpeed);
			pointObject.put("direction", direction);
			pointObject.put("sevenSolarHalo", sevenSolarHalo);
			pointObject.put("tenSolarHalo", tenSolarHalo);
			
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
