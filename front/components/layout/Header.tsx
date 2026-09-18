import Link from "next/link";
import { LinkButton } from "@/components/ui/LinkButton";

export function Header() {
  return (
    <header className="sticky top-0 z-40 border-b border-white/5 bg-black/20 backdrop-blur-xl">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <Link href="/" className="flex items-center gap-2 text-lg font-bold tracking-tight text-white">
          <span aria-hidden>🌌</span>
          <span className="text-gradient">Stellar Events</span>
        </Link>
        <nav className="flex items-center gap-2">
          <Link
            href="/events"
            className="rounded-full px-4 py-2 text-sm font-medium text-white/70 transition-colors hover:text-white"
          >
            イベント一覧
          </Link>
          <LinkButton href="/events/new" variant="primary" className="hidden sm:inline-flex">
            + イベントを作成
          </LinkButton>
        </nav>
      </div>
    </header>
  );
}
