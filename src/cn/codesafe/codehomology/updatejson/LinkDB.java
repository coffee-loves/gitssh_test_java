package cn.codesafe.codehomology.updatejson;

import cn.codesafe.codehomology.db.DBTool;

public class LinkDB {
	public static void insertLink(ProLink pl){
		DBTool db = new DBTool();
		db.insertlink(pl);
	}
}
