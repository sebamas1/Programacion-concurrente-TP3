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
   * Este metodo se fija si existen transiciones señalizadas, y en caso positivo,
   * devuelve true si el indice es la transicion señalizada, o false en caso
   * contrario. Si no existen transiciones señalizadas, devuelve true.
   * 
   * @param indice representa el indice de la transicion sobre la que trabaja la
   *               funcion.
   * @return true en caso de que la transicion este señalizada, o no existan
   *         transiciones señalizadas, o false si la transicion del indice, no
   *         coincide con la transicion señalizada.
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
   * Seter, que pone el vector de señalizacion en false, para que se deje pasar a
   * la siguiente transicion que intente entrar en el monitor. Fue creada
   * exclusivamente por la existencia de las transiciones temporizadas.
   */
  protected void reseteo() {
    for (int i = 0; i < senializadas.size(); i++) {
      senializadas.set(i, false);
    }
  }

  /**
   * Setea la señalizacion de esta transicion en false, lo que permite que otras
   * transiciones puedan tomar el monitor. Es protegida, porque aunque se usa casi
   * exclusivamente de forma interna, las transiciones temporizadas deben poder
   * "soltar" el monitor.
   * 
   * @param indice representa a la transicion.
   */
  protected void setSenializacionFalse(int indice) {
    senializadas.set(indice, false);
  }
}
