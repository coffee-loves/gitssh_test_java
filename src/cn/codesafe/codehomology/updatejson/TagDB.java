package cn.codesafe.codehomology.updatejson;

import java.util.List;
import java.util.Map;

import cn.codesafe.codehomology.db.DBTool;

public class TagDB {
	public static void insertTag(int pid, List<String> tags, Map<String, Integer> index) {
		DBTool db = new DBTool();
		for (String tag : tags) {
			if (index.containsKey(tag)) {
				db.insertSign(pid, index.get(tag));
			} else {
				db.insertTag(tag);
				int id = db.getTid(tag);
				if(id != 0){
					db.insertSign(pid, id);
					index.put(tag, id);
				}
			}
		}
	}
}
