[CmdletBinding()]
param(
    [string]$RemoteHost = "146.235.232.128",
    [string]$RemoteUser = "ubuntu",
    [string]$SshKey = "$(Join-Path ([Environment]::GetFolderPath('MyDocuments')) '.ssh\NovusKey.key')",
    [string]$RemoteHeadlessSourceDir = "/srv/headless/bot-testing/src",
    [string]$RemoteEnvFile = "/srv/PROJECT-NOVUS/Website/store-web.env",
    [string]$StoreApiBaseUrl = "http://127.0.0.1:8081",
    [string]$StoreProxyHost = "127.0.0.1",
    [int]$StoreProxyPort = 25565,
    [string]$MinecraftVersion = "1.21.4",
    [string]$BotUsername = "StoreBot01",
    [string]$GiftBotUsername = "StoreGift01",
    [string]$GiftBuyerUsername = "StoreBuyer01",
    [string]$RetryBotUsername = "StoreRetry01",
    [string]$RevokeBotUsername = "StoreRevoke01",
    [string]$PackageSlug = "god-rank-lifetime",
    [string]$ExpectedRank = "God",
    [string]$FallbackRank = "Member",
    [string]$Scenarios = "happy-path,duplicate-settle,gift-purchase,invalid-recipient,proxy-retry,revoke-rank",
    [string]$ProxyDir = "/srv/proxy",
    [string]$ProxyStartCommand = "./start.sh",
    [string]$ProxyRestartCommand = "/srv/proxy/restart-proxy-with-store-env.sh",
    [string]$ProxyProcessMatch = "velocity.jar",
    [string]$BootstrapSecret = $env:APP_STORE_INTEGRATION_BOOTSTRAP_SECRET
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$scriptPath = Join-Path $repoRoot "ops\headless\store-e2e.ts"
$whitelistScriptPath = Join-Path $repoRoot "ops\remote\ensure-proxy-whitelist-users.sh"
$ssh = "C:\Windows\System32\OpenSSH\ssh.exe"
$scp = "C:\Windows\System32\OpenSSH\scp.exe"
$remote = "$RemoteUser@$RemoteHost"
$bootstrapSecretValue = if ([string]::IsNullOrWhiteSpace($BootstrapSecret)) { "" } else { $BootstrapSecret.Replace("'", "'""'""'") }
$proxyStartCommandValue = $ProxyStartCommand.Replace("'", "'""'""'")
$proxyRestartCommandValue = $ProxyRestartCommand.Replace("'", "'""'""'")
$proxyProcessMatchValue = $ProxyProcessMatch.Replace("'", "'""'""'")

& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $scriptPath "${remote}:$RemoteHeadlessSourceDir/store-e2e.ts"
& $scp -i $SshKey -o StrictHostKeyChecking=accept-new $whitelistScriptPath "${remote}:/srv/proxy/ensure-proxy-whitelist-users.sh"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to upload remote regression scripts"
}

& $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote "chmod +x /srv/proxy/ensure-proxy-whitelist-users.sh && /srv/proxy/ensure-proxy-whitelist-users.sh '$BotUsername' '$GiftBotUsername' '$GiftBuyerUsername' '$RetryBotUsername' '$RevokeBotUsername'"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to whitelist regression bot usernames"
}

$remoteCommand = @"
set -a
if [ -f '$RemoteEnvFile' ]; then
  . '$RemoteEnvFile'
fi
set +a
BOOTSTRAP_SECRET='$bootstrapSecretValue'
if [ -z "`$BOOTSTRAP_SECRET" ]; then
  BOOTSTRAP_SECRET="`$APP_STORE_INTEGRATION_BOOTSTRAP_SECRET"
fi
if [ -z "`$BOOTSTRAP_SECRET" ]; then
  echo 'Bootstrap secret not available from local input or remote env.' >&2
  exit 1
fi
cd /srv/headless
STORE_API_BASE_URL='$StoreApiBaseUrl' \
STORE_PROXY_HOST='$StoreProxyHost' \
STORE_PROXY_PORT='$StoreProxyPort' \
STORE_MINECRAFT_VERSION='$MinecraftVersion' \
STORE_BOT_USERNAME='$BotUsername' \
STORE_GIFT_BOT_USERNAME='$GiftBotUsername' \
STORE_GIFT_BUYER_USERNAME='$GiftBuyerUsername' \
STORE_RETRY_BOT_USERNAME='$RetryBotUsername' \
STORE_REVOKE_BOT_USERNAME='$RevokeBotUsername' \
STORE_PACKAGE_SLUG='$PackageSlug' \
STORE_EXPECTED_RANK='$ExpectedRank' \
STORE_FALLBACK_RANK='$FallbackRank' \
STORE_E2E_SCENARIOS='$Scenarios' \
STORE_PROXY_DIR='$ProxyDir' \
STORE_PROXY_START_COMMAND='$proxyStartCommandValue' \
STORE_PROXY_RESTART_COMMAND='$proxyRestartCommandValue' \
STORE_PROXY_PROCESS_MATCH='$proxyProcessMatchValue' \
STORE_ADMIN_USERNAME="`$APP_STORE_LOCAL_ADMIN_SEED_USERNAME" \
STORE_ADMIN_PASSWORD="`$APP_STORE_LOCAL_ADMIN_SEED_PASSWORD" \
STORE_BOOTSTRAP_SECRET="`$BOOTSTRAP_SECRET" \
npx tsx '$RemoteHeadlessSourceDir/store-e2e.ts'
"@

& $ssh -i $SshKey -o StrictHostKeyChecking=accept-new $remote $remoteCommand
if ($LASTEXITCODE -ne 0) {
    throw "Remote store E2E run failed."
}
