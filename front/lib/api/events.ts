import { http } from "@/lib/api/http";
import type { Event, EventInput } from "@/lib/types/event";

export const listEvents = () => http.get<Event[]>("/events");

export const getEvent = (eventId: string) => http.get<Event>(`/events/${eventId}`);

export const createEvent = (input: EventInput) => http.post<Event>("/events", input);

// バックエンドはPUTボディのeventIdとURLパスの一致を要求するため、ここで合成する
export const updateEvent = (eventId: string, input: EventInput) =>
  http.put<Event>(`/events/${eventId}`, { eventId, ...input });

export const deleteEvent = (eventId: string) => http.delete(`/events/${eventId}`);
