package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tools.RequestBody;

@WebServlet(value = "/test")
public class Test extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doPost(req, resp);
		request.setCharacterEncoding("UTF-8");
		String jsonStr = RequestBody.readJsonFromRequestBody(request);
		try {
			JSONObject object = new JSONObject(jsonStr);
			System.out.println(object.get("result"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter printWriter = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("data", "helloworld");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printWriter.println(jsonObject);
		printWriter.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
}
