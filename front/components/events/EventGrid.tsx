"use client";

import { useMemo, useState } from "react";
import useSWR from "swr";
import { AnimatePresence, motion } from "motion/react";
import { EventCard } from "@/components/events/EventCard";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { listEvents } from "@/lib/api/events";
import { categoryLabel } from "@/lib/event-category";
import type { EventCategoryCode } from "@/lib/types/event";

const CATEGORY_FILTERS: EventCategoryCode[] = ["LIVE", "FES", "STAGE", "COMEDY", "TALK_EVENT"];

export function EventGrid() {
  const { data: events, error, isLoading } = useSWR("events", listEvents);
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState<EventCategoryCode | null>(null);

  const filteredEvents = useMemo(() => {
    if (!events) return [];
    return events.filter((event) => {
      const matchesKeyword =
        keyword.trim() === "" ||
        event.eventName.includes(keyword) ||
        event.performer.includes(keyword) ||
        (event.venueName ?? "").includes(keyword);
      const matchesCategory = !category || event.eventCategoryName === category;
      return matchesKeyword && matchesCategory;
    });
  }, [events, keyword, category]);

  if (error) {
    return (
      <EmptyState
        title="イベントを読み込めませんでした"
        description="APIサーバー(event-management)が起動しているか確認してください。"
      />
    );
  }

  return (
    <div>
      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <input
          type="search"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="イベント名・出演者・会場で検索"
          className="glass-panel w-full rounded-full px-5 py-2.5 text-sm text-white placeholder:text-white/40 focus:ring-2 focus:ring-violet-400 focus:outline-none sm:max-w-sm"
        />
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={() => setCategory(null)}
            className={`rounded-full px-4 py-1.5 text-xs font-semibold transition-colors ${
              category === null ? "bg-white text-black" : "glass-panel text-white/60 hover:text-white"
            }`}
          >
            すべて
          </button>
          {CATEGORY_FILTERS.map((code) => (
            <button
              type="button"
              key={code}
              onClick={() => setCategory((current) => (current === code ? null : code))}
              className={`rounded-full px-4 py-1.5 text-xs font-semibold transition-colors ${
                category === code ? "bg-white text-black" : "glass-panel text-white/60 hover:text-white"
              }`}
            >
              {categoryLabel(code)}
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <Skeleton key={index} className="h-56" />
          ))}
        </div>
      ) : filteredEvents.length === 0 ? (
        <EmptyState
          title="イベントが見つかりません"
          description="条件を変えて検索するか、新しいイベントを作成しましょう。"
        />
      ) : (
        <motion.div layout className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <AnimatePresence mode="popLayout">
            {filteredEvents.map((event) => (
              <EventCard key={event.eventId} event={event} />
            ))}
          </AnimatePresence>
        </motion.div>
      )}
    </div>
  );
}
