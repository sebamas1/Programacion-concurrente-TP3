package monitorTesting;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;
import monitor.Politica;

class PoliticaTest {
  /**
   * UniTest para senializacion(indice). Se fija que senializacion devuelva true
   * si no existen transiciones senializadas, y en caso de que exista alguna
   * transicion senializada, que solo devuelva true en caso de que el indice
   * coincida con la transicion senializada; en otro caso devuelve false.
   */
  @SuppressWarnings("unchecked")
  @Test
  void senializacionTest() {
    Politica politica = new Politica(15);
    Class<?> refleccion = politica.getClass();
    try {
      Method senializacion = refleccion.getDeclaredMethod("senializacion", new Class[] { int.class });
      Field senializadas = refleccion.getDeclaredField("senializadas");
      senializacion.setAccessible(true);
      senializadas.setAccessible(true);
      ArrayList<Boolean> senializadasObject = (ArrayList<Boolean>) senializadas.get(politica);

      for (int j = 0; j < 1000; j++) {
        for (int i = 0; i < senializadasObject.size(); i++) {
          assertTrue((boolean) senializacion.invoke(politica, i));
        }
        for (int i = 0; i < senializadasObject.size(); i++) {
          senializadasObject.set(i, true);
          assertTrue((boolean) senializacion.invoke(politica, i));
          senializadasObject.set(i, false);
        }
        for (int i = 0; i < senializadasObject.size(); i++) {
          senializadasObject.set(i, true);
          double prueba = Math.random() * 15;
          while ((int) prueba == i) {
            prueba = Math.random() * 15;
          }
          assertTrue((int) prueba != i);
          assertTrue(!(boolean) senializacion.invoke(politica, (int) prueba));
          senializadasObject.set(i, false);
        }
      }

    } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  /**
   * UniTest para prioridad(ArrayList), crea un array con todas las transiciones y
   * se fija que siempre devuelva alguna transicion con la prioridad mas alta que
   * se haya generado aleatoriamente por este test.
   */
  @SuppressWarnings("unchecked")
  @Test
  void prioridadTest() {
    Politica politica = new Politica(15);
    Class<?> refleccion = politica.getClass();
    try {
      Method prioridad = refleccion.getDeclaredMethod("prioridad", new Class[] { ArrayList.class });
      Field vectorPrioridad = refleccion.getDeclaredField("vectorPrioridad");
      Field senializadas = refleccion.getDeclaredField("senializadas");
      prioridad.setAccessible(true);
      vectorPrioridad.setAccessible(true);
      senializadas.setAccessible(true);

      RealMatrix objetoVectorPrioridad = (RealMatrix) vectorPrioridad.get(politica);
      ArrayList<Integer> transiciones = new ArrayList<Integer>();
      ArrayList<Boolean> arraySenializadas = (ArrayList<Boolean>) senializadas.get(politica);
      for (int i = 0; i < 15; i++) {
        transiciones.add(i);
      }
      for (int i = 0; i < 1000; i++) {
        int prioridad_mas_alta = 0;
        for (int j = 0; j < objetoVectorPrioridad.getRowDimension(); j++) {
          double grado_prioridad = Math.random() * 10;
          prioridad_mas_alta = (int) grado_prioridad > prioridad_mas_alta ? (int) grado_prioridad : prioridad_mas_alta;
          objetoVectorPrioridad.setEntry(j, 0, (int) grado_prioridad);
        }
        ArrayList<Integer> transiciones2 = (ArrayList<Integer>) transiciones.clone();
        assertTrue(
            objetoVectorPrioridad.getEntry((int) prioridad.invoke(politica, transiciones2), 0) == prioridad_mas_alta);
        assertTrue(arraySenializadas.get((int) prioridad.invoke(politica, transiciones2)));
      }
    } catch (NoSuchMethodException | SecurityException | NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
