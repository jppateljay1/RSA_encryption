import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

//This class has no function calls, but on creation takes in a file and turns it into the unblocked

public class UnblockedMessage {
	
	File source;
	int blockSize;
	
	public UnblockedMessage(){
		JFileChooser fc = new JFileChooser();													//Read the file chosen by the user
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fc.setDialogTitle("Select message to unblock");

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			source = fc.getSelectedFile();
			//This is where a real application would open the file.
		}else{
			System.out.println("Error reading in file");
			return;
		}
		
		blockSize = detectBlockSize();															//detect the block size
		unblockMessage();																		//unblock the file
	}
	
	//returns the block size of the file that is given.
	private int detectBlockSize(){
		
		try{
			
		BufferedReader reader = new BufferedReader(new FileReader(source));												
		int lineLength = 0;
		
		while(reader.read() != '\n')																	//Read until new line is seen to get total alternate ASCII numbers on each line
			lineLength++;
		
		reader.close();
		return lineLength/2;																			//return half the value because 2 ASCII numbers correspond to 1 message character
		
		}catch(IOException e){
		}
		return 0;
	}
	
	//This function unblocks the file given by the user from numbers to a readable character message
	private void unblockMessage(){
		
		try{
			
			BufferedReader reader = new BufferedReader(new FileReader(source));								//setup reader
			int charRead;
			
			String unblockedFileName = "u"+source.getName();												//determine file name
			
			File unblockedFile = new File(unblockedFileName);												//make new file
			unblockedFile.createNewFile();
			
			PrintWriter write = new PrintWriter(unblockedFile);												//new writer
			
			int readCount = 0;																			
			String charGroup = "";
			String lineRead = "";
			int lineCharsRead = 0;
			
			while((charRead = reader.read()) != -1){														//reader.read() returns -1 upon reaching end of file			
				
					if(lineCharsRead == this.blockSize){													//Read lines based on block size detected in file			
						write.print(lineRead);
						
						//reset all the variables and start reading new line
						lineRead = "";
						charGroup = "";
						lineCharsRead = 0;
						readCount = 0;
					}
					else{
						if(readCount == 2){																	//read 2 numbers/chars, means that we have all the information to change back into a char
							int alteredCharValue = Integer.parseInt(charGroup);								//Turn the chars into an int
							int charValue = convertAlternateToASCII(alteredCharValue);
							
							if(alteredCharValue != 0)														//Used to prevent reading in padded 0s at the end
								lineRead = (char)charValue + lineRead;										//Append the characters together

							readCount = 0;
							charGroup = "";
							lineCharsRead++;																//Keep track of how far into line we are to know when to go to next line based on block size
						}
						charGroup = charGroup +(char)charRead;												//Put together a group of 2 chars to get our alternative ASCII value
						readCount++;																		//Keep track of when we have read both characters necessary
					}
			}
			
			//close files
			write.close();
			reader.close();		
		}catch(IOException e){
		}
	}
	
	//Converts between char tables
	private int convertAlternateToASCII(int alt){
		
		switch(alt){			//cases for first 4 chars
		case 1:
			return 11;
		case 2:
			return 9;
		case 3:
			return 10;
		case 4:
			return 13;
		}
		
		if(alt>4)					//general case
			return alt+27;
		else
			return alt;
			
	}
	
	public static void main(String[] args){
		UnblockedMessage test = new UnblockedMessage();
		System.out.println(test.detectBlockSize());
	}
}
