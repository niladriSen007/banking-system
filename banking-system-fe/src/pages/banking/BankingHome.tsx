import { NetworkActionCard } from "@/components/customer/dashboard/NetworkActionCard";
import { PaymentActionCard } from "@/components/customer/dashboard/PaymentActionCard";
import { DashboardStats } from "@/components/customer/dashboard/DashboardStats";
import { PromoBanner } from "@/components/customer/dashboard/PromoBanner";
import { FeatureLinks } from "@/components/customer/dashboard/FeatureLinks";
import { useAuthStore } from "@/features/auth/store/auth.store";
import en from "@/locales/en.json";
import { ShieldCheck } from "lucide-react";

const { dashboard } = en.banking;

const headerRowClass =
  "flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between";

const eyebrowClass =
  "text-xs font-semibold tracking-wide text-dashboard-status uppercase";

const titleClass = "mt-1 text-3xl font-semibold tracking-tight text-foreground";

const subtitleClass = "mt-1 text-sm text-muted-foreground";

const statusBadgeClass =
  "inline-flex h-8 shrink-0 items-center gap-1.5 rounded-full border border-border/60 bg-white px-3 text-xs font-medium text-foreground";

const cardsGridClass = "mt-8 grid grid-cols-1 gap-4 lg:grid-cols-2";

function getGreeting(hour: number) {
  if (hour < 12) return dashboard.greeting.morning;
  if (hour < 18) return dashboard.greeting.afternoon;
  return dashboard.greeting.evening;
}

const BankingHome = () => {
  const { user } = useAuthStore();
  const firstName = user?.name?.split(" ").at(0) || "";
  const greeting = getGreeting(new Date().getHours()).replace(
    "{name}",
    firstName,
  );

  return (
    <div>
      <div className={headerRowClass}>
        <div>
          <p className={eyebrowClass}>{dashboard.eyebrow}</p>
          <h1 className={titleClass}>{greeting}</h1>
          <p className={subtitleClass}>{dashboard.subtitle}</p>
        </div>

        <span className={statusBadgeClass}>
          <ShieldCheck className="h-3.5 w-3.5 text-dashboard-status" />
          {dashboard.statusBadge}
        </span>
      </div>

      <div className={cardsGridClass}>
        <PaymentActionCard />
        <NetworkActionCard />
      </div>

      <div className="mt-4">
        <DashboardStats />
      </div>

      <PromoBanner />

      <FeatureLinks />
    </div>
  );
};
export default BankingHome;
