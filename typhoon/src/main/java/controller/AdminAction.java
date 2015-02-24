package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;

import service.AdminActionService;
import tools.RequestBody;

@WebServlet(value = "/adminaction")
public class AdminAction extends HttpServlet{
	
	private AdminActionService adminActionService;
		
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		String jsonStr = RequestBody.readJsonFromRequestBody(request);
		JSONObject object = null;
		try {
			object = new JSONObject(jsonStr);
			adminActionService = new AdminActionService();
			JSONObject result = adminActionService.action(object);
			
			PrintWriter printWriter = response.getWriter();
			printWriter.println(result);
			printWriter.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);					
	}
}
