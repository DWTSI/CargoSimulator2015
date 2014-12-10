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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
import java.util.concurrent.TimeUnit;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


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
	private JPanel panelQueueStatus;
	private JPanel panelTimeControl;
	
	
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
	long ANIMATION_THREAD_DELAY = 50;  //in milliseconds 
	int ANIMATION_INTERVAL = 30; // in minutes. 600 is 10 hours
	final int MAX_TIME = 525599;
	private JTextField txtfldInterval;
	
	/*########### Nested Classes ###########*/
	
	private class Plane {
	    private int type;
	    private int id;
	    private int berthNumber;
	    private boolean isLoading;
	    private boolean isFinishedLoading = false;
	    private int timeStartLoading;
	    private int loadingTime = 0;
	    
	    private int[] loadingTimes = {18, 24, 36};
	    
	    public int getType(){ return this.type; }
	    public int getId(){ return this.id; }
	    public void setBerthNumber(String b){ this.berthNumber = Integer.parseInt(b); }
	    public void setLoading(boolean l){ 
	    	this.isLoading = l;
	    	if (l == true) {
	    		timeStartLoading = t;
	    	}
	    }
	    
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
	    
	    public float getPercentLoaded() {
	    	
	    	//int totalTime = loadingTimes[type-1];
	    	//totalTime = totalTime*60;
	    	
	    	float result = (float)(t-timeStartLoading)/((float)loadingTime);
	    	if (result > 1)
	    		result = 1;
	    	return result;
	    }
	    
	    public String getPercentBar() {
	    	String s = "[";
	    	int length = 20;
	    	int percent = (int)((float)length*getPercentLoaded());
	    	int blank = length - percent;
	    	for (int i=0; i<percent; i++)
	    		s += "=";
	    	for (int i=0; i<blank; i++)
	    		s += "..";
	    	s += "]";
	    	return s;
	    }
	    
	    // returns a formatted string to be displayed in UI text panels
	    public String getInfo() {
	    	String r = "";
	    	r += "Plane ID: " + id + "\n";
	    	r += "Plane Type: " + type + "\n";
	    	if (isLoading) {
	    		//r += "Plane loading: " + isLoading + "\n";
	    		//r += "Percent loaded: " + (t-timeStartLoading) + " " + (loadingTimes[type-1]*60) + "\n";
	    		r += getPercentBar() + "\n";
	    	}
	    	
	    	
	    	return r;
	    }
	}
	
	private class AnimationThread extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			for(int i = 0; !isCancelled() && t < MAX_TIME; i++){
				int eventTime = Integer.parseInt(currentEvent[0]);
				t++;
				
				//This is really bad. But it works.
				while(t == eventTime) {
					handleEvent(currentEvent);
					
					if(events.indexOf(currentEvent) < events.size() - 1) {
						currentEvent = events.get(events.indexOf(currentEvent) + 1);
						eventTime = Integer.parseInt(currentEvent[0]);
					}
					//if it's the last event, increment t
					if (events.indexOf(currentEvent) == events.size() - 1) {
						t++;
					}
				}
				draw();
				//Thread.sleep(ANIMATION_THREAD_DELAY);
//				TimeUnit.NANOSECONDS.sleep(ANIMATION_THREAD_DELAY);
			}
			
			if( t == MAX_TIME )
				closeSim();
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
		setBackground(new Color(240, 240, 240));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// initialize globals
		deberthingQueue = new LinkedList<Plane>();
		berthingQueue = new LinkedList<Plane>();
		transit = new LinkedList<Plane>();
		isStorming = false;
		
		setTitle("CargoSimulator2015");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 453, 425);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(192, 192, 192));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelTimeControl = new JPanel();
		panelTimeControl.setBackground(Color.LIGHT_GRAY);
		panelTimeControl.setBounds(6, 6, 441, 97);
		contentPane.add(panelTimeControl);
		panelTimeControl.setLayout(null);
		
		txtfldAnimationRate = new JTextField();
		txtfldAnimationRate.setText("50");
		txtfldAnimationRate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nuDelay = 50;
				try {
					nuDelay = Integer.parseInt(txtfldAnimationRate.getText());
				}
				catch (NumberFormatException asdfasdf) {
					txtfldAnimationRate.setText("50");
				}
				if (nuDelay > 0 && nuDelay < 100000)
				ANIMATION_THREAD_DELAY = nuDelay;
			}
		});
		txtfldAnimationRate.setBounds(135, 32, 86, 28);
		panelTimeControl.add(txtfldAnimationRate);
		txtfldAnimationRate.setColumns(10);
		
		txtfldInterval = new JTextField();
		txtfldInterval.setText("30");
		txtfldInterval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nuDelay = 30;
				try {
					nuDelay = Integer.parseInt(txtfldInterval.getText());
				}
				catch (NumberFormatException asdfasdf) {
					txtfldInterval.setText("30");
				}
				if (nuDelay > 0 && nuDelay < 100000)
				ANIMATION_INTERVAL = nuDelay;
			}
		});
		txtfldInterval.setBounds(232, 32, 86, 28);
		panelTimeControl.add(txtfldInterval);
		txtfldInterval.setColumns(10);
		

		txtfldCurrentHour = new JTextField();
		txtfldCurrentHour.setText("0");
		txtfldCurrentHour.setBounds(329, 32, 86, 28);
		panelTimeControl.add(txtfldCurrentHour);
		txtfldCurrentHour.setColumns(10);
		
		
		slider = new JSlider();
		slider.setBackground(Color.LIGHT_GRAY);
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
		

		JLabel lblAnimationRate = new JLabel("msec/interval:");
		lblAnimationRate.setBounds(133, 12, 73, 16);
		panelTimeControl.add(lblAnimationRate);
		
		JLabel lblCurrentHour = new JLabel("  Current Hour:");
		lblCurrentHour.setBounds(326, 12, 98, 16);
		panelTimeControl.add(lblCurrentHour);
		
		JButton btnStop = new JButton("END");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentThread.cancel(true);
				closeSim();
			}
		});
		btnStop.setBounds(6, 62, 117, 29);
		panelTimeControl.add(btnStop);
		
		JLabel lblIntervalminutes = new JLabel("Interval (minutes):");
		lblIntervalminutes.setBounds(229, 12, 98, 16);
		panelTimeControl.add(lblIntervalminutes);
		
		panelQueueStatus = new JPanel();
		panelQueueStatus.setBackground(Color.LIGHT_GRAY);
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
		txtpnBerthingQueueOverflow.setBackground(Color.LIGHT_GRAY);
		txtpnBerthingQueueOverflow.setText("0 more in queue");
		txtpnBerthingQueueOverflow.setBounds(222, 225, 204, 16);
		panelQueueStatus.add(txtpnBerthingQueueOverflow);
		
		JLabel lblRunway = new JLabel("Runway :");
		lblRunway.setBounds(222, 2, 61, 16);
		panelQueueStatus.add(lblRunway);
		
		JLabel lblDeberthingQueue = new JLabel("Deberthing Queue :");
		lblDeberthingQueue.setBounds(6, 2, 138, 16);
		panelQueueStatus.add(lblDeberthingQueue);
		
		txtpnTaxiStatus = new JTextPane();
		txtpnTaxiStatus.setBackground(Color.LIGHT_GRAY);
		txtpnTaxiStatus.setBounds(6, 225, 204, 16);
		panelQueueStatus.add(txtpnTaxiStatus);
		txtpnTaxiStatus.setText("TAXI STATUS");
		
		txtpnIsStorming = new JTextPane();
		txtpnIsStorming.setBackground(Color.LIGHT_GRAY);
		txtpnIsStorming.setText("Storming : false");
		txtpnIsStorming.setBounds(160, 362, 113, 21);
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
			l = new String[7];
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
			p.loadingTime = Integer.parseInt(event[6]);
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
		
		/* Draw only every animation interval */
		if (t != (t/ANIMATION_INTERVAL)*ANIMATION_INTERVAL)
			return;
		
		// Make the color of the background darker if there is a storm.
		Color c;
		if (!isStorming) {
			c = new Color(217, 217, 217);
		}
		else {
			c = Color.LIGHT_GRAY;
		}
		contentPane.setBackground(c);
		panelQueueStatus.setBackground(c);
		panelTimeControl.setBackground(c);
		slider.setBackground(c);
		txtpnIsStorming.setBackground(c);
		txtpnTaxiStatus.setBackground(c);
		txtpnBerthingQueueOverflow.setBackground(c);
		
		
		
		try {
			TimeUnit.MILLISECONDS.sleep(ANIMATION_THREAD_DELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// draw deberthing queue
		for(JTextPane tp : deberthingQueueTextPanes) {
			tp.setText("");
			tp.setBackground(new Color(255, 255, 255));
		}
		for(Plane p : deberthingQueue) {
			deberthingQueueTextPanes[p.berthNumber-1].setText(p.getInfo());
//			if (!p.isLoading) 
//				deberthingQueueTextPanes[p.berthNumber-1].setBackground(new Color(0, 240, 0));
//			else
//				deberthingQueueTextPanes[p.berthNumber-1].setBackground(new Color(255, 255, 255));
			
			Color color = getColorFromId(p.id);
			deberthingQueueTextPanes[p.berthNumber-1].setBackground(color);
		}
		
		// draw berthing queue
		for(JTextPane tp : berthingQueueTextPanes) {
			tp.setText("");
		}
		if(berthingQueue.size() >= 3) {
			txtpnBerthingQueue1.setText(berthingQueue.get(0).getInfo());
			txtpnBerthingQueue2.setText(berthingQueue.get(1).getInfo());
			txtpnBerthingQueue3.setText(berthingQueue.get(2).getInfo());
			Color color = getColorFromId(berthingQueue.get(0).id);
			berthingQueueTextPanes[0].setBackground(color);
			color = getColorFromId(berthingQueue.get(1).id);
			berthingQueueTextPanes[1].setBackground(color);
			color = getColorFromId(berthingQueue.get(2).id);
			berthingQueueTextPanes[2].setBackground(color);
			txtpnBerthingQueueOverflow.setText((berthingQueue.size() - 3) + " more in queue");
		}	else {
			int i = 0;
			while(i < berthingQueue.size()) {
				berthingQueueTextPanes[i].setText(berthingQueue.get(i).getInfo());
				Color color = getColorFromId(berthingQueue.get(i).id);
				berthingQueueTextPanes[i].setBackground(color);
				i++;
			}
			while(i < berthingQueueTextPanes.length) {
				berthingQueueTextPanes[i].setText("");
				berthingQueueTextPanes[i].setBackground(Color.WHITE);
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
		
		// draw current time
		this.txtfldCurrentHour.setText("" + ((double)Math.round(t/60.0f * 100)/100));
	}
	
	
	
	private Color getColorFromId(int planeId) {
		int colorId = planeId;
		while (colorId > 8) {
			colorId = colorId - 8;
		}
		
		switch (colorId) {
		case 1:
			return Color.RED;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.CYAN;
		case 4:
			return Color.YELLOW;
		case 5:
			return Color.MAGENTA;
		case 6:
			return Color.ORANGE;
		case 7:
			return Color.BLUE;
		case 8:
			return Color.PINK;
		}
		
		return Color.WHITE;
		
	}
	
	public void closeSim() {
		CargoSimStatUI.main(null);
		this.setVisible(false);
	}
}
