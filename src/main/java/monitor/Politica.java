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
	private final ArrayList<Boolean> se�alizadas;

	public Politica(int transiciones) {
		vectorPrioridad = new Array2DRowRealMatrix();
		this.transiciones = transiciones;
		se�alizadas = new ArrayList<Boolean>();
		for (int i = 0; i < transiciones; i++) {
			se�alizadas.add(false);
		}
		try {
			leerMatriz();
		} catch (FileNotFoundException e) {
		}

	}
	protected void setSe�alizacionFalse(int indice) {
		se�alizadas.set(indice, false);
	}
	protected Integer despertar(ArrayList<Integer> sensiYencol, RealMatrix mAReal, int transicion) {
		setSe�alizacionFalse(transicion);
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
		while(it2.hasNext()) {
			int actual = it2.next();
			if(vectorPrioridad.getEntry(actual, 0) < mayor) {
				it2.remove();
			}
		}
		Random rand = new Random();
		int index = sensiYencol.get(rand.nextInt(sensiYencol.size()));
		se�alizadas.set(index, true); 
		return index; 
	}

	protected boolean se�alizacion(int indice) { 
		for (int i = 0; i < se�alizadas.size(); i++) {
			if (se�alizadas.get(i)) {
				if (indice == i) {
					return true;
				} else
					return false;
			}
		}
		return true;
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
	protected void reseteo() { 
		for(int i = 0; i < se�alizadas.size(); i++) {
			se�alizadas.set(i, false);
		}
	}
}
