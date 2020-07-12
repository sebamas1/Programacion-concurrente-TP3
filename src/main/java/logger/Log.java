package logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Log {
	private final static Logger logger = Logger.getLogger("log.txt");
	private RealMatrix mDReal;
	private RealMatrix mOReal;
	private HashMap<Integer, String> transiciones;

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
		}
		try {
			leerMatriz();
		} catch (FileNotFoundException e) {
		}
		transiciones = new HashMap<Integer, String>();
		transiciones.put(0, "Arrival rate");
		transiciones.put(1, "PowerDownThresHold 1");
		transiciones.put(2, "PowerDownThresHold 2");
		transiciones.put(3, "PowerUpDelay 1");
		transiciones.put(4, "PowerUpDelay 2");
		transiciones.put(5, "T0");
		transiciones.put(6, "T10");
		transiciones.put(7, "T11");
		transiciones.put(8, "T12");
		transiciones.put(9, "T13");
		transiciones.put(10, "T2");
		transiciones.put(11, "T4");
		transiciones.put(12, "T5");
		transiciones.put(13, "T7");
		transiciones.put(14, "T8");
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
		logger.info(transiciones.get(indice));
		// logger.info(""+ indice);
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
		Scanner input = null;
		input = new Scanner(new File("src/main/java/Registro.txt"));
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
		while (input.hasNextLine()) {
			input.nextLine();
		}
		input.close();
	}
}
