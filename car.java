package project3;

/**
 * @author tyler spring
 *  This is the car class. Here is where the car threads are passed to and processed depending on what the user
 *  does during the simulation.
 *
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class car implements Runnable {
	// Uses the same logic with atomic booleans but also has some for current light
	// for a car thread, and if a thread is suspended.
	private int x;
	private final AtomicBoolean appRun = new AtomicBoolean(false);
	public final AtomicBoolean curLit = new AtomicBoolean(false);
	public final AtomicBoolean suspd = new AtomicBoolean(false);
	private String tName = "";
	public Thread carThread;
	private int speed;

	// Constructor for the car class. Is passed the thread name, then a minimum and
	// maximum for the thread. Prints a string from when a thread is made.
	public car(String carName, int press, int brake) {
		this.tName = carName;
		this.x = ThreadLocalRandom.current().nextInt(press, brake);
		System.out.println("Car " + tName + " made.");
	}

	// Returns the current x position for the thread. I did not see the point in
	// creating one for the y. But then again that might explain some of my errors.
	public synchronized int getPosition() {

		return x;
	}

	// Method used to start car threads.
	public void start() {
		System.out.println("Thread" + tName + "started.");
		if (carThread == null) {
			carThread = new Thread(this, tName);
			carThread.start();
		}

	}

	// Method used to stop car threads.
	public void stop() {
		carThread.interrupt();
		appRun.set(false);
		System.out.println(tName + " Stopped.");
	}

	// Method used to suspend car threads.
	public void suspend() {
		suspd.set(true);
		System.out.println(tName + " suspended.");
	}

	// Method used to resume car threads after being paused.
	public synchronized void resume() {
		if (suspd.get() || curLit.get()) {
			suspd.set(false);
			curLit.set(false);
			notify();
			System.out.println(tName + " resumed");
		}
	}

	// Method used to calculate speed of current threads. If it is 0, or just not
	// active, it returns 0.
	public Object getSpeed() {
		if (appRun.get()) {
			if (curLit.get())
				speed = 0;
			else
				speed = 3 * 60;

		} else
			speed = 0;
		return speed;
	}

	// Overridden run method. Tests boolean to see if the simulation is running then
	// validates by checking suspend and curlit values.
	@Override
	public void run() {
		System.out.println(carThread + " is running");
		appRun.set(true);
		while (appRun.get()) {
			try {
				while (x < 500) {
					synchronized (this) {
						while (suspd.get() || curLit.get()) {
							System.out.println(tName + " currently waiting.");
							wait();
						}
					}
					if (appRun.get()) {
						Thread.sleep(100);
						x += 5;
					}
				}
				x = 0;
			} catch (InterruptedException exe) {
				return;
			}
		}

	}

}
