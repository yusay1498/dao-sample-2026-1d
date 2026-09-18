// event-management API (GET /events 等)のレスポンス/リクエスト型定義

export type EventCategoryCode = "LIVE" | "FES" | "STAGE" | "COMEDY" | "TALK_EVENT";

export interface Event {
  eventId: string;
  venueId: string;
  venueName: string | null;
  eventCategoryId: string;
  eventCategoryName: EventCategoryCode | null;
  eventName: string;
  performer: string;
  description: string;
  startTime: string;
  endTime: string;
  availableSeats: number;
  reservedSeats: number;
}

export interface EventInput {
  venueId: string;
  eventCategoryId: string;
  eventName: string;
  performer: string;
  description: string;
  startTime: string;
  endTime: string;
  availableSeats: number;
  reservedSeats: number;
}
