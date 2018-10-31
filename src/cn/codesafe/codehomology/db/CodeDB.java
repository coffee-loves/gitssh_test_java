package cn.codesafe.codehomology.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.POJO.CPE;
import cn.codesafe.codehomology.POJO.DependencyVersion;
import cn.codesafe.codehomology.POJO.Vulnerability;
import cn.codesafe.codehomology.utils.Settings;


public class CodeDB {
	private static Properties statementBundle = new Properties();
	private Connection conn;
	static{
		try {
			Class.forName(Settings.getProperty(Settings.DRIVER));
			statementBundle.load(new FileInputStream(new File(CodeHomology.CODEHOMELOGY_HOME,"dbsql.properties")));
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public CodeDB() throws Exception {
		open();
	}

	public final void open() throws Exception {
		if (!isOpen()) {
			conn = CodeConnectionFactory.getConnection();
		}
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isOpen() {
		return conn != null;
	}

	protected Connection getConnection() {
		return conn;
	}

	public void initHashEntry() {
		PreparedStatement create = null;
		try {
			create = getConnection().prepareStatement("CREATE TABLE hashEntry (id INT auto_increment PRIMARY KEY, cpe VARCHAR(250), md5sum VARCHAR(64), sha1sum VARCHAR(64));");
			create.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (create != null) {
				try {
					create.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	private DependencyVersion parseDependencyVersion(CPE cpes) {
		DependencyVersion cpeVersion;
		if (cpes.getVersion() != null && !cpes.getVersion().isEmpty()) {
			String versionText;
			if (cpes.getRevision() != null && !cpes.getRevision().isEmpty()) {
				versionText = String.format("%s.%s", cpes.getVersion(), cpes.getRevision());
			} else {
				versionText = cpes.getVersion();
			}
			cpeVersion = DependencyVersion.toVersion(versionText);
		} else {
			cpeVersion = new DependencyVersion("-");
		}
		return cpeVersion;
	}
	
	private DependencyVersion parseDependencyVersion(String cpeStr) {
		CPE cpe = new CPE();
		try {
			cpe.parseName(cpeStr);
		} catch (UnsupportedEncodingException ex) {
			//
		}
		return parseDependencyVersion(cpe);
	}
	
	Entry<String, Boolean> getMatchingSoftware(Map<String, Boolean> vulnerableSoftware, String vendor, String product, DependencyVersion reasonVersion) {
		boolean isVersionTwoADifferentProduct = "apache".equals(vendor) && "struts".equals(product);
		Set<String> majorVersionsAffectingAllPrevious = new HashSet<String>();
		boolean matchesAnyPrevious = reasonVersion == null || "-".equals(reasonVersion.toString());
		String majorVersionMatch = null;
		for (Entry<String, Boolean> entry : vulnerableSoftware.entrySet()) {
			DependencyVersion v = parseDependencyVersion(entry.getKey());
			if (v == null || "-".equals(v.toString())) { // all versions
				return entry;
			}
			if (entry.getValue()) {
				if (matchesAnyPrevious) {
					return entry;
				}
				if (reasonVersion != null && reasonVersion.getVersionParts().get(0).equals(v.getVersionParts().get(0))) {
					majorVersionMatch = v.getVersionParts().get(0);
				}
				majorVersionsAffectingAllPrevious.add(v.getVersionParts().get(0));
			}
		}
		if (matchesAnyPrevious) {
			return null;
		}

		final boolean canSkipVersions = majorVersionMatch != null && majorVersionsAffectingAllPrevious.size() > 1;
		for (Entry<String, Boolean> entry : vulnerableSoftware.entrySet()) {
			if (!entry.getValue()) {
				DependencyVersion v = parseDependencyVersion(entry.getKey());
				if (canSkipVersions && !majorVersionMatch.equals(v.getVersionParts().get(0))) {
					continue;
				}
				if (reasonVersion.equals(v)) {
					return entry;
				}
			}
		}
		for (Entry<String, Boolean> entry : vulnerableSoftware.entrySet()) {
			if (entry.getValue()) {
				DependencyVersion v = parseDependencyVersion(entry.getKey());
				if (canSkipVersions && !majorVersionMatch.equals(v.getVersionParts().get(0))) {
					continue;
				}
				if (entry.getValue() && reasonVersion.compareTo(v) <= 0) {
					if (!(isVersionTwoADifferentProduct && !reasonVersion.getVersionParts().get(0).equals(v.getVersionParts().get(0)))) {
						return entry;
					}
				}
			}
		}
		return null;
	}
	
	public Vulnerability getCVE(String cveName) {
		PreparedStatement psV = null;
		PreparedStatement psR = null;
		PreparedStatement psS = null;
		ResultSet rsV = null;
		ResultSet rsR = null;
		ResultSet rsS = null;
		Vulnerability cve = null;

		try {
			System.out.println(cveName);
			psV = getConnection().prepareStatement(statementBundle.getProperty("SELECT_VULNERABILITY"));
			psV.setString(1, cveName);
			rsV = psV.executeQuery();
			if (rsV.next()) {
				cve = new Vulnerability();
				cve.setCve(cveName);
				cve.setDes(rsV.getString(2));
				String cwe = rsV.getString(3);
				cve.setCwe(cwe);
				cve.setCvssscore(rsV.getFloat(4));// lijiaming:okay , it comes
													// from the database.
				cve.setCvssAccessVector(rsV.getString(5));
				cve.setCvssAccessComplexity(rsV.getString(6));
				cve.setCvssAuthentication(rsV.getString(7));
				cve.setCvssConfidentialityImpact(rsV.getString(8));
				cve.setCvssIntegrityImpact(rsV.getString(9));
				cve.setCvssAvailabilityImpact(rsV.getString(10));
				cve.setPublisheddatetime(rsV.getString(11));
				cve.setModifieddatetime(rsV.getString(12));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rsV != null) {
				try {
					rsV.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (rsR != null) {
				try {
					rsR.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (rsS != null) {
				try {
					rsS.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (psV != null) {
				try {
					psV.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (psR != null) {
				try {
					psR.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (psS != null) {
				try {
					psS.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}

		return cve;
	}
	
	public List<Vulnerability> getCVEs(String cpeName) {
		CPE cpes = new CPE();
		try {
			cpes.parseName(cpeName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		DependencyVersion resolvedVer = parseDependencyVersion(cpes);
		List<Vulnerability> cves = new ArrayList<Vulnerability>();

		try {
			ps = getConnection().prepareStatement(statementBundle.getProperty("SELECT_CVE_FROM_SOFTWARE"));
			ps.setString(1, cpes.getVendor());
			ps.setString(2, cpes.getProduct());
			rs = ps.executeQuery();
			String currentCVE = "";
			Map<String, Boolean> vulnSoftware = new HashMap<String, Boolean>();
			while (rs.next()) {
				String cve = rs.getString(1);
				if (!currentCVE.equals(cve)) {
					Entry<String, Boolean> matchedCPE = getMatchingSoftware(vulnSoftware, cpes.getVendor(), cpes.getProduct(), resolvedVer);
					if (matchedCPE != null) {
						final Vulnerability v = getCVE(currentCVE);
						cves.add(v);// lijiaming:found a vulnerability
					}
					vulnSoftware.clear();
					currentCVE = cve;
				}
				String cpe = rs.getString(2);
				String previous = rs.getString(3);
				Boolean p = previous != null && !previous.isEmpty();
				vulnSoftware.put(cpe, p);
			}
			Entry<String, Boolean> matchedCPE = getMatchingSoftware(vulnSoftware, cpes.getVendor(), cpes.getProduct(), resolvedVer);
			if (matchedCPE != null) {
				Vulnerability v = getCVE(currentCVE);
				cves.add(v);// lijiaming:found a vulnerability
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}

		}
		return cves;
	}
	
	public Set<CPE> getCpes(String vendor, String product) {
		Set<CPE> cpes = new HashSet<CPE>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statementBundle.getProperty("SELECT_CPE_ENTRIES"));
			ps.setString(1, vendor);
			ps.setString(2, product);
			rs = ps.executeQuery();

			while (rs.next()) {
				CPE cve = new CPE();
				cve.setCpe(rs.getString(1));
				cpes.add(cve);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return cpes;
	}
	
	public List<String> getcpes(String product){
		List<String> vendors = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statementBundle.getProperty("SELECT_VENDOR"));
			ps.setString(1, product);
			rs = ps.executeQuery();
			while(rs.next()){
				vendors.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		return vendors;
	}
	public void test(){
		try {
			PreparedStatement ps = getConnection().prepareStatement("select count(*) from CPEENTRY");
			ResultSet rs = 
			ps.executeQuery();
			rs.next();
			System.out.println(rs.getInt(1));
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
}
