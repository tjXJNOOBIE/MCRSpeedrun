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
$gradle = Join-Path $repoRoot "gradlew.bat"
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
    & $gradle --no-daemon clean check stageDistribution
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed."
    }
}

$websiteJar = Get-Item -Path (Join-Path $repoRoot "distribution\website\application.jar")
$websiteLibDir = Get-Item -Path (Join-Path $repoRoot "distribution\website\libs")
$proxyJar = Get-Item -Path (Join-Path $repoRoot "distribution\plugins\velocitycore.jar")
$proxyLibDir = Get-Item -Path (Join-Path $repoRoot "distribution\plugins\libs")
$remoteStartScript = Join-Path $repoRoot "ops\remote\start-store-web-remote.sh"
$remoteStopScript = Join-Path $repoRoot "ops\remote\stop-store-web-remote.sh"
$remoteProxyRestartScript = Join-Path $repoRoot "ops\remote\restart-proxy-with-store-env.sh"
$headlessScript = Join-Path $repoRoot "ops\headless\store-e2e.ts"
$proxySchemaSql = Join-Path $repoRoot "ops\remote\bootstrap-proxy-rank-schema.sql"
$proxySchemaBootstrapScript = Join-Path $repoRoot "ops\remote\bootstrap-proxy-rank-schema.ps1"
$velocityLobbyFixScript = Join-Path $repoRoot "ops\remote\fix_velocity_lobby.py"

if (-not $websiteJar -or -not $websiteLibDir) { throw "Website distribution not found. Run stageDistribution first." }
if (-not $proxyJar -or -not $proxyLibDir) { throw "VelocityCore distribution not found. Run stageDistribution first." }

Write-Host "Deploying website jar: $($websiteJar.FullName)"
Write-Host "Deploying proxy jar: $($proxyJar.FullName)"

Invoke-Remote @"
set -e
mkdir -p '$RemoteWebsiteRoot/distribution/backups/$timestamp' '$RemoteWebsiteRoot/logs' \
  '$RemoteProxyDir/plugins/backups/$timestamp' '$RemoteHeadlessSourceDir'
if [ -f '$RemoteWebsiteRoot/distribution/application.jar' ]; then
  cp '$RemoteWebsiteRoot/distribution/application.jar' '$RemoteWebsiteRoot/distribution/backups/$timestamp/application.jar'
fi
if [ -d '$RemoteWebsiteRoot/distribution/libs' ]; then
  cp -a '$RemoteWebsiteRoot/distribution/libs' '$RemoteWebsiteRoot/distribution/backups/$timestamp/libs'
fi
if [ -f '$RemoteProxyDir/plugins/Speedrun.jar' ]; then
  cp '$RemoteProxyDir/plugins/Speedrun.jar' '$RemoteProxyDir/plugins/backups/$timestamp/Speedrun.jar'
fi
if [ -d '$RemoteProxyDir/plugins/libs' ]; then
  cp -a '$RemoteProxyDir/plugins/libs' '$RemoteProxyDir/plugins/backups/$timestamp/libs'
fi
rm -rf '$RemoteWebsiteRoot/distribution/libs.new' '$RemoteProxyDir/plugins/libs.new'
"@

& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $websiteJar.FullName "${remote}:$RemoteWebsiteRoot/distribution/application.jar.new"
& $scp -r -i $SshKey -o StrictHostKeyChecking=accept-new $websiteLibDir.FullName "${remote}:$RemoteWebsiteRoot/distribution/libs.new"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $proxyJar.FullName "${remote}:$RemoteProxyDir/plugins/Speedrun.jar.new"
& $scp -r -i $SshKey -o StrictHostKeyChecking=accept-new $proxyLibDir.FullName "${remote}:$RemoteProxyDir/plugins/libs.new"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $remoteStartScript "${remote}:$RemoteWebsiteRoot/start-store-web.sh"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $remoteStopScript "${remote}:$RemoteWebsiteRoot/stop-store-web.sh"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $remoteProxyRestartScript "${remote}:$RemoteProxyDir/restart-proxy-with-store-env.sh"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $headlessScript "${remote}:$RemoteHeadlessSourceDir/store-e2e.ts"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $proxySchemaSql "${remote}:$RemoteProxyDir/bootstrap-proxy-rank-schema.sql"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $velocityLobbyFixScript "${remote}:$RemoteProxyDir/fix_velocity_lobby.py"
if ($LASTEXITCODE -ne 0) {
    throw "File copy failed."
}

Invoke-Remote @"
set -e
mv '$RemoteWebsiteRoot/distribution/application.jar.new' '$RemoteWebsiteRoot/distribution/application.jar'
rm -rf '$RemoteWebsiteRoot/distribution/libs'
mv '$RemoteWebsiteRoot/distribution/libs.new' '$RemoteWebsiteRoot/distribution/libs'
mv '$RemoteProxyDir/plugins/Speedrun.jar.new' '$RemoteProxyDir/plugins/Speedrun.jar'
rm -rf '$RemoteProxyDir/plugins/libs'
mv '$RemoteProxyDir/plugins/libs.new' '$RemoteProxyDir/plugins/libs'
chmod +x '$RemoteWebsiteRoot/start-store-web.sh' '$RemoteWebsiteRoot/stop-store-web.sh' '$RemoteProxyDir/restart-proxy-with-store-env.sh'
python3 '$RemoteProxyDir/fix_velocity_lobby.py' '$RemoteProxyDir/velocity.toml' '$LobbyAddress'
"@

& $proxySchemaBootstrapScript -RemoteHost $RemoteHost -RemoteUser $RemoteUser -SshKey $SshKey -RemoteProxyDir $RemoteProxyDir
if ($LASTEXITCODE -ne 0) {
    throw "Proxy rank schema bootstrap failed."
}

if (-not $SkipWebsiteStart) {
    Invoke-Remote "ENV_FILE='$RemoteWebsiteRoot/store-web.env' APP_ROOT='$RemoteWebsiteRoot' bash '$RemoteWebsiteRoot/start-store-web.sh'"
}

if (-not $SkipProxyRestart) {
    $proxyEnvPrefix = ""
    if (-not [string]::IsNullOrWhiteSpace($StoreBaseUrl)) {
        $proxyEnvPrefix += "STORE_BASE_URL='$StoreBaseUrl' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreBootstrapSecret)) {
        $proxyEnvPrefix += "STORE_BOOTSTRAP_SECRET='$StoreBootstrapSecret' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreNodeId)) {
        $proxyEnvPrefix += "STORE_NODE_ID='$StoreNodeId' "
    }
    if (-not [string]::IsNullOrWhiteSpace($StoreFallbackRank)) {
        $proxyEnvPrefix += "STORE_FALLBACK_RANK='$StoreFallbackRank' "
    }
    Invoke-Remote "cd '$RemoteProxyDir' && env ${proxyEnvPrefix}PROXY_DIR='$RemoteProxyDir' WEBSITE_ENV_FILE='$RemoteWebsiteRoot/store-web.env' bash ./restart-proxy-with-store-env.sh"
}

Write-Host "Deployment complete."
Write-Host "Website root: $RemoteWebsiteRoot"
Write-Host "Proxy dir:    $RemoteProxyDir"
Write-Host "Lobby target: $LobbyAddress"
