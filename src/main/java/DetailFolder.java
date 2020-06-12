

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DetailFolder {

	private JFrame frame;
	private JTable table;
	Map<Integer, List<String>> sameList;

	/**
	 * Create the application.
	 */
	public DetailFolder(Map<Integer, List<String>> sameList) {
		this.sameList = sameList;
		initialize();
	}

	public void doShow() {
		JPanel pnCenter = new JPanel();
		frame.getContentPane().add(pnCenter, BorderLayout.CENTER);
		pnCenter.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		pnCenter.add(scrollPane);

		DefaultTableModel dfTable = new DefaultTableModel();
		dfTable.addColumn("Name");
		dfTable.addColumn("Ingredient");

		Set<Integer> keySet = sameList.keySet();
		for (Integer id : keySet) {
			List<String> listIngredient = sameList.get(id);
			String strIngredient = listIngredient.toString().substring(1, listIngredient.toString().length() - 1);
			String nameColumn = String.valueOf("Group " + id);
			dfTable.addRow(new String[] { nameColumn, strIngredient });
		}

		table = new JTable(dfTable);
		table.setFont(new Font("Tahoma", Font.PLAIN, 18));
		table.setDefaultEditor(Object.class, null);
		table.setRowHeight(30);
		table.setRowHeight(0, 30);
		table.setRowMargin(2);
		table.getColumnModel().getColumn(0).setPreferredWidth(112);
		table.getColumnModel().getColumn(1).setPreferredWidth(534);

		scrollPane.setViewportView(table);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		doShow();
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

}
