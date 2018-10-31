package cn.codesafe.codehomology.POJO;

public class MatchInfo {
	private String path;
	private String project;
	private int percent;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}
	public MatchInfo(String path, String project, int percent) {
		super();
		this.path = path;
		this.project = project;
		this.percent = percent;
	}
	
}
