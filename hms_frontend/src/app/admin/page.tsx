'use client';

import Link from 'next/link';

export default function AdminHome() {
  const sections = [
    { name: 'Manage Rooms', link: '/admin/rooms', description: 'Add, update, or delete room details.' },
    { name: 'Manage Bookings', link: '/admin/bookings', description: 'View and handle all bookings.' },
    { name: 'Manage Clients', link: '/admin/clients', description: 'Manage client accounts and details.' },
  ];

  return (
    <main className="p-6">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {sections.map((section) => (
          <Link key={section.name} href={section.link}>
            <div className="block bg-blue-100 p-4 rounded-lg shadow hover:bg-blue-200">
              <h2 className="text-xl font-semibold">{section.name}</h2>
              <p className="text-gray-700">{section.description}</p>
            </div>
          </Link>
        ))}
      </div>
    </main>
  );
}
