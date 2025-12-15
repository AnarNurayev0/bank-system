# Automatically kill port 8080 and start Spring Boot
Write-Host "Checking port 8080..." -ForegroundColor Cyan

# Kill port 8080
& "$PSScriptRoot\kill-port-8080.ps1"

Write-Host "`nStarting Spring Boot application..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run
