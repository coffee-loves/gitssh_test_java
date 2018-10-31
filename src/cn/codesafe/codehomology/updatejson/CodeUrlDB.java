package cn.codesafe.codehomology.updatejson;

import cn.codesafe.codehomology.db.DBTool;

public class CodeUrlDB {
	public static void insertCode(CodeUrl cu){
		DBTool db = new DBTool();
		db.insertcode(cu);
	}
}
