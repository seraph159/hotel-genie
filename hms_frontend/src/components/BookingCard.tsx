import { Booking } from "@/lib/types";

interface BookingCardProps {
  booking: Booking;
  onCancel: () => void;
}

const BookingCard = ({ booking, onCancel }: BookingCardProps) => {
  return (
    <div className="bg-white shadow-md p-4 rounded">
      <h3 className="text-lg font-semibold mb-2">{booking.roomNr}</h3>
      <p className="mb-2">
        {booking.startDate} - {booking.endDate}
      </p>
      <button
        className="bg-red-600 text-white py-2 px-4 rounded hover:bg-red-700"
        onClick={onCancel}
      >
        Cancel Booking
      </button>
    </div>
  );
};

export default BookingCard;