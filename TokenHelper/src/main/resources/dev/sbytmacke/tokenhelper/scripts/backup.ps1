#!/bin/bash

# Archivos que deseas respaldar
JSON_FILE="backup.json"
#CSV_FILE="backup.csv"

# Rama de respaldo
BRANCH_NAME="backup"

# Directorio temporal fuera del proyecto
TEMP_DIR="../../temp_backup"
mkdir -p $TEMP_DIR
cp $JSON_FILE $TEMP_DIR
# cp $CSV_FILE $TEMP_DIR

# Cambiar a la rama de respaldo (si existe, o crearla si no)
git checkout $BRANCH_NAME || git checkout -b $BRANCH_NAME

# Restaurar los archivos desde el directorio temporal
cp $TEMP_DIR/$JSON_FILE .
# cp $TEMP_DIR/$CSV_FILE .

# Agregamos los archivos al índice de Git
git add $JSON_FILE
#git add $CSV_FILE

COMMIT_MESSAGE="Backup de JSON"
git commit -m "$COMMIT_MESSAGE"

git push origin $BRANCH_NAME

# Regresar a la rama original
git checkout -

# Eliminar el directorio temporal
rm -rf $TEMP_DIR
