import en from "@/locales/en.json";
import { UserPlus } from "lucide-react";

const { peopleToKnow } = en.network;

type Suggestion = {
  id: string;
  name: string;
  role: string;
  initials: string;
};

// Placeholder suggestions until a connections-recommendation endpoint is available.
const suggestions: Suggestion[] = [
  { id: "1", name: "Priya Shah", role: "Product Designer at Linear", initials: "PS" },
  { id: "2", name: "Marcus Reed", role: "Founder, Northstar Labs", initials: "MR" },
  { id: "3", name: "Elena Rossi", role: "Growth Lead at Orbit", initials: "ER" },
];

const cardClass = "rounded-2xl border border-border/60 bg-white p-4";

const headerRowClass = "flex items-center justify-between";

const titleClass = "text-sm font-semibold text-foreground";

const addButtonClass =
  "flex h-8 w-8 items-center justify-center rounded-full text-muted-foreground transition hover:bg-muted";

const listClass = "mt-3 space-y-3";

const rowClass = "flex items-center gap-3";

const avatarClass =
  "flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-dashboard-network-avatar-from to-dashboard-network-avatar-to text-xs font-semibold text-white";

const nameClass = "text-sm font-medium text-foreground";

const roleClass = "text-xs text-muted-foreground";

const connectButtonClass =
  "ml-auto shrink-0 rounded-full border border-border/60 px-3 py-1 text-xs font-medium text-foreground transition hover:bg-muted";

const viewAllClass = "mt-4 block text-xs font-medium text-foreground hover:underline";

export function PeopleToKnowCard() {
  return (
    <div className={cardClass}>
      <div className={headerRowClass}>
        <p className={titleClass}>{peopleToKnow.title}</p>
        <button
          type="button"
          className={addButtonClass}
          aria-label={peopleToKnow.addSuggestionLabel}
        >
          <UserPlus className="h-4 w-4" />
        </button>
      </div>

      <div className={listClass}>
        {suggestions.map((person) => (
          <div key={person.id} className={rowClass}>
            <div className={avatarClass}>{person.initials}</div>
            <div>
              <p className={nameClass}>{person.name}</p>
              <p className={roleClass}>{person.role}</p>
            </div>
            <button type="button" className={connectButtonClass}>
              {peopleToKnow.connect}
            </button>
          </div>
        ))}
      </div>

      <button type="button" className={viewAllClass}>
        {peopleToKnow.viewAll}
      </button>
    </div>
  );
}
