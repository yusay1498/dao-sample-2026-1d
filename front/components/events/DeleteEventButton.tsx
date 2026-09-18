"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { deleteEvent } from "@/lib/api/events";
import { ApiError } from "@/lib/api/http";

interface DeleteEventButtonProps {
  eventId: string;
  eventName: string;
}

export function DeleteEventButton({ eventId, eventName }: DeleteEventButtonProps) {
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const [pending, setPending] = useState(false);

  async function handleConfirm() {
    setPending(true);
    try {
      await deleteEvent(eventId);
      toast.success("イベントを削除しました");
      router.push("/events");
      router.refresh();
    } catch (error) {
      toast.error(error instanceof ApiError ? error.message : "削除に失敗しました");
    } finally {
      setPending(false);
      setOpen(false);
    }
  }

  return (
    <>
      <Button variant="danger" onClick={() => setOpen(true)}>
        削除する
      </Button>
      <ConfirmDialog
        open={open}
        title="イベントを削除しますか？"
        description={`「${eventName}」を削除すると元に戻せません。`}
        pending={pending}
        onCancel={() => setOpen(false)}
        onConfirm={handleConfirm}
      />
    </>
  );
}
