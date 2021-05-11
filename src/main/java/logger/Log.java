package logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;


public class Log {
  private List<Integer> invariantesT = new ArrayList<Integer>();
  private final static Logger logger = Logger.getLogger("log.txt");
  private RealMatrix mDReal;
  private RealMatrix mOReal;
  private boolean flagPInvariants = false;

  /**
   * Contruye el Log, leyendo la matriz de marcado esperada. Crea un registro para
   * escribir el log e imprime un mensaje en consola en caso de no poder escribir
   * el log. Inicializa la matriz con las transiciones correspondientes a los T
   * invariants.
   */
  public Log() {
    LogManager.getLogManager().reset();
    logger.setLevel(Level.INFO);
    try {
      FileHandler fh = new FileHandler("src/main/java/Registro.txt");
      fh.setLevel(Level.INFO);
      CustomRecordFormatter formatter = new CustomRecordFormatter();
      fh.setFormatter(formatter);
      logger.addHandler(fh);
    } catch (IOException e) {
      System.out.println("NO SE GUARDO EL LOG");
      e.printStackTrace();
    }
    try {
      leerMatriz();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    invariantesT.add(1);
    invariantesT.add(3);
    invariantesT.add(5);
    invariantesT.add(14);
    invariantesT.add(4);
    invariantesT.add(2);
    invariantesT.add(7);
    invariantesT.add(8);
    invariantesT.add(12);
    invariantesT.add(13);
  }

  /**
   * Lee la matriz que deberia de marcado que deberia presentarse al final de la
   * ejecucion de programa.
   * 
   * @throws FileNotFoundException cuando no se encuentra el .txt.
   */

  private void leerMatriz() throws FileNotFoundException {
    Scanner input = null;
    input = new Scanner(new File("src/main/java/mARealObtener.txt"));
    double[][] m0 = new double[16][1];
    for (int i = 0; input.hasNextLine(); i++) {
      m0[i][0] = input.nextInt();
    }
    mDReal = new Array2DRowRealMatrix(m0);
    input.close();
  }

  /**
   * Lee la red de petri, y checkea que los invariantes de plaza se cumplan. En
   * caso de que no, se guarda un mensaje en el Registro.txt cual invariante no se
   * cumple. Ademas, guarda en el mismo .txt el indice de la transicion que se
   * acaba de disparar.
   * 
   * @param indice es el indice de la transicion que llama a la actualizacion del
   *               log.
   * @param mAReal es el marcado actual de la red de petri.
   */
  public void actualizarLog(int indice, RealMatrix mAReal) {
    mOReal = mAReal;
    checkPInvariants(mAReal);
    logger.info("" + indice);
  }

  /**
   * Checkea los invariantes de plaza para este ejercicio en particular. Si no se
   * cumple, imprime un mensaje en el log y setea la flag para los Pinvariants en
   * true, indicando que en algun lugar no se cumplio un invariante de plaza.
   * 
   * @param mAReal es la matriz de marcado actual de la red de petri.
   */
  private void checkPInvariants(RealMatrix mAReal) {
    if ((mAReal.getEntry(15, 0) + mAReal.getEntry(7, 0)) != 1) {
      logger.warning("No se cumple el invariante de las tasks.");
      flagPInvariants = true;
    }
    if ((mAReal.getEntry(13, 0) + mAReal.getEntry(11, 0) + mAReal.getEntry(1, 0)) != 1) {
      logger.warning("No se cumple el invariante del encendido 1.");
      flagPInvariants = true;
    }
    if ((mAReal.getEntry(8, 0) + mAReal.getEntry(9, 0)) != 1) {
      logger.warning("No se cumple el invariante del proceso 1.");
      flagPInvariants = true;
    }
    if ((mAReal.getEntry(14, 0) + mAReal.getEntry(12, 0) + mAReal.getEntry(3, 0)) != 1) {
      logger.warning("No se cumple el invariante del encendido 2.");
      flagPInvariants = true;
    }
    if ((mAReal.getEntry(4, 0) + mAReal.getEntry(5, 0)) != 1) {
      logger.warning("No se cumple el invariante del proceso 2.");
      flagPInvariants = true;
    }
  }

  /**
   * Se fija que el marcado final sea el esperado. Tanto si es correcto o no,
   * imprime un mensaje indicandolo. Luego, lee los Tinvariants y se fija que se
   * cumplan. Imprime un mensaje en el log informando del exito o fracaso de los
   * invariantes P y T.
   * 
   * @throws FileNotFoundException si no puede escribir el log.
   */
  public void leerResultados() throws FileNotFoundException {
    
    boolean iguales = true;
    for (int i = 0; i < mOReal.getRowDimension(); i++) {
      if (mDReal.getEntry(i, 0) != mOReal.getEntry(i, 0)) {
        logger.warning("\nMatrices observada y esperada no son iguales en el elemento " + i);
        logger.info(mOReal.toString());
        logger.info(mDReal.toString());
        iguales = false;
        break;
      }
    }
    if (iguales) {
      logger.info("\nEl marcado final esperado, coincide con el real.");
    }
    
    if (flagPInvariants) {
      logger.info("\nNo se cumplen los invP.");
    } else {
      logger.info("\nTodo en orden invP.\n");
    } 
  }
}