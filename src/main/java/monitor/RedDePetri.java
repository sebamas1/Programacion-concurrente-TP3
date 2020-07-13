package monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

public class RedDePetri {
  private RealMatrix incidenciaReal;
  private RealMatrix incidenciaRealNegativa;
  private RealMatrix incidenciaRealPositiva;
  private RealMatrix mAReal;
  private RealMatrix mVReal;
  private RealMatrix m0Real;
  private RealMatrix inhibicionReal;
  private RealMatrix alfaReal;
  private RealMatrix betaReal;
  private final ArrayList<Long> timeStamp;
  private int columnas;
  private int filas;

  public RedDePetri() {
    try {
      leerIncidenciaPositiva();
      leerIncidenciaNegativa();
      leerInhibicion();
      leerInicial();
      leerAlfa();
      leerBeta();
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro el archivo para leer la matriz.");
    }
    mAReal = new Array2DRowRealMatrix();
    mAReal = m0Real;
    incidenciaReal = incidenciaRealPositiva.subtract(incidenciaRealNegativa);
    timeStamp = new ArrayList<Long>();
    for (int i = 0; i < columnas; i++) {
      timeStamp.add((long) 0);
    }
    double[][] seteo0 = new double[filas][1];
    for (int i = 0; i < filas; i++) {
      seteo0[i][0] = 0;
    }
    mVReal = new Array2DRowRealMatrix(seteo0);
    setTimeStamp();

  }

  private void leerIncidenciaPositiva() throws FileNotFoundException {
    boolean leido = false;
    Scanner input = null;
    input = new Scanner(new File("src/main/java/incidenciaPositiva.txt"));
    int rows = 0;
    int columns = 0;
    while (input.hasNextLine()) {
      ++rows;
      Scanner colReader = new Scanner(input.nextLine());
      while (colReader.hasNextInt() && !leido) {
        ++columns;
        colReader.nextInt();
      }
      leido = true;
      colReader.close();
    }
    double[][] incidenciaPositiva = new double[rows][columns];
    input.close();
    input = new Scanner(new File("src/main/java/incidenciaPositiva.txt"));
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < columns; ++j) {
        if (input.hasNextInt()) {
          incidenciaPositiva[i][j] = input.nextInt();
        }
      }
    }
    incidenciaRealPositiva = new Array2DRowRealMatrix(incidenciaPositiva);
    columnas = columns;
    filas = rows;
    input.close();
  }

  private void leerIncidenciaNegativa() throws FileNotFoundException {
    boolean leido = false;
    Scanner input = null;
    input = new Scanner(new File("src/main/java/incidenciaNegativa.txt"));
    int rows = 0;
    int columns = 0;
    while (input.hasNextLine()) {
      ++rows;
      Scanner colReader = new Scanner(input.nextLine());
      while (colReader.hasNextInt() && !leido) {
        ++columns;
        colReader.nextInt();
      }
      leido = true;
      colReader.close();
    }
    double[][] incidenciaNegativa = new double[rows][columns];
    input.close();
    input = new Scanner(new File("src/main/java/incidenciaNegativa.txt"));
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < columns; ++j) {
        if (input.hasNextInt()) {
          incidenciaNegativa[i][j] = input.nextInt();
        }
      }
    }
    incidenciaRealNegativa = new Array2DRowRealMatrix(incidenciaNegativa);
    input.close();
  }

  private void leerInicial() throws FileNotFoundException {
    Scanner input = null;
    input = new Scanner(new File("src/main/java/inicial.txt"));
    double[][] m0 = new double[filas][1];
    for (int i = 0; input.hasNextLine(); i++) {
      m0[i][0] = input.nextInt();
    }
    m0Real = new Array2DRowRealMatrix(m0);
    input.close();
  }

  private void leerAlfa() throws FileNotFoundException {
    Scanner input = null;
    input = new Scanner(new File("src/main/java/alfa.txt"));
    double[][] m0 = new double[1][columnas];
    for (int i = 0; input.hasNextLine(); i++) {
      m0[0][i] = input.nextInt();
    }
    alfaReal = new Array2DRowRealMatrix(m0);
    input.close();
  }

  private void leerBeta() throws FileNotFoundException {
    Scanner input = null;
    input = new Scanner(new File("src/main/java/beta.txt"));
    double[][] m0 = new double[1][columnas];
    for (int i = 0; input.hasNextLine(); i++) {
      m0[0][i] = input.nextInt();
    }
    betaReal = new Array2DRowRealMatrix(m0);
    input.close();
  }

  private void leerInhibicion() throws FileNotFoundException {
    boolean leido = false;
    Scanner input = null;
    input = new Scanner(new File("src/main/java/inhibicion.txt"));
    int rows = 0;
    int columns = 0;
    while (input.hasNextLine()) {
      ++rows;
      Scanner colReader = new Scanner(input.nextLine());
      while (colReader.hasNextInt() && !leido) {
        ++columns;
        colReader.nextInt();
      }
      leido = true;
      colReader.close();
    }
    double[][] inhibicion = new double[rows][columns];
    input.close();
    input = new Scanner(new File("src/main/java/inhibicion.txt"));
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < columns; ++j) {
        if (input.hasNextInt()) {
          inhibicion[i][j] = input.nextInt();
        }
      }
    }
    inhibicionReal = new Array2DRowRealMatrix(inhibicion);
    input.close();
  }

  /**
   * Se fija si la transicion esta dentro de la ventana de tiempo, en cuyo caso dispara la transicion, y en caso
   * de que no este en la ventana de tiempo, retorna false y termina la ejecucion.
   * Ademas, si el disparo es exitoso, actualiza el tiempo de sensibilizado de cada transicion.
   * 
   * @param indice Representa el indice de la transicion que se desea disparar
   * 
   * @return true en caso de que la transicion este dentro de la ventana de tiempo
   * y retorna false si la transicion esta fuera de la ventana de tiempo.
   */
  protected boolean disparar(int indice) {
    if (((alfaReal.getEntry(0, indice) > 0)
        && (System.currentTimeMillis() - timeStamp.get(indice) < (alfaReal.getEntry(0, indice)))))
      return false;
    if (((betaReal.getEntry(0, indice) > 0)
        && (System.currentTimeMillis() - timeStamp.get(indice) > betaReal.getEntry(0, indice))))
      return false;
    double[][] disparo = new double[columnas][1];
    for (int i = 0; i < columnas; i++) {
      if (i != indice) {
        disparo[i][0] = 0;
      } else
        disparo[i][0] = 1;
    }
    RealMatrix disparoReal = new Array2DRowRealMatrix(disparo);
    mVReal = mAReal;
    mAReal = mAReal.add(incidenciaReal.multiply(disparoReal));
    setTimeStamp();
    return true;
  }

  private boolean sensibilizado(int indice, RealMatrix matriz) {
    if (esInhibidora(indice)) {
      HashSet<Integer> plazasInhibidas = new HashSet<Integer>();
      for (int i = 0; i < filas; i++) {
        if (inhibicionReal.getEntry(i, indice) != 0) {
          plazasInhibidas.add(i);
        }
      }
      Iterator<Integer> it = plazasInhibidas.iterator();
      while (it.hasNext()) {
        if (matriz.getEntry(it.next(), 0) != 0) {
          return false;
        }
      }
      return true;
    } else {
      double[][] disparo = new double[columnas][1];
      for (int i = 0; i < columnas; i++) {
        if (i != indice) {
          disparo[i][0] = 0;
        } else
          disparo[i][0] = 1;
      }
      boolean autoloop = false;
      HashSet<Integer> plazasAutoloop = new HashSet<Integer>();
      RealMatrix disparoReal = new Array2DRowRealMatrix(disparo);
      for (int i = 0; i < filas; i++) {
        if ((incidenciaRealPositiva.getEntry(i, indice) != 0 && incidenciaRealNegativa.getEntry(i, indice) != 0)) {
          autoloop = true;
          plazasAutoloop.add(i);
        }
      }
      RealMatrix aux = new Array2DRowRealMatrix();
      aux = matriz.add(incidenciaReal.multiply(disparoReal));
      if (!autoloop) {
        for (int i = 0; i < filas; i++) {
          if (aux.getEntry(i, 0) < 0) {
            return false;
          }
        }
        return true;
      } else {
        for (int i = 0; i < filas; i++) {
          if ((aux.getEntry(i, 0) < 0)) {
            return false;
          }
        }
        Iterator<Integer> itA = plazasAutoloop.iterator();
        while (itA.hasNext()) {
          int plaza = itA.next();
          if (matriz.getEntry(plaza, 0) < incidenciaRealNegativa.getEntry(plaza, indice)) {
            return false;
          }
        }
        return true;
      }
    }
  }

  private boolean esInhibidora(int indice) {
    boolean inhibidora = false;
    for (int i = 0; i < filas; i++) {
      if (inhibicionReal.getEntry(i, indice) != 0) {
        inhibidora = true;
        return inhibidora;
      }
    }
    return inhibidora;
  }

  private void setTimeStamp() {
    for (int i = 0; i < columnas; i++) {
      if (sensibilizado(i, mAReal) && !sensibilizado(i, mVReal)) {
        timeStamp.set(i, System.currentTimeMillis());
      }
    }
  }

  protected HashSet<Integer> habilitacion() {
    HashSet<Integer> habilitacion = new HashSet<Integer>();
    for (int indice = 0; indice < columnas; indice++) {
      if (sensibilizado(indice, mAReal)) {
        habilitacion.add(indice);
      }
    }
    return habilitacion;
  }

  protected Long sleepTime(int indice) {
    long currentTime = System.currentTimeMillis();
    if ((currentTime - timeStamp.get(indice)) < alfaReal.getEntry(0, indice)) {
      double aux = (alfaReal.getEntry(0, indice) - (currentTime - timeStamp.get(indice)));
      long sleep = (Double.valueOf(aux)).longValue();
      return sleep;
    }
    if ((currentTime - timeStamp.get(indice)) > betaReal.getEntry(0, indice)) {
      return (long) -1;
    }
    return (long) 0;
  }

  protected boolean sensibilizadoTransicion(int indice) {
    return sensibilizado(indice, mAReal);
  }

  protected int getCantTransiciones() {
    return columnas;
  }

  protected RealMatrix getMatriz() {
    return mAReal;
  }

  protected RealMatrix getInicial() {
    return m0Real;
  }
}