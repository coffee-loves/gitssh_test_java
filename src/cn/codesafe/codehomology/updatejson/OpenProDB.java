package cn.codesafe.codehomology.updatejson;

import java.util.List;

import cn.codesafe.codehomology.db.DBTool;

public class OpenProDB {
	public static int insertPro(OpenPro pros) {
		DBTool db = new DBTool();
		db.insertopenpro(pros);
		return db.getpid(pros.getOpenhuburl());
	}
	public static void insertmatch(int pid,String vendor,String product){
		DBTool db = new DBTool();
		db.insertmatch(pid, vendor, product);
	}
	public static void updatestate1(int pid){
		DBTool db = new DBTool();
		db.updatestate1(pid);;
	}
	public static void insertDownload(int pid,String url,String type){
		DBTool db = new DBTool();
		db.insertURL(pid, url, type);
	}
	public static void insertVersion(int pid,String version,String url,String codepath,String standardversion){
		DBTool db = new DBTool();
		db.insertVersion(pid, version, url, codepath, standardversion);
	}
	public static void insertCPE(String cpe,String vendor,String product,String version){
		DBTool db = new DBTool();
		db.insertCPE(cpe, vendor, product, version);
	}
}
