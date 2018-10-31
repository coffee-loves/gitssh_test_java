package cn.codesafe.codehomology.updatejson;

public class ProCpe {
	private String vendor;
	private String product;
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
	public ProCpe(String vendor, String product) {
		super();
		this.vendor = vendor;
		this.product = product;
	}
}
