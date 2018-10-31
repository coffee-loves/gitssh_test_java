package cn.codesafe.codehomology.updatejson;

public class CodeUrl {
	int pid;
	String url;
	String  type;
	String state;
	String ingorefile;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIngorefile() {
		return ingorefile;
	}
	public void setIngorefile(String ingorefile) {
		this.ingorefile = ingorefile;
	}
	public CodeUrl(int pid, String url, String type, String state, String ingorefile) {
		super();
		this.pid = pid;
		this.url = url;
		this.type = type;
		this.state = state;
		this.ingorefile = ingorefile;
	}
	
}
