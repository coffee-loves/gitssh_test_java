package cn.codesafe.codehomology.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.POJO.Project;
import cn.codesafe.codehomology.POJO.SourceFile;
import cn.codesafe.codehomology.POJO.Version;
import cn.codesafe.codehomology.updatejson.CodeUrl;
import cn.codesafe.codehomology.updatejson.OpenPro;
import cn.codesafe.codehomology.updatejson.ProLink;
import cn.codesafe.codehomology.utils.Settings;



public class DBTool {
	
	private static Properties statementBundle = new Properties();
	
	private static Connection conn = null;
	
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
	
	
	public static List<String> getRelCVE(String vendor,String product){
		List<String> result = new ArrayList<>();
		String sql = "SELECT cve FROM ch_software INNER JOIN ch_vulnerability ON ch_vulnerability.id = ch_software.cveid INNER JOIN ch_cpe_entry ON ch_cpe_entry.id = ch_software.cpeEntryId WHERE vendor = ? AND product = ? ";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(sql);
			pst.setString(1, vendor);
			pst.setString(2, product);
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString(1) != null&&!"".equals(rs.getString(1)))
					result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static Connection getConn(){
		try {
			if(conn == null||conn.isClosed()){
				conn = DriverManager.getConnection(Settings.getProperty(Settings.URL),Settings.getProperty(Settings.USER),Settings.getProperty(Settings.PASSWORD));
				return conn;
			}
			else{
				return conn;
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	public Map<String,Integer> getTags(){
		Map<String,Integer> tagIndex = new HashMap<String,Integer>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_TAG"));
			rs = pst.executeQuery();
			while(rs.next()){
				tagIndex.put(rs.getString(2), rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(rs, pst);
		}
		return tagIndex;
	}
	public void insertTag(String tag){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_TAG"));
			pst.setString(1, tag);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("tag : " + tag);
		}finally{
			closeps(null, pst);
		}
	}
	public int getTid(String tag){
		PreparedStatement pst = null;
		ResultSet rs = null;
		int id = 0;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_TID"));
			pst.setString(1, tag);
			rs = pst.executeQuery();
			if(rs.next())
				id = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("tag : " + tag);
			e.printStackTrace();
		}finally{
			closeps(null, pst);
		}
		return id;
	}
	public void insertSign(int pid,int tid){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_SIGN"));
			pst.setInt(1, pid);
			pst.setInt(2, tid);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(null, pst);
		}
	}
	public void insertopenpro(OpenPro pro){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_OPENPRO"));
			pst.setString(1, pro.getTitle());
			pst.setString(2, pro.getSummary());
			pst.setString(3, pro.getInfo());
			pst.setString(4, pro.getOpenhuburl());
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(null, pst);
		}
	}
	public void insertlink(ProLink pl){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_LINK"));
			pst.setInt(1, pl.getPid());
			pst.setString(2, pl.getName());
			pst.setString(3, pl.getUrl());
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(null, pst);
		}
	}
	public void insertcode(CodeUrl cu){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_CODE_URL"));
			pst.setInt(1, cu.getPid());
			pst.setString(2, cu.getUrl());
			pst.setString(3, cu.getType());
			pst.setString(4, cu.getState());
			pst.setString(5, cu.getIngorefile());
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(null, pst);
		}
	}
	
	public void insertCPE(String cpe,String vendor,String product,String version){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_CPE"));
			pst.setString(1, cpe);
			pst.setString(2, vendor);
			pst.setString(3, product);
			pst.setString(4, version);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertVersion(int pid,String version,String url,String codepath,String standardversion){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_VERSION"));
			pst.setInt(1, pid);
			pst.setString(2, version);
			pst.setString(3, url);
			pst.setString(4, codepath);
			pst.setString(5, standardversion);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertURL(int pid,String url,String type){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_DOWNLOAD"));
			pst.setInt(1, pid);
			pst.setString(2, url);
			pst.setString(3, type);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatestate1(int pid){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("UPDATE_STATE1"));
			pst.setInt(1, pid);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertmatch(int pid,String vendor,String product){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("UPDATE_VENDOR_PRODUCT"));
			pst.setString(1, vendor);
			pst.setString(2, product);
			pst.setInt(3, pid);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getpid(String url){
		PreparedStatement pst = null;
		ResultSet rs = null;
		int id = 0;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_PID"));
			pst.setString(1, url);
			rs = pst.executeQuery();
			if(rs.next()){
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeps(rs, pst);
		}
		return id;
	}
	public Map<String,List<SourceFile>> getIndex(){
		Map<String,List<SourceFile>> fileList = new HashMap<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_INDEX"));
			rs = pst.executeQuery();
			while(rs.next()){
				if(fileList.containsKey(rs.getString("k.md5"))){
					fileList.get(rs.getString("k.md5")).add(new SourceFile(rs.getString("f.name"),rs.getString("f.filepath"),rs.getInt("f.vid")));
				}
				else{
					List<SourceFile> fl = new ArrayList<>();
					fl.add(new SourceFile(rs.getString("f.name"),rs.getString("f.filepath"),rs.getInt("f.vid")));
					fileList.put(rs.getString("k.md5"), fl);
				}
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(rs,pst);
		}
		return fileList;
	}
	public Set<Integer> getPidList(){
		Set<Integer> pList = new HashSet<Integer>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_ALL_ID_PROJECT"));
			rs = pst.executeQuery();
			while(rs.next()){
				pList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(rs,pst);
		}
		return pList;
	}
	
	public Map<Integer,Version> getVerMap(){
		Map<Integer,Version> verList = new HashMap<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_ALL_VERSION"));
			rs = pst.executeQuery();
			while(rs.next()){
				if(!verList.containsKey(rs.getInt("vid"))){
					verList.put(rs.getInt("vid"), new Version(rs.getString("version"),rs.getString("des"),rs.getString("url"),rs.getString("codepath"),rs.getInt("pid")));
				}
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(rs,pst);
		}
		return verList;
	}
	
	public Map<Integer,Project> getProMap(){
		Map<Integer,Project> proList = new HashMap<Integer, Project>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_ALL_PROJECT"));
			rs = pst.executeQuery();
			while(rs.next()){
				if(!proList.containsKey(rs.getInt("pid"))){
					proList.put(rs.getInt("pid"), new Project(rs.getString("vendor"),rs.getString("product"),rs.getString("des"),rs.getString("homepage")));
				}
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(rs,pst);
		}
		return proList;
	}
	public Set<Integer> getVidList(){
		Set<Integer> vList = new HashSet<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_ALL_ID_VERSION"));
			rs = pst.executeQuery();
			while(rs.next()){
				vList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(rs,pst);
		}
		return vList;
	}
	public void insertProject(int id,String vendor,String product,String des,String homepage){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_PROJECT"));
			pst.setInt(1, id);
			pst.setString(2, vendor);
			pst.setString(3, product);
			pst.setString(4, des);
			pst.setString(5, homepage);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
	}
	
	public Map<String,Integer> getKeys(){
		Map<String,Integer> result = new HashMap<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_KEY"));
			rs = pst.executeQuery();
			while(rs.next()){
				result.put(rs.getString("md5"), rs.getInt("kid"));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
		return result;
	}
	
	public void insertFile(int kid,int vid,String name,String path){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_FILE"));
			pst.setInt(1, kid);
			pst.setInt(2, vid);
			pst.setString(3, path);
			pst.setString(4, name);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
	}
	
	public int getKeyId(String key){
		int kid = -1;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("SELECT_ID_KEY"));
			pst.setString(1, key);
			rs = pst.executeQuery();
			if(rs.next()){
				kid = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
		return kid;
	}
	
	public void insertKey(String key){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_KEY"));
			pst.setString(1, key);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
	}
	
	public void insertVersion(int id,int pid,String version,String url,String des,String codepath){
		PreparedStatement pst = null;
		try {
			pst = getConn().prepareStatement(statementBundle.getProperty("INSERT_VERSION"));
			pst.setInt(1, id);
			pst.setString(2, version);
			pst.setString(3, des);
			pst.setString(4, url);
			pst.setString(5, codepath);
			pst.setInt(6, pid);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			closeps(null,pst);
		}
	}
	
	public static void openTran(){
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static void closeTran(){
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static void close(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
	public void closeps(ResultSet rs, PreparedStatement ps) {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
