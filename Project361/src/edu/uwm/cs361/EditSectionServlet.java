package edu.uwm.cs361;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class EditSectionServlet extends HttpServlet {

	private String courseKey = null;

	ProjectServlet page = new ProjectServlet();
	DemeritDatastoreService data = new DemeritDatastoreService();
	DatastoreService ds = data.getDatastore();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Query q = new Query(data.COURSE);
		List<Entity> courses = ds.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		String http = "";

		if (courseKey == null)
			courseKey = "";

		String sectionKey = "";
		sectionKey = req.getParameter("sectionKey");
		if (sectionKey == null) {
			page.banner(req, resp);
			http += "<form id=\"ccf\" method=\"GET\" action=\"/editSection\">"
					+ "<div id=\"title-create-staff\">"
					+ "Edit Section"
					+ "</div>"
					+ "<div id=\"sub\">"
					+ "<table>"
					+ "<tr>"
					+ "<td class='form'>"
					+ "Courses:"
					+ "<select id='staff' name='sectionKey' class='staff-select'>";
			for (Entity course : courses) {
				courseKey = data.getOurKey(course.getKey());
				http += "<option disabled>COMPSCI " + courseKey + "</option>";
				String list = course.getProperty(data.SECTION_LIST).toString();
				String[] listArray = data.makeDelStringToArray(list);

				for (String i : listArray) {
					Entity section = null;
					try {
						i = i.replaceAll("~", "");
						section = data.getSection(courseKey + " " + i);
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
					http += "<option>--"
							+ courseKey + " - "
							+ section.getProperty(data.TYPE).toString() + " "
							+ i + "</option>";
				}

			}

			http += "</select><br><br>" + "</td>" + "</tr>";
			http += "</table>"
					+ "<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
					+ "</div>" + "</form>";
			page.layout(http, req, resp);
			page.menu(req, resp);
		} else {
			page.banner(req, resp);
			page.layout(
					displayForm(req, resp, new ArrayList<String>(),sectionKey), req, resp);
			page.menu(req, resp);
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String staff = req.getParameter("staff");
		String mainKey = req.getParameter("mainKey");
		//System.out.println("staff: " + staff + "KEy: " + mainKey);
		
		Entity sectionEn = null;
		try {
			sectionEn = data.getSection(mainKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String courseNumber = mainKey.substring(0, 3);
		String days = sectionEn.getProperty(data.DAYS).toString();
		String lecLabDis = sectionEn.getProperty(data.TYPE).toString();
		String sectionNumber = mainKey.substring(4);
		String room = sectionEn.getProperty(data.ROOM).toString();
		String time = sectionEn.getProperty(data.TIME).toString();
		
		List<String> errors = new ArrayList<String>();

		if (staff == null)
			staff = "";

		if (errors.size() > 0) {
			page.banner(req, resp);
			page.layout(displayForm(req, resp, errors, staff), req, resp);
			page.menu(req, resp);
		} else {

			Entity e = null;

			try {

				data.updateSection(courseNumber, days, lecLabDis, sectionNumber, room, staff, time);
				e = data.getSection(mainKey);
				//System.out.println(e.toString());

			} catch (EntityNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

			String http = "";

			http += "<form id=\"ccf\" method=\"GET\" action=\"/editSection\">"
					+			"<div id=\"title-create-staff\">"
					+				"Edit Course Section Conformation"
					+			"</div>"
					+ 			"<div id=\"sub\">"
					+ 				"New section instructor: "
					+ 					e.getProperty(data.STAFF).toString()
					+ 					"<br>"
					+ 					"This section has been updated.<br><br><br><br><br><br>"
					+				"<input class=\"submit\" type=\"submit\" value=\"Back\" />"
					+			"</div>"
					+		"</form>";
			page.banner(req, resp);
			page.layout(http, req, resp);
			page.menu(req, resp);
		}
	}

	private String displayForm(HttpServletRequest req,
			HttpServletResponse resp, List<String> errors, String mainKey)
			throws IOException {

		//System.out.println(mainKey);

		String[] temp = mainKey.split(" - ");
		String[] temp2 = temp[1].split(" ");
		mainKey = temp[0] + " " + temp2[1];
		mainKey = mainKey.substring(2);

		//System.out.println(mainKey);


		resp.setContentType("text/html");
		String http = "";
		String courseNum = mainKey.substring(0, 3);
		String sectionNum = mainKey.substring(5);

		Entity sectionEn = null;
		try {
			sectionEn = data.getSection(mainKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String type = sectionEn.getProperty(data.TYPE).toString();

		http += "<form id=\"ccf\" method=\"POST\" action=\"/editSection\">"
				+ "<div id=\"title-create-staff\">" + "CompSci " + courseNum
				+ " - " + type + " " + sectionNum + "<br>" + "</div>";

		if (errors.size() > 0) {
			http += "<ul class='errors'>";

			for (String error : errors) {
				http += "  <li>" + error + "</li>";
			}

			http += "</ul>";
		}

		Query q = new Query(data.STAFF);
		List<Entity> users = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
		
		http += "<div id=\"sub\">" + "<table>" + "<tr>"
				+ "<td class=\"form\" >" + "Staff:"
				+ "<select id='staff' name='staff' class='staff-select'>";
		http += "<option disabled>Instructor's</option>";
		for (Entity user : users) {
			if (!user.getProperty(data.TYPE).equals("TA"))
				http += "<option>" + data.getOurKey(user.getKey())
						+ "</option>";

		}
		http += "<option disabled>TA's</option>";
		for (Entity user : users) {
			if (user.getProperty(data.TYPE).equals("TA"))
				http += "<option>" + data.getOurKey(user.getKey())
						+ "</option>";
		}
		http += "<input class='createStaffInput' type=\"hidden\" id='staff' name='mainKey' value='" + mainKey + "'/><br>";
		http += "</select><br><br>" + "</td>" + "</tr>" + "</table>"
				+ "<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
				+ "</div>" + "</form>";
		
		
		return http;

	}

}