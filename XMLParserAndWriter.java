import java.io.File;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//This class is a tool that allows the user to parse and write XML files containing the keyPair values

public class XMLParserAndWriter {
	
	File pubKeySource;
	File privKeySource;
	
	public XMLParserAndWriter(){}
	
	//This function sets up the call to the parseHelper which does the actual parsing, this simply get the required input
	public KeyPair parse() throws Exception{
		JFileChooser fc = new JFileChooser();													//Read the file chosen by the user
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fc.setDialogTitle("Select privateKey XML file to use");

		int returnVal = fc.showOpenDialog(null);																//read private key

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			privKeySource = fc.getSelectedFile();
			//This is where a real application would open the file.
		}else{
			System.out.println("Error reading in file");
			System.exit(0);
		}
		
		fc.setDialogTitle("Select pubKey XML file to use");													//Read public key
		returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			pubKeySource = fc.getSelectedFile();

		}else{
			System.out.println("Error reading in file");
			System.exit(0);
		}
		
		return parseHelper();
	}
	
	//Uses DocumentBuilderFactory to help parse and write XML files
	private KeyPair parseHelper() throws Exception{
		
		KeyPair read = new KeyPair();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		//setup public key file reader
		DocumentBuilder pubkeyBuilder = factory.newDocumentBuilder();
		Document pubkeyDoc = pubkeyBuilder.parse(this.pubKeySource);
		
		//setup private key file reader
		DocumentBuilder privkeyBuilder = factory.newDocumentBuilder();
		Document privkeyDoc = privkeyBuilder.parse(this.privKeySource);
		
		//Reads the first child item of the respective tag and stores them
		 String nValue = pubkeyDoc.getElementsByTagName("nvalue").item(0).getFirstChild().getNodeValue();
		 String eValue = pubkeyDoc.getElementsByTagName("evalue").item(0).getFirstChild().getNodeValue();
		 String dValue = privkeyDoc.getElementsByTagName("dvalue").item(0).getFirstChild().getNodeValue();
		 
		 //puts them into the new key
		 read.dValue = new HugeInt(dValue);
		 read.eValue = new HugeInt(eValue);
		 read.nValue = new HugeInt(nValue);
		
		return read;
	}
	
	//Writes a pair of keys to XML adding an input prefix before each file name
	public void write(KeyPair key, String prefix) throws Exception{
		writePublicKey(key, prefix);
		writePrivateKey(key, prefix);
	}
	
	//Writes the public key file 
	private void writePublicKey(KeyPair key, String prefix) throws Exception{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		Document doc = builder.newDocument();
		
		//Create main element
		Element rsakey = doc.createElement("rsakey");
		doc.appendChild(rsakey);
		
		//Create evalue subElement and append it to the rsaKey element
		Element e = doc.createElement("evalue");
		rsakey.appendChild(e);
		e.appendChild(doc.createTextNode(key.eValue.toString()));
		
		//Create nvalue subElement and append it to the rsaKey element
		Element n = doc.createElement("nvalue");
		rsakey.appendChild(n);
		n.appendChild(doc.createTextNode(key.nValue.toString()));
		
		//Create the key key with the preferred prefix given with the call
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(prefix + "Pubkey.xml"));
		transformer.transform(source, result);
	}
	
	//Writes the public key file 
	private void writePrivateKey(KeyPair key, String prefix) throws Exception{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		Document doc = builder.newDocument();
		
		//Creates rsa key header element to the file
		Element rsakey = doc.createElement("rsakey");
		doc.appendChild(rsakey);
		
		//Creates dvalue key header element to the file
		Element d = doc.createElement("dvalue");
		rsakey.appendChild(d);
		d.appendChild(doc.createTextNode(key.dValue.toString()));
		
		//Creates nvalue key header element to the file
		Element n = doc.createElement("nvalue");
		rsakey.appendChild(n);
		n.appendChild(doc.createTextNode(key.nValue.toString()));
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(prefix + "Privkey.xml"));
		transformer.transform(source, result);
	}
	
	public static void main(String[] args) throws Exception{
		XMLParserAndWriter test = new XMLParserAndWriter();
		KeyPair testKey = new KeyPair();
		testKey.dValue = new HugeInt(""+6943);
		testKey.eValue = new HugeInt(""+7);
		testKey.nValue = new HugeInt(""+16463);
		
		test.write(testKey, "test");
		
		KeyPair readKey = test.parse();
		System.out.println("d " + readKey.dValue.toString() + "e "+ readKey.eValue.toString() + "n "+ readKey.nValue.toString());
	}
}
