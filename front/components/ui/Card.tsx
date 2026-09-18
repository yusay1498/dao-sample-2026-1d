import type { HTMLAttributes } from "react";

export function Card({ className = "", ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={`glass-panel rounded-3xl p-6 ${className}`} {...props} />;
}
