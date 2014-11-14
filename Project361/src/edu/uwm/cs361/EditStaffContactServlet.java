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

@SuppressWarnings("serial")
public class EditStaffContactServlet extends HttpServlet{
	ProjectServlet page = new ProjectServlet();
	DemeritDatastoreService data = new DemeritDatastoreService();
	DatastoreService ds =  data.getDatastore();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		Query q = new Query(data.STAFF);
		List<Entity> users = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
		String http = "";

		String staff = "";
		staff = req.getParameter("staff");
		if(staff==null){
			page.banner(req,resp);
			http += "<form id=\"ccf\" method=\"GET\" action=\"/editStaffContact\">"
			+			"<div id=\"title-create-staff\">"
			+				"Edit Staff Contact"
			+			"</div>"
			+			"<div id=\"sub\">"
			+				"<table>"
			+					"<tr>"
			+						"<td class='form'>"
			+							"Staff:"
			+							"<select id='staff' name='staff' class='staff-select'>";
											http += "<option disabled>Instructor's</option>";		
											for(Entity user:users){
												if(!user.getProperty(data.TYPE).equals("TA"))
														http += "<option>" + data.getOurKey(user.getKey()) + "</option>";
														
											}
											http += "<option disabled>TA's</option>";
											for(Entity user:users){
												if(user.getProperty(data.TYPE).equals("TA"))
													http += "<option>" + data.getOurKey(user.getKey()) + "</option>";
											}
			http +=						"</select><br><br>"
			+						"</td>"
			+					"</tr>";
			http+=				"</table>"
			+				"<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
			+			"</div>"
			+		"</form>";
			page.layout(http,req,resp);
			page.menu(req,resp);
		}
		else{
			page.banner(req,resp);
			page.layout(displayForm(req,resp, new ArrayList<String>(), staff), req, resp);
			page.menu(req,resp);
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		
		String toEditEmail = req.getParameter("staff");
		String officePhone = req.getParameter("officePhone");
		String office = req.getParameter("office");
		String homeAddress = req.getParameter("homeAddress");
		String homePhone = req.getParameter("homePhone");
		
		List<String> errors = new ArrayList<String>();
		
		if (officePhone == null)
			officePhone = "";
		if (office == null)
			office = "";
		if (homeAddress == null)
			homeAddress = "";
		if (homePhone == null)
			homePhone = "";

		
		if (errors.size() > 0) {
			page.banner(req,resp);
			page.layout(displayForm (req,resp,errors, toEditEmail),req,resp);
			page.menu(req,resp);
		} else {
	
			Entity e = null;
			
			try {
				
				data.updateStaffContact(toEditEmail, office, officePhone, homeAddress, homePhone);
				e = data.getStaff(toEditEmail);
				
			} catch (EntityNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			String http = "";
			
			http += "<form id=\"ccf\" method=\"GET\" action=\"/editStaffContact\">"
			+			"<div id=\"title-create-staff\">"
			+				"Edit Contact info: " + req.getParameter("staff")
			+			"</div>"
			+ 			"<div id=\"sub\">"
			+				"Office: " + e.getProperty(data.OFFICE_LOCATION) + "<br>" 
			+				"Office Phone: " + e.getProperty(data.OFFICE_PHONE) + "<br>" 
			+				"Home Address: " + e.getProperty(data.HOME_ADDRESS) + "<br><br>" 
			+				"Home Phone: " + e.getProperty(data.HOME_PHONE) + "<br>" 
			+				"The User's contact info has been updated.<br><br><br><br><br><br>"
			+				"<input class=\"submit\" type=\"submit\" value=\"Back\" />"
			+			"</div>"
			+		"</form>";
			page.banner(req,resp);
			page.layout(http,req,resp);
			page.menu(req,resp);
		}
	}
	
	private String displayForm(HttpServletRequest req, HttpServletResponse resp, List<String> errors, String staff) throws IOException
	{	
		
		Entity toUpdate = null;
		try {
			toUpdate = data.getStaff(staff);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resp.setContentType("text/html");
		String http = "";
		
		http += "<form id=\"ccf\" method=\"POST\" action=\"/editStaffContact\">"
		+			"<div id=\"title-create-staff\">"
		+				"Edit Contact info: " + toUpdate.getProperty(data.NAME).toString()
		+			"</div>";
		
		String name = toUpdate.getProperty(data.NAME).toString();
		String office = toUpdate.getProperty(data.OFFICE_LOCATION).toString();
		String officePhone = toUpdate.getProperty(data.OFFICE_PHONE).toString();
		String homeAddress = toUpdate.getProperty(data.HOME_ADDRESS).toString();
		String homePhone = toUpdate.getProperty(data.HOME_PHONE).toString();
		System.out.println(name + "\n" + office + "\n" + officePhone + "\n" + homeAddress + "\n" + homePhone);
		

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
		+						"<td class=\"form\" >"
		+							"<input class='createStaffInput' type=\"hidden\" id='staff' name='staff' value='" + staff + "'/><br>"
		+							"Office: <input class='createStaffInput' type=\"text\" id='officeLoc' name='office' value='" + office + "'/><br>"
		+							"Office Phone: <input class='createStaffInput' type=\"text\" id='officePhone' name='officePhone' value='" + officePhone + "'/><br>"
		+							"Home Address: <input class='createStaffInput' type=\"text\" id='homeAddress' name='homeAddress' value='" + homeAddress + "'/><br>"
		+							"Home Phone: <input class='createStaffInput' type=\"text\" id='homePhone' name='homePhone' value='" + homePhone + "'/><br>"
		+						"</td>"
		+					"</tr>"
		+				"</table>"
		+				"<input class=\"submit\" type=\"submit\" value=\"Submit\" />"
		+			"</div>"
		+		"</form>";
		
		
		
		return http;
	}

}