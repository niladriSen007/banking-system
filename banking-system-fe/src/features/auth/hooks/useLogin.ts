import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { loginMutation } from "../services/auth.api";
import { useAuthStore } from "../store/auth.store";
import type { LoginRequest, LoginResponse } from "../types";
import { toast } from "@/components/ui/toast";

export const useLogin = () => {
  const navigateTo = useNavigate();
  const { setAccessToken, setUser, logout,setIsAuthenticated } = useAuthStore();
  const { mutate, isPending } = useMutation({
    mutationFn: (data: LoginRequest) => loginMutation(data),
    onSuccess: (loginResponse: LoginResponse) => {
      const user = loginResponse.userResponse;
      const normalizedRole = user.role.toLowerCase();
      setIsAuthenticated(true);
      setAccessToken(loginResponse.accessToken);
      setUser({
        id: String(user.id),
        name: `${user.firstName} ${user.lastName}`.trim(),
        email: user.email,
        phoneNumber: user.phoneNumber,
        role:
          normalizedRole === "admin" || normalizedRole === "customer"
            ? [normalizedRole]
            : [],
      });
      navigateTo("/");
      toast.add({
        type: "success",
        description: loginResponse.message + " " + loginResponse.title,
      });
    },
    onError: () => {
      setIsAuthenticated(false);
      logout();
    },
  });

  return {
    mutate,
    isPending,
  };
};
