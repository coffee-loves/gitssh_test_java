package cn.codesafe.codehomology.POJO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class DependencyVersion implements Comparable<DependencyVersion> {
	private static final Pattern RX_VERSION = Pattern.compile("\\d+(\\.\\d{1,6})+(\\.?([_-](release|beta|alpha|\\d+)|[a-zA-Z_-]{1,3}\\d{0,8}))?");
	private static final Pattern RX_SINGLE_VERSION = Pattern.compile("\\d+(\\.?([_-](release|beta|alpha)|[a-zA-Z_-]{1,3}\\d{1,8}))?");
	private List<String> versionParts;
	public DependencyVersion() {}
	public DependencyVersion(String version) {
        parseVersion(version);
    }
	
	public List<String> getVersionParts() {
		return versionParts;
	}
	public void setVersionParts(List<String> versionParts) {
		this.versionParts = versionParts;
	}
	public final void parseVersion(String version) {
        versionParts = new ArrayList<String>();
        if (version != null) {
            final Pattern rx = Pattern.compile("(\\d+[a-z]{1,3}$|[a-z]+\\d+|\\d+|(release|beta|alpha)$)");
            final Matcher matcher = rx.matcher(version.toLowerCase());
            while (matcher.find()) {
                versionParts.add(matcher.group());
            }
            if (versionParts.isEmpty()) {
                versionParts.add(version);
            }
        }
    }
	public boolean matchesAtLeastThreeLevels(DependencyVersion version) {
        if (version == null) {
            return false;
        }
        if (Math.abs(this.versionParts.size() - version.versionParts.size()) >= 3) {
            return false;
        }

        final int max = (this.versionParts.size() < version.versionParts.size()) ? this.versionParts.size() : version.versionParts.size();

        boolean ret = true;
        for (int i = 0; i < max; i++) {
            final String thisVersion = this.versionParts.get(i);
            final String otherVersion = version.getVersionParts().get(i);
            if (i >= 3) {
                if (thisVersion.compareToIgnoreCase(otherVersion) >= 0) {
                    ret = false;
                    break;
                }
            } else if (!thisVersion.equals(otherVersion)) {
                ret = false;
                break;
            }
        }

        return ret;
    }
	
	public static DependencyVersion toVersion(String text) {
        if (text == null) {
            return null;
        }
        //'-' is a special case used within the CVE entries
        if ("-".equals(text)) {
            final DependencyVersion dv = new DependencyVersion();
            final List<String> list = new ArrayList<String>();
            list.add(text);
            dv.setVersionParts(list);
            return dv;
        }
        String version = null;
        Matcher matcher = RX_VERSION.matcher(text);
        if (matcher.find()) {
            version = matcher.group();
        }
        if (matcher.find()) {
            return null;
        }
        if (version == null) {
            matcher = RX_SINGLE_VERSION.matcher(text);
            if (matcher.find()) {
                version = matcher.group();
            } else {
                return null;
            }
            if (matcher.find()) {
                return null;
            }
        }
        return new DependencyVersion(version);
    }
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DependencyVersion other = (DependencyVersion) obj;
        int max = (this.versionParts.size() < other.versionParts.size()) ? this.versionParts.size() : other.versionParts.size();
        for (int i = 0; i < max; i++) {
            String thisPart = this.versionParts.get(i);
            String otherPart = other.versionParts.get(i);
            if (!thisPart.equals(otherPart)) {
                return false;
            }
        }
        if (this.versionParts.size() > max) {
            for (int i = max; i < this.versionParts.size(); i++) {
                if (!"0".equals(this.versionParts.get(i))) {
                    return false;
                }
            }
        }

        if (other.versionParts.size() > max) {
            for (int i = max; i < other.versionParts.size(); i++) {
                if (!"0".equals(other.versionParts.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
	
	@Override
    public String toString() {
        return StringUtils.join(versionParts.toArray(), ".");
    }
	@Override
	public int compareTo(DependencyVersion version) {
        if (version == null) {
            return 1;
        }
        final List<String> left = this.getVersionParts();
        final List<String> right = version.getVersionParts();
        final int max = left.size() < right.size() ? left.size() : right.size();

        for (int i = 0; i < max; i++) {
            final String lStr = left.get(i);
            final String rStr = right.get(i);
            if (lStr.equals(rStr)) {
                continue;
            }
            try {
                final int l = Integer.parseInt(lStr);
                final int r = Integer.parseInt(rStr);
                if (l < r) {
                    return -1;
                } else if (l > r) {
                    return 1;
                }
            } catch (NumberFormatException ex) {
                final int comp = left.get(i).compareTo(right.get(i));
                if (comp < 0) {
                    return -1;
                } else if (comp > 0) {
                    return 1;
                }
            }
        }
        if (left.size() < right.size()) {
            return -1;
        } else if (left.size() > right.size()) {
            return 1;
        } else {
            return 0;
        }
    }
}
