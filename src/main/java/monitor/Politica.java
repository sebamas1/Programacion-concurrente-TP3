package monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Politica {
  private RealMatrix vectorPrioridad;
  private double buffer1 = 0;
  private double buffer2 = 0;
  private int transiciones = 0;
  private final ArrayList<Boolean> senializadas;

  /**
   * Crea un objeto de Politica con la cantidad de transiciones especificada.
   * 
   * @param transiciones cantidad de transiciones de la Red De Petri
   */
  public Politica(int transiciones) {
    vectorPrioridad = new Array2DRowRealMatrix();
    this.transiciones = transiciones;
    senializadas = new ArrayList<Boolean>();
    for (int i = 0; i < transiciones; i++) {
      senializadas.add(false);
    }
    try {
      leerMatriz();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Lee la matriz de prioridad para las transiciones pasadas en el .txt.
   * 
   * @throws FileNotFoundException
   */
  private void leerMatriz() throws FileNotFoundException {
    Scanner input = null;
    input = new Scanner(new File("src/main/java/vectorPrioridad.txt"));
    double[][] m0 = new double[transiciones][1];
    for (int i = 0; input.hasNextLine(); i++) {
      m0[i][0] = input.nextInt();
    }
    vectorPrioridad = new Array2DRowRealMatrix(m0);
    input.close();
  }

  /**
   * Este metodo setea la senializacion de la transicion(o thread) que ejecuta
   * este metodo en false, para avisarle a la politica que va a abandonar el
   * monitor, y que puede dejar pasar a otro thread. Ademas, observa el marcado
   * actual, y remueve de la lista(si existe) a la transicion que alimenta al
   * buffer que tenga mas productos.
   * 
   * 
   * @param sensiYencol transiciones sensibilizadas, que estan esperando dentro
   *                    del monitor.
   * @param mAReal      es el marcado actual de la RedDePetri.
   * @param transicion  la transicion que ejecuta este metodo.
   * @return un Integer representando el numero de transicion que se desea
   *         despertar, o null si no hay transiciones para despertar.
   */
  protected Integer despertar(ArrayList<Integer> sensiYencol, RealMatrix mAReal, int transicion) {
    setSenializacionFalse(transicion);
    buffer1 = mAReal.getEntry(0, 0);
    buffer2 = mAReal.getEntry(2, 0);
    if (buffer1 > buffer2) {
      sensiYencol.remove(Integer.valueOf(11));
    } else if (buffer1 < buffer2) {
      sensiYencol.remove(Integer.valueOf(6));
    }
    if (sensiYencol.size() > 0) {
      return prioridad(sensiYencol);
    } else
      return (Integer) null;
  }

  /**
   * Encuentra la transicion de mayor prioridad de entre las transiciones que
   * contiene el ArrayList que se le pasa como argumento y devuelve la que tiene
   * mayor priodidad. En caso de que haya prioridades iguales, devuelve una al
   * azar.
   * 
   * @param sensiYencol ArrayList con los indices de las transiciones
   *                    sensibilizadas, esperando en el monitor.
   * @return retorna la transicion que tiene mas prioridad entre las transiciones
   *         presentes en el ArrayList. Si algunas de las transiciones tienen la
   *         misma prioridad mas alta, devuelve una al azar entre las transiciones
   *         que tienen mas prioridad.
   */
  private int prioridad(ArrayList<Integer> sensiYencol) {
    Iterator<Integer> it1 = sensiYencol.iterator();
    double mayor = 0;
    while (it1.hasNext()) {
      int actual = it1.next();
      double prioAc = vectorPrioridad.getEntry(actual, 0);
      if (prioAc > mayor) {
        mayor = prioAc;
      }
    }
    Iterator<Integer> it2 = sensiYencol.iterator();
    while (it2.hasNext()) {
      int actual = it2.next();
      if (vectorPrioridad.getEntry(actual, 0) < mayor) {
        it2.remove();
      }
    }
    Random rand = new Random();
    int index = sensiYencol.get(rand.nextInt(sensiYencol.size()));
    senializadas.set(index, true);
    return index;
  }

  /**
   * Este metodo se fija si existen transiciones senializadas, y en caso positivo,
   * devuelve true si el indice es la transicion senializada, o false en caso
   * contrario. Si no existen transiciones senializadas, devuelve true.
   * 
   * @param indice representa el indice de la transicion sobre la que trabaja la
   *               funcion.
   * @return true en caso de que la transicion este senializada, o no existan
   *         transiciones senializadas, o false si la transicion del indice, no
   *         coincide con la transicion senializada.
   */
  protected boolean senializacion(int indice) {
    for (int i = 0; i < senializadas.size(); i++) {
      if (senializadas.get(i)) {
        if (indice == i) {
          return true;
        } else
          return false;
      }
    }
    return true;
  }

  /**
   * Seter, que pone el vector de senializacion en false, para que se deje pasar a
   * la siguiente transicion que intente entrar en el monitor. Fue creada
   * exclusivamente por la existencia de las transiciones temporizadas.
   */
  protected void reseteo() {
    for (int i = 0; i < senializadas.size(); i++) {
      senializadas.set(i, false);
    }
  }

  /**
   * Setea la senializacion de esta transicion en false, lo que permite que otras
   * transiciones puedan tomar el monitor.
   * 
   * @param indice representa a la transicion.
   */
  private void setSenializacionFalse(int indice) {
    senializadas.set(indice, false);
  }
}
