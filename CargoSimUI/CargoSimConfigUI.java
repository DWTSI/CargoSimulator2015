import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


public class CargoSimConfigUI extends JFrame {
	
	private CargoSimUI cargoSimUI;

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
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setTitle("CargoSimulator2015");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 405, 576);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnLaunchWithDefault = new JButton("Launch with Default Configuration");
		btnLaunchWithDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					generate_config_file_default();
					startSimulation();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLaunchWithDefault.setBounds(6, 503, 261, 29);
		contentPane.add(btnLaunchWithDefault);
		
		JButton btnLaunchWithCustom = new JButton("Launch with Custom Configuration");
		btnLaunchWithCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					generate_config_file();
					startSimulation();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLaunchWithCustom.setBounds(6, 472, 261, 29);
		contentPane.add(btnLaunchWithCustom);
		
		txtfldTimeLandFreq = new JTextField();
		txtfldTimeLandFreq.setText("11");
		txtfldTimeLandFreq.setBounds(132, 66, 96, 28);
		contentPane.add(txtfldTimeLandFreq);
		txtfldTimeLandFreq.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Arrival Frequency:");
		lblNewLabel.setBounds(6, 72, 119, 16);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("\u00B1");
		label.setBounds(240, 72, 25, 16);
		contentPane.add(label);
		
		txtfldTimeLandFreqVar = new JTextField();
		txtfldTimeLandFreqVar.setText("7");
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
		txtfldFreqPlane1.setText("0.25");
		txtfldFreqPlane1.setBounds(71, 122, 45, 28);
		contentPane.add(txtfldFreqPlane1);
		txtfldFreqPlane1.setColumns(10);
		
		JLabel lblType = new JLabel("Type 1:");
		lblType.setBounds(16, 128, 61, 16);
		contentPane.add(lblType);
		
		txtfldFreqPlane2 = new JTextField();
		txtfldFreqPlane2.setText("0.25");
		txtfldFreqPlane2.setColumns(10);
		txtfldFreqPlane2.setBounds(183, 122, 45, 28);
		contentPane.add(txtfldFreqPlane2);
		
		txtfldFreqPlane3 = new JTextField();
		txtfldFreqPlane3.setText("0.5");
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
		txtfldTimeLoad1.setText("18");
		txtfldTimeLoad1.setColumns(10);
		txtfldTimeLoad1.setBounds(71, 193, 96, 28);
		contentPane.add(txtfldTimeLoad1);
		
		JLabel label_2 = new JLabel("\u00B1");
		label_2.setBounds(167, 199, 25, 16);
		contentPane.add(label_2);
		
		txtfldTimeLoad1Var = new JTextField();
		txtfldTimeLoad1Var.setText("2");
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
		txtfldTimeLoad2.setText("24");
		txtfldTimeLoad2.setColumns(10);
		txtfldTimeLoad2.setBounds(71, 221, 96, 28);
		contentPane.add(txtfldTimeLoad2);
		
		JLabel label_5 = new JLabel("\u00B1");
		label_5.setBounds(167, 227, 25, 16);
		contentPane.add(label_5);
		
		txtfldTimeLoad2Var = new JTextField();
		txtfldTimeLoad2Var.setText("4");
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
		txtfldTimeLoad3.setText("36");
		txtfldTimeLoad3.setColumns(10);
		txtfldTimeLoad3.setBounds(71, 249, 96, 28);
		contentPane.add(txtfldTimeLoad3);
		
		JLabel label_8 = new JLabel("\u00B1");
		label_8.setBounds(167, 255, 25, 16);
		contentPane.add(label_8);
		
		txtfldTimeLoad3Var = new JTextField();
		txtfldTimeLoad3Var.setText("4");
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
		txtfldTimeBerthDeberth.setText("1");
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
		txtfldTimeTaxiTravel.setText("0.25");
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
		txtfldNumBerths.setText("3");
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
		txtfldTimeStormDur.setText("4");
		txtfldTimeStormDur.setColumns(10);
		txtfldTimeStormDur.setBounds(120, 409, 96, 28);
		contentPane.add(txtfldTimeStormDur);
		
		JLabel label_12 = new JLabel("\u00B1");
		label_12.setBounds(215, 415, 25, 16);
		contentPane.add(label_12);
		
		txtfldTimeStormVar = new JTextField();
		txtfldTimeStormVar.setText("2");
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
		txtfldTimeBetweenStorms.setText("48");
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
	
	public void startSimulation() {
		try {
            copyFileOutOfJar("CargoSimulator2015.exe");
            System.out.println(runFile("CargoSimulator2015.exe"));
            //Thread.sleep(5000);
            deleteFile("CargoSimulator2015.exe");
            CargoSimUI.main(null);
            this.setVisible(false);
        } catch (Exception ex) {
            Logger.getLogger(CargoSimConfigUI.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void generate_config_file_default() throws FileNotFoundException {
		String output = "TIME_LAND_FREQ      = 11\nTIME_LAND_FREQ_VAR  =  7\nTIME_STORM_DUR      =  4\nTIME_STORM_VAR      =  2\nTIME_BETWEEN_STORMS = 48\n\nFREQ_PLANE1 = 0.25\nFREQ_PLANE2 = 0.25\nFREQ_PLANE3 = 0.50\n\nTIME_LOAD1     = 18\nTIME_LOAD1_VAR =  2\nTIME_LOAD2     = 24\nTIME_LOAD2_VAR =  4\nTIME_LOAD3     = 36\nTIME_LOAD3_VAR =  4\n\nTIME_TAXI_TRAVEL   = 0.25\nTIME_BERTH_DEBERTH = 1\n\nNUM_BERTHS = 3";
		PrintWriter out = new PrintWriter("config.ini");
		out.println(output);
		out.close();
	}
	
	public void generate_config_file() throws FileNotFoundException {
		double[] input = new double[18]; 
		
		input[1] = Double.parseDouble(this.txtfldTimeLandFreq.getText());
		input[2] = Double.parseDouble(this.txtfldTimeLandFreqVar.getText());
		input[3] = Double.parseDouble(this.txtfldTimeStormDur.getText());
		input[4] = Double.parseDouble(this.txtfldTimeStormVar.getText());
		input[5] = Double.parseDouble(this.txtfldTimeBetweenStorms.getText());
		input[6] = Double.parseDouble(this.txtfldFreqPlane1.getText());
		input[7] = Double.parseDouble(this.txtfldFreqPlane2.getText());
		input[8] = Double.parseDouble(this.txtfldFreqPlane3.getText());
		input[9] = Double.parseDouble(this.txtfldTimeLoad1.getText());
		input[10] = Double.parseDouble(this.txtfldTimeLoad1Var.getText());
		input[11] = Double.parseDouble(this.txtfldTimeLoad2.getText());
		input[12] = Double.parseDouble(this.txtfldTimeLoad2Var.getText());
		input[13] = Double.parseDouble(this.txtfldTimeLoad3.getText());
		input[14] = Double.parseDouble(this.txtfldTimeLoad3Var.getText());
		input[15] = Double.parseDouble(this.txtfldTimeTaxiTravel.getText());
		input[16] = Double.parseDouble(this.txtfldTimeBerthDeberth.getText());
		input[17] = Double.parseDouble(this.txtfldNumBerths.getText());
		
		String output = "TIME_LAND_FREQ      = " + input[1] + "\r\n"
						+"TIME_LAND_FREQ_VAR  =  " + input[2] + "\r\n" 
						+"TIME_STORM_DUR      =  " + input[3] + "\r\n"
						+"TIME_STORM_VAR      =  " + input[4] + "\r\n"
						+"TIME_BETWEEN_STORMS = "  + input[5] + "\r\n"
						
						+"FREQ_PLANE1 = " + input[6] + "\r\n"
						+"FREQ_PLANE2 = " + input[7] + "\r\n"
						+"FREQ_PLANE3 = "  + input[8] + "\r\n"
						
						+"TIME_LOAD1     = " + input[9] + "\r\n"
						+"TIME_LOAD1_VAR =  " + input[10] + "\r\n"
						+"TIME_LOAD2     = " + input[11] + "\r\n"
						+"TIME_LOAD2_VAR =  " + input[12] + "\r\n"
						+"TIME_LOAD3     = " + input[13] + "\r\n"
						+"TIME_LOAD3_VAR =  " + input[14] + "\r\n"
						
						+"TIME_TAXI_TRAVEL   = " + input[15] + "\r\n"
						+"TIME_BERTH_DEBERTH = " + input[16] + "\r\n"
						
						+"NUM_BERTHS = " + input[17] + "\r\n";
		
		//System.out.println(output);
		
		PrintWriter	out = new PrintWriter("config.ini");
		out.println(output);
		out.close();
		
	}
	
	private static int runFile(String filename) throws IOException, InterruptedException {
        return Runtime.getRuntime().exec(filename).waitFor();
    }

    private static void deleteFile(String filename) throws NoSuchFileException, IOException {
        Files.deleteIfExists(Paths.get(filename));
    }

    private static void copyFileOutOfJar(String filename) throws FileNotFoundException, IOException {  
        InputStream stream = CargoSimConfigUI.class.getResourceAsStream("/cargosimulator2015/" + filename);

        if (stream == null) {
            // exception for empty stream
        }

        OutputStream resStreamOut;
        int readBytes;
        byte[] buffer = new byte[4096];

        resStreamOut = new FileOutputStream(new File(filename));
        while ((readBytes = stream.read(buffer)) > 0) {
            resStreamOut.write(buffer, 0, readBytes);
        }

        stream.close();
        resStreamOut.close();
    }
}
