package main;

import java.io.FileNotFoundException;
import logger.Log;
import monitor.Monitor;

public class Sistema {
  public static final int TAREASTOTALES = 433;
  protected static int tareasHechasCore1 = 0;
  protected static int tareasHechasCore2 = 0;
  protected static boolean running = true;
  private final long inicio;
  private final long finall;

  /**
   * Construye el sistema del TP3. Crea los objetos runnable necesarios y se los
   * pasa a los distintos threads. Luego, inicia el programa y espera a que se
   * procesen los productos totales especificados, momento en el cual se espera un
   * tiempo de gracia, y luego se lee el log, y se imprimen en consola los
   * resultados del programa. Todos los threads que hayan quedado vivos, se los
   * "mata".
   */
  @SuppressWarnings("deprecation")
  public Sistema() {
    Log lg = new Log();
    Monitor monitor = new Monitor(lg);
    Proceso1 p1 = new Proceso1(monitor);
    Proceso2 p2 = new Proceso2(monitor);
    Productor p = new Productor(monitor);
    Productor1 pr1 = new Productor1(monitor);
    Productor2 pr2 = new Productor2(monitor);
    Encendido1 e1 = new Encendido1(monitor);
    Encendido2 e2 = new Encendido2(monitor);
    JVM1 jvm1 = new JVM1(monitor);
    JVM2 jvm2 = new JVM2(monitor);
    Thread t1 = new Thread(p1, "Proceso1");
    Thread t2 = new Thread(p2, "Proceso2");
    Thread t3 = new Thread(p, "Productor");
    Thread t4 = new Thread(pr1, "Productor1");
    Thread t5 = new Thread(pr2, "Productor2");
    Thread t6 = new Thread(e1, "Encendido1");
    Thread t7 = new Thread(e2, "Encendido2");
    Thread t8 = new Thread(jvm1, "Garbage Collector 1");
    Thread t9 = new Thread(jvm2, "Garbage Collector 2");
    t1.start();
    t2.start();
    t3.start();
    t4.start();
    t5.start();
    t6.start();
    t7.start();
    t8.start();
    t9.start();

    inicio = System.nanoTime();
    while ((tareasHechasCore1 + tareasHechasCore2) < TAREASTOTALES) {
      System.out.print("");
    }
    finall = System.nanoTime();
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
    }
    try {
      lg.leerResultados();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    System.out.println("Tareas procesadas totales: " + (tareasHechasCore1 + tareasHechasCore2));
    System.out.println("Tareas hechas por el core 1: " + (tareasHechasCore1));
    System.out.println("Tareas hechas por el core 2: " + (tareasHechasCore2));
    System.out.println("Tiempo de ejecucion total en milisegundos: " + (finall - inicio) / 1000000);

    t1.stop();
    t2.stop();
    t3.stop();
    t4.stop();
    t5.stop();
    t6.stop();
    t7.stop();
    t8.stop();
    t9.stop();
  }
}
