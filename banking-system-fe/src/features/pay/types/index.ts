import { formOptions } from "@tanstack/react-form";
import { z } from "zod";
import english from "@/locales/en.json";

const paySetupText = english.banking.paySetup;

export interface CreatePayAccountFormValues {
  accountHolderName: string;
  phoneNumber: string;
  email: string;
  password: string;
  upiId: string;
  termsAccepted: boolean;
}

const createPayAccountSchema = z.object({
  accountHolderName: z.string().trim().min(1, paySetupText.errors.required),
  phoneNumber: z.string().trim().min(1, paySetupText.errors.required),
  email: z
    .string()
    .trim()
    .min(1, paySetupText.errors.required)
    .email(paySetupText.errors.email),
  password: z.string().min(1, paySetupText.errors.required),
  upiId: z.string().trim().min(1, paySetupText.errors.required),
  termsAccepted: z.boolean().refine(Boolean, paySetupText.errors.terms),
});

export const createPayAccountFormOpts = ({
  defaultValues,
}: {
  defaultValues: CreatePayAccountFormValues;
}) =>
  formOptions({
    defaultValues,
    validators: {
      onChange: createPayAccountSchema,
      onSubmit: createPayAccountSchema,
    },
  });
