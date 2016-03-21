import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

//This class has no function calls, but on creation takes in a file and turns it into the blocked form with blocksize bs

public class BlockedMessage {
	private File source;
	private int blockSize;
	
	//Reads the file that should be blocked and performs the blocking
	public BlockedMessage(int bs){
		JFileChooser fc = new JFileChooser();													//Read the file
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fc.setDialogTitle("Select message to block");

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			source = fc.getSelectedFile();
			//This is where a real application would open the file.
		}else{
			System.out.println("Error reading in file");
			return;
		}
		
		blockSize = bs;
		blockMessage();
	}
	
	//Intakes a file and turns it into a blocked file
	private void blockMessage(){
		
		try{
		
		BufferedReader reader = new BufferedReader(new FileReader(source));
		int charRead;
		int linePosition = 0;
		
		String blockedFileName = "blocked"+source.getName();	//append blocked before the name given
		String numberLineBlock = "";
		
		File blockedFile = new File(blockedFileName);
		blockedFile.createNewFile();
		
		PrintWriter write = new PrintWriter(blockedFile);
		
		while((charRead = reader.read()) != -1){														//reader.read() returns -1 upon reaching end of file
			if(linePosition == blockSize){												//write line once a line of blocksize has been read
				write.println(numberLineBlock);
				numberLineBlock = "";
				linePosition = 0;
			}
			
			charRead = convertASCIItoAlternate(charRead);			//convert every char value to the 0-99 value
			
			if(charRead < 10)																		//If less than 10 we have to concatenate a 0																
					numberLineBlock = "0" + charRead + numberLineBlock;								//append a 0 if the value is below 10 so each char is the same length
			else
				numberLineBlock = charRead + numberLineBlock;
			
			linePosition++;
		}
		
		//Get and write the last complete line
		while(linePosition != blockSize){																		//Add null if the characters in the message is not a multiple of block size
			numberLineBlock = "00" + numberLineBlock;				//pad with zeros if necessary
			linePosition++;
		}
		write.println(numberLineBlock);								//write last line
		
		//close files and readers
		write.close();
		reader.close();
		
		}catch(IOException e){
		}
	}
	
	//Converts the ASCII values from their original values to the ones in the 0-100 range
	private int convertASCIItoAlternate(int old){
		
		switch(old){													//cases for the first 4 values
		case 11:
			return 1;
		case 9:
			return 2;
		case 10:
			return 3;
		case 13:
			return 4;
		}
		
		if(old>4)														//general case for all the other values that are together
			return old-27;
		else
			return old;
	}
	
	public static void main(String[] args){
		BlockedMessage test = new BlockedMessage(8);
	}

}
