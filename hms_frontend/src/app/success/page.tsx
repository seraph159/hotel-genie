"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

const SuccessPage = () => {
  const searchParams = useSearchParams();
  const session_id = searchParams.get("session_id");

  const [loading, setLoading] = useState(true);
  const [sessionDetails, setSessionDetails] = useState<any>(null);

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

  if (loading) return <p>Loading...</p>;

  if (!sessionDetails) return <p>Unable to retrieve booking details.</p>;

  return (
    <div style={{ textAlign: "center", margin: "20px" }}>
      <h1>Payment Successful! 🎉</h1>
      <p>Thank you for your booking!</p>
      <p><strong>Room:</strong> {sessionDetails.metadata.roomNr}</p>
      <p><strong>Start Date:</strong> {sessionDetails.metadata.startDate}</p>
      <p><strong>End Date:</strong> {sessionDetails.metadata.endDate}</p>
      <p><strong>Total Paid:</strong> ${(sessionDetails.amount_total / 100).toFixed(2)}</p>
    </div>
  );
};

export default SuccessPage;