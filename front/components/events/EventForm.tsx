"use client";

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { motion } from "motion/react";
import { Button } from "@/components/ui/Button";
import { createEvent, updateEvent } from "@/lib/api/events";
import { ApiError } from "@/lib/api/http";
import { fromDateTimeLocalValue, toDateTimeLocalValue } from "@/lib/format";
import type { Event, EventInput } from "@/lib/types/event";

// db/02_data.sqlに投入済みの参照データ。venueId/eventCategoryIdはAPI未提供のため、入力の手がかりとして提示する
const SEED_VENUES = [
  { id: "7f3a1c20-1a8e-4b01-9c11-000000000001", name: "星降るドーム" },
  { id: "7f3a1c20-1a8e-4b01-9c11-000000000002", name: "ミラージュホール" },
  { id: "7f3a1c20-1a8e-4b01-9c11-000000000003", name: "月虹シアター" },
  { id: "7f3a1c20-1a8e-4b01-9c11-000000000004", name: "サンライズアリーナ" },
];

const SEED_CATEGORIES = [
  { id: "8a4b2d10-2b9f-4c02-8d22-000000000101", name: "ライブ" },
  { id: "8a4b2d10-2b9f-4c02-8d22-000000000102", name: "フェス" },
  { id: "8a4b2d10-2b9f-4c02-8d22-000000000103", name: "舞台" },
  { id: "8a4b2d10-2b9f-4c02-8d22-000000000104", name: "コメディ" },
  { id: "8a4b2d10-2b9f-4c02-8d22-000000000105", name: "トークイベント" },
];

interface EventFormProps {
  mode: "create" | "edit";
  eventId?: string;
  initialEvent?: Event;
}

function emptyInput(): EventInput {
  return {
    venueId: "",
    eventCategoryId: "",
    eventName: "",
    performer: "",
    description: "",
    startTime: "",
    endTime: "",
    availableSeats: 0,
    reservedSeats: 0,
  };
}

function toInput(event: Event): EventInput {
  return {
    venueId: event.venueId,
    eventCategoryId: event.eventCategoryId,
    eventName: event.eventName,
    performer: event.performer,
    description: event.description,
    startTime: toDateTimeLocalValue(event.startTime),
    endTime: toDateTimeLocalValue(event.endTime),
    availableSeats: event.availableSeats,
    reservedSeats: event.reservedSeats,
  };
}

const inputClassName =
  "mt-1.5 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-2.5 text-white placeholder:text-white/30 focus:border-violet-400 focus:ring-2 focus:ring-violet-400/40 focus:outline-none";

export function EventForm({ mode, eventId, initialEvent }: EventFormProps) {
  const router = useRouter();
  const [form, setForm] = useState<EventInput>(initialEvent ? toInput(initialEvent) : emptyInput());
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<string[]>([]);

  function update<K extends keyof EventInput>(key: K, value: EventInput[K]) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function handleSubmit(formEvent: FormEvent) {
    formEvent.preventDefault();
    setSubmitting(true);
    setFieldErrors([]);

    const payload: EventInput = {
      ...form,
      startTime: fromDateTimeLocalValue(form.startTime),
      endTime: fromDateTimeLocalValue(form.endTime),
    };

    try {
      const saved = mode === "create" ? await createEvent(payload) : await updateEvent(eventId!, payload);
      toast.success(mode === "create" ? "イベントを作成しました 🎉" : "イベントを更新しました ✨");
      router.push(`/events/${saved.eventId}`);
      router.refresh();
    } catch (error) {
      if (error instanceof ApiError) {
        toast.error(error.message);
        setFieldErrors(error.errors ?? []);
      } else {
        toast.error("予期しないエラーが発生しました");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <motion.form
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      onSubmit={handleSubmit}
      className="space-y-6"
    >
      {fieldErrors.length > 0 && (
        <div className="rounded-2xl border border-rose-400/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-200">
          <ul className="list-inside list-disc space-y-1">
            {fieldErrors.map((message) => (
              <li key={message}>{message}</li>
            ))}
          </ul>
        </div>
      )}

      <div>
        <label className="block text-sm font-medium text-white/70">
          会場ID (venueId)
          <input
            required
            value={form.venueId}
            onChange={(event) => update("venueId", event.target.value)}
            placeholder="7f3a1c20-1a8e-4b01-9c11-000000000001"
            className={inputClassName}
          />
        </label>
        <div className="mt-2 flex flex-wrap gap-2">
          {SEED_VENUES.map((venue) => (
            <button
              type="button"
              key={venue.id}
              onClick={() => update("venueId", venue.id)}
              className="rounded-full bg-white/5 px-3 py-1 text-xs text-white/50 transition-colors hover:bg-white/10 hover:text-white"
            >
              {venue.name}
            </button>
          ))}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-white/70">
          区分ID (eventCategoryId)
          <input
            required
            value={form.eventCategoryId}
            onChange={(event) => update("eventCategoryId", event.target.value)}
            placeholder="8a4b2d10-2b9f-4c02-8d22-000000000101"
            className={inputClassName}
          />
        </label>
        <div className="mt-2 flex flex-wrap gap-2">
          {SEED_CATEGORIES.map((category) => (
            <button
              type="button"
              key={category.id}
              onClick={() => update("eventCategoryId", category.id)}
              className="rounded-full bg-white/5 px-3 py-1 text-xs text-white/50 transition-colors hover:bg-white/10 hover:text-white"
            >
              {category.name}
            </button>
          ))}
        </div>
      </div>

      <label className="block text-sm font-medium text-white/70">
        イベント名
        <input
          required
          maxLength={100}
          value={form.eventName}
          onChange={(event) => update("eventName", event.target.value)}
          className={inputClassName}
        />
      </label>

      <label className="block text-sm font-medium text-white/70">
        出演者
        <input
          required
          maxLength={100}
          value={form.performer}
          onChange={(event) => update("performer", event.target.value)}
          className={inputClassName}
        />
      </label>

      <label className="block text-sm font-medium text-white/70">
        説明
        <textarea
          required
          maxLength={1500}
          rows={4}
          value={form.description}
          onChange={(event) => update("description", event.target.value)}
          className={inputClassName}
        />
      </label>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <label className="block text-sm font-medium text-white/70">
          開演日時
          <input
            required
            type="datetime-local"
            value={form.startTime}
            onChange={(event) => update("startTime", event.target.value)}
            className={inputClassName}
          />
        </label>
        <label className="block text-sm font-medium text-white/70">
          終演日時
          <input
            required
            type="datetime-local"
            value={form.endTime}
            onChange={(event) => update("endTime", event.target.value)}
            className={inputClassName}
          />
        </label>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <label className="block text-sm font-medium text-white/70">
          販売可能席数
          <input
            required
            type="number"
            min={1}
            value={form.availableSeats}
            onChange={(event) => update("availableSeats", Number(event.target.value))}
            className={inputClassName}
          />
        </label>
        <label className="block text-sm font-medium text-white/70">
          購入済席数
          <input
            required
            type="number"
            min={0}
            value={form.reservedSeats}
            onChange={(event) => update("reservedSeats", Number(event.target.value))}
            className={inputClassName}
          />
        </label>
      </div>

      <div className="flex justify-end gap-3 pt-2">
        <Button type="submit" disabled={submitting}>
          {submitting ? "保存中..." : mode === "create" ? "イベントを作成する" : "変更を保存する"}
        </Button>
      </div>
    </motion.form>
  );
}
