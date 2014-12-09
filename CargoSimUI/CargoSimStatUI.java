import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CargoSimStatUI extends JFrame {

	private JPanel contentPane;
	private String stats;
	final static String STATISTICS_LOG_NAME = "statistics.log";
	JTextArea txtrStats;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CargoSimStatUI frame = new CargoSimStatUI();
					frame.setVisible(true);
					frame.loadStatFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CargoSimStatUI() {
		setTitle("Simulation Analysis");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 535);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtrStats = new JTextArea();
		txtrStats.setFont(new Font("Courier", Font.PLAIN, 14));
		txtrStats.setText("stats");
		txtrStats.setBounds(6, 6, 687, 501);
		contentPane.add(txtrStats);
		
		stats = "";
	}
	
	public void loadStatFile() {
		Path statlog = Paths.get("");
		statlog = statlog.resolve(STATISTICS_LOG_NAME);
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(statlog.toAbsolutePath().toString()));
			
			txtrStats.read(br, null);
			
			br.close();
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
