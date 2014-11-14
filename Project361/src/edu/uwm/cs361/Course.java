package edu.uwm.cs361;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Entity;

public class Course {
	String designation;
	String title;
	ArrayList<Section> sections;
	
	public Course(String des, String titl, ArrayList<Section> secs){
		designation = des;
		title = titl;
		sections = secs;
	}

	public Course(Entity e) {
		DemeritDatastoreService ds = new DemeritDatastoreService();
		String myKey = ds.getOurKey(e.getKey());
		designation = "COMPSCI-" +myKey; 
		title = e.getProperty(ds.COURSE_TITLE).toString();
		
	}
	
	public String getDesignation() {
		return designation;
	}
	public String getNumber() {
		// gotta comply with DemeritDatastoreService.java
		return designation.replaceFirst(".*?(?=\\d)", "");
	}
	public String getTitle() {
		return title;
	}
	public ArrayList<Section> getSections() {
		return sections;
	}
	public void setSections(ArrayList<Section> secs){
		sections = secs;
	}
	@Override
	public String toString() {
		return "Course [designation=" + designation + ", title=" + title
				+ ", sections=" + sections + "]";
	}
	public String toHtmlTable() {
		String str = "";
		str = String.format("<tr><td>%s</td><td>%s</td></tr>\n",designation,title);
		for(Section s : sections){
			str += s.toHtmlTR();
		}
		return str;
	}
}
