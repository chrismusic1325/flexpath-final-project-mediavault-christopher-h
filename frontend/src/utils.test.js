import {
  describe,
  expect,
  test,
} from "@jest/globals";

import {
  buildQuery,
  visibilityLabel,
} from "./utils.js";

describe(
  "MediaVault utilities",
  () => {
    test(
      "buildQuery skips empty values",
      () => {
        expect(
          buildQuery({
            title: "Song",
            creator: "",
            direction: "asc",
          })
        ).toBe(
          "title=Song&direction=asc"
        );
      }
    );

    test(
      "visibilityLabel describes privacy",
      () => {
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
