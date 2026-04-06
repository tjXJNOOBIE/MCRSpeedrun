[CmdletBinding()]
param(
    [string]$RemoteHost = "146.235.232.128",
    [string]$RemoteUser = "ubuntu",
    [string]$SshKey = "$(Join-Path ([Environment]::GetFolderPath('MyDocuments')) '.ssh\NovusKey.key')",
    [string]$RemoteWebsiteRoot = "/srv/PROJECT-NOVUS/Website",
    [string]$RemoteProxyDir = "/srv/proxy",
    [string]$RemoteHeadlessSourceDir = "/srv/headless/bot-testing/src",
    [string]$LobbyAddress = "127.0.0.1:25566",
    [string]$StoreBaseUrl = $env:NOVUS_STORE_BASE_URL,
    [string]$StoreBootstrapSecret = $env:APP_STORE_INTEGRATION_BOOTSTRAP_SECRET,
    [string]$StoreNodeId = "velocity-proxy",
    [string]$StoreFallbackRank = "Member",
    [switch]$SkipBuild,
    [switch]$SkipProxyRestart,
    [switch]$SkipWebsiteStart
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$maven = "C:\Tools\apache-maven-3.9.9\bin\mvn.cmd"
$ssh = "C:\Windows\System32\OpenSSH\ssh.exe"
$scp = "C:\Windows\System32\OpenSSH\scp.exe"
$remote = "$RemoteUser@$RemoteHost"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"

function Invoke-Remote([string]$Command) {
    & $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote $Command
    if ($LASTEXITCODE -ne 0) {
        throw "Remote command failed: $Command"
    }
}

if (-not $SkipBuild) {
    $env:MAVEN_OPTS = "-Xmx768m -Xms128m"
    & $maven -pl Website,VelocityCore -am -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven package failed."
    }
}

$websiteJar = Get-ChildItem -Path (Join-Path $repoRoot "Website\target\*exec.jar") | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$proxyJar = Get-ChildItem -Path (Join-Path $repoRoot "VelocityCore\target\velocitycore-*.jar") |
    Where-Object { $_.Name -notlike "*.original*" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1
$remoteStartScript = Join-Path $repoRoot "ops\remote\start-store-web-remote.sh"
$remoteStopScript = Join-Path $repoRoot "ops\remote\stop-store-web-remote.sh"
$headlessScript = Join-Path $repoRoot "ops\headless\store-e2e.ts"
$proxySchemaSql = Join-Path $repoRoot "ops\remote\bootstrap-proxy-rank-schema.sql"
$proxySchemaBootstrapScript = Join-Path $repoRoot "ops\remote\bootstrap-proxy-rank-schema.ps1"
$velocityLobbyFixScript = Join-Path $repoRoot "ops\remote\fix_velocity_lobby.py"

if (-not $websiteJar) { throw "Website executable jar not found. Run package first." }
if (-not $proxyJar) { throw "VelocityCore jar not found. Run package first." }

Write-Host "Deploying website jar: $($websiteJar.FullName)"
Write-Host "Deploying proxy jar: $($proxyJar.FullName)"

Invoke-Remote "mkdir -p '$RemoteWebsiteRoot/target' '$RemoteWebsiteRoot/logs' '$RemoteProxyDir/plugins' '$RemoteHeadlessSourceDir'"

& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $websiteJar.FullName "${remote}:$RemoteWebsiteRoot/target/store-web-exec.jar"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $proxyJar.FullName "${remote}:$RemoteProxyDir/plugins/Speedrun.jar.new"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $remoteStartScript "${remote}:$RemoteWebsiteRoot/start-store-web.sh"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $remoteStopScript "${remote}:$RemoteWebsiteRoot/stop-store-web.sh"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $headlessScript "${remote}:$RemoteHeadlessSourceDir/store-e2e.ts"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $proxySchemaSql "${remote}:$RemoteProxyDir/bootstrap-proxy-rank-schema.sql"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $velocityLobbyFixScript "${remote}:$RemoteProxyDir/fix_velocity_lobby.py"
if ($LASTEXITCODE -ne 0) {
    throw "File copy failed."
}

Invoke-Remote @"
set -e
if [ -f '$RemoteWebsiteRoot/target/store-web-exec.jar' ]; then
  cp '$RemoteWebsiteRoot/target/store-web-exec.jar' '$RemoteWebsiteRoot/target/store-web-exec.jar.bak-$timestamp' || true
fi
if [ -f '$RemoteProxyDir/plugins/Speedrun.jar' ]; then
  cp '$RemoteProxyDir/plugins/Speedrun.jar' '$RemoteProxyDir/plugins/Speedrun.jar.bak-$timestamp'
fi
mv '$RemoteProxyDir/plugins/Speedrun.jar.new' '$RemoteProxyDir/plugins/Speedrun.jar'
chmod +x '$RemoteWebsiteRoot/start-store-web.sh' '$RemoteWebsiteRoot/stop-store-web.sh'
python3 '$RemoteProxyDir/fix_velocity_lobby.py' '$RemoteProxyDir/velocity.toml' '$LobbyAddress'
"@

& $proxySchemaBootstrapScript -RemoteHost $RemoteHost -RemoteUser $RemoteUser -SshKey $SshKey -RemoteProxyDir $RemoteProxyDir
if ($LASTEXITCODE -ne 0) {
    throw "Proxy rank schema bootstrap failed."
}

if (-not $SkipWebsiteStart) {
    Invoke-Remote "ENV_FILE='$RemoteWebsiteRoot/store-web.env' APP_ROOT='$RemoteWebsiteRoot' JAR_PATH='$RemoteWebsiteRoot/target/store-web-exec.jar' bash '$RemoteWebsiteRoot/start-store-web.sh'"
}

if (-not $SkipProxyRestart) {
    $proxyEnvPrefix = ""
    if (-not [string]::IsNullOrWhiteSpace($StoreBaseUrl)) {
        $proxyEnvPrefix += "NOVUS_STORE_BASE_URL='$StoreBaseUrl' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreBootstrapSecret)) {
        $proxyEnvPrefix += "NOVUS_STORE_BOOTSTRAP_SECRET='$StoreBootstrapSecret' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreNodeId)) {
        $proxyEnvPrefix += "NOVUS_STORE_NODE_ID='$StoreNodeId' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreFallbackRank)) {
        $proxyEnvPrefix += "NOVUS_STORE_FALLBACK_RANK='$StoreFallbackRank' "
    }
    Invoke-Remote "pkill -f '[v]elocity.jar' || true; cd '$RemoteProxyDir' && nohup env ${proxyEnvPrefix}bash ./start.sh > logs/proxy-store.out.log 2> logs/proxy-store.err.log < /dev/null &"
}

Write-Host "Deployment complete."
Write-Host "Website root: $RemoteWebsiteRoot"
Write-Host "Proxy dir:    $RemoteProxyDir"
Write-Host "Lobby target: $LobbyAddress"
