package cn.codesafe.codehomology.uploadProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.POJO.FileScan;
import cn.codesafe.codehomology.POJO.UploadInfo;
import cn.codesafe.codehomology.db.DBTool;
import cn.codesafe.codehomology.db.ProjectDB;
import cn.codesafe.codehomology.db.VersionDB;
import cn.codesafe.codehomology.fingermark.FileFingerSetter;
import cn.codesafe.codehomology.scan.Scanner;
import cn.codesafe.codehomology.utils.Settings;
import cn.codesafe.codehomology.utils.Util;
import net.sf.json.JSONObject;

public class Upload {
	public static void readUploadFile(String path){
		if(!new File(path).exists()){
			return;
		}
		UUID uuid = UUID.randomUUID();
		String destPath = CodeHomology.CODEHOMELOGY_HOME + Settings.getProperty(Settings.TMPDIC) + uuid.toString() + "\\";
		File destdic = new File(destPath);
		if(!destdic.exists()){
			destdic.mkdirs();
		}
		try {
			Util.extract(new File(path),new File(destPath));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		File indexFile = new File(destPath + Settings.getProperty(Settings.INDEX));
		if(indexFile.exists()){
			BufferedReader bf = null;
			List<JSONObject> jList = new ArrayList<>();
			String jstr = null;
			try {
				bf = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile)));
				while((jstr = bf.readLine()) != null){
					jList.add(JSONObject.fromObject(jstr));
				}
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} finally{
				try {
					bf.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			List<FileScan> scanResult = new ArrayList<>();
			List<UploadInfo> dics = new ArrayList<>(); 
			for(JSONObject j:jList){
				if(!CodeHomology.hasVer(j.getInt(Settings.getProperty(Settings.VID)))){
					File f = new File(destPath + j.getString(Settings.getProperty(Settings.FILENAME)));
					if(f.exists()){
						try {
							Util.copyFile(f.getAbsolutePath(), Settings.getProperty(Settings.BASEPATH) + f.getName());
							File dic = new File(destPath + Util.getFileRealName(j.getString(Settings.getProperty(Settings.FILENAME))) + "\\");
							if (dic.mkdirs()) {
								Util.extract(f, dic);
								if (!CodeHomology.hasPro(j.getInt(Settings.getProperty(Settings.PID)))) {
									ProjectDB.insertProject(j.getInt(Settings.getProperty(Settings.PID)),
											j.getString(Settings.getProperty(Settings.VENDOR)),
											j.getString(Settings.getProperty(Settings.PRODUCT)),
											j.getString(Settings.getProperty(Settings.PDES)),
											j.getString(Settings.getProperty(Settings.HOMEPAGE)));
									CodeHomology.addPro(j.getInt(Settings.getProperty(Settings.PID)));
								}
								VersionDB.insertVersion(j.getInt(Settings.getProperty(Settings.VID)),
										j.getInt(Settings.getProperty(Settings.PID)),
										j.getString(Settings.getProperty(Settings.VERSION)),
										j.getString(Settings.getProperty(Settings.VURL)),
										j.getString(Settings.getProperty(Settings.VDES)),
										Settings.getProperty(Settings.BASEPATH) + f.getName());
								CodeHomology.addVer(j.getInt(Settings.getProperty(Settings.VID)));
								dics.add(new UploadInfo(dic, j.getString(Settings.getProperty(Settings.FILENAME)), j.getInt(Settings.getProperty(Settings.VID)), j.getInt(Settings.getProperty(Settings.PID))));
							}
							
						} catch (IOException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
					
				}
			}
//			File uploadFile = uploadNgram(dics,destPath);
			for(UploadInfo ui:dics){
				scanResult = Scanner.scan("", ui.getDic());
				FileFingerSetter.setFileFinger(scanResult,
						ui.getVid());
			}
//			while(uploadFile.exists()){
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
//			}
			DBTool.close();
		}else{
			System.out.println("error");
		}
		Util.removedic(destPath);
	}
	
	public static File uploadNgram(List<UploadInfo> dic, String destPath) {
		long time = new Date().getTime();
		File uploadFile = new File(Settings.getProperty(Settings.NGRAMINFOPATH),
				Settings.getProperty(Settings.NGRAMUPLOAD) + "." + time);
		FileWriter fos = null;
		BufferedWriter bw = null;
		try {
			fos = new FileWriter(uploadFile);
			bw = new BufferedWriter(fos);
			bw.write(Settings.getProperty(Settings.NGRAMSTART));
			bw.newLine();
			for (UploadInfo ui : dic) {
				JSONObject info = new JSONObject();
				info.put(Settings.getProperty(Settings.NGRAMID), ui.getPid() + "");
				info.put(Settings.getProperty(Settings.NGRAMVERSION), ui.getVid() + "");
				info.put(Settings.getProperty(Settings.NGRAMSCANPATH),
						(destPath + Util.getFileRealName(ui.getFilename()) + "\\").replaceAll("/", "\\"));
				bw.write(info.toString());
				bw.newLine();
			}
			bw.write(Settings.getProperty(Settings.NGRAMEND));
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		return uploadFile;
	}
}
