// @/lib/api.tsx

export const fetchRooms = async (startDate: string, endDate: string, minOccupancy: number) => {
    const response = await fetch(
      `/api/main/available-rooms?startDate=${startDate}&endDate=${endDate}&minOccupancy=${minOccupancy}`
    );
    if (!response.ok) throw new Error("Failed to fetch available rooms.");
    return response.json();
  };
  
  export const createBooking = async (roomNr: string, startDate: string, endDate: string, accessToken: string) => {
    const booking = { id: { startDate, roomNr }, price: 100, room: { roomNr }, endDate};
    const response = await fetch("/api/bookings", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${accessToken}` },
      body: JSON.stringify(booking),
    });
    
    if (!response.ok) throw new Error("Failed to create booking.");
    const { checkoutLink } = await response.json();

    // Redirect to Stripe Checkout
    window.location.href = checkoutLink;
  };
  
  export const deleteBooking = async (roomNr: string, startDate: string, accessToken: string) => {
    const response = await fetch(`/api/bookings/${startDate}/${roomNr}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    if (!response.ok) throw new Error("Failed to delete booking.");
  };
  
  export const fetchBookings = async (accessToken: string) => {
    try {
      const response = await fetch('/api/bookings', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json', Authorization: `Bearer ${accessToken}`,
        },
      });
  
      if (!response.ok) {
        throw new Error('Failed to fetch bookings');
      }
  
      const bookings = await response.json();
      return bookings;
    } catch (error) {
      console.error(error);
      return [];
    }
  };