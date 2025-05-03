module.exports = cfg => {
  return {
    dir: {
      input: "docs",
      output: "build/site",
      data: "../build/reports/jmh"
    }
  };
};