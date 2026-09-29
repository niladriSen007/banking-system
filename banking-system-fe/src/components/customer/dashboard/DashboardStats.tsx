import { StatCard } from "@/components/customer/dashboard/StatCard";
import en from "@/locales/en.json";

const { stats } = en.banking.dashboard;

const gridClass = "grid grid-cols-1 gap-4 sm:grid-cols-3";

// Placeholder values until a dashboard summary endpoint is available.
const movedSecurely = 12480;
const connections = 246;
const trustScore = 4.9;

export function DashboardStats() {
  return (
    <div className={gridClass}>
      <StatCard value={movedSecurely} label={stats.movedSecurely} format="currency" />
      <StatCard value={connections} label={stats.connections} format="count" />
      <StatCard value={trustScore} label={stats.trustScore} format="rating" />
    </div>
  );
}
