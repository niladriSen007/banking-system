import { useAuthStore } from "@/features/auth/store/auth.store";
import en from "@/locales/en.json";
import { Bookmark, Users } from "lucide-react";

const { profileCard } = en.network;

const cardClass =
  "overflow-hidden rounded-2xl border border-border/60 bg-white";

const bannerClass =
  "h-16 bg-gradient-to-br from-dashboard-pay-from via-dashboard-pay-via to-dashboard-pay-to";

const bodyClass = "px-5 pb-5 text-center";

const avatarClass =
  "mx-auto -mt-8 flex h-16 w-16 items-center justify-center rounded-full border-4 border-white bg-gradient-to-br from-dashboard-network-avatar-from to-dashboard-network-avatar-to text-lg font-semibold text-white";

const nameClass = "mt-3 text-base font-semibold text-foreground";

const taglineClass = "mt-1 text-xs text-muted-foreground";

const statsRowClass = "mt-4 flex items-center justify-center gap-6 text-left";

const statValueClass = "text-sm font-semibold text-foreground";

const statLabelClass = "text-xs text-muted-foreground";

const linksWrapClass = "border-t border-border/60";

const linkRowClass =
  "flex items-center gap-3 px-5 py-3 text-sm font-medium text-foreground";

// Placeholder engagement numbers until a profile summary endpoint is available.
const profileViews = 184;
const connectionsCount = 246;

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

export function ProfileSummaryCard() {
  const { user } = useAuthStore();
  const name = user?.name || "";

  return (
    <div className={cardClass}>
      <div className={bannerClass} />

      <div className={bodyClass}>
        <div className={avatarClass}>{getInitials(name)}</div>
        <p className={nameClass}>{name}</p>
        <p className={taglineClass}>{profileCard.tagline}</p>

        <div className={statsRowClass}>
          <div>
            <p className={statValueClass}>{profileViews}</p>
            <p className={statLabelClass}>{profileCard.profileViews}</p>
          </div>
          <div>
            <p className={statValueClass}>{connectionsCount}</p>
            <p className={statLabelClass}>{profileCard.connections}</p>
          </div>
        </div>
      </div>

      <div className={linksWrapClass}>
        <div className={linkRowClass}>
          <Users className="h-4 w-4 text-muted-foreground" />
          {profileCard.myConnections}
        </div>
        <div className={linkRowClass}>
          <Bookmark className="h-4 w-4 text-muted-foreground" />
          {profileCard.savedJobs}
        </div>
      </div>
    </div>
  );
}
