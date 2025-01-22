'use client';

import { useEffect, useState, useCallback } from 'react';

interface Client {
  email: string;
  name: string;
  phone: string;
}

export default function ManageClients() {
  const [clients, setClients] = useState<Client[]>([]);
  const [form, setForm] = useState<Partial<Client>>({});
  const [editingClient, setEditingClient] = useState<Client | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  // Fetch clients function
  const fetchClients = useCallback(async () => {
    try {
      const response = await fetch('/api/clients', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data = await response.json();
      setClients(data);
    } catch (err) {
      setError('Failed to fetch clients: ' + err);
    }
  }, [accessToken]);

  // Load token on component mount
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      setAccessToken(token);
    }
  }, []);

  // Fetch clients when accessToken changes
  useEffect(() => {
    if (accessToken) {
      fetchClients();
    }
  }, [accessToken, fetchClients]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const url = editingClient ? `/api/clients/${editingClient.email}/${form.phone}` : '/api/clients';
    const method = editingClient ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${accessToken}` },
        body: JSON.stringify(form),
      });
      if (!response.ok) throw new Error('Failed to save client');
      setForm({});
      setEditingClient(null);
      fetchClients();
    } catch (err) {
      setError('Failed to save client: ' + err);
    }
  };

  const handleEdit = (client: Client) => {
    setForm(client);
    setEditingClient(client);
  };

  const handleDelete = async (email: string) => {
    try {
      const response = await fetch(`/api/clients/${email}`, { 
        method: 'DELETE',
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) throw new Error('Failed to delete client');
      fetchClients();
    } catch (err) {
      setError('Failed to delete client: ' + err);
    }
  };

  return (
    <main className="p-6">
      <h1 className="text-3xl font-bold mb-6">Manage Clients</h1>

      {/* Error message */}
      {error && <p className="text-red-500">{error}</p>}

      {/* Client form */}
      <form onSubmit={handleSubmit} className="mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            name="email"
            type="email"
            value={form.email || ''}
            onChange={handleInputChange}
            placeholder="Email"
            className="border p-2 rounded"
            required
            disabled={!!editingClient}
          />
          <input
            name="name"
            type="text"
            value={form.name || ''}
            onChange={handleInputChange}
            placeholder="Name"
            className="border p-2 rounded"
            required
          />
          <input
            name="phone"
            type="text"
            value={form.phone || ''}
            onChange={handleInputChange}
            placeholder="Phone Number"
            className="border p-2 rounded"
          />
          {/* <select
            name="paymentType"
            value={form.paymentType || ''}
            onChange={handleInputChange}
            className="border p-2 rounded"
            required
          >
            <option value="" disabled>
              Select Payment Type
            </option>
            <option value="CREDIT_CARD">Credit Card</option>
            <option value="PAYPAL">PayPal</option>
            <option value="BANK_TRANSFER">Bank Transfer</option>
          </select> */}
        </div>
        <button
            type="submit"
            className="mt-4 bg-blue-500 text-white px-4 py-2 rounded"
        >
            {editingClient ? 'Update Client' : 'Add Client'}
        </button>
        {editingClient && (
            <button
            type="button"
            onClick={() => {
                setEditingClient(null);
                setForm({});
            }}
            className="ml-2 mt-4 bg-gray-500 text-white px-4 py-2 rounded"
            >
            Cancel
            </button>
        )}
        </form>

      {/* Client list */}
      <ul>
        {clients.map((client) => (
          <li
            key={client.email}
            className="border-b p-4 flex justify-between items-center"
          >
            <div>
              <strong>{client.name}</strong> ({client.email})<br />
              Phone: {client.phone || 'N/A'}<br />
              {/* Payment Type: {client.paymentType} */}
            </div>
            <div className="flex space-x-2">
              <button
                onClick={() => handleEdit(client)}
                className="bg-yellow-400 text-white px-2 py-1 rounded"
              >
                Edit
              </button>
              <button
                onClick={() => handleDelete(client.email)}
                className="bg-red-500 text-white px-2 py-1 rounded"
              >
                Delete
              </button>
            </div>
          </li>
        ))}
      </ul>
    </main>
  );
}
