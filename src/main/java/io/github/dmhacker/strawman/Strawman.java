package io.github.dmhacker.strawman;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import io.github.dmhacker.strawman.poll.Poll;
import io.github.dmhacker.strawman.poll.PollFetcher;
import io.github.dmhacker.strawman.proxy.ProxyGatherer;
import io.github.dmhacker.strawman.proxy.ProxyInfo;
import io.github.dmhacker.strawman.proxy.YasakvarProxyGatherer;

public class Strawman {
	private static final String VERSION = "0.0.2";
	private static Set<ProxyGatherer> HARVESTERS = new HashSet<ProxyGatherer>();
	public static Set<ProxyInfo> PROXIES = new HashSet<ProxyInfo>();
	
	static {
		HARVESTERS.add(new YasakvarProxyGatherer());
	}
	
	public static void main(String[] args) throws Exception {
		print("v"+VERSION+" booting up.");

		findProxies();
		findPoll();
	}
	
	private static void findProxies() {
		for (ProxyGatherer gatherer : HARVESTERS) {
			PROXIES.addAll(gatherer.gather());
		}
		print("Proxies found: "+PROXIES.size()+"\n");
	}
	
	private static void findPoll() throws Exception {
		
        String string = JOptionPane.showInputDialog("Poll Number");
        int pollNumber;
        try {
        	pollNumber = Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
        	findPoll();
        	return;
        }
        
        PollFetcher fetcher = new PollFetcher(pollNumber);
        if (!fetcher.valid()) {
        	findPoll();
        	return;
        }
        
        Poll poll = fetcher.parse();
        poll.select();
	}
	
	private static void print(String string) {
		System.out.println(string);
	}
}
