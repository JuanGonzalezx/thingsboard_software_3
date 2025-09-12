#!/bin/bash

echo "🧪 === TELEGRAM TESTS RUNNER ==="
echo ""

# Directorio del proyecto
PROJECT_DIR="/e/university/sw3/thingsboard_software_3"
cd "$PROJECT_DIR"

echo "📁 Directorio actual: $(pwd)"
echo ""

# Función para ejecutar pruebas unitarias
run_unit_tests() {
    echo "🔧 === EJECUTANDO PRUEBAS UNITARIAS ==="
    echo "⚡ Rápidas - NO envían mensajes reales"
    echo ""
    
    mvn test -pl application -Dtest=DefaultTelegramServiceTest
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Pruebas unitarias completadas exitosamente"
    else
        echo ""
        echo "❌ Error en pruebas unitarias"
        return 1
    fi
}

# Función para ejecutar pruebas de integración
run_integration_tests() {
    echo ""
    echo "🚀 === EJECUTANDO PRUEBAS DE INTEGRACIÓN ==="
    echo "⚠️  ESTAS ENVÍAN MENSAJES REALES A TELEGRAM"
    echo ""
    
    # Verificar variables de entorno
    if [ -z "$TELEGRAM_BOT_TOKEN" ]; then
        echo "❌ Variable TELEGRAM_BOT_TOKEN no configurada"
        echo "Configúrala con: export TELEGRAM_BOT_TOKEN=\"tu_token\""
        return 1
    fi
    
    if [ -z "$TELEGRAM_CHAT_ID" ]; then
        echo "❌ Variable TELEGRAM_CHAT_ID no configurada"
        echo "Configúrala con: export TELEGRAM_CHAT_ID=\"tu_chat_id\""
        return 1
    fi
    
    echo "✅ Variables configuradas:"
    echo "  Bot Token: ${TELEGRAM_BOT_TOKEN:0:10}..."
    echo "  Chat ID: $TELEGRAM_CHAT_ID"
    echo ""
    
    mvn test -pl application -Dtest=TelegramIntegrationTest
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Pruebas de integración completadas exitosamente"
        echo "📱 Revisa tu chat de Telegram para ver los mensajes enviados"
    else
        echo ""
        echo "❌ Error en pruebas de integración"
        return 1
    fi
}

# Función principal
main() {
    case "${1:-all}" in
        "unit")
            run_unit_tests
            ;;
        "integration")
            run_integration_tests
            ;;
        "all")
            run_unit_tests
            if [ $? -eq 0 ]; then
                run_integration_tests
            fi
            ;;
        "help")
            echo "Uso: $0 [unit|integration|all|help]"
            echo ""
            echo "Comandos:"
            echo "  unit        - Solo pruebas unitarias (rápidas, sin mensajes reales)"
            echo "  integration - Solo pruebas de integración (envían mensajes reales)"
            echo "  all         - Ambas pruebas (default)"
            echo "  help        - Mostrar esta ayuda"
            echo ""
            echo "Variables requeridas para pruebas de integración:"
            echo "  export TELEGRAM_BOT_TOKEN=\"tu_bot_token\""
            echo "  export TELEGRAM_CHAT_ID=\"tu_chat_id\""
            ;;
        *)
            echo "❌ Opción desconocida: $1"
            echo "Usa: $0 help para ver opciones disponibles"
            exit 1
            ;;
    esac
}

# Ejecutar función principal
main "$@"