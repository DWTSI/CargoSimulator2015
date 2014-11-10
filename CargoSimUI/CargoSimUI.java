import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;


public class CargoSimUI extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CargoSimUI frame = new CargoSimUI();
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
	public CargoSimUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 453, 574);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelTimeControl = new JPanel();
		panelTimeControl.setBounds(6, 6, 441, 97);
		contentPane.add(panelTimeControl);
		
		JPanel panelQueueStatus = new JPanel();
		panelQueueStatus.setBounds(6, 115, 441, 240);
		contentPane.add(panelQueueStatus);
		
		JPanel panelTaxiStatus = new JPanel();
		panelTaxiStatus.setBounds(6, 367, 441, 73);
		contentPane.add(panelTaxiStatus);
		
		JPanel panelAnimation = new JPanel();
		panelAnimation.setBounds(6, 452, 441, 94);
		contentPane.add(panelAnimation);
	}
}
