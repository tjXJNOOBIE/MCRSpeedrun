(function () {
    const CART_KEY = "novus-store-cart";
    const rawBasePath = document.querySelector('meta[name="app-base-path"]')?.getAttribute("content") || "/";
    const basePath = rawBasePath === "/" ? "" : rawBasePath.replace(/\/$/, "");

    function appUrl(path) {
        return `${basePath}${path.startsWith("/") ? path : `/${path}`}`;
    }

    function csrfHeaders() {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "X-CSRF-TOKEN";
        return token ? { [header]: token } : {};
    }

    async function postJson(url, body) {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...csrfHeaders()
            },
            body: JSON.stringify(body)
        });
        if (!response.ok) {
            let message = "Request failed";
            try {
                const payload = await response.json();
                message = payload.message || payload.error || message;
            } catch (_error) {
                message = response.statusText || message;
            }
            throw new Error(message);
        }
        return response.status === 204 ? null : response.json();
    }

    function getCart() {
        try {
            return JSON.parse(localStorage.getItem(CART_KEY) || "[]");
        } catch (_error) {
            return [];
        }
    }

    function saveCart(cart) {
        localStorage.setItem(CART_KEY, JSON.stringify(cart));
    }

    function addToCart(item) {
        const cart = getCart();
        const existing = cart.find((entry) => entry.packageSlug === item.packageSlug);
        if (existing) {
            existing.quantity += 1;
        } else {
            cart.push({ ...item, quantity: 1 });
        }
        saveCart(cart);
        renderCart();
    }

    function removeFromCart(packageSlug) {
        const cart = getCart().filter((item) => item.packageSlug !== packageSlug);
        saveCart(cart);
        renderCart();
    }

    function renderCart() {
        const cart = getCart();
        document.querySelectorAll("[data-cart-count]").forEach((node) => {
            node.textContent = String(cart.reduce((sum, item) => sum + item.quantity, 0));
        });

        const list = document.querySelector("[data-cart-items]");
        const totalNode = document.querySelector("[data-cart-total]");
        if (!list || !totalNode) {
            return;
        }

        if (!cart.length) {
            list.innerHTML = '<div class="list-row"><div><strong>Cart is empty</strong><span>Add a package to begin checkout.</span></div></div>';
            totalNode.textContent = "$0.00";
            return;
        }

        list.innerHTML = "";
        let total = 0;
        cart.forEach((item) => {
            total += Number(item.packagePrice) * item.quantity;
            const node = document.createElement("article");
            node.className = "cart-item";
            node.innerHTML = `
                <div>
                    <strong>${item.packageName}</strong>
                    <span>${item.quantity} × $${Number(item.packagePrice).toFixed(2)}</span>
                </div>
                <button type="button" data-remove-cart="${item.packageSlug}">Remove</button>
            `;
            list.appendChild(node);
        });
        totalNode.textContent = `$${total.toFixed(2)}`;
    }

    function toggleCart(open) {
        const drawer = document.querySelector("[data-cart-drawer]");
        const overlay = document.querySelector("[data-cart-overlay]");
        if (!drawer || !overlay) {
            return;
        }
        drawer.classList.toggle("is-open", open);
        overlay.classList.toggle("is-visible", open);
    }

    function initCart() {
        renderCart();
        document.querySelectorAll("[data-add-to-cart]").forEach((button) => {
            button.addEventListener("click", () => {
                addToCart({
                    packageSlug: button.getAttribute("data-package-slug"),
                    packageName: button.getAttribute("data-package-name"),
                    packagePrice: button.getAttribute("data-package-price"),
                    packageType: button.getAttribute("data-package-type"),
                    packageInterval: button.getAttribute("data-package-interval")
                });
                toggleCart(true);
            });
        });

        document.addEventListener("click", (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }
            if (target.closest("[data-cart-toggle]")) {
                toggleCart(true);
            }
            if (target.closest("[data-cart-close]") || target.matches("[data-cart-overlay]")) {
                toggleCart(false);
            }
            const removeButton = target.closest("[data-remove-cart]");
            if (removeButton) {
                removeFromCart(removeButton.getAttribute("data-remove-cart"));
            }
        });

        const checkoutForm = document.querySelector("[data-checkout-form]");
        if (checkoutForm instanceof HTMLFormElement) {
            checkoutForm.addEventListener("submit", async (event) => {
                event.preventDefault();
                const status = checkoutForm.querySelector("[data-checkout-status]");
                const cart = getCart();
                if (!cart.length) {
                    if (status) {
                        status.textContent = "Add at least one package before checkout.";
                        status.className = "form-status is-error";
                    }
                    return;
                }
                const formData = new FormData(checkoutForm);
                const payload = {
                    purchaserUsername: String(formData.get("purchaserUsername") || "").trim(),
                    recipientUsername: String(formData.get("recipientUsername") || "").trim(),
                    couponCode: String(formData.get("couponCode") || "").trim() || null,
                    items: cart.map((item) => ({
                        packageSlug: item.packageSlug,
                        quantity: item.quantity
                    }))
                };
                try {
                    if (status) {
                        status.textContent = "Creating checkout session…";
                        status.className = "form-status";
                    }
                    const draft = await postJson(appUrl("/api/v1/checkout/session"), payload);
                    const payment = await postJson(appUrl("/api/v1/payments/create-intent"), { sessionToken: draft.sessionToken });
                    window.location.href = payment.redirectUrl;
                } catch (error) {
                    if (status) {
                        status.textContent = error.message;
                        status.className = "form-status is-error";
                    }
                }
            });
        }
    }

    function attachVerificationForm(form) {
        const status = form.parentElement?.querySelector("[data-verification-status]");
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
                pollVerification(challenge.code, status);
            } catch (error) {
                if (status) {
                    status.textContent = error.message;
                    status.className = "verify-status is-error";
                }
            }
        });
    }

    function pollVerification(code, status) {
        const timer = setInterval(async () => {
            try {
                const response = await fetch(appUrl(`/api/v1/account/verification/challenges/${code}`));
                if (!response.ok) {
                    return;
                }
                const payload = await response.json();
                if (payload.verified) {
                    clearInterval(timer);
                    await postJson(appUrl("/api/v1/account/session"), { code });
                    if (status) {
                        status.textContent = "Ownership verified. Reloading…";
                        status.className = "verify-status is-success";
                    }
                    window.location.reload();
                }
                if (payload.expired) {
                    clearInterval(timer);
                    if (status) {
                        status.textContent = "Verification code expired. Generate a new one.";
                        status.className = "verify-status is-error";
                    }
                }
            } catch (_error) {
                clearInterval(timer);
            }
        }, 3000);
    }

    function initVerificationForms() {
        document.querySelectorAll("[data-verification-form]").forEach((form) => {
            if (form instanceof HTMLFormElement) {
                attachVerificationForm(form);
            }
        });
    }

    function initSupportForm() {
        const form = document.querySelector("[data-support-form]");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }
        const status = form.querySelector("[data-support-status]");
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const payload = Object.fromEntries(formData.entries());
            try {
                const response = await postJson(appUrl("/api/v1/support/tickets"), payload);
                if (status) {
                    status.textContent = response.message || "Support request submitted.";
                    status.className = "form-status is-success";
                }
                form.reset();
            } catch (error) {
                if (status) {
                    status.textContent = error.message;
                    status.className = "form-status is-error";
                }
            }
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        if (window.lucide?.createIcons) {
            window.lucide.createIcons();
        }
        initCart();
        initVerificationForms();
        initSupportForm();
    });
})();
