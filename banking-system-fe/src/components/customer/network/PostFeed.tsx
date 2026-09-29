import { FeedPost, type FeedPostData } from "@/components/customer/network/FeedPost";

const feedWrapClass = "mt-4 space-y-4";

// Placeholder posts until a feed API is available.
const posts: FeedPostData[] = [
  {
    id: "1",
    authorName: "Maya Chen",
    authorRole: "Design Director at Arc Studio",
    authorInitials: "MC",
    postedAt: "2h",
    content:
      "The best products don't just solve problems, they make people feel understood. Proud of what our team explored this week around more human onboarding experiences.",
    reactionsCount: 124,
    commentsCount: 18,
    repostsCount: 4,
  },
];

export function PostFeed() {
  return (
    <div className={feedWrapClass}>
      {posts.map((post) => (
        <FeedPost key={post.id} post={post} />
      ))}
    </div>
  );
}
