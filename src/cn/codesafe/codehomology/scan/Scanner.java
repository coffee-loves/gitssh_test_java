package cn.codesafe.codehomology.scan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.POJO.FileScan;
import cn.codesafe.codehomology.POJO.MatchInfo;
import cn.codesafe.codehomology.POJO.MatchResult;
import cn.codesafe.codehomology.POJO.Project;
import cn.codesafe.codehomology.POJO.SourceFile;
import cn.codesafe.codehomology.POJO.Version;
import cn.codesafe.codehomology.utils.Settings;
import cn.codesafe.codehomology.utils.Util;
import net.sf.json.JSONObject;

public class Scanner {
	public static List<FileScan> scan(String path, File dic){
		System.out.println("scan  " + dic);
		List<FileScan> scanList = new ArrayList<>();
		if(dic.exists()){
			if(dic.isDirectory()){
				File[] files = dic.listFiles();
				for(File f:files){
					if("".equals(path))
						scanList.addAll(scan(f.getName(), f));
					else
						scanList.addAll(scan(path + File.separator + f.getName(), f));
				}
			}
			else{
				try {
					scanList.add(new FileScan(dic.getName(),path,Util.getMD5Checksum(dic),dic.getAbsolutePath()));
				} catch (NoSuchAlgorithmException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		return scanList;
	}
	
	public static void doNgramScan(String ngramId,List<Project> result,Map<String,List<MatchInfo>> matchs){
		File resultFile = new File(Settings.getProperty(Settings.NGRAMINFOPATH),
				Settings.getProperty(Settings.NGRAMRESPOND) + "." + ngramId);
		File postFile = new File(Settings.getProperty(Settings.NGRAMINFOPATH),
				Settings.getProperty(Settings.NGRAMPROCESS) + "." + ngramId);
		while(!resultFile.exists()){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		JSONObject jo = null;
		BufferedReader bf = null;
		String str = null;
		Project pro;
		Version ver;
		FileScan fs = null;
		SourceFile sf = null;
		List<MatchInfo> infos = null;
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
			if((str = bf.readLine()) != null){
				while((str = bf.readLine()) != null){
					jo = JSONObject.fromObject(str);
					if(jo.getString(Settings.getProperty(Settings.NGRAMRESULTPATH)).indexOf('.') < 0)
						continue;
					pro = CodeHomology.getPro(Integer.parseInt(jo.getString(Settings.getProperty(Settings.NGRAMPID))));
					ver = CodeHomology.getVer(Integer.parseInt(jo.getString(Settings.getProperty(Settings.NGRAMVID))));
					if (!result.contains(pro)) {
						result.add(pro);
					}
					if (!pro.hasVer(ver)) {
						pro.addVer(ver);
					}
					fs = new FileScan();
					fs.setPath(jo.getString(Settings.getProperty(Settings.NGRAMRESULTPATH)));
					sf = new SourceFile();
					sf.setPath(jo.getString(Settings.getProperty(Settings.NGRAMSOURCEPATH)));
					ver.addFile(new MatchResult(sf, fs,jo.getInt(Settings.getProperty(Settings.NGRAMDESTPERCENT))));
					if(matchs.keySet().contains(fs.getPath())){
						matchs.get(fs.getPath()).add(new MatchInfo(sf.getPath(),pro.getVendor() + ":" + pro.getProduct() + ":" + ver.getVersion(),jo.getInt(Settings.getProperty(Settings.NGRAMDESTPERCENT))));
					}
					else{
						infos = new ArrayList<>();
						infos.add(new MatchInfo(sf.getPath(),pro.getVendor() + ":" + pro.getProduct() + ":" + ver.getVersion(),jo.getInt(Settings.getProperty(Settings.NGRAMDESTPERCENT))));
						matchs.put(fs.getPath(), infos);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			if(bf != null){
				try {
					bf.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			resultFile.delete();
			postFile.delete();
		}
	}
	
	public static void NgramScan(File dic,String ngramId,String name){
		JSONObject jb = new JSONObject();
		jb.put(Settings.getProperty(Settings.NGRAMID), ngramId);
		jb.put(Settings.getProperty(Settings.NGRAMNAME), name);
		jb.put(Settings.getProperty(Settings.NGRAMSCANPATH), dic.getAbsolutePath());
		File scanFile = new File(Settings.getProperty(Settings.NGRAMINFOPATH),
				Settings.getProperty(Settings.NGRAMSCANNAME) + "." + ngramId);
		FileWriter fos = null;
		BufferedWriter bw = null;
		if (!scanFile.exists()) {
			try {
				fos = new FileWriter(scanFile);
				bw = new BufferedWriter(fos);
				bw.write(jb.toString());
				bw.flush();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
				if(bw != null){
					try {
						bw.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void fileCheck(List<FileScan> scanList,List<Project> result){
		List<SourceFile> sfList;
		Project pro;
		Version ver;
//		List<MatchInfo> infos = null;
		for(FileScan fs:scanList){
			if(CodeHomology.hasMd5(fs.getMd5())){
				sfList = CodeHomology.getFiles(fs.getMd5());
//				infos = new ArrayList<>();
				for(SourceFile sf:sfList){
					ver = CodeHomology.getVer(sf.getVid());
					pro = CodeHomology.getPro(ver.getPid());
					if (!result.contains(pro)) {
						result.add(pro);
					}
					if (!pro.hasVer(ver)) {
						pro.addVer(ver);
					}
					ver.addFile(new MatchResult(sf, fs,100));
					ver.addMatchedFiles(fs);
//					infos.add(new MatchInfo(sf.getPath(),pro.getVendor() + ":" + pro.getProduct() + ":" + ver.getVersion(),100));
//					matchs.put(fs.getPath(),infos);
				}
			}
//			else{
//				try {
//					Util.makefile(new File(dic,fs.getPath()));
//					Util.copyFile(fs.getFilePath(), new File(dic,fs.getPath()).getAbsolutePath());
//					flag = true;
//				} catch (IOException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
//			}
		}
	}
}
