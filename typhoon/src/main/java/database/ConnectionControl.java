package database;

import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public interface ConnectionControl {
	
	public void release() throws SQLException;
}
