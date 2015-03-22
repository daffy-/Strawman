package io.github.dmhacker.strawman.proxy;

public class ProxyInfo {
	private String host;
	private int port;
	
	public ProxyInfo(String host) {
		this(host, 80);
	}
	
	public ProxyInfo(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHostname() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof ProxyInfo))
			return false;
		ProxyInfo other = (ProxyInfo) object;
		return host.equals(other.host) && port == other.port;
	}
	
	@Override
	public int hashCode() {
		return host.hashCode() + port;
	}
	
	@Override
	public String toString() {
		return "Proxy[ip="+host+",port="+port+"]";
	}
}
