import axios, { type AxiosInstance } from "axios";
import mineflayer from "mineflayer";
import { execFile } from "child_process";
import { once } from "events";
import net from "net";
import { promisify } from "util";

type PlayerLookup = {
  uuid: string;
  username: string;
};

type CheckoutDraft = {
  sessionToken: string;
  orderNumber: string;
};

type IntegrationAuthResponse = {
  accessToken: string;
  expiresAtEpochSecond: number;
};

type PendingFulfillmentJobView = {
  jobId: number;
  playerUuid: string;
  playerUsername: string;
  targetValue: string;
  correlationId: string;
};

type JobDiagnostics = {
  id: number;
  status: string;
  retryCount: number;
  lastError: string | null;
  correlationId: string;
  idempotencyKey: string;
  nextAttemptAt: string | null;
  lastAttemptAt: string | null;
};

type EntitlementDiagnostics = {
  id: number;
  benefitType: string;
  targetSystem: string;
  targetKey: string;
  targetValue: string;
  state: string;
  effectiveAt: string;
  revokedAt: string | null;
  revocationReason: string | null;
  jobs: JobDiagnostics[];
};

type PaymentDiagnostics = {
  id: number;
  provider: string;
  status: string;
  providerReference: string | null;
  providerEventId: string | null;
  processedAt: string | null;
};

type TestOrderDiagnostics = {
  orderNumber: string;
  state: string;
  purchaser: string;
  recipient: string;
  paidAt: string | null;
  payments: PaymentDiagnostics[];
  entitlements: EntitlementDiagnostics[];
};

type BotSession = {
  bot: mineflayer.Bot;
  username: string;
  messages: string[];
};

type ScenarioResult = {
  name: string;
  orderNumber?: string;
  username?: string;
  notes: string[];
};

const execFileAsync = promisify(execFile);

const apiBaseUrl = process.env.STORE_API_BASE_URL || "http://127.0.0.1:8080";
const proxyHost = process.env.STORE_PROXY_HOST || "127.0.0.1";
const proxyPort = Number(process.env.STORE_PROXY_PORT || 25565);
const minecraftVersion = process.env.STORE_MINECRAFT_VERSION || "1.21.4";
const botUsername = process.env.STORE_BOT_USERNAME || "StoreBot01";
const giftBotUsername = process.env.STORE_GIFT_BOT_USERNAME || "StoreGift01";
const giftBuyerUsername = process.env.STORE_GIFT_BUYER_USERNAME || "StoreBuyer01";
const retryBotUsername = process.env.STORE_RETRY_BOT_USERNAME || "StoreRetry01";
const revokeBotUsername = process.env.STORE_REVOKE_BOT_USERNAME || "StoreRevoke01";
const packageSlug = process.env.STORE_PACKAGE_SLUG || "god-rank-lifetime";
const expectedRank = process.env.STORE_EXPECTED_RANK || "God";
const fallbackRank = process.env.STORE_FALLBACK_RANK || "Member";
const bootstrapSecret = process.env.STORE_BOOTSTRAP_SECRET;
const integrationNodeId = process.env.STORE_TEST_NODE_ID || "store-e2e";
const timeoutMs = Number(process.env.STORE_E2E_TIMEOUT_MS || 60000);
const scenarios = (process.env.STORE_E2E_SCENARIOS
  || "happy-path,duplicate-settle,gift-purchase,invalid-recipient,proxy-retry,revoke-rank")
  .split(",")
  .map((entry) => entry.trim().toLowerCase())
  .filter(Boolean);
const proxyDir = process.env.STORE_PROXY_DIR || "/srv/proxy";
const proxyStartCommand = process.env.STORE_PROXY_START_COMMAND || "./start.sh";
const proxyRestartCommand = process.env.STORE_PROXY_RESTART_COMMAND || "/srv/proxy/restart-proxy-with-store-env.sh";
const proxyLogPath = process.env.STORE_PROXY_LOG_PATH || "/tmp/novus-store-proxy.log";
const proxyProcessMatch = process.env.STORE_PROXY_PROCESS_MATCH || "velocity.jar";

if (!bootstrapSecret) {
  throw new Error("STORE_BOOTSTRAP_SECRET is required.");
}

const http = axios.create({
  baseURL: apiBaseUrl,
  timeout: 10000,
  maxRedirects: 0,
  validateStatus: () => true
});

let activeBot: BotSession | undefined;

function log(message: string): void {
  console.log(`[store-e2e] ${message}`);
}

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function shQuote(value: string): string {
  return `'${value.replace(/'/g, `'\\''`)}'`;
}

async function runShell(command: string): Promise<void> {
  await execFileAsync("bash", ["-lc", command], { timeout: 30000 });
}

function isTcpOpen(host: string, port: number): Promise<boolean> {
  return new Promise((resolve) => {
    const socket = new net.Socket();
    let settled = false;
    const finish = (value: boolean) => {
      if (!settled) {
        settled = true;
        socket.destroy();
        resolve(value);
      }
    };
    socket.setTimeout(1500);
    socket.once("connect", () => finish(true));
    socket.once("timeout", () => finish(false));
    socket.once("error", () => finish(false));
    socket.connect(port, host);
  });
}

async function waitFor<T>(
  label: string,
  supplier: () => Promise<T | undefined>,
  maxWaitMs = timeoutMs,
  intervalMs = 1500
): Promise<T> {
  const started = Date.now();
  while (Date.now() - started < maxWaitMs) {
    const value = await supplier();
    if (value !== undefined) {
      return value;
    }
    await sleep(intervalMs);
  }
  throw new Error(`Timed out waiting for ${label}`);
}

function updateCookieJar(cookieJar: Map<string, string>, setCookies: string[] | undefined): void {
  for (const cookie of setCookies || []) {
    const [pair] = cookie.split(";", 1);
    const separator = pair.indexOf("=");
    if (separator <= 0) {
      continue;
    }
    cookieJar.set(pair.slice(0, separator), pair.slice(separator + 1));
  }
}

function cookieHeader(cookieJar: Map<string, string>): string {
  return Array.from(cookieJar.entries())
    .map(([name, value]) => `${name}=${value}`)
    .join("; ");
}

function decodeCookie(value: string | undefined): string {
  return value ? decodeURIComponent(value) : "";
}

async function authenticateIntegration(): Promise<AxiosInstance> {
  const response = await http.post<IntegrationAuthResponse>("/api/v1/integration/auth/token", {
    nodeId: integrationNodeId,
    bootstrapSecret
  });
  if (response.status >= 400 || !response.data?.accessToken) {
    throw new Error(`Integration auth failed: HTTP ${response.status}`);
  }
  return axios.create({
    baseURL: apiBaseUrl,
    timeout: 10000,
    maxRedirects: 0,
    headers: {
      Authorization: `Bearer ${response.data.accessToken}`
    },
    validateStatus: () => true
  });
}

async function lookupPlayer(username: string): Promise<PlayerLookup> {
  const response = await http.get<PlayerLookup>("/api/v1/players/lookup", {
    params: { username }
  });
  if (response.status >= 400) {
    throw new Error(`Player lookup failed for ${username}: HTTP ${response.status}`);
  }
  return response.data;
}

async function createCheckout(purchaserUsername: string, recipientUsername: string): Promise<CheckoutDraft> {
  const response = await http.post<CheckoutDraft>("/api/v1/checkout/session", {
    purchaserUsername,
    recipientUsername,
    items: [{ packageSlug, quantity: 1 }]
  });
  if (response.status >= 400 || !response.data?.orderNumber) {
    throw new Error(`Checkout session creation failed for ${recipientUsername}: HTTP ${response.status}`);
  }
  return response.data;
}

async function createCheckoutExpectBadRequest(purchaserUsername: string, recipientUsername: string): Promise<void> {
  const response = await http.post("/api/v1/checkout/session", {
    purchaserUsername,
    recipientUsername,
    items: [{ packageSlug, quantity: 1 }]
  });
  if (response.status !== 400) {
    throw new Error(`Expected invalid recipient to return HTTP 400 but got ${response.status}`);
  }
}

async function settleOrder(integration: AxiosInstance, orderNumber: string): Promise<void> {
  const response = await integration.post(`/api/v1/integration/test/orders/${orderNumber}/settle`);
  if (response.status >= 400) {
    throw new Error(`Test settlement failed for ${orderNumber}: HTTP ${response.status}`);
  }
}

async function fetchOrderDiagnostics(integration: AxiosInstance, orderNumber: string): Promise<TestOrderDiagnostics> {
  const response = await integration.get<TestOrderDiagnostics>(`/api/v1/integration/test/orders/${orderNumber}`);
  if (response.status >= 400 || !response.data?.orderNumber) {
    throw new Error(`Unable to fetch diagnostics for ${orderNumber}: HTTP ${response.status}`);
  }
  return response.data;
}

async function waitForOrderDiagnostics(
  integration: AxiosInstance,
  orderNumber: string,
  label: string,
  predicate: (diagnostics: TestOrderDiagnostics) => boolean,
  intervalMs = 1500
): Promise<TestOrderDiagnostics> {
  return waitFor(label, async () => {
    const diagnostics = await fetchOrderDiagnostics(integration, orderNumber);
    return predicate(diagnostics) ? diagnostics : undefined;
  }, timeoutMs, intervalMs);
}

function findRankEntitlement(diagnostics: TestOrderDiagnostics, rankValue = expectedRank): EntitlementDiagnostics | undefined {
  return diagnostics.entitlements.find((entry) => entry.targetValue === rankValue);
}

function latestJob(entitlement: EntitlementDiagnostics | undefined): JobDiagnostics | undefined {
  return entitlement?.jobs?.[0];
}

async function connectBot(username: string): Promise<BotSession> {
  await closeBot();

  const bot = mineflayer.createBot({
    host: proxyHost,
    port: proxyPort,
    username,
    auth: "offline",
    version: minecraftVersion,
    checkTimeoutInterval: 60000
  });
  const messages: string[] = [];
  const session: BotSession = { bot, username, messages };
  activeBot = session;

  bot.on("messagestr", (message) => {
    messages.push(message);
    console.log(`[bot:${username}] ${message}`);
  });
  bot.on("error", (error) => console.log(`[bot:${username}:error] ${error.message}`));
  bot.on("kicked", (reason) => console.log(`[bot:${username}:kicked] ${reason}`));

  await Promise.race([
    once(bot, "spawn"),
    once(bot, "error").then(([error]) => {
      throw error instanceof Error ? error : new Error(String(error));
    }),
    once(bot, "kicked").then(([reason]) => {
      throw new Error(`Bot ${username} was kicked: ${String(reason)}`);
    })
  ]);

  await sleep(1000);
  return session;
}

async function closeBot(): Promise<void> {
  if (!activeBot) {
    return;
  }
  try {
    activeBot.bot.quit();
  } catch (error) {
    console.error("[store-e2e] bot quit failure", error instanceof Error ? error.message : error);
  } finally {
    activeBot = undefined;
  }
  await sleep(1000);
}

async function waitForRankMessage(username: string, targetRank: string): Promise<void> {
  const session = activeBot?.username === username ? activeBot : await connectBot(username);
  await waitFor(`proxy rank ${targetRank} for ${username}`, async () => {
    const baseline = session.messages.length;
    session.bot.chat("/store purchases");
    await sleep(1800);
    const freshMessages = session.messages.slice(baseline);
    return freshMessages.some((message) => message.includes(`Current proxy rank: ${targetRank}`)) ? true : undefined;
  }, timeoutMs, 2500);
}

async function stopProxy(): Promise<void> {
  log("stopping proxy");
  await closeBot();
  const command = `
    pids=""
    if command -v lsof >/dev/null 2>&1; then
      pids=$(lsof -ti tcp:${proxyPort} || true)
    fi
    if [ -z "$pids" ]; then
      pids=$(pgrep -f ${shQuote(proxyProcessMatch)} || true)
    fi
    if [ -n "$pids" ]; then
      kill $pids || true
    fi
  `;
  await runShell(command);
  await waitFor("proxy to stop", async () => !(await isTcpOpen(proxyHost, proxyPort)) ? true : undefined, 30000, 1000);
}

async function startProxy(): Promise<void> {
  if (await isTcpOpen(proxyHost, proxyPort)) {
    return;
  }
  log("starting proxy");
  const command = proxyRestartCommand
    ? proxyRestartCommand
    : `cd ${shQuote(proxyDir)} && nohup bash -lc ${shQuote(proxyStartCommand)} > ${shQuote(proxyLogPath)} 2>&1 &`;
  await runShell(command);
  await waitFor("proxy to start", async () => await isTcpOpen(proxyHost, proxyPort) ? true : undefined, 45000, 1000);
}

async function ensureProxyOnline(): Promise<void> {
  if (!(await isTcpOpen(proxyHost, proxyPort))) {
    await startProxy();
  }
}

async function completeOrderAndVerify(
  integration: AxiosInstance,
  purchaserUsername: string,
  recipientUsername: string,
  verifyUsername: string
): Promise<{ player: PlayerLookup; checkout: CheckoutDraft; diagnostics: TestOrderDiagnostics }> {
  const player = await lookupPlayer(recipientUsername);
  const checkout = await createCheckout(purchaserUsername, recipientUsername);
  log(`created order ${checkout.orderNumber} for ${recipientUsername}`);

  await settleOrder(integration, checkout.orderNumber);
  const diagnostics = await waitForOrderDiagnostics(
    integration,
    checkout.orderNumber,
    `order ${checkout.orderNumber} fulfillment completion`,
    (entry) => {
      const entitlement = findRankEntitlement(entry);
      const job = latestJob(entitlement);
      return entry.state === "PAID"
        && entitlement?.state === "ACTIVE"
        && job?.status === "COMPLETED";
    },
    2000
  );

  await waitForRankMessage(verifyUsername, expectedRank);
  return { player, checkout, diagnostics };
}

async function runHappyPath(integration: AxiosInstance): Promise<ScenarioResult> {
  await ensureProxyOnline();
  await connectBot(botUsername);
  const result = await completeOrderAndVerify(integration, botUsername, botUsername, botUsername);
  return {
    name: "happy-path",
    orderNumber: result.checkout.orderNumber,
    username: botUsername,
    notes: [
      `order=${result.checkout.orderNumber}`,
      `entitlements=${result.diagnostics.entitlements.length}`,
      `payments=${result.diagnostics.payments.length}`
    ]
  };
}

async function runDuplicateSettle(integration: AxiosInstance): Promise<ScenarioResult> {
  await ensureProxyOnline();
  await connectBot(botUsername);
  const checkout = await createCheckout(botUsername, botUsername);
  log(`created duplicate-settle order ${checkout.orderNumber}`);

  await settleOrder(integration, checkout.orderNumber);
  await settleOrder(integration, checkout.orderNumber);

  const diagnostics = await waitForOrderDiagnostics(
    integration,
    checkout.orderNumber,
    `duplicate settlement reconciliation for ${checkout.orderNumber}`,
    (entry) => {
      const entitlement = findRankEntitlement(entry);
      const job = latestJob(entitlement);
      return entry.state === "PAID"
        && entry.payments.length === 1
        && entitlement !== undefined
        && entitlement.jobs.length === 1
        && job?.status === "COMPLETED";
    },
    2000
  );

  await waitForRankMessage(botUsername, expectedRank);
  return {
    name: "duplicate-settle",
    orderNumber: checkout.orderNumber,
    username: botUsername,
    notes: [
      `paymentRows=${diagnostics.payments.length}`,
      `jobCount=${findRankEntitlement(diagnostics)?.jobs.length ?? 0}`,
      "duplicate settle preserved a single payment row and single fulfillment job"
    ]
  };
}

async function runGiftPurchase(integration: AxiosInstance): Promise<ScenarioResult> {
  await ensureProxyOnline();
  const result = await completeOrderAndVerify(integration, giftBuyerUsername, giftBotUsername, giftBotUsername);
  if (result.diagnostics.purchaser.toLowerCase() !== giftBuyerUsername.toLowerCase()
    || result.diagnostics.recipient.toLowerCase() !== giftBotUsername.toLowerCase()) {
    throw new Error("Gift purchase diagnostics did not preserve purchaser/recipient separation.");
  }
  return {
    name: "gift-purchase",
    orderNumber: result.checkout.orderNumber,
    username: giftBotUsername,
    notes: [
      `purchaser=${result.diagnostics.purchaser}`,
      `recipient=${result.diagnostics.recipient}`
    ]
  };
}

async function runInvalidRecipient(): Promise<ScenarioResult> {
  await createCheckoutExpectBadRequest(botUsername, "bad*recipient");
  return {
    name: "invalid-recipient",
    notes: ["invalid recipient request returned HTTP 400"]
  };
}

async function runProxyRetry(integration: AxiosInstance): Promise<ScenarioResult> {
  await stopProxy();

  const checkout = await createCheckout(retryBotUsername, retryBotUsername);
  log(`created retry order ${checkout.orderNumber} while proxy was offline`);
  await settleOrder(integration, checkout.orderNumber);

  const pendingDiagnostics = await waitForOrderDiagnostics(
    integration,
    checkout.orderNumber,
    `pending job for ${checkout.orderNumber}`,
    (entry) => {
      const entitlement = findRankEntitlement(entry);
      const job = latestJob(entitlement);
      return entry.state === "PAID"
        && entitlement?.state === "ACTIVE"
        && job?.status === "PENDING";
    }
  );

  const entitlement = findRankEntitlement(pendingDiagnostics);
  const job = latestJob(entitlement);
  if (!job) {
    throw new Error("No fulfillment job was available for proxy-retry scenario.");
  }

  const failResponse = await integration.post(`/api/v1/integration/fulfillment/${job.id}/fail`, {
    correlationId: job.correlationId,
    error: "Simulated proxy outage during regression test",
    retryable: true
  });
  if (failResponse.status >= 400) {
    throw new Error(`Unable to simulate retryable failure: HTTP ${failResponse.status}`);
  }

  await waitForOrderDiagnostics(
    integration,
    checkout.orderNumber,
    `retry-ready job for ${checkout.orderNumber}`,
    (entry) => latestJob(findRankEntitlement(entry))?.status === "RETRY_READY"
  );

  const retryResponse = await integration.post(`/api/v1/integration/test/fulfillment/${job.id}/retry`);
  if (retryResponse.status >= 400) {
    throw new Error(`Test retry failed: HTTP ${retryResponse.status}`);
  }

  await startProxy();
  await connectBot(retryBotUsername);

  const completedDiagnostics = await waitForOrderDiagnostics(
    integration,
    checkout.orderNumber,
    `proxy retry completion for ${checkout.orderNumber}`,
    (entry) => latestJob(findRankEntitlement(entry))?.status === "COMPLETED",
    2000
  );

  await waitForRankMessage(retryBotUsername, expectedRank);
  return {
    name: "proxy-retry",
    orderNumber: checkout.orderNumber,
    username: retryBotUsername,
    notes: [
      `retryCount=${latestJob(findRankEntitlement(completedDiagnostics))?.retryCount ?? 0}`,
      "simulated retryable failure recovered after admin retry and proxy restart"
    ]
  };
}

async function runRevokeRank(integration: AxiosInstance): Promise<ScenarioResult> {
  await ensureProxyOnline();
  await connectBot(revokeBotUsername);
  const result = await completeOrderAndVerify(integration, revokeBotUsername, revokeBotUsername, revokeBotUsername);
  const entitlement = findRankEntitlement(result.diagnostics);
  if (!entitlement) {
    throw new Error("No active rank entitlement was found for revoke scenario.");
  }

  const revokeResponse = await integration.post(`/api/v1/integration/test/entitlements/${entitlement.id}/revoke`, {
    reason: "regression-revoke"
  });
  if (revokeResponse.status >= 400) {
    throw new Error(`Test revoke failed: HTTP ${revokeResponse.status}`);
  }

  const revokedDiagnostics = await waitForOrderDiagnostics(
    integration,
    result.checkout.orderNumber,
    `revoke completion for ${result.checkout.orderNumber}`,
    (entry) => {
      const rankEntitlement = findRankEntitlement(entry);
      const job = latestJob(rankEntitlement);
      return rankEntitlement?.state === "REVOKED" && job?.status === "COMPLETED";
    },
    2000
  );

  await waitForRankMessage(revokeBotUsername, fallbackRank);
  return {
    name: "revoke-rank",
    orderNumber: result.checkout.orderNumber,
    username: revokeBotUsername,
    notes: [
      `revocationReason=${findRankEntitlement(revokedDiagnostics)?.revocationReason ?? ""}`,
      `fallbackRank=${fallbackRank}`
    ]
  };
}

async function runScenario(name: string, integration: AxiosInstance): Promise<ScenarioResult> {
  switch (name) {
    case "happy-path":
      return runHappyPath(integration);
    case "duplicate-settle":
      return runDuplicateSettle(integration);
    case "gift-purchase":
      return runGiftPurchase(integration);
    case "invalid-recipient":
      return runInvalidRecipient();
    case "proxy-retry":
      return runProxyRetry(integration);
    case "revoke-rank":
      return runRevokeRank(integration);
    default:
      throw new Error(`Unknown scenario: ${name}`);
  }
}

async function main(): Promise<void> {
  log(`api=${apiBaseUrl} proxy=${proxyHost}:${proxyPort} scenarios=${scenarios.join(",")}`);

  const integration = await authenticateIntegration();

  const results: ScenarioResult[] = [];
  try {
    await ensureProxyOnline();
    for (const scenario of scenarios) {
      log(`starting scenario ${scenario}`);
      const result = await runScenario(scenario, integration);
      results.push(result);
      log(`completed scenario ${scenario}${result.orderNumber ? ` order=${result.orderNumber}` : ""}`);
    }

    log("REGRESSION MATRIX SUCCESS");
    for (const result of results) {
      console.log(JSON.stringify(result));
    }
    await closeBot();
    process.exit(0);
  } finally {
    await ensureProxyOnline();
  }
}

main().catch(async (error) => {
  await closeBot();
  try {
    await ensureProxyOnline();
  } catch (recoveryError) {
    console.error("[store-e2e] proxy recovery failure", recoveryError instanceof Error ? recoveryError.message : recoveryError);
  }
  console.error("[store-e2e] FAILURE", error instanceof Error ? error.message : error);
  setTimeout(() => process.exit(1), 500);
});
