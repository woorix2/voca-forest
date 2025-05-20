document.addEventListener("DOMContentLoaded", function () {
  const submitBtn = document.querySelector('.submit-btn');
  const yearSelect = document.querySelector('select[name="birthYear"]');
  const monthSelect = document.querySelector('select[name="birthMonth"]');
  const daySelect = document.querySelector('select[name="birthDay"]');

  const emailInput = document.querySelector('input[name="email"]');
  const nameInput = document.querySelector('input[name="name"]');

  // 년도: 1950 ~ 현재년도까지
  const currentYear = new Date().getFullYear();
  for (let y = currentYear - 5; y >= 1950; y--) {
    const option = document.createElement("option");
    option.value = y;
    option.textContent = y + "년";
    yearSelect.appendChild(option);
  }

  // 월: 1 ~ 12
  for (let m = 1; m <= 12; m++) {
    const option = document.createElement("option");
    option.value = m;
    option.textContent = m + "월";
    monthSelect.appendChild(option);
  }

  // 일 동적 생성
  function updateDays() {
    daySelect.innerHTML = "<option>일</option>";
    const year = parseInt(yearSelect.value);
    const month = parseInt(monthSelect.value);
    if (!year || !month) return;

    const lastDay = new Date(year, month, 0).getDate();
    for (let d = 1; d <= lastDay; d++) {
      const option = document.createElement("option");
      option.value = d;
      option.textContent = d + "일";
      daySelect.appendChild(option);
    }
  }

  yearSelect.addEventListener("change", updateDays);
  monthSelect.addEventListener("change", updateDays);

  // 찾기 버튼 이벤트
  submitBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    const email = emailInput.value.trim();
    const name = nameInput.value.trim();
    const birthYear = yearSelect.value;
    const birthMonth = monthSelect.value;
    const birthDay = daySelect.value;

    if (!email || !name || birthYear === "년도" || birthMonth === "월" || birthDay === "일") {
      alert("모든 항목을 올바르게 입력해주세요.");
      return;
    }

    const birthDate = `${birthYear}-${birthMonth.padStart(2, '0')}-${birthDay.padStart(2, '0')}`;

    try {
      const response = await fetch('/find-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, name, birthDate: birthDate })
      });

      if (response.ok) {
        alert("비밀번호 재설정 링크가 이메일로 전송되었습니다. 메일함을 확인해주세요.");
        location.href = "/login"; // 또는 그대로 두고 대기 상태 유지 가능
      } else {
        const errorMessage = await response.text();
        alert(errorMessage);
      }
    } catch (error) {
      console.error('비밀번호 찾기 요청 실패:', error);
      alert('서버 오류가 발생했습니다.');
    }
  });
});
