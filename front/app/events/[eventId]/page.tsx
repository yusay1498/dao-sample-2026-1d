import { EventDetail } from "@/components/events/EventDetail";

export default async function EventDetailPage({ params }: PageProps<"/events/[eventId]">) {
  const { eventId } = await params;
  return <EventDetail eventId={eventId} />;
}
