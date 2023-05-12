package project3;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author tyler spring 
 * Project 3 CMSC 335 
 * 			This is the third project for CMSC
 *         335. For this project the following classes work together by the use
 *         of threads to simulate a traffic light at an intersection. The
 *         threads work concurrently as well as the lights for the
 *         intersections. When running, they provide the location of a thread,
 *         car, in regards to another thread, a light. Through this it
 *         calculates the speed of a car at an exact moment, in one second
 *         intervals. The window provides buttons that allow the user to start,
 *         pause, then continue, add another light, add another car, and stop
 *         the simulation. When the user adds another car or light another
 *         object of that type is made and the user is notified via a pop up
 *         window and in the console.
 */
public class mainGUI extends JFrame implements Runnable, ChangeListener {

	/**
	 * Here is were the components for the GUI are made. Labels, buttons, sliders.
	 */
	private static final long serialVersionUID = 1L;
	public int addCar = 4;
	public int addLit = 3;
	// Fun fact, if you try to create these labels in one line, it gives a null
	// error. Least thats what happened to me.
	static JLabel timer = new JLabel();
	static JLabel car1 = new JLabel();
	static JLabel car2 = new JLabel();
	static JLabel car3 = new JLabel();
	private final JButton start = new JButton("Start");
	private final JButton pause = new JButton("Pause");
	private final JButton stop = new JButton("Stop");
	private final JButton add = new JButton("Add car");
	private final JButton addL = new JButton("Add light");
	// I went with sliders because they seemed better than having check boxes
	// randomly going off.
	static JSlider slide1 = new JSlider(0, 5000);
	static JSlider slide2 = new JSlider(0, 5000);
	static JSlider slide3 = new JSlider(0, 5000);
	static JSlider slide4 = new JSlider(0, 5000);
	// Boolean created for the running of the simulation, followed atomic boolean in
	// order be used by other classes, threads.
	private static boolean appRun;
	private static final AtomicBoolean appRunning = new AtomicBoolean(false);
	// Light objects made. The name of the thread and the JLabel for the car is
	// passed in order to make a light object.
	lights first = new lights("Thread 1", car1);
	lights second = new lights("Thread 2", car2);
	lights third = new lights("Thread 3", car3);
	// Car objects made, the name of the thread, and the
	car one = new car("Thread one", 1000, 2000);
	car two = new car("Thread two", 1000, 2000);
	car three = new car("Thread three", 1000, 2000);
	car four = new car("Thread four", 1000, 2000);
	// This array of car objects is used to track x positions of said cars when the
	// simulation is first ran. The same is for the lights objects array. They are
	// both used to be iterated through for tracking and the event listeners farther
	// down.
	car[] carConvoy = { one, two, three, four };
	lights[] rgbLights = { first, second, third };
	static Thread windowTraf;
	// Here a 2D array with a string of the current thread/car with method call to
	// the position is made. This is what is used to track each position of each
	// thread at the given time.
	Object[][] trafficTimes = { { "Car 1", one.getPosition(), 0, 0 }, { "Car 2", two.getPosition(), 0, 0 },
			{ "Car 3", three.getPosition(), 0, 0 }, { "Car 4", four.getPosition(), 0, 0 } };
	// This array of strings is passed to a JTable that is used to display the cars,
	// their x position, y position, and their speed. This is later passed to a
	// JScroll.
	String[] col = { "Car", "x", "y", "speed" };
	JTable data = new JTable(trafficTimes, col);

	// Constructor for this class. What calls the boolean for starting the thread
	// that is the window, calls the startGUI method and the buttons method to
	// generate the window and simulation.
	public mainGUI() {
		super("Traffic Tracking sim");
		appRun = Thread.currentThread().isAlive();
		window();
		btns();

	}

	// Display method, just used to set bounds and visibility for the window.
	private void display() {
		setSize(700, 400);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	// window method is used to generate new labels, give value to sliders, and set
	// the layout for the window, that was a total nightmare to debug.
	public void window() {
		JLabel txt1 = new JLabel("Traffic tracking sim");
		JLabel txt2 = new JLabel("Press start to begin. Pause to pause, Stop to stop.");
		// Labels for current time and intersections
		JLabel timer2 = new JLabel("Time: ");
		JLabel trafLit1 = new JLabel("Intersection 1: ");
		JLabel trafLit2 = new JLabel("Intersection 2: ");
		JLabel trafLit3 = new JLabel("Intersection 3: ");
		// listeners to declare sliders.
		slide1.addChangeListener(this);
		slide2.addChangeListener(this);
		slide3.addChangeListener(this);
		slide4.addChangeListener(this);
		// Value being set for the sliders, calling back to the array of the car objects
		// with these method calls.
		slide1.setValue(one.getPosition());
		slide2.setValue(two.getPosition());
		slide3.setValue(three.getPosition());
		slide4.setValue(four.getPosition());
		// spacing for slider 1
		slide1.setMajorTickSpacing(1000);
		slide1.setPaintTicks(true);
		// spacing for slider 2
		slide2.setMajorTickSpacing(2000);
		slide2.setPaintTicks(true);
		// spacing for slider 3
		slide3.setMajorTickSpacing(3000);
		slide3.setPaintTicks(true);
		// spacing for slider 4
		slide4.setMajorTickSpacing(4000);
		slide4.setPaintTicks(true);
		// Size of chart/scroll object for where cars and data are shown. Regretted
		// using scroll later, but ran out of time.
		data.setPreferredScrollableViewportSize(new Dimension(400, 65));
		data.setFillsViewportHeight(true);
		// Panel object used to add components to.
		JPanel dataWindow = new JPanel();
		// Scroll object passed data and added to window.
		JScrollPane scroller = new JScrollPane(data);
		dataWindow.add(scroller);
		dataWindow.setBackground(Color.BLACK);
		dataWindow.setForeground(Color.WHITE);
		// Layout for window object created here.
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		// Formatting for the window created here. I used grouplayout because of how it
		// worked in my last project and I was too lazy to be picky with how I wanted
		// it.
		layout.setHorizontalGroup(layout.createSequentialGroup().addContainerGap(25, 40)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(txt1)
						.addComponent(txt2)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addComponent(timer).addComponent(timer2)))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addGroup(layout.createSequentialGroup().addComponent(start).addComponent(pause)
										.addComponent(add).addComponent(addL).addComponent(stop)))
						.addComponent(slide1).addComponent(slide2).addComponent(slide3).addComponent(slide4)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addGroup(layout.createSequentialGroup().addComponent(trafLit1).addComponent(car1)
										.addContainerGap(10, 50).addComponent(trafLit2).addComponent(car2)
										.addContainerGap(10, 30).addComponent(trafLit3).addComponent(car3))
								.addComponent(dataWindow)))
				.addContainerGap(20, 30));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup().addComponent(txt1)).addComponent(txt2).addGap(50, 50, 150)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(timer)
						.addComponent(timer2))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(start)
						.addComponent(pause).addComponent(add).addComponent(addL).addComponent(stop))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(slide1)
						.addComponent(slide2).addComponent(slide3).addComponent(slide4))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(trafLit1)
						.addComponent(car1).addComponent(trafLit2).addComponent(car2).addComponent(trafLit3)
						.addComponent(car3))
				.addComponent(dataWindow)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addGap(10, 25, 30))
				.addGap(10, 25, 30));
	}

	// Method that creates buttons that use action listeners.
	private void btns() {
		// Start method that waits for a click and then sets all threads for car and
		// lights to start position.
		start.addActionListener((ActionEvent e) -> {
			if (!appRunning.get()) {
				System.out.println(Thread.currentThread().getName() + " call start");
				first.start();
				second.start();
				third.start();
				one.start();
				two.start();
				three.start();
				four.start();
				windowTraf.start();
			}
			appRunning.set(true);
		});
		// Car add button. Listens for click to then create a new car object, then
		// displays that a car has been added in pop up window and in the console.
		add.addActionListener((ActionEvent e) -> {

			if (!appRunning.get()) {
				System.out.println("Car being added.");
				addCar++;
				JLabel nCar = new JLabel();
				car newCar = new car("Thread " + addCar, 1000, 2000);
				newCar.getPosition();
				JOptionPane.showMessageDialog(nCar, "New car added.");

			}
		});
		// Lights add button. Listens for click to then create a new car and lights
		// object, then displays that a car and lights has been added in pop up window
		// and in the console.
		addL.addActionListener((ActionEvent e) -> {
			if (!appRunning.get()) {
				System.out.println("Intersection being added.");
				addLit++;
				JLabel litCar = new JLabel();
				JOptionPane.showMessageDialog(litCar, "New Intersection and car added.");

			}
		});
		// Pause button, this one is different than the others. It first listens for a
		// click to pause the simulation. After that it changes its functionality to a
		// continue button
		// Then it listens again to see if the user would like to continue with the
		// simulation. This can go back and forth.
		pause.addActionListener((ActionEvent e) -> {
			if (appRunning.get()) {
				for (car x : carConvoy) {
					x.suspend();
					System.out.println(Thread.currentThread().getName() + " calling suspend");
				}
				for (lights x : rgbLights) {
					x.interrupt();
					x.suspend();
				}
				// Button changed to continue. Then listens.
				pause.setText("Continue");
				appRunning.set(false);
			} else {
				for (car x : carConvoy) {
					if (x.suspd.get()) {
						x.resume();
						System.out.println(Thread.currentThread().getName() + "calling resume");
					}
				}
				for (lights x : rgbLights) {
					x.resume();
				}
				pause.setText("Pause");
				appRunning.set(true);
			}
		});
		// Button made to stop the simulation. Listens for click then to then stop all
		// the threads. DOES NOT EXIT THE WINDOW.
		stop.addActionListener((ActionEvent e) -> {
			if (appRunning.get()) {
				System.out.println(Thread.currentThread().getName() + "calling stop");
				for (car y : carConvoy) {
					y.stop();
				}
				for (lights y : rgbLights) {
					y.stop();
				}
				appRunning.set(false);
			}
		});
	}

	// statechanged method. When called it assigns the new speed calculation to the
	// array made earlier that was used in the simulation to display data.
	@Override
	public void stateChanged(ChangeEvent e) {
		trafficTimes[0][1] = slide1.getValue();
		trafficTimes[1][1] = slide2.getValue();
		trafficTimes[2][1] = slide3.getValue();
		trafficTimes[3][1] = slide4.getValue();
		trafficTimes[0][3] = one.getSpeed();
		trafficTimes[1][3] = two.getSpeed();
		trafficTimes[2][3] = three.getSpeed();
		trafficTimes[3][3] = four.getSpeed();

	}

	// Here is where the car object array is used again. Here each object of the
	// array is iterated through, having its current light state, color of traffic
	// like, looked at.
	// After the color is found, the car array is iterated through to find its
	// location to determine if it is in the correct bounds and then assigns a car
	// thread to the light thread.
	private void lightData() {
		if (appRunning.get()) {
			switch (first.getColor()) {
			case "Red":
				for (car z : carConvoy) {
					if (z.getPosition() > 500 && z.getPosition() < 1000) {
						z.curLit.set(true);
					}
				}
				break;
			case "Green":
				for (car z : carConvoy) {
					if (z.curLit.get()) {
						z.resume();
					}
				}
				break;

			}
			switch (second.getColor()) {
			case "Red":
				for (car z : carConvoy) {
					if (z.getPosition() > 1500 && z.getPosition() < 2000) {
						z.curLit.set(true);
					}
				}
				break;
			case "Green":
				for (car r : carConvoy) {
					if (r.curLit.get()) {
						r.resume();
					}
				}
				break;
			}
			switch (third.getColor()) {
			case "Red":
				for (car r : carConvoy) {
					if (r.getPosition() > 2500 && r.getPosition() < 3000) {
						r.curLit.set(true);

					}
				}
				break;
			case "Green":
				for (car q : carConvoy) {
					if (q.curLit.get()) {
						q.resume();
					}
				}
				break;
			}
		}
	}

//Method is overridden due to it being used in car and lights classes. Here is just tests the boolean value of the simulation running then reassigns values,data for the sliders.
	@Override
	public void run() {
		while (appRun) {
			if (appRunning.get()) {
				slide1.setValue(one.getPosition());
				slide1.setValue(two.getPosition());
				slide3.setValue(three.getPosition());
				slide4.setValue(four.getPosition());
				lightData();

			}
		}
	}

//Main method. Just creates an object of the mainGUI class and a thread. Then starts the threads.
	public static void main(String[] args) {
		mainGUI test = new mainGUI();
		test.display();
		windowTraf = new Thread(test);
		Thread timez = new Thread(new intervals());
		timez.start();
	}

}
