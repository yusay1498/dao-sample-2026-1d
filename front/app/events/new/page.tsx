import { EventForm } from "@/components/events/EventForm";

export default function NewEventPage() {
  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="text-3xl font-black tracking-tight text-white">
        新しい<span className="text-gradient">イベント</span>をつくる
      </h1>
      <p className="mt-2 text-sm text-white/50">星空の下で開催する、とっておきのイベントを登録しましょう。</p>
      <div className="mt-8">
        <EventForm mode="create" />
      </div>
    </div>
  );
}
