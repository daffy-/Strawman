package io.github.dmhacker.strawman.proxy;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YasakvarProxyGatherer implements ProxyGatherer {
	private static final String URL = "http://www.yasakvar.com/apiv1/?type=json";
	
	@Override
	public Set<ProxyInfo> gather() {
		Set<ProxyInfo> proxies = new HashSet<ProxyInfo>();
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(new InputStreamReader(new URL(URL).openStream())).getAsJsonObject();
			JsonObject rawProxies = json.get("proxylist").getAsJsonObject();
			for (Entry<String, JsonElement> entry : rawProxies.entrySet()) {
				JsonObject desc = entry.getValue().getAsJsonObject();
				proxies.add(new ProxyInfo(desc.get("ip").getAsString(), desc.get("port").getAsInt()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proxies;
	}

}
