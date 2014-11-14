package edu.uwm.cs361;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.api.server.spi.auth.common.User;
import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import edu.uwm.cs361.ProjectServlet;
import edu.uwm.cs361.DemeritDatastoreService;;

@SuppressWarnings("serial")
public class CreateCourseServlet extends HttpServlet{
	/*
	 * Create a variable to call project servlet methods for HTTP
	 * create insistence of datastore service 
	 */
	ProjectServlet page = new ProjectServlet();
	DemeritDatastoreService data = new DemeritDatastoreService();
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		page.banner(req,resp);
		page.layout(displayForm(req,resp,new ArrayList<String>()),req,resp);
		page.menu(req,resp);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// get all the parameter from the form
		String courseDesig = req.getParameter("designation");
		String title = req.getParameter("title");
		String units = req.getParameter("units");
		String sectionDesig = req.getParameter("sectionDesig");
		String hours = req.getParameter("hours");
		String days = req.getParameter("days");
		String dates = req.getParameter("dates");
		String instructor = req.getParameter("instructor");
		String room = req.getParameter("room");

		List<String> errors = new ArrayList<String>();
		
		DatastoreService dsNew =  data.getDatastore();
		
		Query q = new Query(data.COURSE);
		
		//get all the staff and match the username if its exist 
		List<Entity> courseList = dsNew.prepare(q).asList(FetchOptions.Builder.withDefaults());
		q = new Query(data.SECTION);
		//get all the staff and match the username if its exist 
		List<Entity> sectionList = dsNew.prepare(q).asList(FetchOptions.Builder.withDefaults());
		
		boolean courseExist = true;
		boolean sectionExist = true;
		for(Entity course:courseList){
			if(!courseDesig.isEmpty() && data.getOurKey(course.getKey()) != null){
				if(data.getOurKey(course.getKey()).equals(courseDesig)){
					errors.add("User '"+ courseDesig +"' Already Exist.");
					courseExist = false;
				}
			}
		}
		for(Entity section:sectionList){
			if(!sectionDesig.isEmpty() && data.getOurKey(section.getKey()) != null){
				if(data.getOurKey(section.getKey()).equals(sectionDesig)){
					errors.add("User '"+ sectionDesig +"' Already Exist.");
					sectionExist = false;
				}
			}
		}
		//checking for blanks
		if(courseExist || sectionExist){
			if (title.isEmpty()) {
				errors.add("Title is required.");
			}
			if (units.isEmpty()) {
				errors.add("Units is required.");
			} 
			if (hours.isEmpty()) {
				errors.add("Hours is required.");
			} 
			if (days.isEmpty()) {
				errors.add("Days is required.");
			} 
			if (dates.isEmpty()) {
				errors.add("Dates is required.");
			} 
			if (instructor.isEmpty()) {
				errors.add("Instructor is required.");
			} 
			if (room.isEmpty()) {
				errors.add("Room is required.");
			} 
		}
		//if there is any error then print the form again
		if (errors.size() > 0) {
			page.banner(req,resp);
			page.layout(displayForm(req,resp,errors),req,resp);
			page.menu(req,resp);
		} else {	
			try {
				String[] temp = sectionDesig.split(" ");
				
				Section s = new Section(units, temp[0], hours, days, dates, instructor, room);
				ArrayList<Section> listSec = new ArrayList<Section>();
				listSec.add(s);
				Course c = new Course(courseDesig, title, listSec);
				data.createCourse(c);
				//data.createCourse(title, courseDesig, temp[1], units, days, temp[0], room, instructor, hours);
			} catch (EntityNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			String http = "";
			//Staff created confirmation page.

			String[] temp = sectionDesig.split(" ");
			http += "<form id=\"ccf\" method=\"GET\" action=\"/createCourse\">"
			+			"<div id=\"title-create-staff\">"
			+				"Course Created Conformation"
			+			"</div>"
			+ 			"<div id=\"sub\">"
			+				"Course Number : " + courseDesig + "<br>" 
			+				"Title : " + title + "<br>" 
			+				"Units : " + units + "<br><br>" 
			+				"Section Type: " + temp[1] + "<br>" 
			+				"Section Number: " + temp[0] + "<br>" 
			+				"Meeting Time : " + hours + "<br>" 
			+				"Meeting Days : " + days + "<br>" 
			+				"Dates : " + dates + "<br><br>" 
			+				"Instructor : " + instructor + "<br>" 
			+				"Room : " + room + "<br>"
			+				"The course has been Created.<br><br><br><br><br><br>"
			+				"<input class=\"submit\" type=\"submit\" value=\"Back\" />"
			+			"</div>"
			+		"</form>";
			page.banner(req,resp);
			page.layout(http,req,resp);
			page.menu(req,resp);
		}
	}
	
	/*
	 * display form will get a list for errors 
	 * print the form with errors.
	 */
	private String displayForm(HttpServletRequest req, HttpServletResponse resp, List<String> errors) throws IOException
	{
		resp.setContentType("text/html");
		String http = "";
		
		http += "<form id=\"ccf\" method=\"POST\" action=\"/createCourse\">"
		+			"<div id=\"title-create-staff\">"
		+				"Create Course"
		+			"</div>";
		
		String courseDesig = req.getParameter("designation") != null ? req.getParameter("designation") : "";
		String title = req.getParameter("title") != null ? req.getParameter("title") : "";
		String units = req.getParameter("units") != null ? req.getParameter("units") : "";
		String sectionDesig = req.getParameter("sectionDesig") != null ? req.getParameter("sectionDesig") : "";
		String hours = req.getParameter("hours") != null ? req.getParameter("hours") : "";
		String days = req.getParameter("days") != null ? req.getParameter("days") : "";
		String dates = req.getParameter("dates") != null ? req.getParameter("dates") : "";
		String instructor = req.getParameter("instructor") != null ? req.getParameter("instructor") : "";
		String room = req.getParameter("room") != null ? req.getParameter("room") : "";
		
		if (errors.size() > 0) {
			http += "<ul class='errors'>";

			for (String error : errors) {
				http +="  <li>" + error + "</li>";
			}

			http += "</ul>";
		}

		http += 	"<div id=\"sub\">"
		+				"<table>"
		+					"<tr>"
		+						"<td class=\"form\">"
		+							"Course Number : <input class='createStaffInput' type=\"text\" id='designation' name='designation' value='" + courseDesig + "'/><br>"
		+							"Title : <input class='createStaffInput' type=\"text\" id='title' name='title' value='" + title + "'/><br>"
		+							"Units : <input class='createStaffInput' type=\"text\" id='units' name='units' value='" + units + "'/><br>"
		+							"Section Type: <input class='createStaffInput' type=\"text\" id='sectionDesig' name='sectionDesig' value='" + sectionDesig + "'/><br>"
		+							"Meeting Time : <input class='createStaffInput' type=\"text\" id='hours' name='hours' value='" + hours + "'/><br>"
		+							"Meeting Days : <input class='createStaffInput' type=\"text\" id='days' name='days' value='" + days + "'/><br>"						
		+							"Dates : <input class='createStaffInput' type=\"text\" id='dates' name='dates' value='" + dates + "'/><br>"
		+							"Instructor : <input class='createStaffInput' type=\"text\" id='instructor' name='instructor' value='" + instructor + "'/><br>"
		+							"Room : <input class='createStaffInput' type=\"text\" id='room' name='room' value='" + room + "'/><br>"
		+						"</td>"
		+					"</tr>"
		+				"</table>"
		+				"<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
		+			"</div>"
		+		"</form>";
		
		return http;
	}

}