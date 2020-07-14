package monitorTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

import monitor.*;

@SuppressWarnings("rawtypes")
class RedDePetriTest {

  /**
   * Unit test para disparar(). Genera alfas y betas al azar, y se va fijando para
   * cada transicion, que se pueda o no hacer el disparo segun corresponda.
   */
  @Test
  void disparoTest() {
    RedDePetri rdp = new RedDePetri();
    Class<?> refleccion = rdp.getClass();
    Class[] parameterTypes = new Class[] { int.class };
    try {
      Method disparo = refleccion.getDeclaredMethod("disparar", parameterTypes);
      disparo.setAccessible(true);

      Field alfaReal = refleccion.getDeclaredField("alfaReal");
      Field betaReal = refleccion.getDeclaredField("betaReal");
      Field timeStamp = refleccion.getDeclaredField("timeStamp");

      alfaReal.setAccessible(true);
      betaReal.setAccessible(true);
      timeStamp.setAccessible(true);

      Object objetoAlfaReal = alfaReal.get(rdp);
      Method setAlfaReal = objetoAlfaReal.getClass().getDeclaredMethod("setEntry",
          new Class[] { int.class, int.class, double.class });

      Object objetoBetaReal = betaReal.get(rdp);
      Method setBetaReal = objetoBetaReal.getClass().getDeclaredMethod("setEntry",
          new Class[] { int.class, int.class, double.class });

      Object objetoTimeStamp = timeStamp.get(rdp);
      Method setTimeStamp = objetoTimeStamp.getClass().getDeclaredMethod("set",
          new Class[] { int.class, Object.class });
      Method size = objetoTimeStamp.getClass().getDeclaredMethod("size");
      Method getTimeStamp = objetoTimeStamp.getClass().getDeclaredMethod("get", new Class[] { int.class });

      // 20 por la cantidad de pruebas que quiero hacer por cada transicion
      for (int i = 0; i < (int) size.invoke(objetoTimeStamp); i++) {
        for (int j = 0; j < 1000; j++) {
          Long alfa = (ThreadLocalRandom.current().nextLong(10) + 1) * 10;
          Long beta = (ThreadLocalRandom.current().nextLong(100, 1000) + 1);
          setTimeStamp.invoke(objetoTimeStamp, i, (Long) System.currentTimeMillis());
          setAlfaReal.invoke(objetoAlfaReal, 0, i, alfa);
          setBetaReal.invoke(objetoBetaReal, 0, i, beta);
          assertTrue(!(boolean) disparo.invoke(rdp, i));
          Long stampInferior = (Long) getTimeStamp.invoke(objetoTimeStamp, i) - alfa;
          setTimeStamp.invoke(objetoTimeStamp, i, stampInferior);
          assertTrue((boolean) disparo.invoke(rdp, i));
          Long stampSuperior = (Long) getTimeStamp.invoke(objetoTimeStamp, i) + alfa + beta;
          setTimeStamp.invoke(objetoTimeStamp, i, stampSuperior);
          assertTrue(!(boolean) disparo.invoke(rdp, i));
        }
      }
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Unit test de sensibilizado(), y de habilitacion(). Ademas, se podrian tener
   * en cuenta esInhibidora() y sensibilizadoPorInhibicion(), que se usan
   * internamente por sensibilizado(). En este test, se van a agregar manualmente
   * a un ArrayList las transiciones que deberian estar sensibilizadas para un
   * momento dado y luego comprobar que la funcion de sensibilizado se comporta
   * correctamente. Luego se va a cambiar el estado de la red de petri, y se va a
   * intentar de nuevo.
   * 
   * Ademas, se testea la funcion de habilitacion, que esta intimamente ligada a
   * la funcion de sensibilizado.
   */
  @Test
  void sensibilizadoYhabilitacionTest() {
    RedDePetri rdp = new RedDePetri();
    Class<?> refleccion = rdp.getClass();
    ArrayList<Integer> transicionesSensibilizadas = new ArrayList<Integer>();

    try {
      Method sensibilizado = refleccion.getDeclaredMethod("sensibilizadoTransicion", new Class[] { int.class });
      Method cantidadTransiciones = refleccion.getDeclaredMethod("getCantTransiciones");
      Method disparar = refleccion.getDeclaredMethod("disparar", new Class[] { int.class });
      sensibilizado.setAccessible(true);
      cantidadTransiciones.setAccessible(true);
      disparar.setAccessible(true);
      int transiciones = (int) cantidadTransiciones.invoke(rdp);

      // estas lineas son para inicializar las cosas necesarias para testear el metodo
      // habilitacion de la red de petri.
      Method habilitacion = refleccion.getDeclaredMethod("habilitacion");
      habilitacion.setAccessible(true);
      HashSet transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);

      // quiero ignorar la ventana de tiempo, que es testeada en la funcion de disparo
      Field alfa = refleccion.getDeclaredField("alfaReal");
      alfa.setAccessible(true);
      Object objetoAlfaReal = alfa.get(rdp);
      Method setAlfaReal = objetoAlfaReal.getClass().getDeclaredMethod("setEntry",
          new Class[] { int.class, int.class, double.class });
      for (int i = 0; i < transiciones; i++) {
        setAlfaReal.invoke(objetoAlfaReal, 0, i, (double) -1);
      }

      // inicialmente, solo deberia estar sensibilizada la transicion 0(arrival rate)
      // checkeo eso, y luego disparo la transicion 0
      transicionesSensibilizadas.add(0);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      assertTrue(transicionesHabilitadas.size() == 1);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue((boolean) disparar.invoke(rdp, 0));

      // me voy para el buffer del procesador 2(T10)
      transicionesSensibilizadas.add(6);
      transicionesSensibilizadas.add(11);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(6));
      assertTrue(transicionesHabilitadas.contains(11));
      assertTrue((boolean) disparar.invoke(rdp, 6));

      // enciendo el procesador 2 (T8)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 14));

      // powerUpDelay2
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(4);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(4));
      assertTrue((boolean) disparar.invoke(rdp, 4));

      // ejecuto el inicio del proceso de un paquete en el buffer2(T11)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(7);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(7));
      assertTrue((boolean) disparar.invoke(rdp, 7));

      // delay2 de proceso de paquete (T7)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(8);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(8));
      assertTrue((boolean) disparar.invoke(rdp, 8));

      // apagado del procesador 2
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(2);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(2));
      assertTrue((boolean) disparar.invoke(rdp, 2));

      // segunda secuencia

      // ejecuto arrival rate
      transicionesSensibilizadas.add(0);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 1);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue((boolean) disparar.invoke(rdp, 0));

      // voy por el buffer del procesador 1
      transicionesSensibilizadas.add(6);
      transicionesSensibilizadas.add(11);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(6));
      assertTrue(transicionesHabilitadas.contains(11));
      assertTrue((boolean) disparar.invoke(rdp, 11));

      // ejecuto arrival rate
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(5);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue((boolean) disparar.invoke(rdp, 0));

      // voy por el buffer del procesador 1
      transicionesSensibilizadas.add(6);
      transicionesSensibilizadas.add(11);
      transicionesSensibilizadas.add(5);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(6));
      assertTrue(transicionesHabilitadas.contains(11));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue((boolean) disparar.invoke(rdp, 11));

      // ejecuto arrival rate
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(5);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue((boolean) disparar.invoke(rdp, 0));

      // me voy por el buffer del procesador 2
      transicionesSensibilizadas.add(6);
      transicionesSensibilizadas.add(11);
      transicionesSensibilizadas.add(5);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(6));
      assertTrue(transicionesHabilitadas.contains(11));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue((boolean) disparar.invoke(rdp, 6));

      // ejecuto arrival rate
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(5);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 0));

      // me voy por el buffer del procesador 2
      transicionesSensibilizadas.add(6);
      transicionesSensibilizadas.add(11);
      transicionesSensibilizadas.add(5);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 4);
      assertTrue(transicionesHabilitadas.contains(6));
      assertTrue(transicionesHabilitadas.contains(11));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 6));

      // ejecuto T0 que es la secuencia de prendido del procesador 1
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(5);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue(transicionesHabilitadas.contains(5));
      assertTrue((boolean) disparar.invoke(rdp, 5));

      // ejecuto el powerUpDelay1
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(3);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(3));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 3));

      // ejecuto JVM1 (T2)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(10);
      transicionesSensibilizadas.add(12);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 4);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(10));
      assertTrue(transicionesHabilitadas.contains(12));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 10));

      // ejecuto el inicio del proceso de un paquete en el buffer1(T5)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(12);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(12));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 12));

      // delay1 de proceso de paquete (T7)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(13);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(13));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 13));

      // ejecuto el inicio del proceso de un paquete en el buffer1(T5)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(12);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(12));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 12));

      // delay1 de proceso de paquete (T7)
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(13);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(13));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 13));

      // apagado del procesador 1
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(1);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 3);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(1));
      assertTrue(transicionesHabilitadas.contains(14));
      assertTrue((boolean) disparar.invoke(rdp, 1));

      // se fija si el estado es el esperado
      transicionesSensibilizadas.add(0);
      transicionesSensibilizadas.add(14);
      checkearSensibilizado(transicionesSensibilizadas, transiciones, sensibilizado, rdp);
      transicionesHabilitadas = (HashSet) habilitacion.invoke(rdp);
      assertTrue(transicionesHabilitadas.size() == 2);
      assertTrue(transicionesHabilitadas.contains(0));
      assertTrue(transicionesHabilitadas.contains(14));

    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  private void checkearSensibilizado(ArrayList<Integer> sensibilizadas, int transiciones, Method sensibilizado,
      RedDePetri rdp) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    for (int i = 0; i < transiciones; i++) {
      if (!sensibilizadas.contains(i)) {
        assertTrue(!(boolean) sensibilizado.invoke(rdp, i));
      } else {
        assertTrue((boolean) sensibilizado.invoke(rdp, i));
      }
    }
    sensibilizadas.clear();
  }

  /**
   * Unit test para setTimeStamp Este test es para setTimeStamp, y se fija que
   * dicho metodo actualice las marcas de tiempos cuando debe actualizarlas.
   */
  @SuppressWarnings("unchecked")
  @Test
  void setTimeStampTest() {
    RedDePetri rdp = new RedDePetri();
    Class<?> refleccion = rdp.getClass();
    try {
      Method setTimeStamp = refleccion.getDeclaredMethod("setTimeStamp");
      Method disparar = refleccion.getDeclaredMethod("disparar", new Class[] { int.class });
      Field timeStamp = refleccion.getDeclaredField("timeStamp");
      setTimeStamp.setAccessible(true);
      disparar.setAccessible(true);
      timeStamp.setAccessible(true);

      ArrayList<Long> tiemposSensibilizadoActual = (ArrayList<Long>) timeStamp.get(rdp);
      ArrayList<Long> tiemposSensibilizadosAnterior = (ArrayList<Long>) tiemposSensibilizadoActual.clone();
      ArrayList<Integer> transicionesQueDeberianSensibilizarse = new ArrayList<Integer>();

      // quiero ignorar la ventana de tiempo
      Field alfa = refleccion.getDeclaredField("alfaReal");
      alfa.setAccessible(true);
      Object objetoAlfaReal = alfa.get(rdp);
      Method setAlfaReal = objetoAlfaReal.getClass().getDeclaredMethod("setEntry",
          new Class[] { int.class, int.class, double.class });
      for (int i = 0; i < tiemposSensibilizadoActual.size(); i++) {
        setAlfaReal.invoke(objetoAlfaReal, 0, i, (double) -1);
      }

      disparar.invoke(rdp, 0);
      transicionesQueDeberianSensibilizarse.add(6);
      transicionesQueDeberianSensibilizarse.add(11);
      for (int i = 0; i < tiemposSensibilizadoActual.size(); i++) {
        if (transicionesQueDeberianSensibilizarse.contains(i)) {
          assertTrue(tiemposSensibilizadosAnterior.get(i) != tiemposSensibilizadoActual.get(i));
        } else {
          assertTrue(tiemposSensibilizadosAnterior.get(i) == tiemposSensibilizadoActual.get(i));
        }
      }

      transicionesQueDeberianSensibilizarse.clear();
      tiemposSensibilizadosAnterior = (ArrayList<Long>) tiemposSensibilizadoActual.clone();
      disparar.invoke(rdp, 11);
      transicionesQueDeberianSensibilizarse.add(0);
      transicionesQueDeberianSensibilizarse.add(5);
      for (int i = 0; i < tiemposSensibilizadoActual.size(); i++) {
        if (transicionesQueDeberianSensibilizarse.contains(i)) {
          assertTrue(tiemposSensibilizadosAnterior.get(i) != tiemposSensibilizadoActual.get(i));
        } else {
          assertTrue(tiemposSensibilizadosAnterior.get(i) == tiemposSensibilizadoActual.get(i));
        }
      }

      transicionesQueDeberianSensibilizarse.clear();
      tiemposSensibilizadosAnterior = (ArrayList<Long>) tiemposSensibilizadoActual.clone();
      disparar.invoke(rdp, 5);
      transicionesQueDeberianSensibilizarse.add(3);
      for (int i = 0; i < tiemposSensibilizadoActual.size(); i++) {
        if (transicionesQueDeberianSensibilizarse.contains(i)) {
          assertTrue(tiemposSensibilizadosAnterior.get(i) != tiemposSensibilizadoActual.get(i));
        } else {
          assertTrue(tiemposSensibilizadosAnterior.get(i) == tiemposSensibilizadoActual.get(i));
        }
      }

    } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  void sleepTimeTest() {
    long tolerancia = 5;
    RedDePetri rdp = new RedDePetri();
    Class<?> refleccion = rdp.getClass();
    try {
      Method sleepTime = refleccion.getDeclaredMethod("sleepTime", new Class[] { int.class });
      Method disparar = refleccion.getDeclaredMethod("disparar", new Class[] { int.class });
      Field alfas = refleccion.getDeclaredField("alfaReal");
      Field timeStamp = refleccion.getDeclaredField("timeStamp");
      sleepTime.setAccessible(true);
      disparar.setAccessible(true);
      alfas.setAccessible(true);
      timeStamp.setAccessible(true);
      ArrayList<Long> marcasTiempo = (ArrayList<Long>) timeStamp.get(rdp);
      RealMatrix alfaReal = (RealMatrix) alfas.get(rdp);
      alfaReal.setEntry(0, 0, -1);

      assertTrue((boolean) disparar.invoke(rdp, 0));
      assertTrue((boolean) disparar.invoke(rdp, 11));
      assertTrue((boolean) disparar.invoke(rdp, 5));
      assertTrue((boolean) disparar.invoke(rdp, 3));
      assertTrue((boolean) disparar.invoke(rdp, 12));
      assertTrue((System.currentTimeMillis() - marcasTiempo.get(13) + (long) sleepTime.invoke(rdp, 13)
          - alfaReal.getEntry(0, 13) < tolerancia));
      assertTrue(!(boolean) disparar.invoke(rdp, 13));
      Thread.sleep((long) sleepTime.invoke(rdp, 13));
      assertTrue((boolean) disparar.invoke(rdp, 13));

      assertTrue((boolean) disparar.invoke(rdp, 0));
      assertTrue((boolean) disparar.invoke(rdp, 6));
      assertTrue((boolean) disparar.invoke(rdp, 14));
      assertTrue((boolean) disparar.invoke(rdp, 4));

      for (int i = 0; i < 499; i++) {
        assertTrue((boolean) disparar.invoke(rdp, 0));
        assertTrue((boolean) disparar.invoke(rdp, 6));
      }
      
      for(int i = 0; i < 500 ; i++) {
        assertTrue((boolean) disparar.invoke(rdp, 7));
        assertTrue(!(boolean) disparar.invoke(rdp, 8));
        assertTrue((System.currentTimeMillis() - marcasTiempo.get(8) + (long) sleepTime.invoke(rdp, 8)
        - alfaReal.getEntry(0, 8) < tolerancia));
        Thread.sleep((long) sleepTime.invoke(rdp, 8));
        assertTrue((boolean) disparar.invoke(rdp, 8));
      }

    } catch (NoSuchMethodException | SecurityException | InterruptedException e) {
      e.printStackTrace();
    } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
