package cn.codesafe.codehomology.db;

import java.util.Map;
import java.util.Set;

import cn.codesafe.codehomology.POJO.Project;

public class ProjectDB{
	public static Set<Integer> getPidList(){
		DBTool db = new DBTool();
		return db.getPidList();
	}
	public static void insertProject(int id,String vendor,String product,String des,String homepage){
		DBTool db = new DBTool();
		db.insertProject(id, vendor, product, des, homepage);
	}
	public static Map<Integer,Project> getproList(){
		DBTool db = new DBTool();
		return db.getProMap();
	}
}
