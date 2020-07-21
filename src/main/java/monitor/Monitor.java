package monitor;

import logger.Log;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;

public class Monitor {
  private final Lock lock;
  private final RedDePetri RdP;
  private final Politica politica;
  private final ArrayList<Condition> condiciones;
  private final ArrayList<Boolean> encolados;
  private Log log;

  /**
   * Construye el monitor, con todos los objetos necesarios para controlar la
   * concurrencia.
   * 
   * @param log objeto necesario para actualizar un txt cada vez que se realiza un
   *            disparo.
   */
  public Monitor(Log log) {
    lock = new ReentrantLock(true);
    RdP = new RedDePetri();
    politica = new Politica(RdP.getCantTransiciones());
    condiciones = new ArrayList<Condition>();
    encolados = new ArrayList<Boolean>();
    for (int i = 0; i < RdP.getCantTransiciones(); i++) {
      condiciones.add(lock.newCondition());
      encolados.add(false);
    }
    this.log = log;
  }

  /**
   * Dispara la transicion que corresponde al indice pasado como argumento.
   * ThreadSafe: solo dispara la transicion cuando la politica y el marcado actual
   * de la red de petri lo permiten.
   * 
   * @param indice representa a la transicion que se desea disparar.
   */
  public void dispararTransicion(int indice) {
    lock.lock();
    while (!RdP.sensibilizadoTransicion(indice) || !politica.senializacion(indice)) {
      encolados.set(indice, true);
      try {
        condiciones.get(indice).await();
      } catch (InterruptedException e) {
        System.out.println(Thread.currentThread().getName() + " interrumpido en la condicion de transicion " + indice);
        Thread.currentThread().interrupt();
        return;
      }
    }
    encolados.set(indice, false);
    while (!RdP.disparar(indice)) {
      try {
        condiciones.get(politica.despertar(sensiYencol(), RdP.getMatriz(), indice)).signal();
      } catch (NullPointerException e) {
      }
      long sleep = RdP.sleepTime(indice);
      lock.unlock();
      try {
        Thread.sleep(sleep);
      } catch (IllegalArgumentException e) {
        System.out.println("Superado el BETA! Soy " + indice);
        return;
      } catch (InterruptedException e) {
        e.printStackTrace();
        return;
      }
      lock.lock();
      politica.reseteo();
    }
    log.actualizarLog(indice, RdP.getMatriz());
    try {
      condiciones.get(politica.despertar(sensiYencol(), RdP.getMatriz(), indice)).signal();
    } catch (NullPointerException e) {
    }
    lock.unlock();
  }

  /**
   * Toma la lista de encolados y se fija que tambien estan sensibilizados. Si eso
   * es cierto, los agrega a un array de sensibilizados y encolados y una vez
   * terminado, devuelve ese array.
   * 
   * @return arrayList con los indices de todas las transiciones encoladas en el
   *         monitor, y sensibilizadas al mismo tiempo.
   */
  private ArrayList<Integer> sensiYencol() {
    Iterator<Integer> it = RdP.habilitacion().iterator();
    ArrayList<Integer> sensiYencol = new ArrayList<Integer>();
    while (it.hasNext()) {
      int aux = it.next();
      if (encolados.get(aux)) {
        sensiYencol.add(aux);
      }
    }
    return sensiYencol;
  }
}
