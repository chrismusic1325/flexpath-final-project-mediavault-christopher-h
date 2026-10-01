module.exports = {
  testEnvironment: "node",
  coverageProvider: "v8",

  transform: {
    "^.+\\.[jt]sx?$":
      "<rootDir>/jest-esbuild-transformer.cjs",
  },

  moduleNameMapper: {
    "\\.(css|less|scss|sass)$":
      "<rootDir>/src/test/styleMock.cjs",
  },

  collectCoverageFrom: [
    "src/**/*.{js,jsx}",
    "!src/main.jsx",
    "!src/**/*.test.{js,jsx}",
    "!src/test/**",
  ],

  coverageReporters: [
    "text",
    "json-summary",
  ],

  coverageThreshold: {
    global: {
      lines: 70,
      statements: 70,
      functions: 70,
    },
  },
};
