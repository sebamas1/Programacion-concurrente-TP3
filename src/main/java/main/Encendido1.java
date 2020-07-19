package main;

import monitor.Monitor;

public class Encendido1 implements Runnable {
  Monitor monitor;

  protected Encendido1(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(5); // T0
      monitor.dispararTransicion(3); // PowerUpDelay
      monitor.dispararTransicion(1);// PowerDownTheshold 1
    }
  }
}
