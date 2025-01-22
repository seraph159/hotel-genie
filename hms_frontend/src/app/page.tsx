"use client";

import { useState, useEffect } from "react";
import RoomsList from "@/components/RoomsList";
import DatePicker from "@/components/DatePicker";
import { fetchRooms, createBooking, deleteBooking } from "@/lib/api";
import { useAuth } from "./context/AuthContext";

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
      setError(null); // Clear error message
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
      <h1 className="text-4xl font-bold text-center text-blue-700 mb-6">
        Welcome to HotelGenie
      </h1>
      <p className="text-center text-gray-600 mb-8">
        Your perfect getaway is just a few clicks away.
      </p>
      <div className="flex flex-col items-center bg-white rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-semibold text-gray-800 mb-4">
          Find Your Perfect Room
        </h2>
        <div className="flex flex-wrap gap-4 items-end">
          <DatePicker label="Check-in Date" value={startDate} onChange={setStartDate} />
          <DatePicker label="Check-out Date" value={endDate} onChange={setEndDate} />
          <div>
            <label className="block mb-1 font-medium text-gray-700">Min Occupancy</label>
            <input
              type="number"
              value={minOccupancy}
              onChange={(e) => setMinOccupancy(parseInt(e.target.value, 10))}
              className="border rounded p-2 w-full focus:outline-blue-500"
            />
          </div>
          <button
            onClick={handleFetchRooms}
            className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Search Rooms
          </button>
        </div>
        {error && <div className="text-red-600 mt-4">{error}</div>}
      </div>
      <RoomsList rooms={rooms} onBook={handleBook} onCancel={handleCancel} />
    </div>
  );
}
