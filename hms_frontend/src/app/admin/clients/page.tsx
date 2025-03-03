"use client";

import { useCallback, useEffect, useState } from "react";
import { FaUsers, FaPlus, FaEdit, FaTrash, FaSave, FaTimes, FaExclamationCircle } from "react-icons/fa";
import { motion } from "framer-motion";

interface Client {
  email: string;
  name: string;
  phone: string;
  paymentType?: string;
}

export default function ManageClients() {
  const [clients, setClients] = useState<Client[]>([]);
  const [form, setForm] = useState<Partial<Client>>({});
  const [editingClient, setEditingClient] = useState<Client | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  // Fetch clients function
  const fetchClients = useCallback(async () => {
    try {
      const response = await fetch("/api/clients", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      if (!response.ok) throw new Error("Failed to fetch clients");
      const data = await response.json();
      setClients(data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  }, [accessToken]);

  // Load token and fetch clients
  useEffect(() => {
    const token = localStorage.getItem("authToken");
    if (token) {
      setAccessToken(token);
    }
  }, []);

  useEffect(() => {
    if (accessToken) {
      fetchClients();
    }
  }, [accessToken, fetchClients]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Basic validation
    if (!form.email || !form.name) {
      setError("Email and name are required.");
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      setError("Please enter a valid email address.");
      return;
    }
    if (form.phone && !/^\+?\d{7,15}$/.test(form.phone)) {
      setError("Please enter a valid phone number (7-15 digits).");
      return;
    }

    const url = editingClient ? `/api/clients/${editingClient.email}` : "/api/clients"; // Adjusted URL for PUT
    const method = editingClient ? "PUT" : "POST";

    try {
      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error(`Failed to ${editingClient ? "update" : "add"} client`);
      setForm({});
      setEditingClient(null);
      fetchClients();
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    }
  };

  const handleEdit = (client: Client) => {
    setForm(client);
    setEditingClient(client);
  };

  const handleDelete = async (email: string) => {
    if (!confirm("Are you sure you want to delete this client?")) return;

    try {
      const response = await fetch(`/api/clients/${email}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error("Failed to delete client");
      fetchClients();
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
        <FaUsers /> Manage Clients
      </motion.h1>

      {/* Form Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200 mb-8"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          {editingClient ? "Edit Client" : "Add New Client"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block mb-1 font-medium text-gray-700">Email</label>
            <input
              name="email"
              type="email"
              value={form.email || ""}
              onChange={handleInputChange}
              placeholder="client@example.com"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
              disabled={!!editingClient} // Email as unique identifier
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Name</label>
            <input
              name="name"
              type="text"
              value={form.name || ""}
              onChange={handleInputChange}
              placeholder="John Doe"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-medium text-gray-700">Phone</label>
            <input
              name="phone"
              type="text"
              value={form.phone || ""}
              onChange={handleInputChange}
              placeholder="+1234567890"
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          {/* Uncommented paymentType field */}
          <div>
            <label className="block mb-1 font-medium text-gray-700">Payment Type</label>
            <select
              name="paymentType"
              value={form.paymentType || ""}
              onChange={handleInputChange}
              className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            >
              <option value="">Select Payment Type (Optional)</option>
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="PAYPAL">PayPal</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
            </select>
          </div>
          <div className="md:col-span-2 flex gap-4">
            <button
              type="submit"
              className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            >
              {editingClient ? <FaSave /> : <FaPlus />}
              {editingClient ? "Update Client" : "Add Client"}
            </button>
            {editingClient && (
              <button
                type="button"
                onClick={() => {
                  setEditingClient(null);
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

      {/* Clients List */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="bg-white p-6 rounded-lg shadow-md border border-gray-200"
      >
        <h2 className="text-xl font-semibold text-gray-800 mb-4">All Clients</h2>
        {clients.length === 0 ? (
          <p className="text-gray-600">No clients found.</p>
        ) : (
          <ul className="space-y-4">
            {clients.map((client) => (
              <li
                key={client.email}
                className="border-b p-4 flex justify-between items-center hover:bg-gray-50 transition-colors duration-200"
              >
                <div>
                  <strong className="text-gray-800">{client.name}</strong> ({client.email})
                  <p className="text-gray-600">
                    Phone: {client.phone || "N/A"}
                    <br />
                    Payment Type: {client.paymentType || "Not Set"}
                  </p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEdit(client)}
                    className="bg-yellow-500 text-white py-1 px-3 rounded flex items-center gap-1 hover:bg-yellow-600 transition-all duration-200"
                  >
                    <FaEdit /> Edit
                  </button>
                  <button
                    onClick={() => handleDelete(client.email)}
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