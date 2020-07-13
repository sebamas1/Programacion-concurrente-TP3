package monitorTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import monitor.*;

class RedDePetriTest {
  @SuppressWarnings("rawtypes")
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
      Method getAlfaReal = objetoAlfaReal.getClass().getDeclaredMethod("getEntry",
          new Class[] { int.class, int.class });

      Object objetoBetaReal = betaReal.get(rdp);
      Method setBetaReal = objetoBetaReal.getClass().getDeclaredMethod("setEntry",
          new Class[] { int.class, int.class, double.class });
      Method getBetaReal = objetoBetaReal.getClass().getDeclaredMethod("getEntry",
          new Class[] { int.class, int.class });

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
}
