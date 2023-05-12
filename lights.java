package project3;

/**
 * @author tyler spring
 *  This is the lights class. Here is where the light threads are passed to and processed depending on what the user
 *  does during the simulation.
 *
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.*;

public class lights implements Runnable {
	// Labels, ints for tracking, array for iterating, and reuse of atomic booleans
	// to test simulation running and suspend state of whatever current light
	// thread.
	private final JLabel lightLabel;
	private int light = 0;

	private final String[] LIGHTS = { "Green", "Yellow", "Red" };
	private String curLight = LIGHTS[light];

	private final AtomicBoolean appRun = new AtomicBoolean(false);
	public final AtomicBoolean suspd = new AtomicBoolean(false);

	Thread litThread;
	String litName;

	// Constructor for the lights class. Takes thread name as string, and label for
	// the current thread being passed to it. Prints string when it is created.
	public lights(String thisLit, JLabel lightLabel) {
		this.litName = thisLit;
		this.lightLabel = lightLabel;
		System.out.println(litName + " lights made.");
	}

	// Method used to start light threads.
	public void start() {
		System.out.println(litName + " started.");
		if (litThread == null) {
			litThread = new Thread(this, litName);
			litThread.start();
		}

	}

	// Method used to resume light threads.
	public synchronized void resume() {
		suspd.set(false);
		notify();
		System.out.println(litName + " resumed");
	}

	// Method used to suspend light threads.
	public void suspend() {
		suspd.set(true);
		System.out.println(litName + " suspended");
	}

	// Method used to stop light threads.
	public void stop() {
		litThread.interrupt();
		appRun.set(false);
		System.out.println(litName + " stopped.");
	}

	// Method used to interrupt threads when needed.
	public void interrupt() {
		litThread.interrupt();
	}

	// Method used to find and assign a color to the current light thread by going
	// through the above array of the three colors. Then returns said color to have
	// it tested in mainGUI.
	public String getColor() {
		this.curLight = LIGHTS[light];
		return this.curLight;
	}

	// Overridden run method. Tests to see if simulation is running and state of
	// suspended.
	@Override
	public void run() {
		System.out.println(litName + " running.");
		appRun.set(true);
		while (appRun.get()) {
			try {
				synchronized (this) {
					while (suspd.get()) {
						System.out.println(litName + " currently waiting.");
						wait();
					}
				}
				// Switch statement used assign color of label that indicates the current light
				// at each light thread. Each are separate.
				switch (getColor()) {
				case "Green":
					lightLabel.setForeground(Color.GREEN);
					lightLabel.setText(getColor());
					Thread.sleep(100);
					light++;

					break;
				case "Yellow":
					lightLabel.setForeground(Color.YELLOW);
					lightLabel.setText(getColor());
					Thread.sleep(50);
					light++;
					break;
				case "Red":
					lightLabel.setForeground(Color.RED);
					lightLabel.setText(getColor());
					Thread.sleep(50);
					light = 0;
					break;
				default:
					break;
				}
			} catch (InterruptedException exe) {
				suspd.set(true);
			}
		}

	}

}
