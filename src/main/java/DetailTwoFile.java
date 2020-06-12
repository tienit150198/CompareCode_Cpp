
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.SystemColor;

public class DetailTwoFile {

	private JFrame frame;
	private JTextArea taLeft;
	private JTextArea taRight;
	private JButton btnNext;
	private JButton btnReturn;
	private JButton btnBack;
	int numberStep;
	ArrayList<String> stepCode1, stepCode2;
	String[] stepName = { "Code Gốc", "format code và bỏ comment", "thay thế define", "chuẩn hóa biến", "loại bỏ code thừa",
			"bỏ các kí tự đặc biệt" };
	private JLabel lblNewLabel;
	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { // Detail window = new Detail(); //
	 * window.frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); }
	 * } }); }
	 */

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public DetailTwoFile(ArrayList<String> stepCode1, ArrayList<String> stepCode2) {
		this.stepCode1 = stepCode1;
		this.stepCode2 = stepCode2;
		this.numberStep = 0;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void doShow() {
		JPanel pnBottom = new JPanel();
		frame.getContentPane().add(pnBottom, BorderLayout.SOUTH);
		pnBottom.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		pnBottom.add(panel, BorderLayout.WEST);

		btnBack = new JButton("Back");
		btnBack.setEnabled(false);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberStep--;
				checkEnable();
				performStep(numberStep);
			}
		});
		panel.add(btnBack);

		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberStep++;
				checkEnable();
				performStep(numberStep);

//				System.out.println(stepCode1.size() + " - " + stepCode2.size());
			}
		});
		panel.add(btnNext);

		JPanel panel_1 = new JPanel();
		pnBottom.add(panel_1, BorderLayout.EAST);

		btnReturn = new JButton("Return");
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		panel_1.add(btnReturn);

		JPanel panel_2 = new JPanel();
		pnBottom.add(panel_2, BorderLayout.CENTER);

		lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(SystemColor.textHighlightText);
		lblNewLabel.setForeground(Color.RED);
		lblNewLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
		panel_2.add(lblNewLabel);

		JPanel pnCenter = new JPanel();
		frame.getContentPane().add(pnCenter, BorderLayout.CENTER);
		pnCenter.setLayout(new GridLayout(1, 0, 10, 0));

		JScrollPane scrollPaneLeft = new JScrollPane();
		pnCenter.add(scrollPaneLeft);

		taLeft = new JTextArea();
		taLeft.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPaneLeft.setViewportView(taLeft);

		JScrollPane scrollPaneRight = new JScrollPane();
		pnCenter.add(scrollPaneRight);

		taRight = new JTextArea();
		taRight.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPaneRight.setViewportView(taRight);
	}

	protected void performStep(int numberStep2) {
		taLeft.setText(stepCode1.get(numberStep2).toString());
		taRight.setText(stepCode2.get(numberStep2).toString());
		lblNewLabel.setText(stepName[numberStep2]);
	}

	protected void checkEnable() {
		btnNext.setEnabled(true);
		btnBack.setEnabled(true);

		if (numberStep == stepName.length - 1)
			btnNext.setEnabled(false);
		if (numberStep == 0)
			btnBack.setEnabled(false);

	}

	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		doShow();
		performStep(numberStep);

		frame.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50),
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 150));
		frame.setMaximizedBounds(new Rectangle(0, 0, 500, 500));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

}
