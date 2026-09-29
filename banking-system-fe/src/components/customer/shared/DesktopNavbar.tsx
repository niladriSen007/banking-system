import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import CustomerMobileNavbar from "@/components/customer/shared/MobileNavbar";
import { useAuthStore } from "@/features/auth/store/auth.store";
import en from "@/locales/en.json";
import { LogOut, Sparkles, User } from "lucide-react";
import { Link } from "react-router-dom";

const { navbar } = en.layout;

const headerClass =
  "sticky top-0 z-50 border-b border-border bg-background/95 backdrop-blur-xl";

const shell =
  "mx-auto flex h-16 max-w-7xl items-center gap-3 px-4 sm:px-6 lg:px-8";

const brandWrap = "flex shrink-0 items-center gap-2";

const brandIconWrap =
  "flex h-8 w-8 items-center justify-center rounded-xl bg-primary text-primary-foreground";

const brandTitle = "text-lg font-semibold tracking-[-0.02em] text-foreground";

const desktopNav = "ml-auto hidden items-center gap-1 lg:flex";

const dropdownButton =
  "flex h-10 items-center gap-2 rounded-xl px-3 text-sm font-medium text-foreground/90 transition hover:bg-muted";

const dropdownContent =
  "mt-3 w-48 rounded-2xl border-border bg-popover/95 p-1 backdrop-blur";

const dropdownItemLink =
  "flex cursor-pointer items-center gap-3 rounded-xl px-3 py-2.5";

const signInLink =
  "inline-flex h-10 items-center gap-2 rounded-xl px-3 text-sm font-medium text-foreground/90 transition hover:bg-muted";

const DesktopNavbar = () => {
  const { isAuthenticated: isSignedIn, logout } = useAuthStore();

  return (
    <header className={headerClass}>
      <div className={shell}>
        <Link to={"/"} className={brandWrap} aria-label={navbar.brandHomeLabel}>
          <span className={brandIconWrap}>
            <Sparkles className="h-4.5 w-4.5" />
          </span>
          <span className={brandTitle}>{navbar.brandName}</span>
        </Link>

        <nav className={desktopNav}>
          {isSignedIn ? (
            <DropdownMenu>
              <DropdownMenuTrigger>
                <span className={dropdownButton}>
                  <User className="h-4.5 w-4.5" />
                  {navbar.account}
                </span>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className={dropdownContent}>
                <DropdownMenuItem className={dropdownItemLink}>
                  <User className="h-4 w-4" />
                  <span>{navbar.myAccount}</span>
                </DropdownMenuItem>

                <DropdownMenuItem
                  onClick={() => logout()}
                  className={dropdownItemLink}
                >
                  <LogOut className="h-4 w-4" />
                  <span>{navbar.logout}</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Link to="/sign-in" className={signInLink}>
              {navbar.signIn}
            </Link>
          )}
        </nav>

        <CustomerMobileNavbar isSignedIn={!!isSignedIn} />
      </div>
    </header>
  );
};
export default DesktopNavbar;

