"use client";

import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from "react";
import { useRouter } from "next/navigation";
import { jwtDecode } from "jwt-decode";

const isTokenExpired = (token: string) => {
  try {
    const { exp } = jwtDecode<{ exp: number }>(token);
    console.log(Date.now());
    console.log(exp * 1000);
    return exp * 1000 < Date.now(); // Compare expiration with current time
  } catch (error) {
    console.error("Failed to decode token:", error);
    return true; // Treat invalid token as expired
  }
};


type AuthContextType = {
  isAuthenticated: boolean;
  role: string | null;
  login: (token: string, role: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [role, setRole] = useState<string | null>(null);

  const logout = useCallback(() => {
    setIsAuthenticated(false);
    setRole(null);
    localStorage.removeItem("authToken"); // Clear token
    localStorage.removeItem("authRole"); // Clear user
    setTimeout(() => router.push("/login"), 1000);
  }, [router]); // Add router as a dependency
  
  // Load authentication state from localStorage
  useEffect(() => {
    const validateToken = async () => {
      const token = localStorage.getItem("authToken");
      const storedRole = localStorage.getItem("authRole");
  
      if (token && !isTokenExpired(token)) {
        try {
          const response = await fetch("/api/auth/validate-token", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          });
  
          if (response.ok) {
            const data = await response.json();
            setIsAuthenticated(true);
            setRole(data.role || storedRole);
          } else {
            logout();
          }
        } catch (error) {
          console.error("Failed to validate token:", error);
          logout();
        }
      } else {
        logout(); // Clear session if token is expired or invalid
      }
    };
  
    validateToken();
  }, [logout]); // Include logout in the dependency array  
  

  const login = (token: string, role: string) => {
    setIsAuthenticated(true);
    setRole(role);
    localStorage.setItem("authToken", token); // Save token
    localStorage.setItem("authRole", role); // Save user
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, role, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
