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
   * Integration test, donde se testea el metodo dispararTransicion(indice) de
   * Monitor. Crea un thread para casi todas las transiciones existentes y luego
   * ve que los estados de los threads sean los correctos, y que los threads que
   * quedan encolados sean los correctos. Esto esta verificando, que cuando llega
   * el arrival rate, notifica y señaliza correctamente a los threads que tiene
   * que notificar, y el monitor los deja hacer el trabajo que tienen que hacer.
   * 
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

      Field senializadas = politicaReflejada.getDeclaredField("senializadas");
      senializadas.setAccessible(true);
      ArrayList<Boolean> objetoSenializadas = (ArrayList<Boolean>) senializadas.get(objetoPolitica);

      Field rdp = refleccion.getDeclaredField("RdP");
      rdp.setAccessible(true);
      RedDePetri objetoRDP = (RedDePetri) rdp.get(monitor);
      Method getMatriz = objetoRDP.getClass().getDeclaredMethod("getMatriz");
      getMatriz.setAccessible(true);

      objetoVectorPrioridad.setEntry(0, 0, cantTransiciones + 1); // maxima prioridad al arrival rate
      for (int i = 1; i < cantTransiciones; i++) {
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
      Thread disparar11 = new Thread(disparador11);
      Thread disparar12 = new Thread(disparador12);
      Thread disparar13 = new Thread(disparador13);
      Thread disparar14 = new Thread(disparador14);

      assertEquals("NEW", disparar0.getState().toString());

      disparar1.start();
      disparar2.start();
      disparar3.start();
      disparar4.start();
      disparar5.start();
      disparar6.start();
      disparar7.start();
      disparar8.start();
      disparar11.start();
      disparar12.start();
      disparar13.start();
      disparar14.start();

      Thread.sleep(10);

      assertEquals("WAITING", disparar1.getState().toString());
      assertEquals("WAITING", disparar2.getState().toString());
      assertEquals("WAITING", disparar3.getState().toString());
      assertEquals("WAITING", disparar4.getState().toString());
      assertEquals("WAITING", disparar5.getState().toString());
      assertEquals("WAITING", disparar6.getState().toString());
      assertEquals("WAITING", disparar7.getState().toString());
      assertEquals("WAITING", disparar8.getState().toString());
      assertEquals("WAITING", disparar11.getState().toString());
      assertEquals("WAITING", disparar12.getState().toString());
      assertEquals("WAITING", disparar13.getState().toString());
      assertEquals("WAITING", disparar14.getState().toString());

      assertTrue(objetoEncolados.get(1));
      assertTrue(objetoEncolados.get(2));
      assertTrue(objetoEncolados.get(3));
      assertTrue(objetoEncolados.get(4));
      assertTrue(objetoEncolados.get(5));
      assertTrue(objetoEncolados.get(6));
      assertTrue(objetoEncolados.get(7));
      assertTrue(objetoEncolados.get(8));
      assertTrue(!objetoEncolados.get(9));
      assertTrue(!objetoEncolados.get(10));
      assertTrue(objetoEncolados.get(11));
      assertTrue(objetoEncolados.get(12));
      assertTrue(objetoEncolados.get(13));
      assertTrue(objetoEncolados.get(14));
      disparar0.start(); // el tiempo que tarda desde que inicia la transicion de indice 12, hasta aca,
                         // mas los 10ms = 20ms
      Thread.sleep(50);
      // si revisas cuanto tiempo pasa hasta que se hace el service rate del core 1,
      // es 40, menos los 20 de antes = 20
      // todo esto tomando alfa = 20

      assertTrue(!objetoEncolados.get(0));
      assertTrue(!objetoEncolados.get(1));
      assertTrue(objetoEncolados.get(2));
      assertTrue(!objetoEncolados.get(3));
      assertTrue(objetoEncolados.get(4));
      assertTrue(!objetoEncolados.get(5));
      assertTrue(objetoEncolados.get(6));
      assertTrue(objetoEncolados.get(7));
      assertTrue(objetoEncolados.get(8));
      assertTrue(!objetoEncolados.get(9));
      assertTrue(!objetoEncolados.get(10));
      assertTrue(!objetoEncolados.get(11));
      assertTrue(!objetoEncolados.get(12));
      assertTrue(!objetoEncolados.get(13));
      assertTrue(objetoEncolados.get(14));

      for (int i = 0; i < cantTransiciones; i++) {
        assertTrue(!objetoSenializadas.get(i));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private class Disparador implements Runnable {
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
