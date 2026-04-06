const accountBoot = (): void => {
  (window as Window & { lucide?: { createIcons(): void } }).lucide?.createIcons();
};

document.addEventListener("DOMContentLoaded", accountBoot);
