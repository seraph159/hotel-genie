"use client";

import { Client } from "@/lib/types";
import { useState, useEffect } from "react";
import { FaUser, FaEnvelope, FaPhone, FaSave, FaExclamationCircle, FaSpinner } from "react-icons/fa";
import { motion } from "framer-motion";

export default function AccountPage() {
  const [client, setClient] = useState<Client | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch account details
  useEffect(() => {
    const fetchAccountDetails = async () => {
      try {
        const token = localStorage.getItem("authToken");
        if (!token) {
          throw new Error("Please log in to view account details.");
        }

        const response = await fetch("http://localhost:8080/api/account", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch account details.");
        }

        const data: Client = await response.json();
        setClient(data);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError("An unexpected error occurred.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchAccountDetails();
  }, []);

  const handleUpdateAccount = async () => {
    if (!client) return;

    // Basic validation
    if (!client.name.trim()) {
      setError("Name cannot be empty.");
      return;
    }
    if (client.phone && !/^\+?\d{7,15}$/.test(client.phone)) {
      setError("Please enter a valid phone number (7-15 digits).");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/account", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
        body: JSON.stringify(client),
      });

      if (response.ok) {
        alert("Account updated successfully!");
        setError(null);
      } else {
        throw new Error("Failed to update account.");
      }
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("An unexpected error occurred.");
      }
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-gray-100">
        <FaSpinner className="text-4xl text-blue-600 animate-spin" />
      </div>
    );
  }

  if (error && !client) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-red-600 flex items-center gap-2 text-lg"
        >
          <FaExclamationCircle /> Error: {error}
        </motion.div>
      </div>
    );
  }

  return (
    <div className="container flex flex-col items-center mx-auto px-4 py-8 min-h-screen bg-gray-100">
      {/* Animated Title */}
      <motion.h2
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-4xl font-bold text-center text-blue-700 mb-6 flex items-center gap-2"
      >
        <FaUser /> Account Details
      </motion.h2>

      {/* Account Form Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-white rounded-lg p-6 shadow-md w-full max-w-lg border border-gray-200"
      >
        {client && (
          <>
            <div className="mb-4">
              <label className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
                <FaUser /> Name
              </label>
              <input
                type="text"
                value={client.name}
                onChange={(e) => setClient({ ...client, name: e.target.value })}
                className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              />
            </div>
            <div className="mb-4">
              <label className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
                <FaEnvelope /> Email
              </label>
              <input
                type="email"
                value={client.email}
                disabled
                className="border rounded p-2 w-full bg-gray-100 text-gray-500 cursor-not-allowed"
              />
            </div>
            <div className="mb-4">
              <label className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
                <FaPhone /> Phone
              </label>
              <input
                type="text"
                value={client.phone || ""}
                onChange={(e) => setClient({ ...client, phone: e.target.value })}
                className="border rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
                placeholder="+1234567890"
              />
            </div>

            {error && (
              <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="text-red-600 mb-4 flex items-center gap-2"
              >
                <FaExclamationCircle /> {error}
              </motion.p>
            )}

            <div className="flex justify-end gap-4">
              <button
                onClick={handleUpdateAccount}
                className="bg-blue-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
              >
                <FaSave /> Update Account
              </button>
            </div>
          </>
        )}
      </motion.div>
    </div>
  );
}