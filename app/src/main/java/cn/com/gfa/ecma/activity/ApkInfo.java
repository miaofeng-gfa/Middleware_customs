package cn.com.gfa.ecma.activity;

public class ApkInfo {

	private String version;
	private String size;
	private String des;
	
	public ApkInfo(String version, String size, String des) {
		super();
		this.version = version;
		this.size = size;
		this.des = des;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	@Override
	public String toString() {
		return "ApkInfo [version=" + version + ", size=" + size + ", des="
				+ des + "]";
	}
	
}
