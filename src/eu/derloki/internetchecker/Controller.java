package eu.derloki.internetchecker;

import java.awt.Image;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eu.derloki.util.properties.PropertiesHelper;
import eu.derloki.util.tray.TrayHelper;

public class Controller {

	private Main ui;
	private TrayHelper th;
	private Properties config;
	
	private TimeUnit selectedTimeUnit;
	private int selectedInterval;
	private String selectedUrl;
	private boolean autoStart;
	private boolean autopopup;
	
	private boolean started;
	
	
	private final String TIME_UNIT="timeUnit";
	private final String INTERVAL = "interval";
	private final String URL = "url";
	private final String AUTOSTART="autostart";
	private final String AUTOPOPUP="autopopup";
	private final int maxCount = 20;
	
	private ScheduledExecutorService executor;
	private LinkedList<Packet> packetList;
	
	private long mTime = 0;
	private int pLostPacket = 0;
	private boolean lock = false;
	
	private JMenuItem startItem;
	
	
	private HashMap<String, Image> imageMap;
	private String currentImage = "";
	
	public Controller(Main ui){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
		}
		
		this.ui = ui;
		
		this.config = PropertiesHelper.getShortV("config");
		
		this.packetList = new LinkedList<Packet>();
		
		started = false;
		
		boolean changed = false;
		try{
			selectedTimeUnit = TimeUnit.valueOf(config.getProperty(TIME_UNIT, "SECONDS"));
		}catch(Exception e){
			selectedTimeUnit = TimeUnit.SECONDS;
			changed = true;
		}
		
		try{
			selectedInterval = Integer.parseInt(config.getProperty(INTERVAL,"1"));
		}catch(Exception e){
			selectedInterval = 1;
			changed = true;
		}
		
		try{
			selectedUrl = config.getProperty(URL, "http://www.google.com");
		}catch(Exception e){
			selectedUrl = "http://www.google.com";
			changed = true;
		}
		
		try{
			autoStart = Boolean.parseBoolean(config.getProperty(AUTOSTART,"false"));
		}
		catch(Exception e){
			autoStart = false;
			changed = true;
		}
		
		try{
			autopopup = Boolean.parseBoolean(config.getProperty(AUTOPOPUP,"true"));
		}
		catch(Exception e){
			autoStart = true;
			changed = true;
		}
		
		if(changed){
			save();
		}
		
		imageMap = new HashMap<String, Image>();
		
		imageMap.put("icon_mg",TrayHelper.getImage("resources/img/icon_mg.png"));
		imageMap.put("icon_hg",TrayHelper.getImage("resources/img/icon_hg.png"));
		imageMap.put("icon_lg",TrayHelper.getImage("resources/img/icon_lg.png"));
		imageMap.put("icon_ng",TrayHelper.getImage("resources/img/icon_ng.png"));
		imageMap.put("icon_my",TrayHelper.getImage("resources/img/icon_my.png"));
		imageMap.put("icon_hy",TrayHelper.getImage("resources/img/icon_hy.png"));
		imageMap.put("icon_ly",TrayHelper.getImage("resources/img/icon_ly.png"));
		imageMap.put("icon_ny",TrayHelper.getImage("resources/img/icon_ny.png"));
		imageMap.put("icon_mr",TrayHelper.getImage("resources/img/icon_mr.png"));
		imageMap.put("icon_hr",TrayHelper.getImage("resources/img/icon_hr.png"));
		imageMap.put("icon_lr",TrayHelper.getImage("resources/img/icon_lr.png"));
		imageMap.put("icon_nr",TrayHelper.getImage("resources/img/icon_nr.png"));
		imageMap.put("icon_def",TrayHelper.getImage("resources/img/icon_def.png"));
		
	}
	
	private void startExecutor() {
		if(!started){
			started = true;
			executor = new ScheduledThreadPoolExecutor(20);
			
			executor.scheduleAtFixedRate(()->{

				
				
				Packet p = pingUrl(selectedUrl);
				if(packetList.size() >= maxCount){
					packetList.removeFirst();
				}
				packetList.add(p);
				
				if(!lock){
					lock=true;
					int countLostPackets = 0;
					long countTime = 0;
					LinkedList<Packet> tmpList = new LinkedList<Packet>();
					tmpList.addAll(packetList);
					
					for (Packet packet : tmpList) {
						if(packet.lost){
							countLostPackets++;
						}
						else{
							countTime+=packet.time;
						}
					}
					pLostPacket = (countLostPackets*100)/tmpList.size();
					mTime=countTime/tmpList.size();
					String color = "r";
					if(mTime<100){
						color="g";
					}
					else if(mTime < 500){
						color="y";
					}
					else{
						color="r";
					}
					String filled = "n";
					if(pLostPacket < 10){
						filled="m";
					}
					else if(pLostPacket < 30){
						filled="h";
					}
					else if(pLostPacket < 80){
						filled="l";
					}
					else{
						filled = "n";
					}
					
					String newImage = "icon_"+filled+color;
					
					if(!currentImage.equals(newImage)){
						th.setImage(imageMap.get(newImage));
					}
					
					
					lock = false;
				}
				
				
				
				
			}, 0, selectedInterval, selectedTimeUnit);
			
			startItem.setText("Stop");
		}
	}

	public void exit(){
		th.exit();
		executor.shutdown();
	}

	public void afterUI() {
		// TODO Auto-generated method stub
		
		
		th = new TrayHelper("resources/img/icon_def.png", "Internet Tester",(event)->{
			Platform.runLater(()->{
				ui.showWhenHidden();
			});
		 });
		
		startItem = th.addMenuItem("Start", ()->{
			if(!started){
				startExecutor();
			}
			else{
				stopExecutor();
			}
		});
		
		th.addMenuItem("Properties", ()->{
			Platform.runLater(()->{
				try {
					ui.showWhenHidden();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		});
		
		th.addMenuItem("Exit", ()->{
			Platform.runLater(()->{
				try {
					ui.exit(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		});
		
		if(autoStart){
			startExecutor();
		}
		
		if(autopopup){
			Platform.runLater(()->{
				ui.showWhenHidden();
			});
		}
		
	}
	
	private void stopExecutor() {
		if(started){
			executor.shutdown();
			started = false;
			startItem.setText("Start");
		}
	}

	
	public void save(){
		config.setProperty(TIME_UNIT, selectedTimeUnit.name());
		config.setProperty(INTERVAL, ""+selectedInterval);
		config.setProperty(URL, selectedUrl);
		config.setProperty(AUTOSTART,""+autoStart);
		config.setProperty(AUTOPOPUP, ""+autopopup);
		
		PropertiesHelper.save("config", "Changed Values");
	}
	
	public Packet pingUrl(final String address) {
		Packet p = new Packet();
		long endTimeF;
		long startTime = System.currentTimeMillis();
		 try {
		  final URL url = new URL(selectedUrl);
		  final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		  urlConn.setConnectTimeout(1000 * 10); // mTimeout is in seconds
		  startTime = System.currentTimeMillis();
		  urlConn.connect();
		  final long endTime = System.currentTimeMillis();
		  if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		  long timeTaken = (endTime - startTime);
		   p.lost = false;
		   p.time = timeTaken;
		   return p;
		  }
		 }catch(final SocketTimeoutException e){
			 
		 }
		 catch (final MalformedURLException e1) {
		 } catch (final IOException e) {
		 }
		 catch(Exception e){
			 
		 }
		 
		 endTimeF = System.currentTimeMillis();
		 p.lost = true;
		 p.time = (endTimeF - startTime);
		 return p;
		}

	public String[] cancelInput() {
		// TODO Auto-generated method stub
		String oldUrl = selectedUrl;
		String oldInterval = ""+selectedInterval;
		String oldTimeUnit = selectedTimeUnit.name();
		String oldAutostart = ""+autoStart;
		String oldAutopopup = ""+autopopup;
		
		return new String[]{oldUrl,oldInterval,oldTimeUnit,oldAutostart,oldAutopopup};
	}

	public void setNewInput(String[] strings) {
		String newUrl = strings[0];
		String newInterval = strings[1];
		String newTimeUnit = strings[2];
		String newAutostart = strings[3];
		String newAutopopup = strings[4];
		
		stopExecutor();
		
		selectedUrl = newUrl;
		selectedInterval = Integer.parseInt(newInterval);
		selectedTimeUnit = TimeUnit.valueOf(newTimeUnit);
		autoStart = Boolean.parseBoolean(newAutostart);
		autopopup = Boolean.parseBoolean(newAutopopup);
		
		save();
		
		if(autoStart){
			startExecutor();
		}
	}
	
	
}
