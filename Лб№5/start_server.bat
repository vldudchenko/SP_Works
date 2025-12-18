@echo off
chcp 65001 >nul
title REST API Server - Task Management

cd /d %~dp0

echo ========================================
echo   REST API Server - Task Management
echo   URL: http://localhost:8080/api/tasks
echo ========================================
echo.

if not exist *.class (
    echo Компиляция Java файлов...
    javac *.java
    if %errorlevel% neq 0 (
        echo ОШИБКА: Не удалось скомпилировать файлы!
        pause
        exit /b 1
    )
    echo ✓ Компиляция завершена
    echo.
)

echo Запуск REST API сервера...
echo.
echo Сервер будет доступен по адресу:
echo   http://localhost:8080/api/tasks
echo.
echo Методы: GET, POST, PUT, DELETE
echo.
echo Нажмите Enter для остановки сервера...
echo.

java RestApiServer

pause

