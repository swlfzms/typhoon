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
import org.codehaus.jettison.json.JSONObject;

import service.SimilarRouteService;
import tools.RequestBody;

@WebServlet(value = "/similarroute")
public class SimilarRoute extends HttpServlet {

	private SimilarRouteService similarRouteService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doPost(req, resp);

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String jsonStr = RequestBody.readJsonFromRequestBody(request);
		try {
			JSONObject object = new JSONObject(jsonStr);
			this.similarRouteService = new SimilarRouteService();

			JSONArray jsonArray = this.similarRouteService
					.getSimilarRouteBySerialNumber(object);

			PrintWriter printWriter = response.getWriter();
			printWriter.println(jsonArray);
			printWriter.flush();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// JSONArray jsonArray = getSimilarRouteBySerialNumber(object);
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
