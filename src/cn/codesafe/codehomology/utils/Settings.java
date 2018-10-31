package cn.codesafe.codehomology.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import cn.codesafe.codehomology.CodeHomology;

public class Settings {
	private static Properties properties = new Properties();
	public static String URL = "db.url";
	public static String USER = "db.user";
	public static String PASSWORD = "db.password";
	public static String DRIVER = "db.driver";
	public static String TMPDIC = "data.tmpdic";
	public static String INDEX = "data.index";
	public static String BASEPATH="data.basepath";
	public static String PID ="index.pid";
	public static String VENDOR = "index.vendor";
	public static String PRODUCT = "index.product";
	public static String PDES = "index.pdes";
	public static String HOMEPAGE = "index.homepage";
	public static String VID = "index.vid";
	public static String VERSION = "index.version";
	public static String VDES = "index.vdes";
	public static String VURL = "index.url";
	public static String FILENAME = "index.filename";
	public static String NGRAMINFOPATH = "scan.ngram.infopath";
	public static String NGRAMUPLOAD = "scan.ngram.filename";
	public static String NGRAMID = "scan.ngram.id";
	public static String NGRAMVERSION = "scan.ngram.version";
	public static String NGRAMSCANPATH = "scan.ngram.path";
	public static String NGRAMSTART = "scan.ngram.start";
	public static String NGRAMEND = "scan.ngram.end";
	public static String NGRAMNAME = "scan.ngram.name";
	public static String NGRAMSCANNAME = "scan.ngram.scanname";
	public static String NGRAMRESPOND = "scan.ngram.respond";
	public static String NGRAMPID = "report.ngram.pid";
	public static String NGRAMVID = "report.ngram.vid";
	public static String NGRAMRESULTPATH = "report.ngram.scanpath";
	public static String NGRAMSOURCEPATH = "report.ngram.sourcepath";
	public static String NGRAMDESTPERCENT = "report.ngram.destpercent";
	public static String NGRAMPROCESS = "report.ngram.process";
	public static void init(){
		try {
			properties.load(new FileInputStream(new File(CodeHomology.CODEHOMELOGY_HOME , "codehomology.properties")));
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
}
