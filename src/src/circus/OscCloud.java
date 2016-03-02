package circus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import netP5.NetAddress;
import netP5.NetAddressList;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class OscCloud extends PApplet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	PApplet parent;
	String mode = "child"; //"parent"
	int port = 1337;
	public NetAddressList myNetAddressList = new NetAddressList();
	String ip = "192.168.1.1";
	String parentIp = "";
	int parentPort = 0;
	public int maxTime = 9999999;
	public String behaviour = "backtozero"; //"upanddown"
	boolean direction = true;

	String separator = "&&&&&,&&&&&";

	OscP5 oscP5;

	public int time = 0;
	public int currentTime = 0;

	public boolean registered = false;
	public boolean running = false;

	int registerTimer = 0;
	int registerTimeOut = 100;

	public OscCloud(PApplet p){
		parent = p;
		setup();
	}

	public OscCloud(PApplet p, String m){
		mode = m;
		parent = p;
		setup();
	}

	public OscCloud(PApplet p, String m, String iIP, int iPort, String pIP, int pPort){
		mode = m;
		port = iPort;
		parentPort = pPort;
		parentIp = pIP;
		parent = p;
		ip = iIP;
		setup();
	}

	public OscCloud(PApplet p, String m, String iIP, int iPort){
		mode = m;
		port = iPort;
		parent = p;
		ip = iIP;
		setup();
	}

	public OscCloud(PApplet p, String iIP, int iPort){
		port = iPort;
		parent = p;
		ip = iIP;
		setup();
	}

	public void setup() {
		oscP5 = new OscP5(parent,port);
		parent.registerDraw(this);
		if(mode == "child"){
			connectToParent();
		}
	}
	
	public void connectToParent(){
		OscMessage message = new OscMessage("/registerip");
		message.add(ip+","+port);
		NetAddress address = new NetAddress(parentIp, parentPort);
		oscP5.send(message, address);
	}

	public void draw() {
		if(running && (mode == "parent")){
			OscMessage message = new OscMessage("/time");
			message.add(time);
			oscP5.send(message, myNetAddressList);
			if(direction){
				time++;
			}else{
				time--;
			}
			if(time == maxTime){
				if(behaviour == "backtozero"){
					time = 0;
				}else if(behaviour == "upanddown"){
					direction = false;
				}
			}else if(time < 0){
				direction = true;
			}
		}
		if(!registered){
			registerTimer++;
			if(registerTimer > registerTimeOut){
				connectToParent();
				registerTimer = 0;
			}
		}
	}

	public void addChild(String ip, String port){
		if(!myNetAddressList.contains(ip, Integer.parseInt(port))) {
			myNetAddressList.add(new NetAddress(ip, Integer.parseInt(port)));
			PApplet.println("new registration: ip:"+ip+" port:"+port);
			OscMessage message = new OscMessage("/welcome");
			message.add(true);
			NetAddress address = new NetAddress(ip, Integer.parseInt(port));
			oscP5.send(message, address);
		}

	}

	public void oscEvent(OscMessage theOscMessage) {
		String message = theOscMessage.addrPattern();
		String[] messageElements = message.split("/");
		if(messageElements[1].equals("registerip")){
			String values[] = theOscMessage.get(0).stringValue().split(",");
			addChild(values[0], values[1]);
		}
		if(messageElements[1].equals("time")){
			currentTime = theOscMessage.get(0).intValue();
			PApplet.println("currentTime:"+currentTime);
		}
		if(messageElements[1].equals("welcome")){
			registered = true;
		}
		if(messageElements[1].equals("sendMessage")){
			String values[] = theOscMessage.get(0).stringValue().split(separator);
			send(values[0], values[1]);
		}
		if(messageElements[1].equals("incomingMessage")){
			try{
				Class<?> classes[] = {String.class, String.class};
				Method m = parent.getClass().getMethod("incomingMessage", classes);
				m.setAccessible(true);
				try {
					String values[] = theOscMessage.get(0).stringValue().split(separator);
					Object sendValues[] = {(String) values[0], (String) values[1]};
					m.invoke(parent, sendValues);
				} catch (IllegalArgumentException e) {	e.printStackTrace();
				} catch (IllegalAccessException e) {	e.printStackTrace();
				} catch (InvocationTargetException e) {	e.printStackTrace();
				}

			} catch (SecurityException e){ 		e.printStackTrace();
			} catch (NoSuchMethodException e){	e.printStackTrace();
			}
		}
	}

	public void send(String message, String content){
		if(mode=="child"){
			OscMessage oMessage = new OscMessage("/sendMessage");
			oMessage.add(message+separator+content);
			NetAddress address = new NetAddress(parentIp, parentPort);
			oscP5.send(oMessage, address);
		}else{
			OscMessage oMessage = new OscMessage("/incomingMessage");
			oMessage.add(message+separator+content);
			oscP5.send(oMessage, myNetAddressList);
		}
	}
}
