package main;

import monitor.Monitor;

public class JVM2 implements Runnable {
  private Monitor monitor;

  public JVM2(Monitor monitor) {
    this.monitor = monitor;
  }

  public void run() {
    while (Sistema.TAREASTOTALES > Sistema.tareasHechasCore1 + Sistema.tareasHechasCore2) {
      monitor.dispararTransicion(9);
    }
  }
}
