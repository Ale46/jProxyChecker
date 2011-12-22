/*******************************************************************************
 * Copyright (c) 2009 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class ProxyEngine {
	
	private ArrayList<Proxy> proxyList = new ArrayList<Proxy>();

	
	//controllo se l'input è del tipo IP:PORT
	public boolean isProxy(String iPaddress){
        final Pattern IP_PATTERN =
              Pattern.compile("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}:[0-9]{1,5}");
        return IP_PATTERN.matcher(iPaddress).matches();
	}
	


	private void extractProxyListFromUrl(String url) throws IOException{
		ArrayList<String> proxyData;
		proxyData =  new WebUrl(url).getData();
		String ip = null,port = null;
		

		for (int i = 0;i<proxyData.size();i++){
			if (isProxy(proxyData.get(i))) {
				String[] stuff = proxyData.get(i).split(":");
				ip = stuff[0];
				port = stuff[1];
				proxyList.add(new Proxy(ip,port));
			}
		}
	}
	

	public ArrayList<Proxy> getProxyFromUrl(String url){
		try {
			extractProxyListFromUrl(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proxyList;
	}

	@SuppressWarnings("deprecation")
	private void extractProxyListFromFile(String filename) throws IOException{
		ArrayList<String> proxyData = new ArrayList<String>();
	    File file = new File(filename);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    try {
	      fis = new FileInputStream(file);
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      while (dis.available() != 0) {
	        proxyData.add(dis.readLine());
	      }
	      fis.close();
	      bis.close();
	      dis.close();

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		String ip,port;
		for (int i = 0;i<proxyData.size();i++){
			if (isProxy(proxyData.get(i))) {
				String[] stuff = proxyData.get(i).split(":");
				ip = stuff[0];
				port = stuff[1];
				proxyList.add(new Proxy(ip,port));
			}

		}
	}
	
	public ArrayList<Proxy> getProxyFromFile(String filename){
		try {
			extractProxyListFromFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proxyList;
	}


	
}
