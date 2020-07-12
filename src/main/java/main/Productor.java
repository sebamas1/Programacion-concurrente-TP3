package main;

import monitor.Monitor;

public class Productor implements Runnable {
	private int tareas = 0;
	private Monitor monitor;

	public Productor(Monitor monitor) {
		this.monitor = monitor;
	}

	public void run() {
		while (tareas < Sistema.TAREASTOTALES) {
			monitor.dispararTransicion(0); // arrival rate
			tareas++;
		}
	}
}