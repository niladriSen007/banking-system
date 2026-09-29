import { CommonLoader } from "@/components/common/CommonLoader";
import { useAuthStore, type UserRole } from "@/features/auth/store/auth.store";

import { Navigate, Outlet } from "react-router-dom";

type RoleGuardLayoutProps = {
  allow: UserRole[];
};

const RoleGuardLayout = ({ allow }: RoleGuardLayoutProps) => {
  const { isAuthenticated, status, user } = useAuthStore();

  if (!isAuthenticated || status === "loading") {
    return <CommonLoader />;
  }

  if (!user) {
    return <Navigate to="/sign-in" replace />;
  }

  if (!user?.role.filter((role) => allow?.includes(role))?.length) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};
export default RoleGuardLayout;
