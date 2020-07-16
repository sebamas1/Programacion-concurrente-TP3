package main;

import monitor.Monitor;

public class Productor2 implements Runnable {
  private Monitor monitor;

  public Productor2(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(6); // T10
    }
  }
}
