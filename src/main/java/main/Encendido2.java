package main;

import monitor.Monitor;

public class Encendido2 implements Runnable {
	Monitor monitor;

	public Encendido2(Monitor monitor) {
		this.monitor = monitor;
	}

	public void run() {
		while (true) {
			monitor.dispararTransicion(14); // T8
			monitor.dispararTransicion(4); // PowerUpDelay2
			monitor.dispararTransicion(2); // PowerDownThresHold2
		}
	}
}
