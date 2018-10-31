package cn.codesafe.codehomology.POJO;

public class FileScan {
	private String name;
	private String path;
	private String md5;
	private String filePath;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
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
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public FileScan(String name, String path, String md5,String filePath) {
		super();
		this.name = name;
		this.path = path;
		this.md5 = md5;
		this.filePath = filePath;
	}
	public FileScan(){}
}
