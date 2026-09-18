"use client";

import useSWR from "swr";
import { EventForm } from "@/components/events/EventForm";
import { Skeleton } from "@/components/ui/Skeleton";
import { EmptyState } from "@/components/ui/EmptyState";
import { getEvent } from "@/lib/api/events";

export function EventEdit({ eventId }: { eventId: string }) {
  const { data: event, error, isLoading } = useSWR(["event", eventId], () => getEvent(eventId));

  if (isLoading) {
    return (
      <div className="mx-auto max-w-2xl space-y-4">
        <Skeleton className="h-10 w-1/2" />
        <Skeleton className="h-96" />
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
    <div className="mx-auto max-w-2xl">
      <h1 className="text-3xl font-black tracking-tight text-white">
        イベントを<span className="text-gradient">編集</span>
      </h1>
      <p className="mt-2 text-sm text-white/50">「{event.eventName}」の内容を更新します。</p>
      <div className="mt-8">
        <EventForm mode="edit" eventId={event.eventId} initialEvent={event} />
      </div>
    </div>
  );
}
