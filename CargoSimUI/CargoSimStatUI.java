import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JScrollPane;

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
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setTitle("Simulation Analysis");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 535);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 683, 497);
		contentPane.add(scrollPane);
		
		txtrStats = new JTextArea();
		scrollPane.setViewportView(txtrStats);
		txtrStats.setFont(new Font("Courier", Font.PLAIN, 14));
		txtrStats.setText("stats");
		
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
