import { CommonLoader } from "@/components/common/CommonLoader";
import { useAuthStore } from "@/features/auth/store/auth.store";
import { Navigate, Outlet, useLocation } from "react-router-dom";

const PublicOnlyLayout = () => {
  const { isAuthenticated, status } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated && status === "loading") {
    return <CommonLoader />;
  }

  if (
    isAuthenticated &&
    (location.pathname === "/sign-in" || location.pathname === "/sign-up")
  ) {
    return <Navigate to={"/"} replace />;
  }

  return <Outlet />;
};
export default PublicOnlyLayout;
