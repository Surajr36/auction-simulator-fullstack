import { useState, useEffect } from "react";

type Props = {
  timerEndAt?: string; // ISO 8601 timestamp from backend
  status: string;
  adminPaused?: boolean;
  timerRemainingOnPause?: number; // milliseconds remaining when paused
};

/**
 * CountdownTimer Component
 *
 * Displays real-time countdown until timer expires.
 *
 * Features:
 * - Updates every second (client-side, no backend calls)
 * - Red color when < 10 seconds (visual urgency)
 * - Auto-hides when player is not LIVE
 * - Format: M:SS (e.g., 1:45, 0:08)
 *
 * Why client-side countdown?
 * - Backend sends timerEndAt (absolute timestamp)
 * - Frontend calculates remaining = timerEndAt - NOW
 * - No network latency issues (uses local clock)
 *
 * Important:
 * - Frontend timer is a VISUAL HINT only
 * - Backend validates timer on every bid
 * - Server time is authoritative (prevents manipulation)
 */
export function CountdownTimer({ timerEndAt, status, adminPaused, timerRemainingOnPause }: Props) {
  const [remaining, setRemaining] = useState<number>(() => {
    if (status !== "LIVE" || !timerEndAt) return 0;
    // If admin-paused, show frozen time
    if (adminPaused && timerRemainingOnPause != null) {
      return Math.max(0, Math.floor(timerRemainingOnPause / 1000));
    }
    const now = Date.now();
    const end = new Date(timerEndAt).getTime();
    return Math.max(0, Math.floor((end - now) / 1000));
  });

  useEffect(() => {
    if (status !== "LIVE" || !timerEndAt) {
      return;
    }

    // If paused, show frozen remaining time and don't tick
    if (adminPaused && timerRemainingOnPause != null) {
      setRemaining(Math.max(0, Math.floor(timerRemainingOnPause / 1000)));
      return;
    }

    const calculateRemaining = () => {
      const now = Date.now();
      const end = new Date(timerEndAt).getTime();
      return Math.max(0, Math.floor((end - now) / 1000));
    };

    const interval = setInterval(() => {
      const newRemaining = calculateRemaining();
      setRemaining(newRemaining);

      if (newRemaining <= 0) {
        clearInterval(interval);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [status, timerEndAt, adminPaused, timerRemainingOnPause]);

  if (status !== "LIVE") {
    return null;
  }

  const minutes = Math.floor(remaining / 60);
  const seconds = remaining % 60;

  // Visual urgency: Red when < 10 seconds, yellow when paused
  const colorClass = adminPaused
    ? "text-yellow-600"
    : remaining < 10
      ? "text-red-600"
      : "text-blue-600";
  const pulseClass = adminPaused ? "" : remaining < 10 ? "animate-pulse" : "";

  return (
    <div className={`text-2xl font-bold ${colorClass} ${pulseClass}`}>
      {adminPaused ? (
        <span>⏸ PAUSED — {minutes}:{seconds.toString().padStart(2, "0")}</span>
      ) : (
        <span>{minutes}:{seconds.toString().padStart(2, "0")}</span>
      )}
    </div>
  );
}
