const dateTimeFormatter = new Intl.DateTimeFormat("ja-JP", {
  year: "numeric",
  month: "long",
  day: "numeric",
  weekday: "short",
  hour: "2-digit",
  minute: "2-digit",
});

export function formatDateTime(value: string): string {
  return dateTimeFormatter.format(new Date(value));
}

function pad(value: number): string {
  return String(value).padStart(2, "0");
}

// <input type="datetime-local"> 用に "YYYY-MM-DDTHH:mm" (ローカル時刻・オフセットなし) へ変換する
export function toDateTimeLocalValue(value: string): string {
  const date = new Date(value);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

// datetime-local inputの値をOffsetDateTime互換のISO文字列へ変換する
export function fromDateTimeLocalValue(value: string): string {
  return new Date(value).toISOString();
}
