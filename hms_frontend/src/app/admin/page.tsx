"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import {
  FaBed,
  FaCalendarCheck,
  FaUsers,
  FaTachometerAlt,
} from "react-icons/fa";
import { motion } from "framer-motion";

export default function AdminHome() {
  const [adminName, setAdminName] = useState<string>("Admin");

  // Fetch admin name
  useEffect(() => {
    const fetchAdminName = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/account", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("authToken")}`,
          },
        });
        if (response.ok) {
          const data = await response.json();
          setAdminName(data.name || "Admin");
        }
      } catch (err) {
        console.error("Failed to fetch admin name:", err);
      }
    };
    fetchAdminName();
  }, []);

  const sections = [
    {
      name: "Manage Rooms",
      link: "/admin/rooms",
      description: "Add, update, or delete room details.",
      icon: <FaBed className="text-3xl text-blue-600" />,
    },
    {
      name: "Manage Bookings",
      link: "/admin/bookings",
      description: "View and handle all bookings.",
      icon: <FaCalendarCheck className="text-3xl text-blue-600" />,
    },
    {
      name: "Manage Clients",
      link: "/admin/clients",
      description: "Manage client accounts and details.",
      icon: <FaUsers className="text-3xl text-blue-600" />,
    },
  ];

  return (
    <main className="min-h-screen bg-gray-100 p-6">
      {/* Header Section */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="flex justify-between items-center mb-8"
      >
        <h1 className="text-4xl font-bold text-blue-700 flex items-center gap-2">
          <FaTachometerAlt /> Welcome, {adminName}
        </h1>
      </motion.div>

      {/* Dashboard Cards */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        {sections.map((section) => (
          <Link key={section.name} href={section.link}>
            <motion.div
              whileHover={{ scale: 1.05, boxShadow: "0 10px 20px rgba(0,0,0,0.1)" }}
              whileTap={{ scale: 0.95 }}
              className="block bg-white p-6 rounded-lg shadow-md border border-gray-200 hover:bg-blue-50 transition-colors duration-200 cursor-pointer"
            >
              <div className="flex items-center gap-4">
                {section.icon}
                <div>
                  <h2 className="text-xl font-semibold text-gray-800">{section.name}</h2>
                  <p className="text-gray-600">{section.description}</p>
                </div>
              </div>
            </motion.div>
          </Link>
        ))}
      </motion.div>
    </main>
  );
}