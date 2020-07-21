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
  private RealMatrix marcado;
  private final long tiempoGracia = 10;
  private RealMatrix alfas;

  /**
   * Integration test, donde se testea el metodo dispararTransicion(indice) de
   * Monitor. Crea un thread para casi todas las transiciones existentes y luego
   * ve que los estados de los threads sean los correctos, y que los threads que
   * quedan encolados sean los correctos. Esto esta verificando, que cuando llega
   * el arrival rate, notifica y señaliza correctamente a los threads que tiene
   * que notificar, y el monitor los deja hacer el trabajo que tienen que hacer.
   * 
   * NOTA: este test podria llegar a fallar para distintas PC, el tiempo de gracia
   * debe ser regulado en funcion de eso. Ademas, esos 10 estan pensandos para un
   * alfa maximo de 20ms, si se incrementa, se debe ir aumentando el valor hasta
   * el test pase. Por lo general, con un tiempoGracia = alfa/2 supongo que
   * deberia alcanzar.
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

      actualizarRDP(refleccion, monitor);

      for (int i = 0; i < cantTransiciones; i++) {
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
      Thread disparar9;
      Thread disparar10;
      Thread disparar11 = new Thread(disparador11);
      Thread disparar12 = new Thread(disparador12);
      Thread disparar13 = new Thread(disparador13);
      Thread disparar14 = new Thread(disparador14); // creo muchos threads cada uno dispara una transicion distinta

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
      disparar14.start(); // mando a hacer start a todos menos al unico que puede hacer algo

      Thread.sleep(tiempoGracia);

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
      assertEquals("WAITING", disparar14.getState().toString()); // verifico que el monitor mande a esperar a todo el
                                                                 // mundo

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
      assertTrue(objetoEncolados.get(14)); // me fijo que esten todos encolados(excluidos JVM que los exclui por
                                           // facilidad
      disparar0.start(); // mando el arrival rate

      Thread.sleep(tiempoGracia * 10);

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
      assertTrue(!objetoEncolados.get(11)); // me fijo que sigan encolados las transiciones correspondientes al core 2
      assertTrue(!objetoEncolados.get(12)); // porque por prioridad, seguro se ejecuta todo lo del core 1
      assertTrue(!objetoEncolados.get(13));
      assertTrue(objetoEncolados.get(14)); // con todo esto veo que el sistema de señalizacion funciona bien

      for (int i = 0; i < cantTransiciones; i++) {
        assertTrue(!objetoSenializadas.get(i));
      }

      disparar0 = new Thread(disparador0);
      disparar0.start(); // mando otro producto, que seguro se ejecuta en la parte del core 2
      Thread.sleep(tiempoGracia * 10);

      assertTrue(!objetoEncolados.get(0));
      assertTrue(!objetoEncolados.get(1));
      assertTrue(!objetoEncolados.get(2));
      assertTrue(!objetoEncolados.get(3));
      assertTrue(!objetoEncolados.get(4));
      assertTrue(!objetoEncolados.get(5));
      assertTrue(!objetoEncolados.get(6));
      assertTrue(!objetoEncolados.get(7));
      assertTrue(!objetoEncolados.get(8));
      assertTrue(!objetoEncolados.get(9));
      assertTrue(!objetoEncolados.get(10));
      assertTrue(!objetoEncolados.get(11));
      assertTrue(!objetoEncolados.get(12));
      assertTrue(!objetoEncolados.get(13));// me fijo que nadie este encolado
      assertTrue(!objetoEncolados.get(14)); // con esto vuelvo a verificar que el sistema de señalizado funciona bien

      // a partir de aca, envio 2 productos a cada buffer
      disparar6 = new Thread(disparador6);
      disparar11 = new Thread(disparador11);
      disparar6.start();
      disparar11.start();

      disparar0 = new Thread(disparador0);
      disparar0.start();
      Thread.sleep(tiempoGracia);

      disparar0 = new Thread(disparador0);
      disparar0.start();
      Thread.sleep(tiempoGracia);

      actualizarRDP(refleccion, monitor);
      assertTrue(marcado.getEntry(0, 0) == 1); // se reparten equitativamente
      assertTrue(marcado.getEntry(2, 0) == 1);

      disparar6 = new Thread(disparador6);
      disparar11 = new Thread(disparador11);
      disparar6.start();
      disparar11.start();
      Thread.sleep(tiempoGracia);
      disparar0 = new Thread(disparador0);
      disparar0.start();
      Thread.sleep(tiempoGracia);
      disparar0 = new Thread(disparador0);
      disparar0.start();
      Thread.sleep(tiempoGracia);

      actualizarRDP(refleccion, monitor);
      assertTrue(marcado.getEntry(0, 0) == 2); // se reparten equitativamente
      assertTrue(marcado.getEntry(2, 0) == 2);

      assertEquals("TERMINATED", disparar0.getState().toString());
      assertEquals("TERMINATED", disparar1.getState().toString());
      assertEquals("TERMINATED", disparar2.getState().toString());
      assertEquals("TERMINATED", disparar3.getState().toString());
      assertEquals("TERMINATED", disparar4.getState().toString());
      assertEquals("TERMINATED", disparar5.getState().toString());
      assertEquals("TERMINATED", disparar6.getState().toString());
      assertEquals("TERMINATED", disparar7.getState().toString());
      assertEquals("TERMINATED", disparar8.getState().toString());
      assertEquals("TERMINATED", disparar11.getState().toString());
      assertEquals("TERMINATED", disparar12.getState().toString());
      assertEquals("TERMINATED", disparar13.getState().toString());
      assertEquals("TERMINATED", disparar14.getState().toString()); // me fijo que no haya ningun thread haciendo nada
                                                                    // raro

      disparar1 = new Thread(disparador1);
      disparar10 = new Thread(disparador10);
      disparar3 = new Thread(disparador3);
      disparar12 = new Thread(disparador12);
      disparar7 = new Thread(disparador7); // inicializo primero todos los threads que no pueden hacer nada
      disparar2 = new Thread(disparador2);
      disparar4 = new Thread(disparador4);
      disparar9 = new Thread(disparador9);

      disparar8 = new Thread(disparador8);
      disparar13 = new Thread(disparador13);

      disparar1.start();
      disparar10.start();
      disparar3.start();
      disparar12.start();
      disparar7.start();
      disparar2.start();
      disparar4.start();
      disparar9.start();
      Thread.sleep(tiempoGracia);

      assertEquals("WAITING", disparar1.getState().toString());
      assertEquals("WAITING", disparar10.getState().toString());
      assertEquals("WAITING", disparar3.getState().toString());
      assertEquals("WAITING", disparar12.getState().toString());
      assertEquals("WAITING", disparar7.getState().toString());
      assertEquals("WAITING", disparar2.getState().toString());
      assertEquals("WAITING", disparar4.getState().toString());
      assertEquals("WAITING", disparar9.getState().toString()); // me fijo que ningun thread este haciendo nada raro

      disparar8.start();
      disparar13.start();

      Thread.sleep(tiempoGracia);

      assertEquals("WAITING", disparar8.getState().toString());
      assertEquals("WAITING", disparar13.getState().toString());

      disparar5 = new Thread(disparador5);
      disparar14 = new Thread(disparador14);

      disparar5.start();
      disparar14.start(); // mando la transicion de encendido del core

      Thread.sleep(tiempoGracia * 10);

      assertEquals("TERMINATED", disparar5.getState().toString());
      assertEquals("TERMINATED", disparar3.getState().toString());
      assertEquals("TERMINATED", disparar10.getState().toString());
      assertEquals("TERMINATED", disparar12.getState().toString());
      assertEquals("TERMINATED", disparar13.getState().toString());
      assertEquals("TERMINATED", disparar7.getState().toString());
      assertEquals("TERMINATED", disparar8.getState().toString());
      assertEquals("TERMINATED", disparar14.getState().toString()); // me fijo que todos los threads esten como deben
      assertEquals("TERMINATED", disparar4.getState().toString()); // estar, esto me asegura que el sistema de
      assertEquals("TERMINATED", disparar9.getState().toString()); // señalizacion esta andando bien
      assertEquals("TERMINATED", disparar10.getState().toString());
      assertEquals("WAITING", disparar1.getState().toString());// estos dos quedan prendidos porque quedan productos en
      assertEquals("WAITING", disparar2.getState().toString()); // el buffer

      assertTrue(((disparador13.getFinal() - disparador12.getInicio()) / 1000000) >= alfas.getEntry(0, 8));
      // esto ultimo se fija que el tiempo de espera para la transicion sea el que se
      // debe
      // es de 40ms para un service rate de 20ms, pero si restas los tiempos de
      // gracia, mas o menos queda bien
      assertTrue(((disparador8.getFinal() - disparador7.getInicio()) / 1000000) >= alfas.getEntry(0, 13));

      disparar12 = new Thread(disparador12);
      disparar13 = new Thread(disparador13);
      disparar7 = new Thread(disparador7);
      disparar8 = new Thread(disparador8);

      disparar12.start();
      disparar13.start();
      disparar7.start();
      disparar8.start();

      Thread.sleep(tiempoGracia * 10);

      assertEquals("TERMINATED", disparar5.getState().toString());
      assertEquals("TERMINATED", disparar3.getState().toString());
      assertEquals("TERMINATED", disparar10.getState().toString());
      assertEquals("TERMINATED", disparar12.getState().toString());
      assertEquals("TERMINATED", disparar13.getState().toString());
      assertEquals("TERMINATED", disparar7.getState().toString());
      assertEquals("TERMINATED", disparar8.getState().toString());
      assertEquals("TERMINATED", disparar14.getState().toString());
      assertEquals("TERMINATED", disparar4.getState().toString());
      assertEquals("TERMINATED", disparar9.getState().toString());
      assertEquals("TERMINATED", disparar10.getState().toString());// le mande 2 ciclos de procesamiento de datos mas
      assertEquals("TERMINATED", disparar1.getState().toString());// y como no quedan productos, se apagan los dos
      assertEquals("TERMINATED", disparar2.getState().toString());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void actualizarRDP(Class<?> refleccion, Monitor monitor) {
    try {
      Field rdp = refleccion.getDeclaredField("RdP");
      rdp.setAccessible(true);
      RedDePetri objetoRDP = (RedDePetri) rdp.get(monitor);
      Method getMatriz = objetoRDP.getClass().getDeclaredMethod("getMatriz");
      getMatriz.setAccessible(true);
      marcado = (RealMatrix) getMatriz.invoke(objetoRDP);
      if (alfas == null) {
        Class<?> petriReflejada = objetoRDP.getClass();
        Field alfa = petriReflejada.getDeclaredField("alfaReal");
        alfa.setAccessible(true);
        alfas = (RealMatrix) alfa.get(objetoRDP);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private class Disparador implements Runnable {
    private final int indice;
    private final Method dispararTransicion;
    private final Monitor monitor;
    private long inicio;
    private long finall;

    public Disparador(int indice, Method dispararTransicion, Monitor monitor) {
      this.indice = indice;
      this.dispararTransicion = dispararTransicion;
      this.monitor = monitor;
    }

    public void run() {
      try {
        inicio = System.nanoTime();
        dispararTransicion.invoke(monitor, indice);
        finall = System.nanoTime();
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    private long getInicio() {
      return inicio;
    }

    private long getFinal() {
      return finall;
    }
  }
}
