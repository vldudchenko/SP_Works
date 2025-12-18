@echo off
chcp 65001 >nul
title Лабораторная работа №4 - Автозапуск

echo ========================================
echo   Лабораторная работа №4
echo   Клиент-серверное приложение
echo ========================================
echo.

echo [1/4] Компиляция Java файлов...
javac *.java
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось скомпилировать файлы!
    pause
    exit /b 1
)
echo ✓ Компиляция завершена успешно
echo.

echo [2/4] Запуск Server (UDP Multicast)...
start "Server - UDP Multicast" cmd /k "cd /d %~dp0 && java Server"
timeout /t 2 /nobreak >nul

echo [3/4] Запуск IntermediateClient (UDP Client + TCP Server)...
start "IntermediateClient - UDP Client + TCP Server" cmd /k "cd /d %~dp0 && java IntermediateClient"
timeout /t 2 /nobreak >nul

echo [4/4] Запуск EndClient (TCP Client + GUI)...
start "EndClient - TCP Client + GUI" cmd /k "cd /d %~dp0 && java EndClient"
timeout /t 2 /nobreak >nul

echo.
echo ========================================
echo   Все компоненты запущены!
echo ========================================
echo.
echo Открыто 3 окна:
echo   - Server (UDP Multicast сервер)
echo   - IntermediateClient (Промежуточный клиент)
echo   - EndClient (Конечный клиент с GUI)
echo.
echo Порядок остановки:
echo   1. Закройте окно EndClient (GUI)
echo   2. Закройте окно IntermediateClient
echo   3. Закройте окно Server (или нажмите Ctrl+C)
echo.
pause

