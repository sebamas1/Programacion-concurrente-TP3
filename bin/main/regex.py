import re
import sys

file = open("Registro.txt")

log = file.read().replace("\n", " ")
file.close()

match = re.search(r"\b0\b(.*?)(?:\b11\b(.*?)(?:\b10\b(.*?)\b12\b(.*?)\b13\b|\b12\b(.*?)(?:\b10\b(.*?)\b13\b|\b13\b(.*?)\b10\b)|\b5\b(.*?)\b3\b(.*?)\b12\b(.*?)\b13\b(.*?)\b1\b)|\b6\b(.*?)(?:\b9\b(.*?)\b7\b(.*?)\b8\b|\b7\b(.*?)(?:\b9\b(.*?)\b8\b|\b8\b(.*?)\b9\b)|\b14\b(.*?)\b4\b(.*?)\b7\b(.*?)\b8\b(.*?)\b2\b))",
                  log)
while (match != None): # Si no matcheas nada, fin
  match_sin_inv = '' # Concatenación de grupos capturados
  for string in match.groups(): # Si el grupo no está vacío
    if string != None:
      match_sin_inv += string # Concatenamos
      # match.span() -> (principio,fin)
  log = log[0 : match.span()[0]] + match_sin_inv + log[match.span()[1]:]
  match = re.search(r"\b0\b(.*?)(?:\b11\b(.*?)(?:\b10\b(.*?)\b12\b(.*?)\b13\b|\b12\b(.*?)(?:\b10\b(.*?)\b13\b|\b13\b(.*?)\b10\b)|\b5\b(.*?)\b3\b(.*?)\b12\b(.*?)\b13\b(.*?)\b1\b)|\b6\b(.*?)(?:\b9\b(.*?)\b7\b(.*?)\b8\b|\b7\b(.*?)(?:\b9\b(.*?)\b8\b|\b8\b(.*?)\b9\b)|\b14\b(.*?)\b4\b(.*?)\b7\b(.*?)\b8\b(.*?)\b2\b))",
                    log)

for char in log:
  if char != ' ':
    file = open("Registro.txt", "a")
    file.write("\nERROR EN LOS T-INVARIANTS")
    exit()

file = open("Registro.txt", "a")
file.write("\nTodo bien invariantes T.")
file.close()

    