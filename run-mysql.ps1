param([string]$DatabaseHost='localhost',[int]$DatabasePort=3306,[string]$Database='campus_marketplace')
$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot
if ($Database -notmatch '^[a-zA-Z0-9_]+$') { throw 'Use letters, digits and underscores for the database name.' }
if (-not (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
  $jdk=Get-ChildItem 'C:\Program Files\Eclipse Adoptium\jdk-17*' -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
  if ($jdk) { $env:JAVA_HOME=$jdk.FullName } else { throw 'Install JDK 17 and set JAVA_HOME.' }
}
$env:DB_DRIVER='com.mysql.cj.jdbc.Driver'
$env:DB_URL="jdbc:mysql://${DatabaseHost}:${DatabasePort}/${Database}?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER=Read-Host 'MySQL username'
$secret=Read-Host 'MySQL password (hidden)' -AsSecureString
$ptr=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
try {
  $env:DB_PASSWORD=[Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
  Write-Host 'Starting CampusMarket on http://localhost:8080/browse. Keep this window open.'
  & .\mvnw.cmd compile exec:java
} finally {
  [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
  Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
