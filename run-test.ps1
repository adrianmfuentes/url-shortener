#!/bin/bash

set -e

cleanup() {
    echo "🛑 Deteniendo MongoDB..."
    docker-compose -f docker-compose.test.yml down -v
}

trap cleanup EXIT

echo "🚀 Iniciando MongoDB..."
docker-compose -f docker-compose.test.yml up -d

echo "⏳ Esperando a que MongoDB esté listo..."
timeout=30
until docker exec $(docker-compose -f docker-compose.test.yml ps -q mongodb-test) mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; do
    timeout=$((timeout - 1))
    if [ $timeout -le 0 ]; then
        echo "❌ MongoDB no respondió a tiempo"
        exit 1
    fi
    sleep 1
done

echo "🧪 Ejecutando tests..."
mvn clean test

echo "✅ Tests completados"
