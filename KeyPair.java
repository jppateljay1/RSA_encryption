import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

//This class takes a pair of prime numbers and numbers and turns them into the respective d, e, n values.
// This class also holds the functions to encrypt and decrypt a file given the keys that are here.

public class KeyPair {
	public HugeInt eValue;
	public HugeInt dValue;
	public HugeInt nValue;
	
	public KeyPair(){};
		
	public KeyPair(PrimeNumber p, PrimeNumber q){
		nValue = new HugeInt(p.p);
		nValue.multiplyByHugeInt(q.p);											//n = p*q
		
		HugeInt m = new HugeInt(p.p);
		m.subtractHugeInt(new HugeInt("1"));								//p-1
		HugeInt m2 = new HugeInt(q.p);
		m2.subtractHugeInt(new HugeInt("1"));								//q-1
		
		m.multiplyByHugeInt(m2);											//m = (p-1)*(q-1)
		HugeInt mTemp = new HugeInt(m);
		
		eValue = new HugeInt("2");
		HugeInt compare = new HugeInt("1");
		while(m2.compareHugeInts(gcd(eValue, m), compare) != 0){					//start at 2 and search for a coprime
			eValue.increment(1);
			m = new HugeInt(mTemp);
		}
		m = new HugeInt(mTemp);
		
		dValue = findDValue(m, eValue);
	}
	
	private HugeInt gcd(HugeInt p, HugeInt q){
		HugeInt e = new HugeInt("0");
		while(e.compareHugeInts(e, q) != 0){								//while q and zero are not equal
			HugeInt temp = new HugeInt(q);
			p.modByHugeInt(q);
			q = new HugeInt(p);
			p = new HugeInt(temp);
		}
		return p;
	}
	
	private HugeInt findDValue(HugeInt m, HugeInt e){
		
		int n = 0;
		HugeInt zero = new HugeInt("0");
		HugeInt one = new HugeInt("1");
		HugeInt ret;
		HugeInt eTemp = new HugeInt(e);
		
		HugeInt d = new HugeInt(""+n);														//d = n
		d.multiplyByHugeInt(m);															//d = nm
		d.addHugeInt(one);																//d = 1 + nm
		ret = new HugeInt(d);															//store to return if next statement is true
		d.modByHugeInt(eTemp);																//d = (1 + nm) % e
		
		while(d.compareHugeInts(d, zero) != 0){											//Keep multiplying m*n until we find an n s.t. m*n % e = 0
			n++;
			eTemp = new HugeInt(e);
			d = new HugeInt(""+n);																//d = n
			d.multiplyByHugeInt(m);															//d = nm
			d.addHugeInt(one);																//d = 1 + nm
			ret = new HugeInt(d);															//store to return if next statement is true
			d.modByHugeInt(eTemp);																//d = (1 + nm) % e
		}
		eTemp = new HugeInt(e);
		ret.divideByHugeInt(eTemp);
		return ret;
	}
	
	public void encryptFile(String destName){
		
		JFileChooser fc = new JFileChooser();													//Read the file
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fc.setDialogTitle("Select blocked file to encrypt");
		File readFile;

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			readFile = fc.getSelectedFile();
			//This is where a real application would open the file.
		}else{
			System.out.println("Error reading in file");
			return;
		}
		
		try{
		
			BufferedReader reader = new BufferedReader(new FileReader(readFile));
			String lineRead;
			
			File encFile = new File(destName+".txt");
			encFile.createNewFile();
			
			PrintWriter write = new PrintWriter(encFile);
			HugeInt nTemp = nValue;
			
			while((lineRead = reader.readLine()) != null){
				System.out.println(lineRead);
				
				//Loop to encrypt every line
				HugeInt m = new HugeInt(lineRead);								
				HugeInt env = new HugeInt(1);
				HugeInt c = new HugeInt("1");															//C = 1
				for(HugeInt i = new HugeInt("0"); env.compareHugeInts(nValue, i) == -1; i.addHugeInt(new HugeInt("1")) ){
					nValue = new HugeInt(nTemp);
					c.multiplyByHugeInt(m);																//C=(C*M)%n
					c.modByHugeInt(nValue);
				}
				write.println(c);
			}
		reader.close();	
		write.close();
		}catch(IOException e){
		}
	}
	
	public void decryptFile(String destName){
		JFileChooser fc = new JFileChooser();													//Read the file
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fc.setDialogTitle("Select blocked file to encrypt");
		File readFile;

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			readFile = fc.getSelectedFile();
			//This is where a real application would open the file.
		}else{
			System.out.println("Error reading in file");
			return;
		}
		
		try{
		
			BufferedReader reader = new BufferedReader(new FileReader(readFile));
			String lineRead;
			
			File deEncFile = new File(destName);
			deEncFile.createNewFile();
			
			PrintWriter write = new PrintWriter(destName+".txt");
			HugeInt nTemp = nValue;
			
			while((lineRead = reader.readLine()) != null){
				System.out.println(lineRead);
				
				HugeInt m = new HugeInt(lineRead);								
				HugeInt env = new HugeInt(1);
				HugeInt c = new HugeInt("1");															//C = 1
				
				//loop to decrypt every line
				for(HugeInt i = new HugeInt("0"); env.compareHugeInts(nValue, i) == -1; i.addHugeInt(new HugeInt("1")) ){
					nValue = new HugeInt(nTemp);
					c.multiplyByHugeInt(m);																//C=(C*M)%n
					c.modByHugeInt(nValue);
				}
				write.println(c);
			}
		reader.close();	
		write.close();
		}catch(IOException e){
		}
		
	}
	
	public static void main(String[] args){
		KeyPair test = new KeyPair();
		System.out.println("Public key: ");
		test.eValue.printHugeInt();
		System.out.println("Private key: ");
		test.dValue.printHugeInt();
		System.out.println("n value: ");
		test.nValue.printHugeInt();
		
		test.encryptFile("test2Enc");
		test.decryptFile("test2Dec");
		
	}
	
}
