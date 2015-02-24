package service;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.SQLStatement;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mysql.jdbc.ResultSet;

import dao.DataDao;

public class DataInitializationService {

	private TyphoonYearService typhoonYearService;
	private List<String> yearList = new ArrayList<String>();

	private DataDao dataDao;

	public DataInitializationService() {
		// TODO Auto-generated constructor stub
		typhoonYearService = new TyphoonYearService();
		dataDao = new DataDao();
	}

	public JSONArray getData() throws SQLException, JSONException {
		// 列表中的年份
		this.yearList = typhoonYearService.getTyphoonYear();
		JSONArray jsonArray = new JSONArray();

		for (int i = 0; i < this.yearList.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			JSONArray yearListObject = new JSONArray();
			yearListObject = getDataByYear(this.yearList.get(i));
			jsonObject.put("year", this.yearList.get(i));
			jsonObject.put("data", yearListObject);
			jsonArray.put(jsonObject);
		}
		
		release();
		return jsonArray;
	}

	public JSONArray getDataByYear(String year) throws SQLException, JSONException {
		String sql = SQLStatement.GETTYPHOONDATABYYEAR;
		sql = sql.replace("TABLENAME", year);
		ResultSet rs = (ResultSet) dataDao.query(sql);
		JSONArray jsonArray = new JSONArray();
		
		while (rs.next()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("serialNumber", rs.getString("serialNumber"));
			jsonObject.put("name_zh", rs.getString("name_zh"));
			jsonObject.put("name_en", rs.getString("name_en"));
			jsonArray.put(jsonObject);
		}
		
		rs.close();		
		return jsonArray;
	}

	public void release() throws SQLException {
		dataDao.release();
	}
}
