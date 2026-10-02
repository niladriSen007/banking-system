import { toast } from "@/components/ui/toast";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import english from "@/locales/en.json";
import { createPayAccountMutation } from "../services/pay.api";
import type {
  CreatePayAccountRequest,
  CreatePayAccountResponse,
} from "../types";
import { usePayStore } from "../store/pay.store";

const paySetupText = english.banking.paySetup;

export const useCreatePayAccount = () => {
  const navigate = useNavigate();
  const { mutateAsync, isPending } = useMutation<
    CreatePayAccountResponse | null,
    Error,
    CreatePayAccountRequest
  >({
    mutationFn: (request: CreatePayAccountRequest) =>
      createPayAccountMutation(request),
    onSuccess: (account) => {
      if (!account) {
        toast.add({
          type: "error",
          description: paySetupText.createResponseError,
          priority: "high",
        });
        return;
      }

      toast.add({
        type: "success",    
        description: paySetupText.created,
        priority: "high",
      });
      usePayStore.getState().setIsPayAccountSetupDone(true);
      navigate("/pay/dashboard");
    },
    onError: (error: Error) => {
      toast.add({
        type: "error",
        description: error.message || paySetupText.createError,
        priority: "high",
      });
    },
  });

  return { createPayAccount: mutateAsync, isPending };
};
