"use client";

import { Room } from "@/lib/types";
import { FaBed, FaStar, FaDollarSign, FaPaw, FaWifi, FaWater, FaSnowflake, FaDoorOpen } from "react-icons/fa"; // Icons
import { motion } from "framer-motion"; // Optional for animations

type RoomsListProps = {
  rooms: Room[];
  onBook: (roomNr: string) => void;
  onCancel: (roomNr: string) => void;
};

const RoomsList: React.FC<RoomsListProps> = ({ rooms, onBook }) => (
  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl">
    {rooms.map((room, index) => (
      <motion.div
        key={index}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: index * 0.1 }} // Staggered animation
        className="border p-6 rounded-lg shadow-md bg-white hover:shadow-lg transition-shadow duration-300 flex flex-col justify-between"
      >
        {/* Room Header */}
        <h3 className="text-xl font-bold text-gray-800 mb-3 flex items-center gap-2">
          <FaBed className="text-blue-600" /> {room.room.roomType}
        </h3>

        {/* Room Details */}
        <div className="text-sm text-gray-700 mb-4 space-y-1">
          <p>
            <strong>Room Number:</strong> {room.room.roomNr}
          </p>
          <p>
            <strong>Floor:</strong> {room.room.floor}
          </p>
          <p>
            <strong>Max Occupancy:</strong> {room.room.maxOccupancy}
          </p>
          <p className="flex items-center gap-1">
            <FaStar className="text-yellow-400" />
            <strong>Rating:</strong> {room.room.rating.toFixed(1)}
          </p>
          <p>
            <strong>Amenities:</strong> {room.room.amenities || "Standard"}
          </p>
          <p className="flex items-center gap-1">
            <FaDollarSign className="text-green-600" />
            <strong>Price:</strong> ${room.price.toFixed(2)} / night
          </p>
        </div>

        {/* Room Features */}
        <ul className="text-sm text-gray-600 mb-4 space-y-2">
          {room.room.hasSeaView && (
            <li className="flex items-center gap-2">
              <FaWater className="text-blue-500" /> Sea View
            </li>
          )}
          {room.room.hasBalcony && (
            <li className="flex items-center gap-2">
              <FaDoorOpen className="text-gray-500" /> Balcony
            </li>
          )}
          {room.room.hasWifi && (
            <li className="flex items-center gap-2">
              <FaWifi className="text-blue-400" /> Wi-Fi
            </li>
          )}
          {room.room.hasAirConditioning && (
            <li className="flex items-center gap-2">
              <FaSnowflake className="text-blue-300" /> Air Conditioning
            </li>
          )}
          {room.room.petFriendly && (
            <li className="flex items-center gap-2">
              <FaPaw className="text-brown-500" /> Pet Friendly
            </li>
          )}
        </ul>

        {/* Action Buttons */}
        <div className="flex gap-3 mt-auto">
          <button
            onClick={() => onBook(room.room.roomNr)}
            className="bg-green-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 transition-all duration-200"
          >
            <FaBed /> Book Now
          </button>
          {/* Uncomment if Cancel functionality is needed */}
          {/* <button
            onClick={() => onCancel(room.room.roomNr)}
            className="bg-red-600 text-white py-2 px-4 rounded flex items-center gap-2 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 transition-all duration-200"
          >
            <FaBan /> Cancel Booking
          </button> */}
        </div>
      </motion.div>
    ))}
  </div>
);

export default RoomsList;