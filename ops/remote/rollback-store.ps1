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
website_backup=$(find "{0}/distribution/backups" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort -r | head -n1 || true)
proxy_backup=$(find "{1}/plugins/backups" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort -r | head -n1 || true)
if [ -n "$website_backup" ] && [ -f "$website_backup/application.jar" ]; then
  cp "$website_backup/application.jar" "{0}/distribution/application.jar"
  rm -rf "{0}/distribution/libs"
  cp -a "$website_backup/libs" "{0}/distribution/libs"
  ENV_FILE="{0}/store-web.env" APP_ROOT="{0}" bash "{0}/stop-store-web.sh" || true
  ENV_FILE="{0}/store-web.env" APP_ROOT="{0}" bash "{0}/start-store-web.sh"
fi
if [ -n "$proxy_backup" ] && [ -f "$proxy_backup/Speedrun.jar" ]; then
  cp "$proxy_backup/Speedrun.jar" "{1}/plugins/Speedrun.jar"
  rm -rf "{1}/plugins/libs"
  cp -a "$proxy_backup/libs" "{1}/plugins/libs"
  PROXY_DIR="{1}" WEBSITE_ENV_FILE="{0}/store-web.env" bash "{1}/restart-proxy-with-store-env.sh"
fi
'@ -f $RemoteWebsiteRoot, $RemoteProxyDir

& $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote $remoteCommand
if ($LASTEXITCODE -ne 0) {
    throw "Rollback failed."
}

Write-Host "Rollback complete."
