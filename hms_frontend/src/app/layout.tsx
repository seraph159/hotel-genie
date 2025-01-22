"use client";

import Link from "next/link";
import { AuthProvider, useAuth } from "./context/AuthContext";
import "./globals.css";

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthProvider>
      <html lang="en">
        <body>
          <Header />
          <main className="p-6">{children}</main>
        </body>
      </html>
    </AuthProvider>
  );
}

function Header() {
  const { isAuthenticated, role, logout } = useAuth();

  const isAdmin = role === 'ROLE_ADMIN';

  return (
    <header className="bg-blue-600 text-white p-4 flex justify-between items-center">
      <h1 className="text-xl font-bold">HotelGenie</h1>
      <nav className="flex space-x-4">
        <Link href="/" className="hover:underline">
          Home
        </Link>
        <Link href="/recommendations" className="hover:underline">
          Ask AI
        </Link>
        {!isAuthenticated ? (
          <>
            <Link href="/login" className="hover:underline">
              Login
            </Link>
            <Link href="/register" className="hover:underline">
              Register
            </Link>
          </>
        ) : (
          <>
            {isAdmin ? (
              <>
                <Link href="/admin" className="hover:underline">
                  Dashboard
                </Link>
                <Link href="/account" className="hover:underline">
                  Account
                </Link>
              </>
            ) : (
              <>
                <Link href="/bookings" className="hover:underline">
                  Bookings
                </Link>
                <Link href="/account" className="hover:underline">
                  Account
                </Link>
              </>
            )}
            <button
              onClick={logout}
              className="hover:underline text-red-400"
            >
              Log Out
            </button>
          </>
        )}
      </nav>
    </header>
  );
}
