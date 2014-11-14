package edu.uwm.cs361;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.*;

import edu.uwm.cs361.DemeritDatastoreService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class ProjectServlet extends HttpServlet {
	
	//constructor
	public ProjectServlet(){};

	DemeritDatastoreService data = new DemeritDatastoreService();
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		//form to show the names of the staff in the system for now
		String http = "";
		http += "<form id=\"ccf\">"
		+			"<div id=\"title-create-staff\">"
		+				"Staff List"
		+			"</div>"
		+ 			"<div id=\"sub\">";
		
		Query q = new Query(data.STAFF);

		DatastoreService ds = data.getDatastore();
		
		List<Entity> users = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
		http += "Ther are " + users.size() + " users.<br><br>";
		for(Entity user:users){
			http += "Name: " + user.getProperty(data.NAME) + "<br>";
			//ds.delete(user.getKey());
		}
		http += "</div>"
		+		"</form>";
		banner(req,resp);
		layout(http,req,resp);
		menu(req,resp);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	}
	
	
	/*
	 * to create a banner for the page and include the CSS file
	 */
	public void banner(HttpServletRequest req, HttpServletResponse resp)throws IOException{
		resp.setContentType("text/html");
		
		resp.getWriter().println("<head>"
		+							"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
		+							"<title>UW Milwaukee</title>"
		+							"<link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\"/>"
		+							"<style type=\"text/css\"></style>"
		+						"</head>");
		
		resp.getWriter().println("<div class=\"banner\">"
		+							"<a class=\"plbrand mainlogo-link\" href=\"Admin_Home_Page.html\" title=\"UW-Milwaukee D2L\">"
		+								"<img class=\"mainlogo-img\" src=\"Images/UWM_D2L_banner_960w1.png\" alt=\"UW-Milwaukee D2L\">"
		+							"</a>"
		+						"</div>");
	}
	
	/*
	 * takes a string which will create the Contents page
	 */
	public void layout(String http, HttpServletRequest req, HttpServletResponse resp)throws IOException{
		resp.setContentType("text/html");
		
		resp.getWriter().println("<div class=\"layout background-style\">"
		+							"	<div class=\"page-after-banner\">"
		+									http
		+							"	</div>"
		+							"</div>");
	}
	
	/*
	 * takes a string which will create the Contents page
	 */
	public void courseListLayout(String http, HttpServletRequest req, HttpServletResponse resp)throws IOException{
		resp.setContentType("text/html");
		
		resp.getWriter().println("<div class=\"courselayout background-style\">"
		+							"	<div class=\"page-after-banner\">"
		+									http
		+							"	</div>"
		+							"</div>");
	}
	
	/*
	 * menu methods create the menu on side of the page
	 */
	public void menu(HttpServletRequest req, HttpServletResponse resp)throws IOException{
		resp.setContentType("text/html");
		resp.getWriter().println("<div class=\"menu\">");					
		resp.getWriter().println("	<div class=\"buttons\">");
		resp.getWriter().println("		<ul class=\"buttons-outline\">");
		resp.getWriter().println("			<li> <a href=\"/project\"> Home</a></li>");
		resp.getWriter().println("		</ul>");
		resp.getWriter().println("		<ul class=\"buttons-outline\">");
		resp.getWriter().println("			<li> <a href=\"/scrape\"> Scrape</a></li>");
		resp.getWriter().println("		</ul>");
//		resp.getWriter().println("		<ul class=\"buttons-outline\">");
//		resp.getWriter().println("			<li id=\"b1\"> <a href=\"#\">Admin</a>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/project\"> Admin Query</a></li>");
//		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/project\"> Staff Contacts</a></li>");
//		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("			</li>");
//		resp.getWriter().println("		</ul>");
		resp.getWriter().println("		<ul class=\"buttons-outline\">");
		resp.getWriter().println("			<li> <a href=\"#\">Staff</a>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href=\"/createStaff\"> Create Staff</a></li>");
		resp.getWriter().println("				</ul>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href=\"/editStaff\"> Edit Staff</a></li>");
		resp.getWriter().println("				</ul>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href=\"/viewStaff\"> View Staff</a></li>");
		resp.getWriter().println("				</ul>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href='/editStaffContact'> Edit Staff Contact</a></li>");
		resp.getWriter().println("				</ul>");
		resp.getWriter().println("			</li>");
		resp.getWriter().println("		</ul>");
//		resp.getWriter().println("		<ul class=\"buttons-outline\">");
//		resp.getWriter().println("			<li> <a href=\"#\">Instructor</a>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/project\"> TA's List </a></li>");
//		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/project\"> Assign TA's</a></li>");
//		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("			</li>");
//		resp.getWriter().println("		</ul>");
		resp.getWriter().println("		<ul class=\"buttons-outline\">");
		resp.getWriter().println("			<li> <a href=\"#\"> Course</a>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/createCourse\"> Create Course</a></li>");
//		resp.getWriter().println("				</ul>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href=\"/courseList\">Course List</a></li>");
		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href=\"/project\"> Edit Course</a></li>");
//		resp.getWriter().println("				</ul>");
		resp.getWriter().println("				<ul class=\"buttons-outline\">");
		resp.getWriter().println("					<li><a href=\"/editSection\"> Edit Section</a></li>");
		resp.getWriter().println("				</ul>");
		resp.getWriter().println("			</li>");
		resp.getWriter().println("		</ul>");
//		resp.getWriter().println("		<ul class=\"buttons-outline\">");
//		resp.getWriter().println("			<li> <a href=\"/project\"> My Office Hours</a></li>");
//		resp.getWriter().println("		</ul>");
//		resp.getWriter().println("		<ul class=\"buttons-outline\">");
//		resp.getWriter().println("			<li> <a href=\"#\"> My Contact</a>");
//		resp.getWriter().println("				<ul class=\"buttons-outline\">");
//		resp.getWriter().println("					<li><a href='/myContact'> Contact</a></li>");
//		resp.getWriter().println("				</ul>");
//		resp.getWriter().println("			</li>");
//		resp.getWriter().println("		</ul>");
		resp.getWriter().println("		<ul class=\"buttons-outline\">");
		resp.getWriter().println("			<li> <a href=\"index.html\">Logout</a></li>");
		resp.getWriter().println("		</ul>");
		resp.getWriter().println("	</div>");
		resp.getWriter().println("</div>");

	}
}
