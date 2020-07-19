package main;

import monitor.Monitor;

public class Proceso2 implements Runnable {
  private Monitor monitor;

  protected Proceso2(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(7);
      monitor.dispararTransicion(8);
      Sistema.tareasHechasCore2++;
    }
  }
}
