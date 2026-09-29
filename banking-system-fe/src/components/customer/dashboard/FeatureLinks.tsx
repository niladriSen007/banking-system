import en from "@/locales/en.json";
import {
  Briefcase,
  Lightbulb,
  Send,
  Users,
  type LucideIcon,
  ArrowRight,
} from "lucide-react";
import { Link } from "react-router-dom";

const { featureLinks } = en.banking.dashboard;

const iconByHref: Record<string, { icon: LucideIcon; wrapClass: string; iconClass: string }> = {
  "/pay": {
    icon: Send,
    wrapClass: "bg-dashboard-send-accent",
    iconClass: "text-dashboard-send-accent-foreground",
  },
  "/network": {
    icon: Users,
    wrapClass: "bg-dashboard-network-accent",
    iconClass: "text-dashboard-network-accent-foreground",
  },
  "/opportunities": {
    icon: Briefcase,
    wrapClass: "bg-dashboard-opportunities-accent",
    iconClass: "text-dashboard-opportunities-accent-foreground",
  },
  "/learn": {
    icon: Lightbulb,
    wrapClass: "bg-dashboard-learn-accent",
    iconClass: "text-dashboard-learn-accent-foreground",
  },
};

const sectionClass = "mt-10";

const headerTitleClass = "text-xl font-semibold tracking-tight text-foreground";

const headerSubtitleClass = "mt-1 text-sm text-muted-foreground";

const gridClass = "mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4";

const itemClass =
  "flex items-start justify-between gap-3 rounded-2xl border border-border/60 bg-white p-5 transition hover:border-border";

const iconWrapClass = "flex h-10 w-10 shrink-0 items-center justify-center rounded-xl";

const itemTitleClass = "text-sm font-semibold text-foreground";

const itemDescriptionClass = "mt-1 text-xs text-muted-foreground";

const arrowWrapClass =
  "flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-border/60 text-foreground";

export function FeatureLinks() {
  return (
    <section className={sectionClass}>
      <h2 className={headerTitleClass}>{featureLinks.title}</h2>
      <p className={headerSubtitleClass}>{featureLinks.subtitle}</p>

      <div className={gridClass}>
        {featureLinks.items.map((item) => {
          const iconConfig = iconByHref[item.href];
          const Icon = iconConfig?.icon ?? Send;

          return (
            <Link key={item.href} to={item.href} className={itemClass}>
              <div className="flex items-start gap-3">
                <div className={`${iconWrapClass} ${iconConfig?.wrapClass ?? ""}`}>
                  <Icon className={`h-5 w-5 ${iconConfig?.iconClass ?? ""}`} />
                </div>
                <div>
                  <p className={itemTitleClass}>{item.title}</p>
                  <p className={itemDescriptionClass}>{item.description}</p>
                </div>
              </div>

              <span className={arrowWrapClass}>
                <ArrowRight className="h-4 w-4" />
              </span>
            </Link>
          );
        })}
      </div>
    </section>
  );
}
