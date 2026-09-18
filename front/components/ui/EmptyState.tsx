"use client";

import { motion } from "motion/react";

interface EmptyStateProps {
  title: string;
  description: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      className="glass-panel flex flex-col items-center gap-3 rounded-3xl px-8 py-16 text-center"
    >
      <span aria-hidden className="text-4xl">
        ✨
      </span>
      <h3 className="text-lg font-semibold text-white">{title}</h3>
      <p className="max-w-sm text-sm text-white/60">{description}</p>
    </motion.div>
  );
}
