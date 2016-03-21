import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.*;
import java.util.Scanner;

//This class with hold the GUI and will be the class which is run in order to link all the other classes together

public class SimpleRSA {

	private JFrame mainFrame;
	private JButton keyCreationButton;
	private JButton blockFileButton;
	private JButton unblockFileButton;
	private JButton encryptButton;
	private KeyPair keys;
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				SimpleRSA window = new SimpleRSA();
				window.mainFrame.setVisible(true);
			}
		});
	}
	
	public SimpleRSA(){
		initialize();
	}
	
	private void initialize(){
		//setup the main frame
		mainFrame = new JFrame();
		mainFrame.setTitle("Simple RSA Messenger");
		mainFrame.setBounds(200, 200, 250, 130);
		mainFrame.setResizable(false);
		mainFrame.setLayout( new FlowLayout() );
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Add the 4 main buttons and their action listeners that prompt further dialog
		keyCreationButton = new JButton("Create new keyset");
		mainFrame.getContentPane().add(keyCreationButton);
		keyCreationButton.addActionListener(new keyButtonHandler());
		
		blockFileButton = new JButton("Block a File");
		mainFrame.getContentPane().add(blockFileButton);
		blockFileButton.addActionListener(new blockButtonHandler());

		unblockFileButton = new JButton("Unblock a File");
		mainFrame.getContentPane().add(unblockFileButton);
		unblockFileButton.addActionListener(new unblockButtonHandler());
		
		encryptButton = new JButton("Encrypt/Decrypt a file");
		mainFrame.getContentPane().add(encryptButton);
		encryptButton.addActionListener(new encryptButtonHandler());
	}
	
	private class keyButtonHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			String options[] = new String[]{"Enter own primes", "Use pregenerated primes", "Select XML Keypair"};
			File file;
			//used to differentiate between the different options that can be chosen in the keys menu
			int response = JOptionPane.showOptionDialog(null, "Which primes would you like to use?", "Primes", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			XMLParserAndWriter xmlTool = new XMLParserAndWriter();
			
			if(response == 0){	//User wants to enter their own primes																							//User wants to enter own primes
				String sInput = (String)JOptionPane.showInputDialog("Enter first prime");
				String sInput2 = (String)JOptionPane.showInputDialog("Enter second prime");
		
				PrimeNumber prime1 = new PrimeNumber("sInput");
				PrimeNumber prime2 = new PrimeNumber("sInput2");
				//TODO verify primes and readd this line
				keys = new KeyPair(prime1, prime2);																		//Keypair with 
				
				String prefix = (String)JOptionPane.showInputDialog("Enter the prefix you want for your keyPair files");
				//TODO re add this line once prime selection works
				xmlTool.write(key, prefix);
			}
			else if(response == 2){	//User wants to select XML files with keys
				try{
					keys = xmlTool.parse();
					
				}catch(Exception e1){
				}
			}
			else{	//User wants to randomly select from primes list
				try{
					//Navigate to the rsc file that contains the list of prechosen primes
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
					fc.setDialogTitle("Select prime file");
		
					int returnVal = fc.showOpenDialog(keyCreationButton);
		
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = fc.getSelectedFile();
						//This is where a real application would open the file.
					}else{
						System.out.println("Error reading in file");
						return;
					}
		
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String input1 = "a", input2 = "b";
		 					
					int x = 0, y=0;
					while(x == y){//make sure we do not read the same prime twice
 						x = (int)(Math.random()*18)+1;
 						y = (int)(Math.random()*18)+1;
 					}
 
 					System.out.println(x + " test " + y);
 					
 					for(int i = 0; i < x; i ++)	//Read x from the primes list
 						input1 = reader.readLine();
 					
 					reader.close();
 					reader = new BufferedReader(new FileReader(file));	//Reset the fileReader to the beginning
 					
 					for(int i =0; i < y; i ++)	//read y from the primes list
 						input2 = reader.readLine();
 					
 
 					
 					PrimeNumber prime1 = new PrimeNumber(input1);
 					PrimeNumber prime2 = new PrimeNumber(input2);
 					
 					keys = new KeyPair(prime1,prime2);	//Create the keypair based on the input primes
 					
 					String prefix = (String)JOptionPane.showInputDialog("Enter the prefix you want for your keyPair files");
 					//TODO re add this line once prime selection works
 					xmlTool.write(keys, prefix);
 					
 					System.out.println(input1 + " " + input2);
 					prime1.p.printHugeInt();
 					prime2.p.printHugeInt();
			
 					reader.close();
				}catch(FileNotFoundException e1){
				}catch(IOException e1){
				}
			}
		}
	}

	//Hanlder for button that blocks a file
	class blockButtonHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			 String sInput2 = (String)JOptionPane.showInputDialog("Enter in the block size of the file");
			 BlockedMessage m = new BlockedMessage(Integer.parseInt(sInput2));
		}
	}
	
	//Hanlder for button that blocks a file
	class unblockButtonHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			UnblockedMessage m = new UnblockedMessage();
		}
	}
	
	//Hanlder for button that encrypts and decrypts files
	class encryptButtonHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			
			if(keys == null){	//Checks that user has selected a key set before they try to encrypt a file
				JOptionPane.showMessageDialog(null, "You have to generate or select a keypair before encrypting");
				return;
			}
			
			String options[] = new String[]{"Encrypt a file", "Decrypt a file"};
			File file;
			int response = JOptionPane.showOptionDialog(null, "What would you like to do?", "Encryption", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			if(response == 0){	//Encrypt file chosen
					 String newName = (String)JOptionPane.showInputDialog("What do you want to name the new file?");
					 keys.encryptFile(newName);
			}
			else{			//Decrypt file chosen
				String newName = (String)JOptionPane.showInputDialog("What do you want to name the new file?");
				keys.decryptFile(newName);
			}
		}
	}
}
