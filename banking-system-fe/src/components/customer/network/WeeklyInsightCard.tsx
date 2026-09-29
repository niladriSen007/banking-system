import en from "@/locales/en.json";

const { weeklyInsight } = en.network;

// Placeholder metric until a profile-analytics endpoint is available.
const searchAppearances = 32;

const cardClass = "rounded-2xl bg-dashboard-send-accent p-4";

const eyebrowClass = "text-xs font-semibold tracking-wide text-dashboard-send-accent-foreground uppercase";

const messageClass = "mt-2 text-sm font-medium text-foreground";

const ctaClass = "mt-3 block text-xs font-semibold text-dashboard-send-accent-foreground";

export function WeeklyInsightCard() {
  const message = weeklyInsight.message.replace("{count}", String(searchAppearances));

  return (
    <div className={cardClass}>
      <p className={eyebrowClass}>{weeklyInsight.eyebrow}</p>
      <p className={messageClass}>{message}</p>
      <span className={ctaClass}>{weeklyInsight.cta} →</span>
    </div>
  );
}
