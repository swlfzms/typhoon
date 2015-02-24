package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.PreparedStatement;

import database.ConnectionControl;
import database.ConnectionPool;

public class DataDao implements ConnectionControl {
	
	private Logger logger = Logger.getLogger(DataDao.class.getName());
	private Connection connection;
	private ConnectionPool connectionPool;
	private PreparedStatement preparedStatement;
	private ResultSet rs;
	private int count;
	
	public DataDao() {
		// TODO Auto-generated constructor stub
		connectionPool = ConnectionPool.getInstance();		
		connection = (Connection) connectionPool.getConnection();		
	}
	
	public boolean excute(String sql) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);		
		return preparedStatement.execute();
	}
	
	public int insert(String sql) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		count = preparedStatement.executeUpdate();
		return count;
	}
	
	public int insert(String sql, String year) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setString(1, year);
		count = preparedStatement.executeUpdate();
		return count;
	}
	public int insert(String sql, String arg1, String arg2) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setString(1, arg1);
		preparedStatement.setString(2, arg2);
		count = preparedStatement.executeUpdate();
		return count;
	}
	
	public ResultSet query(String sql) throws SQLException {
		logger.log(Level.INFO, "method: query, " + sql);
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);		
		rs = preparedStatement.executeQuery();
		return rs;
	}
	
	public ResultSet query(String sql,String arg1) throws SQLException {
		logger.log(Level.INFO, "method: query, " + sql);
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setString(1, arg1);
		rs = preparedStatement.executeQuery();
		return rs;
	}
	
	public ResultSet query(String sql,String arg1, String arg2) throws SQLException {
		logger.log(Level.INFO, "method: query, " + sql);
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setString(1, arg1);
		preparedStatement.setString(2, arg2);
		rs = preparedStatement.executeQuery();
		return rs;
	}
	
	public ResultSet query(String sql,int arg1) throws SQLException {
		logger.log(Level.INFO, "method: query, " + sql);
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setInt(1, arg1);
		rs = preparedStatement.executeQuery();
		return rs;
	}
	
	public int update(String sql) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		count = preparedStatement.executeUpdate();
		return count;
	}
	public int update(String sql,String arg1,String arg2) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		preparedStatement.setString(1, arg1);
		preparedStatement.setString(2, arg2);
		count = preparedStatement.executeUpdate();
		return count;
	}
	
	public int getCount(String sql) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		rs = preparedStatement.executeQuery();
		while (rs.next()) {
			count++;
		}
		return count;
	}
	
	public int delete(String sql) throws SQLException {
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		count = preparedStatement.executeUpdate();
		return count;
	}
	
	@Override
	public void release() throws SQLException {
		// TODO Auto-generated method stub
		if (rs != null) {
			rs.close();
		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		connectionPool.release(connection);
	}
	
}
