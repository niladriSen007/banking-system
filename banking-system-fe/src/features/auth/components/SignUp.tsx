import { useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, LockKeyhole, Mail, Phone, Sparkles, UserRound } from "lucide-react";
import { Button } from "@/components/ui/button";
import { FieldLabel, Field as UiField } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { useForm } from "@tanstack/react-form";
import { useSignup } from "../hooks/useSignup";
import { signupFormOpts, type SignupRequest } from "../types";
import english from "@/locales/en.json";

const signInText = english.auth.signIn;
const signUpText = english.auth.signUp;
const signUpFieldNames = ["firstName", "lastName", "email", "phoneNumber", "password"] as const satisfies readonly (keyof SignupRequest)[];
const avatarColors = [
  "bg-auth-avatar-1",
  "bg-auth-avatar-2",
  "bg-auth-avatar-3",
  "bg-auth-avatar-4 text-auth-hero-foreground",
];

const SignUp = () => {
  const { mutate } = useSignup();
  const [showPassword, setShowPassword] = useState(false);
  const { handleSubmit, Field, Subscribe } = useForm({
    ...signupFormOpts,
    onSubmit: async ({ value }) => {
      mutate(value);
    },
  });

  return (
    <main className="-my-8 grid min-h-svh place-items-center p-3.5 text-auth-foreground">
      <section
        className="grid min-h-[min(42rem,calc(100svh-1.75rem))] w-full max-w-360 grid-cols-[1.12fr_1fr] overflow-hidden rounded-[1.25rem] border border-(--auth-card-border) bg-white shadow-[0_1.25rem_3.5rem_var(--auth-card-shadow)] max-[760px]:min-h-0 max-[760px]:grid-cols-1 max-[420px]:rounded-2xl"
        aria-label={signUpText.shellLabel}
      >
        <aside className="relative flex min-w-0 flex-col justify-between overflow-hidden bg-[radial-gradient(ellipse_at_94%_5%,var(--auth-hero-glow),transparent_32%)] bg-auth-hero p-6 text-auth-hero-foreground md:p-10 xl:p-11 max-[760px]:min-h-64">
          <Link
            to="/"
            className="relative z-10 inline-flex w-fit items-center gap-2.5 font-semibold text-inherit no-underline"
            aria-label={signInText.brandHomeLabel}
          >
            <span className="grid size-8 place-items-center rounded-[0.6rem] bg-auth-accent text-auth-hero shadow-[0_0.3rem_1rem_var(--auth-accent-shadow)] [&_svg]:size-4">
              <Sparkles aria-hidden="true" />
            </span>
            <span>{signInText.brandName}</span>
          </Link>

          <div className="relative z-10 max-w-136 py-12 max-[760px]:py-8 max-[760px]:pb-4">
            <p className="m-0 inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/[0.07] px-3 py-2 text-xs font-normal text-auth-hero-badge-foreground">
              <span className="size-1.5 rounded-full bg-auth-accent shadow-[0_0_0.7rem_var(--auth-accent-glow)]" />
              {signInText.heroEyebrow}
            </p>
            <h1 className="mb-4 mt-6 max-w-[10ch] text-[2.4rem] leading-[1.04] font-medium tracking-normal max-[760px]:mb-4 max-[760px]:mt-4 max-[760px]:max-w-[13ch] max-[760px]:text-4xl">
              {signInText.heroTitle}
            </h1>
            <p className="m-0 max-w-140 text-xs leading-[1.8] text-auth-hero-muted/75">
              {signInText.heroDescription}
            </p>
          </div>

          <div className="relative z-10 flex items-center justify-between gap-4 border-t border-white/10 pt-5 text-auth-hero-muted/70 max-[760px]:hidden">
            <div className="flex pl-1.5" aria-hidden="true">
              {signInText.avatarInitials.map((initials, index) => (
                <span
                  className={`-ml-1.5 grid size-7 place-items-center rounded-full border-2 border-auth-hero text-xs font-bold text-auth-hero ${avatarColors[index]}`}
                  key={initials}
                >
                  {initials}
                </span>
              ))}
            </div>
            <p className="m-0">{signInText.communityCaption}</p>
          </div>
        </aside>

        <section
          className="grid min-w-0 place-items-center bg-white px-8 py-10 md:px-12 lg:px-16 xl:p-20 max-[760px]:px-6 max-[760px]:py-10"
          aria-labelledby="signup-title"
        >
          <div className="w-full max-w-90">
            <p className="mb-2 m-0 text-[0.625rem] font-semibold tracking-[0.12em] text-auth-caption uppercase">
              {signUpText.welcomeEyebrow}
            </p>
            <h2 id="signup-title" className="m-0 text-2xl leading-[1.2] font-medium tracking-normal text-auth-heading">
              {signUpText.title}
            </h2>
            <p className="mb-6 mt-2 text-xs leading-[1.6] text-auth-muted">{signUpText.subtitle}</p>

            <form
              className="grid grid-cols-2 gap-x-3 gap-y-3"
              onSubmit={(e) => {
                e.preventDefault();
                e.stopPropagation();
                handleSubmit();
              }}
            >
              {signUpFieldNames.map((name) => (
                <Field
                  key={name}
                  name={name}
                  children={(field) => {
                    const isPassword = field.name === "password";
                    const isEmail = field.name === "email";
                    const isPhoneNumber = field.name === "phoneNumber";
                    const fieldText = signUpText.fields[field.name];
                    const Icon = isPassword ? LockKeyhole : isEmail ? Mail : isPhoneNumber ? Phone : UserRound;

                    return (
                      <UiField className={field.name === "firstName" || field.name === "lastName" ? "gap-2" : "col-span-2 gap-2"}>
                        <FieldLabel className="text-[0.625rem] font-medium text-auth-label" htmlFor={field.name}>
                          {fieldText.label}
                        </FieldLabel>
                        <div className="relative flex items-center">
                          <Icon className="pointer-events-none absolute left-3.5 z-10 size-4 text-auth-icon" aria-hidden="true" />
                          <Input
                            id={field.name}
                            className="h-9 rounded-lg border-auth-input-border bg-auth-input-bg py-1 pl-11 pr-11 text-xs text-auth-input-text shadow-none placeholder:text-auth-input-placeholder focus-visible:border-auth-input-focus focus-visible:ring-[3px] focus-visible:ring-(--auth-input-ring)"
                            type={isPassword ? (showPassword ? "text" : "password") : isEmail ? "email" : isPhoneNumber ? "tel" : "text"}
                            name={field.name}
                            autoComplete={
                              field.name === "firstName" ? "given-name" :
                                field.name === "lastName" ? "family-name" :
                                  isEmail ? "email" : isPhoneNumber ? "tel" : "new-password"
                            }
                            placeholder={fieldText.placeholder}
                            value={field.state.value}
                            onBlur={field.handleBlur}
                            onChange={(e) => field.handleChange(e.target.value)}
                          />
                          {isPassword && (
                            <button
                              className="absolute right-2.5 grid size-8 place-items-center rounded-md bg-transparent text-auth-icon-muted hover:bg-auth-hover hover:text-auth-link [&_svg]:size-4"
                              type="button"
                              onClick={() => setShowPassword((visible) => !visible)}
                              aria-label={showPassword ? signUpText.hidePassword : signUpText.showPassword}
                            >
                              {showPassword ? <EyeOff aria-hidden="true" /> : <Eye aria-hidden="true" />}
                            </button>
                          )}
                        </div>
                      </UiField>
                    );
                  }}
                />
              ))}
              <Subscribe
                selector={(state) => [state.canSubmit, state.isSubmitting]}
                children={([canSubmit, isSubmitting]) => (
                  <Button
                    className="col-span-2 mt-1 min-h-9 w-full justify-center gap-2 rounded-lg bg-auth-button text-xs text-white shadow-[0_0.5rem_1.2rem_var(--auth-button-shadow)] hover:bg-auth-button-hover hover:shadow-[0_0.7rem_1.4rem_var(--auth-button-shadow-hover)] disabled:cursor-not-allowed disabled:opacity-60"
                    type="submit"
                    disabled={!canSubmit || isSubmitting}
                  >
                    {isSubmitting ? signUpText.submitting : signUpText.submit}
                    {!isSubmitting && <ArrowRight aria-hidden="true" />}
                  </Button>
                )}
              />
            </form>

            <p className="mt-5 text-center text-xs text-auth-muted">
              {signUpText.signinPrompt}{" "}
              <Link className="font-semibold text-auth-link no-underline hover:underline hover:underline-offset-4" to="/sign-in">
                {signUpText.signinLink}
              </Link>
            </p>
            <div className="mt-8 flex justify-center gap-5 text-[0.625rem] text-auth-footer max-[420px]:gap-3" aria-label={signUpText.footerLabel}>
              {signUpText.footerItems.map((item) => <span key={item}>{item}</span>)}
            </div>
          </div>
        </section>
      </section>
    </main>
  );
};

export default SignUp;