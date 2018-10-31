package cn.codesafe.codehomology.updatejson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.codesafe.codehomology.db.DBTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetJson {
	public static int pid = 1;
	public static Map<String,OpenPro> pros = new HashMap<>();
	public static List<OpenPro> prolist = new ArrayList<>();
	public static List<ProLink> prolink = new ArrayList<>();
	public static List<CodeUrl> urls = new ArrayList<>();
	public static Map<String,Integer> tagIndex = new HashMap<String,Integer>();
	public static void makeJson(String filepath){
		List<JSONObject> pr = new ArrayList<>();
		List<JSONObject> li = new ArrayList<>();
		List<JSONObject> ur = new ArrayList<>();
		String str;
		JSONObject jo;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			while((str = br.readLine()) != null){
				jo = JSONObject.fromObject(str);
				if (jo.containsKey("title"))
					pr.add(jo);
				else if(jo.containsKey("linkprojecturl"))
					li.add(jo);
				else if(jo.containsKey("projecturl"))
					ur.add(jo);
			}
			DBTool db = new DBTool();
			tagIndex = db.getTags();
			System.out.println("pros");
			for(JSONObject j:pr){
				getpros(j);
			}
			System.out.println("link");
			for(JSONObject j:li){
				addlink(j);
			}
			System.out.println("url");
			for(JSONObject j:ur){
				addcode(j);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private static void getpros(JSONObject jo){
		String title = jo.getString("title").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
		System.out.println(title);
		String url = jo.getString("url").trim();
		String info = jo.getString("info").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
		String summary = jo.getString("summary").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
		JSONArray ja =  jo.getJSONArray("batch");
		List<String> tags = new ArrayList<String>();
		for(int i = 1;i < ja.size();i ++){
			tags.add(((JSONObject)ja.get(i)).getString("tags").trim());
		}
		addpro(title,url,info,summary,tags);
	}
	private static void addpro(String title,String url,String info,String summary,List<String> tags){
		OpenPro op = new OpenPro(pid,title,tags,summary,info,url);
		if(pros.containsKey(url)){
			return;
		}
		int id = OpenProDB.insertPro(op);
		if (id != 0) {
			op.setPid(id);
			TagDB.insertTag(id, tags, tagIndex);
			pros.put(url, op);
			prolist.add(op);
			pid++;
		}
	}
	private static void addlink(JSONObject jo){
		String prourl = jo.getString("linkprojecturl").trim();
		OpenPro op = pros.get(prourl);
		if(op != null){
			int id = op.getPid();
			JSONArray ja =  jo.getJSONArray("batch");
			String name;
			String link;
			ProLink pl = null;
			for(int i = 0;i < ja.size();i ++){
				name = ((JSONObject)ja.get(i)).getString("name").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				link = ((JSONObject)ja.get(i)).getString("links").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				pl = new ProLink(id,name,link);
				LinkDB.insertLink(pl);
				prolink.add(pl);
			}
		}else{
			System.out.println("droplink : " + prourl);
		}
	}
	private static void addcode(JSONObject jo){
		String prourl = jo.getString("projecturl").trim();
		OpenPro op = pros.get(prourl);
		if(op != null){
			int id = op.getPid();
			JSONArray ja =  jo.getJSONArray("batch");
			String url;
			String type;
			String status;
			String ignor;
			CodeUrl cu = null;
			for(int i = 0;i < ja.size();i ++){
				url = ((JSONObject)ja.get(i)).getString("locurl").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				type = ((JSONObject)ja.get(i)).getString("loctype").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				status = ((JSONObject)ja.get(i)).getString("locstatus").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				ignor = ((JSONObject)ja.get(i)).getString("locignore").replaceAll("(\n|\\[br\\]|\\[p\\])+", " ").trim();
				cu = new CodeUrl(id,url,type,status,ignor);
				CodeUrlDB.insertCode(cu);
				urls.add(cu);
			}
		}else{
			System.out.println("dropdownload : " + prourl);
		}
	}
}
