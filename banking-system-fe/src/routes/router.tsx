import { CustomerLayout } from "@/components/layouts/CustomerLayout";
import ProtectedLayout from "@/layouts/ProtectedLayout";
import PublicOnlyLayout from "@/layouts/PublicOnlyLayout";
import SignInPage from "@/pages/auth/SignInPage";
import SignUpPage from "@/pages/auth/SignUpPage";
import BankingHome from "@/pages/banking/BankingHome";
import CreateAccount from "@/pages/banking/CreateAccount";
import PaySetup from "@/pages/banking/PaySetup";
import PayDashboard from "@/pages/banking/PayDashboard";
import PayTransfer from "@/pages/banking/PayTransfer";
import NetworkHome from "@/pages/network/NetworkHome";
import { createBrowserRouter } from "react-router-dom";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <CustomerLayout />,
    children: [
      {
        element: <ProtectedLayout />,
        children: [
          {
            index: true,
            element: <BankingHome />,
          },
          {
            path: "/create-account",
            element: <CreateAccount />,
          },
          {
            path: "/pay",
            element: <PaySetup />,
          },
          {
            path: "/pay/dashboard",
            element: <PayDashboard />,
          },
          {
            path: "/pay/send",
            element: <PayTransfer />,
          },
          {
            path: "/network",
            element: <NetworkHome />,
          },
        ],
      },
      {
        element: <PublicOnlyLayout />,
        children: [
          {
            path: "/sign-in",
            element: <SignInPage />,
          },
          {
            path: "/sign-up",
            element: <SignUpPage />,
          },
        ],
      },
    ],
  },
]);
