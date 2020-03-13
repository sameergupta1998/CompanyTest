package com.cerner.devcenter.rulemigration.sameer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Returns VBtoXMLStringValue that is passed to the UI class. The converted XML
 * string is displayed after the convert_btn is clicked. This string value is
 * written on the local system in the formatted manner.
 * 
 * @param path an absolute file path giving location of the text file
 * @return VBtoXMLStringValue to UI class
 * @see Vb to Xml converted string
 */
public class VbtoXmlLogic {
	public String vbToXml(String path) throws IOException, ParserConfigurationException {

		int j = 0;
		int k = 0;
		String ab = null;
		int counter = 1;
		int counterElseCall = 1;
		int counterIfCall = 1;
		int flag = 0;
		int flag1 = 0;
		int c = 0;
		int operatorIndex = 0;
		String a=null;
		String strRead = null;
		String lhsValue = null;
		String rhsValue = null;
		String map_callString = "";
		String map_callString1 = "";
		String remQuotes = null;
		String[] fileValue = null;
		String operatorValue = null;
		String VBtoXMLStringValue = null;
		String[] split_action1= new String[50];
		String operatorStringValue = null;
		Map<Integer, String> map = new HashMap<Integer, String>();
		Map<Integer, String> mapLHS = new HashMap<Integer, String>();
		Map<Integer, String> mapOperatorwithIndex = new HashMap<Integer, String>();
		Map<Integer, String> map9 = new HashMap<Integer, String>();
		Map<Integer, String> mapOperator = new HashMap<Integer, String>();
		Map<Integer, String> mapRHS = new HashMap<Integer, String>();
		Map<Integer, String> map_call = new HashMap<Integer, String>();
		Map<Integer, String> map_elseCall = new HashMap<Integer, String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			while ((strRead = reader.readLine()) != null) {
				String data = strRead.trim();
				String delimiters = "[\\s\\;\n\r,]";
				remQuotes = data.replaceAll("\"", "").replaceAll(",", "");
				fileValue = remQuotes.split(delimiters);

				for (int i = 0; i < fileValue.length; i++, ++k) {
					
					for (j = i; j < fileValue.length; j++) {
						if(!fileValue[i].contentEquals("end")) { 
						if (fileValue[j].contentEquals("else")) {
							flag1 = 1;
						}
						
						if (flag1 == 0) {
							if (fileValue[j].contentEquals("call")) {
								map_call.put(counterIfCall, fileValue[j + 1]); // fileValue[j+1]=action-parameter value for if
								counterIfCall++;
							}
						}
						
						if (flag1 == 1) {
							if (fileValue[j].contentEquals("call")) {
								map_elseCall.put(counterElseCall, fileValue[j + 1]); // fileValue[j+1]=action-parameter value for else 
								counterElseCall++;
							}
						}
					
					
					map.put(k, fileValue[i]);	// inserting all elements in map with index
						}
				}
					if (map.containsValue(">") || map.containsValue("<") || map.containsValue("=")|| map.containsValue("!=")) {
							if (map.get(i).contentEquals(">") || map.get(i).contentEquals("<") || map.get(i).contentEquals("=")
									|| map.get(i).contentEquals("!=")) {
										c++;
										mapOperator.put(c, map.get(i));			
										mapLHS.put(c,map.get(i-1));
										mapOperatorwithIndex.put(i,map.get(i));							}
					}
				}
			}

			System.out.println("The elements in the map are\n" + map);			//original map elements
			System.out.println("\nThe count of number of operators present="+c);	//count
			System.out.println("\nOperator values with index in original map\n"+mapOperatorwithIndex);	//Operator with index
			
			for(int i=20;i>0;i--)
			{if(mapOperatorwithIndex.containsKey(i))
			{mapRHS.put(counter,map.get(i+1));
			counter++;}}
			
			System.out.println("\nLHS values to Operator are\n"+mapLHS);			//LHS
			System.out.println("\nOperators are"+mapOperator);					//function
			System.out.println("\nRHS values to Operator are\n"+mapRHS);			//RHS
			
			System.out.println("\nThe Action map values are\n" + map_call);		//if call action map
			//System.out.println("\nhell0\n" + map_elseCall);						//else call action map
			
			
			// First check whether the rule contains with "if"
			if (map.containsValue("if")) {

				// Create artifact root element
				Element artifactRootElement = doc.createElement("com.cerner.revenuecycle.rules.app.ml");
				doc.appendChild(artifactRootElement);

					// Create condition element with type attribute appended to main artifactRootElement
					Element conditionElement = doc.createElement("condition");
					Attr attr1 = doc.createAttribute("type");
					attr1.setValue("Binary Expression");
					conditionElement.setAttributeNode(attr1);
					artifactRootElement.appendChild(conditionElement);

					// Then check if map contains operator
					if (map.containsValue(">") || map.containsValue("<") || map.containsValue("=")
							|| map.containsValue("!=")) {
						for(int i=1;i<c+1;i++)
						{
						// Create LHS Element with key and type attribute appended to condition
						Element lhsElement = doc.createElement("leftHandSide");
						Attr attr2 = doc.createAttribute("key");
						attr2.setValue(mapLHS.get(i));
						lhsElement.setAttributeNode(attr2);
						Attr attr3 = doc.createAttribute("type");
						attr3.setValue("");
						lhsElement.setAttributeNode(attr3);
						conditionElement.appendChild(lhsElement);

						// Create RHS Element with type and value attribute appended to condition
						Element rhsElement = doc.createElement("rightHandSide");
						Attr attr4 = doc.createAttribute("type");
						attr4.setValue("");
						rhsElement.setAttributeNode(attr4);
						Attr attr5 = doc.createAttribute("value");
						attr5.setValue(mapRHS.get(i));
						rhsElement.setAttributeNode(attr5);
						conditionElement.appendChild(rhsElement);

						// Create Function Element with type attribute appended to condition
						Element functionElement = doc.createElement("function");
						Attr attr6 = doc.createAttribute("type");
						
						if(mapOperator.get(i).contentEquals(">"))		a="GreaterThan";
						else if(mapOperator.get(i).contentEquals("<"))	a="LessThan";
						else if(mapOperator.get(i).contentEquals("="))	a="Equals";
						else											a="NotEquals";
						attr6.setValue(a);
						functionElement.setAttributeNode(attr6);
						conditionElement.appendChild(functionElement);
				
						}
					}
				
					
				//Splitting for "if" call statements	
				for (int i = 1; i < counterIfCall; i++) {
					map_callString += map_call.get(i);	
				}
			
				System.out.println("\nAction map values put into string\n"+map_callString);// Putting map_call values to map_callString
				String data = map_callString.trim();
				String delimiters = "[\\s\\;\n\r()]";
				String[] split_action = new String[50];
				split_action = data.split(delimiters); // Splitting the map_callString. Removing () brackets

				System.out.println("\nThe key-Value after call method are:");
				for (int i = 0; i < split_action.length; i++) {
					System.out.println(split_action[i] + "  " + i);
				}

				
				//Splitting for "else" call statements
				if(counterElseCall>1) {
				for (int i = 1; i < counterElseCall; i++) {
					map_callString1 += map_elseCall.get(i);		// Putting map_call values to map_callString
				}
				
				System.out.println("\nThe elseCall Map values are\n"+map_callString1);
				String data1 = map_callString1.trim();
				String delimiters1 = "[\\s\\;\n\r()]";
				split_action1 = new String[50];
				split_action1 = data1.split(delimiters); // Splitting the map_callString. Removing () brackets
				System.out.println("\nThe key-Value after call method are:");
				for (int i = 0; i < split_action1.length; i++) {
					System.out.println(split_action1[i] + "  " + i);
				}
				}
				
				
				int action_length = split_action.length;
				System.out.println("\nIf Call actions length="+action_length);
		
				// Create actions element with type attribute appended to main artifactRootElement
				Element ActionElement = doc.createElement("actions");
				Attr attr7 = doc.createAttribute("type");
				attr7.setValue("");
				ActionElement.setAttributeNode(attr7);
				artifactRootElement.appendChild(ActionElement);

				for (j = 0; j < action_length; j += 2) {
					// Create actions element with description and type attribute appended to actions
					Element acElement1 = (doc.createElement("actions"));
					Attr attr8 = doc.createAttribute("typeMeaning");
					attr8.setValue(split_action[j]);
					acElement1.setAttributeNode(attr8);
					Attr attr9 = doc.createAttribute("description");
					attr9.setValue("");
					acElement1.setAttributeNode(attr9);
					ActionElement.appendChild(acElement1);

					// Create parameters element with key attribute appended to actions
					Element valElement1 = doc.createElement("parameters");
					Attr attr10 = doc.createAttribute("key");
					attr10.setValue("");
					valElement1.setAttributeNode(attr10);
					acElement1.appendChild(valElement1);

					// Create value element with type and value attribute appended to parameters
					Element valElement2 = doc.createElement("value");
					Attr attr11 = doc.createAttribute("type");
					attr11.setValue("");
					valElement2.setAttributeNode(attr11);
					Attr attr12 = doc.createAttribute("value");
					attr12.setValue(split_action[j + 1]);
					valElement2.setAttributeNode(attr12);
					valElement1.appendChild(valElement2);
				}
				
				if (map.containsValue("else")) {
				Element ElseActionElement = doc.createElement("elseactions");
				Attr attr20 = doc.createAttribute("type");
				attr20.setValue("");
				ActionElement.setAttributeNode(attr20);
				artifactRootElement.appendChild(ElseActionElement);

				
					for (j = 0; j < action_length; j += 2) {
						// Create actions element with description and type attribute appended to
						// actions
						Element acElement2 = (doc.createElement("actions"));
						Attr attr13 = doc.createAttribute("typeMeaning");
						attr13.setValue(split_action1[j]);
						acElement2.setAttributeNode(attr13);
						Attr attr14 = doc.createAttribute("description");
						attr14.setValue("");
						acElement2.setAttributeNode(attr14);
						ElseActionElement.appendChild(acElement2);

						// Create parameters element with key attribute appended to actions
						Element valElement3 = doc.createElement("parameters");
						Attr attr15 = doc.createAttribute("key");
						attr15.setValue("");
						valElement3.setAttributeNode(attr15);
						acElement2.appendChild(valElement3);

						// Create value element with type and value attribute appended to parameters
						Element valElement4 = doc.createElement("value");
						Attr attr16 = doc.createAttribute("type");
						attr16.setValue("");
						valElement4.setAttributeNode(attr16);
						Attr attr17 = doc.createAttribute("value");
						attr17.setValue(split_action1[j + 1]);
						valElement4.setAttributeNode(attr17);
						valElement3.appendChild(valElement4);
					}
				}
				
				
				// Transform Document to XML String
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(writer));
				VBtoXMLStringValue = writer.getBuffer().toString();
				XmlFormatter formatter = new XmlFormatter();
				// Printing the VbtoXml conversion value on console
				System.out.println("\nVB to Xml converted value\n\n " + formatter.format(VBtoXMLStringValue));

				// Writing output to new file at desktop
				File file = new File(
						"C:\\\\Users\\\\SG078341\\\\OneDrive - Cerner Corporation\\\\Desktop\\\\ConvertedFile"+ ".xml");
				Writer writexml = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(writexml);
				bufferedWriter.write("\n" + formatter.format(VBtoXMLStringValue));
				bufferedWriter.close();
			}
			reader.close();
		}

	catch(

	TransformerException e)
	{
			e.printStackTrace();
		}return VBtoXMLStringValue;
}

// Class for XML Style Formatting
public static class XmlFormatter {

	public String format(String VBtoXMLStringValue) {
		return prettyFormat(VBtoXMLStringValue, "2");
	}

	public String prettyFormat(String xml2, String indent) {
		Source xmlInput = new StreamSource(new StringReader(xml2));
		StringWriter stringWriter = new StringWriter();
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", indent);
			transformer.transform(xmlInput, new StreamResult(stringWriter));
			return stringWriter.toString().trim();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

}