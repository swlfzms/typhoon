package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tools.RequestBody;

@WebServlet(value = "/datareduce")
public class DataReduce extends HttpServlet {

	private JSONArray getDataByNum(String num) throws IOException,
			JSONException {
		String path = DataReduce.class.getClassLoader().getResource("")
				.getPath().toString();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(path
						+ "/data/" + num + ".txt")), "UTF-8"));
		String line;
		JSONArray jsonArray = new JSONArray();
		int count = 1;

		while ((line = bufferedReader.readLine()) != null) {
			if (count == 1) {
				count = 0;
				continue;
			}
			String[] array = line.split("\t");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("lat", array[0]);
			jsonObject.put("lng", array[1]);
			jsonObject.put("pressure", array.length > 2 ? array[2] : 0);
			jsonObject.put("center_speed", array.length > 3 ? array[3] : 0);
			jsonObject.put("move_speed", array.length > 4 ? array[4] : 0);
			jsonObject.put("direction", array.length > 5 ? array[5] : 0);
			jsonObject.put("seven_solar_halo", array.length > 6 ? array[6] : 0);
			jsonObject.put("ten_solar_halo", array.length > 7 ? array[7] : 0);

			if (array.length == 9) {
				jsonObject.put("DEPICT", array[8]);
			}
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		String jsonStr = RequestBody.readJsonFromRequestBody(request);
		JSONObject object;
		try {
			object = new JSONObject(jsonStr);
			String num = object.getString("num");
			System.out.println(num);
			JSONArray jsonArray = getDataByNum(num);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter printWriter = response.getWriter();
			printWriter.println(jsonArray);
			printWriter.flush();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
