package edu.uwm.cs361;

import com.google.appengine.api.datastore.Entity;


public class Section {
	Course course;
	String units;
	String designation;
	String hours;
	String days;
	String dates;
	String instructor;
	String room;
	
	public Section(Course c, String un, String des, 
			String hr, String dy, String dts, String ins, String rm){
		course = c;
		units = un;
		designation = des;
		hours = hr;
		days = dy;
		dates = dts;
		instructor = ins;
		room = rm;
	}
	public Section(String un, String des, 
			String hr, String dy, String dts, String ins, String rm){
		units = un;
		designation = des;
		hours = hr;
		days = dy;
		dates = dts;
		instructor = ins;
		room = rm;
	}
	
	public Section(Entity s){
		DemeritDatastoreService ds = new DemeritDatastoreService();
		units = s.getProperty(ds.UNITS).toString();
		String str = s.getKey().toString();
		designation = s.getProperty(ds.TYPE).toString() + " " +str.substring(str.indexOf(' ')+1, str.indexOf(')') - 1);
		if(s.getProperty(ds.TIME) != null)
				hours = s.getProperty(ds.TIME).toString();
		days = s.getProperty(ds.DAYS).toString();
//		dates = s.getProperty(ds.UNITS).toString(); fug
		try{
			instructor = s.getProperty(ds.STAFF).toString();
		}catch(Exception e){
			// eh
			instructor = "";
		}
		room = s.getProperty(ds.ROOM).toString();
		
	}

	// CLOS does OO better
	public String getUnits() {
		return units;
	}
	public String getDesignation() {
		return designation;
	}
	public String getType(){
		return designation.substring(0, 4);
	}
	public String getNumber() {
		return designation.replaceFirst(".*?(?=\\d)", "");
	}
	public String getHours() {
		return hours;
	}
	public String getDays() {
		return days;
	}
	public String getDates() {
		return dates;
	}
	public String getInstructor() {
		return instructor;
	}
	public String getRoom() {
		return room;
	}
	@Override
	public String toString() {
		return "Section [units=" + units + ", designation=" + designation
				+ ", hours=" + hours + ", days=" + days + ", dates=" + dates
				+ ", instructor=" + instructor + ", room=" + room + "]";
	}
	public String toHtmlTR() {
		return String.format("<tr class='border_bottom'><td></td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
				designation, units, hours, days, instructor, room);
	}
}
