package cn.codesafe.codehomology.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import cn.codesafe.codehomology.POJO.MatchInfo;

public class FileMatchResult {
	public static void getResult(Map<String,List<MatchInfo>> matchs,int fileamount,String name){
		Element root = new Element("test");
		Document Doc = new Document(root);
		addInfo(root,name,fileamount);
		addPro(root,matchs);
		XMLOutputter XMLOut = new XMLOutputter();
		try {
			XMLOut.output(Doc, new FileOutputStream("e:/percent.xml"));
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static void addPro(Element root,Map<String,List<MatchInfo>> matchs){
		Element results = new Element("matches");
		root.addContent(results);
		for(String filepath:matchs.keySet()){
			Element singlere = new Element("match");
			results.addContent(singlere);
			singlere.addContent(new Element("filepath").setText(filepath));
			Element versions = new Element("matchedfiles");
			singlere.addContent(versions);
			for(MatchInfo mi:matchs.get(filepath)){
				Element ver = new Element("matchedfile");
				versions.addContent(ver);
				ver.addContent(new Element("project").setText(mi.getProject()));
				ver.addContent(new Element("matchedpath").setText(mi.getPath()));
				ver.addContent(new Element("percent").setText(mi.getPercent() + ""));
			}
		}
	}
	public static void addInfo(Element root,String name,int num){
		Element info = new Element("info");
		root.addContent(info);
		info.addContent(new Element("name").setText(name));
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd  HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);
		info.addContent(new Element("time").setText(str));
		info.addContent(new Element("fileamount").setText(num + ""));
	}
}
