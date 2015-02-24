package service;

import java.sql.SQLException;

import model.SQLStatement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tools.Encryption;

import com.mysql.jdbc.ResultSet;

import dao.DataDao;

public class AdminActionService {

	private DataDao dataDao;

	public AdminActionService() {
		// TODO Auto-generated constructor stub
		dataDao = new DataDao();
	}

	public JSONObject action(JSONObject jsonObject) throws JSONException,
			SQLException {
		JSONObject resultJsonObject = new JSONObject();

		String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		password = Encryption.getMD5ofStr(password, 5);
		System.out.println(password);

		if (type.equals("login")) {
			resultJsonObject = login(username, password);
			release();
			return resultJsonObject;
		} else if (type.equals("register")) {
			resultJsonObject = createUser(username, password);
			release();
			return resultJsonObject;
		} else if (type.equals("update")) {
			resultJsonObject = update(username, password);
			release();
			return resultJsonObject;
		}

		resultJsonObject.put("result", false);
		release();
		return resultJsonObject;
	}

	public JSONObject login(String username, String password)
			throws JSONException, SQLException {

		String sql = SQLStatement.ADMINLOGIN;
		ResultSet resultSet = (ResultSet) dataDao
				.query(sql, username, password);

		JSONObject resultJsonObject = new JSONObject();
		if (resultSet.next()) {
			resultJsonObject.put("result", true);
			resultSet.close();
			return resultJsonObject;
		}
		resultSet.close();
		resultJsonObject.put("result", false);
		return resultJsonObject;
	}

	public JSONObject createUser(String username, String password)
			throws JSONException, SQLException {

		String sql = SQLStatement.CREATEADMIN;		
		JSONObject resultJsonObject = login(username, password);
		
		boolean result = resultJsonObject.getBoolean("result");
		if(result){
			resultJsonObject.put("result", false);
			return resultJsonObject;
		}else{	
			int count = dataDao.insert(sql, username, password);
			if (count == 1) {
				resultJsonObject.put("result", true);
				return resultJsonObject;
			}
		}		
		resultJsonObject.put("result", false);
		return resultJsonObject;
	}

	public JSONObject update(String username, String password) throws SQLException, JSONException {
		String sql = SQLStatement.UPDATEADMIN;
		int count = dataDao.update(sql, password, username);
		
		JSONObject resultJsonObject = new JSONObject();
		System.out.println(count);
		if (count == 1) {
			resultJsonObject.put("result", true);
			return resultJsonObject;
		}		
		resultJsonObject.put("result", false);
		return resultJsonObject;		
	}

	public void release() throws SQLException {
		dataDao.release();
	}
}
