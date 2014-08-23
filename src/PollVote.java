import javax.swing.*;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
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
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The PollVote Class reads Poll URL and ANSWER_ID for vote Submission
 * @author <a href="mailto:flopex@live.com">flopex</a>
 * @version 0.1, August 2013
 * 
 */
public class PollVote extends JFrame{
	
	
	private final Font ALL_FIELDS = new Font("Helvetica", Font.BOLD, 30);
	private final Font ALL_TEXT = new Font("Helvetica", Font.BOLD, 12);
	
	private JButton vote;
	private JTextField num;
	private JLabel numVotes;
	
	
	private JLabel website;
	private JTextField url;
	private JButton scan;
	
	private JList answers;
	
	
	private JButton choose;
	private String proxyLocation;
	
	
	
	
	public PollVote(){
		
		super();
		
		Logger logger = Logger.getLogger ("");
		logger.setLevel (Level.OFF);
		
		initGUI();
		
		
	}
	
	
	private void initGUI(){
		getContentPane().setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.setTitle("PolldaddyHack v0.2 by flopex<flopex@live.com> - DynoBin.com/blog");
		
		vote = new JButton("VOTE");
		vote.setBounds(500, 265, 100, 50);
		getContentPane().add(vote);
		
		
		num = new JTextField();
		num.setBounds(420,265,75,50);
		num.setFont(ALL_FIELDS);
		num.setHorizontalAlignment(JTextField.CENTER);
		getContentPane().add(num);
		
		
		numVotes = new JLabel("Number of Votes");
		numVotes.setBounds(320,265,100,50);
		numVotes.setFont(ALL_TEXT);
		getContentPane().add(numVotes);
		
		
		
		website = new JLabel("Enter Poll URL");
		website.setBounds(10,10,100,50);
		website.setFont(ALL_TEXT);
		getContentPane().add(website);
		
		
		url = new JTextField();
		url.setBounds(110,20,400,30);
		url.setFont(new Font("Helvetica", Font.BOLD, 20));
		getContentPane().add(url);
		
		
		scan = new JButton("SCAN");
		scan.setBounds(530,20,70,30);
		getContentPane().add(scan);
		
		
		//String[] sample = {"Item 1","Item 2", "Item 3", "Item 4"};
		
		answers = new JList();
		answers.setBounds(10,60,590,200);
		answers.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(answers);
		
		
		proxyLocation = "";
		
		choose = new JButton("Set Proxies");
		choose.setBounds(10,265,100,50);
		getContentPane().add(choose);
		
		choose.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				int option = chooser.showOpenDialog(PollVote.this);
				if (option == JFileChooser.APPROVE_OPTION){
					
					try{
						File file = chooser.getSelectedFile();
						proxyLocation = file.getAbsolutePath();
						
					}catch(Exception ee){
						ee.printStackTrace();
					}
					
				}
			}
			
		});
		
		
		
		
		
		scan.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() > 0){
					answers.setListData(getList().toArray());
				}
			}
			
		});
		
		vote.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() > 0){
					submitVotes();
				}
			}
			
		});
		
		
		
		
		pack();
		
		
	}
	
	
	
	private ArrayList<String> getList(){
		
		ArrayList<String> list = new ArrayList<String>();
		
		String URL = url.getText();
		
		if (isValidURL(URL)){
			
			try{
				WebClient webClient = new WebClient();
				
				webClient.setRedirectEnabled(true);
				webClient.setThrowExceptionOnScriptError(false);
				webClient.setThrowExceptionOnFailingStatusCode(false); 
				
				
				webClient.setJavaScriptEnabled(true);
				webClient.setAjaxController(new NicelyResynchronizingAjaxController());
				webClient.setActiveXNative(true);
				webClient.setCssEnabled(true);
				
				webClient.getCache().clear();
				webClient.getCookieManager().clearCookies();
				
				
				HtmlPage votePage = webClient.getPage(URL);
				
				List<HtmlInput> inputs = (List<HtmlInput>) votePage.getByXPath("//input[contains(@id,'PDI_answer')]");
				
				for (int i = 0; i < inputs.size(); ++i){
					String ansID = inputs.get(i).getId();
					HtmlLabel ans = (HtmlLabel) votePage.getByXPath("//label[@for='"+ansID+"']").get(0);
					
					list.add(ans.asText()+" ::: "+ansID);
					
					
					//list.add(inputs.get(i).getId());
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
		
		
		return list;
		
		
		
	}
	
	
	private void submitVotes(){
		
		String selected = answers.getSelectedValue().toString();
		String numberVotes = num.getText();
		if (isInteger(numberVotes)){
			
			if (selected.length() > 0){
				
				Poll v;
				
				if (proxyLocation.length() > 0){
					v = new Poll(url.getText().trim(),selected.split(":::")[1].trim(),proxyLocation);
				}else{
					v = new Poll(url.getText().trim(),selected.split(":::")[1].trim());
				}
				
				int amount = Integer.parseInt(numberVotes);
				
				for (int i = 0; i < amount; ++i){
					System.out.println(v.submitVote());
					try{
						if (proxyLocation.length() == 0){
							Thread.sleep(10000);
						}
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				
				
			}else{
				JOptionPane.showMessageDialog(this, "Select one option from the list");
			}
			
			
		}else{
			JOptionPane.showMessageDialog(this, "Number of Votes - NAN");
		}
		
		
		
	}
	
	
	
	private boolean isValidURL(String url){
		UrlValidator validator = new UrlValidator();
		return  validator.isValid(url);		
	}
	
		

	 public static boolean isInteger(String s) {
	     try { 
	         Integer.parseInt(s); 
	     } catch(NumberFormatException e) { 
	         return false; 
	     }
	     return true;
	 }
	
	
	
	
	
	
	public static void main(String[] args){
		
		
		PollVote pv = new PollVote();
		pv.setSize(610, 350);
		pv.setResizable(false);
		pv.setLocationRelativeTo(null);
		pv.setVisible(true);
		
		
		
	}

}
