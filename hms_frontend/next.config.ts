import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*', // Matches requests to /api/*
        destination: 'http://localhost:8080/api/:path*', // Forwards them to Spring Boot API
      },
    ];
  },
};

export default nextConfig;
