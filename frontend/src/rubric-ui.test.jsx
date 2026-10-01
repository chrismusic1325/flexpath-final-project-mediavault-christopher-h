import {
  beforeEach,
  describe,
  expect,
  jest,
  test,
} from "@jest/globals";

import TestRenderer, {
  act,
} from "react-test-renderer";

import {
  MemoryRouter,
  Route,
  Routes,
} from "react-router-dom";

import ProtectedRoute from
  "./components/ProtectedRoute.jsx";

function json(renderer) {
  return JSON.stringify(
    renderer.toJSON()
  );
}

beforeEach(() => {
  const values =
    new Map();

  global.localStorage = {
    getItem:
      jest.fn(
        (key) =>
          values.has(key)
            ? values.get(key)
            : null
      ),

    setItem:
      jest.fn(
        (key, value) =>
          values.set(
            key,
            String(value)
          )
      ),

    removeItem:
      jest.fn(
        (key) =>
          values.delete(key)
      ),
  };
});

describe(
  "ProtectedRoute",
  () => {
    test(
      "redirects signed-out users",
      async () => {
        let renderer;

        await act(async () => {
          renderer =
            TestRenderer.create(
              <MemoryRouter
                initialEntries={[
                  "/private",
                ]}
              >
                <Routes>
                  <Route
                    path="/private"
                    element={
                      <ProtectedRoute>
                        <p>
                          Private
                        </p>
                      </ProtectedRoute>
                    }
                  />

                  <Route
                    path="/login"
                    element={
                      <p>
                        Login screen
                      </p>
                    }
                  />
                </Routes>
              </MemoryRouter>
            );
        });

        expect(
          json(renderer)
        ).toContain(
          "Login screen"
        );
      }
    );

    test(
      "renders content for signed-in users",
      async () => {
        localStorage.setItem(
          "mediavault_username",
          "alice"
        );

        let renderer;

        await act(async () => {
          renderer =
            TestRenderer.create(
              <MemoryRouter>
                <ProtectedRoute>
                  <p>
                    Private
                  </p>
                </ProtectedRoute>
              </MemoryRouter>
            );
        });

        expect(
          json(renderer)
        ).toContain(
          "Private"
        );
      }
    );
  }
);
