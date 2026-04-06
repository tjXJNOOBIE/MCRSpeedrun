(function () {
    const rawBasePath = document.querySelector('meta[name="app-base-path"]')?.getAttribute("content") || "/";
    const basePath = rawBasePath === "/" ? "" : rawBasePath.replace(/\/$/, "");

    function appUrl(path) {
        return `${basePath}${path.startsWith("/") ? path : `/${path}`}`;
    }

    async function postJson(url, body) {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "X-CSRF-TOKEN";
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(token ? { [header]: token } : {})
            },
            body: JSON.stringify(body)
        });
        if (!response.ok) {
            throw new Error(response.statusText || "Request failed");
        }
        return response.json();
    }

    function attachVerificationForm(form) {
        const status = form.querySelector("[data-verification-status]") || form.parentElement?.querySelector("[data-verification-status]");
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const username = form.querySelector('input[name="username"]')?.value?.trim();
            if (!username) {
                if (status) {
                    status.textContent = "Enter your Minecraft username.";
                    status.className = "verify-status is-error";
                }
                return;
            }
            try {
                const challenge = await postJson(appUrl("/api/v1/account/verification/challenges"), { username });
                if (status) {
                    status.innerHTML = `Verification code <strong>${challenge.code}</strong> issued. Run <code>/store verify ${challenge.code}</code> in-game.`;
                    status.className = "verify-status";
                }
                pollStatus(challenge.code, status);
            } catch (error) {
                if (status) {
                    status.textContent = error.message;
                    status.className = "verify-status is-error";
                }
            }
        });
    }

    function pollStatus(code, status) {
        const interval = setInterval(async () => {
            const response = await fetch(appUrl(`/api/v1/account/verification/challenges/${code}`));
            if (!response.ok) {
                return;
            }
            const payload = await response.json();
            if (payload.verified) {
                clearInterval(interval);
                await postJson(appUrl("/api/v1/account/session"), { code });
                if (status) {
                    status.textContent = "Ownership verified. Reloading account…";
                    status.className = "verify-status is-success";
                }
                window.location.reload();
            }
            if (payload.expired) {
                clearInterval(interval);
                if (status) {
                    status.textContent = "Verification code expired. Try again.";
                    status.className = "verify-status is-error";
                }
            }
        }, 3000);
    }

    document.addEventListener("DOMContentLoaded", () => {
        if (window.lucide?.createIcons) {
            window.lucide.createIcons();
        }
        document.querySelectorAll("[data-verification-form]").forEach((form) => {
            if (form instanceof HTMLFormElement) {
                attachVerificationForm(form);
            }
        });
    });
})();
