package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import service.DataInitializationService;

@WebServlet(value = "/datainitialization")
public class DataInitialization extends HttpServlet {

	/**
	 * 
	 */
	private DataInitializationService dataInitializationService = new DataInitializationService();
	private JSONArray jsonArray = new JSONArray();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");			

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		try {
			jsonArray = dataInitializationService.getData();
			PrintWriter printWriter = response.getWriter();
			printWriter.println(jsonArray);
			printWriter.flush();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
