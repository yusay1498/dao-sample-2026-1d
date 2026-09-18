"use client";

import Link from "next/link";
import { motion } from "motion/react";
import type { ComponentProps } from "react";

type Variant = "primary" | "secondary" | "ghost";

const variantClasses: Record<Variant, string> = {
  primary:
    "bg-gradient-to-r from-violet-500 to-pink-500 text-white shadow-lg shadow-violet-500/25 hover:shadow-violet-500/40",
  secondary: "glass-panel text-white/90 hover:bg-white/10",
  ghost: "text-white/70 hover:bg-white/5 hover:text-white",
};

const MotionLink = motion(Link);

interface LinkButtonProps extends ComponentProps<typeof MotionLink> {
  variant?: Variant;
}

export function LinkButton({ variant = "primary", className = "", ...props }: LinkButtonProps) {
  return (
    <MotionLink
      whileHover={{ scale: 1.03 }}
      whileTap={{ scale: 0.97 }}
      transition={{ type: "spring", stiffness: 400, damping: 20 }}
      className={`inline-flex items-center justify-center gap-2 rounded-full px-5 py-2.5 text-sm font-semibold transition-colors ${variantClasses[variant]} ${className}`}
      {...props}
    />
  );
}
