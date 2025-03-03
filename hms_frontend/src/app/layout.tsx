"use client";

import Link from "next/link";
import { AuthProvider, useAuth } from "./context/AuthContext";
import "./globals.css";
import { FaHome, FaRobot, FaSignInAlt, FaUserPlus, FaSignOutAlt, FaUser, FaTachometerAlt, FaHotel, FaBars } from "react-icons/fa";
import { useState } from "react";

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthProvider>
      <html lang="en">
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        </head>
        <body>
          <Header />
          <main className="p-4 sm:p-6 min-h-screen bg-gray-100">{children}</main>
        </body>
      </html>
    </AuthProvider>
  );
}

function Header() {
  const { isAuthenticated, role, logout } = useAuth();
  const isAdmin = role === "ROLE_ADMIN";
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="bg-blue-600 text-white p-4 flex flex-col sm:flex-row justify-between items-start sm:items-center shadow-lg sticky top-0 z-10">
      <div className="flex justify-between w-full sm:w-auto items-center">
        <h1 className="text-lg sm:text-xl font-bold flex items-center gap-2 transition-transform hover:scale-105">
          <FaHotel className="text-xl sm:text-2xl" />
          HotelGenie
        </h1>
        <button 
          className="sm:hidden text-2xl"
          onClick={() => setIsMenuOpen(!isMenuOpen)}
        >
          <FaBars />
        </button>
      </div>
      
      <nav className={`
        ${isMenuOpen ? 'flex' : 'hidden'} 
        sm:flex 
        flex-col sm:flex-row 
        w-full sm:w-auto 
        mt-4 sm:mt-0 
        space-y-4 sm:space-y-0 
        sm:space-x-6 
        text-sm font-medium
      `}>
        <Link 
          href="/" 
          className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
          onClick={() => setIsMenuOpen(false)}
        >
          <FaHome /> Home
        </Link>
        <Link 
          href="/recommendations" 
          className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
          onClick={() => setIsMenuOpen(false)}
        >
          <FaRobot /> Ask AI
        </Link>
        
        {!isAuthenticated ? (
          <>
            <Link 
              href="/login" 
              className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
              onClick={() => setIsMenuOpen(false)}
            >
              <FaSignInAlt /> Login
            </Link>
            <Link 
              href="/register" 
              className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
              onClick={() => setIsMenuOpen(false)}
            >
              <FaUserPlus /> Register
            </Link>
          </>
        ) : (
          <>
            {isAdmin ? (
              <>
                <Link 
                  href="/admin" 
                  className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <FaTachometerAlt /> Dashboard
                </Link>
                <Link 
                  href="/account" 
                  className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <FaUser /> Account
                </Link>
              </>
            ) : (
              <>
                <Link 
                  href="/bookings" 
                  className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <FaHotel /> Bookings
                </Link>
                <Link 
                  href="/account" 
                  className="flex items-center gap-1 hover:underline hover:text-blue-200 transition-colors duration-200"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <FaUser /> Account
                </Link>
              </>
            )}
            <button
              onClick={() => {
                logout();
                setIsMenuOpen(false);
              }}
              className="flex items-center gap-1 text-red-400 hover:text-red-300 hover:underline transition-colors duration-200"
            >
              <FaSignOutAlt /> Log Out
            </button>
          </>
        )}
      </nav>
    </header>
  );
}