import en from "@/locales/en.json";

const { promoBanner } = en.banking.dashboard;

const wrapClass = "my-12 overflow-hidden rounded-3xl";

export function PromoBanner() {
  return (
    <div className={wrapClass}>
      <img src="/banner_1.png" alt={promoBanner.imageAlt} className="w-full" />
    </div>
  );
}
