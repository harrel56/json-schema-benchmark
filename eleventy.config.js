module.exports = cfg => {
  cfg.addFilter("prettyJson", obj => JSON.stringify(obj, null, 2))
  cfg.addFilter("entries", obj => Object.entries(obj))
  cfg.addFilter('processBenchmarks', data => {
    const res = {}
    Object.entries(data).forEach(([provider, providerData]) => {
      Object.entries(providerData).forEach(([version, benchmarks]) => {
        benchmarks.forEach(benchmark => {
          const filename = benchmark.params.benchmarkFileName.split(/[/\\]/).pop().replace(/\.[^/.]+$/, "")
          const keywordData = res[filename] = res[filename] ?? {}

          keywordData.title = benchmark.params.model?.title
          keywordData.description = benchmark.params.model?.description
          keywordData.schema = benchmark.params.model?.schema
          keywordData.instance = benchmark.params.model?.instance

          keywordData.scoreUnit = benchmark.primaryMetric.scoreUnit
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