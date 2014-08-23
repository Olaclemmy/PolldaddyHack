/**
 * The Poll Class represents the instance of a polldaddy Poll
 * @author <a href="mailto:flopex@live.com">flopex</a>
 * @version 0.1, August 2013
 * 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;



public class Poll {
	
	
	private String pollURL;
	private String pollAns;
	private String proxyList;
	private UrlValidator validator;
	
	private ArrayList<String> proxiesIP;
	
	
	public Poll(String pollURL, String pollAns){
		
		this.pollURL = pollURL;
		this.pollAns = pollAns;
		this.proxyList = "";
		
		validator = new UrlValidator();
		
		Logger logger = Logger.getLogger ("");
		logger.setLevel (Level.OFF);
		
		
	}
	
	public Poll(String pollURL, String pollAns, String proxyList){
		this.pollURL = pollURL;
		this.pollAns = pollAns;
		this.proxyList = proxyList;
		
		proxiesIP = new ArrayList<String>();
		
		validator = new UrlValidator();

		readProxyList();
		
		Logger logger = Logger.getLogger ("");
		logger.setLevel (Level.OFF);
		
	}
	
	
	public String submitVote(){
		String log = "";
		try{
			
			if (isValidURL(pollURL)){
				
				String cProxy = "";
				
				if (proxyList.trim().length() > 0){
					int ind = 0 + (int)(Math.random() * ((proxiesIP.size() - 0) + 1));
					cProxy = proxiesIP.get(ind);
					
					
				}
				
				WebClient webClient = null;
				
				if (cProxy.trim().length() > 0){
					String[] tmp = cProxy.trim().split(":");
					webClient = new WebClient(BrowserVersion.FIREFOX_17,tmp[0],Integer.parseInt(tmp[1]));
					webClient.getProxyConfig().setProxyHost(tmp[0]);
					webClient.getProxyConfig().setProxyPort(Integer.parseInt(tmp[1]));
					webClient.setProxyConfig(new ProxyConfig(tmp[0],Integer.parseInt(tmp[1]),false));
				}else{
					webClient = new WebClient();
				}
				
				webClient.setRedirectEnabled(true);
				webClient.setThrowExceptionOnScriptError(false);
				webClient.setThrowExceptionOnFailingStatusCode(false); 
				
				
				webClient.setJavaScriptEnabled(true);
				webClient.setAjaxController(new NicelyResynchronizingAjaxController());
				webClient.setActiveXNative(true);
				webClient.setCssEnabled(true);
				
				webClient.getCache().clear();
				webClient.getCookieManager().clearCookies();
				
				
				HtmlPage votePage = webClient.getPage(pollURL);
				HtmlInput radio = votePage.getHtmlElementById(pollAns);
				radio.click();
				
				HtmlAnchor submit = (HtmlAnchor)votePage.getAnchorByText("Vote");
				votePage = submit.click();
				
				log = "SUCCESS";
				
			}else{
				log = "INVALID URL";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return log;
	}
	
	private void readProxyList(){
		
		try{
			
			File list = new File(proxyList);
			Scanner input = new Scanner(list);
			
			while (input.hasNext()){
				
				String line = input.nextLine().trim();
				
				proxiesIP.add(line);				
				
			}
			
			input.close();
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
				
	}
	
	
	
	private boolean isValidURL(String url){		
		return validator.isValid(url);		
	}
	
	

}
