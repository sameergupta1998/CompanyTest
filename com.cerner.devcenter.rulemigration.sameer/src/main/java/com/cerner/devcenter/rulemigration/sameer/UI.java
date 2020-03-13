package com.cerner.devcenter.rulemigration.sameer;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * @author Sameer Gupta The UI class is implemented with SWT widgets for
 *         browsing files from local system, parsing the text format and
 *         converting to XML string. The new file is saved on required location
 *         on computer.
 */
public class UI extends VbtoXmlLogic {

	Display display = Display.getDefault();
	String data = null;
	public static String str4;
	public static Shell shell = new Shell();
	public static Text file_path;

	public UI() {
		initialize();
		shell.pack();
		shell.setSize(500, 248);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * The initialize() method implements SWT widgets for User Interface and sets
	 * their position in shell. The event handling of the buttons - browse, cancel
	 * and convert are defined.
	 */
	public void initialize() {

		shell.setText("Rule Migration App");
		shell.setLayout(null);

		Label lblEnterFilePath = new Label(shell, SWT.NONE);
		lblEnterFilePath.setBounds(10, 21, 131, 25);
		lblEnterFilePath.setText("Enter File Path");

		file_path = new Text(shell, SWT.BORDER);
		file_path.setBounds(10, 59, 319, 35);

		Button browse_btn = new Button(shell, SWT.PUSH);
		browse_btn.setBounds(357, 57, 87, 35);
		browse_btn.setText("Browse");

		Button convert_btn = new Button(shell, SWT.PUSH);
		convert_btn.setBounds(48, 117, 103, 35);
		convert_btn.setText("Convert");

		Button reset_btn = new Button(shell, SWT.PUSH);
		reset_btn.setBounds(188, 117, 103, 35);
		reset_btn.setText("Reset");

		browse_btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// String path1 = file_path.getText();
				FileDialog dialog = new FileDialog(shell, SWT.NULL);
				String path = dialog.open();
				if (path != null) {
					File file = new File(path);
					if (file.isFile())
						displayFiles(new String[] { file.toString() });
					else
						displayFiles(file.list());
				}
			}
		});

		convert_btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					String path = file_path.getText();
					VbtoXmlLogic xmlObj = new VbtoXmlLogic();
					xmlObj.vbToXml(path);
				}

				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		reset_btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				file_path.setText("");
			}
		});

	}

	public void displayFiles(String[] files) {
		for (int i = 0; files != null && i < files.length; i++) {
			file_path.setText(files[i]);
			file_path.setEditable(true);
		}
	}

	/**
	 * The UI() method is called to run the java program which executes the User
	 * Interface and the conversion logic from Vb to XML format. The new file is
	 * written and saved on the local system
	 */
	public static void main(String[] args) throws IOException {
		new UI();
	}	
}

// Inserting "null" value if last action has empty parameter
/*
 * if (action_length % 2 == 1) { String newItem = "null"; int currentSize =
 * split_action.length; int newSize = currentSize + 1; String[] tempArray = new
 * String[newSize]; // tempArray created with size greater by 1 for (int i = 0;
 * i < currentSize; i++) { tempArray[i] = split_action[i]; } tempArray[newSize -
 * 1] = newItem; // inserting newItem value at last index split_action =
 * tempArray; // new element("null") copied to original string array System.out
 * .println("\nAdded null parameter to last action. New call method action-parameters are:\n"
 * ); for (String element : split_action) { System.out.println(element); } }
 */




//for getting index and value of operators in original map
//for (Map.Entry<Integer,String> entry : map.entrySet()) 
//{ if (entry.getValue().equals(">") ||entry.getValue().equals("<") || entry.getValue().equals("=") ||
//		 entry.getValue().equals("!="))
//		{ map10.put(entry.getKey(),entry.getValue());
//			operatorIndex = entry.getKey();
//		 	operatorValue = entry.getValue();
//		  }
//		  } 