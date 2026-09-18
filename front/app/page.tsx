"use client";

import useSWR from "swr";
import { motion } from "motion/react";
import { LinkButton } from "@/components/ui/LinkButton";
import { listEvents } from "@/lib/api/events";

export default function Home() {
  const { data: events } = useSWR("events", listEvents);
  const totalEvents = events?.length ?? null;
  const totalSeatsLeft =
    events?.reduce((sum, event) => sum + Math.max(event.availableSeats - event.reservedSeats, 0), 0) ?? null;

  return (
    <div className="flex flex-1 flex-col items-center justify-center py-16 text-center">
      <motion.span
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="rounded-full border border-white/10 bg-white/5 px-4 py-1 text-xs font-medium tracking-wide text-white/60"
      >
        ✨ EVENT MANAGEMENT, REIMAGINED
      </motion.span>

      <motion.h1
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="mt-6 max-w-3xl text-4xl leading-tight font-black tracking-tight text-white sm:text-6xl"
      >
        夜空にきらめく
        <br />
        <span className="text-gradient">イベント</span>を、見つけて、つくる。
      </motion.h1>

      <motion.p
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="mt-6 max-w-xl text-white/60"
      >
        ライブ・フェス・舞台・コメディ・トークイベント。あらゆる催しを、ひとつの星空の下で管理しよう。
      </motion.p>

      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="mt-10 flex flex-wrap items-center justify-center gap-4"
      >
        <LinkButton href="/events" variant="primary">
          イベントを見る
        </LinkButton>
        <LinkButton href="/events/new" variant="secondary">
          + イベントを作成する
        </LinkButton>
      </motion.div>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.4 }}
        className="mt-16 grid grid-cols-2 gap-6 text-left"
      >
        <div className="glass-panel rounded-3xl px-8 py-6">
          <p className="text-xs tracking-widest text-white/40 uppercase">開催イベント数</p>
          <p className="mt-2 text-3xl font-black text-white">{totalEvents ?? "—"}</p>
        </div>
        <div className="glass-panel rounded-3xl px-8 py-6">
          <p className="text-xs tracking-widest text-white/40 uppercase">残席の合計</p>
          <p className="mt-2 text-3xl font-black text-white">{totalSeatsLeft ?? "—"}</p>
        </div>
      </motion.div>
    </div>
  );
}

