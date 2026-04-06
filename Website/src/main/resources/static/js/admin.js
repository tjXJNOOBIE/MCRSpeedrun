(function () {
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

    async function sendJson(url, method, payload) {
        const response = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json",
                ...csrfHeaders()
            },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            let message = response.statusText || "Request failed";
            try {
                const json = await response.json();
                message = json.message || json.error || message;
            } catch (_error) {
                // ignore
            }
            throw new Error(message);
        }
        return response.status === 204 ? null : response.json();
    }

    function toggleModal(name, open) {
        document.querySelector(`[data-modal="${name}"]`)?.classList.toggle("is-open", open);
    }

    function clearForm(form) {
        form.reset();
        const id = form.querySelector('input[name="id"]');
        if (id) {
            id.value = "";
        }
    }

    function setValue(form, name, value) {
        const field = form.querySelector(`[name="${name}"]`);
        if (!field) {
            return;
        }
        if (field instanceof HTMLInputElement && field.type === "checkbox") {
            field.checked = String(value) === "true";
        } else {
            field.value = value == null ? "" : String(value);
        }
    }

    function fillPackageForm(trigger) {
        const form = document.querySelector("[data-package-form]");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }
        clearForm(form);
        const title = document.querySelector("[data-package-modal-title]");
        if (trigger?.dataset.packageId) {
            if (title) title.textContent = "Edit Product";
            [
                "id", "categorySlug", "packageSlug", "packageName", "packageShort", "packageDescription",
                "packagePrice", "packageType", "packageInterval", "packageAccent", "benefitType",
                "benefitSystem", "benefitKey", "benefitValue", "benefitDuration", "benefitStacking"
            ].forEach((key) => {
                const value = trigger.dataset[key];
                const target = {
                    id: "id",
                    categorySlug: "categorySlug",
                    packageSlug: "slug",
                    packageName: "name",
                    packageShort: "shortDescription",
                    packageDescription: "descriptionHtml",
                    packagePrice: "price",
                    packageType: "packageType",
                    packageInterval: "billingInterval",
                    packageAccent: "accentKey",
                    benefitType: "benefitType",
                    benefitSystem: "targetSystem",
                    benefitKey: "targetKey",
                    benefitValue: "targetValue",
                    benefitDuration: "durationDays",
                    benefitStacking: "stackingPolicy"
                }[key];
                setValue(form, target, value);
            });
            setValue(form, "visible", trigger.dataset.packageVisible);
            setValue(form, "featured", trigger.dataset.packageFeatured);
            setValue(form, "giftable", trigger.dataset.packageGiftable);
        } else if (title) {
            title.textContent = "New Product";
            setValue(form, "visible", true);
            setValue(form, "giftable", true);
        }
    }

    function fillPromotionForm(trigger) {
        const form = document.querySelector("[data-promotion-form]");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }
        clearForm(form);
        const title = document.querySelector("[data-promotion-modal-title]");
        if (trigger?.dataset.promotionId) {
            if (title) title.textContent = "Edit Promotion";
            setValue(form, "id", trigger.dataset.promotionId);
            setValue(form, "name", trigger.dataset.promotionName);
            setValue(form, "slug", trigger.dataset.promotionSlug);
            setValue(form, "status", trigger.dataset.promotionStatus);
            setValue(form, "targetType", trigger.dataset.promotionTargetType);
            setValue(form, "discountType", trigger.dataset.promotionDiscountType);
            setValue(form, "discountValue", trigger.dataset.promotionDiscountValue);
            setValue(form, "categorySlug", trigger.dataset.promotionCategory);
            setValue(form, "packageSlug", trigger.dataset.promotionPackage);
            setValue(form, "startsAt", trigger.dataset.promotionStarts);
            setValue(form, "endsAt", trigger.dataset.promotionEnds);
        } else if (title) {
            title.textContent = "New Promotion";
        }
    }

    function initModalButtons() {
        document.addEventListener("click", (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }
            const openButton = target.closest("[data-modal-open]");
            if (openButton) {
                const name = openButton.getAttribute("data-modal-open");
                if (name === "package-modal") fillPackageForm(openButton);
                if (name === "promotion-modal") fillPromotionForm(openButton);
                toggleModal(name, true);
            }
            const closeButton = target.closest("[data-modal-close]");
            if (closeButton) {
                toggleModal(closeButton.getAttribute("data-modal-close"), false);
            }
            if (target.matches(".modal-shell.is-open")) {
                target.classList.remove("is-open");
            }
        });
    }

    function initPackageForm() {
        const form = document.querySelector("[data-package-form]");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }
        const status = form.querySelector("[data-package-status]");
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const id = String(formData.get("id") || "").trim();
            const payload = {
                categorySlug: formData.get("categorySlug"),
                slug: formData.get("slug"),
                name: formData.get("name"),
                shortDescription: formData.get("shortDescription"),
                descriptionHtml: formData.get("descriptionHtml"),
                price: Number(formData.get("price")),
                visible: form.querySelector('[name="visible"]')?.checked || false,
                featured: form.querySelector('[name="featured"]')?.checked || false,
                giftable: form.querySelector('[name="giftable"]')?.checked || false,
                packageType: formData.get("packageType"),
                billingInterval: formData.get("billingInterval"),
                accentKey: formData.get("accentKey"),
                benefits: [{
                    benefitType: formData.get("benefitType"),
                    targetSystem: formData.get("targetSystem"),
                    targetKey: formData.get("targetKey"),
                    targetValue: formData.get("targetValue"),
                    durationDays: formData.get("durationDays") ? Number(formData.get("durationDays")) : null,
                    stackingPolicy: formData.get("stackingPolicy")
                }]
            };
            try {
                if (status) {
                    status.textContent = "Saving package…";
                    status.className = "form-status";
                }
                await sendJson(id ? appUrl(`/api/v1/admin/packages/${id}`) : appUrl("/api/v1/admin/packages"), id ? "PUT" : "POST", payload);
                window.location.reload();
            } catch (error) {
                if (status) {
                    status.textContent = error.message;
                    status.className = "form-status is-error";
                }
            }
        });
    }

    function initPromotionForm() {
        const form = document.querySelector("[data-promotion-form]");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }
        const status = form.querySelector("[data-promotion-status]");
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const id = String(formData.get("id") || "").trim();
            const payload = {
                name: formData.get("name"),
                slug: formData.get("slug"),
                status: formData.get("status"),
                targetType: formData.get("targetType"),
                discountType: formData.get("discountType"),
                discountValue: Number(formData.get("discountValue")),
                categorySlug: formData.get("categorySlug") || null,
                packageSlug: formData.get("packageSlug") || null,
                startsAt: formData.get("startsAt") || null,
                endsAt: formData.get("endsAt") || null
            };
            try {
                if (status) {
                    status.textContent = "Saving promotion…";
                    status.className = "form-status";
                }
                await sendJson(id ? appUrl(`/api/v1/admin/promotions/${id}`) : appUrl("/api/v1/admin/promotions"), id ? "PUT" : "POST", payload);
                window.location.reload();
            } catch (error) {
                if (status) {
                    status.textContent = error.message;
                    status.className = "form-status is-error";
                }
            }
        });
    }

    function initSupportActions() {
        const status = document.querySelector("[data-admin-action-status]");
        document.addEventListener("click", async (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }
            const retryButton = target.closest("[data-retry-fulfillment]");
            const revokeButton = target.closest("[data-revoke-entitlement]");
            if (!retryButton && !revokeButton) {
                return;
            }

            try {
                if (status) {
                    status.textContent = retryButton ? "Queueing fulfillment retry..." : "Revoking entitlement...";
                    status.className = "form-status";
                }
                if (retryButton) {
                    await sendJson(appUrl(`/api/v1/admin/fulfillment/${retryButton.getAttribute("data-job-id")}/retry`), "POST", {});
                } else if (revokeButton) {
                    await sendJson(appUrl(`/api/v1/admin/entitlements/${revokeButton.getAttribute("data-entitlement-id")}/revoke`), "POST", {
                        reason: "REVOKED_FROM_SUPPORT_DASHBOARD"
                    });
                }
                if (status) {
                    status.textContent = retryButton ? "Fulfillment retry queued." : "Entitlement revoked and queued for fallback application.";
                    status.className = "form-status is-success";
                }
                window.setTimeout(() => window.location.reload(), 900);
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
        initModalButtons();
        initPackageForm();
        initPromotionForm();
        initSupportActions();
    });
})();
