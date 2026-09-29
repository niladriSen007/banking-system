import { router } from "@/routes/router";
import { useRestoreSession } from "@/features/auth/hooks/useRestoreSession";
import { QueryClientProvider as Wrapper } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { RouterProvider } from "react-router-dom";
import { queryClient } from "./export";

export const QueryClientProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  useRestoreSession();

  return (
    <Wrapper client={queryClient}>
      {children}
      <RouterProvider router={router} />
      <ReactQueryDevtools initialIsOpen={false} />
    </Wrapper>
  );
};
