"use client";

import { Room } from "@/lib/types";

type RoomsListProps = {
  rooms: Room[];
  onBook: (roomNr: string) => void;
  onCancel: (roomNr: string) => void;
};

const RoomsList: React.FC<RoomsListProps> = ({ rooms, onBook }) => (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      {rooms.map((room, index) => (
        <div key={index} className="border p-4 rounded-lg shadow-md bg-white">
          {/* Room Header */}
          <h3 className="text-lg font-bold mb-2">{room.room.roomType}</h3>
  
          {/* Room Details */}
          <div className="text-sm mb-3">
            <p>
              <strong>Room Number:</strong> {room.room.roomNr}
            </p>
            <p>
              <strong>Floor:</strong> {room.room.floor}
            </p>
            <p>
              <strong>Max Occupancy:</strong> {room.room.maxOccupancy}
            </p>
            <p>
              <strong>Rating:</strong> ⭐ {room.room.rating.toFixed(1)}
            </p>
            <p>
              <strong>Amenities:</strong> {room.room.amenities || "Standard"}
            </p>
            <p>
              <strong>Price:</strong> ${room.price.toFixed(2)}
            </p>
          </div>
  
          {/* Room Features */}
          <ul className="text-sm space-y-1 mb-4">
            {room.room.hasSeaView && <li>🌊 Sea View</li>}
            {room.room.hasBalcony && <li>🏖 Balcony</li>}
            {room.room.hasWifi && <li>📶 Wi-Fi</li>}
            {room.room.hasAirConditioning && <li>❄️ Air Conditioning</li>}
            {room.room.petFriendly && <li>🐾 Pet Friendly</li>}
          </ul>
  
          {/* Action Buttons */}
          <div className="flex gap-2">
            <button
              onClick={() => onBook(room.room.roomNr)}
              className="bg-green-600 text-white py-1 px-3 rounded hover:bg-green-700"
            >
              Book
            </button>
            {/* <button
              onClick={() => onCancel(room.room.roomNr)}
              className="bg-red-600 text-white py-1 px-3 rounded hover:bg-red-700"
            >
              Cancel Booking
            </button> */}
          </div>
        </div>
      ))}
    </div>
  );

export default RoomsList;
