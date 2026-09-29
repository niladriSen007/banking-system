import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import en from "@/locales/en.json";
import {
  LogIn,
  LogOut,
  Menu,
  Sparkles,
  User,
  type LucideIcon,
} from "lucide-react";
import { Link } from "react-router-dom";

const { navbar } = en.layout;

type CustomerMobileNavbarProps = {
  isSignedIn: boolean;
};

export type NavItem = {
  label: string;
  href: string;
  icon: LucideIcon;
};

const mobileWrap = "ml-auto flex items-center gap-1 lg:hidden";

const menuButton =
  "h-11 w-11 rounded-xl border border-border bg-background text-foreground hover:bg-muted";

const sheetContent =
  "w-[380px] max-w-[92vw] border-r border-border bg-background p-0 sm:w-[460px]";

const brandWrap = "flex items-center gap-2";

const brandIconWrap =
  "flex h-8 w-8 items-center justify-center rounded-xl bg-primary text-primary-foreground";

const brandTitle = "text-lg font-semibold tracking-[-0.02em] text-foreground";

const brandBlock = "px-5 py-6 sm:px-6";

const drawerSection = "space-y-3 px-5 py-5 sm:px-6";

const drawerTitle = "text-sm font-semibold tracking-wide text-muted-foreground";

const drawerItemsWrap = "space-y-1";

const drawerItemLink =
  "flex items-center gap-3 rounded-xl px-2 py-3 text-[18px] font-medium text-foreground transition hover:bg-muted";

function DrawerSection({ title, items }: { title: string; items: NavItem[] }) {
  return (
    <section className={drawerSection}>
      <p className={drawerTitle}>{title}</p>
      <div className={drawerItemsWrap}>
        {items.map((item) => {
          const Icon = item.icon;

          return (
            <Link key={item.label} to={item.href} className={drawerItemLink}>
              <Icon className="h-4.5 w-4.5" />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </div>
    </section>
  );
}

const MobileNavbar = ({ isSignedIn }: CustomerMobileNavbarProps) => {
  const mobileAccountItems: NavItem[] = isSignedIn
    ? [
        { label: navbar.myAccount, href: "/account", icon: User },
        { label: navbar.logout, href: "/logout", icon: LogOut },
      ]
    : [
        {
          label: navbar.signIn,
          href: "/sign-in",
          icon: LogIn,
        },
      ];

  return (
    <div className={mobileWrap}>
      <Sheet>
        <SheetTrigger>
          <Button variant={"ghost"} size={"icon"} className={menuButton}>
            <Menu className="h-5 w-5" />
          </Button>
        </SheetTrigger>

        <SheetContent side="left" className={sheetContent}>
          <SheetHeader className="sr-only">
            <SheetTitle>{navbar.menuLabel}</SheetTitle>
          </SheetHeader>
          <div className={brandBlock}>
            <Link to={"/"} className={brandWrap}>
              <span className={brandIconWrap}>
                <Sparkles className="h-4.5 w-4.5" />
              </span>
              <span className={brandTitle}>{navbar.brandName}</span>
            </Link>
          </div>
          <Separator />
          <DrawerSection title={navbar.account} items={mobileAccountItems} />
        </SheetContent>
      </Sheet>
    </div>
  );
};
export default MobileNavbar;

