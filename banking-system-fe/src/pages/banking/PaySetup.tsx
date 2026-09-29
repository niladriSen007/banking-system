import { Button } from "@/components/ui/button";
import { FieldLabel, Field as UiField } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { useAuthStore } from "@/features/auth/store/auth.store";
import {
    createPayAccountFormOpts,
    type CreatePayAccountFormValues,
} from "@/features/pay/types";
import english from "@/locales/en.json";
import { useForm } from "@tanstack/react-form";
import {
    ArrowRight,
    Check,
    CreditCard,
    Eye,
    EyeOff,
    LockKeyhole,
    Mail,
    Phone,
    Send,
    ShieldCheck,
    Sparkles,
    Timer,
    UserRound,
} from "lucide-react";
import { Activity, useState } from "react";
import { useNavigate } from "react-router-dom";

const paySetupText = english.banking.paySetup;
const paySetupFieldNames = [
  "accountHolderName",
  "phoneNumber",
  "email",
  "password",
  "upiId",
] as const satisfies readonly (keyof CreatePayAccountFormValues)[];

const PaySetup = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const { user } = useAuthStore();

  const defaultValues: CreatePayAccountFormValues = {
    accountHolderName: user?.name ?? "",
    phoneNumber: "",
    email: user?.email ?? "",
    password: "",
    upiId: "",
    termsAccepted: false,
  };

  const { handleSubmit, Field, Subscribe } = useForm({
    ...createPayAccountFormOpts({ defaultValues }),
    onSubmit: async () => {
      navigate("/pay/dashboard");
    },
  });

  return (
    <section
      className="relative mx-auto w-full max-w-180 overflow-hidden rounded-[1.75rem] border border-(--auth-card-border) bg-white px-5 py-7 text-auth-foreground shadow-[0_1.25rem_3.5rem_var(--auth-card-shadow)] sm:px-9 sm:py-9 lg:px-12 lg:py-10"
      aria-labelledby="pay-setup-title"
    >
      <div className="pointer-events-none absolute -right-20 -top-24 size-64 rounded-full bg-auth-accent/15" />
      <header className="relative">
        <div className="flex items-start justify-between gap-4">
          <span className="grid size-14 place-items-center rounded-2xl bg-auth-accent/25 text-auth-link shadow-[0_0.75rem_1.5rem_var(--auth-accent-shadow)]">
            <CreditCard className="size-7" aria-hidden="true" />
          </span>
          <span className="inline-flex shrink-0 items-center gap-2 rounded-full border border-auth-input-border bg-white/90 px-3 py-2 text-xs text-auth-label">
            <Timer className="size-3.5 text-auth-caption" aria-hidden="true" />
            {paySetupText.timeEstimate}
          </span>
        </div>
        <p className="mb-2 mt-8 text-[0.625rem] font-semibold tracking-[0.12em] text-auth-caption uppercase">
          {paySetupText.eyebrow}
        </p>
        <h1
          id="pay-setup-title"
          className="m-0 text-3xl leading-tight font-medium text-auth-heading sm:text-4xl"
        >
          {paySetupText.title}
        </h1>
        <p className="mb-0 mt-3 max-w-130 text-sm leading-7 text-auth-muted">
          {paySetupText.description}
        </p>
      </header>

      <ul className="relative mt-7 grid list-none grid-cols-1 gap-3 p-0 sm:grid-cols-3">
        {[ShieldCheck, Send, Sparkles].map((Icon, index) => (
          <li
            className="flex min-h-18 flex-col justify-between rounded-2xl border border-auth-input-border bg-auth-input-bg p-3.5"
            key={paySetupText.benefits[index]}
          >
            <Icon className="size-4 text-auth-caption" aria-hidden="true" />
            <span className="mt-3 text-xs font-medium text-auth-label">
              {paySetupText.benefits[index]}
            </span>
          </li>
        ))}
      </ul>

      <form
        className="relative mt-8 grid grid-cols-1 gap-x-4 gap-y-5 sm:grid-cols-2"
        onSubmit={(event) => {
          event.preventDefault();
          event.stopPropagation();
          handleSubmit();
        }}
      >
        {paySetupFieldNames.map((name) => (
          <Field
            key={name}
            name={name}
            children={(field) => {
              const isPassword = field.name === "password";
              const fieldText = paySetupText.fields[field.name];
              const Icon =
                field.name === "accountHolderName"
                  ? UserRound
                  : field.name === "phoneNumber"
                    ? Phone
                    : field.name === "email"
                      ? Mail
                      : field.name === "upiId"
                        ? Send
                        : LockKeyhole;
              const { errors, isTouched } = field.state.meta;

              return (
                <UiField
                  className={
                    field.name === "upiId" ? "gap-2 sm:col-span-2" : "gap-2"
                  }
                >
                  <FieldLabel
                    className="text-xs font-medium text-auth-label"
                    htmlFor={field.name}
                  >
                    {fieldText.label}
                  </FieldLabel>
                  <div className="relative flex items-center">
                    <Icon
                      className="pointer-events-none absolute left-3.5 z-10 size-4 text-auth-icon"
                      aria-hidden="true"
                    />
                    <Input
                      id={field.name}
                      className="h-13 rounded-xl border-auth-input-border bg-auth-input-bg py-2 pl-11 pr-11 text-sm text-auth-input-text shadow-none placeholder:text-auth-input-placeholder focus-visible:border-auth-input-focus focus-visible:ring-[3px] focus-visible:ring-(--auth-input-ring)"
                      type={
                        isPassword
                          ? showPassword
                            ? "text"
                            : "password"
                          : field.name === "email"
                            ? "email"
                            : field.name === "phoneNumber"
                              ? "tel"
                              : "text"
                      }
                      name={field.name}
                      autoComplete={
                        field.name === "accountHolderName"
                          ? "name"
                          : field.name === "phoneNumber"
                            ? "tel"
                            : field.name === "email"
                              ? "email"
                              : isPassword
                                ? "new-password"
                                : "off"
                      }
                      disabled={
                        field.name === "email" ||
                        field.name === "accountHolderName"
                      }
                      required
                      placeholder={fieldText.placeholder}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(event) =>
                        field.handleChange(event.target.value)
                      }
                    />
                    {isPassword ? (
                      <Button
                        className="absolute right-2.5 grid size-8 place-items-center rounded-md bg-transparent text-auth-icon-muted hover:bg-auth-hover hover:text-auth-link [&_svg]:size-4"
                        type="button"
                        onClick={() => setShowPassword((visible) => !visible)}
                        aria-label={
                          showPassword
                            ? paySetupText.hidePassword
                            : paySetupText.showPassword
                        }
                      >
                        {showPassword ? (
                          <EyeOff aria-hidden="true" />
                        ) : (
                          <Eye aria-hidden="true" />
                        )}
                      </Button>
                    ) : field.name === "upiId" &&
                      field.state.value.length > 0 &&
                      !errors?.length ? (
                      <Check
                        className="absolute right-3.5 size-4 text-auth-caption"
                        aria-hidden="true"
                      />
                    ) : null}
                  </div>
                  <Activity
                    mode={isTouched && errors.length > 0 ? "visible" : "hidden"}
                  >
                    <p className="mt-1 text-xs text-red-600">
                      {errors[0]?.message}
                    </p>
                  </Activity>
                </UiField>
              );
            }}
          />
        ))}

        <Field
          name="termsAccepted"
          children={(field) => (
            <UiField className="gap-2 sm:col-span-2">
              <label className="flex min-h-14 cursor-pointer items-start gap-3 rounded-2xl bg-auth-input-bg px-4 py-4 text-sm leading-6 text-auth-muted">
                <input
                  className="mt-1 size-4 shrink-0 accent-auth-button"
                  id="termsAccepted"
                  type="checkbox"
                  checked={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(event) => field.handleChange(event.target.checked)}
                />
                <span>{paySetupText.terms}</span>
              </label>
              {field.state.meta.errors[0]?.message &&
              field.state.meta.isTouched ? (
                <p className="m-0 text-xs text-destructive">
                  {field.state.meta.errors[0].message}
                </p>
              ) : null}
            </UiField>
          )}
        />

        <div className="flex flex-col items-start gap-3 sm:col-span-2">
          <Subscribe
            selector={(state) => [state.canSubmit, state.isSubmitting]}
            children={([canSubmit, isSubmitting]) => (
              <Button
                className="min-h-13 justify-center gap-2 rounded-xl bg-auth-button px-5 text-sm text-white shadow-[0_0.5rem_1.2rem_var(--auth-button-shadow)] hover:bg-auth-button-hover hover:shadow-[0_0.7rem_1.4rem_var(--auth-button-shadow-hover)] disabled:cursor-not-allowed disabled:opacity-60"
                type="submit"
                disabled={!canSubmit || isSubmitting}
              >
                {paySetupText.submit}
                <ArrowRight className="size-4" aria-hidden="true" />
              </Button>
            )}
          />
        </div>
      </form>
    </section>
  );
};

export default PaySetup;
