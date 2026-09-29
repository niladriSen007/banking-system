import { useState } from "react";
import {
  ArrowDownLeft,
  ArrowUpRight,
  BarChart3,
  Check,
  CreditCard,
  Grid2X2,
  Plus,
  Send,
  Sparkles,
} from "lucide-react";
import english from "@/locales/en.json";
import { useNavigate } from "react-router-dom";

const payDashboardText = english.banking.payDashboard;
type ActivityFilter = "all" | "sent" | "received";

const quickActions = [
  { key: "send", icon: Send },
  { key: "addMoney", icon: Plus },
  { key: "cards", icon: CreditCard },
  { key: "analytics", icon: BarChart3 },
] as const;

const avatarColors = [
  "bg-emerald-100 text-emerald-800",
  "bg-violet-100 text-violet-800",
  "bg-orange-100 text-orange-800",
  "bg-cyan-100 text-cyan-800",
  "bg-rose-100 text-rose-800",
  "bg-amber-100 text-amber-800",
  "bg-sky-100 text-sky-800",
  "bg-lime-100 text-lime-800",
];

const PayDashboard = () => {
  const navigate = useNavigate();
  const [activeFilter, setActiveFilter] = useState<ActivityFilter>("all");
  const [showAllTransactions, setShowAllTransactions] = useState(false);
  const [statusMessage, setStatusMessage] = useState("");

  const filteredTransactions = payDashboardText.activity.transactions.filter(
    (transaction) =>
      activeFilter === "all" || transaction.type === activeFilter,
  );
  const visibleTransactions = showAllTransactions
    ? filteredTransactions
    : filteredTransactions.slice(0, 5);

  return (
    <div className="mx-auto max-w-xl pb-8" aria-label={payDashboardText.accountStatus}>
      <section
        className="relative isolate overflow-hidden rounded-[1.5rem] bg-[#143f33] bg-linear-to-br from-[#143f33] via-[#194838] to-[#12382e] p-6 text-white shadow-[0_1.25rem_2.5rem_rgba(16,54,43,0.2)]"
        aria-labelledby="pay-balance-title"
      >
        <div className="pointer-events-none absolute -right-20 -top-28 z-0 size-64 rounded-full bg-lime-300/10" />
        <div className="relative z-10 flex items-start justify-between gap-4">
          <span className="inline-flex items-center gap-2 rounded-full border border-lime-300/35 bg-lime-300/10 px-3 py-1.5 text-xs font-medium text-lime-100">
            <span className="size-1.5 rounded-full bg-lime-300" />
            {payDashboardText.accountStatus}
          </span>
          <Grid2X2 className="size-5 text-white/45" aria-hidden="true" />
        </div>

        <div className="relative z-10 mt-6">
          <p className="m-0 text-[0.65rem] font-medium tracking-[0.16em] text-white/65 uppercase">
            {payDashboardText.balanceLabel}
          </p>
          <h1
            id="pay-balance-title"
            className="mt-1 flex items-baseline gap-2 text-4xl leading-none font-semibold tracking-tight sm:text-5xl"
          >
            <span>$8,240</span>
            <span className="text-2xl font-normal text-white/55">.50</span>
          </h1>
          <p className="mt-3 text-xs text-white/65">
            {payDashboardText.payIdLabel} ·{" "}
            <span className="text-white/90">{payDashboardText.payId}</span>
          </p>
        </div>

        <div className="relative z-10 mt-6 grid grid-cols-3 gap-3 border-t border-white/15 pt-4 sm:gap-6">
          {(["sent", "received", "rewards"] as const).map((key) => (
            <div className="min-w-0" key={key}>
              <p className="m-0 truncate text-[0.6rem] font-medium tracking-[0.12em] text-white/55 uppercase">
                {payDashboardText.summary[key]}
              </p>
              <p className="mb-0 mt-1.5 text-sm font-medium text-white">
                {payDashboardText.summaryValues[key]}
              </p>
            </div>
          ))}
        </div>
      </section>

      <section className="mt-7" aria-labelledby="quick-actions-title">
        <h2
          id="quick-actions-title"
          className="m-0 text-[0.65rem] font-medium tracking-[0.15em] text-muted-foreground uppercase"
        >
          {payDashboardText.quickActionsLabel}
        </h2>
        <div className="mt-3 grid grid-cols-2 gap-2.5 min-[480px]:grid-cols-4 sm:gap-3">
          {quickActions.map(({ key, icon: Icon }, index) => (
            <button
              className={`flex min-h-16 flex-col items-center justify-center gap-1.5 rounded-xl border px-2 py-3 text-xs font-medium transition-colors focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 sm:min-h-18 ${
                index === 0
                  ? "border-dashboard-pay-accent bg-dashboard-pay-accent text-dashboard-pay-accent-foreground hover:bg-lime-200"
                  : "border-border bg-white text-foreground hover:bg-muted/60"
              }`}
              key={key}
              type="button"
              onClick={() =>
                key === "send"
                  ? navigate("/pay/send")
                  : setStatusMessage(payDashboardText.actionMessages[key])
              }
            >
              <Icon className="size-4" aria-hidden="true" />
              {payDashboardText.actions[key]}
            </button>
          ))}
        </div>
        {statusMessage ? (
          <p className="mt-3 text-xs text-muted-foreground" role="status">
            {statusMessage}
          </p>
        ) : null}
      </section>

      <section
        className="mt-5 flex flex-col gap-3 rounded-xl border border-lime-200 bg-lime-50 p-4 min-[480px]:flex-row min-[480px]:items-center"
        aria-label={payDashboardText.rewards.title}
      >
        <span className="grid size-10 shrink-0 place-items-center rounded-xl bg-lime-300/80 text-emerald-950">
          <Sparkles className="size-4" aria-hidden="true" />
        </span>
        <div className="min-w-0 flex-1">
          <p className="m-0 text-sm font-medium text-emerald-950">
            {payDashboardText.rewards.title}
          </p>
          <div
            className="mt-2 h-1.5 overflow-hidden rounded-full bg-lime-200"
            role="progressbar"
            aria-valuenow={1260}
            aria-valuemin={0}
            aria-valuemax={3000}
            aria-label={payDashboardText.rewards.progress}
          >
            <div className="h-full w-[42%] rounded-full bg-lime-600" />
          </div>
          <p className="mb-0 mt-1.5 text-[0.65rem] text-emerald-900/70">
            {payDashboardText.rewards.progress}
          </p>
        </div>
        <button
          className="inline-flex min-h-10 shrink-0 items-center justify-center rounded-lg bg-emerald-900 px-4 text-sm font-medium text-white transition-colors hover:bg-emerald-800 focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
          type="button"
          onClick={() => setStatusMessage(payDashboardText.rewards.redeemMessage)}
        >
          {payDashboardText.rewards.redeem}
        </button>
      </section>

      <section className="mt-7" aria-labelledby="activity-title">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <h2
            id="activity-title"
            className="m-0 text-[0.65rem] font-medium tracking-[0.15em] text-muted-foreground uppercase"
          >
            {payDashboardText.activity.title}
          </h2>
          <div
            className="inline-flex w-fit items-center rounded-xl bg-muted p-1"
            role="group"
          >
            {(["all", "sent", "received"] as const).map((filter) => (
              <button
                className={`min-h-8 rounded-lg px-3 text-xs transition-colors focus-visible:ring-2 focus-visible:ring-ring ${
                  activeFilter === filter
                    ? "bg-white font-medium text-foreground shadow-sm"
                    : "text-muted-foreground hover:text-foreground"
                }`}
                key={filter}
                type="button"
                aria-pressed={activeFilter === filter}
                onClick={() => {
                  setActiveFilter(filter);
                  setShowAllTransactions(false);
                }}
              >
                {payDashboardText.activity.filters[filter]}
              </button>
            ))}
          </div>
        </div>

        <div className="mt-3 overflow-hidden rounded-xl border border-border bg-white shadow-sm">
          {visibleTransactions.length ? (
            <ul className="m-0 list-none divide-y divide-border/70 p-0">
              {visibleTransactions.map((transaction, index) => (
                <li
                  className="flex min-h-16 items-center gap-3 px-4 py-2.5 sm:gap-4"
                  key={transaction.reference}
                >
                  <span
                    className={`grid size-9 shrink-0 place-items-center rounded-full text-[0.65rem] font-semibold ${avatarColors[index % avatarColors.length]}`}
                    aria-hidden="true"
                  >
                    {transaction.initials}
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="m-0 truncate text-sm font-medium text-foreground">
                      {transaction.name}
                    </p>
                    <p className="mb-0 mt-0.5 text-[0.65rem] text-muted-foreground">
                      {transaction.date}
                    </p>
                  </div>
                  <div className="shrink-0 text-right">
                    <p
                      className={`m-0 text-sm font-medium tabular-nums ${
                        transaction.type === "received"
                          ? "text-emerald-700"
                          : "text-rose-600"
                      }`}
                    >
                      {transaction.type === "received" ? (
                        <ArrowDownLeft
                          className="mr-1 inline size-3.5"
                          aria-hidden="true"
                        />
                      ) : (
                        <ArrowUpRight
                          className="mr-1 inline size-3.5"
                          aria-hidden="true"
                        />
                      )}
                      {transaction.amount}
                    </p>
                    <p className="mb-0 mt-0.5 text-[0.6rem] text-muted-foreground">
                      {transaction.reference}
                    </p>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p className="m-0 px-4 py-8 text-center text-sm text-muted-foreground">
              {payDashboardText.activity.empty}
            </p>
          )}
        </div>

        {filteredTransactions.length > 5 ? (
          <button
            className="mt-3 flex min-h-11 w-full items-center justify-center gap-2 rounded-xl border border-border bg-white px-4 text-sm font-medium text-emerald-900 shadow-sm transition-colors hover:bg-muted/40 focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
            type="button"
            onClick={() => setShowAllTransactions((visible) => !visible)}
          >
            {showAllTransactions ? (
              <Check className="size-4" aria-hidden="true" />
            ) : null}
            {showAllTransactions
              ? payDashboardText.activity.viewLess
              : payDashboardText.activity.viewAll}
          </button>
        ) : null}
      </section>

    </div>
  );
};

export default PayDashboard;