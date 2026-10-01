import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import NavBar from "./components/NavBar";
import ProtectedRoute from "./components/ProtectedRoute";
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
            element={
              <ProtectedRoute>
                <ItemsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/collections"
            element={
              <ProtectedRoute>
                <CollectionsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/public"
            element={<PublicLibraryPage />}
          />

          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <AdminPage />
              </ProtectedRoute>
            }
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
