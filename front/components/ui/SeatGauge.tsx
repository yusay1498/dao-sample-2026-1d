"use client";

import { useEffect, useState } from "react";
import { motion } from "motion/react";

function useCountUp(target: number, durationMs = 700): number {
  const [value, setValue] = useState(0);

  useEffect(() => {
    let frame: number;
    const start = performance.now();

    const tick = (now: number) => {
      const progress = Math.min((now - start) / durationMs, 1);
      setValue(Math.round(target * progress));
      if (progress < 1) {
        frame = requestAnimationFrame(tick);
      }
    };

    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [target, durationMs]);

  return value;
}

interface SeatGaugeProps {
  reservedSeats: number;
  availableSeats: number;
}

export function SeatGauge({ reservedSeats, availableSeats }: SeatGaugeProps) {
  const ratio = availableSeats > 0 ? Math.min(reservedSeats / availableSeats, 1) : 0;
  const remaining = Math.max(availableSeats - reservedSeats, 0);
  const animatedRemaining = useCountUp(remaining);
  const isSoldOut = remaining <= 0;

  return (
    <div>
      <div className="flex items-baseline justify-between text-sm">
        <span className="text-white/60">残席</span>
        <span className={`font-mono text-lg font-bold ${isSoldOut ? "text-rose-400" : "text-white"}`}>
          {isSoldOut ? "SOLD OUT" : `${animatedRemaining} 席`}
        </span>
      </div>
      <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-white/10">
        <motion.div
          className="h-full rounded-full bg-gradient-to-r from-cyan-400 via-violet-500 to-pink-500"
          initial={{ width: 0 }}
          animate={{ width: `${ratio * 100}%` }}
          transition={{ duration: 0.9, ease: "easeOut" }}
        />
      </div>
      <p className="mt-1 text-xs text-white/40">
        {reservedSeats} / {availableSeats} 席 予約済み
      </p>
    </div>
  );
}
