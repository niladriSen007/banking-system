import { useState } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Check,
  CheckCircle2,
  CircleCheck,
  LockKeyhole,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import english from "@/locales/en.json";
import { Link } from "react-router-dom";

const transferText = english.banking.payTransfer;
type TransferStep = 1 | 2 | 3;
const stepKeys = ["recipient", "verification", "complete"] as const;
const recipientColors = [
  "bg-violet-100 text-violet-800",
  "bg-orange-100 text-orange-800",
  "bg-cyan-100 text-cyan-800",
  "bg-emerald-100 text-emerald-800",
];

const currencyFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
});

const PayTransfer = () => {
  const [step, setStep] = useState<TransferStep>(1);
  const [selectedRecipientId, setSelectedRecipientId] = useState("");
  const [customRecipient, setCustomRecipient] = useState("");
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");
  const [pin, setPin] = useState("");
  const [error, setError] = useState("");

  const selectedRecipient = transferText.recipients.find(
    (recipient) => recipient.id === selectedRecipientId,
  );
  const recipientName = selectedRecipient?.name || customRecipient.trim();
  const numericAmount = Number(amount);
  const formattedAmount = currencyFormatter.format(
    Number.isFinite(numericAmount) ? numericAmount : 0,
  );

  const continueToVerification = () => {
    if (!selectedRecipient && !customRecipient.trim()) {
      setError(transferText.form.errors.recipient);
      return;
    }
    if (!Number.isFinite(numericAmount) || numericAmount <= 0) {
      setError(transferText.form.errors.amount);
      return;
    }
    setError("");
    setStep(2);
  };

  const verifyTransfer = () => {
    if (!/^\d{6}$/.test(pin)) {
      setError(transferText.verification.error);
      return;
    }
    setError("");
    setStep(3);
  };

  const startAnotherTransfer = () => {
    setStep(1);
    setSelectedRecipientId("");
    setCustomRecipient("");
    setAmount("");
    setNote("");
    setPin("");
    setError("");
  };

  const statusText =
    step === 1
      ? transferText.statuses.details
      : step === 2
        ? transferText.statuses.verification
        : transferText.statuses.complete;

  return (
    <div className="mx-auto max-w-2xl pb-10">
      <section
        className="relative isolate overflow-hidden rounded-2xl bg-emerald-950 p-5 text-white shadow-[0_1rem_2.5rem_rgba(16,54,43,0.16)] sm:p-7"
        aria-label={transferText.statusLabel}
      >
        <div className="pointer-events-none absolute -right-14 -top-28 size-56 rounded-full bg-lime-300/10" />
        <div className="relative z-10 flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="m-0 text-[0.65rem] font-medium tracking-[0.15em] text-lime-200 uppercase">
              {transferText.statusLabel}
            </p>
            <p className="mb-0 mt-1 text-lg font-semibold sm:text-xl">
              {statusText}
            </p>
          </div>
          {step === 2 ? (
            <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1.5 text-xs text-white/85">
              <ShieldCheck className="size-4 text-lime-300" aria-hidden="true" />
              {transferText.statuses.verification}
            </span>
          ) : null}
          {step === 3 ? (
            <span className="grid size-10 place-items-center rounded-full bg-lime-300 text-emerald-950">
              <Check className="size-5" aria-hidden="true" />
            </span>
          ) : null}
        </div>

        <ol className="relative z-10 mt-6 grid list-none grid-cols-3 gap-2 p-0">
          {stepKeys.map((key, index) => {
            const stepNumber = index + 1;
            const isComplete = stepNumber < step;
            const isCurrent = stepNumber === step;

            return (
              <li className="min-w-0" key={key}>
                <div
                  className={`mb-2 h-1 rounded-full ${
                    stepNumber <= step ? "bg-lime-300" : "bg-white/20"
                  }`}
                />
                <div className="flex items-center gap-2">
                  <span
                    className={`grid size-6 shrink-0 place-items-center rounded-full text-xs font-semibold ${
                      isCurrent || isComplete
                        ? "bg-lime-300 text-emerald-950"
                        : "border border-white/25 text-white/55"
                    }`}
                  >
                    {isComplete ? (
                      <Check className="size-3.5" aria-hidden="true" />
                    ) : (
                      stepNumber
                    )}
                  </span>
                  <span
                    className={`truncate text-[0.65rem] sm:text-xs ${
                      isCurrent ? "font-medium text-white" : "text-white/60"
                    }`}
                  >
                    {transferText.steps[key]}
                  </span>
                </div>
              </li>
            );
          })}
        </ol>
      </section>

      <section className="mt-5 rounded-2xl border border-border bg-white p-5 shadow-sm sm:p-8">
        {step === 1 ? (
          <div>
            <header>
              <p className="m-0 text-[0.65rem] font-semibold tracking-[0.14em] text-lime-700 uppercase">
                {transferText.form.eyebrow}
              </p>
              <h1 className="mb-0 mt-1 text-2xl font-semibold tracking-tight text-foreground sm:text-3xl">
                {transferText.form.title}
              </h1>
              <p className="mb-0 mt-2 text-sm text-muted-foreground">
                {transferText.form.description}
              </p>
            </header>

            <div className="mt-7">
              <h2 className="m-0 text-sm font-medium text-foreground">
                {transferText.form.recipientLabel}
              </h2>
              <p className="mb-2 mt-4 text-xs text-muted-foreground">
                {transferText.form.orChooseContact}
              </p>
              <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
                {transferText.recipients.map((recipient, index) => {
                  const isSelected = selectedRecipientId === recipient.id;

                  return (
                    <button
                      className={`flex min-w-0 items-center gap-2 rounded-xl border p-2.5 text-left transition-colors focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 ${
                        isSelected
                          ? "border-emerald-800 bg-emerald-50"
                          : "border-border bg-white hover:bg-muted/50"
                      }`}
                      key={recipient.id}
                      type="button"
                      aria-pressed={isSelected}
                      onClick={() => {
                        setSelectedRecipientId(recipient.id);
                        setCustomRecipient("");
                        setError("");
                      }}
                    >
                      <span
                        className={`grid size-8 shrink-0 place-items-center rounded-full text-[0.65rem] font-semibold ${recipientColors[index]}`}
                        aria-hidden="true"
                      >
                        {recipient.initials}
                      </span>
                      <span className="min-w-0">
                        <span className="block truncate text-xs font-medium text-foreground">
                          {recipient.name}
                        </span>
                        <span className="block truncate text-[0.6rem] text-muted-foreground">
                          {recipient.email}
                        </span>
                      </span>
                    </button>
                  );
                })}
              </div>

              <label
                className="mb-2 mt-5 block text-xs font-medium text-foreground"
                htmlFor="custom-recipient"
              >
                {transferText.form.recipientInputLabel}
              </label>
              <Input
                id="custom-recipient"
                className="h-11 rounded-xl border-border bg-muted/30 px-3 text-sm"
                autoComplete="off"
                placeholder={transferText.form.recipientInputPlaceholder}
                value={customRecipient}
                onChange={(event) => {
                  setCustomRecipient(event.target.value);
                  setSelectedRecipientId("");
                  setError("");
                }}
              />
            </div>

            <div className="mt-6">
              <label
                className="mb-2 block text-xs font-medium text-foreground"
                htmlFor="transfer-amount"
              >
                {transferText.form.amountLabel}
              </label>
              <div className="flex h-14 items-center gap-3 rounded-xl border border-border bg-muted/30 px-4 focus-within:border-emerald-800 focus-within:ring-2 focus-within:ring-emerald-800/10">
                <span className="text-xl text-muted-foreground" aria-hidden="true">
                  $
                </span>
                <Input
                  id="transfer-amount"
                  className="h-full border-0 bg-transparent px-0 text-lg font-medium shadow-none focus-visible:ring-0"
                  type="number"
                  min="0.01"
                  step="0.01"
                  inputMode="decimal"
                  placeholder={transferText.form.amountPlaceholder}
                  value={amount}
                  onChange={(event) => {
                    setAmount(event.target.value);
                    setError("");
                  }}
                />
                <span className="rounded-md bg-white px-2 py-1 text-[0.65rem] text-muted-foreground">
                  {transferText.form.currencyCode}
                </span>
              </div>
              <p className="mb-2 mt-3 text-[0.65rem] text-muted-foreground">
                {transferText.form.presetLabel}
              </p>
              <div className="grid grid-cols-4 gap-2">
                {transferText.amountPresets.map((preset) => (
                  <button
                    className={`min-h-10 rounded-lg border text-xs font-medium transition-colors focus-visible:ring-2 focus-visible:ring-ring ${
                      amount === preset.value
                        ? "border-emerald-900 bg-emerald-900 text-white"
                        : "border-border bg-white text-foreground hover:bg-muted/50"
                    }`}
                    key={preset.value}
                    type="button"
                    aria-pressed={amount === preset.value}
                    onClick={() => {
                      setAmount(preset.value);
                      setError("");
                    }}
                  >
                    {preset.label}
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-6">
              <label
                className="mb-2 block text-xs font-medium text-foreground"
                htmlFor="transfer-note"
              >
                {transferText.form.noteLabel}{" "}
                <span className="font-normal text-muted-foreground">
                  ({transferText.form.noteOptional})
                </span>
              </label>
              <Input
                id="transfer-note"
                className="h-11 rounded-xl border-border bg-muted/30 px-3 text-sm"
                maxLength={120}
                placeholder={transferText.form.notePlaceholder}
                value={note}
                onChange={(event) => setNote(event.target.value)}
              />
            </div>

            <dl className="mt-6 space-y-3 rounded-xl border border-border bg-muted/20 p-4 text-sm">
              <div className="flex items-center justify-between">
                <dt className="text-muted-foreground">{transferText.form.feeLabel}</dt>
                <dd className="m-0 font-medium text-emerald-700">
                  {currencyFormatter.format(0)}
                </dd>
              </div>
              <div className="flex items-center justify-between border-t border-border pt-3">
                <dt className="font-medium text-foreground">{transferText.form.totalLabel}</dt>
                <dd className="m-0 font-semibold tabular-nums text-foreground">
                  {formattedAmount}
                </dd>
              </div>
            </dl>

            {error ? (
              <p className="mb-0 mt-4 text-sm text-destructive" role="alert">
                {error}
              </p>
            ) : null}

            <Button
              className="mt-6 h-12 w-full rounded-xl bg-emerald-900 text-white hover:bg-emerald-800"
              type="button"
              onClick={continueToVerification}
            >
              {transferText.form.continue}
              <ArrowRight className="size-4" aria-hidden="true" />
            </Button>
          </div>
        ) : null}

        {step === 2 ? (
          <div className="mx-auto max-w-md py-3 text-center">
            <span className="mx-auto grid size-12 place-items-center rounded-full bg-amber-100 text-amber-700">
              <LockKeyhole className="size-5" aria-hidden="true" />
            </span>
            <p className="mb-0 mt-5 text-[0.65rem] font-semibold tracking-[0.14em] text-lime-700 uppercase">
              {transferText.verification.eyebrow}
            </p>
            <h1 className="mb-0 mt-2 text-2xl font-semibold tracking-tight text-foreground sm:text-3xl">
              {transferText.verification.title}
            </h1>
            <p className="mb-0 mt-2 text-sm leading-6 text-muted-foreground">
              {transferText.verification.description}
            </p>
            <div className="mt-7 rounded-xl bg-muted/40 px-4 py-3 text-left">
              <div className="flex items-center justify-between gap-3 text-sm">
                <span className="text-muted-foreground">
                  {transferText.verification.recipientLabel} {recipientName}
                </span>
                <span className="shrink-0 font-semibold tabular-nums text-foreground">
                  {formattedAmount}
                </span>
              </div>
            </div>
            <label
              className="mb-2 mt-7 block text-left text-xs font-medium text-foreground"
              htmlFor="transfer-pin"
            >
              {transferText.verification.pinLabel}
            </label>
            <Input
              id="transfer-pin"
              className="h-14 rounded-xl border-border bg-muted/30 text-center text-xl tracking-[0.6em]"
              type="password"
              inputMode="numeric"
              autoComplete="one-time-code"
              maxLength={6}
              placeholder={transferText.verification.pinPlaceholder}
              value={pin}
              onChange={(event) => {
                setPin(event.target.value.replace(/\D/g, "").slice(0, 6));
                setError("");
              }}
            />
            <p className="mb-0 mt-3 text-left text-xs text-muted-foreground" role="note">
              {transferText.verification.previewNotice}
            </p>
            {error ? (
              <p className="mb-0 mt-3 text-left text-sm text-destructive" role="alert">
                {error}
              </p>
            ) : null}
            <Button
              className="mt-6 h-12 w-full rounded-xl bg-emerald-900 text-white hover:bg-emerald-800"
              type="button"
              onClick={verifyTransfer}
            >
              {transferText.verification.verify}
              <LockKeyhole className="size-4" aria-hidden="true" />
            </Button>
            <button
              className="mt-4 inline-flex min-h-10 items-center gap-2 px-3 text-sm text-muted-foreground hover:text-foreground"
              type="button"
              onClick={() => {
                setError("");
                setStep(1);
              }}
            >
              <ArrowLeft className="size-4" aria-hidden="true" />
              {transferText.verification.back}
            </button>
          </div>
        ) : null}

        {step === 3 ? (
          <div className="mx-auto max-w-md py-4 text-center">
            <span className="mx-auto grid size-16 place-items-center rounded-full bg-lime-300 text-emerald-950 ring-8 ring-lime-100">
              <CheckCircle2 className="size-8" aria-hidden="true" />
            </span>
            <p className="mb-0 mt-7 text-[0.65rem] font-semibold tracking-[0.14em] text-lime-700 uppercase">
              {transferText.complete.eyebrow}
            </p>
            <h1 className="mb-0 mt-2 text-3xl font-semibold tracking-tight text-foreground">
              {transferText.complete.title}
            </h1>
            <p className="mb-0 mt-2 text-sm text-muted-foreground">
              {transferText.complete.description}
            </p>
            <div className="mt-7 rounded-xl border border-border p-4 text-left">
              <div className="flex items-center justify-between gap-4 pb-4">
                <div className="flex min-w-0 items-center gap-3">
                  <span className="grid size-10 shrink-0 place-items-center rounded-full bg-violet-100 text-violet-800">
                    {selectedRecipient ? (
                      selectedRecipient.initials
                    ) : (
                      <UserRound className="size-4" aria-hidden="true" />
                    )}
                  </span>
                  <div className="min-w-0">
                    <p className="m-0 text-xs text-muted-foreground">
                      {transferText.complete.sentTo}
                    </p>
                    <p className="mb-0 mt-0.5 truncate text-sm font-medium text-foreground">
                      {recipientName}
                    </p>
                  </div>
                </div>
                <p className="m-0 shrink-0 text-xl font-semibold tabular-nums text-foreground">
                  {formattedAmount}
                </p>
              </div>
              <dl className="grid grid-cols-2 gap-3 border-t border-border pt-4 text-xs">
                <div>
                  <dt className="text-muted-foreground">
                    {transferText.complete.reference}
                  </dt>
                  <dd className="mb-0 mt-1 font-medium text-foreground">
                    {transferText.complete.referenceValue}
                  </dd>
                </div>
                <div>
                  <dt className="text-muted-foreground">
                    {transferText.complete.status}
                  </dt>
                  <dd className="mb-0 mt-1 inline-flex items-center gap-1 font-medium text-emerald-700">
                    <CircleCheck className="size-3.5" aria-hidden="true" />
                    {transferText.complete.completed}
                  </dd>
                </div>
                {note.trim() ? (
                  <div className="col-span-2 border-t border-border pt-3">
                    <dt className="text-muted-foreground">
                      {transferText.form.noteLabel}
                    </dt>
                    <dd className="mb-0 mt-1 text-foreground">{note}</dd>
                  </div>
                ) : null}
              </dl>
            </div>
            <p className="mb-0 mt-4 text-xs text-muted-foreground" role="note">
              {transferText.complete.previewNotice}
            </p>
            <Button
              className="mt-6 h-12 w-full rounded-xl bg-emerald-900 text-white hover:bg-emerald-800"
              type="button"
              onClick={startAnotherTransfer}
            >
              {transferText.complete.sendAnother}
            </Button>
            <Link
              className="mt-3 inline-flex min-h-10 items-center px-3 text-sm text-muted-foreground hover:text-foreground"
              to="/pay/dashboard"
            >
              {transferText.complete.backToPay}
            </Link>
          </div>
        ) : null}
      </section>
    </div>
  );
};

export default PayTransfer;