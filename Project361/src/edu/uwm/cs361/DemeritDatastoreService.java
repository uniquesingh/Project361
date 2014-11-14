package edu.uwm.cs361;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class DemeritDatastoreService {

	public final String STAFF = "staff";
	public final String EMAIL = "email";
	public final String OFFICE_HOURS = "officeHours";
	public final String OFFICE_LOCATION = "officeLoc";
	public final String OFFICE_PHONE = "officePhone";
	public final String HOME_ADDRESS = "address";
	public final String HOME_PHONE = "homePhone";
	public final String PASSWORD = "password";
	public final String COURSE_AND_SECTION = "courses";
	public final String NAME = "name";
	public final String TYPE = "type";

	public final String UNITS = "units";
	public final String COURSE = "course";
	public final String SECTION_LIST = "sections";
	public final String CREDITS = "credits";
	public final String DESIGNATION = "designation";
	public final String COURSE_TITLE = "title";

	public final String SECTION = "section";
	public final String DATES = "dates";
	public final String DAYS = "days";
	public final String ROOM = "room";
	public final String TIME = "time";
	public final String HOURS = "time";

	private final String DELIMITER = "~";

	private DatastoreService ds = null;

	/**
	 * Constructor for DemeritDatastoreService Use an instance of this for
	 * creates, updates, retrievals. Use the getDatastore() method to get a DS
	 * reference for your queries.
	 */
	public DemeritDatastoreService() {
		ds = DatastoreServiceFactory.getDatastoreService();
	}

	/**
	 * getDatastore
	 * 
	 * Gets a reference to the internal datastore object- used to run your own
	 * queries
	 * 
	 * @return reference to the internal datastore.
	 */
	public DatastoreService getDatastore() {
		return ds;
	}

	/**
	 * CreateStaff
	 * 
	 * Creates a new staff within the datastore. <br>
	 * <br>
	 * Precondition: Method does not check for duplicates, must call
	 * hasNoDuplicate(String entityType, String myKey) first.
	 * 
	 * @param email
	 *            String representing staff's email address
	 * @param name
	 *            String representing staff's name: First Last
	 * @param courseNumSectionNum
	 *            : (NULL if none) String array, each index is a string
	 *            representing a class taught, MUST be in "COURSENUM SECTIONNUM"
	 *            format.
	 * @param officeHours
	 *            (NULL if none) String array, each index is a string
	 *            representing a set of office hours. MUST be in
	 *            "DAY TIMEFROM-TIMETO" format.
	 * @param type
	 *            String representing type of staff: TA, Instructor, Admin.
	 * @throws EntityNotFoundException
	 *             Throws exception if any index of courseNumSectionNum is not
	 *             already created.
	 * 
	 * @postcondition Staff will be an entity in datastore.
	 */
	public void createStaff(String email, String name, String password, String homePhone, String[] officeHours, String type)
			throws EntityNotFoundException {

		Entity newStaff = new Entity(STAFF, email);
		newStaff.setProperty(TYPE, type);
		newStaff.setProperty(NAME, name);
		newStaff.setProperty(PASSWORD, password);
		newStaff.setProperty(HOME_PHONE, homePhone);
		
		Query q = new Query(SECTION);
		List<Entity> sectionList = ds.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		
		String temp = "";
		String staffSections = "";

		for (Entity i : sectionList)
		{
			if ( i.getProperty(STAFF).equals(email))
			{
				temp = getOurKey(i.getKey());
				String[] x = temp.split(" ");
				staffSections += x[1] + "~";
			}	
		}
		
		if(!staffSections.isEmpty() && staffSections.charAt(staffSections.length() -1) == '~'){
			staffSections = staffSections.substring(0, staffSections.length() -1);
		}
		

		String newOfficeHours = "";
		if (officeHours != null && officeHours[0] != "")
			newOfficeHours = makeDelString(officeHours);
		
		newStaff.setProperty(OFFICE_HOURS, newOfficeHours);
		newStaff.setProperty(SECTION_LIST, staffSections);
		newStaff.setProperty(OFFICE_LOCATION, "");
		newStaff.setProperty(OFFICE_PHONE, "");
		newStaff.setProperty(HOME_ADDRESS, "");
		newStaff.setProperty(HOME_PHONE, "");

		ds.put(newStaff);

	}

	/*
	 * Helper method
	 */
	private void addStaffToSection(String[] courseNumSectionNum, String email)
			throws EntityNotFoundException {
		Entity mySection = null;

		for (int i = 0; i < courseNumSectionNum.length; ++i) {
			if (courseNumSectionNum[i].equals("")
					|| courseNumSectionNum[i] == null)
				return;

			Transaction txn = ds.beginTransaction();
			try {
				mySection = getSection(courseNumSectionNum[i]);

				Entity myRealSection = ds.get(mySection.getKey());
				myRealSection.setProperty(STAFF, email);

				ds.put(myRealSection);
				txn.commit();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
				}
			}

		}

	}

	/*
	 * Helper method
	 */
	private void addSectionToStaff(String courseNumSectionNum, String staffEmail)
			throws EntityNotFoundException {
		
		if (!hasDuplicate(STAFF, staffEmail))
			return;
		
		Transaction txn = ds.beginTransaction();
		try {
			Entity myTeacher = getStaff(staffEmail);
			myTeacher = ds.get(myTeacher.getKey());

			String mySections = (String) myTeacher
					.getProperty(COURSE_AND_SECTION);
			mySections = delimitedStringAppend(mySections, courseNumSectionNum);

			myTeacher.setProperty(COURSE_AND_SECTION, mySections);

			ds.put(myTeacher);

			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	/**
	 * updateSection
	 * 
	 * Use this to change any fields of the section, <br>
	 * EXCEPT the course number or section number ~these cannot change.
	 * 
	 * @param courseNumber
	 *            String representing the course number
	 * @param days
	 *            Days of the week section meets. Use single letter
	 *            representation to match standard. ex) M W R F
	 * @param lecLabDis
	 *            String indicating if section is LEC, LAB, or DIS
	 * @param sectionNumber
	 *            String representing the section number
	 * @param room
	 *            String representing the section location. ex) EMS W120
	 * @param staffEmail
	 *            String representing the staff's email address, use "" if none
	 * @param time
	 *            String representing meeting times.
	 * @throws EntityNotFoundException
	 *             Throws exception if trying to update course which doesn't
	 *             exist
	 */
	public void updateSection(String courseNumber, String days,
			String lecLabDis, String sectionNumber, String room,
			String staffEmail, String time) throws EntityNotFoundException {
		Transaction txn = ds.beginTransaction();
		try {
			String scKey = courseNumber + " " + sectionNumber;
			Key sectionKey = KeyFactory.createKey(SECTION, scKey);
			Entity updatedSection = ds.get(sectionKey);

			updatedSection.setProperty(DAYS, days);
			updatedSection.setProperty(TYPE, lecLabDis);
			updatedSection.setProperty(ROOM, room);

			if (!(staffEmail == null || staffEmail == ""))
				updatedSection.setProperty(STAFF, staffEmail);
			else
				updatedSection.setProperty(STAFF, "");

			updatedSection.setProperty(TIME, time);

			ds.put(updatedSection);

			if (!staffEmail.equals(""))
				addSectionToStaff(scKey, staffEmail);

			txn.commit();
		}

		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	/**
	 * updateStaff
	 * 
	 * Use this to update any of the passed staff's fields. <br>
	 * Email CANNOT change and any sections MUST already exist **Will be
	 * updating to allow changing email**
	 * 
	 * @param email
	 *            String representing staff's current email CANNOT change
	 * @param name
	 *            String representing staffs new / unchanged name
	 * @param officeHours
	 *            String array, each index is a string representing a set of
	 *            office hours. MUST be in "DAY TIMEFROM-TIMETO" format.
	 * @param type
	 *            String indicating Instructor or TA
	 * @throws EntityNotFoundException
	 *             Throws exception if staff is not found. i.e. email not
	 *             existing staff
	 */
	public void updateStaff(String email, String name, String password, String homePhone, String[] officeHours, String type)
			throws EntityNotFoundException {

		Transaction txn = ds.beginTransaction();
		try {
			Key employeeKey = KeyFactory.createKey(STAFF, email);
			Entity employee = ds.get(employeeKey);

			employee.setProperty(NAME, name);
			employee.setProperty(TYPE, type);
			employee.setProperty(PASSWORD, password);
			employee.setProperty(HOME_PHONE, homePhone);

			String newOfficeHours = "";
			if (officeHours != null && officeHours[0] != "")
				newOfficeHours = makeDelString(officeHours);


			employee.setProperty(OFFICE_HOURS, newOfficeHours);


			ds.put(employee);
			txn.commit();
		}

		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	/**
	 * updateStaffContact
	 * 
	 * Adds or updates staff contact info. Staff must already exist
	 * 
	 * @param email
	 *            String representing staff's email address
	 * @param officeLocation
	 *            String containing office building and room number
	 * @param officePhone
	 *            String representing staff's office phone number
	 * @param homeAddress
	 *            String representing staff's home address
	 * @param homePhone
	 *            String representing staff's home telephone number
	 * 
	 * @throws EntityNotFoundException
	 *             Throws exception if email not found(staff doesnt exist)
	 */
	public void updateStaffContact(String email, String officeLocation,
			String officePhone, String homeAddress, String homePhone)
			throws EntityNotFoundException {

		Transaction txn = ds.beginTransaction();
		try {
			Key employeeKey = KeyFactory.createKey(STAFF, email);
			Entity employee = ds.get(employeeKey);

			employee.setProperty(OFFICE_LOCATION, officeLocation);
			employee.setProperty(OFFICE_PHONE, officePhone);
			employee.setProperty(HOME_ADDRESS, homeAddress);
			employee.setProperty(HOME_PHONE, homePhone);

			ds.put(employee);
			txn.commit();
		}

		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	/**
	 * hasNoDuplicate
	 * 
	 * Returns true if there is no duplicate of the passed key within the
	 * datastore, false otherwise.
	 * 
	 * @param entityType
	 *            String representing what you are looking for. Can use our
	 *            datastore instance variables: <br>
	 *            ds.STAFF <br>
	 *            ds.SECTION <br>
	 *            ds.COURSE
	 * @param myKey
	 *            String of the key for the particular object you are checking. <br>
	 *            For staff: use email <br>
	 *            For course: use course number <br>
	 *            For section, use COURSENUM SECTIONNUM !If section key not
	 *            entered properly, will fail
	 * @return True if entity already exists, false otherwise.
	 */
	public boolean hasDuplicate(String entityType, String myKey) {

		Entity lookingFor = null;
		Transaction txn = ds.beginTransaction();

		try {

			Key entityKey = KeyFactory.createKey(entityType, myKey);
			lookingFor = ds.get(entityKey);

		} catch (EntityNotFoundException e) {
		}
		txn.commit();

		if (lookingFor == null)
			return false;
		else
			return true;
	}

	/**
	 * createCourse
	 * 
	 * adds the given course instantiation to datastore NOTICE: all sections in
	 * ArrayList<Section> course.sectionList WILL BE automatically added
	 * 
	 * @param course
	 *            Course instance to add to datastore
	 * @throws EntityNotFoundException
	 */
	public void createCourse(Course course) throws EntityNotFoundException
	{
		
		Entity newCourse = new Entity(COURSE, course.getNumber());
		newCourse.setProperty(COURSE_TITLE, course.getTitle());
		newCourse.setProperty(DESIGNATION, course.getDesignation());
		newCourse.setProperty(SECTION_LIST, makeDelString(course.getSections()));
		
		String courseNum = course.getNumber();
		ArrayList<Section> temp = course.getSections();
		ds.put(newCourse);
		for( Section i : temp)
			createSection(courseNum, i);
		
		
	}
	
	/*
	 * Helper
	 */
	public void createCourse(String name, String number, String sectionNumber,
			String credits, String days, String lecLabDis, String room,
			String staffEmail, String time) throws EntityNotFoundException {

		Entity newCourse = new Entity(COURSE, number);
		newCourse.setProperty(NAME, name);
		newCourse.setProperty(CREDITS, credits);

		ds.put(newCourse);

		createSection(number, days, lecLabDis, sectionNumber, room, staffEmail,
				time);
	}

	/*
	 * Helper
	 */
	public void createSection(String courseNumber, String days,
			String lecLabDis, String sectionNumber, String room,
			String staffEmail, String time) throws EntityNotFoundException {

		String scKey = courseNumber + " " + sectionNumber;
		Entity newSection = new Entity(SECTION, scKey);

		newSection.setProperty(DAYS, days);
		newSection.setProperty(TYPE, lecLabDis);
		newSection.setProperty(ROOM, room);
		newSection.setProperty(STAFF, staffEmail);
		newSection.setProperty(TIME, time);

		if (!staffEmail.equals(""))
			addSectionToStaff(scKey, staffEmail);

		Transaction txn = ds.beginTransaction();
		try {
			Entity myFakeCourse = getCourse(courseNumber);
			Entity myCourse = ds.get(myFakeCourse.getKey());

			String mySections = (String) myCourse.getProperty(SECTION_LIST);
			mySections = delimitedStringAppend(mySections, sectionNumber);

			myCourse.setProperty(SECTION_LIST, mySections);

			ds.put(myCourse);

			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		ds.put(newSection);
	}

	/**
	 * createSection
	 * 
	 * adds a section to the datastore section's course MUST already be created
	 * 
	 * @param courseNum
	 *            String containing the course number to which this section
	 *            belongs
	 * @param section
	 *            Section to add
	 * @throws EntityNotFoundException
	 */
	public void createSection(String courseNum, Section section) throws EntityNotFoundException
	{
		
		String scKey = courseNum + " " + section.getNumber();
		Entity newSection = new Entity(SECTION, scKey);
		
		newSection.setProperty(DATES, section.getDates());
		newSection.setProperty(DAYS, section.getDays());
		newSection.setProperty(TYPE, section.getType());
		newSection.setProperty(ROOM, section.getRoom());
		newSection.setProperty(HOURS, section.getHours());
		newSection.setProperty(UNITS,  section.getUnits());
		
//		String emailKey = null;
//		Query q = new Query(STAFF);
//		List<Entity> staffList = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
//		for (Entity i : staffList)
//			if (i.getProperty(NAME).equals(section.getInstructor()))
//				emailKey = getOurKey(i.getKey());
//		
//		newSection.setProperty(STAFF, emailKey);
//
//		
//		//add section to staffs section list
//		if(emailKey!=null && !section.getInstructor().equals("")){
//			addSectionToStaff(scKey, section.getInstructor());
//		}
//		
		newSection.setProperty(STAFF, section.getInstructor());
		//add section to course list
		Transaction txn = ds.beginTransaction();
		try {
			Entity myFakeCourse = getCourse(courseNum);
			Entity myCourse = ds.get(myFakeCourse.getKey());
			
			String mySections = (String) myCourse.getProperty(SECTION_LIST);
			if (!mySections.contains(section.getNumber()))
				mySections = delimitedStringAppend(mySections, section.getNumber());
			
			myCourse.setProperty(SECTION_LIST,mySections);
		
			ds.put(myCourse);
			
			txn.commit();
			
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	
		ds.put(newSection);
	}

	/**
	 * makeDelStringToArray
	 * 
	 * Makes a deliminated string into an array.Likely string will be office
	 * hours or section list. <br>
	 * Use this on the entity properties from the entities you get from queries
	 * 
	 * @param stringIn
	 *            Deliminated string to convert to array
	 * @return String array containing the stringIn tokens
	 */
    public String[] makeDelStringToArray(String stringIn) {
        if(stringIn.equals("")) {
            String[] myRetFixer = {""};
            return myRetFixer; 
        }
        
        ArrayList<String> strList = new ArrayList<String>();
        String[] ret = new String[50];
        ret[0] = "";
        
        int i = stringIn.indexOf(DELIMITER);
        int j = -1;
        int count = 0;
        while(i >= 0)
        {
            if(j == -1) j = 0;
            String addend = stringIn.substring(j, i);
            ret[count] = addend;
            count++;
            j = i;
            i = stringIn.indexOf(DELIMITER, i+1);
        }
        
        ret[count] = stringIn.substring(j+1);
        
        for(int p = 0;p<ret.length;++p){
            if(ret[p] != null) strList.add(ret[p]);
        }
        
        String[] realRet = new String[strList.size()];
        for(int zz = 0; zz < realRet.length; ++zz){
            realRet[zz] = strList.get(zz);
        }
        
        return realRet;
    }

	private ArrayList<Section> makeDelStringToArrayList(String courseNum,
			String string) throws EntityNotFoundException {

		ArrayList<Section> sections = new ArrayList<Section>();

		int i = string.indexOf(DELIMITER);
		int j = -1;

		while (i >= 0) {
			if (j == -1)
				j = 0;
			Key sectionKey = createSectionKey(courseNum, string.substring(j, i));
			Entity temp = getSection(getOurKey(sectionKey));
			Section tempSection = new Section(temp.getProperty(UNITS)
					.toString(), temp.getProperty(DESIGNATION).toString(), temp
					.getProperty(HOURS).toString(), temp.getProperty(DAYS)
					.toString(), temp.getProperty(DATES).toString(), temp
					.getProperty(STAFF).toString(), temp.getProperty(ROOM)
					.toString());

			sections.add(tempSection);

			j = i;
			i = string.indexOf(DELIMITER, i + 1);
		}

		Key sectionKey = createSectionKey(courseNum, string.substring(j + 1));
		Entity temp = getSection(getOurKey(sectionKey));
		Section tempSection = new Section(temp.getProperty(UNITS).toString(),
				temp.getProperty(DESIGNATION).toString(), temp.getProperty(
						HOURS).toString(), temp.getProperty(DAYS).toString(),
				temp.getProperty(DATES).toString(), temp.getProperty(STAFF)
						.toString(), temp.getProperty(ROOM).toString());

		sections.add(tempSection);

		return sections;
	}

	/*
	 * Helper method
	 */
	private String makeDelString(String[] rawStr) {

		String ret = "";

		for (int i = 0; i < rawStr.length; ++i) {
			if (i > 0) {
				ret += DELIMITER;
			}

			ret += rawStr[i];

		}

		return ret;
	}
	
	private String makeDelString(ArrayList<Section> sections) {
		
		String[] sctns = new String[sections.size()];
		
		int j = 0;
		for (Section i : sections)
		{
			sctns[j] = i.getNumber();
			j++;
		}

		return makeDelString(sctns);
	}
	
	/*
	 * Helper method
	 */
	private String delimitedStringAppend(String mySections, String addend) {
		String ret = "";
		if (mySections != null) {
			ret = mySections + DELIMITER;
		}
		return ret + addend;
	}

	/**
	 * getOurKey
	 * 
	 * Gets an actual key value from a Key value returned from an Entities
	 * getKey() method
	 * 
	 * @param entry
	 *            Key value returned from an Entities getKey() method
	 * @return String containing a usable key
	 */
	public String getOurKey(Key entry) {
		String asString = entry.toString();
		int index1 = asString.indexOf("\"");
		int index2 = asString.indexOf("\"", index1 + 1);

		return asString.substring(index1 + 1, index2);
	}

	/**
	 * createSectionKey
	 * 
	 * Creates a unique section key which can be used for queries, datastore
	 * retrieves, datastore updates, etc.
	 * 
	 * @param courseNumber
	 *            String representing the course number
	 * @param sectionNumber
	 *            String representing the section number
	 * @return Key unique section key
	 */
	public Key createSectionKey(String courseNumber, String sectionNumber) {

		return KeyFactory.createKey(SECTION,
				(courseNumber + " " + sectionNumber));
	}

	/*
	 * Helper method
	 */
	private Entity getEntity(String type, String myKey)
			throws EntityNotFoundException {

		Entity lookingFor;
		Transaction txn = ds.beginTransaction();
		try {
			Key staffKey = KeyFactory.createKey(type, myKey);
			lookingFor = ds.get(staffKey);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

		return lookingFor;
	}

	/**
	 * getStaff
	 * 
	 * Returns a staff entity from datastore based off the unique email
	 * identifier
	 * 
	 * @param myKey
	 *            String representing the staff's email address
	 * @return Entity representing the given staff
	 * @throws EntityNotFoundException
	 *             Throws exception if staff does not exist
	 */
	public Entity getStaff(String myKey) throws EntityNotFoundException {
		return getEntity(STAFF, myKey);
	}

	/**
	 * getSection
	 * 
	 * Returns a section entity from datastore based off the unique section
	 * identifier(COURSENUM SECTIONNUM) <br>
	 * Can use createSectionKey() to get key value
	 * 
	 * @param myKey
	 *            String representing the the section
	 * @return Entity representing the given section
	 * @throws EntityNotFoundException
	 *             Throws exception if section does not exist
	 */
	public Entity getSection(String myKey) throws EntityNotFoundException {
		return getEntity(SECTION, myKey);
	}

	/**
	 * getCourse
	 * 
	 * Returns a course entity from datastore based off the unique course
	 * identifier(COURSENUM)
	 * 
	 * @param myKey
	 *            String representing the course number
	 * @return Entity representing the given course
	 * @throws EntityNotFoundException
	 *             Throws exception if course does not exist
	 */
	private Entity getCourse(String myKey) throws EntityNotFoundException {
		return getEntity(COURSE, myKey);
	}

	public ArrayList<Course> getAllCourses() throws EntityNotFoundException {
		ArrayList<Course> courses = new ArrayList<Course>();

		String emailKey = null;
		Query q = new Query(COURSE);
		List<Entity> courseList = ds.prepare(q).asList(
				FetchOptions.Builder.withDefaults());

		for (Entity i : courseList) {
			ArrayList<Section> courseSections = makeDelStringToArrayList(
					emailKey = getOurKey(i.getKey()), i.getProperty(SECTION_LIST).toString());
			Course temp = new Course(i.getProperty(DESIGNATION).toString(), i
					.getProperty(COURSE_TITLE).toString(), courseSections);
			courses.add(temp);
		}

		return courses;
	}
}