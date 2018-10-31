package cn.codesafe.codehomology.POJO;

public class MatchResult {
	public SourceFile sf;
	public FileScan fs;
	public int per;
	public MatchResult(SourceFile sf, FileScan fs,int per) {
		super();
		this.sf = sf;
		this.fs = fs;
		this.per = per;
	}
}
