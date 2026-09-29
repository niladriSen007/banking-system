import en from "@/locales/en.json";
import { MessageCircle, MoreHorizontal, Repeat2, Send, ThumbsUp } from "lucide-react";

const { feed } = en.network;

export type FeedPostData = {
  id: string;
  authorName: string;
  authorRole: string;
  authorInitials: string;
  postedAt: string;
  content: string;
  imageSrc?: string;
  imageAlt?: string;
  reactionsCount: number;
  commentsCount: number;
  repostsCount: number;
};

const cardClass = "rounded-2xl border border-border/60 bg-white p-4";

const headerRowClass = "flex items-start gap-3";

const avatarClass =
  "flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-dashboard-network-avatar-from to-dashboard-network-avatar-to text-sm font-semibold text-white";

const authorNameRowClass = "flex items-center gap-1.5 text-sm font-semibold text-foreground";

const connectionBadgeClass = "text-xs font-normal text-muted-foreground";

const authorRoleClass = "text-xs text-muted-foreground";

const moreButtonClass =
  "ml-auto flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-muted-foreground transition hover:bg-muted";

const contentClass = "mt-3 text-sm text-foreground";

const imageWrapClass = "mt-3 overflow-hidden rounded-xl";

const metaRowClass =
  "mt-3 flex items-center justify-between border-b border-border/60 pb-3 text-xs text-muted-foreground";

const actionsRowClass = "flex items-center justify-between pt-1";

const actionButtonClass =
  "flex flex-1 items-center justify-center gap-2 rounded-xl px-3 py-2 text-xs font-medium text-muted-foreground transition hover:bg-muted";

export function FeedPost({ post }: { post: FeedPostData }) {
  return (
    <div className={cardClass}>
      <div className={headerRowClass}>
        <div className={avatarClass}>{post.authorInitials}</div>
        <div>
          <p className={authorNameRowClass}>
            {post.authorName}
            <span className={connectionBadgeClass}>· {feed.connectionDegree}</span>
          </p>
          <p className={authorRoleClass}>
            {post.authorRole} · {post.postedAt}
          </p>
        </div>
        <button
          type="button"
          className={moreButtonClass}
          aria-label={feed.moreOptions}
        >
          <MoreHorizontal className="h-4 w-4" />
        </button>
      </div>

      <p className={contentClass}>{post.content}</p>

      {post.imageSrc ? (
        <div className={imageWrapClass}>
          <img src={post.imageSrc} alt={post.imageAlt} className="w-full" />
        </div>
      ) : null}

      <div className={metaRowClass}>
        <span>{feed.reactionsLabel.replace("{count}", String(post.reactionsCount))}</span>
        <span>
          {feed.commentsLabel.replace("{count}", String(post.commentsCount))} ·{" "}
          {feed.repostsLabel.replace("{count}", String(post.repostsCount))}
        </span>
      </div>

      <div className={actionsRowClass}>
        <button type="button" className={actionButtonClass}>
          <ThumbsUp className="h-4 w-4" />
          {feed.like}
        </button>
        <button type="button" className={actionButtonClass}>
          <MessageCircle className="h-4 w-4" />
          {feed.comment}
        </button>
        <button type="button" className={actionButtonClass}>
          <Repeat2 className="h-4 w-4" />
          {feed.repost}
        </button>
        <button type="button" className={actionButtonClass}>
          <Send className="h-4 w-4" />
          {feed.send}
        </button>
      </div>
    </div>
  );
}
