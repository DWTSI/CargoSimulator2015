import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;


public class CargoSimConfigUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtfldTimeLandFreq;
	private JTextField txtfldTimeLandFreqVar;
	private JTextField txtfldFreqPlane1;
	private JTextField txtfldFreqPlane2;
	private JTextField txtfldFreqPlane3;
	private JTextField txtfldTimeLoad1;
	private JTextField txtfldTimeLoad1Var;
	private JTextField txtfldTimeLoad2;
	private JTextField txtfldTimeLoad2Var;
	private JTextField txtfldTimeLoad3;
	private JTextField txtfldTimeLoad3Var;
	private JTextField txtfldTimeBerthDeberth;
	private JTextField txtfldTimeTaxiTravel;
	private JTextField txtfldNumBerths;
	private JTextField txtfldTimeStormDur;
	private JTextField txtfldTimeStormVar;
	private JTextField txtfldTimeBetweenStorms;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CargoSimConfigUI frame = new CargoSimConfigUI();
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
	public CargoSimConfigUI() {
		setTitle("CargoSimulator2015");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 405, 560);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnLaunchWithDefault = new JButton("Launch with Default Configuration");
		btnLaunchWithDefault.setBounds(6, 503, 261, 29);
		contentPane.add(btnLaunchWithDefault);
		
		JButton btnLaunchWithCustom = new JButton("Launch with Custom Configuration");
		btnLaunchWithCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnLaunchWithCustom.setBounds(6, 472, 261, 29);
		contentPane.add(btnLaunchWithCustom);
		
		txtfldTimeLandFreq = new JTextField();
		txtfldTimeLandFreq.setBounds(132, 66, 96, 28);
		contentPane.add(txtfldTimeLandFreq);
		txtfldTimeLandFreq.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Arrival Frequency:");
		lblNewLabel.setBounds(6, 72, 119, 16);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("±");
		label.setBounds(240, 72, 25, 16);
		contentPane.add(label);
		
		txtfldTimeLandFreqVar = new JTextField();
		txtfldTimeLandFreqVar.setBounds(262, 66, 45, 28);
		contentPane.add(txtfldTimeLandFreqVar);
		txtfldTimeLandFreqVar.setColumns(10);
		
		JLabel lblHours = new JLabel("hours");
		lblHours.setBounds(310, 72, 61, 16);
		contentPane.add(lblHours);
		
		JLabel lblPlaneTypeDistribution = new JLabel("Plane type distribution:");
		lblPlaneTypeDistribution.setBounds(6, 100, 152, 16);
		contentPane.add(lblPlaneTypeDistribution);
		
		txtfldFreqPlane1 = new JTextField();
		txtfldFreqPlane1.setBounds(71, 122, 45, 28);
		contentPane.add(txtfldFreqPlane1);
		txtfldFreqPlane1.setColumns(10);
		
		JLabel lblType = new JLabel("Type 1:");
		lblType.setBounds(16, 128, 61, 16);
		contentPane.add(lblType);
		
		txtfldFreqPlane2 = new JTextField();
		txtfldFreqPlane2.setColumns(10);
		txtfldFreqPlane2.setBounds(183, 122, 45, 28);
		contentPane.add(txtfldFreqPlane2);
		
		txtfldFreqPlane3 = new JTextField();
		txtfldFreqPlane3.setColumns(10);
		txtfldFreqPlane3.setBounds(297, 122, 45, 28);
		contentPane.add(txtfldFreqPlane3);
		
		JLabel lblType_1 = new JLabel("Type 2:");
		lblType_1.setBounds(132, 128, 46, 16);
		contentPane.add(lblType_1);
		
		JLabel lblType_2 = new JLabel("Type 3:");
		lblType_2.setBounds(246, 128, 61, 16);
		contentPane.add(lblType_2);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 153, 390, 12);
		contentPane.add(separator);
		
		JLabel lblLoadingTimes = new JLabel("Loading times:");
		lblLoadingTimes.setBounds(6, 171, 110, 16);
		contentPane.add(lblLoadingTimes);
		
		JLabel label_1 = new JLabel("Type 1:");
		label_1.setBounds(16, 199, 61, 16);
		contentPane.add(label_1);
		
		txtfldTimeLoad1 = new JTextField();
		txtfldTimeLoad1.setColumns(10);
		txtfldTimeLoad1.setBounds(71, 193, 96, 28);
		contentPane.add(txtfldTimeLoad1);
		
		JLabel label_2 = new JLabel("±");
		label_2.setBounds(167, 199, 25, 16);
		contentPane.add(label_2);
		
		txtfldTimeLoad1Var = new JTextField();
		txtfldTimeLoad1Var.setColumns(10);
		txtfldTimeLoad1Var.setBounds(183, 193, 45, 28);
		contentPane.add(txtfldTimeLoad1Var);
		
		JLabel label_3 = new JLabel("hours");
		label_3.setBounds(240, 199, 61, 16);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("Type 2:");
		label_4.setBounds(16, 227, 46, 16);
		contentPane.add(label_4);
		
		txtfldTimeLoad2 = new JTextField();
		txtfldTimeLoad2.setColumns(10);
		txtfldTimeLoad2.setBounds(71, 221, 96, 28);
		contentPane.add(txtfldTimeLoad2);
		
		JLabel label_5 = new JLabel("±");
		label_5.setBounds(167, 227, 25, 16);
		contentPane.add(label_5);
		
		txtfldTimeLoad2Var = new JTextField();
		txtfldTimeLoad2Var.setColumns(10);
		txtfldTimeLoad2Var.setBounds(183, 221, 45, 28);
		contentPane.add(txtfldTimeLoad2Var);
		
		JLabel label_6 = new JLabel("hours");
		label_6.setBounds(240, 227, 61, 16);
		contentPane.add(label_6);
		
		JLabel label_7 = new JLabel("Type 3:");
		label_7.setBounds(16, 255, 61, 16);
		contentPane.add(label_7);
		
		txtfldTimeLoad3 = new JTextField();
		txtfldTimeLoad3.setColumns(10);
		txtfldTimeLoad3.setBounds(71, 249, 96, 28);
		contentPane.add(txtfldTimeLoad3);
		
		JLabel label_8 = new JLabel("±");
		label_8.setBounds(167, 255, 25, 16);
		contentPane.add(label_8);
		
		txtfldTimeLoad3Var = new JTextField();
		txtfldTimeLoad3Var.setColumns(10);
		txtfldTimeLoad3Var.setBounds(183, 249, 45, 28);
		contentPane.add(txtfldTimeLoad3Var);
		
		JLabel label_9 = new JLabel("hours");
		label_9.setBounds(240, 255, 61, 16);
		contentPane.add(label_9);
		
		JLabel lblBerthingDeberthing = new JLabel("Berthing / deberthing time:");
		lblBerthingDeberthing.setBounds(6, 307, 171, 16);
		contentPane.add(lblBerthingDeberthing);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 283, 390, 12);
		contentPane.add(separator_1);
		
		txtfldTimeBerthDeberth = new JTextField();
		txtfldTimeBerthDeberth.setColumns(10);
		txtfldTimeBerthDeberth.setBounds(183, 301, 45, 28);
		contentPane.add(txtfldTimeBerthDeberth);
		
		JLabel label_10 = new JLabel("hours");
		label_10.setBounds(240, 307, 61, 16);
		contentPane.add(label_10);
		
		JLabel lblTaxiTravelTime = new JLabel("Taxi travel time:");
		lblTaxiTravelTime.setBounds(71, 335, 112, 16);
		contentPane.add(lblTaxiTravelTime);
		
		txtfldTimeTaxiTravel = new JTextField();
		txtfldTimeTaxiTravel.setColumns(10);
		txtfldTimeTaxiTravel.setBounds(183, 329, 45, 28);
		contentPane.add(txtfldTimeTaxiTravel);
		
		JLabel label_11 = new JLabel("hours");
		label_11.setBounds(240, 335, 61, 16);
		contentPane.add(label_11);
		
		JLabel lblNumberOfBerths = new JLabel("Number of berths:");
		lblNumberOfBerths.setBounds(59, 363, 119, 16);
		contentPane.add(lblNumberOfBerths);
		
		txtfldNumBerths = new JTextField();
		txtfldNumBerths.setColumns(10);
		txtfldNumBerths.setBounds(183, 357, 45, 28);
		contentPane.add(txtfldNumBerths);
		
		JLabel lblBerths = new JLabel("berths");
		lblBerths.setBounds(240, 363, 61, 16);
		contentPane.add(lblBerths);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(6, 391, 390, 12);
		contentPane.add(separator_2);
		
		JLabel lblStormDuration = new JLabel("Storm duration: ");
		lblStormDuration.setBounds(6, 415, 102, 16);
		contentPane.add(lblStormDuration);
		
		txtfldTimeStormDur = new JTextField();
		txtfldTimeStormDur.setColumns(10);
		txtfldTimeStormDur.setBounds(120, 409, 96, 28);
		contentPane.add(txtfldTimeStormDur);
		
		JLabel label_12 = new JLabel("±");
		label_12.setBounds(215, 415, 25, 16);
		contentPane.add(label_12);
		
		txtfldTimeStormVar = new JTextField();
		txtfldTimeStormVar.setColumns(10);
		txtfldTimeStormVar.setBounds(228, 409, 45, 28);
		contentPane.add(txtfldTimeStormVar);
		
		JLabel label_13 = new JLabel("hours");
		label_13.setBounds(285, 415, 61, 16);
		contentPane.add(label_13);
		
		JLabel lblAverageTimeBetween = new JLabel("Average time between storms:");
		lblAverageTimeBetween.setBounds(6, 443, 210, 16);
		contentPane.add(lblAverageTimeBetween);
		
		txtfldTimeBetweenStorms = new JTextField();
		txtfldTimeBetweenStorms.setColumns(10);
		txtfldTimeBetweenStorms.setBounds(228, 438, 45, 28);
		contentPane.add(txtfldTimeBetweenStorms);
		
		JLabel label_14 = new JLabel("hours");
		label_14.setBounds(285, 443, 61, 16);
		contentPane.add(label_14);
		
		JLabel lblNewLabel_1 = new JLabel("Simulation Settings");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(16, 12, 326, 48);
		contentPane.add(lblNewLabel_1);
	}
}
