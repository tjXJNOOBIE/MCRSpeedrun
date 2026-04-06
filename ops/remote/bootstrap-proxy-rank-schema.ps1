[CmdletBinding()]
param(
    [string]$RemoteHost = "146.235.232.128",
    [string]$RemoteUser = "ubuntu",
    [string]$SshKey = "$(Join-Path ([Environment]::GetFolderPath('MyDocuments')) '.ssh\NovusKey.key')",
    [string]$RemoteProxyDir = "/srv/proxy"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$ssh = "C:\Windows\System32\OpenSSH\ssh.exe"
$scp = "C:\Windows\System32\OpenSSH\scp.exe"
$remote = "$RemoteUser@$RemoteHost"
$localSql = Join-Path $repoRoot "ops\remote\bootstrap-proxy-rank-schema.sql"
$localBootstrapPy = Join-Path $repoRoot "ops\remote\bootstrap_proxy_rank_schema.py"
$remoteSql = "$RemoteProxyDir/bootstrap-proxy-rank-schema.sql"
$remoteBootstrapPy = "$RemoteProxyDir/bootstrap_proxy_rank_schema.py"

if (-not (Test-Path $localSql)) {
    throw "Missing SQL bootstrap file: $localSql"
}
if (-not (Test-Path $localBootstrapPy)) {
    throw "Missing bootstrap helper: $localBootstrapPy"
}

& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $localSql "${remote}:$remoteSql"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $localBootstrapPy "${remote}:$remoteBootstrapPy"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to upload bootstrap assets."
}

$remoteCommand = "python3 '$remoteBootstrapPy' '$RemoteProxyDir/plugins/X/database.yml' '$remoteSql'"

& $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote $remoteCommand
if ($LASTEXITCODE -ne 0) {
    throw "Failed to apply proxy rank schema bootstrap."
}
