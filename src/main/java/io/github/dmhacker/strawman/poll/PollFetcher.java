package io.github.dmhacker.strawman.poll;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PollFetcher {
	private int id;
	private JsonObject json;
	private boolean valid = true;
	
	public PollFetcher(int poll) throws Exception {
		this.id = poll;
		try {
			this.json = new JsonParser().parse(readUrl(Poll.API_URL+poll)).getAsJsonObject();
		} catch (FileNotFoundException fnfe) {
			this.valid = false;
		}
	}
	
	public Poll parse() {
		return new Poll(id, json);
	}
	
	public boolean valid() {
		return valid;
	}
	
	private static String readUrl(String urlString) throws Exception {
		URLConnection connection = new URL(urlString).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		connection.connect();

		BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    sb.append(line);
		}
		return sb.toString();
	}
}
