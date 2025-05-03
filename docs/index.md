---
title: dev.harrel benchmarks
---

# Hello there
what up

<ul>
{%- for x in benchmarks['1.8.1'].jackson['jmh-result'] -%}
  <li>{{ x }}</li>
{%- endfor -%}
</ul>

<canvas id="chart1" width="600" height="400"></canvas>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
  const data = {
    labels: ["Test A", "Test B", "Test C"],
    datasets: [{
      label: 'Time (ms)',
      data: [120, 90, 150],
      backgroundColor: 'rgba(75, 192, 192, 0.6)'
    }]
  };

  const ctx = document.getElementById('chart1').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: data,
    options: { responsive: true }
  });
</script>