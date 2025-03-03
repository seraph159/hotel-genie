"use client";

import { useState, useEffect } from "react";
import RoomsList from "@/components/RoomsList";
import DatePicker from "@/components/DatePicker";
import { fetchRooms, createBooking, deleteBooking } from "@/lib/api";
import { useAuth } from "./context/AuthContext";
import { FaSearch, FaExclamationCircle, FaCalendarAlt, FaHotel } from "react-icons/fa";
import { motion } from "framer-motion";

export default function HotelGenieHomePage() {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [minOccupancy, setMinOccupancy] = useState(1);
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const { role } = useAuth();

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) setAccessToken(token);
  }, []);

  const handleFetchRooms = async () => {
    if (!startDate || !endDate) {
      setError("Please select check-in and check-out dates.");
      return;
    }

    const selectedStartDate = new Date(startDate);
    const selectedEndDate = new Date(endDate);
    const today = new Date();

    selectedStartDate.setHours(0, 0, 0, 0);
    selectedEndDate.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    if (selectedStartDate < today || selectedEndDate < today) {
      setError("Dates cannot be in the past. Please choose valid dates.");
      return;
    }

    if (selectedStartDate >= selectedEndDate) {
      setError("Check-in date must be earlier than the check-out date.");
      return;
    }

    try {
      const fetchedRooms = await fetchRooms(startDate, endDate, minOccupancy);
      setRooms(fetchedRooms);
      setError(null);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        alert("An unexpected error occurred.");
      }
    }
  };

  const handleBook = async (roomNr: string) => {
    if (!accessToken) return alert("Please log in to book a room.");
    if (role === "ROLE_ADMIN") {
      alert("Admins cannot book rooms here. Use the admin dashboard.");
      return;
    }
    try {
      await createBooking(roomNr, startDate, endDate, accessToken);
      handleFetchRooms();
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert("An unexpected error occurred.");
      }
    }
  };

  const handleCancel = async (roomNr: string) => {
    if (!accessToken) return alert("Please log in to cancel a booking.");
    try {
      await deleteBooking(roomNr, startDate, accessToken);
      alert("Booking canceled successfully.");
      handleFetchRooms();
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert("An unexpected error occurred.");
      }
    }
  };

  return (
    <div className="container flex flex-col items-center mx-auto px-4 py-8">
      {/* Animated Title */}
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-4xl font-bold text-center text-blue-700 mb-6 flex items-center gap-2"
      >
        <FaHotel /> Welcome to HotelGenie
      </motion.h1>
      <p className="text-center text-gray-600 mb-8 italic">Your perfect getaway is just a few clicks away.</p>

      {/* Search Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="flex flex-col items-center bg-white rounded-lg p-6 mb-8 shadow-md w-full max-w-2xl border border-gray-200"
      >
        <h2 className="text-2xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
          <FaCalendarAlt /> Find Your Perfect Room
        </h2>
        <div className="flex flex-wrap gap-4 justify-center">
          <DatePicker label="Check-in Date" value={startDate} onChange={setStartDate} />
          <DatePicker label="Check-out Date" value={endDate} onChange={setEndDate} />
          <div>
            <label className="block mb-1 font-medium text-gray-700">Min Occupancy</label>
            <input
              type="number"
              value={minOccupancy}
              onChange={(e) => setMinOccupancy(parseInt(e.target.value, 10))}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <button
            onClick={handleFetchRooms}
            className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
          >
            <FaSearch /> Search Rooms
          </button>
        </div>
        {error && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-red-600 mt-4 flex items-center gap-2"
          >
            <FaExclamationCircle /> {error}
          </motion.div>
        )}
      </motion.div>

      {/* Rooms List */}
      <RoomsList rooms={rooms} onBook={handleBook} onCancel={handleCancel} />
    </div>
  );
}