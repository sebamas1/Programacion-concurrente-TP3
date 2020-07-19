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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Log {
  private String histInvT = "";
  private List<Integer> invariantesT = new ArrayList<Integer>();
  private final static Logger logger = Logger.getLogger("log.txt");
  private RealMatrix mDReal;
  private RealMatrix mOReal;

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

  public void actualizarLog(int indice, RealMatrix mAReal) {
    mOReal = mAReal;
    checkPInvariants(mAReal);
    logger.info("" + indice);
    if (this.invariantesT.contains(indice)) {
      this.histInvT += " " + Integer.toString(indice);
    }
  }

  private void checkPInvariants(RealMatrix mAReal) {
    if ((mAReal.getEntry(15, 0) + mAReal.getEntry(7, 0)) != 1) {
      logger.warning("No se cumple el invariante de las tasks.");
    }
    if ((mAReal.getEntry(13, 0) + mAReal.getEntry(11, 0) + mAReal.getEntry(1, 0)) != 1) {
      logger.warning("No se cumple el invariante del encendido 1.");
    }
    if ((mAReal.getEntry(8, 0) + mAReal.getEntry(9, 0)) != 1) {
      logger.warning("No se cumple el invariante del proceso 1.");
    }
    if ((mAReal.getEntry(14, 0) + mAReal.getEntry(12, 0) + mAReal.getEntry(3, 0)) != 1) {
      logger.warning("No se cumple el invariante del encendido 2.");
    }
    if ((mAReal.getEntry(4, 0) + mAReal.getEntry(5, 0)) != 1) {
      logger.warning("No se cumple el invariante del proceso 2.");
    }
  }

  public void leerResultados() throws FileNotFoundException {
    boolean iguales = true;
    for (int i = 0; i < 16; i++) {
      if (mDReal.getEntry(i, 0) != mOReal.getEntry(i, 0)) {
        logger.warning("Matrices observada y debida no es igual en el elemento " + i);
        logger.info(mOReal.toString());
        logger.info(mDReal.toString());
        iguales = false;
        break;
      }
    }
    if (iguales) {
      logger.info("Todo bien, matrices iguales.");
    }
    histInvT += " ";
    if (this.checkInvT(histInvT)) {
      logger.info("Todo en orden invT");
    } else {
      logger.info("No se cumplen InvT");
    }
  }

  private boolean checkInvT(String input) {
    String resultado = histInvT;
    // Invariante 12-13
    Pattern invT1 = Pattern.compile("\\b12\\b((\\s|.)*?)\\b13\\b");
    Matcher regexMatcher = invT1.matcher(resultado);
    resultado = regexMatcher.replaceAll("$1");

    // //Invariante 7-8
    invT1 = Pattern.compile("\\b14\\b((\\s|.)*?)\\b4\\b((\\s|.)*?)\\b2\\b");
    regexMatcher = invT1.matcher(resultado);
    resultado = regexMatcher.replaceAll("$1$3");

    invT1 = Pattern.compile("\\b7\\b((\\s|.)*?)\\b8\\b");
    regexMatcher = invT1.matcher(resultado);
    resultado = regexMatcher.replaceAll("$1");

    invT1 = Pattern.compile("\\b5\\b((\\s|.)*?)\\b3\\b((\\s|.)*?)\\b1\\b");
    regexMatcher = invT1.matcher(resultado);
    resultado = regexMatcher.replaceAll("$1$3");

    invT1 = Pattern.compile("[^\\s]+");
    regexMatcher = invT1.matcher(resultado);
    return !regexMatcher.find();
  }
}