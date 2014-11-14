
package edu.uwm.cs361;

//cargo cult imports from CreateStaffServlet. clean up later
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

import edu.uwm.cs361.ProjectServlet;
import edu.uwm.cs361.DemeritDatastoreService;;

@SuppressWarnings("serial")
public class ScrapeServlet extends HttpServlet{
	ProjectServlet page = new ProjectServlet();
	DemeritDatastoreService ds = new DemeritDatastoreService();
	 	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		page.banner(req,resp);
		page.courseListLayout(buildPage(),req,resp);
		page.menu(req,resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	}
	
	private String buildPage(){
		Scrape.getCourseListandStore();
		String http = "";
		http += "<form id=\"ccflist\">"
		+			"<div id=\"title-create-staff\">"
		+				"Scrapping Course List Conformation"
		+			"</div>";
		http += 	"<div id=\"sub\">"
		+					"The Course Scrapping is done."
		+			"</div>"
		+		"</form>";
		return http;
	}
		
}
