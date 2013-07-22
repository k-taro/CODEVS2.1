package codevs.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MyFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private JTextArea textArea;
	private JButton btnForcedTermination;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void append(String str){
				super.append(str);
				this.setCaretPosition(this.getDocument().getLength());
			}
		};
		textArea.setFont(new Font("Courier", Font.PLAIN, 13));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		btnForcedTermination = new JButton("forced termination");
		btnForcedTermination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.print("0 4\n");
				System.out.flush();
			}
		});
		contentPane.add(btnForcedTermination, BorderLayout.SOUTH);
		//sprint("0\n");
	}
	
	public void print(String s){
		textArea.append(s);
	}

}
