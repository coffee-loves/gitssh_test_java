package cn.codesafe.codehomology.POJO;

public class SourceFile {
	private String name;
	private String path;
	private int vid;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public SourceFile(String name, String path, int vid) {
		super();
		this.name = name;
		this.path = path;
		this.vid = vid;
	}
	public SourceFile(){}
}
