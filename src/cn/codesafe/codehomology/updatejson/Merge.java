package cn.codesafe.codehomology.updatejson;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Merge {
	public static void insert(){
		int pid = 30;
		String vendor = "mysql";
		String product = "mysql";
		OpenProDB.insertmatch(pid, vendor, product);
	}
	public static void state1(){
		int pid = 30;
		OpenProDB.updatestate1(pid);
	}
	public static void insertdownload(){
		int pid = 30;
		String url = "https://github.com/mysql/mysql-server";
		String type = "Git";
		OpenProDB.insertDownload(pid, url, type);
	}
	public static void insertVersion(){
		int pid = 30;
		String version = "5_6_35";
		String url = "https://github.com/mysql/mysql-server";
		String codepath = "git/mysql-server-mysql-5.6.35.zip";
		String standardversion = "5.6.35";
		OpenProDB.insertVersion(pid, version, url, codepath, standardversion);
	}
	
	private static String urlDecode(String string) {
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
	public static void getcpe(){
		String path = "D:/cpes.xml";
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		String vendor = "";
		String product = "";
		String version = "";
		String revision = "";
		int i = 0;
		try {
			System.out.println("aaa");
			doc = builder.build(new File(path));
			System.out.println("aaa");
			Element rootEl = doc.getRootElement();
			System.out.println(rootEl.getName());
			System.out.println("aaa");
			List<Element> list = rootEl.getChildren();
			System.out.println("aaa");
			System.out.println(list.size());
			for (Element el : list) {
				if ("cpe-item".equals(el.getName())) {
					String name = el.getAttributeValue("name");
					if (name != null && name.length() > 7) {
						final String[] data = name.split(":");
						if (data.length >= 3) {
							vendor = urlDecode(data[2]);
						}
						if (data.length >= 4) {
							product = urlDecode(data[3]);
						}
						if (data.length >= 5) {
							version = urlDecode(data[4]);
						}
						if (data.length >= 6) {
							revision = urlDecode(data[5]);
						}
						if(revision != null&&!revision.isEmpty())
							version = version + "." + revision;
						OpenProDB.insertCPE(name, vendor, product, version);
					}
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
}
