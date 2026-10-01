/* eslint-disable react/prop-types */
import {
  Navigate,
} from "react-router-dom";

import {
  getUsername,
} from "../api";

export default function ProtectedRoute({
  children,
}) {
  if (!getUsername()) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return children;
}
