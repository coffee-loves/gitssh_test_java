package cn.codesafe.codehomology.POJO;

import java.io.File;

public class UploadInfo {
	private File dic;
	private String filename;
	private int vid;
	private int pid;
	public File getDic() {
		return dic;
	}
	public void setDic(File dic) {
		this.dic = dic;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public UploadInfo(File dic, String filename, int vid, int pid) {
		super();
		this.dic = dic;
		this.filename = filename;
		this.vid = vid;
		this.pid = pid;
	}
}
