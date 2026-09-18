import { EventEdit } from "@/components/events/EventEdit";

export default async function EditEventPage({ params }: PageProps<"/events/[eventId]/edit">) {
  const { eventId } = await params;
  return <EventEdit eventId={eventId} />;
}
