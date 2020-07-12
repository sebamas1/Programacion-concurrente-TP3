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

	public void dispararTransicion(int indice) {
		lock.lock();

		while (!RdP.sensibilizadoTransicion(indice) || !politica.señalizacion(indice)) {
			encolados.set(indice, true);
			try {
				condiciones.get(indice).await();
			} catch (InterruptedException e) {
				System.out.println(
						Thread.currentThread().getName() + " interrumpido en la condicion de transicion " + indice);
				return;
			}
		}
		encolados.set(indice, false); 
		while (!RdP.disparar(indice)) {
			politica.setSeñalizacionFalse(indice); 
			long sleep = RdP.sleepTime(indice); 
			lock.unlock();
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException | IllegalArgumentException ex) {
				System.out.println("Superado el BETA! Soy " + indice);
				return;
			}
			lock.lock();
			politica.reseteo(); 
		}
		log.actualizarLog(indice, RdP.getMatriz()); 
		Iterator<Integer> it = RdP.habilitacion().iterator(); 
		ArrayList<Integer> sensiYencol = new ArrayList<Integer>();
		while (it.hasNext()) {
			int aux = it.next();
			if (encolados.get(aux)) {
				sensiYencol.add(aux);
			}
		}
		try {
			condiciones.get(politica.despertar(sensiYencol, RdP.getMatriz(), indice)).signal();
		} catch (NullPointerException e) {
		}
		lock.unlock();
	}
}
