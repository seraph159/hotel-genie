"use client";
import { useEffect, useState } from 'react';

type RecommendationResponse = object;

const RecommendationsPage = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [minOccupancy, setMinOccupancy] = useState(1);
  const [preferences, setPreferences] = useState('');
  const [recommendedRooms, setRecommendedRooms] = useState<RecommendationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
      const token = localStorage.getItem("authToken");
      if (token) setAccessToken(token);
    }, []);
  
  const handleRecommend = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (!accessToken) {
        setError('Access token is missing');
        return;
      }

      const url = `/api/recommendations?startDate=${startDate}&endDate=${endDate}&minOccupancy=${minOccupancy}&preferences=${preferences}`;
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data: RecommendationResponse = await response.json();
      setRecommendedRooms(data);
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(`Error: ${err.message}`);
        setError(err.message);
      } else {
        alert("An unexpected error occurred.");
      }
    }
  };

  return (
    <div className="flex flex-col items-center max-w-md mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Ask AI</h1>
      <form onSubmit={handleRecommend} className="flex flex-col gap-4">
        <label className="flex flex-col">
          Start Date:
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="p-2 border border-gray-300 rounded"
          />
        </label>
        <label className="flex flex-col">
          End Date:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="p-2 border border-gray-300 rounded"
          />
        </label>
        <label className="flex flex-col">
          Min Occupancy:
          <input
            type="number"
            value={minOccupancy}
            onChange={(e) => setMinOccupancy(parseInt(e.target.value))}
            className="p-2 border border-gray-300 rounded"
          />
        </label>
        <label className="flex flex-col">
          Preferences:
          <input
            type="text"
            value={preferences}
            onChange={(e) => setPreferences(e.target.value)}
            className="p-2 border border-gray-300 rounded"
          />
        </label>
        <button
          type="submit"
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Recommend Rooms
        </button>
      </form>
      {error && <p className="text-red-500">{error}</p>}
      {recommendedRooms && <p>{JSON.stringify(recommendedRooms)}</p>}
    </div>
  );
};

export default RecommendationsPage;