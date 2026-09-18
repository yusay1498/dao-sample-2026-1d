"use client";

import Link from "next/link";
import useSWR from "swr";
import { motion } from "motion/react";
import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { LinkButton } from "@/components/ui/LinkButton";
import { SeatGauge } from "@/components/ui/SeatGauge";
import { Skeleton } from "@/components/ui/Skeleton";
import { EmptyState } from "@/components/ui/EmptyState";
import { DeleteEventButton } from "@/components/events/DeleteEventButton";
import { getEvent } from "@/lib/api/events";
import { categoryAccent, categoryLabel } from "@/lib/event-category";
import { formatDateTime } from "@/lib/format";

export function EventDetail({ eventId }: { eventId: string }) {
  const { data: event, error, isLoading } = useSWR(["event", eventId], () => getEvent(eventId));

  if (isLoading) {
    return (
      <div className="mx-auto max-w-3xl space-y-4">
        <Skeleton className="h-10 w-2/3" />
        <Skeleton className="h-64" />
      </div>
    );
  }

  if (error || !event) {
    return (
      <EmptyState
        title="イベントが見つかりません"
        description="削除されたか、URLが正しくない可能性があります。"
      />
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="mx-auto max-w-3xl">
      <Link href="/events" className="text-sm text-white/50 transition-colors hover:text-white">
        ← イベント一覧へ戻る
      </Link>

      <Card className="mt-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <Badge className={categoryAccent(event.eventCategoryName)}>
            {categoryLabel(event.eventCategoryName)}
          </Badge>
          <span className="text-sm text-white/50">{event.venueName ?? "会場未設定"}</span>
        </div>

        <h1 className="mt-4 text-3xl font-black text-white">{event.eventName}</h1>
        <p className="mt-1 text-lg text-white/70">{event.performer}</p>

        <div className="mt-6 grid grid-cols-1 gap-4 text-sm text-white/60 sm:grid-cols-2">
          <div>
            <p className="text-white/40">開演</p>
            <p className="text-white">{formatDateTime(event.startTime)}</p>
          </div>
          <div>
            <p className="text-white/40">終演</p>
            <p className="text-white">{formatDateTime(event.endTime)}</p>
          </div>
        </div>

        <p className="mt-6 whitespace-pre-wrap text-white/80">{event.description}</p>

        <div className="mt-8">
          <SeatGauge reservedSeats={event.reservedSeats} availableSeats={event.availableSeats} />
        </div>

        <div className="mt-8 flex flex-wrap gap-3 border-t border-white/10 pt-6">
          <LinkButton href={`/events/${event.eventId}/edit`} variant="secondary">
            編集する
          </LinkButton>
          <DeleteEventButton eventId={event.eventId} eventName={event.eventName} />
        </div>
      </Card>
    </motion.div>
  );
}
