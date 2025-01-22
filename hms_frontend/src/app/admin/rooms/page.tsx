'use client';

import { useCallback, useEffect, useState } from 'react';

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
  const [form, setForm] = useState<Partial<Room>>({});
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
      const response = await fetch('/api/rooms', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data = await response.json();
      setRooms(data);
    } catch (err) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert('An unexpected error occurred.');
      }
      setError('Failed to fetch rooms');
    }
  }, [accessToken]);
  
  useEffect(() => {
    if (accessToken) {
      fetchRooms();
    }
  }, [accessToken, fetchRooms]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type, checked } = e.target as HTMLInputElement;
    setForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const url = editingRoom ? `/api/rooms/${editingRoom.roomNr}` : '/api/rooms';
    const method = editingRoom ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${accessToken}` },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error('Failed to save room');
      setForm({});
      setEditingRoom(null);
      fetchRooms();
    } catch (err) {
      if (err instanceof Error) {
      alert(`Error: ${err.message}`);
    } else {
      alert("An unexpected error occurred.");
    }
      setError('Failed to save room');
    }
  };

  const handleEdit = (room: Room) => {
    setForm(room);
    setEditingRoom(room);
  };

  const handleDelete = async (roomNr: string) => {
    try {
      const response = await fetch(`/api/rooms/${roomNr}`, { 
        method: 'DELETE',
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error('Failed to delete room');
      fetchRooms();
    } catch (err) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert("An unexpected error occurred.");
      }
      setError('Failed to delete room');
    }
  };

  return (
    <main className="p-6">
      <h1 className="text-3xl font-bold mb-6">Manage Rooms</h1>

      {/* Error message */}
      {error && <p className="text-red-500">{error}</p>}

      {/* Room form */}
      <form onSubmit={handleSubmit} className="mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            name="roomNr"
            type="text"
            value={form.roomNr || ''}
            onChange={handleInputChange}
            placeholder="Room Number"
            className="border p-2 rounded"
            required
            disabled={!!editingRoom}
          />
          <input
            name="floor"
            type="number"
            value={form.floor || ''}
            onChange={handleInputChange}
            placeholder="Floor"
            className="border p-2 rounded"
            required
          />
          <input
            name="maxOccupancy"
            type="number"
            value={form.maxOccupancy || ''}
            onChange={handleInputChange}
            placeholder="Max Occupancy"
            className="border p-2 rounded"
            required
          />
          <input
            name="basePrice"
            type="number"
            value={form.basePrice || ''}
            onChange={handleInputChange}
            placeholder="Base Price"
            className="border p-2 rounded"
            required
          />
          <select
            name="roomType"
            value={form.roomType || ''}
            onChange={handleInputChange}
            className="border p-2 rounded"
            required
          >
            <option value="" disabled>
              Select Room Type
            </option>
            <option value="Single">Single</option>
            <option value="Double">Double</option>
            <option value="Suite">Suite</option>
          </select>
          <input
            name="amenities"
            type="text"
            value={form.amenities || ''}
            onChange={handleInputChange}
            placeholder="Amenities (comma-separated) e.g., Pool Access, Gym Access, Free Breakfast"
            className="border p-2 rounded"
          />
          <input
            name="preferredFor"
            type="text"
            value={form.preferredFor || ''}
            onChange={handleInputChange}
            placeholder="Preferred For (comma-separated) e.g., Family, Honeymoon, Business"
            className="border p-2 rounded"
          />
          <label className="flex items-center">
            <input
              name="available"
              type="checkbox"
              checked={form.available || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Available
          </label>
          <label className="flex items-center">
            <input
              name="hasSeaView"
              type="checkbox"
              checked={form.hasSeaView || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Sea View
          </label>
          <label className="flex items-center">
            <input
              name="hasWifi"
              type="checkbox"
              checked={form.hasWifi || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Wifi
          </label>
          <label className="flex items-center">
            <input
              name="hasAirConditioning"
              type="checkbox"
              checked={form.hasAirConditioning || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Air Conditioning
          </label>
          <label className="flex items-center">
            <input
              name="hasBalcony"
              type="checkbox"
              checked={form.hasBalcony || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Balcony
          </label>
          <label className="flex items-center">
            <input
              name="petFriendly"
              type="checkbox"
              checked={form.petFriendly || false}
              onChange={handleInputChange}
              className="mr-2"
            />
            Pet-Friendly
          </label>
        </div>
        <button
            type="submit"
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded"
        >
            {editingRoom ? 'Update Client' : 'Add Client'}
        </button>
        {editingRoom && (
            <button
            type="button"
            onClick={() => {
                setEditingRoom(null);
                setForm({});
            }}
            className="ml-2 mt-4 bg-gray-500 text-white px-4 py-2 rounded"
            >
            Cancel
            </button>
        )}
        </form>

      {/* Room list */}
      <ul>
        {rooms.map((room) => (
          <li
            key={room.roomNr}
            className="border-b p-4 flex justify-between items-center"
          >
            <div>
              <strong>Room #{room.roomNr}</strong> - {room.roomType} - ${room.basePrice}
              <p>{room.available ? 'Available' : 'Occupied'}</p>
            </div>
            <div className="flex space-x-2">
              <button
                onClick={() => handleEdit(room)}
                className="bg-yellow-400 text-white px-2 py-1 rounded"
              >
                Edit
              </button>
              <button
                onClick={() => handleDelete(room.roomNr)}
                className="bg-red-500 text-white px-2 py-1 rounded"
              >
                Delete
              </button>
            </div>
          </li>
        ))}
      </ul>
    </main>
  );
}
