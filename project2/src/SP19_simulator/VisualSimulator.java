package SP19_simulator;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import javax.swing.JEditorPane;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;


import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;

public class VisualSimulator extends JFrame {
	ResourceManager resourceManager = new ResourceManager();
	SicLoader sicLoader = new SicLoader(resourceManager);
	SicSimulator sicSimulator = new SicSimulator(resourceManager);
	
	
	private JPanel contentPane;
	private JTextField fileName;
	private JTextField programName;
	private JTextField startAdress;
	private JTextField lengthOfProgram;
	private JTextField adressOfFirstInstruction;
	private JTextField target;
	private JTextField decA;
	private JTextField hexA;
	private JTextField decX;
	private JTextField hexX;
	private JTextField decL;
	private JTextField hexL;
	private JTextField decB;
	private JTextField hexB;
	private JTextField decS;
	private JTextField hexS;
	private JTextField device;
	private JTextField decT;
	private JTextField hexT;
	private JTextField regF;
	private JTextField decPC;
	private JTextField hexPC;
	private JTextField regSW;
	private JList<?> list;
	private JTextArea textArea;
	/**
	 * 프로그램 로드 명령을 전달한다.
	 */
	public static void main(String[] args) {
		VisualSimulator frame = new VisualSimulator();
		frame.setTitle("SIC/XE");
		frame.setVisible(true);
	}
	public void load(File string){
		//...
		sicLoader.load(string);
	};

	/**
	 * 하나의 명령어만 수행할 것을 SicSimulator에 요청한다.
	 */
	public void oneStep(){
		textArea.append(sicSimulator.oneStep(sicLoader) + "\n");
		update();
	};

	/**
	 * 남아있는 모든 명령어를 수행할 것을 SicSimulator에 요청한다.
	 */
	public void allStep(){
		int before=0;
		while (sicSimulator.instCnt <= sicLoader.inst.length()) {
			before = sicSimulator.instCnt;
			oneStep();
			if (before == sicSimulator.instCnt)
				break;
			update();
		}
	};
	
	/**
	 * 화면을 최신값으로 갱신하는 역할을 수행한다.
	 */
	public void update(){
		programName.setText(sicLoader.programName);
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < sicLoader.inst.length(); i++) {
			model.addElement(sicLoader.inst.getToken(i).name);
		}
		list.setModel(model);
		lengthOfProgram.setText(String.format("%06X",sicLoader.programLength));
		startAdress.setText(sicLoader.programStart);
		decA.setText(String.format("%06d", sicLoader.rMgr.getRegister(0)));
		hexA.setText(String.format("%06X", sicLoader.rMgr.getRegister(0)));
		decX.setText(String.format("%06d", sicLoader.rMgr.getRegister(1)));
		hexX.setText(String.format("%06X", sicLoader.rMgr.getRegister(1)));
		decL.setText(String.format("%06d", sicLoader.rMgr.getRegister(2)));
		hexL.setText(String.format("%06X", sicLoader.rMgr.getRegister(2)));
		decB.setText(String.format("%06d", sicLoader.rMgr.getRegister(3)));
		hexB.setText(String.format("%06X", sicLoader.rMgr.getRegister(3)));
		decS.setText(String.format("%06d", sicLoader.rMgr.getRegister(4)));
		hexS.setText(String.format("%06X", sicLoader.rMgr.getRegister(4)));
		decT.setText(String.format("%06d", sicLoader.rMgr.getRegister(5)));
		hexT.setText(String.format("%06X", sicLoader.rMgr.getRegister(5)));
		regF.setText(String.format("%06X", sicLoader.rMgr.getRegister(6)));
		hexPC.setText(String.format("%06X", sicLoader.rMgr.getRegister(7)));
		decPC.setText(String.format("%06d", sicLoader.rMgr.getRegister(7)));
		regSW.setText(String.format("%06X", sicLoader.rMgr.getRegister(8)));
		adressOfFirstInstruction.setText(String.format("%06X",sicLoader.inst.getToken(0).addr));
		if (sicSimulator.instCnt < sicLoader.inst.length())
			target.setText(String.format("%06X",sicLoader.inst.getToken(sicSimulator.instCnt).addr));
		if (resourceManager.curdev != null)
			device.setText(String.format("%02X", resourceManager.curdev));
	};
	/**
	 * Create the frame.
	 */
	public VisualSimulator() {
		setTitle("SIC/XE Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 556, 732);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel File = new JLabel("File Name :");
		File.setBounds(12, 13, 76, 15);
		contentPane.add(File);
		
		fileName = new JTextField();
		fileName.setBounds(85, 10, 116, 21);
		contentPane.add(fileName);
		fileName.setColumns(10);
		
		JButton btnOpen = new JButton("open");
		btnOpen.setBounds(213, 10, 76, 21);
		FileDialog dialog = new FileDialog(this, "파일 열기", FileDialog.LOAD);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        dialog.setVisible(true);
		        File file = new File(dialog.getDirectory()+dialog.getFile());
		        fileName.setText(dialog.getFile());
		        load(file);
		        update();
		        
			}
		});
		contentPane.add(btnOpen);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(12, 38, 260, 146);
		layeredPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
		contentPane.add(layeredPane);
		
		JLabel lblHheaderRecord = new JLabel("H (Header Record)");
		lblHheaderRecord.setForeground(Color.BLACK);
		lblHheaderRecord.setBounds(12, 0, 112, 20);
		layeredPane.add(lblHheaderRecord);
		
		JLabel lblProgramName = new JLabel("Program name :");
		lblProgramName.setBounds(12, 22, 104, 20);
		layeredPane.add(lblProgramName);
		
		programName = new JTextField();
		programName.setEditable(false);
		programName.setBounds(128, 22, 116, 21);
		layeredPane.add(programName);
		programName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Start Adress of \r\nObject Program :");
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setBounds(12, 52, 191, 20);
		layeredPane.add(lblNewLabel_1);
		
		startAdress = new JTextField();
		startAdress.setEditable(false);
		startAdress.setBounds(128, 69, 116, 21);
		layeredPane.add(startAdress);
		startAdress.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Length of Program : ");
		lblNewLabel_2.setBounds(12, 100, 116, 15);
		layeredPane.add(lblNewLabel_2);
		
		lengthOfProgram = new JTextField();
		lengthOfProgram.setEditable(false);
		lengthOfProgram.setBounds(128, 102, 116, 21);
		layeredPane.add(lengthOfProgram);
		lengthOfProgram.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(284, 38, 232, 97);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblEendRecord = new JLabel("E (End Record)");
		lblEendRecord.setBounds(12, 0, 88, 23);
		panel.add(lblEendRecord);
		
		JLabel lblNewLabel_3 = new JLabel("<html>Address of First Instruction<br> in Object Program :\r\n");
		lblNewLabel_3.setBounds(12, 20, 208, 36);
		panel.add(lblNewLabel_3);
		
		adressOfFirstInstruction = new JTextField();
		adressOfFirstInstruction.setEditable(false);
		adressOfFirstInstruction.setBounds(104, 66, 116, 21);
		panel.add(adressOfFirstInstruction);
		adressOfFirstInstruction.setColumns(10);
		
		
		
		JLabel lblTargetAddress = new JLabel("Target Address :");
		lblTargetAddress.setBounds(284, 198, 97, 15);
		contentPane.add(lblTargetAddress);
		
		target = new JTextField();
		target.setEditable(false);
		target.setBounds(400, 195, 116, 21);
		contentPane.add(target);
		target.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(12, 198, 260, 278);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_5 = new JLabel("Register");
		lblNewLabel_5.setBounds(12, 0, 57, 24);
		panel_1.add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("A (#0)");
		lblNewLabel_6.setBounds(12, 41, 57, 15);
		panel_1.add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("X (#1)");
		lblNewLabel_7.setBounds(12, 66, 57, 15);
		panel_1.add(lblNewLabel_7);
		
		JLabel lblNewLabel_8 = new JLabel("L (#2)");
		lblNewLabel_8.setBounds(12, 91, 57, 15);
		panel_1.add(lblNewLabel_8);
		
		JLabel lblNewLabel_9 = new JLabel("B (#3)");
		lblNewLabel_9.setBounds(12, 116, 57, 15);
		panel_1.add(lblNewLabel_9);
		
		JLabel lblNewLabel_10 = new JLabel("S (#4)");
		lblNewLabel_10.setBounds(12, 141, 57, 15);
		panel_1.add(lblNewLabel_10);
		
		JLabel lblNewLabel_11 = new JLabel("Dec");
		lblNewLabel_11.setBounds(60, 21, 57, 15);
		panel_1.add(lblNewLabel_11);
		
		JLabel lblNewLabel_12 = new JLabel("Hex");
		lblNewLabel_12.setBounds(154, 21, 57, 15);
		panel_1.add(lblNewLabel_12);
		
		decA = new JTextField();
		decA.setEditable(false);
		decA.setBounds(60, 38, 79, 21);
		panel_1.add(decA);
		decA.setColumns(10);
		
		hexA = new JTextField();
		hexA.setEditable(false);
		hexA.setColumns(10);
		hexA.setBounds(154, 38, 79, 21);
		panel_1.add(hexA);
		
		decX = new JTextField();
		decX.setEditable(false);
		decX.setColumns(10);
		decX.setBounds(60, 63, 79, 21);
		panel_1.add(decX);
		
		hexX = new JTextField();
		hexX.setEditable(false);
		hexX.setColumns(10);
		hexX.setBounds(154, 63, 79, 21);
		panel_1.add(hexX);
		
		decL = new JTextField();
		decL.setEditable(false);
		decL.setColumns(10);
		decL.setBounds(60, 88, 79, 21);
		panel_1.add(decL);
		
		hexL = new JTextField();
		hexL.setEditable(false);
		hexL.setColumns(10);
		hexL.setBounds(154, 88, 79, 21);
		panel_1.add(hexL);
		
		decB = new JTextField();
		decB.setEditable(false);
		decB.setColumns(10);
		decB.setBounds(60, 113, 79, 21);
		panel_1.add(decB);
		
		hexB = new JTextField();
		hexB.setEditable(false);
		hexB.setColumns(10);
		hexB.setBounds(154, 113, 79, 21);
		panel_1.add(hexB);
		
		decS = new JTextField();
		decS.setEditable(false);
		decS.setColumns(10);
		decS.setBounds(60, 138, 79, 21);
		panel_1.add(decS);
		
		hexS = new JTextField();
		hexS.setEditable(false);
		hexS.setColumns(10);
		hexS.setBounds(154, 138, 79, 21);
		panel_1.add(hexS);
		
		JLabel lblF = new JLabel("F (#6)");
		lblF.setBounds(12, 193, 57, 15);
		panel_1.add(lblF);
		
		JLabel lblT = new JLabel("T (#5)");
		lblT.setBounds(12, 168, 57, 15);
		panel_1.add(lblT);
		
		decT = new JTextField();
		decT.setEditable(false);
		decT.setColumns(10);
		decT.setBounds(60, 165, 79, 21);
		panel_1.add(decT);
		
		hexT = new JTextField();
		hexT.setEditable(false);
		hexT.setColumns(10);
		hexT.setBounds(154, 165, 79, 21);
		panel_1.add(hexT);
		
		regF = new JTextField();
		regF.setEditable(false);
		regF.setColumns(10);
		regF.setBounds(60, 190, 173, 21);
		panel_1.add(regF);
		
		JLabel label_2 = new JLabel("SW (#9)");
		label_2.setBounds(12, 246, 57, 15);
		panel_1.add(label_2);
		
		JLabel label_3 = new JLabel("PC (#8)");
		label_3.setBounds(12, 221, 57, 15);
		panel_1.add(label_3);
		
		decPC = new JTextField();
		decPC.setEditable(false);
		decPC.setColumns(10);
		decPC.setBounds(60, 218, 79, 21);
		panel_1.add(decPC);
		
		hexPC = new JTextField();
		hexPC.setEditable(false);
		hexPC.setColumns(10);
		hexPC.setBounds(154, 218, 79, 21);
		panel_1.add(hexPC);
		
		regSW = new JTextField();
		regSW.setEditable(false);
		regSW.setColumns(10);
		regSW.setBounds(60, 243, 173, 21);
		panel_1.add(regSW);
		
		JLabel lblNewLabel_13 = new JLabel("Instruction :");
		lblNewLabel_13.setBounds(284, 232, 76, 15);
		contentPane.add(lblNewLabel_13);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(284, 258, 134, 218);
		contentPane.add(scrollPane);
		
		list = new JList();
		scrollPane.setViewportView(list);
		
		JLabel lblNewLabel_14 = new JLabel("\uC0AC\uC6A9\uC911\uC778 \uC7A5\uCE58");
		lblNewLabel_14.setBounds(442, 238, 86, 15);
		contentPane.add(lblNewLabel_14);
		
		device = new JTextField();
		device.setEditable(false);
		device.setBounds(440, 263, 88, 21);
		contentPane.add(device);
		device.setColumns(10);
		
		JButton btnNewButton = new JButton("\uC2E4\uD589(1step)");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oneStep();
			}
		});
		btnNewButton.setBounds(430, 377, 98, 23);
		contentPane.add(btnNewButton);
		
		JButton btnall = new JButton("\uC2E4\uD589(All)");
		btnall.setFont(new Font("굴림", Font.PLAIN, 11));
		btnall.setBounds(430, 409, 98, 23);
		btnall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allStep();
			}
		});
		contentPane.add(btnall);
		
		JButton button = new JButton("\uC885\uB8CC");
		button.setFont(new Font("굴림", Font.PLAIN, 11));
		button.setBounds(430, 442, 98, 23);
		contentPane.add(button);
		
		JLabel lblNewLabel_15 = new JLabel("Log (\uBA85\uB839\uC5B4 \uC218\uD589 \uAD00\uB828) :");
		lblNewLabel_15.setBounds(12, 490, 144, 15);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(lblNewLabel_15);
		
		textArea = new JTextArea();
		JScrollPane scrollPane2 = new JScrollPane(textArea);
		scrollPane2.setBounds(12, 515, 516, 156);
		contentPane.add(scrollPane2);
	}
}
