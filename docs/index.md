---
title: dev.harrel benchmarks
---

# Hello there
what up

<ul>
{%- for res in benchmarks.jackson['1.8.1'] -%}
  <li>{{ res.params.benchmarkFileName }}</li>
{%- endfor -%}
</ul>

<canvas id="chart1" width="600" height="400"></canvas>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
  const benchmarks = {{ benchmarks | processBenchmarks | jsonify }}
  const benchmark = benchmarks.enum
  const datasets = Object.values(benchmark.providers).map(providerData => {
    return {
      label: providerData.name,
      data: providerData.data,
      backgroundColor: 'rgba(75, 192, 192, 0.6)'
    }
  })
  const data = {
    labels: benchmark.versions,
    datasets: datasets
  };

  const ctx = document.getElementById('chart1').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: data,
    options: { responsive: true }
  });
</script>