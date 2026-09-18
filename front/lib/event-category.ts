// EventCategoryName(バックエンドの列挙子名)を画面表示用のラベル/配色に変換する

export const EVENT_CATEGORY_LABELS: Record<string, string> = {
  LIVE: "ライブ",
  FES: "フェス",
  STAGE: "舞台",
  COMEDY: "コメディ",
  TALK_EVENT: "トークイベント",
};

export const EVENT_CATEGORY_ACCENTS: Record<string, string> = {
  LIVE: "from-fuchsia-500 to-pink-500",
  FES: "from-amber-400 to-orange-500",
  STAGE: "from-violet-500 to-indigo-500",
  COMEDY: "from-lime-400 to-emerald-500",
  TALK_EVENT: "from-cyan-400 to-sky-500",
};

export function categoryLabel(code: string | null | undefined): string {
  if (!code) return "未分類";
  return EVENT_CATEGORY_LABELS[code] ?? code;
}

export function categoryAccent(code: string | null | undefined): string {
  if (!code) return "from-slate-400 to-slate-500";
  return EVENT_CATEGORY_ACCENTS[code] ?? "from-slate-400 to-slate-500";
}
