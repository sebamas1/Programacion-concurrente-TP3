package main;

import monitor.Monitor;

public class JVM1 implements Runnable {
  private Monitor monitor;

  protected JVM1(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (Sistema.TAREASTOTALES > Sistema.tareasHechasCore1 + Sistema.tareasHechasCore2) {
      monitor.dispararTransicion(10);
    }
  }
}
