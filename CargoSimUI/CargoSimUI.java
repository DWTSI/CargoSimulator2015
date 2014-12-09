import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.List;

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
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Color;


public class CargoSimUI extends JFrame {
	
	// GUI globals
	private JPanel contentPane;
	private JTextField txtfldAnimationRate;
	private JTextField txtfldCurrentHour;
	private JTextPane txtpnDeberthingQueue1;
	private JTextPane txtpnDeberthingQueue2;
	private JTextPane txtpnDeberthingQueue3;
	private JTextPane txtpnBerthingQueue1;
	private JTextPane txtpnBerthingQueue2;
	private JTextPane txtpnBerthingQueue3;
	private JTextPane txtpnBerthingQueueOverflow;
	private JTextPane txtpnIsStorming;
	private JTextPane txtpnTaxiStatus;
	private JSlider slider;
	
	
	// globals
	private LinkedList<String[]> events;
	String[] currentEvent;
	private LinkedList<Plane> deberthingQueue;
	private LinkedList<Plane> berthingQueue;
	private LinkedList<Plane> transit;
	private boolean isStorming;
	private int taxiStatus;
	private int t; 	// time in minutes
	private SwingWorker currentThread;
	
	  
	// constants
	final String SIMULATION_LOG_NAME = "output_log.csv";
	final int TAXI_IDLE = 0;
	final int TAXI_TRAVELLING_TO_RUNWAY = 1;
	final int TAXI_TRAVELLING_TO_BERTHS = 2;
	final int TAXI_BERTHING_PLANE = 3;
	final int TAXI_DEBERTHING_PLANE = 4;
	final long ANIMATION_THREAD_DELAY = 1; 
	final int MAX_TIME = 525599;
	
	/*########### Nested Classes ###########*/
	
	private class Plane {
	    private int type;
	    private int id;
	    private int berthNumber;
	    private boolean isLoading;
	    
	    public int getType(){ return this.type; }
	    public int getId(){ return this.id; }
	    public void setBerthNumber(String b){ this.berthNumber = Integer.parseInt(b); }
	    public void setLoading(boolean l){ this.isLoading = l; }
	    
	    public Plane(int type, int id){
	    	this.type = type;
	    	this.id = id;
	    	this.berthNumber = 0;
	    	this.isLoading = false;
	    }
	    
	    public Plane(String type, String id){
	    	this.type = Integer.parseInt(type);
	    	this.id = Integer.parseInt(id);
	    }
	    
	    // returns a formatted string to be displayed in UI text panels
	    public String getInfo() {
	    	String r = "";
	    	r += "Plane ID: " + id + "\n";
	    	r += "Plane Type: " + type + "\n";
	    	r += "Plane loading:" + isLoading + "\n";
	    	return r;
	    }
	}
	
	private class AnimationThread extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			for(int i = 0; !isCancelled() && i < MAX_TIME; i++){
				int eventTime = Integer.parseInt(currentEvent[0]);
				t++;
				//System.out.println("Time: " + t);
				if(t == eventTime) {
					handleEvent(currentEvent);
					
					if(events.indexOf(currentEvent) < events.size() - 1)
						currentEvent = events.get(events.indexOf(currentEvent) + 1);
				}
				if(t == eventTime) {
					handleEvent(currentEvent);
					
					if(events.indexOf(currentEvent) < events.size() - 1)
						currentEvent = events.get(events.indexOf(currentEvent) + 1);
				}
				
				draw();
				Thread.sleep(ANIMATION_THREAD_DELAY);
			}
			return null;
		}
		
		protected void process() throws Exception {
			// who cares?
		}
	}
	
	/*########### End Nested Classes ###########*/

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CargoSimUI c = new CargoSimUI();
					c.setVisible(true);
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

		// initialize globals
		deberthingQueue = new LinkedList<Plane>();
		berthingQueue = new LinkedList<Plane>();
		transit = new LinkedList<Plane>();
		isStorming = false;
		
		setTitle("CargoSimulator2015");
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
		
		
		slider = new JSlider();
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
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				(currentThread = new AnimationThread()).execute();
			}
		});
		btnPlay.setBounds(6, 33, 117, 29);
		panelTimeControl.add(btnPlay);
		
		JButton btnPause = new JButton("PAUSE");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentThread.cancel(true);
			}
		});
		btnPause.setBounds(6, 6, 117, 29);
		panelTimeControl.add(btnPause);
		
		JLabel lblAnimationRate = new JLabel("Animation Rate");
		lblAnimationRate.setBounds(135, 18, 134, 16);
		panelTimeControl.add(lblAnimationRate);
		
		JLabel lblCurrentHour = new JLabel("Current Hour:");
		lblCurrentHour.setBounds(281, 18, 98, 16);
		panelTimeControl.add(lblCurrentHour);
		
		JButton btnStop = new JButton("STOP");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Plane p1 = new Plane("1","1");
				Plane p2 = new Plane("2","2");
				Plane p3 = new Plane("3","3");
				p1.setBerthNumber("1");
				p2.setBerthNumber("2");
				p3.setBerthNumber("3");
				deberthingQueue.add(p1);
				deberthingQueue.add(p2);
				//deberthingQueue.add(p3);
				
				try{
					draw();
				} catch(Exception eeee) {
					System.out.println(eeee.getMessage());
				}
			}
		});
		btnStop.setBounds(6, 62, 117, 29);
		panelTimeControl.add(btnStop);
		
		JPanel panelQueueStatus = new JPanel();
		panelQueueStatus.setBounds(6, 115, 441, 247);
		contentPane.add(panelQueueStatus);
		panelQueueStatus.setLayout(null);
		
		txtpnDeberthingQueue1 = new JTextPane();
		txtpnDeberthingQueue1.setBackground(new Color(255, 255, 255));
		txtpnDeberthingQueue1.setForeground(Color.BLACK);
		txtpnDeberthingQueue1.setText("DEBERTHING QUEUE 1");
		txtpnDeberthingQueue1.setBounds(6, 30, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue1);
		
		txtpnDeberthingQueue2 = new JTextPane();
		txtpnDeberthingQueue2.setText("DEBERTHING QUEUE 2");
		txtpnDeberthingQueue2.setBounds(6, 94, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue2);
		
		txtpnDeberthingQueue3 = new JTextPane();
		txtpnDeberthingQueue3.setText("DEBERTHING QUEUE 3");
		txtpnDeberthingQueue3.setBounds(6, 158, 204, 60);
		panelQueueStatus.add(txtpnDeberthingQueue3);
		
		txtpnBerthingQueue1 = new JTextPane();
		txtpnBerthingQueue1.setText("RUNWAY QUEUE 1");
		txtpnBerthingQueue1.setBounds(222, 30, 204, 60);
		panelQueueStatus.add(txtpnBerthingQueue1);
		
		txtpnBerthingQueue2 = new JTextPane();
		txtpnBerthingQueue2.setText("RUNWAY QUEUE 2");
		txtpnBerthingQueue2.setBounds(222, 94, 204, 60);
		panelQueueStatus.add(txtpnBerthingQueue2);
		
		txtpnBerthingQueue3 = new JTextPane();
		txtpnBerthingQueue3.setText("RUNWAY QUEUE 3");
		txtpnBerthingQueue3.setBounds(222, 158, 204, 60);
		panelQueueStatus.add(txtpnBerthingQueue3);
		
		txtpnBerthingQueueOverflow = new JTextPane();
		txtpnBerthingQueueOverflow.setText("0 more in queue");
		txtpnBerthingQueueOverflow.setBounds(222, 225, 204, 16);
		panelQueueStatus.add(txtpnBerthingQueueOverflow);
		
		JLabel lblRunway = new JLabel("Runway :");
		lblRunway.setBounds(222, 2, 61, 16);
		panelQueueStatus.add(lblRunway);
		
		JLabel lblDeberthingQueue = new JLabel("Deberthing Queue :");
		lblDeberthingQueue.setBounds(6, 2, 138, 16);
		panelQueueStatus.add(lblDeberthingQueue);
		
		JPanel panelTaxiStatus = new JPanel();
		panelTaxiStatus.setBounds(6, 367, 441, 73);
		contentPane.add(panelTaxiStatus);
		panelTaxiStatus.setLayout(null);
		
		txtpnTaxiStatus = new JTextPane();
		txtpnTaxiStatus.setBounds(6, 6, 429, 62);
		txtpnTaxiStatus.setText("TAXI STATUS");
		panelTaxiStatus.add(txtpnTaxiStatus);
		
		JPanel panelAnimation = new JPanel();
		panelAnimation.setBounds(6, 480, 441, 66);
		contentPane.add(panelAnimation);
		
		JTextArea txtrFancyAnimationGoes = new JTextArea();
		txtrFancyAnimationGoes.setText("ANIMATION PANEL");
		panelAnimation.add(txtrFancyAnimationGoes);
		
		txtpnIsStorming = new JTextPane();
		txtpnIsStorming.setText("Storm : off");
		txtpnIsStorming.setBounds(6, 444, 441, 30);
		contentPane.add(txtpnIsStorming);
		
		try{
			loadEvents();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		t = 0;		// reset time
		currentEvent = events.getFirst();
	}
	
	/**
	 * Load simulation log into memory
	 */
	public void loadEvents() throws Exception {
		//Locate and load config file
		Path simlog = Paths.get("");		// Get current working directory
		simlog = simlog.resolve(SIMULATION_LOG_NAME);		// Append simlog name to directory
		
		// initialize event list
		events = new LinkedList<String[]>();
		for(String[] l : events) {
			l = new String[6];
		}
		
		BufferedReader br = new BufferedReader(new FileReader(simlog.toAbsolutePath().toString()));
		String line = null;
		while((line = br.readLine()) != null) {
			events.add(line.split(","));
		}
		br.close();
	}
	
	/**
	 * Process a line the output file as an event, update model accordingly
	 */
	public void handleEvent(String[] event) throws Exception {
		int eventType = Integer.parseInt(event[1]);
		int currentTaxiStatus = Integer.parseInt(event[2]);
		
		// event types 1-3 correspond to a new plane landing on the runway (of types 1 through 3 repsectively)
		if(eventType > 0 && eventType <= 3) {
			berthingQueue.add(new Plane(event[1],event[3]));
			taxiStatus = currentTaxiStatus;
		}
		
		// event types 4 and 5 correspond to a storm starting and ending respectively
		if(eventType > 3 && eventType <= 5) {
			if (eventType == 4) { isStorming = true; }
			if (eventType == 5) { isStorming = false;} 
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 6 corresponds to the taxi starting to berth a plane
		if(eventType == 6) {
			// remove from berthing queue (FIFO)
			transit.add(berthingQueue.removeFirst());
			// update taxi status
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 7 corresponds to the taxi starting to deberth a plane
		if(eventType == 7) {
			Plane p = null;
			// find plane to deberth
			for(Plane pp : deberthingQueue) {
				if(pp.getId() == Integer.parseInt(event[3])){
					p = pp;		//hah, pee pee.
				}
			}
			// deberth it
			deberthingQueue.remove(p);
			System.out.println("Removed plane from deberthing queue: " + p.getId());
			// update taxi status
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 8 corresponds to plane loading activity finishing
		if(eventType == 8) {
			// find plane to update
			Plane p = null;
			for(Plane pp : deberthingQueue) {
				if(pp.getId() == Integer.parseInt(event[3])){
					p = pp;
				}
			}
			p.setLoading(false);
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 9 corresponds to a berth activity finishing
		if(eventType == 9) {
			Plane p = null;
			// find plane in transit to berth
			for(Plane pp : transit){
				if(pp.getId() == Integer.parseInt(event[3]))
					p = pp;
			}
			p.setBerthNumber(event[5]);
			p.setLoading(true);
			deberthingQueue.add(p);
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 10 corresponds to a deberth activity finishing
		if(eventType == 10) {
			// update taxi status
			taxiStatus = currentTaxiStatus;
		}
		
		// event type 11 corresponds to the taxi returning to berth with no plane
		if(eventType == 11) {
			taxiStatus = currentTaxiStatus;
		}
		
	}
	
	/**
	 * Redraw view based on the current state of the model
	 */
	public void draw() {
		JTextPane[] berthingQueueTextPanes = {txtpnBerthingQueue1, txtpnBerthingQueue2, txtpnBerthingQueue3};
		JTextPane[] deberthingQueueTextPanes = {txtpnDeberthingQueue1, txtpnDeberthingQueue2, txtpnDeberthingQueue3};

		// draw deberthing queue
		for(JTextPane tp : deberthingQueueTextPanes) {
			tp.setText("");
		}
		for(Plane p : deberthingQueue) {
			if(p.berthNumber == 1)
				txtpnDeberthingQueue1.setText(p.getInfo());
			if(p.berthNumber == 2)
				txtpnDeberthingQueue2.setText(p.getInfo());
			if(p.berthNumber == 3)
				txtpnDeberthingQueue3.setText(p.getInfo());
		}
		
//		 draw berthing queue
		for(JTextPane tp : berthingQueueTextPanes) {
			tp.setText("");
		}
		if(berthingQueue.size() >= 3) {
			txtpnBerthingQueue1.setText(berthingQueue.get(0).getInfo());
			txtpnBerthingQueue2.setText(berthingQueue.get(1).getInfo());
			txtpnBerthingQueue3.setText(berthingQueue.get(2).getInfo());
			txtpnBerthingQueueOverflow.setText((berthingQueue.size() - 3) + " more in queue");
		}	else {
			int i = 0;
			while(i < berthingQueue.size()) {
				berthingQueueTextPanes[i].setText(berthingQueue.get(i).getInfo());
				i++;
			}
			while(i < berthingQueueTextPanes.length) {
				berthingQueueTextPanes[i].setText("");
				i++;
			}
		}
		
		// update slider position
		slider.setValue(t);
		
		// draw taxi status
		if(taxiStatus == TAXI_BERTHING_PLANE)
			txtpnTaxiStatus.setText("Taxi status : Berthing Plane");
		else if(taxiStatus == TAXI_DEBERTHING_PLANE)
			txtpnTaxiStatus.setText("Taxi status : Deberthing Plane");
		else if(taxiStatus == TAXI_IDLE)
			txtpnTaxiStatus.setText("Taxi status : Idle");
		else if(taxiStatus == TAXI_TRAVELLING_TO_RUNWAY)
			txtpnTaxiStatus.setText("Taxi status : Travelling to runway");
		else if(taxiStatus == TAXI_TRAVELLING_TO_BERTHS)
			txtpnTaxiStatus.setText("Taxi status : Travelling to berths");
		// draw storm status
		txtpnIsStorming.setText("Storming : " + isStorming);
		
		this.txtfldCurrentHour.setText("" + (t/60.0f));
	}
}
