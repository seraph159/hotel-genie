"use client";
import { useEffect, useState } from "react";
import { FaCalendarAlt, FaUsers, FaComment, FaRobot, FaExclamationCircle } from "react-icons/fa";
import { motion } from "framer-motion";


type RecommendationResponse = {
  rooms: { roomNr: string; capacity: number; description: string }[];
};

const RecommendationsPage = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [minOccupancy, setMinOccupancy] = useState(1);
  const [preferences, setPreferences] = useState("");
  const [recommendedRooms, setRecommendedRooms] = useState<RecommendationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) setAccessToken(token);
  }, []);

  const handleRecommend = async (e: React.FormEvent) => {
    e.preventDefault();

    // Basic validation
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

    if (!accessToken) {
      setError("Please log in to get recommendations.");
      return;
    }

    try {
      const url = `/api/recommendations?startDate=${startDate}&endDate=${endDate}&minOccupancy=${minOccupancy}&preferences=${encodeURIComponent(preferences)}`;
      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch recommendations.");
      }

      const data: RecommendationResponse = await response.json();
      setRecommendedRooms(data);
      setError(null);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("An unexpected error occurred.");
      }
    }
  };

  return (
    <div className="container flex flex-col items-center mx-auto px-4 py-8 min-h-screen bg-gray-100">
      {/* Animated Title */}
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-4xl font-bold text-center text-blue-700 mb-6 flex items-center gap-2"
      >
        <FaRobot /> Ask AI for Recommendations
      </motion.h1>
      <p className="text-center text-gray-600 mb-8 italic">
        Let our AI find the perfect room for your stay!
      </p>

      {/* Form Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-white rounded-lg p-6 shadow-md w-full max-w-lg border border-gray-200"
      >
        <form onSubmit={handleRecommend} className="flex flex-col gap-4">
          <div className="flex flex-col">
            <label className="mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaCalendarAlt /> Check-in Date
            </label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div className="flex flex-col">
            <label className="mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaCalendarAlt /> Check-out Date
            </label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div className="flex flex-col">
            <label className="mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaUsers /> Min Occupancy
            </label>
            <input
              type="number"
              value={minOccupancy}
              onChange={(e) => setMinOccupancy(parseInt(e.target.value, 10))}
              className="p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div className="flex flex-col">
            <label className="mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaComment /> Preferences
            </label>
            <input
              type="text"
              value={preferences}
              onChange={(e) => setPreferences(e.target.value)}
              placeholder="e.g., ocean view, near pool"
              className="p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <button
            type="submit"
            className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 justify-center hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
          >
            <FaRobot /> Recommend Rooms
          </button>
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

      {/* Recommended Rooms */}
      {recommendedRooms && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="mt-8 w-full max-w-2xl"
        >
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">Recommended Rooms</h2>
          {recommendedRooms.rooms.length > 0 ? (
            <div className="grid grid-cols-1 gap-4">
              {recommendedRooms.rooms.map((room) => (
                <div
                  key={room.roomNr}
                  className="bg-white p-4 rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow duration-200"
                >
                  <h3 className="text-lg font-semibold text-gray-800">Room {room.roomNr}</h3>
                  <p className="text-gray-600">Capacity: {room.capacity}</p>
                  <p className="text-gray-600">{room.description}</p>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-600">No recommendations found for your preferences.</p>
          )}
        </motion.div>
      )}
    </div>
  );
};

export default RecommendationsPage;