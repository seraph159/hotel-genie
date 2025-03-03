"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import { FaCheckCircle, FaBed, FaCalendarAlt, FaDollarSign } from "react-icons/fa";
import { motion } from "framer-motion";

interface SessionDetails {
  metadata: {
    roomNr: string;
    startDate: string;
    endDate: string;
  };
  amount_total: number;
}

const SuccessContent = () => {
  const searchParams = useSearchParams();
  const session_id = searchParams.get("session_id");

  const [loading, setLoading] = useState(true);
  const [sessionDetails, setSessionDetails] = useState<SessionDetails | null>(null);

  useEffect(() => {
    if (!session_id) return;

    const fetchSessionDetails = async () => {
      try {
        const response = await fetch(`/api/stripe/get-session?session_id=${session_id}`);
        if (!response.ok) throw new Error("Failed to retrieve session details.");
        const data = await response.json();
        setSessionDetails(data);
      } catch (error) {
        console.error(error);
        setSessionDetails(null);
      } finally {
        setLoading(false);
      }
    };

    fetchSessionDetails();
  }, [session_id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <p className="text-gray-600 text-lg">Loading...</p>
      </div>
    );
  }

  if (!sessionDetails) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <p className="text-red-600 text-lg">Unable to retrieve booking details.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center px-4 py-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="bg-white p-8 rounded-lg shadow-md text-center max-w-md w-full border border-gray-200"
      >
        <h1 className="text-3xl font-bold text-gray-800 mb-4 flex items-center justify-center gap-2">
          <FaCheckCircle className="text-green-500" /> Payment Successful!
        </h1>
        <p className="text-gray-600 mb-6">Thank you for your booking!</p>
        <div className="text-sm text-gray-700 space-y-3">
          <p className="flex items-center justify-center gap-2">
            <FaBed className="text-blue-600" />
            <strong>Room:</strong> {sessionDetails.metadata.roomNr}
          </p>
          <p className="flex items-center justify-center gap-2">
            <FaCalendarAlt className="text-blue-500" />
            <strong>Start Date:</strong> {sessionDetails.metadata.startDate}
          </p>
          <p className="flex items-center justify-center gap-2">
            <FaCalendarAlt className="text-blue-500" />
            <strong>End Date:</strong> {sessionDetails.metadata.endDate}
          </p>
          <p className="flex items-center justify-center gap-2">
            <FaDollarSign className="text-green-600" />
            <strong>Total Paid:</strong> ${(sessionDetails.amount_total / 100).toFixed(2)}
          </p>
        </div>
        <Link
  href="/"
  className="mt-6 inline-flex items-center gap-2 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
>
  <FaBed /> Back to Home
</Link>
      </motion.div>
    </div>
  );
};

const SuccessPage = () => (
  <Suspense fallback={<p className="text-gray-600">Loading page...</p>}>
    <SuccessContent />
  </Suspense>
);

export default SuccessPage;