package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.SQLStatement;
import dao.DataDao;

public class TyphoonYearService {

	private DataDao dataDao;

	public TyphoonYearService() {
		dataDao = new DataDao();
	}

	public List<String> getTyphoonYear() throws SQLException {
		String sql = SQLStatement.GETTYPHOONYEAR;
		ResultSet rs = dataDao.query(sql);

		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			list.add("" + rs.getInt("year"));
		}

		release();
		return list;
	}

	public void release() throws SQLException {
		dataDao.release();
	}
}
