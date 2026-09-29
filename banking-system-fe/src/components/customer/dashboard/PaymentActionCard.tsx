import en from "@/locales/en.json";
import { ArrowRight, Wallet } from "lucide-react";
import { Link } from "react-router-dom";

const { payCard } = en.banking.dashboard;

const cardClass =
  "relative flex h-full flex-col justify-between overflow-hidden rounded-3xl bg-gradient-to-br from-dashboard-pay-from via-dashboard-pay-via to-dashboard-pay-to p-8 text-white shadow-sm";

const iconWrapClass =
  "flex h-12 w-12 items-center justify-center rounded-2xl bg-dashboard-pay-accent text-dashboard-pay-accent-foreground";

const eyebrowClass =
  "text-xs font-semibold tracking-wide text-dashboard-pay-accent uppercase";

const titleClass = "mt-2 text-2xl font-semibold tracking-tight";

const descriptionClass = "mt-3 max-w-sm text-sm text-dashboard-pay-foreground-muted/80";

const ctaClass =
  "mt-6 inline-flex w-fit items-center gap-2 rounded-full text-sm font-medium text-white transition hover:underline";

export function PaymentActionCard() {
  return (
    <div className={cardClass}>
      <div>
        <div className={iconWrapClass}>
          <Wallet className="h-6 w-6" />
        </div>
        <p className={eyebrowClass}>{payCard.eyebrow}</p>
        <h2 className={titleClass}>{payCard.title}</h2>
        <p className={descriptionClass}>{payCard.description}</p>
      </div>

      <Link to="/pay" className={ctaClass}>
        {payCard.cta}
        <ArrowRight className="h-4 w-4" />
      </Link>
    </div>
  );
}
