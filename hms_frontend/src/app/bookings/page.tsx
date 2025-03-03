"use client";

import { useState, useEffect, useCallback } from "react";
import { deleteBooking, fetchBookings } from "@/lib/api";
import { Booking } from "@/lib/types";
import BookingCard from "@/components/BookingCard";
import { FaCalendarCheck, FaExclamationCircle, FaSpinner } from "react-icons/fa";
import { motion } from "framer-motion";

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true); // loading state
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) setAccessToken(token);
    else setLoading(false); // If no token, stop loading
  }, []);

  const handleFetchBookings = useCallback(async () => {
    if (!accessToken) {
      setError("Please log in to view your bookings.");
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const fetchedBookings = await fetchBookings(accessToken);
      setBookings(fetchedBookings);
      setError(null);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  }, [accessToken]);

  useEffect(() => {
    handleFetchBookings();
  }, [accessToken, handleFetchBookings]);

  const handleCancelBooking = async (roomNr: string, startDate: string) => {
    if (!accessToken || !confirm("Are you sure you want to cancel this booking?")) return;
    try {
      await deleteBooking(roomNr, startDate, accessToken);
      handleFetchBookings();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  };

  return (
    <div className="container mx-auto px-4 py-8 min-h-screen bg-gray-100 flex flex-col items-center">
      <motion.h2
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-4xl font-bold text-blue-700 mb-6 flex items-center gap-2"
      >
        <FaCalendarCheck /> My Bookings
      </motion.h2>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <FaSpinner className="text-4xl text-blue-600 animate-spin" />
        </div>
      ) : (
        <>
          {error && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="text-red-600 mb-4 flex items-center gap-2"
            >
              <FaExclamationCircle /> {error}
            </motion.div>
          )}
          {bookings.length === 0 ? (
            <p className="text-gray-600 text-center">No bookings found.</p>
          ) : (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.2 }}
              className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
            >
              {bookings.map((booking) => (
                <BookingCard
                  key={`${booking.roomNr}-${booking.startDate}`}
                  booking={booking}
                  onCancel={() => handleCancelBooking(booking.roomNr, booking.startDate)}
                />
              ))}
            </motion.div>
          )}
        </>
      )}
    </div>
  );
}