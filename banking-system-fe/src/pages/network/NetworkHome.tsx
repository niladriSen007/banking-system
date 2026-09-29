import { NetworkSubHeader } from "@/components/customer/network/NetworkSubHeader";
import { ProfileSummaryCard } from "@/components/customer/network/ProfileSummaryCard";
import { PostComposer } from "@/components/customer/network/PostComposer";
import { PostFeed } from "@/components/customer/network/PostFeed";
import { PeopleToKnowCard } from "@/components/customer/network/PeopleToKnowCard";
import { WeeklyInsightCard } from "@/components/customer/network/WeeklyInsightCard";

const gridClass = "mt-6 grid grid-cols-1 gap-6 lg:grid-cols-[260px_1fr_300px]";

const sideStackClass = "space-y-4";

const NetworkHome = () => {
  return (
    <div>
      <NetworkSubHeader />

      <div className={gridClass}>
        <div className={sideStackClass}>
          <ProfileSummaryCard />
        </div>

        <div>
          <PostComposer />
          <PostFeed />
        </div>

        <div className={sideStackClass}>
          <PeopleToKnowCard />
          <WeeklyInsightCard />
        </div>
      </div>
    </div>
  );
};
export default NetworkHome;
