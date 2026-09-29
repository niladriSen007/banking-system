const cardClass = "rounded-2xl border border-border/60 bg-white p-5";

const valueClass = "text-2xl font-semibold tracking-tight text-foreground";

const labelClass = "mt-1 text-sm text-muted-foreground";

type StatFormat = "currency" | "count" | "rating";

type StatCardProps = {
  value: number;
  label: string;
  format: StatFormat;
};

const currencyFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  maximumFractionDigits: 0,
});

const countFormatter = new Intl.NumberFormat("en-US");

function formatStatValue(value: number, format: StatFormat) {
  switch (format) {
    case "currency":
      return currencyFormatter.format(value);
    case "rating":
      return `${value.toFixed(1)}/5`;
    case "count":
    default:
      return countFormatter.format(value);
  }
}

export function StatCard({ value, label, format }: StatCardProps) {
  return (
    <div className={cardClass}>
      <p className={valueClass}>{formatStatValue(value, format)}</p>
      <p className={labelClass}>{label}</p>
    </div>
  );
}
