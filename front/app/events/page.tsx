import { EventGrid } from "@/components/events/EventGrid";

export default function EventsPage() {
  return (
    <div>
      <div className="mb-10">
        <h1 className="text-3xl font-black tracking-tight text-white sm:text-4xl">
          イベント<span className="text-gradient">一覧</span>
        </h1>
        <p className="mt-2 text-sm text-white/50">気になるイベントを見つけて、詳細をチェックしよう。</p>
      </div>
      <EventGrid />
    </div>
  );
}
