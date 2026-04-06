[CmdletBinding()]
param(
    [string]$RemoteHost = "146.235.232.128",
    [string]$RemoteUser = "ubuntu",
    [string]$SshKey = "$(Join-Path ([Environment]::GetFolderPath('MyDocuments')) '.ssh\NovusKey.key')",
    [string]$RemoteWebsiteRoot = "/srv/PROJECT-NOVUS/Website",
    [string]$RemoteProxyDir = "/srv/proxy"
)

$ErrorActionPreference = "Stop"
$ssh = "C:\Windows\System32\OpenSSH\ssh.exe"
$remote = "$RemoteUser@$RemoteHost"
$remoteCommand = @'
set -e
website_backup=$(ls -1t "{0}"/target/store-web-exec.jar.bak-* 2>/dev/null | head -n1 || true)
proxy_backup=$(ls -1t "{1}"/plugins/Speedrun.jar.bak-* 2>/dev/null | head -n1 || true)
if [ -n "$website_backup" ]; then
  cp "$website_backup" "{0}/target/store-web-exec.jar"
  ENV_FILE="{0}/store-web.env" APP_ROOT="{0}" bash "{0}/stop-store-web.sh" || true
  ENV_FILE="{0}/store-web.env" APP_ROOT="{0}" JAR_PATH="{0}/target/store-web-exec.jar" bash "{0}/start-store-web.sh"
fi
if [ -n "$proxy_backup" ]; then
  cp "$proxy_backup" "{1}/plugins/Speedrun.jar"
  pkill -f 'velocity.jar' || true
  cd "{1}" && nohup bash ./start.sh > logs/proxy-store.out.log 2> logs/proxy-store.err.log < /dev/null &
fi
'@ -f $RemoteWebsiteRoot, $RemoteProxyDir

& $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote $remoteCommand
if ($LASTEXITCODE -ne 0) {
    throw "Rollback failed."
}

Write-Host "Rollback complete."
