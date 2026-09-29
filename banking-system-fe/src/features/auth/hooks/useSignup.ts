import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { signupMutation } from "../services/auth.api";
import type { SignupRequest, SignupResponse } from "../types";
import { toast } from "@/components/ui/toast";

export const useSignup = () => {
  const navigateTo = useNavigate();
  const { mutate } = useMutation({
    mutationFn: (data: SignupRequest) => signupMutation(data),
    onSuccess: (data: SignupResponse | null) => {
      navigateTo("/sign-in");
      toast.add({
        type: "success",
        description: data?.message,
        priority: "high",
      });
    },
  });

  return {
    mutate,
  };
};
