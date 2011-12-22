/*******************************************************************************
 * Copyright (c) 2009 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

class Proxy{
	private String ip;
	private String port;
	private long delay;
	private InetSocketAddress address;
	private String geodata = getClass().getResource("/GeoIP.dat").getFile();
	private String geocity = getClass().getResource("/GeoLiteCity.dat").getFile();
	private int timeout = 3000; //ms
	private String anonChecker = "http://jnerd.altervista.org/jproxycheck/check.php";
	
	Proxy (String ip,String port) throws UnknownHostException{
		this.ip = ip;
		this.port = port;
		address  = new InetSocketAddress(ip,Integer.parseInt(port));
	}
	
	public String getIp(){
		return this.ip;
	}
	
	public String getPort(){
		return this.port;
	}
	
	
	
	public boolean isAlive() throws IOException {
		boolean status = false;
		long t0 = System.currentTimeMillis();
		
		if (address.getAddress().isReachable(timeout)){		//controlla se l'indirizzo è raggiungibile
			status =  true;
		}
		long t1 = System.currentTimeMillis();
		delay = t1-t0;
		
		return status;

	}
	
	public double getDelay() {
		return (double) delay/1000.0;
	}
	
	public String getHostName() {
		return address.getHostName();
		
	}
	

	public String getCountry() throws IOException {
		LookupService cl = new LookupService(geodata);
		String countrycode = cl.getCountry(ip).getCode();
		
		cl.close();
		
		return countrycode;
	}
	
	public String getCity() throws IOException {
		LookupService cl = new LookupService(geocity,LookupService.GEOIP_MEMORY_CACHE);
		Location l1 = cl.getLocation(ip);
		String city = l1.city;
		cl.close();
		return city;
	
	}
	
	
	public String getAnonLevel() throws IOException {

		ArrayList<String> stuff = new WebUrl(anonChecker,ip,port).getData();
		if (stuff == null) return "Time Out";
		
		return stuff.get(0);
	}
	

	
	
}
