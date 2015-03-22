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
	private static Set<ProxyGatherer> HARVESTERS = new HashSet<ProxyGatherer>();
	public static Set<ProxyInfo> PROXIES = new HashSet<ProxyInfo>();
	
	static {
		HARVESTERS.add(new YasakvarProxyGatherer());
	}
	
	public static void main(String[] args) throws Exception {
		System.setProperty("java.net.useSystemProxies", "true");
		
		// Get list of proxies
		print("Gathering proxies ...");
		for (ProxyGatherer gatherer : HARVESTERS) {
			PROXIES.addAll(gatherer.gather());
		}
		print("Found a total of "+PROXIES.size()+" proxies to use.\n");
		
		// Get polls
        String string = JOptionPane.showInputDialog("Poll Number");
        int pollNumber = 0;
        try {
        	pollNumber = Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
        	print("Not a number. Exiting.");
        	System.exit(-1);
        }
        
        
        PollFetcher fetcher = new PollFetcher(pollNumber);
        if (!fetcher.valid()) {
        	print("Invalid poll. Exiting.");
        	System.exit(-1);
        }
        
        Poll poll = fetcher.parse();
        poll.select();
	}
	
	private static void print(String string) {
		System.out.println(string);
	}
}
