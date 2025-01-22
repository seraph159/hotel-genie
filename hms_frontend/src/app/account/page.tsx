"use client";

import { Client } from "@/lib/types";
import { useState, useEffect } from "react";

export default function AccountPage() {
  const [client, setClient] = useState<Client | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch account details
  useEffect(() => {
    const fetchAccountDetails = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/account", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("authToken")}`,
          },
        });
  
        if (!response.ok) {
          throw new Error("Failed to fetch account details.");
        }
  
        const data = await response.json(); // the API response includes payment details
  
        setClient(data); // Set the client data (name, email, phone, paymentType, etc.)
  
      } catch (err: unknown) {
        if (err instanceof Error) {
          alert(`Error: ${err.message}`);
          setError(err.message);
        } else {
          alert("An unexpected error occurred.");
        }
      } finally {
        setLoading(false);
      }
    };
  
    fetchAccountDetails();
  }, []);


  const handleUpdateAccount = async () => {
    if (!client) return;

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
      } else {
        alert("Failed to update account.");
      }
    } catch (err) {
      console.error("Failed to update account:", err);
    }
  };


  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="max-w-2xl mx-auto mt-10 p-6 bg-white shadow rounded">
      <h2 className="text-2xl font-semibold mb-4">Account Details</h2>

      {client && (
        <>
          <div className="mb-4">
            <label className="block mb-1 font-medium">Name</label>
            <input
              type="text"
              value={client.name}
              onChange={(e) => setClient({ ...client, name: e.target.value })}
              className="border rounded p-2 w-full"
            />
          </div>
          <div className="mb-4">
            <label className="block mb-1 font-medium">Email</label>
            <input
              type="email"
              value={client.email}
              disabled
              className="border rounded p-2 w-full bg-gray-100"
            />
          </div>
          <div className="mb-4">
            <label className="block mb-1 font-medium">Phone</label>
            <input
              type="text"
              value={client.phone || ""}
              onChange={(e) => setClient({ ...client, phone: e.target.value })}
              className="border rounded p-2 w-full"
            />
          </div>

          <div className="flex justify-end gap-4">
            <button
              onClick={handleUpdateAccount}
              className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700"
            >
              Update Account
            </button>
          </div>
        </>
      )}
    </div>
  );
}
