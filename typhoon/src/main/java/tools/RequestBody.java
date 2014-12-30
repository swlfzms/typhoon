package tools;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class RequestBody {

	public static String readJsonFromRequestBody(HttpServletRequest req) {
		StringBuffer jsonBuf = new StringBuffer();
		char[] buf = new char[2048];
		int len = -1;
		try {
			BufferedReader reader = req.getReader();
			while ((len = reader.read(buf)) != -1) {
				jsonBuf.append(new String(buf, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonBuf.toString();
	}
}
