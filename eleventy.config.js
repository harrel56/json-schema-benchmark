module.exports = cfg => {
  cfg.addFilter('processBenchmarks', data => {
    const res = {}
    Object.entries(data).forEach(([provider, providerData]) => {
      Object.entries(providerData).forEach(([version, benchmarks]) => {
        benchmarks.forEach(benchmark => {
          const keyword = benchmark.params.benchmarkFileName.split(/[/\\]/).pop().replace(/\.[^/.]+$/, "")
          const keywordData = res[keyword] = res[keyword] ?? {}

          keywordData.versions = keywordData.versions ?? []
          if (!keywordData.versions.includes(version)) {
            keywordData.versions.push(version)
          }

          keywordData.providers = keywordData.providers ?? {}
          if (!keywordData.providers[provider]) {
            keywordData.providers[provider] = { name: provider, data: [] }
          }
          keywordData.providers[provider].data.push(benchmark.primaryMetric.score)
        })
      })
    })
    return res
  })
  return {
    dir: {
      input: "docs",
      output: "build/site",
      data: "../build/reports/jmh"
    }
  };
};