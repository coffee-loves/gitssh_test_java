package cn.codesafe.codehomology.POJO;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CPE {
	private String vendor;
	private String product;
	private String name;
	private String version;
	private String previousVersion;
	private String revision;
	private String edition;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public boolean hasPreviousVersion() {
        return previousVersion != null;
    }
	
	public String getPreviousVersion() {
		return previousVersion;
	}

	public void setPreviousVersion(String previousVersion) {
		this.previousVersion = previousVersion;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public void setCpe(String cpe) {
        try {
            parseName(cpe);
        } catch (UnsupportedEncodingException ex) {
            setName(cpe);
        }
    }
	
    public void parseName(String cpeName) throws UnsupportedEncodingException {
        this.name = cpeName;
        if (cpeName != null && cpeName.length() > 7) {
            final String[] data = cpeName.substring(7).split(":");
            if (data.length >= 1) {
                this.setVendor(urlDecode(data[0]));
            }
            if (data.length >= 2) {
                this.setProduct(urlDecode(data[1]));
            }
            if (data.length >= 3) {
                version = urlDecode(data[2]);
            }
            if (data.length >= 4) {
                revision = urlDecode(data[3]);
            }
            if (data.length >= 5) {
                edition = urlDecode(data[4]);
            }
        }
    }
	
	private String urlDecode(String string) {
        final String text = string.replace("+", "%2B");
        String result;
        try {
            result = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            try {
                result = URLDecoder.decode(text, "ASCII");
            } catch (UnsupportedEncodingException ex1) {
                result = text;
            }
        }
        return result;
    }

	@Override
	public String toString() {
		return "CpeSoftware [name=" + name + ", version=" + version
				+ ", previousVersion=" + previousVersion + ", revision="
				+ revision + ", edition=" + edition + "]";
	}
}
