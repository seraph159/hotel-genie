"use client";

import { useCallback, useEffect, useState } from "react";
import { FaCalendarCheck, FaPlus, FaEdit, FaTrash, FaSave, FaTimes, FaExclamationCircle } from "react-icons/fa";
import { motion } from "framer-motion";

interface Booking {
  startDate: string;
  roomNr: string;
  price: number;
  clientEmail: string;
  room: {
    roomNr: string;
    roomType: string;
  };
  endDate: string;
}

interface FormBooking {
  startDate: string;
  endDate: string;
  roomNr: string;
  clientEmail: string;
}

export default function ManageBookings() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [form, setForm] = useState<Partial<FormBooking>>({});
  const [editingBooking, setEditingBooking] = useState<Booking | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  // Memoized function to fetch bookings
  const fetchBookings = useCallback(async () => {
    try {
      const response = await fetch("/api/bookings/all", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (!response.ok) throw new Error("Failed to fetch bookings");
      const data = await response.json();
      setBookings(data);
      setError(null);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  }, [accessToken]);

  // Load token and fetch bookings
  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) {
      setAccessToken(token);
    }
  }, []);

  useEffect(() => {
    if (accessToken) {
      fetchBookings();
    }
  }, [accessToken, fetchBookings]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Basic validation
    if (!form.startDate || !form.endDate || !form.roomNr || !form.clientEmail) {
      setError("All fields are required.");
      return;
    }
    const start = new Date(form.startDate);
    const end = new Date(form.endDate);
    if (start >= end) {
      setError("Start date must be before end date.");
      return;
    }

    const url = "/api/bookings/admin"; // Same endpoint for POST and PUT
    const method = editingBooking ? "PUT" : "POST";

    try {
      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error(`Failed to ${editingBooking ? "update" : "add"} booking`);
      setForm({});
      setEditingBooking(null);
      fetchBookings();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  };

  const handleEdit = (booking: Booking) => {
    setForm({
      startDate: booking.startDate,
      endDate: booking.endDate,
      roomNr: booking.roomNr,
      clientEmail: booking.clientEmail,
    });
    setEditingBooking(booking);
  };

  const handleDelete = async (startDate: string, roomNr: string) => {
    if (!confirm("Are you sure you want to delete this booking?")) return;

    try {
      const response = await fetch(`/api/bookings/admin/${startDate}/${roomNr}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error("Failed to delete booking");
      fetchBookings();
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  };

  return (
    <main className="min-h-screen bg-gray-100 p-6">
      {/* Header */}
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-4xl font-bold text-blue-700 mb-8 flex items-center gap-2"
      >
        <FaCalendarCheck /> Manage Bookings
      </motion.h1>

      {/* Form Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200 mb-8"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          {editingBooking ? "Edit Booking" : "Add New Booking"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block mb-1 font-medium text-gray-700">Start Date</label>
            <input
              name="startDate"
              type="date"
              value={form.startDate || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
              disabled={!!editingBooking} // Disable startDate on edit
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">End Date</label>
            <input
              name="endDate"
              type="date"
              value={form.endDate || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Room Number</label>
            <input
              name="roomNr"
              type="text"
              value={form.roomNr || ""}
              onChange={handleInputChange}
              placeholder="e.g., 101"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Client Email</label>
            <input
              name="clientEmail"
              type="email"
              value={form.clientEmail || ""}
              onChange={handleInputChange}
              placeholder="client@example.com"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div className="md:col-span-2 flex gap-4">
            <button
              type="submit"
              className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            >
              {editingBooking ? <FaSave /> : <FaPlus />}
              {editingBooking ? "Update Booking" : "Add Booking"}
            </button>
            {editingBooking && (
              <button
                type="button"
                onClick={() => {
                  setEditingBooking(null);
                  setForm({});
                }}
                className="bg-gray-500 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-500 transition-all duration-200"
              >
                <FaTimes /> Cancel
              </button>
            )}
          </div>
        </form>
        {error && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-red-600 mt-4 flex items-center gap-2"
          >
            <FaExclamationCircle /> {error}
          </motion.p>
        )}
      </motion.div>

      {/* Bookings List */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">All Bookings</h2>
        {bookings.length === 0 ? (
          <p className="text-gray-600">No bookings found.</p>
        ) : (
          <ul className="space-y-4">
            {bookings.map((booking) => (
              <li
                key={`${booking.startDate}-${booking.roomNr}`}
                className="border-b p-4 flex justify-between items-center hover:bg-gray-50 transition-colors duration-200"
              >
                <div>
                  <strong className="text-gray-800">Room #{booking.room.roomNr}</strong> -{" "}
                  {booking.room.roomType}
                  <p className="text-gray-600">
                    Client: {booking.clientEmail}
                    <br />
                    Dates: {new Date(booking.startDate).toLocaleDateString()} to{" "}
                    {new Date(booking.endDate).toLocaleDateString()}
                    <br />
                    Price: ${booking.price.toFixed(2)}
                  </p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEdit(booking)}
                    className="bg-yellow-500 text-white py-1 px-3 rounded flex items-center gap-1 hover:bg-yellow-600 transition-all duration-200"
                  >
                    <FaEdit /> Edit
                  </button>
                  <button
                    onClick={() => handleDelete(booking.startDate, booking.roomNr)}
                    className="bg-red-500 text-white py-1 px-3 rounded flex items-center gap-1 hover:bg-red-600 transition-all duration-200"
                  >
                    <FaTrash /> Delete
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </motion.div>
    </main>
  );
}