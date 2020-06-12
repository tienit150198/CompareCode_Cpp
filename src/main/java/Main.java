
/*
 * CompareCode
 *
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main implements ItemListener {
	JPanel cards; // a panel that uses CardLayout
	final static String COMPARE_FILE = "Compare 2 File Code";
	final static String COMPARE_FOLDER = "Compare Folder";
	private JPanel card1;
	private JPanel card2;
	private JFileChooser jfc, jfcFolder;
	private List<CodeInformation> listAllCode;
	private FileService fileService;
	private JTextArea taLeft;
	private JTextArea taRight;
	private JTextField tfFilePathLeft;
	private JTextField tfFilePathRight;
	private DocumentListener documentListenerA;
	private DocumentListener documentListenerB;
	boolean checkAccept = false;
	boolean checkEditted = false;
	private JList<String> listFile;
	private DefaultListModel<String> dfList;
	private static ArrayList<CodeInformation> listCode;
	static Map<Integer, List<String>> sameList;
	private static JTextField tfRatio;
	int algorithmType = 1;
	private JRadioButton rdbtnEditDistance;
	private JRadioButton rdbtnBruteForce;
	private JButton btnAcceptTwoFile;
	private JButton btnAcceptListFile;

	// TODO: cal time
	private static long timeStart, timeEnd;

	public void initialization() {

		customJFileChoose();
		customDocumentListener();
		customJList();
		initOther();

	}

	private void customJList() {
		dfList = new DefaultListModel<>();
		// dfList.addElement("code1.cpp");
		// dfList.addElement("code2.cpp");
		listFile = new JList<>(dfList);

		listFile.setVisibleRowCount(20);
	}

	private static ActionListener createStartTaskActionListener(Component parent, ArrayList<CodeInformation> listCode) {
		UIManager.put("ProgressMonitor.progressText", "Process");

		if (!tfRatio.getText().trim().equals("")) {
			return (ae) -> {
				double ratio = Double.parseDouble(tfRatio.getText());
				timeStart = System.currentTimeMillis();
				MySystemManager.start(2, 1, listCode, ratio); // default = edit distance
				new Thread(() -> {
					// creating ProgressMonitor instance
					int _maxProcess = listCode.size();
					ProgressMonitor pm = new ProgressMonitor(parent, "Running", "Start", 0, _maxProcess);

					pm.setMillisToDecideToPopup(0);
					pm.setMillisToPopup(100);
					for (int i = 1; i <= _maxProcess; i++) {
						// updating ProgressMonitor note
						pm.setNote("Running task: " + i);
						// updating ProgressMonitor progress
						pm.setProgress(i);
//						try {
//							// delay for task simulation
//							TimeUnit.MILLISECONDS.sleep(150);
//						} catch (InterruptedException e) {
//							System.err.println(e);
//						}
					}

					if (!pm.isCanceled()) {
						timeEnd = System.currentTimeMillis();
						System.out.println("TIME = " + (timeEnd - timeStart));
						pm.setNote("Completed");
						sameList = MySystemManager.getSameList();
						if (sameList.size() > 0) {
							System.out.println("TIME = " + (timeEnd - timeStart));
							new DetailFolder(sameList);

							// clear when finish
							MySystemManager.clear();

						} else {

						}
					}

//					System.out.println(sameList);
				}).start();

			};
		} else {
			JOptionPane.showMessageDialog(null, "Please input ratio expected", "Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return null;

	}

	public void customDocumentListener() {
		documentListenerA = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTextArea(taLeft, taRight, "A", 0);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTextArea(taLeft, taRight, "A", 0);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateTextArea(taLeft, taRight, "A", 0);
			}
		};

		documentListenerB = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTextArea(taRight, taLeft, "B", 1);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTextArea(taRight, taLeft, "B", 1);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateTextArea(taRight, taLeft, "B", 1);
			}
		};
	}

	public void initOther() {
		listAllCode = new ArrayList<>();
		fileService = new FileService();
		listCode = new ArrayList<>();
		sameList = new HashMap<>();
	}

	public void customJFileChoose() {
		jfc = new JFileChooser();
		jfcFolder = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("file code cpp or c", "cpp", "c");
		jfc.setFileFilter(filter);
		jfc.setMultiSelectionEnabled(false);

		jfcFolder.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfcFolder.setMultiSelectionEnabled(true);
		jfcFolder.setFileFilter(filter);
	}

	public void addComponent_CompareFile() {
		initialization();
		card1 = new JPanel();

		ButtonGroup buttonGroup = new ButtonGroup();

		card1.setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();
		card1.add(toolBar, BorderLayout.NORTH);

		JButton btnNewButton_1 = new JButton("New button");
		toolBar.add(btnNewButton_1);

		JPanel pnBottom = new JPanel();
		card1.add(pnBottom, BorderLayout.SOUTH);
		pnBottom.setLayout(new GridLayout(1, 0, 8, 0));

		JPanel pnBottomLeft = new JPanel();
		pnBottom.add(pnBottomLeft);
		pnBottomLeft.setLayout(new BoxLayout(pnBottomLeft, BoxLayout.Y_AXIS));

		JTextField tfResult = new JTextField();
		tfResult.setHorizontalAlignment(SwingConstants.CENTER);
		tfResult.setFont(new Font("Dialog", Font.PLAIN, 18));
		tfResult.setForeground(Color.RED);
		tfResult.setEditable(false);
		pnBottomLeft.add(tfResult);
		tfResult.setColumns(10);

		tfResult.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Result"));

		JPanel panel_3 = new JPanel();
		pnBottomLeft.add(panel_3);

		JButton btnDetail = new JButton("Detail");
		btnDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkAccept) {
					List<CodeInformation> listAllCode_copy = MySystemManager.getlistAllCode();
					new DetailTwoFile(listAllCode_copy.get(0).getStepCode(), listAllCode_copy.get(1).getStepCode());
				} else {
					JOptionPane.showMessageDialog(null, "Please insert code", "Message", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel_3.add(btnDetail);

		btnAcceptTwoFile = new JButton("Accept");
		btnAcceptTwoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (listAllCode.size() == 2 && !taLeft.getText().trim().equals("")
						&& !taRight.getText().trim().equals("")) {
					checkAccept = true;

					isCheckedRadioButton(rdbtnEditDistance, rdbtnBruteForce);

					timeStart = System.currentTimeMillis();
					MySystemManager.start(1, algorithmType, listAllCode, 0);

					double ratio = MySystemManager.getRatio();
					timeEnd = System.currentTimeMillis();
					tfResult.setText(new DecimalFormat("####0.00").format(ratio));

					System.out.println("TOTAL TIME = " + (timeEnd - timeStart));
					btnAcceptTwoFile.setEnabled(false);
				} else {
					JOptionPane.showMessageDialog(null, "Please insert code", "Message", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel_3.add(btnAcceptTwoFile);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listAllCode.clear();
				taLeft.setText("");
				taRight.setText("");
				tfResult.setText("");
				tfFilePathLeft.setText("");
				tfFilePathRight.setText("");
			}
		});
		panel_3.add(btnClear);

		JPanel pnBottomRight = new JPanel();
		pnBottom.add(pnBottomRight);
		pnBottomRight.setLayout(new BoxLayout(pnBottomRight, BoxLayout.Y_AXIS));

		pnBottomRight
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Algorithm"));

		rdbtnEditDistance = new JRadioButton("Edit Distance");
		rdbtnEditDistance.setSelected(true);
		buttonGroup.add(rdbtnEditDistance);
		pnBottomRight.add(rdbtnEditDistance);

		rdbtnBruteForce = new JRadioButton("Brute Force");
		buttonGroup.add(rdbtnBruteForce);
		pnBottomRight.add(rdbtnBruteForce);

		rdbtnBruteForce.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				updateTextArea(taLeft, taRight, "A", 0);
				btnAcceptTwoFile.setEnabled(true);
			}
		});

		JPanel pnCenter = new JPanel();
		card1.add(pnCenter, BorderLayout.CENTER);
		pnCenter.setLayout(new GridLayout(1, 0, 8, 0));

		JPanel pnLeft = new JPanel();
		pnCenter.add(pnLeft);
		pnLeft.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		pnLeft.add(scrollPane, BorderLayout.CENTER);

		JPanel pnScrollLeft = new JPanel();
		scrollPane.setColumnHeaderView(pnScrollLeft);
		pnScrollLeft.setLayout(new BorderLayout(0, 0));

		tfFilePathLeft = new JTextField();
		tfFilePathLeft.setEditable(false);
		tfFilePathLeft.setColumns(10);
		pnScrollLeft.add(tfFilePathLeft, BorderLayout.CENTER);

		JButton btnFileLeft = new JButton("...");
		btnFileLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();

					System.out.println(selectedFile.getAbsolutePath());
					tfFilePathLeft.setText(selectedFile.getAbsolutePath());

					CodeInformation info = fileService.getCodeInfor(selectedFile.getAbsolutePath());

					//

					taLeft.setText(Utility.convertListToString_default(info.getListCode()));

					if (info != null) {
						if (listAllCode.size() != 2) {
							listAllCode.add(info);
						} else {
							listAllCode.set(0, info);
						}
					}

				}
			}
		});
		btnFileLeft.setToolTipText("select files");
		pnScrollLeft.add(btnFileLeft, BorderLayout.EAST);

		taLeft = new JTextArea();
		taLeft.getDocument().addDocumentListener(documentListenerA);

		taLeft.setRows(20);
		taLeft.setColumns(30);
		scrollPane.setViewportView(taLeft);

		JPanel pnRight = new JPanel();
		pnCenter.add(pnRight);
		pnRight.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		pnRight.add(scrollPane_1, BorderLayout.CENTER);

		JPanel pnScrollRight = new JPanel();
		scrollPane_1.setColumnHeaderView(pnScrollRight);
		pnScrollRight.setLayout(new BorderLayout(0, 0));

		tfFilePathRight = new JTextField();
		tfFilePathRight.setEditable(false);
		tfFilePathRight.setColumns(10);
		pnScrollRight.add(tfFilePathRight, BorderLayout.CENTER);

		JButton btnFileRight = new JButton("...");
		btnFileRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					System.out.println(selectedFile.getAbsolutePath());
					tfFilePathRight.setText(selectedFile.getAbsolutePath());

					CodeInformation info = fileService.getCodeInfor(selectedFile.getAbsolutePath());

					//
					taRight.setText(Utility.convertListToString_default(info.getListCode()));

					if (info != null) {
						if (listAllCode.size() != 2) {
							listAllCode.add(info);
						} else {
							listAllCode.set(1, info);
						}
					}
				}
			}
		});
		btnFileRight.setToolTipText("select files");
		pnScrollRight.add(btnFileRight, BorderLayout.EAST);

		taRight = new JTextArea();
		taRight.getDocument().addDocumentListener(documentListenerB);

		taRight.setRows(20);
		scrollPane_1.setViewportView(taRight);

		setLineNumber(scrollPane, scrollPane_1, taLeft, taRight);
	}

	private void isCheckedRadioButton(JRadioButton rdbtnEditDistance, JRadioButton rdBtnBruteForce) {
		if (rdbtnEditDistance.isSelected())
			algorithmType = 1;
		else if (rdBtnBruteForce.isSelected())
			algorithmType = 2;
	}

	private void setLineNumber(JScrollPane scrollPanel1, JScrollPane scrollPanel2, JTextArea ta1, JTextArea ta2) {
		TextLineNumber tln1 = new TextLineNumber(ta1);
		TextLineNumber tln2 = new TextLineNumber(ta2);

		scrollPanel1.setRowHeaderView(tln1);
		scrollPanel2.setRowHeaderView(tln2);
	}

	public void updateTextArea(JTextArea textArea, JTextArea textAreaOther, String fileName, int index) {
		checkAccept = false;
		btnAcceptTwoFile.setEnabled(true);
		CodeInformation info = Utility.convertTextAreaToCodeInformation(textArea, fileName);
		CodeInformation info2 = Utility.convertTextAreaToCodeInformation(textAreaOther, fileName);

		listAllCode.clear();

		if (index == 0) {
			listAllCode.add(info);
			listAllCode.add(info2);
		} else {
			listAllCode.add(info2);
			listAllCode.add(info);
		}

	}

	public void addComponent_ListFile() {

		card2 = new JPanel();
		card2.setLayout(new BorderLayout(8, 8));

		JPanel pnEast = new JPanel();
		card2.add(pnEast, BorderLayout.EAST);
		JPanel panelWest = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelWest.getLayout();
		flowLayout.setHgap(10);
		card2.add(panelWest, BorderLayout.WEST);

		GridBagLayout gbl_pnEast = new GridBagLayout();
		gbl_pnEast.columnWidths = new int[] { 20, 8 };
		gbl_pnEast.rowHeights = new int[] { 100, 23, 30, 23, 99 };
		gbl_pnEast.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_pnEast.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		pnEast.setLayout(gbl_pnEast);

		JButton btnAdd = new JButton("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 1;
		pnEast.add(btnAdd, gbc_btnAdd);

		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = jfcFolder.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File[] selectedFiles = jfcFolder.getSelectedFiles();

					for (File file : selectedFiles) {
						System.out.println(file.getAbsolutePath());
						if (RegexService.isAcceptFile(file.getAbsolutePath())) {
							CodeInformation info = fileService.getCodeInfor(file.getAbsolutePath());
							if (!listCode.contains(info)) {
								listCode.add(info);
							}
						} else {
							listCode.addAll((ArrayList<CodeInformation>) fileService.readCodeInfolder(file));
						}
					}

					HashSet<CodeInformation> setTmp = new HashSet<CodeInformation>();
					setTmp.addAll(listCode);
//					listCode = new ArrayList<>();
					listCode.clear();
					listCode.addAll(setTmp);
					setTmp.clear();

//					System.out.println("SIZE = " + listCodeSave.size() + " - " + listCode.size());

					if (listCode.size() == 0) {
						JOptionPane.showMessageDialog(null, "Folder does not exist c or cpp file", "Message",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						listCode.forEach(file -> {
							if (!dfList.contains(file.getFileName()))
								dfList.addElement(file.getFileName());
						});
						// System.out.println(dfList.size());
						listFile.setModel(dfList);
					}
				}

			}
		});

		JButton btnDelete = new JButton("Delete");
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = 3;
		pnEast.add(btnDelete, gbc_btnDelete);

		btnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// descending order - exception IndexOutofbounds
				List<Integer> listSelectedIndices = Arrays.stream(listFile.getSelectedIndices()).boxed()
						.collect(Collectors.toList());
				Collections.sort(listSelectedIndices, Collections.reverseOrder());

				for (Integer index : listSelectedIndices) {
					String name = dfList.get(index);
					dfList.removeElement(name);

					for (CodeInformation codeInfo : listCode) {
						if (codeInfo.getFileName().equals(name)) {
							listCode.remove(codeInfo);
							break;
						}
					}
				}

				listFile.setModel(dfList);

			}
		});

		JPanel pnBottom = new JPanel();
		card2.add(pnBottom, BorderLayout.SOUTH);

		btnAcceptListFile = new JButton("Accept");
		pnBottom.add(btnAcceptListFile);

		JScrollPane scrollPane = new JScrollPane();
		card2.add(scrollPane, BorderLayout.CENTER);
		btnAdd.setPreferredSize(btnDelete.getPreferredSize());

		scrollPane.setViewportView(listFile);

		JPanel pnRatio = new JPanel();
		scrollPane.setColumnHeaderView(pnRatio);

		JLabel lbRatio = new JLabel("T\u1EF7 l\u1EC7:");
		lbRatio.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pnRatio.add(lbRatio);

		tfRatio = new JTextField("70.0");
		tfRatio.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pnRatio.add(tfRatio);
		tfRatio.setColumns(10);

		btnAcceptListFile.addActionListener(createStartTaskActionListener(listFile, listCode));
	}

	public void addComponentToPane(Container pane) {
		JPanel comboBoxPane = new JPanel(); // use FlowLayout
		String comboBoxItems[] = { COMPARE_FILE, COMPARE_FOLDER };
		JComboBox<?> cb = new JComboBox<Object>(comboBoxItems);
		cb.setEditable(false);
		cb.addItemListener(this);
		comboBoxPane.add(cb);

		/* 
		  *****************
		 */
		addComponent_CompareFile();
		addComponent_ListFile();

		// Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(card1, COMPARE_FILE);
		cards.add(card2, COMPARE_FOLDER);

		pane.add(comboBoxPane, BorderLayout.PAGE_START);
		pane.add(cards, BorderLayout.CENTER);
	}

	public void itemStateChanged(ItemEvent evt) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, (String) evt.getItem());
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Compare Code");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setResizable(false);
		// Create and set up the content pane.
		Main demo = new Main();
		demo.addComponentToPane(frame.getContentPane());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
