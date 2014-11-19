package edu.uwm.cs361;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Section {
	// applying the same annotation to multiple values seems to only now have been 
	// supported in java 1.8, which is not what we're using
	// lol seriously. 
	
	@Persistent
	private Course course;
	@Persistent
	private String units;
	@Persistent
	private String designation;
	@Persistent
	private String hours;
	@Persistent
	private String days;
	@Persistent
	private String dates;
	@Persistent
	private String instructor;
	@Persistent
	private String room;
	@PrimaryKey
	@Persistent
	private String keyfield;
	
//	@PrimaryKey
//	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//	private Key key;
	
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
		keyfield = c.key() + des;
	}
	public Section(String un, String des, 
			String hr, String dy, String dts, String ins, String rm){
		// deprecated
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
