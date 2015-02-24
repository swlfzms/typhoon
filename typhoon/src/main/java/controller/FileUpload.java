package controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import excel.ExcelOperate;

@WebServlet(value = "/fileupload")
public class FileUpload extends HttpServlet {

	/**
	 * 
	 */
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
		// TODO Auto-generated method stub
		// super.doPost(req, resp);
		request.setCharacterEncoding("UTF-8");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		System.out.println("here1");

		try {
			List<FileItem> items = servletFileUpload.parseRequest(request);
			Iterator<FileItem> iterator = items.iterator();

			System.out.println("here2");			
			while (iterator.hasNext()) {
				FileItem item = (FileItem) iterator.next();

				if (item.isFormField()) {
					System.out.println("parameter name of the form"
							+ item.getFieldName());
					System.out.println("parameter value of the form"
							+ item.getString("UTF-8"));
				} else {
					if (item.getName() != null && !item.getName().equals("")) {
						System.out.println("name of the file: "
								+ item.getName());
						System.out.println("size of the file: "
								+ item.getSize());
						System.out.println("type of the file: "
								+ item.getContentType());

						File tmpFile = new File(item.getName());
						String path = FileUpload.class.getClassLoader()
								.getResource("").getPath();
						System.out.println(path);
						File tmp = new File(path + "data");
						if(!tmp.exists()){
							tmp.mkdir();
						}
						
						final File file = new File(path + "data/" + tmpFile.getName());
						if (!file.exists()) {
							file.createNewFile();
						}
						item.write(file);											
						
						new Thread(){
							public void run(){																
													
								try {	
									ExcelOperate excelOperate = new ExcelOperate();
									System.out.println("file upload success");
									excelOperate.reduce(file);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}.start();
					}

				}
			}
					
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
