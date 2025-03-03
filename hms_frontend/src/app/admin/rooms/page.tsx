"use client";

import { useCallback, useEffect, useState } from "react";
import { FaBed, FaPlus, FaEdit, FaTrash, FaSave, FaTimes, FaExclamationCircle } from "react-icons/fa"; // Icons
import { motion } from "framer-motion"; // Optional for animations

interface Room {
  roomNr: string;
  floor: number;
  maxOccupancy: number;
  available: boolean;
  basePrice: number;
  roomType: string;
  hasSeaView: boolean;
  hasBalcony: boolean;
  hasWifi: boolean;
  hasAirConditioning: boolean;
  petFriendly: boolean;
  amenities: string;
  rating: number;
  preferredFor: string;
}

export default function ManageRooms() {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [form, setForm] = useState<Partial<Room>>({ available: true }); // Default available to true
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) {
      setAccessToken(token);
    }
  }, []);

  const fetchRooms = useCallback(async () => {
    try {
      const response = await fetch("/api/rooms", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (!response.ok) throw new Error("Failed to fetch rooms");
      const data = await response.json();
      setRooms(data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  }, [accessToken]);

  useEffect(() => {
    if (accessToken) {
      fetchRooms();
    }
  }, [accessToken, fetchRooms]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Basic validation
    if (!form.roomNr || !form.floor || !form.maxOccupancy || !form.basePrice || !form.roomType) {
      setError("Room number, floor, max occupancy, base price, and room type are required.");
      return;
    }
    if ((form.maxOccupancy as number) <= 0 || (form.basePrice as number) <= 0) {
      setError("Max occupancy and base price must be positive numbers.");
      return;
    }

    const url = editingRoom ? `/api/rooms/${editingRoom.roomNr}` : "/api/rooms";
    const method = editingRoom ? "PUT" : "POST";

    try {
      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error(`Failed to ${editingRoom ? "update" : "add"} room`);
      setForm({ available: true }); // Reset with default
      setEditingRoom(null);
      fetchRooms();
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  };

  const handleEdit = (room: Room) => {
    setForm(room);
    setEditingRoom(room);
  };

  const handleDelete = async (roomNr: string) => {
    if (!confirm("Are you sure you want to delete this room?")) return;

    try {
      const response = await fetch(`/api/rooms/${roomNr}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error("Failed to delete room");
      fetchRooms();
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
        <FaBed /> Manage Rooms
      </motion.h1>

      {/* Form Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200 mb-8"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          {editingRoom ? "Edit Room" : "Add New Room"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
              disabled={!!editingRoom}
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Floor</label>
            <input
              name="floor"
              type="number"
              value={form.floor || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Max Occupancy</label>
            <input
              name="maxOccupancy"
              type="number"
              value={form.maxOccupancy || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Base Price ($)</label>
            <input
              name="basePrice"
              type="number"
              step="0.01"
              value={form.basePrice || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Room Type</label>
            <select
              name="roomType"
              value={form.roomType || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            >
              <option value="" disabled>
                Select Room Type
              </option>
              <option value="Single">Single</option>
              <option value="Double">Double</option>
              <option value="Suite">Suite</option>
            </select>
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Amenities</label>
            <input
              name="amenities"
              type="text"
              value={form.amenities || ""}
              onChange={handleInputChange}
              placeholder="e.g., Pool Access, Gym Access"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Preferred For</label>
            <input
              name="preferredFor"
              type="text"
              value={form.preferredFor || ""}
              onChange={handleInputChange}
              placeholder="e.g., Family, Honeymoon"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div className="md:col-span-2 grid grid-cols-2 md:grid-cols-3 gap-4">
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="available"
                type="checkbox"
                checked={form.available ?? true}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Available
            </label>
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="hasSeaView"
                type="checkbox"
                checked={form.hasSeaView || false}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Sea View
            </label>
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="hasBalcony"
                type="checkbox"
                checked={form.hasBalcony || false}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Balcony
            </label>
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="hasWifi"
                type="checkbox"
                checked={form.hasWifi || false}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Wifi
            </label>
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="hasAirConditioning"
                type="checkbox"
                checked={form.hasAirConditioning || false}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Air Conditioning
            </label>
            <label className="flex items-center gap-2 text-gray-700">
              <input
                name="petFriendly"
                type="checkbox"
                checked={form.petFriendly || false}
                onChange={handleInputChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500"
              />
              Pet-Friendly
            </label>
          </div>
          <div className="md:col-span-2 flex gap-4">
            <button
              type="submit"
              className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            >
              {editingRoom ? <FaSave /> : <FaPlus />}
              {editingRoom ? "Update Room" : "Add Room"}
            </button>
            {editingRoom && (
              <button
                type="button"
                onClick={() => {
                  setEditingRoom(null);
                  setForm({ available: true });
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

      {/* Rooms List */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">All Rooms</h2>
        {rooms.length === 0 ? (
          <p className="text-gray-600">No rooms found.</p>
        ) : (
          <ul className="space-y-4">
            {rooms.map((room) => (
              <li
                key={room.roomNr}
                className="border-b p-4 flex justify-between items-center hover:bg-gray-50 transition-colors duration-200"
              >
                <div>
                  <strong className="text-gray-800">Room #{room.roomNr}</strong> - {room.roomType}
                  <p className="text-gray-600">
                    Floor: {room.floor} | Max Occupancy: {room.maxOccupancy} | Price: ${room.basePrice.toFixed(2)}
                    <br />
                    Status: {room.available ? "Available" : "Occupied"} | Rating: {room.rating || "N/A"}
                    <br />
                    Features: {[
                      room.hasSeaView && "Sea View",
                      room.hasBalcony && "Balcony",
                      room.hasWifi && "Wifi",
                      room.hasAirConditioning && "A/C",
                      room.petFriendly && "Pet-Friendly",
                    ]
                      .filter(Boolean)
                      .join(", ") || "None"}
                    <br />
                    Amenities: {room.amenities || "None"} | Preferred For: {room.preferredFor || "General"}
                  </p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEdit(room)}
                    className="bg-yellow-500 text-white py-1 px-3 rounded flex items-center gap-1 hover:bg-yellow-600 transition-all duration-200"
                  >
                    <FaEdit /> Edit
                  </button>
                  <button
                    onClick={() => handleDelete(room.roomNr)}
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