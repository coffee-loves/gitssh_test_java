package cn.codesafe.codehomology.POJO;

import java.util.ArrayList;
import java.util.List;

public class Project {
	private String vendor;
	private String product;
	private String des;
	private String homepage;
	private List<Version> vers;
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public Project(String vendor, String product, String des, String homepage) {
		super();
		this.vendor = vendor;
		this.product = product;
		this.des = des;
		this.homepage = homepage;
		this.vers = new ArrayList<>();
	}
	public boolean hasVer(Version ver){
		return this.vers.contains(ver);
	}
	public void addVer(Version ver){
		this.vers.add(ver);
	}
	public List<Version> getVers() {
		return vers;
	}
	
}
