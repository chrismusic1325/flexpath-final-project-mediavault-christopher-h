import React, {
  useState,
} from "react";

import {
  useNavigate,
} from "react-router-dom";

import {
  login,
  register,
} from "../api";

export default function LoginPage() {
  const navigate = useNavigate();

  const [
    username,
    setUsername,
  ] = useState("");

  const [
    password,
    setPassword,
  ] = useState("");

  const [
    message,
    setMessage,
  ] = useState("");

  async function handleLogin(
    event
  ) {
    event.preventDefault();

    setMessage("");

    try {
      await login(
        username,
        password
      );

      navigate("/items");
      window.location.reload();

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function handleRegister() {
    if (!username ||
        !password) {
      setMessage(
        "Enter a username and password first."
      );

      return;
    }

    try {
      await register(
        username,
        password
      );

      setMessage(
        "Account created. You can now log in."
      );

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  return (
    <section className="narrow">
      <h1>
        Login
      </h1>

      <form
        className="form-card"
        onSubmit={handleLogin}
      >
        <label>
          Username

          <input
            value={username}
            onChange={(event) =>
              setUsername(
                event.target.value
              )
            }
            required
          />
        </label>

        <label>
          Password

          <input
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(
                event.target.value
              )
            }
            required
          />
        </label>

        <div className="button-row">
          <button type="submit">
            Login
          </button>

          <button
            type="button"
            className="secondary"
            onClick={
              handleRegister
            }
          >
            Register
          </button>
        </div>

        {message && (
          <p className="message">
            {message}
          </p>
        )}
      </form>
    </section>
  );
}
