package cn.codesafe.codehomology.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.codesafe.codehomology.POJO.CPE;
import cn.codesafe.codehomology.POJO.DependencyVersion;
import cn.codesafe.codehomology.POJO.FileScan;
import cn.codesafe.codehomology.POJO.Project;
import cn.codesafe.codehomology.POJO.Version;
import cn.codesafe.codehomology.db.CodeDB;
import cn.codesafe.codehomology.utils.Util;
import cn.codesafe.codehomology.xml.ResultMaker;

public class Reporter {
	public static void mergeVersion(List<Project> result){
		List<Version> delete;
		List<Version> vList;
		int del = 0;
		for(Project p:result){
			delete = new ArrayList<Version>();
			vList = p.getVers();
			for(int i = 0;i < vList.size() - 1;i ++){
				if(delete.contains(vList.get(i)))
					continue;
				for(int j = i + 1;j < vList.size();j ++){
					if(delete.contains(vList.get(j)))
						continue;
					del = getDelete(vList.get(i),vList.get(j));
					if(del > 0){
						delete.add(vList.get(j));
					}else if(del < 0){
						delete.add(vList.get(i));
						break;
					}
				}
			}
			p.getVers().removeAll(delete);
		}
	}
	public static int getDelete(Version v1,Version v2){
		boolean matched = true;
		if(v1.getMatchList().size() == v2.getMatchList().size()){
			return 0;
		}else if(v1.getMatchList().size() > v2.getMatchList().size()){
			for(FileScan fs:v2.getMatchedFiles()){
				if(!v1.containts(fs)){
					matched = false;
					break;
				}
			}
			if(matched){
				return 1;
			}else{
				return 0;
			}
		}else{
			for(FileScan fs:v1.getMatchedFiles()){
				if(!v2.containts(fs)){
					matched = false;
					break;
				}
			}
			if(matched){
				return -1;
			}else{
				return 0;
			}
		}
	}
	public static void reportHomoPro(List<Project> result,String name){
		DependencyVersion bestGuess = new DependencyVersion("-");
		List<String> vendors = null;
		CodeDB db = null;
		String matchVen;
		String matchcpe = null;
		try {
			db = new CodeDB();
			for(Project p:result){
				vendors = db.getcpes(p.getProduct());
				matchVen = getVendor(p.getProduct(),p.getVendor(),vendors);
				if (matchVen != null)
					p.setVendor(matchVen);
				if (matchVen != null) {
					Set<CPE> cpes = db.getCpes(matchVen, p.getProduct());
					for (Version v : p.getVers()) {
						matchcpe = null;
						DependencyVersion proofVer = DependencyVersion.toVersion(v.getVersion());
						for(CPE cpe:cpes){
							DependencyVersion dbVer;
							if(cpe.getRevision() != null && !cpe.getRevision().isEmpty()) {
								dbVer = DependencyVersion.toVersion(cpe.getVersion() + "." + cpe.getRevision());
							} else {
								dbVer = DependencyVersion.toVersion(cpe.getVersion());
							}
							if (dbVer == null) {
								matchcpe = cpe.getName();
							} else if(proofVer.equals(dbVer)){//equals
								v.setVulList(db.getCVEs(cpe.getName()));
								break;
							}else{
								if (proofVer.getVersionParts().size() <= dbVer.getVersionParts().size()
										&& proofVer.matchesAtLeastThreeLevels(dbVer)) {
									if (bestGuess.getVersionParts().size() < dbVer.getVersionParts().size()) {
										bestGuess = dbVer;
									}
								}
							}
						}
						if(v.getVulList() == null && matchcpe != null){
							v.setVulList(db.getCVEs(matchcpe));
						}else if(v.getVulList() == null){
							if(bestGuess.getVersionParts().size() < proofVer.getVersionParts().size()) {
								bestGuess = proofVer;
							}
							String cpeName = String.format("cpe:/a:%s:%s:%s", p.getVendor(), p.getProduct(), bestGuess.toString());
							v.setVulList(db.getCVEs(cpeName));
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		ResultMaker.getResult(result, name);
	}
	private static String getVendor(String product,String vendor,List<String> vendors){
		double maxper = 0;
		String match = null;
		double len = 0;
		if(vendor.equals(product) && vendors.size() == 1)
			return vendors.get(0);
		else{
			for(String ven:vendors){
				len = (double)Util.getLCString(ven.toCharArray(), vendor.toCharArray());
				if(ven.length() > vendor.length()){
					if(len / vendor.length() > maxper && len / vendor.length() > 0.5d){
						maxper = len / vendor.length();
						match = ven;
					}
				}else{
					if(len / ven.length() > maxper && len / ven.length() > 0.5d){
						maxper = len / ven.length();
						match = ven;
					}
				}
			}
			return match;
		}
		
	}
	public static void reportHomoPer(){
		
	}
}
