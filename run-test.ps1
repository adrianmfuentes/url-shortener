# Definir la función de limpieza
function cleanup {
    Write-Host "🛑 Deteniendo MongoDB..."
    docker-compose -f docker-compose.test.yml down -v
}

# Manejo de errores globales
$ErrorActionPreference = "Stop"  # Para detener el script en caso de error

# Asegurarse de limpiar al salir
$cleanupTask = Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action { cleanup }

Write-Host "🚀 Iniciando MongoDB..."
docker-compose -f docker-compose.test.yml up -d

Write-Host "⏳ Esperando a que MongoDB esté listo..."
$timeout = 30
do {
    $containerId = docker-compose -f docker-compose.test.yml ps -q mongodb-test
    $pingResult = docker exec $containerId mongosh --eval "db.adminCommand('ping')" 2>$null
    if ($pingResult -eq $null) {
        $timeout--
        if ($timeout -le 0) {
            Write-Host "❌ MongoDB no respondió a tiempo"
            exit 1
        }
        Start-Sleep -Seconds 1
    }
} while ($pingResult -ne $null)

Write-Host "🧪 Ejecutando tests..."
mvn clean test

Write-Host "✅ Tests completados"

# Limpiar eventos registrados
Unregister-Event -SourceIdentifier PowerShell.Exiting
