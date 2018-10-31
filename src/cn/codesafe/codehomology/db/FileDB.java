package cn.codesafe.codehomology.db;

public class FileDB {
	public static void insertFile(int kid,int  vid,String name,String path){
		DBTool db = new DBTool();
		db.insertFile(kid, vid, name, path);
	}
}
