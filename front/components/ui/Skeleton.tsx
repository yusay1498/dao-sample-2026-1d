export function Skeleton({ className = "" }: { className?: string }) {
  return <div className={`skeleton-shimmer rounded-2xl ${className}`} />;
}
