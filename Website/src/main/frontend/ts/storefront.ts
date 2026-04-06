type CartItem = {
  packageSlug: string;
  packageName: string;
  packagePrice: string;
  packageType: string;
  packageInterval: string;
  quantity: number;
};

const CART_KEY = "novus-store-cart";

const csrfHeaders = (): Record<string, string> => {
  const token = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
  const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content") || "X-CSRF-TOKEN";
  return token ? { [header]: token } : {};
};

const postJson = async <T>(url: string, body: unknown): Promise<T> => {
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
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }
  return response.json() as Promise<T>;
};

const getCart = (): CartItem[] => {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY) || "[]") as CartItem[];
  } catch {
    return [];
  }
};

const saveCart = (cart: CartItem[]): void => localStorage.setItem(CART_KEY, JSON.stringify(cart));

const renderCart = (): void => {
  const cart = getCart();
  document.querySelectorAll<HTMLElement>("[data-cart-count]").forEach((node) => {
    node.textContent = String(cart.reduce((sum, item) => sum + item.quantity, 0));
  });
};

document.addEventListener("DOMContentLoaded", () => {
  (window as Window & { lucide?: { createIcons(): void } }).lucide?.createIcons();
  renderCart();
});
