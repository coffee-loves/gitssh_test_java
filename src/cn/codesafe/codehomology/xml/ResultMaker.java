package cn.codesafe.codehomology.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import cn.codesafe.codehomology.POJO.MatchResult;
import cn.codesafe.codehomology.POJO.Project;
import cn.codesafe.codehomology.POJO.Version;
import cn.codesafe.codehomology.POJO.Vulnerability;

public class ResultMaker {
	public static void  getResult(List<Project> result,String name){
		Element root = new Element("test");
		Document Doc = new Document(root);
		addInfo(root,name,result.size());
		addPro(root,result);
		XMLOutputter XMLOut = new XMLOutputter();
		try {
			XMLOut.output(Doc, new FileOutputStream("e:/test.xml"));
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static void addInfo(Element root,String name,int num){
		Element info = new Element("info");
		root.addContent(info);
		info.addContent(new Element("name").setText(name));
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd  HH:mm:ss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String str = formatter.format(curDate);
		info.addContent(new Element("time").setText(str));
		info.addContent(new Element("matchedproject").setText(num + ""));
	}
	public static void addPro(Element root,List<Project> result){
		Element results = new Element("results");
		root.addContent(results);
		for(Project p:result){
			Element singlere = new Element("result");
			results.addContent(singlere);
			singlere.addContent(new Element("matchedproject").setText(p.getVendor() + ":" + p.getProduct()));
			Element versions = new Element("versions");
			singlere.addContent(versions);
			for(Version v:p.getVers()){
				Element ver = new Element("version");
				versions.addContent(ver);
				ver.addContent(new Element("info").setText(v.getVersion()));
				Element matchs = new Element("matchs");
				ver.addContent(matchs);
				for(MatchResult mr:v.getMatchList()){
					Element match = new Element("match");
					matchs.addContent(match);
					match.addContent(new Element("filepath").setText(mr.fs.getPath()));
					match.addContent(new Element("matchpath").setText(mr.sf.getPath()));
					match.addContent(new Element("matchpercent").setText(mr.per + ""));
				}
				Element Vulnerabilities = new Element("Vulnerabilities");
				ver.addContent(Vulnerabilities);
				for(Vulnerability vul:v.getVulList()){
					Element Vulnerability = new Element("Vulnerability");
					Vulnerabilities.addContent(Vulnerability);
					Vulnerability.addContent(new Element("cve").setText(vul.getCve()));
					Vulnerability.addContent(new Element("descript").setText(vul.getDes()));
					Vulnerability.addContent(new Element("cwe").setText(vul.getCwe()));
					Vulnerability.addContent(new Element("cvssscore").setText(vul.getCvssscore() + ""));
					Vulnerability.addContent(new Element("cvssAccessVector").setText(vul.getCvssAccessVector()));
					Vulnerability.addContent(new Element("cvssAccessComplexity").setText(vul.getCvssAccessComplexity()));
					Vulnerability.addContent(new Element("cvssAuthentication").setText(vul.getCvssAuthentication()));
					Vulnerability.addContent(new Element("cvssConfidentialityImpact").setText(vul.getCvssConfidentialityImpact()));
					Vulnerability.addContent(new Element("cvssIntegrityImpact").setText(vul.getCvssIntegrityImpact()));
					Vulnerability.addContent(new Element("cvssAvailabilityImpact").setText(vul.getCvssAvailabilityImpact()));
				}
			}
		}
	}
}
