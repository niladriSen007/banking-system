import { CommonLoader } from "@/components/common/CommonLoader";
import { useAuthStore } from "@/features/auth/store/auth.store";
import { Navigate, Outlet, useLocation } from "react-router-dom";

const ProtectedLayout = () => {
   const { isAuthenticated, status } = useAuthStore();
   const location = useLocation();

   if (status === "loading")
     return <CommonLoader />;

   if (!isAuthenticated) {
     return (
       <Navigate
         to="/sign-in"
         replace
         state={{ from: `${location.pathname}${location.search}` }}
       />
     );
   }

   return <Outlet />;
}
export default ProtectedLayout