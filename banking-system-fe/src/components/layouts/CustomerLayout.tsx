import DesktopNavbar from "@/components/customer/shared/DesktopNavbar";
import { Outlet } from "react-router-dom";

export function CustomerLayout() {
  return (
    <div className="min-h-screen bg-background text-foreground">
      <DesktopNavbar />
      <main className="mx-auto max-w-7xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
