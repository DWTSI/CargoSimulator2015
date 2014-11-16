import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class CargoSimUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtfldAnimationRate;
	private JTextField txtfldCurrentHour;

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
		panelTimeControl.setLayout(null);
		
		txtfldAnimationRate = new JTextField();
		txtfldAnimationRate.setBounds(135, 32, 134, 28);
		panelTimeControl.add(txtfldAnimationRate);
		txtfldAnimationRate.setColumns(10);
		

		txtfldCurrentHour = new JTextField();
		txtfldCurrentHour.setText("0");
		txtfldCurrentHour.setBounds(281, 32, 134, 28);
		panelTimeControl.add(txtfldCurrentHour);
		txtfldCurrentHour.setColumns(10);
		
		
		final JSlider slider = new JSlider();
		slider.setValue(0);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				txtfldCurrentHour.setText(Integer.toString(slider.getValue() / 60));
			}
		});
		slider.setMaximum(525599);
		slider.setBounds(135, 62, 300, 29);
		panelTimeControl.add(slider);
		
		JButton btnPlay = new JButton("PLAY");
		btnPlay.setBounds(6, 62, 117, 29);
		panelTimeControl.add(btnPlay);
		
		JButton btnPause = new JButton("PAUSE");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPause.setBounds(6, 33, 117, 29);
		panelTimeControl.add(btnPause);
		
		JLabel lblAnimationRate = new JLabel("Animation Rate");
		lblAnimationRate.setBounds(135, 18, 134, 16);
		panelTimeControl.add(lblAnimationRate);
		
		JLabel lblCurrentHour = new JLabel("Current Hour:");
		lblCurrentHour.setBounds(281, 18, 98, 16);
		panelTimeControl.add(lblCurrentHour);
		
		JPanel panelQueueStatus = new JPanel();
		panelQueueStatus.setBounds(6, 115, 441, 240);
		contentPane.add(panelQueueStatus);
		panelQueueStatus.setLayout(null);
		
		JTextPane txtpnDeberthingQueue1 = new JTextPane();
		txtpnDeberthingQueue1.setText("DEBERTHING QUEUE 1");
		txtpnDeberthingQueue1.setBounds(6, 30, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue1);
		
		JTextPane txtpnDeberthingQueue2 = new JTextPane();
		txtpnDeberthingQueue2.setText("DEBERTHING QUEUE 2");
		txtpnDeberthingQueue2.setBounds(6, 94, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue2);
		
		JTextPane txtpnDeberthingQueue3 = new JTextPane();
		txtpnDeberthingQueue3.setText("DEBERTHING QUEUE 3");
		txtpnDeberthingQueue3.setBounds(6, 158, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue3);
		
		JTextPane txtpnRunwayQueue1 = new JTextPane();
		txtpnRunwayQueue1.setText("RUNWAY QUEUE 1");
		txtpnRunwayQueue1.setBounds(222, 30, 204, 60);
		panelQueueStatus.add(txtpnRunwayQueue1);
		
		JTextPane txtpnRunwayQueue2 = new JTextPane();
		txtpnRunwayQueue2.setText("RUNWAY QUEUE 2");
		txtpnRunwayQueue2.setBounds(222, 94, 204, 60);
		panelQueueStatus.add(txtpnRunwayQueue2);
		
		JTextPane txtpnRunwayQueue3 = new JTextPane();
		txtpnRunwayQueue3.setText("RUNWAY QUEUE 3");
		txtpnRunwayQueue3.setBounds(222, 158, 204, 60);
		panelQueueStatus.add(txtpnRunwayQueue3);
		
		JPanel panelTaxiStatus = new JPanel();
		panelTaxiStatus.setBounds(6, 367, 441, 73);
		contentPane.add(panelTaxiStatus);
		panelTaxiStatus.setLayout(null);
		
		JTextPane txtpnTaxiStatus = new JTextPane();
		txtpnTaxiStatus.setBounds(6, 6, 125, 62);
		txtpnTaxiStatus.setText("TAXI STATUS");
		panelTaxiStatus.add(txtpnTaxiStatus);
		
		JTextArea txtrTimeRemainingOn = new JTextArea();
		txtrTimeRemainingOn.setText("TIME REMAINING ON TASK");
		txtrTimeRemainingOn.setBounds(137, 6, 298, 61);
		panelTaxiStatus.add(txtrTimeRemainingOn);
		
		JPanel panelAnimation = new JPanel();
		panelAnimation.setBounds(6, 452, 441, 94);
		contentPane.add(panelAnimation);
		
		JTextArea txtrFancyAnimationGoes = new JTextArea();
		txtrFancyAnimationGoes.setText("FANCY ANIMATION GOES HERE");
		panelAnimation.add(txtrFancyAnimationGoes);
	}
}
