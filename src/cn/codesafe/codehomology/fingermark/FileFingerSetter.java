package cn.codesafe.codehomology.fingermark;

import java.util.List;

import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.POJO.FileScan;
import cn.codesafe.codehomology.db.DBTool;
import cn.codesafe.codehomology.db.FileDB;
import cn.codesafe.codehomology.db.FileKeyDB;

public class FileFingerSetter {
	public static void setFileFinger(List<FileScan> scanResult,int vid){
		int kid = -1;
		DBTool.openTran();
		for(FileScan fs:scanResult){
			System.out.println(fs.getPath());
			kid = CodeHomology.hasKey(fs.getMd5());
			if(kid == -1){
				kid = FileKeyDB.insertKey(fs.getMd5());
				CodeHomology.putKey(kid, fs.getMd5());
				FileDB.insertFile(kid, vid, fs.getName(), fs.getPath());
			}else{
				FileDB.insertFile(kid, vid, fs.getName(), fs.getPath());
			}
		}
		DBTool.closeTran();
	}
}
