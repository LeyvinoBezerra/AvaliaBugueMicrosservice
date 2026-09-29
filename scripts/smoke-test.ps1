$ErrorActionPreference = "Stop"
Invoke-RestMethod http://localhost:8080/actuator/health | ConvertTo-Json
Invoke-WebRequest http://localhost:8080/v3/api-docs | Select-Object -ExpandProperty StatusCode
Write-Host "BuggyTrip smoke test OK"
