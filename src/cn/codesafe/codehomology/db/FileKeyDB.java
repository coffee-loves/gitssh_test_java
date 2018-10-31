package cn.codesafe.codehomology.db;

import java.util.List;
import java.util.Map;

import cn.codesafe.codehomology.POJO.SourceFile;

public class FileKeyDB {
	public static Map<String,Integer> getkeys(){
		DBTool db = new DBTool();
		return db.getKeys();
	}
	public static int insertKey(String key){
		DBTool db = new DBTool();
		db.insertKey(key);
		return db.getKeyId(key);
	} 
	public static Map<String,List<SourceFile>> getIndex(){
		DBTool db = new DBTool();
		return db.getIndex();
	}
}
