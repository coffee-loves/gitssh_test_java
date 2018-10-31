package cn.codesafe.codehomology;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cn.codesafe.codehomology.POJO.FileScan;
import cn.codesafe.codehomology.POJO.MatchInfo;
import cn.codesafe.codehomology.POJO.Project;
import cn.codesafe.codehomology.POJO.SourceFile;
import cn.codesafe.codehomology.POJO.Version;
import cn.codesafe.codehomology.db.DBTool;
import cn.codesafe.codehomology.db.FileKeyDB;
import cn.codesafe.codehomology.db.ProjectDB;
import cn.codesafe.codehomology.db.VersionDB;
import cn.codesafe.codehomology.report.Reporter;
import cn.codesafe.codehomology.scan.Scanner;
import cn.codesafe.codehomology.uploadProject.Upload;
import cn.codesafe.codehomology.utils.Settings;
import cn.codesafe.codehomology.utils.Util;
import cn.codesafe.codehomology.xml.FileMatchResult;
import cn.codesafe.codehomology.xml.ResultMaker;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class CodeHomology {
	public static final String CODEHOMELOGY_HOME;
	static {
		CODEHOMELOGY_HOME = System.getenv("CODEHOMELOGY_HOME");
	}
	private static Map<String,Integer> kMap;
	private static Set<Integer> pList;
	private static Set<Integer> vList;
	private static Map<Integer,Project> proList;
	private static Map<Integer,Version> verList;
	private static Map<String,List<SourceFile>> fileList;
	private static void upLoadInit(){
		pList = ProjectDB.getPidList();
		vList = VersionDB.getVidList();
		kMap = FileKeyDB.getkeys();
		DBTool.close();
	}
	private static void scanInit(){
		proList = ProjectDB.getproList();
		verList = VersionDB.getVerList();
		fileList = FileKeyDB.getIndex();
		DBTool.close();
	}
	public static void main(String[] args){
//		CommandLineParser parser = new BasicParser();
//		Options options = new Options();
//		options.addOption("update", "update", true, "update the sourcecode store");
//		options.addOption("s","scan",true,"the file to scan");
//		options.addOption("n","name",true,"the name of the scan");
//		Settings.init();
//		try {
//			CommandLine commandLine = parser.parse(options, args);
//			if (commandLine.hasOption("update")) {
//				String filePath = commandLine.getOptionValue("update");
//				upLoadInit();
//				Upload.readUploadFile(filePath);
//			}
//			else if(commandLine.hasOption('s')){
//				String scanpath = commandLine.getOptionValue('s');
//				if(commandLine.hasOption('n')){
//					String name = commandLine.getOptionValue('n');
//					scanInit();
//					List<FileScan> result = Scanner.scan("",new File(scanpath));
////					int filenum = result.size();
////					UUID uuid = UUID.randomUUID();
////					String destPath = CODEHOMELOGY_HOME + Settings.getProperty(Settings.TMPDIC) + uuid.toString() + "\\";
////					File destdic = new File(destPath);
////					if(!destdic.exists()){
////						destdic.mkdirs();
////					}
//					List<Project> pros = new ArrayList<>();
////					Map<String,List<MatchInfo>> matchs = new HashMap<>();
//					Scanner.fileCheck(result,pros);
////					String ngramId = Util.getNgramId();
////					if (flag) {
////						Scanner.NgramScan(destdic, ngramId, name);
////						Scanner.doNgramScan(ngramId,pros,matchs);
////					}
////					Util.removedic(destPath);
//					Reporter.mergeVersion(pros);
//					Reporter.reportHomoPro(pros,name);
////					FileMatchResult.getResult(matchs, filenum, name);
//					ResultMaker.getResult(pros, name);
//				}
//			}
//		} catch (ParseException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
		
//		
		Settings.init();
		File profile = new File("D:\\project.xls");
		Set<String> cves = new HashSet<String>();
		Workbook book = null;
		Set<String> set = new HashSet<>();
		// CVE编号
		String vendor = "";
		String product = "";
		List<String> result = null;

		int vsnum = 0;

		Map<String, String> transMap = new HashMap<String, String>();

		try {

			if (profile.exists()) {

				try {
					// 创建Excel对象
					book = Workbook.getWorkbook(profile);
				} catch (BiffException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				Sheet sheet = book.getSheet(0);// 创建第一个sheet对象

				try {
					// 遍历sheet单元格,获取待插入的数据
					for (int infoNum = 1; infoNum < sheet.getRows(); infoNum++) {

						// 读取 vendor 信息
						if (sheet.getCell(3, infoNum).getContents() != null&&sheet.getCell(4, infoNum).getContents() != null) {
							vendor = sheet.getCell(3, infoNum).getContents()
									.trim();
							product = sheet.getCell(4, infoNum).getContents()
									.trim();
						}
						System.out.println(vendor + ":"+ product);
						result = DBTool.getRelCVE(vendor, product);
						if(result != null){
							for(String s:result){
								set.add(s);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 输出最后一个操作成功的数据的SSAPBugTypeID
					// System.out.println(projectName);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		String[] re = set.toArray(new String[set.size()]);
		
		int length = re.length;

		int sheetSize = 60000;

		int sheetNum = 1;

		WritableWorkbook workbook = null;

		if (length % sheetSize > 0) {
			sheetNum = length / sheetSize + 1;
		} else {
			sheetNum = length / sheetSize;
		}
		if (sheetNum == 0) {
			sheetNum = 1;
		}

		try {

			File tempFile = new File("D:\\CVES.xls");
			workbook = Workbook.createWorkbook(tempFile);

			for (int kk = 0; kk < sheetNum; kk++) {
				WritableSheet sheet = workbook.createSheet("list_" + (kk + 1),
						kk);

				sheet.setRowView(0, 400);

				// cveID, cwe_id, sevirity,cvss_score, publishtime,
				// modifiedtime, description
				// 将第一列的宽度设为20
				sheet.setColumnView(0, 10);
				sheet.setColumnView(1, 20);
				sheet.setColumnView(2, 20);
				sheet.setColumnView(3, 20);
				sheet.setColumnView(4, 20);
				
				// ////////////设置标题开始////////////////
				// 设置写入字体
				WritableFont wf = new WritableFont(WritableFont.TIMES, 11,
						WritableFont.BOLD, false);
				// 设置CellFormat
				WritableCellFormat wcfF = new WritableCellFormat(wf);

				// 把水平对齐方式指定为左对齐
				wcfF.setAlignment(jxl.format.Alignment.LEFT);
				// 把垂直对齐方式指定为居中对齐
				wcfF.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				// 设置列名

				// 序号
				Label label0 = new Label(0, 0, "序号", wcfF);
				sheet.addCell(label0);
				// cveid
				Label label1 = new Label(1, 0, "cve", wcfF);
				sheet.addCell(label1);
				
				// ////////////设置标题结束////////////////

				WritableFont wf1 = new WritableFont(WritableFont.TIMES, 11,
						WritableFont.NO_BOLD, false);
				// 设置CellFormat
				WritableCellFormat wcfF2 = new WritableCellFormat(wf1);

				// ///////////循环写excel主体开始////////////
				String slt = null;
				for (int i = kk * sheetSize; i < (kk + 1) * sheetSize; i++) {
					if (i < length) {

						slt = re[i];

						// xls行数
						int xlsline = i + 1 - (kk * sheetSize);

						// 序号
						Label label0show = new Label(0, xlsline, xlsline + "",
								wcfF2);
						sheet.addCell(label0show);
						// cveid
						Label label1show = new Label(1, xlsline, slt,
								wcfF2);
						sheet.addCell(label1show);
					} else {
						break;
					}

				}

				// ///////////循环写excel主体结束////////////
				// ///////创建sheet[kk]结束/////////////
			}

			workbook.write();
			System.out.println(tempFile + " is created!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		String scanpath = "E:\\sourcecode\\test";
//			String name = "aaaa";
//		scanInit();
//		List<FileScan> result = Scanner.scan("", new File(scanpath));
//		// int filenum = result.size();
//		// UUID uuid = UUID.randomUUID();
//		// String destPath = CODEHOMELOGY_HOME +
//		// Settings.getProperty(Settings.TMPDIC) + uuid.toString() + "\\";
//		// File destdic = new File(destPath);
//		// if(!destdic.exists()){
//		// destdic.mkdirs();
//		// }
//		List<Project> pros = new ArrayList<>();
//		// Map<String,List<MatchInfo>> matchs = new HashMap<>();
//		Scanner.fileCheck(result, pros);
//		// String ngramId = Util.getNgramId();
//		// if (flag) {
//		// Scanner.NgramScan(destdic, ngramId, name);
//		// Scanner.doNgramScan(ngramId,pros,matchs);
//		// }
//		// Util.removedic(destPath);
//		Reporter.mergeVersion(pros);
//		Reporter.reportHomoPro(pros, name);
//		// FileMatchResult.getResult(matchs, filenum, name);
//		ResultMaker.getResult(pros, name);
//		
//		
//		Settings.init();
//		String filePath = "E:\\updateoutpute\\16-12-26-1482745875641.zip";
//		upLoadInit();
//		Upload.readUploadFile(filePath);
		
		
		
//		Settings.init();
		
//		upLoadInit();
//		Upload.readUploadFile("E:\\updateoutpute\\16-12-07.zip");
//		Upload.uploadNgram("d:\\",1, 1);
//		scanInit();
//		List<FileScan> result = Scanner.scan("",new File("E:\\sourcecode\\test\\"));
//		UUID uuid = UUID.randomUUID();
//		String destPath = CODEHOMELOGY_HOME + Settings.getProperty(Settings.TMPDIC) + uuid.toString() + "\\";
//		File destdic = new File(destPath);
//		if(!destdic.exists()){
//			destdic.mkdirs();
//		}
//		List<Project> pros = Scanner.fileCheck(result, destdic);
//		String ngramId = Util.getNgramId();
//		Scanner.NgramScan(destdic, ngramId, "aaa");
//		Reporter.reportHomoPro(pros,"aaa");
//		List<Project> result = Scanner.doScan("E:\\updatesource\\apache-ant-1.9.7-src\\");
//		for(Project p:result){
//			for(Version v:p.getVers()){
//				for(MatchResult mr: v.getMatchList()){
//					System.out.println(mr.fs.getPath() + " : " + p.getVendor() + ":" + p.getProduct() + ":" + v.getVersion() + "\t" + mr.sf.getPath());
//				}
//			}
//		}
//		Upload.readUploadFile("E:\\updateoutpute\\16-11-29.zip");
	}
	public static Project getPro(int pid){
		return proList.get(pid);
	}
	public static Version getVer(int vid){
		return verList.get(vid);
	}
	public static List<SourceFile> getFiles(String md5){
		return fileList.get(md5);
	}
	public static boolean hasMd5(String md5){
		return fileList.containsKey(md5);
	}
	public static boolean hasVer(int vid){
		return vList.contains(vid);
	}
	public static boolean hasPro(int pid){
		return pList.contains(pid);
	}
	public static void addPro(int pid){
		pList.add(pid);
	}
	public static void addVer(int vid){
		vList.add(vid);
	}
	public static int hasKey(String key){
		if(kMap.containsKey(key)){
			return kMap.get(key);
		}else{
			return -1;
		}
	}
	public static void putKey(int id,String key){
		kMap.put(key, id);
	}
}
