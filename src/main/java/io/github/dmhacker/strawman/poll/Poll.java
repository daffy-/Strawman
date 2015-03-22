package io.github.dmhacker.strawman.poll;

import io.github.dmhacker.strawman.Strawman;
import io.github.dmhacker.strawman.proxy.ProxyInfo;

import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class Poll {
	public static final String API_URL = "http://strawpoll.me/api/v2/polls/";
	
	private int id;
	private JsonObject json;
	private String title;
	private boolean multi;
	private List<String> options;
	private List<Integer> votes;
	
	public Poll(int id, JsonObject json) {
		this.id = id;
		this.json = json;
		this.title = json.get("title").getAsString();
		this.multi = json.get("multi").getAsBoolean();
		this.options = new ArrayList<String>();
		this.votes = new ArrayList<Integer>();
		
		JsonArray optionsArray = json.get("options").getAsJsonArray();
		JsonArray votesArray = json.get("votes").getAsJsonArray();
		for (int i = 0; i < optionsArray.size(); i++) {
			options.add(optionsArray.get(i).getAsString());
			votes.add(votesArray.get(i).getAsInt());
		}
	}
	
	public void select() {
		final JsonObject jsonCopy = new JsonParser().parse(json.toString()).getAsJsonObject();
		final JsonArray votes = new JsonArray();
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.setVisible(true);
		frame.setSize(400, 600);
		frame.setTitle("Poll #"+id);
		Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		frame.setLocation((rect.width - frame.getWidth()) / 2 , (rect.height - frame.getHeight()) / 2);
		
		JPanel panel = new JPanel(new GridLayout(0, 1));
		frame.add(panel);
		
		JLabel pollTitle = new JLabel(title, JLabel.CENTER);
		panel.add(pollTitle);
		pollTitle.setVisible(true);
		
		final List<JCheckBox> boxes = new ArrayList<JCheckBox>();
		for (String option : options) {
			JCheckBox box = new JCheckBox(option);
			boxes.add(box);
			panel.add(box);
			box.addItemListener(new ItemListener() {
				
			    @Override
			    public void itemStateChanged(ItemEvent e) {
			        if (e.getStateChange() == ItemEvent.SELECTED && !multi) {
			            List<JCheckBox> selected = new ArrayList<JCheckBox>();
			            for (JCheckBox possible : boxes) {
			            	if (possible.isSelected()) {
			            		selected.add(possible);
			            	}
			            }
			            if (selected.size() > 1) {
			            	for (JCheckBox previous : boxes) {
			            		previous.setSelected(false);
			            	}
			            	((JCheckBox) e.getItem()).setSelected(true);
			            }
			        }
			    }
			    
			});
			box.setVisible(true);
			box.setSelected(false);
		}
		
		JButton button = new JButton("Ok");
		panel.add(button);
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < boxes.size(); i++) {
					JCheckBox box = boxes.get(i);
					if (box.isSelected()) {
						votes.add(new JsonPrimitive(i));
					}
				}
				if (votes.size() == 0) {
					return;
				}
				
				frame.dispose();
				
				jsonCopy.add("votes", votes);
				respond(jsonCopy, Strawman.PROXIES);
			}
			
		});
		button.setVisible(true);
	}
	
	public void respond(JsonObject json, Collection<ProxyInfo> proxies) {
		System.out.println("Trying with the system proxy ...");
		System.setProperty("java.net.useSystemProxies", "true");
		vote(json);
		
		for (ProxyInfo proxy : proxies) {
			String host = proxy.getHostname();
			int port = proxy.getPort();
			System.out.println("Sending data to "+host+":"+port+" ...");
			
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", String.valueOf(port));
			vote(json);
		}
	}
	
	private void vote(JsonObject json) {
		try {
			URL url = new URL(Poll.API_URL+id);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			httpCon.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			OutputStreamWriter out = new OutputStreamWriter(
			    httpCon.getOutputStream());
			out.write(json.toString());
			out.close();
			
			int status = httpCon.getResponseCode();
			if (status == 200) {
				System.out.println("Success: "+status);
			}
			else {
				System.out.println("Failed: "+status);
				String errorString = readStream(httpCon.getErrorStream());
				try {
					JsonObject errorJson = new JsonParser().parse(errorString).getAsJsonObject();
					System.out.println(errorJson.get("error").getAsString());
				} catch (JsonSyntaxException ex) {
					// Json parsing failed
					System.out.println(errorString);
				}
			}
		} catch (Exception ex) {
			System.out.println("Failed: internal error");
			ex.printStackTrace();
		}
		System.out.println(" "); // Spacer
	}
	
	private String readStream(InputStream in) throws IOException {
	    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
	    StringBuilder responseStrBuilder = new StringBuilder();

	    String inputStr;
	    while ((inputStr = streamReader.readLine()) != null)
	        responseStrBuilder.append(inputStr);
	    return responseStrBuilder.toString();
	}
	
	public String getTitle() {
		return title;
	}
	
	public boolean isMulti() {
		return multi;
	}
}
