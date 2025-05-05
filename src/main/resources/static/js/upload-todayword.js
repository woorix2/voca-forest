// 현재년도 기준, +10년까지
const yearSelect = document.getElementById('year');
const currentYear = new Date().getFullYear();
for (let y = currentYear; y <= currentYear + 10; y++) {
  const option = document.createElement('option');
  option.value = y;
  option.textContent = y + "년";
  yearSelect.appendChild(option);
}

// 1일부터 31일까지
const daySelect = document.getElementById('day');
for (let d = 1; d <= 31; d++) {
  const option = document.createElement('option');
  option.value = d;
  option.textContent = d + "일";
  daySelect.appendChild(option);
}