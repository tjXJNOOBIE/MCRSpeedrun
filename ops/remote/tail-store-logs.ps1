[CmdletBinding()]
param(
    [ValidateSet("website", "proxy", "both")]
    [string]$Target = "both",
    [string]$RemoteHost = "146.235.232.128",
    [string]$RemoteUser = "ubuntu",
    [string]$SshKey = "$(Join-Path ([Environment]::GetFolderPath('MyDocuments')) '.ssh\NovusKey.key')",
    [string]$RemoteWebsiteRoot = "/srv/PROJECT-NOVUS/Website",
    [string]$RemoteProxyDir = "/srv/proxy"
)

$ssh = "C:\Windows\System32\OpenSSH\ssh.exe"
$remote = "$RemoteUser@$RemoteHost"

switch ($Target) {
    "website" {
        & $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote "tail -f '$RemoteWebsiteRoot/logs/store-web.out.log' '$RemoteWebsiteRoot/logs/store-web.err.log'"
    }
    "proxy" {
        & $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote "tail -f '$RemoteProxyDir/logs/latest.log' '$RemoteProxyDir/logs/proxy-store.out.log' '$RemoteProxyDir/logs/proxy-store.err.log'"
    }
    default {
        & $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote "tail -f '$RemoteWebsiteRoot/logs/store-web.out.log' '$RemoteWebsiteRoot/logs/store-web.err.log' '$RemoteProxyDir/logs/latest.log' '$RemoteProxyDir/logs/proxy-store.out.log' '$RemoteProxyDir/logs/proxy-store.err.log'"
    }
}
