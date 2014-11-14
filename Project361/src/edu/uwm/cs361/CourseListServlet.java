package edu.uwm.cs361;

// cargo cult imports from CreateStaffServlet. clean up later
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
public class CourseListServlet extends HttpServlet{
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
		String http = "";
//		String http = "<table>\n";
		http += "<form id=\"ccflist\">"
		+			"<div id=\"title-create-staff\">"
		+				"Course List"
		+			"</div>";
		http += 	"<div id=\"sub\">"
		+				"<table class='courselist'>";

		Query q = new Query(ds.COURSE);
		List<Entity> courseListEnts = ds.getDatastore().prepare(q).asList(FetchOptions.Builder.withDefaults());
		
			List<Course> courseList;
			for(Entity courseEnt : courseListEnts){
				try{
					//System.out.println("...");
					ArrayList<Section> sectionList = new ArrayList<Section>();
					String sList = courseEnt.getProperty(ds.SECTION_LIST).toString();
					String[] sListArr = ds.makeDelStringToArray(sList);
					
					for(int i = 0; i < sListArr.length; ++i) {
						//System.out.println(""+sListArr[i]);
						Entity sEnt = ds.getSection(ds.getOurKey(courseEnt.getKey()) + " " + sListArr[i].replaceAll("~", ""));
						//System.out.println(sEnt.toString());
						sectionList.add(new Section(sEnt));
					}
					
					Course curCourse = new Course(courseEnt);
					curCourse.setSections(sectionList);
					
					http+=curCourse.toHtmlTable();
				} catch(Exception e) {
					e.printStackTrace();
					//System.out.println("I Failed");
				}
			}
		http +=				"</table>"
		+			"</div>"
		+		"</form>";
		return http;
	}
		
}
