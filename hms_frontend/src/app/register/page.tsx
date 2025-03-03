"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { FaUserPlus, FaUser, FaEnvelope, FaLock, FaExclamationCircle, FaCheckCircle } from "react-icons/fa";
import { motion } from "framer-motion";
import Link from "next/link";

export default function RegisterPage() {
  const router = useRouter();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false); // loading state

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    // Basic client-side validation
    if (!name.trim()) {
      setError("Name is required.");
      return;
    }
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError("Please enter a valid email address.");
      return;
    }
    if (!password || password.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const response = await fetch("http://localhost:8080/api/auth/client/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, password }),
      });

      if (response.ok) {
        setSuccessMessage("Registration successful! Redirecting to login...");
        setTimeout(() => router.push("/login"), 3000); // Redirect after 3 seconds
      } else {
        const errorData = await response.json();
        setError(errorData.message || "Registration failed.");
      }
    } catch {
      setError("An error occurred during registration. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
        className="max-w-md w-full mx-auto p-6 bg-white shadow-md rounded-lg border border-gray-200"
      >
        <h2 className="text-3xl font-bold text-blue-700 mb-6 flex items-center gap-2">
          <FaUserPlus /> Register
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="name" className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaUser /> Name
            </label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              placeholder="John Doe"
              className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div>
            <label htmlFor="email" className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaEnvelope /> Email
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="you@example.com"
              className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <div>
            <label htmlFor="password" className="block mb-1 font-medium text-gray-700 flex items-center gap-1">
              <FaLock /> Password
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="••••••"
              className="w-full border rounded p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className={`w-full bg-blue-600 text-white py-2 px-4 rounded flex items-center justify-center gap-2 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200 ${
              loading ? "opacity-75 cursor-not-allowed" : ""
            }`}
          >
            {loading ? (
              <FaExclamationCircle className="animate-spin" />
            ) : (
              <FaUserPlus />
            )}
            {loading ? "Registering..." : "Register"}
          </button>
        </form>
        {error && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="mt-4 text-red-600 flex items-center gap-2"
          >
            <FaExclamationCircle /> {error}
          </motion.p>
        )}
        {successMessage && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="mt-4 text-green-600 flex items-center gap-2"
          >
            <FaCheckCircle /> {successMessage}
          </motion.p>
        )}
        <p className="mt-4 text-center text-gray-600">
          Already have an account?{" "}
          <Link href="/login" className="text-blue-600 hover:underline">
            Login here
          </Link>
        </p>
      </motion.div>
    </div>
  );
}