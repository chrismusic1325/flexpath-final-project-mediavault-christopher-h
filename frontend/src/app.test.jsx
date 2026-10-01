
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
} from "react-router-dom";

import App from "./App.jsx";
import NavBar from "./components/NavBar.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import ItemsPage from "./pages/ItemsPage.jsx";
import CollectionsPage from "./pages/CollectionsPage.jsx";
import PublicLibraryPage from "./pages/PublicLibraryPage.jsx";
import AdminPage from "./pages/AdminPage.jsx";

import {
  api,
  extractToken,
  getToken,
  getUsername,
  login,
  logout,
  register,
} from "./api.js";

import {
  buildQuery,
  visibilityLabel,
} from "./utils.js";

const store =
  new Map();

function response(
  data,
  ok = true,
  status = 200
) {
  return {
    ok,
    status,

    text: async () =>
      typeof data === "string"
        ? data
        : JSON.stringify(data),
  };
}

function json(renderer) {
  return JSON.stringify(
    renderer.toJSON()
  );
}

async function render(element) {
  let renderer;

  await act(async () => {
    renderer =
      TestRenderer.create(
        element
      );

    await Promise.resolve();
    await Promise.resolve();
  });

  return renderer;
}

beforeEach(() => {
  store.clear();

  global.localStorage = {
    getItem:
      jest.fn(
        (key) =>
          store.has(key)
            ? store.get(key)
            : null
      ),

    setItem:
      jest.fn(
        (key, value) =>
          store.set(
            key,
            String(value)
          )
      ),

    removeItem:
      jest.fn(
        (key) =>
          store.delete(key)
      ),

    clear:
      jest.fn(
        () =>
          store.clear()
      ),
  };

  global.fetch =
    jest.fn();

  global.window = {
    location: {
      reload:
        jest.fn(),
    },

    prompt:
      jest.fn(),

    confirm:
      jest.fn(),

    alert:
      jest.fn(),
  };
});

describe(
  "utilities",
  () => {
    test(
      "query and visibility helpers work",
      () => {
        expect(
          buildQuery({
            title: "Song",
            creator: "",
            type: null,
            sort: "asc",
          })
        ).toBe(
          "title=Song&sort=asc"
        );

        expect(
          visibilityLabel(true)
        ).toBe("Public");

        expect(
          visibilityLabel(false)
        ).toBe("Private");
      }
    );
  }
);

describe(
  "api client",
  () => {
    test(
      "extractToken supports common response shapes",
      () => {
        expect(
          extractToken(
            "a.b.c"
          )
        ).toBe("a.b.c");

        expect(
          extractToken(
            "not-a-token"
          )
        ).toBe("");

        expect(
          extractToken({
            accessToken:
              "d.e.f",
          })
        ).toBe("d.e.f");

        expect(
          extractToken({
            nested: {
              jwt: "g.h.i",
            },
          })
        ).toBe("g.h.i");

        expect(
          extractToken(null)
        ).toBe("");
      }
    );

    test(
      "login stores credentials and logout removes them",
      async () => {
        fetch.mockResolvedValue(
          response({
            token: "a.b.c",
          })
        );

        await login(
          "alice",
          "secret"
        );

        expect(
          getToken()
        ).toBe("a.b.c");

        expect(
          getUsername()
        ).toBe("alice");

        logout();

        expect(
          getToken()
        ).toBe("");

        expect(
          getUsername()
        ).toBe("");
      }
    );

    test(
      "api sends auth and parses JSON",
      async () => {
        store.set(
          "mediavault_token",
          "a.b.c"
        );

        fetch.mockResolvedValue(
          response({
            id: 1,
          })
        );

        const data =
          await api(
            "/api/items",
            {
              method: "POST",
              body:
                JSON.stringify({
                  title: "Song",
                }),
            }
          );

        expect(
          data.id
        ).toBe(1);

        const options =
          fetch.mock
            .calls[0][1];

        expect(
          options.headers
            .Authorization
        ).toBe(
          "Bearer a.b.c"
        );
      }
    );

    test(
      "api surfaces server errors",
      async () => {
        fetch.mockResolvedValue(
          response(
            {
              message:
                "Denied",
            },
            false,
            403
          )
        );

        await expect(
          api("/api/private")
        ).rejects.toThrow(
          "Denied"
        );

        fetch.mockResolvedValue(
          response(
            "Plain failure",
            false,
            500
          )
        );

        await expect(
          api("/api/error")
        ).rejects.toThrow(
          "Plain failure"
        );
      }
    );

    test(
      "register posts user data",
      async () => {
        fetch.mockResolvedValue(
          response({
            username:
              "alice",
          })
        );

        const result =
          await register(
            "alice",
            "secret"
          );

        expect(
          result.username
        ).toBe("alice");

        expect(
          fetch.mock
            .calls[0][1]
            .method
        ).toBe("POST");
      }
    );
  }
);

describe(
  "navigation and routing",
  () => {
    test(
      "guest navigation renders login link",
      async () => {
        const renderer =
          await render(
            <MemoryRouter>
              <NavBar />
            </MemoryRouter>
          );

        expect(
          json(renderer)
        ).toContain(
          "Login"
        );
      }
    );

    test(
      "logged-in user can log out",
      async () => {
        store.set(
          "mediavault_username",
          "alice"
        );

        const renderer =
          await render(
            <MemoryRouter>
              <NavBar />
            </MemoryRouter>
          );

        const button =
          renderer.root
            .findByProps({
              className:
                "link-button",
            });

        await act(async () => {
          button.props
            .onClick();
        });

        expect(
          getUsername()
        ).toBe("");
      }
    );

    test(
      "app routes dashboard",
      async () => {
        fetch
          .mockResolvedValueOnce(
            response([])
          )
          .mockResolvedValueOnce(
            response([])
          );

        const renderer =
          await render(
            <MemoryRouter
              initialEntries={[
                "/",
              ]}
            >
              <App />
            </MemoryRouter>
          );

        expect(
          json(renderer)
        ).toContain(
          "MediaVault"
        );
      }
    );
  }
);

describe(
  "dashboard and public pages",
  () => {
    test(
      "dashboard loads public counts",
      async () => {
        fetch
          .mockResolvedValueOnce(
            response([
              { id: 1 },
            ])
          )
          .mockResolvedValueOnce(
            response([
              { id: 2 },
            ])
          );

        const renderer =
          await render(
            <DashboardPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "Public items"
        );
      }
    );

    test(
      "dashboard handles API failure",
      async () => {
        fetch.mockRejectedValue(
          new Error(
            "offline"
          )
        );

        const renderer =
          await render(
            <DashboardPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "MediaVault"
        );
      }
    );

    test(
      "public library renders items and collections",
      async () => {
        fetch
          .mockResolvedValueOnce(
            response([
              {
                id: 1,
                title:
                  "Public Song",
                creator:
                  "Artist",
                mediaType:
                  "Music",
                description:
                  "Description",
                owner:
                  "alice",
              },
            ])
          )
          .mockResolvedValueOnce(
            response([
              {
                id: 2,
                name:
                  "Public Collection",
                description:
                  "Description",
                owner:
                  "alice",
              },
            ])
          );

        const renderer =
          await render(
            <PublicLibraryPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "Public Song"
        );

        expect(
          json(renderer)
        ).toContain(
          "Public Collection"
        );
      }
    );

    test(
      "public library renders errors",
      async () => {
        fetch.mockRejectedValue(
          new Error(
            "network"
          )
        );

        const renderer =
          await render(
            <PublicLibraryPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "network"
        );
      }
    );
  }
);

describe(
  "login page",
  () => {
    test(
      "requires credentials before registration",
      async () => {
        const renderer =
          await render(
            <MemoryRouter>
              <LoginPage />
            </MemoryRouter>
          );

        const registerButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.props
                  .type ===
                "button"
            );

        await act(async () => {
          registerButton.props
            .onClick();
        });

        expect(
          json(renderer)
        ).toContain(
          "Enter a username"
        );
      }
    );

    test(
      "registers valid user",
      async () => {
        fetch.mockResolvedValue(
          response({
            username:
              "alice",
          })
        );

        const renderer =
          await render(
            <MemoryRouter>
              <LoginPage />
            </MemoryRouter>
          );

        const inputs =
          renderer.root
            .findAllByType(
              "input"
            );

        await act(async () => {
          inputs[0].props
            .onChange({
              target: {
                value:
                  "alice",
              },
            });

          inputs[1].props
            .onChange({
              target: {
                value:
                  "secret",
              },
            });
        });

        const registerButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.props
                  .type ===
                "button"
            );

        await act(async () => {
          await registerButton
            .props
            .onClick();
        });

        expect(
          json(renderer)
        ).toContain(
          "Account created"
        );
      }
    );

    test(
      "shows failed login",
      async () => {
        fetch.mockResolvedValue(
          response(
            {
              message:
                "Bad login",
            },
            false,
            401
          )
        );

        const renderer =
          await render(
            <MemoryRouter>
              <LoginPage />
            </MemoryRouter>
          );

        const inputs =
          renderer.root
            .findAllByType(
              "input"
            );

        await act(async () => {
          inputs[0].props
            .onChange({
              target: {
                value:
                  "alice",
              },
            });

          inputs[1].props
            .onChange({
              target: {
                value:
                  "bad",
              },
            });
        });

        const form =
          renderer.root
            .findByType("form");

        await act(async () => {
          await form.props
            .onSubmit({
              preventDefault:
                jest.fn(),
            });
        });

        expect(
          json(renderer)
        ).toContain(
          "Bad login"
        );
      }
    );
  }
);

describe(
  "items page",
  () => {
    test(
      "loads, creates, edits and deletes items",
      async () => {
        const item = {
          id: 1,
          title: "Song",
          creator: "Artist",
          mediaType: "Music",
          description:
            "Description",
          public: true,
          owner: "alice",
        };

        fetch
          .mockResolvedValueOnce(
            response([item])
          )
          .mockResolvedValueOnce(
            response(item)
          )
          .mockResolvedValueOnce(
            response([item])
          )
          .mockResolvedValueOnce(
            response(item)
          )
          .mockResolvedValueOnce(
            response([item])
          )
          .mockResolvedValueOnce(
            response("")
          )
          .mockResolvedValueOnce(
            response([])
          );

        const renderer =
          await render(
            <ItemsPage />
          );

        expect(
          json(renderer)
        ).toContain("Song");

        const createForm =
          renderer.root
            .findByType("form");

        const inputs =
          renderer.root
            .findAllByType(
              "input"
            );

        await act(async () => {
          inputs[0].props
            .onChange({
              target: {
                value:
                  "New Song",
              },
            });
        });

        await act(async () => {
          await createForm.props
            .onSubmit({
              preventDefault:
                jest.fn(),
            });
        });

        window.prompt
          .mockReturnValueOnce(
            "Edited Song"
          )
          .mockReturnValueOnce(
            "Edited Description"
          );

        const editButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.children
                  .join("") ===
                "Edit"
            );

        await act(async () => {
          await editButton.props
            .onClick();
        });

        window.confirm
          .mockReturnValue(true);

        const deleteButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.children
                  .join("") ===
                "Delete"
            );

        await act(async () => {
          await deleteButton.props
            .onClick();
        });
      }
    );

    test(
      "shows load errors",
      async () => {
        fetch.mockRejectedValue(
          new Error(
            "items failed"
          )
        );

        const renderer =
          await render(
            <ItemsPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "items failed"
        );
      }
    );
  }
);

describe(
  "collections page",
  () => {
    test(
      "loads and manages collections",
      async () => {
        const collection = {
          id: 1,
          name: "Favorites",
          description:
            "Description",
          public: true,
          owner: "alice",
        };

        fetch
          .mockResolvedValueOnce(
            response([
              collection,
            ])
          )
          .mockResolvedValueOnce(
            response(
              collection
            )
          )
          .mockResolvedValueOnce(
            response([
              collection,
            ])
          )
          .mockResolvedValueOnce(
            response([
              {
                id: 5,
                title:
                  "Song",
              },
            ])
          )
          .mockResolvedValueOnce(
            response("")
          )
          .mockResolvedValueOnce(
            response(
              collection
            )
          )
          .mockResolvedValueOnce(
            response([
              collection,
            ])
          )
          .mockResolvedValueOnce(
            response("")
          )
          .mockResolvedValueOnce(
            response([])
          );

        const renderer =
          await render(
            <CollectionsPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "Favorites"
        );

        const form =
          renderer.root
            .findByType("form");

        const firstInput =
          renderer.root
            .findAllByType(
              "input"
            )[0];

        await act(async () => {
          firstInput.props
            .onChange({
              target: {
                value:
                  "New Collection",
              },
            });
        });

        await act(async () => {
          await form.props
            .onSubmit({
              preventDefault:
                jest.fn(),
            });
        });

        const buttons =
          renderer.root
            .findAllByType(
              "button"
            );

        const viewButton =
          buttons.find(
            (button) =>
              button.children
                .join("") ===
              "View items"
          );

        await act(async () => {
          await viewButton.props
            .onClick();
        });

        window.prompt
          .mockReturnValueOnce(
            "5"
          );

        const addButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.children
                  .join("") ===
                "Add item"
            );

        await act(async () => {
          await addButton.props
            .onClick();
        });

        window.prompt
          .mockReturnValueOnce(
            "Renamed"
          )
          .mockReturnValueOnce(
            "Changed"
          );

        const editButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.children
                  .join("") ===
                "Edit"
            );

        await act(async () => {
          await editButton.props
            .onClick();
        });

        window.confirm
          .mockReturnValue(true);

        const deleteButton =
          renderer.root
            .findAllByType(
              "button"
            )
            .find(
              (button) =>
                button.children
                  .join("") ===
                "Delete"
            );

        await act(async () => {
          await deleteButton.props
            .onClick();
        });
      }
    );

    test(
      "shows collection load errors",
      async () => {
        fetch.mockRejectedValue(
          new Error(
            "collection failed"
          )
        );

        const renderer =
          await render(
            <CollectionsPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "collection failed"
        );
      }
    );
  }
);

describe(
  "admin page",
  () => {
    test(
      "renders users and roles",
      async () => {
        fetch.mockResolvedValue(
          response([
            {
              username:
                "admin",
              roles: [
                "ADMIN",
              ],
            },
          ])
        );

        const renderer =
          await render(
            <AdminPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "ADMIN"
        );
      }
    );

    test(
      "shows authorization error",
      async () => {
        fetch.mockResolvedValue(
          response(
            {
              message:
                "Forbidden",
            },
            false,
            403
          )
        );

        const renderer =
          await render(
            <AdminPage />
          );

        expect(
          json(renderer)
        ).toContain(
          "Forbidden"
        );
      }
    );
  }
);
