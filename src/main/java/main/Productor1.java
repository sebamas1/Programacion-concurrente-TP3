package main;

import monitor.Monitor;

public class Productor1 implements Runnable {
  private Monitor monitor;

  public Productor1(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(11); // T4
    }
  }
}
