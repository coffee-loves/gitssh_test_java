package cn.codesafe.codehomology.POJO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Version {
	private String version;
	private String des;
	private String url;
	private String codepath;
	private List<MatchResult> matchList;
	private List<Vulnerability> vulList = null;
	private Set<FileScan> matchedFiles = null;
	private int pid;
	
	public boolean containts(FileScan fs){
		return this.matchedFiles.contains(fs);
	}
	public Set<FileScan> getMatchedFiles() {
		return matchedFiles;
	}
	public void addMatchedFiles(FileScan fs) {
		this.matchedFiles.add(fs);
	}
	public List<Vulnerability> getVulList() {
		return vulList;
	}
	public void setVulList(List<Vulnerability> vulList) {
		this.vulList = vulList;
	}
	public void setMatchList(List<MatchResult> matchList) {
		this.matchList = matchList;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCodepath() {
		return codepath;
	}
	public void setCodepath(String codepath) {
		this.codepath = codepath;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public Version(String version, String des, String url, String codepath, int pid) {
		super();
		this.version = version;
		this.des = des;
		this.url = url;
		this.codepath = codepath;
		this.pid = pid;
		this.matchList = new ArrayList<>();
		this.matchedFiles = new HashSet<>();
	}
	public void addFile(MatchResult mr){
		this.matchList.add(mr);
	}
	public List<MatchResult> getMatchList() {
		return matchList;
	}
	
}
