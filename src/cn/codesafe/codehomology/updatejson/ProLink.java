package cn.codesafe.codehomology.updatejson;

public class ProLink {
	int pid;
	String name;
	String url;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ProLink(int pid, String name, String url) {
		super();
		this.pid = pid;
		this.name = name;
		this.url = url;
	}
	
}
