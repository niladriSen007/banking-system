import { Input } from "@/components/ui/input";
import en from "@/locales/en.json";
import { ArrowLeft, Bell, Briefcase, Search, Users, Home as HomeIcon, type LucideIcon } from "lucide-react";
import { Link, useLocation } from "react-router-dom";

const { subHeader } = en.network;

const wrapClass =
  "sticky top-16 z-40 -mx-4 flex items-center gap-4 border-b border-border/60 bg-background/95 px-4 py-3 backdrop-blur";

const backButtonClass =
  "flex h-9 w-9 shrink-0 items-center justify-center rounded-full border border-border/60 text-foreground transition hover:bg-muted";

const searchWrapClass = "relative w-full max-w-xs";

const searchIconClass =
  "pointer-events-none absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-muted-foreground";

const searchInputClass = "h-9 rounded-full bg-muted pl-9";

const navClass = "ml-auto hidden items-center gap-1 sm:flex";

const navItemClass =
  "flex flex-col items-center gap-0.5 rounded-xl px-3 py-1.5 text-xs font-medium text-muted-foreground transition hover:bg-muted hover:text-foreground";

const navItemActiveClass = "text-foreground";

type NavTab = { label: string; href?: string; icon: LucideIcon };

// "Alerts" has no route yet, so it renders as a decorative, non-navigating tab.
const navTabs: NavTab[] = [
  { label: subHeader.nav.home, href: "/", icon: HomeIcon },
  { label: subHeader.nav.network, href: "/network", icon: Users },
  { label: subHeader.nav.jobs, href: "/opportunities", icon: Briefcase },
  { label: subHeader.nav.alerts, icon: Bell },
];

export function NetworkSubHeader() {
  const { pathname } = useLocation();

  return (
    <div className={wrapClass}>
      <Link to="/" className={backButtonClass} aria-label={subHeader.backLabel}>
        <ArrowLeft className="h-4 w-4" />
      </Link>

      <div className={searchWrapClass}>
        <Search className={searchIconClass} />
        <Input
          type="search"
          placeholder={subHeader.searchPlaceholder}
          className={searchInputClass}
        />
      </div>

      <nav className={navClass}>
        {navTabs.map((tab) => {
          const Icon = tab.icon;

          if (!tab.href) {
            return (
              <button key={tab.label} type="button" className={navItemClass}>
                <Icon className="h-4.5 w-4.5" />
                {tab.label}
              </button>
            );
          }

          const isActive = pathname === tab.href;

          return (
            <Link
              key={tab.href}
              to={tab.href}
              className={`${navItemClass} ${isActive ? navItemActiveClass : ""}`}
            >
              <Icon className="h-4.5 w-4.5" />
              {tab.label}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
