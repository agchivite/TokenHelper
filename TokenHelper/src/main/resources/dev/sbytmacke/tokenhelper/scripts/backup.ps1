# Archivos que deseas respaldar
$JSON_FILE = "backup.json"
#$CSV_FILE = "backup.csv"

# Rama de respaldo
$BRANCH_NAME = "backup"

# Directorio temporal fuera del proyecto
$TEMP_DIR = "..\..\temp_backup"

# Crear el directorio temporal de respaldo
if (-not(Test-Path -Path $TEMP_DIR))
{
    New-Item -Path $TEMP_DIR -ItemType Directory
}

# Copiar el archivo JSON al directorio temporal de respaldo
Copy-Item -Path $JSON_FILE -Destination $TEMP_DIR
# Copy-Item -Path $CSV_FILE -Destination $TEMP_DIR

git checkout -f $BRANCH_NAME

# Restaurar los archivos desde el directorio temporal
Copy-Item -Path "$TEMP_DIR\$JSON_FILE" -Destination .

# Agregar los archivos al índice de Git
git add -f $JSON_FILE

# git add $CSV_FILE

$COMMIT_MESSAGE = "Backup de JSON"
git commit -m "$COMMIT_MESSAGE"

git push origin $BRANCH_NAME

# Regresar a la rama original
git checkout -f -

# Eliminar el directorio temporal
Remove-Item -Path $TEMP_DIR -Recurse
