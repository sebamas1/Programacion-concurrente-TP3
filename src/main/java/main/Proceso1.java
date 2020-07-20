package main;

import monitor.Monitor;

public class Proceso1 implements Runnable {
  private Monitor monitor;

  protected Proceso1(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(12); // T5
      monitor.dispararTransicion(13); // T7
      Sistema.tareasHechasCore1++;
    }
  }
}
