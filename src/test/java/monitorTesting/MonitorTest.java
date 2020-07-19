package monitorTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

import logger.Log;
import monitor.Monitor;
import monitor.Politica;
import monitor.RedDePetri;

class MonitorTest {

  /**
   * Integration test, donde se testea el metodo dispararTransicion(indice) de Monitor.
   */
  @SuppressWarnings("unchecked")
  @Test
  void dispararTransicionTest() {
    Monitor monitor = new Monitor(new Log());
    Class<?> refleccion = monitor.getClass();
    try {
     Method dispararTransicion = refleccion.getDeclaredMethod("dispararTransicion", new Class[] { int.class });
     Field politica = refleccion.getDeclaredField("politica");
     politica.setAccessible(true);
     Politica objetoPolitica = (Politica) politica.get(monitor);
     
     Class<?> politicaReflejada = objetoPolitica.getClass();
     Field vectorPrioridad = objetoPolitica.getClass().getDeclaredField("vectorPrioridad");
     vectorPrioridad.setAccessible(true);
     RealMatrix objetoVectorPrioridad = (RealMatrix) vectorPrioridad.get(objetoPolitica);
     
     Field transiciones = politicaReflejada.getDeclaredField("transiciones");
     transiciones.setAccessible(true);
     int cantTransiciones = transiciones.getInt(objetoPolitica);
     
     Field encolados = refleccion.getDeclaredField("encolados");
     encolados.setAccessible(true);
     ArrayList<Boolean> objetoEncolados = (ArrayList<Boolean>) encolados.get(monitor);
     
     for(int i = 0; i < cantTransiciones; i++) {
       objetoVectorPrioridad.setEntry(i, 0, i);
     }
     
     Disparador disparador0 = new Disparador(0, dispararTransicion, monitor);
     Disparador disparador1 = new Disparador(1, dispararTransicion, monitor);
     Disparador disparador2 = new Disparador(2, dispararTransicion, monitor);
     Disparador disparador3 = new Disparador(3, dispararTransicion, monitor);
     Disparador disparador4 = new Disparador(4, dispararTransicion, monitor);
     Disparador disparador5 = new Disparador(5, dispararTransicion, monitor);
     Disparador disparador6 = new Disparador(6, dispararTransicion, monitor);
     Disparador disparador7 = new Disparador(7, dispararTransicion, monitor);
     Disparador disparador8 = new Disparador(8, dispararTransicion, monitor);
     Disparador disparador9 = new Disparador(9, dispararTransicion, monitor);
     Disparador disparador10 = new Disparador(10, dispararTransicion, monitor);
     Disparador disparador11 = new Disparador(11, dispararTransicion, monitor);
     Disparador disparador12 = new Disparador(12, dispararTransicion, monitor);
     Disparador disparador13 = new Disparador(13, dispararTransicion, monitor);
     Disparador disparador14 = new Disparador(14, dispararTransicion, monitor);
     
     Thread disparar0 = new Thread(disparador0);
     Thread disparar1 = new Thread(disparador1);
     Thread disparar2 = new Thread(disparador2);
     Thread disparar3 = new Thread(disparador3);
     Thread disparar4 = new Thread(disparador4);
     Thread disparar5 = new Thread(disparador5);
     Thread disparar6 = new Thread(disparador6);
     Thread disparar7 = new Thread(disparador7);
     Thread disparar8 = new Thread(disparador8);
     Thread disparar9 = new Thread(disparador9);
     Thread disparar10 = new Thread(disparador10);
     Thread disparar11 = new Thread(disparador11);
     Thread disparar12 = new Thread(disparador12);
     Thread disparar13 = new Thread(disparador13);
     Thread disparar14 = new Thread(disparador14);
     
     disparar14.start();
     disparar13.start();
     disparar12.start();
     disparar11.start();
     disparar10.start();
     disparar9.start();
     disparar8.start();
     disparar7.start();
     disparar6.start();
     disparar5.start();
     disparar4.start();
     disparar3.start();
     disparar2.start();
     disparar1.start();
     
     Thread.sleep(1000);
     
     assertEquals(disparar1.getState().toString(), "WAITING");
     assertEquals(disparar2.getState().toString(), "WAITING");
     assertEquals(disparar3.getState().toString(), "WAITING");
     assertEquals(disparar4.getState().toString(), "WAITING");
     assertEquals(disparar5.getState().toString(), "WAITING");
     assertEquals(disparar6.getState().toString(), "WAITING");
     assertEquals(disparar7.getState().toString(), "WAITING");
     assertEquals(disparar8.getState().toString(), "WAITING");
     assertEquals(disparar9.getState().toString(), "WAITING");
     assertEquals(disparar10.getState().toString(), "WAITING");
     assertEquals(disparar11.getState().toString(), "WAITING");
     assertEquals(disparar12.getState().toString(), "WAITING");
     assertEquals(disparar13.getState().toString(), "WAITING");
     assertEquals(disparar14.getState().toString(), "WAITING");
     assertEquals(disparar0.getState().toString(), "NEW");
     
     assertTrue(objetoEncolados.get(1));
     assertTrue(objetoEncolados.get(2));
     assertTrue(objetoEncolados.get(3));
     assertTrue(objetoEncolados.get(4));
     assertTrue(objetoEncolados.get(5));
     assertTrue(objetoEncolados.get(6));
     assertTrue(objetoEncolados.get(7));
     assertTrue(objetoEncolados.get(8));
     assertTrue(objetoEncolados.get(9));
     assertTrue(objetoEncolados.get(10));
     assertTrue(objetoEncolados.get(11));
     assertTrue(objetoEncolados.get(12));
     assertTrue(objetoEncolados.get(13));
     assertTrue(objetoEncolados.get(14));
    
     disparar0.start();
     
     Thread.sleep(10);
     
     assertEquals(disparar0.getState().toString(), "TERMINATED");
     assertEquals(disparar11.getState().toString(), "TERMINATED");
     assertEquals(disparar5.getState().toString(), "TERMINATED");
     assertEquals(disparar3.getState().toString(), "TERMINATED");
     assertEquals(disparar12.getState().toString(), "TERMINATED");
     assertEquals(disparar13.getState().toString(), "TIMED_WAITING");
     assertEquals(disparar1.getState().toString(), "WAITING");
     
     assertTrue(!objetoEncolados.get(0));
     assertTrue(objetoEncolados.get(1));
     assertTrue(objetoEncolados.get(2));
     assertTrue(!objetoEncolados.get(3));
     assertTrue(objetoEncolados.get(4));
     assertTrue(!objetoEncolados.get(5));
     assertTrue(objetoEncolados.get(6));
     assertTrue(objetoEncolados.get(7));
     assertTrue(objetoEncolados.get(8));
     assertTrue(objetoEncolados.get(9));
     assertTrue(objetoEncolados.get(10));
     assertTrue(!objetoEncolados.get(11));
     assertTrue(!objetoEncolados.get(12));
     assertTrue(!objetoEncolados.get(13));
     assertTrue(objetoEncolados.get(14));
     
     Thread.sleep(30);
     
     assertEquals(disparar13.getState().toString(), "TERMINATED");
     assertEquals(disparar1.getState().toString(), "TERMINATED");
     assertEquals(disparar2.getState().toString(), "WAITING");
     assertEquals(disparar4.getState().toString(), "WAITING");
     assertEquals(disparar6.getState().toString(), "WAITING");
     assertEquals(disparar7.getState().toString(), "WAITING");
     assertEquals(disparar8.getState().toString(), "WAITING");
     assertEquals(disparar9.getState().toString(), "WAITING");
     assertEquals(disparar10.getState().toString(), "WAITING");
     assertEquals(disparar14.getState().toString(), "WAITING");
     
     disparar0 = new Thread(disparador0);
     disparar0.start();
     Thread.sleep(40);
     
     assertEquals(disparar6.getState().toString(), "TERMINATED");
     assertEquals(disparar14.getState().toString(), "TERMINATED");
     assertEquals(disparar4.getState().toString(), "TERMINATED");
     assertEquals(disparar7.getState().toString(), "TERMINATED");
     assertEquals(disparar8.getState().toString(), "TERMINATED");
     assertEquals(disparar2.getState().toString(), "TERMINATED");
     assertEquals(disparar9.getState().toString(), "WAITING");
     assertEquals(disparar10.getState().toString(), "WAITING");
     
     disparar0 = new Thread(disparador0);
     disparar1 = new Thread(disparador1);
     disparar2 = new Thread(disparador2);
     disparar3 = new Thread(disparador3);
     disparar4 = new Thread(disparador4);
     disparar5 = new Thread(disparador5);
     disparar6 = new Thread(disparador6);
     disparar7 = new Thread(disparador7);
     disparar8 = new Thread(disparador8);
     disparar9 = new Thread(disparador9);
     disparar10 = new Thread(disparador10);
     disparar11 = new Thread(disparador11);
     disparar12 = new Thread(disparador12);
     disparar13 = new Thread(disparador13);
     disparar14 = new Thread(disparador14);
    } catch (NoSuchMethodException | SecurityException | InterruptedException e) {
      e.printStackTrace();
    } catch(NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
  private class Disparador implements Runnable{
    private final int indice;
    private final Method dispararTransicion;
    private final Monitor monitor;
    public Disparador(int indice, Method dispararTransicion, Monitor monitor) {
      this.indice = indice;
      this.dispararTransicion = dispararTransicion;
      this.monitor = monitor;
    }
    public void run() {
      try {
        dispararTransicion.invoke(monitor, indice);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
