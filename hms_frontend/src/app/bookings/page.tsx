"use client";

import { useState, useEffect } from "react";
import { deleteBooking, fetchBookings } from "@/lib/api";
import { Booking } from "@/lib/types"; // booking type
import BookingCard from "@/components/BookingCard";

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) setAccessToken(token);
  }, []);

  const handleFetchBookings = async () => {
    if (!accessToken) return;
    try {
      const fetchedBookings = await fetchBookings(accessToken);
      setBookings(fetchedBookings);
    } catch (err: any) {
      setError(err.message);
    }
  };

  useEffect(() => {
    handleFetchBookings();
  }, [accessToken]);

  const handleCancelBooking = async (roomNr: string, startDate: string, accessToken: string) => {
    try {
      await deleteBooking(roomNr, startDate, accessToken);
      handleFetchBookings();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold mb-4">My Bookings</h2>
      {error && <div className="text-red-600">{error}</div>}
      {bookings.length === 0 ? (
        <p>No bookings found.</p>
      ) : (
        <div className="grid gap-4">
          {bookings.map((booking: Booking) => (
            <BookingCard
              key={`${booking.roomNr}-${booking.startDate}`} // Use a combination of roomNr and startDate as the key
              booking={booking}
              onCancel={() => accessToken && handleCancelBooking(booking.roomNr, booking.startDate, accessToken)}
            />
          ))}
        </div>
      )}
    </div>
  );
}