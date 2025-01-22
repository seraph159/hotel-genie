'use client';

import { useCallback, useEffect, useState } from 'react';

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
      const response = await fetch('/api/bookings/all', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data = await response.json();
      setBookings(data);
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert('An unexpected error occurred.');
      }
      setError('Failed to fetch bookings');
    }
  }, [accessToken]);

  // Load token on component mount
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      setAccessToken(token);
    }
  }, []);

  // Fetch bookings when accessToken changes
  useEffect(() => {
    if (accessToken) {
      fetchBookings();
    }
  }, [accessToken, fetchBookings])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const url = editingBooking
      ? `/api/bookings/admin`
      : '/api/bookings/admin';
    const method = editingBooking ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${accessToken}` },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error('Failed to save booking');
      setForm({});
      setEditingBooking(null);
      fetchBookings();
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
      } else {
        alert("An unexpected error occurred.");
      }
      setError('Failed to save booking');
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
    try {
      const response = await fetch(`/api/bookings/admin/${startDate}/${roomNr}`, { 
        method: 'DELETE',
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error('Failed to delete booking');
      fetchBookings();
    } catch (err) {
      if (err instanceof Error) {
        setError('Failed to delete booking');
      } else {
        alert("An unexpected error occurred.");
      }
    }
  };

  return (
    <main className="p-6">
      <h1 className="text-3xl font-bold mb-6">Manage Bookings</h1>

      {/* Error message */}
      {error && <p className="text-red-500">{error}</p>}

      {/* Booking form */}
      <form onSubmit={handleSubmit} className="mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            name="startDate"
            type="date"
            value={form.startDate || ''}
            onChange={handleInputChange}
            placeholder="Start Date"
            className="border p-2 rounded"
            required
            disabled={!!editingBooking}
          />
          <input
            name="endDate"
            type="date"
            value={form.endDate || ''}
            onChange={handleInputChange}
            placeholder="End Date"
            className="border p-2 rounded"
            required
          />
          <input
            name="roomNr"
            type="text"
            value={form.roomNr || ''}
            onChange={handleInputChange}
            placeholder="Room Number"
            className="border p-2 rounded"
            required
          />
          <input
            name="clientEmail"
            type="email"
            value={form.clientEmail || ''}
            onChange={handleInputChange}
            placeholder="Client Email"
            className="border p-2 rounded"
            required
          />
        </div>
        <button
            type="submit"
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded"
        >
            {editingBooking ? 'Update Client' : 'Add Client'}
        </button>
        {editingBooking && (
            <button
            type="button"
            onClick={() => {
                setEditingBooking(null);
                setForm({});
            }}
            className="ml-2 mt-4 bg-gray-500 text-white px-4 py-2 rounded"
            >
            Cancel
            </button>
        )}
        </form>

      {/* Booking list */}
      <ul>
        {bookings.map((booking) => (
          <li
            key={`${booking.startDate}-${booking.roomNr}`}
            className="border-b p-4 flex justify-between items-center"
          >
            <div>
              <strong>Room #{booking.room.roomNr}</strong> - {booking.room.roomType}
              <p>
                Client:({booking.clientEmail})<br />
                Dates: {booking.startDate} to {booking.endDate}<br />
                Price: ${booking.price}
              </p>
            </div>
            <div className="flex space-x-2">
              <button
                onClick={() => handleEdit(booking)}
                className="bg-yellow-400 text-white px-2 py-1 rounded"
              >
                Edit
              </button>
              <button
                onClick={() => handleDelete(booking.startDate, booking.roomNr)}
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
