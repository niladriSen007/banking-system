import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useAuthStore } from "@/features/auth/store/auth.store";
import en from "@/locales/en.json";
import { Image, PartyPopper, Briefcase } from "lucide-react";

const { composer } = en.network;

const cardClass = "rounded-2xl border border-border/60 bg-white p-4";

const topRowClass = "flex items-center gap-3";

const avatarClass =
  "flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-dashboard-network-avatar-from to-dashboard-network-avatar-to text-sm font-semibold text-white";

const inputClass = "h-11 rounded-full bg-muted";

const postButtonClass = "shrink-0 rounded-full px-5";

const actionsRowClass = "mt-3 flex items-center gap-1 border-t border-border/60 pt-3";

const actionButtonClass =
  "flex items-center gap-2 rounded-xl px-3 py-2 text-xs font-medium text-muted-foreground transition hover:bg-muted";

function getInitials(name: string) {
  return (
    name
      .split(" ")
      .filter(Boolean)
      .map((part) => part[0])
      .slice(0, 2)
      .join("")
      .toUpperCase() || "?"
  );
}

// Composing and publishing posts isn't wired to a backend yet, so the composer is display-only.
export function PostComposer() {
  const { user } = useAuthStore();

  return (
    <div className={cardClass}>
      <div className={topRowClass}>
        <div className={avatarClass}>{getInitials(user?.name || "")}</div>
        <Input
          disabled
          placeholder={composer.placeholder}
          className={inputClass}
        />
        <Button disabled size="lg" className={postButtonClass}>
          {composer.post}
        </Button>
      </div>

      <div className={actionsRowClass}>
        <button type="button" disabled className={actionButtonClass}>
          <Image className="h-4 w-4" />
          {composer.photo}
        </button>
        <button type="button" disabled className={actionButtonClass}>
          <Briefcase className="h-4 w-4" />
          {composer.job}
        </button>
        <button type="button" disabled className={actionButtonClass}>
          <PartyPopper className="h-4 w-4" />
          {composer.celebrate}
        </button>
      </div>
    </div>
  );
}
