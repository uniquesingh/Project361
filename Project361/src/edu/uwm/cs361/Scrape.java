package edu.uwm.cs361;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;

import edu.uwm.cs361.DemeritDatastoreService;

public class Scrape {
	/////////////////////////////////////
	// call this from a web call or something
	public static void main(String[] args){
		getCourseListandStore();
	}
	public static void getCourseListandStore(){
		try{
			ArrayList<Course> c = getURL("http://www4.uwm.edu/schedule/index.cfm?a1=subject_details&subject=COMPSCI&strm=2152");
//			System.out.println(c);
			commitCourses(c);
		} catch (IOException e){ 
			e.printStackTrace(); 
		}
		
	}
	
	//////////////////////////////////////////////////////////////
	// wget
	private static ArrayList<Course> getURL(String url) throws IOException {
		URL place = new URL(url);
		String buf = "";
		BufferedReader in = new BufferedReader(
				new InputStreamReader(place.openStream())
				);
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			if(inputLine.trim().isEmpty())
				continue;
			buf += removeTags(inputLine).trim() + " "; // ugh
		}
		in.close();
		return processCourses(buf);
	}
	
	//////////////////////////////////////////////////////////////
	// grokking
	// Place the important bits into a list which can easily be
	// worked with in java. the resulting data structure is simple
	// enough. it's just a list of Course objects which themselves
	// contain their designation, title, and a list of their 
	// sections. a Section is just a struct containing 'useful'
	// variables
	private static ArrayList<Course> processCourses(String text){
		Pattern pattern = Pattern.compile("COMPSCI[-\\ ]\\d{3}(?:(?!div_course_details).)*?div_course_details");
		Matcher matcher = pattern.matcher(text);
		ArrayList<Course> c = new ArrayList<Course>();
		while (matcher.find()){
			String g = matcher.group();
			Course cour = new Course(slurpDesignation(g),slurpTitle(g),null);
			cour.setSections(processSections(cour,g));
			c.add(cour);
		}
		return c;
	}
	private static ArrayList<Section> processSections(Course c, String text){
		ArrayList<Section> s = new ArrayList<Section>();
		// the only thing iterations are good for is making it difficult to
		// write in a functional style
		for(String str : getSections(text)){
			try{
				s.add(new Section(c, slurpUnits(str), slurpSecDesignation(str), 
					slurpHours(str), slurpDays(str), slurpDates(str), 
					slurpInstructor(str), slurpRoom(str)));
			}catch(Exception e){
				// damn you, Nathaniel Stern
			}
		}
		return s;
	}
	
	///////////////////////////////////////////////////
	// misc
	private static String removeTags(String line){
		// who can see anything with all those angle brackets everywhere?
		return line.replaceAll("<[^<>]*>", ""); // regex a neat
	}
	private static String killnbsp(String text){
		// ended up only needing this in one place
		// maybe should just factor it back it
		return text.replaceAll("&nbsp;", "");
	}
	
	///////////////////////////////////////////////////////////
	// plumbing
	// every method with 'slurp' in the name follows almost the
	// same pattern, though some have some small caveats
	private static String slurper(String text, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		matcher.find();
		return matcher.group();
	}
	
	//////////////////////////////////////////////////////
	// course slurps
	// you can see the basic structure of information at
	// http://www4.uwm.edu/schedule/index.cfm?a1=subject_details&subject=COMPSCI&strm=2152
	// is simply a list of courses, and each course has a few
	// attributes in addition to a list of sections. so get the 
	// attributes, and the sections. then get the attributes for 
	// every section.
	private static String slurpDesignation(String text){
		return slurper(text, "^.*?(?=:)");
	}
	private static String slurpTitle(String text){
		return slurper(text, "(?<=:).*?(?=\\()").trim();
	}
	private static String[] getSections(String text){
		// it sure would be nice if java had a map function
		return text.replaceFirst("^.*?\\(FEE\\)", "").split("\\(FEE\\)");
	}

	////////////////////////////////////////////////
	// section slurps
	// what they slurp is in the name of the method
	// not going to bother explaining my reasoning
	// behind each regex. 
	private static String slurpUnits(String text){
		return killnbsp(slurper(text, "^.*?(?=[A-Z])")).trim();
	}
	private static String slurpSecDesignation(String text){
		return slurper(text,"[A-Z]{3} \\d{3}");
	}
	private static String slurpHours(String text){
		return slurper(text, "(?<=\\d{5}).*?(?=\\s{3})").trim();
	}
	private static String slurpDays(String text){
		return slurper(text,"(?<=\\s)[MTWRF\\-]{1,5}(?=\\s)");
	}
	private static String slurpDates(String text){
		return slurper(text,"\\d{2}/\\d{2}-\\d{2}/\\d{2}");
	}
	private static String slurpInstructor(String text){
		try{
			return slurper(text, "[A-Z][a-z]+, [A-Z][a-z]+");
		}
		catch(Exception e){
			return "";
		}
	}
	private static String slurpRoom(String text){
		String step = slurpInstructor(text);
		if(step.isEmpty())
			return slurper(text.replaceFirst("^.{10}", ""),"(?<=;).*?(?=&)").trim(); // probably a better way to do this
		return slurper(text,"(?<=" + step + ").*?(?=&)").trim();
		// regex a best
	}
	
	private static void commitCourses(ArrayList<Course> courses){
		DemeritDatastoreService ds = new DemeritDatastoreService();
		for(Course c : courses){
			if( ! ds.hasDuplicate(ds.COURSE, c.getNumber()) ){
				// most of these are attributes of the section, not course
				try {
//					Query q = new Query(ds.STAFF).setFilter(new Filter(ds.NAME,c.sections.get(0).getInstructor()));
					ds.createCourse(c);
				} catch (EntityNotFoundException e) {
					e.printStackTrace(); // throw it onto the user's lap
				}
			}
		}
	}
}
