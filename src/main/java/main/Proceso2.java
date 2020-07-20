package main;

import monitor.Monitor;

public class Proceso2 implements Runnable {
  private Monitor monitor;

  protected Proceso2(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (true) {
      monitor.dispararTransicion(7); //T11
      monitor.dispararTransicion(8); //T12
      Sistema.tareasHechasCore2++;
    }
  }
}
