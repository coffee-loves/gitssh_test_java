package cn.codesafe.codehomology.updatejson;

import java.util.ArrayList;
import java.util.List;

public class OpenPro {
	int pid;
	String title;
	List<String> tags;
	String summary;
	String info;
	String openhuburl;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getTags() {
		return tags;
	}
	public void addTag(String tag) {
		this.tags.add(tag);
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getOpenhuburl() {
		return openhuburl;
	}
	public void setOpenhuburl(String openhuburl) {
		this.openhuburl = openhuburl;
	}
	public OpenPro(int pid, String title,List<String> tags, String summary, String info, String openhuburl) {
		super();
		this.pid = pid;
		this.title = title;
		this.tags = tags;
		this.summary = summary;
		this.info = info;
		this.openhuburl = openhuburl;
	}
	
}
