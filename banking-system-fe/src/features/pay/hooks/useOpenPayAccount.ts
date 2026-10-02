import { toast } from "@/components/ui/toast";
import { useAuthStore } from "@/features/auth/store/auth.store";
import { usePayStore } from "@/features/pay/store/pay.store";
import english from "@/locales/en.json";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { getPayAccountByEmailQuery } from "../services/pay.api";

const payCardText = english.banking.dashboard.payCard;

export const useOpenPayAccount = () => {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const { refetch, isFetching } = useQuery({
    queryKey: ["pay-account", user?.email],
    queryFn: () => getPayAccountByEmailQuery(user?.email ?? ""),
    enabled: false,
    retry: false,
  });

  const openPayAccount = async () => {
    if (!user?.email) {
      toast.add({
        type: "error",
        description: payCardText.lookupError,
        priority: "high",
      });
      return;
    }

    try {
      const { data: account } = await refetch({ throwOnError: true });
      usePayStore.getState().setIsPayAccountSetupDone(Boolean(account));
      navigate(account ? "/pay/dashboard" : "/pay");
    } catch (error) {
      toast.add({
        type: "error",
        description:
          error instanceof Error && error.message
            ? error.message
            : payCardText.lookupError,
        priority: "high",
      });
    }
  };

  return { openPayAccount, isPending: isFetching };
};