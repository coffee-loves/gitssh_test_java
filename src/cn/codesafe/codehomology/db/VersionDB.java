package cn.codesafe.codehomology.db;

import java.util.Map;
import java.util.Set;

import cn.codesafe.codehomology.POJO.Version;

public class VersionDB {
	public static Set<Integer> getVidList(){
		DBTool db = new DBTool();
		return db.getVidList();
	}
	public static void insertVersion(int id,int pid,String version,String url,String des,String codepath){
		DBTool db = new DBTool();
		db.insertVersion(id, pid, version, url, des, codepath);;
	}
	public static Map<Integer,Version> getVerList(){
		DBTool db = new DBTool();
		return db.getVerMap();
	}
}
