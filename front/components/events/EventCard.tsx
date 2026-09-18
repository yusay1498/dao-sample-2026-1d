"use client";

import Link from "next/link";
import { motion } from "motion/react";
import { Badge } from "@/components/ui/Badge";
import { SeatGauge } from "@/components/ui/SeatGauge";
import { categoryAccent, categoryLabel } from "@/lib/event-category";
import { formatDateTime } from "@/lib/format";
import type { Event } from "@/lib/types/event";

export function EventCard({ event }: { event: Event }) {
  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -12, scale: 0.96 }}
      whileHover={{ y: -6 }}
      transition={{ type: "spring", stiffness: 300, damping: 24 }}
    >
      <Link
        href={`/events/${event.eventId}`}
        className="glass-panel group block h-full rounded-3xl p-6 transition-colors hover:bg-white/10"
      >
        <div className="flex items-start justify-between gap-3">
          <Badge className={categoryAccent(event.eventCategoryName)}>
            {categoryLabel(event.eventCategoryName)}
          </Badge>
          <span className="text-xs text-white/40">{event.venueName ?? "会場未設定"}</span>
        </div>
        <h3 className="group-hover:text-gradient mt-4 text-xl font-bold text-white transition-colors">
          {event.eventName}
        </h3>
        <p className="mt-1 text-sm text-white/60">{event.performer}</p>
        <p className="mt-3 text-xs text-white/40">{formatDateTime(event.startTime)} 〜</p>
        <div className="mt-5">
          <SeatGauge reservedSeats={event.reservedSeats} availableSeats={event.availableSeats} />
        </div>
      </Link>
    </motion.div>
  );
}
