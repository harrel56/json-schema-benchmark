module.exports = cfg => {
  cfg.addPassthroughCopy({ "build/reports/jmh": "_data/benchmarks" });
  return {
    dir: {
      input: "docs",
      output: "build/site"
    }
  };
};