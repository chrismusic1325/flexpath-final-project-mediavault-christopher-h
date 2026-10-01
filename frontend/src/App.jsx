import React from "react";
import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import NavBar from "./components/NavBar";
import DashboardPage from "./pages/DashboardPage";
import LoginPage from "./pages/LoginPage";
import ItemsPage from "./pages/ItemsPage";
import CollectionsPage from "./pages/CollectionsPage";
import PublicLibraryPage from "./pages/PublicLibraryPage";
import AdminPage from "./pages/AdminPage";

import "./App.css";

export default function App() {
  return (
    <>
      <NavBar />

      <main className="app-shell">
        <Routes>
          <Route
            path="/"
            element={<DashboardPage />}
          />

          <Route
            path="/login"
            element={<LoginPage />}
          />

          <Route
            path="/items"
            element={<ItemsPage />}
          />

          <Route
            path="/collections"
            element={<CollectionsPage />}
          />

          <Route
            path="/public"
            element={<PublicLibraryPage />}
          />

          <Route
            path="/admin"
            element={<AdminPage />}
          />

          <Route
            path="*"
            element={
              <Navigate to="/" />
            }
          />
        </Routes>
      </main>
    </>
  );
}
