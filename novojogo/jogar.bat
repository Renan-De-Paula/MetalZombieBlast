@echo off
setlocal enabledelayedexpansion

echo Iniciando Metal Zombie...
echo.

:: Define a pasta onde o Java portatil vai ficar
set "JRE_PATH=%~dp0jre\bin\java.exe"

:: 1. Verifica se ja baixamos o nosso proprio Java portatil
if exist "%JRE_PATH%" (
    goto :RunGame
)

:: 2. Se nao existir, vamos baixar o Java 21 JRE (Sem precisar instalar no sistema, totalmente portatil!)
echo [AVISO] Java 21 local nao encontrado.
echo O jogo vai baixar automaticamente os arquivos necessarios (aprox. 45 MB).
echo Por favor, aguarde...
echo.

:: Usa o PowerShell em background para baixar e extrair o JRE da Adoptium (Eclipse Temurin)
powershell -NoProfile -Command ^
    "Write-Host '1/3 Baixando o Java 21...'; " ^
    "Invoke-WebRequest -Uri 'https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jre/hotspot/normal/eclipse' -OutFile 'jre.zip'; " ^
    "Write-Host '2/3 Extraindo arquivos...'; " ^
    "Expand-Archive -Path 'jre.zip' -DestinationPath 'jre_temp' -Force; " ^
    "Write-Host '3/3 Organizando pastas...'; " ^
    "New-Item -ItemType Directory -Force -Path 'jre' | Out-Null; " ^
    "Move-Item -Path jre_temp\*\* -Destination 'jre' -Force; " ^
    "Remove-Item -Path 'jre_temp' -Recurse -Force; " ^
    "Remove-Item -Path 'jre.zip' -Force; "

if not exist "%JRE_PATH%" (
    echo.
    echo [ERRO FATAL] Falha ao baixar o Java automaticamente.
    echo Verifique sua conexao com a internet ou instale o Java manualmente.
    pause
    exit /b 1
)

echo.
echo Java baixado e configurado com sucesso!
echo.

:RunGame
:: 3. Executa o jogo usando o nosso Java portatil recem baixado
"%JRE_PATH%" -Dsun.java2d.d3d=false -Dsun.java2d.opengl=true -Dsun.java2d.noddraw=true -jar target\ultimo-abrigo-1.0.0.jar

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERRO] O jogo fechou inesperadamente. O arquivo .jar pode nao existir na pasta 'target'.
    echo Voce ja rodou o comando 'mvn clean package'?
    pause
)
