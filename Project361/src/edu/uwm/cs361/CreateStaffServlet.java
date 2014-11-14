package edu.uwm.cs361;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import edu.uwm.cs361.ProjectServlet;
import edu.uwm.cs361.DemeritDatastoreService;;

@SuppressWarnings("serial")
public class CreateStaffServlet extends HttpServlet{
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
		String username = req.getParameter("username");
		username = username.toLowerCase();
		String password = req.getParameter("password");
		String firstname = req.getParameter("firstname");
		String lastname = req.getParameter("lastname");
		String telephone = req.getParameter("telephone");
		String stafftype = req.getParameter("stafftype");

		List<String> errors = new ArrayList<String>();
		
		DatastoreService dsNew =  data.getDatastore();
		
		Query q = new Query(data.STAFF);
		
		//get all the staff and match the username if its exist 
		List<Entity> users = dsNew.prepare(q).asList(FetchOptions.Builder.withDefaults());
		boolean exist = true;
		for(Entity user:users){
			if(!username.isEmpty() && data.getOurKey(user.getKey()) != null){
				if(data.getOurKey(user.getKey()).equals(username)){
					errors.add("User '"+ username +"' Already Exist.");
					exist = false;
				}
			}
		}
		//checking for blanks
		if(exist){
			if (username.isEmpty()) {
				errors.add("Username is required.");
			}
			if (password.isEmpty()) {
				errors.add("Password is required.");
			} 
			if (firstname.isEmpty()) {
				errors.add("First is required.");
			} 
			if (lastname.isEmpty()) {
				errors.add("Lastname is required.");
			} 
			if (stafftype.isEmpty()) {
				errors.add("Staff Type is required.");
			} 
		}
		//if there is any error then print the form again
		if (errors.size() > 0) {
			page.banner(req,resp);
			page.layout(displayForm(req,resp,errors),req,resp);
			page.menu(req,resp);
		} else {	
			try {
				//create new staff with all the parameter
				String[] myS = {""};
				data.createStaff(username, firstname + " " +lastname, password, telephone, myS, stafftype);
			} catch (EntityNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			String http = "";
			//Staff created confirmation page.
			http += "<form id=\"ccf\" method=\"GET\" action=\"/createStaff\">"
			+			"<div id=\"title-create-staff\">"
			+				"Staff Created Conformation"
			+			"</div>"
			+ 			"<div id=\"sub\">"
			+				"UserName: " + username + "<br>" 
			+				"First Name: " + firstname + "<br>" 
			+				"Last Name: " + lastname + "<br><br>" 
			+				"Staff Type: " + stafftype + "<br>" 
			+				"The User has been Created.<br><br><br><br><br><br>"
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
		
		http += "<form id=\"ccf\" method=\"POST\" action=\"/createStaff\">"
		+			"<div id=\"title-create-staff\">"
		+				"Create Staff"
		+			"</div>";
		
		String username = req.getParameter("username") != null ? req.getParameter("username") : "";
		username = username.toLowerCase();
		String password = req.getParameter("password") != null ? req.getParameter("password") : "";
		String firstname = req.getParameter("firstname") != null ? req.getParameter("firstname") : "";
		String lastname = req.getParameter("lastname") != null ? req.getParameter("lastname") : "";
		String telephone = req.getParameter("telephone") != null ? req.getParameter("telephone") : "";
		String stafftype = req.getParameter("stafftype") != null ? req.getParameter("stafftype") : "";

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
		+							"Username *: <input class='createStaffInput' type=\"text\" id='username' name='username' value='" + username + "'/><br>"
		+							"Password *: <input class='createStaffInput' type=\"password\" id='password' name='password' value='" + password + "'/><br>"
		+							"First Name *: <input class='createStaffInput' type=\"text\" id='firstname' name='firstname' value='" + firstname + "'/><br>"
		+							"Last Name *: <input class='createStaffInput' type=\"text\" id='lastname' name='lastname' value='" + lastname + "'/><br>"
		+							"Telephone: <input class='createStaffInput' type=\"text\" id='telephone' name='telephone' value='" + telephone + "'/><br>"
		+							"Staff Type: <select class='staff-select createStaffInput' id='stafftype' name='stafftype' value='" + stafftype + "'>"
				+									"<option value = '' selected> Select a Type </option>"
		+											"<option> Instructor </option>"
		+											"<option> TA </option>"
		+										"</select><br>"
		+						"</td>"
		+					"</tr>"
		+				"</table>"
		+				"<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
		+			"</div>"
		+		"</form>";
		
		return http;
	}

}