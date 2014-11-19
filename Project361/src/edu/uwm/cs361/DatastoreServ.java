package edu.uwm.cs361;

import java.util.ArrayList;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class DatastoreServ {
	private DatastoreService ds = null;
	
	public DatastoreServ() {
		ds = DatastoreServiceFactory.getDatastoreService();
	}
	public DatastoreService getDatastore(){
		return ds;
	}
	
	public void createStaff(){
		// TODO
	}
	
	public void createCourse(Course c){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(c);
			pm.makePersistentAll(c.getSections());
		}finally{
			pm.close();
		}
		// TODO
	}
	public void createCourse(ArrayList<Course> cs){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistentAll(cs);
			for(Course c : cs){
				pm.makePersistentAll(c.getSections());
			}
		}finally{
			pm.close();
		}
	}
	public void createSection(Section s){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(s);
		}finally{
			pm.close();
		}
	}
	public void createSection(ArrayList<Section> ss){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistentAll(ss);
		}finally{
			pm.close();
		}
	}
}
