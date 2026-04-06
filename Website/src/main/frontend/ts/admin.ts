const adminBoot = (): void => {
  (window as Window & { lucide?: { createIcons(): void } }).lucide?.createIcons();
};

document.addEventListener("DOMContentLoaded", adminBoot);
