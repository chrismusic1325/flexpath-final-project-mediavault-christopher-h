const esbuild = require("esbuild");

module.exports = {
  process(sourceText, sourcePath) {
    const loader = sourcePath.endsWith(".jsx") ? "jsx" : "js";

    const result = esbuild.transformSync(sourceText, {
      loader,
      format: "cjs",
      target: "node20",
      jsx: "automatic",
      sourcemap: "inline",
    });

    return { code: result.code };
  },
};
