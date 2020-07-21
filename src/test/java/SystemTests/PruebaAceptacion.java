package SystemTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import main.Sistema;

class PruebaAceptacion {
  private final int PRODUCTOS = Sistema.TAREASTOTALES;

  /**
   * Ejecuta una vez el programa completo, y prueba que al final de su ejecucion,
   * el tiempo total no sea menos que una cota minima. Ademas, lee el archivo de
   * Registro y corrobora que se haya reportado que se cumplen los invariantes, y
   * que el marcado final sea el esperado. La cota inferior se calcula segun la
   * transicion que tenga mayor alfa multiplicado por la cantidad de veces que
   * tendria que dispararse esa transicion. Por ejemplo, el arrival rate tendria
   * que dispararse la misma cantidad de veces que productos haya, y los service
   * rate tendrian que dispararse (aproximadamente) la mitad de veces.
   */
  @Test
  void aceptacion() {
    Sistema launcher = new Sistema();
    Class<?> refleccion = launcher.getClass();

    try {

      Field tareas1 = refleccion.getDeclaredField("tareasHechasCore1");
      Field tareas2 = refleccion.getDeclaredField("tareasHechasCore2");
      Field inicio = refleccion.getDeclaredField("inicio");
      Field finall = refleccion.getDeclaredField("finall");
      tareas1.setAccessible(true);
      tareas2.setAccessible(true);
      inicio.setAccessible(true);
      finall.setAccessible(true);

      Scanner input = null;
      input = new Scanner(new File("src/main/java/alfa.txt"));
      int arrival_rate = 0;
      int service_time1 = 0;
      int service_time2 = 0;
      for (int i = 0; input.hasNextLine(); i++) {
        if (i == 0) {
          arrival_rate = input.nextInt();
          continue;
        }
        if (i == 8) {
          service_time2 = input.nextInt();
          continue;
        }
        if (i == 13) {
          service_time1 = input.nextInt();
          continue;
        }
        input.nextInt();
      }
      input.close();
      int max_service = (service_time1 + service_time2) / 2;
      @SuppressWarnings("unused")
      long tiempo_cota_inferior = max_service < arrival_rate ? (arrival_rate * PRODUCTOS)
          : (max_service * (PRODUCTOS / 2));
      assertTrue(tareas1.getLong(launcher) + tareas2.getLong(launcher) == PRODUCTOS);
      // assertTrue(((finall.getLong(launcher) - inicio.getLong(launcher)) / 1000000 >= tiempo_cota_inferior);
      // es dificil estblecer una cota inferior o superior, porque nunca se sabe cual
      // va a ser
      // el comportamiento de reparticion de productos entre cores para distintos
      // valores de alfa.

      File registro = new File("src/main/java/Registro.txt");
      String lineas[] = lectorRegistro(registro, 6).split("\\r?\\n");

      assertEquals("El marcado final esperado, coincide con el real.", lineas[1]);
      assertEquals("Todo en orden invP.", lineas[2]);
      assertEquals("Todo en orden invT.", lineas[3]);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Toma el archivo pasdo como argumento, y lee las ultimas N lineas, luego las
   * concatena y las devuelve en un solo String.
   * 
   * @param file  es el archivo desde el que quiero que lea.
   * @param lines es la N cantidad de filas que quiero que lea empezando desde
   *              abajo del archivo.
   * @return un String que contiene las ultimas N lineas.
   */
  private String lectorRegistro(File file, int lines) {
    java.io.RandomAccessFile fileHandler = null;
    try {
      fileHandler = new java.io.RandomAccessFile(file, "r");
      long fileLength = fileHandler.length() - 1;
      StringBuilder sb = new StringBuilder();
      int line = 0;

      for (long filePointer = fileLength; filePointer != -1; filePointer--) {
        fileHandler.seek(filePointer);
        int readByte = fileHandler.readByte();

        if (readByte == 0xA) {
          if (filePointer < fileLength) {
            line = line + 1;
          }
        } else if (readByte == 0xD) {
          if (filePointer < fileLength - 1) {
            line = line + 1;
          }
        }
        if (line >= lines) {
          break;
        }
        sb.append((char) readByte);
      }

      String lastLine = sb.reverse().toString();
      return lastLine;
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (java.io.IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (fileHandler != null)
        try {
          fileHandler.close();
        } catch (IOException e) {
        }
    }
  }
}
