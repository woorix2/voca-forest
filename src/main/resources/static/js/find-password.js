document.addEventListener("DOMContentLoaded", function () {
  const submitBtn = document.querySelector('.submit-btn');
  const yearSelect = document.querySelector('select[name="birthYear"]');
  const monthSelect = document.querySelector('select[name="birthMonth"]');
  const daySelect = document.querySelector('select[name="birthDay"]');
  const findContainer = document.querySelector('.find-container');
  const resultContainer = document.querySelector('.result-container');

  const emailInput = document.querySelector('input[name="email"]');
  const nameInput = document.querySelector('input[name="name"]');
  const resultName = document.querySelector('.result-name');
  const resultEmail = document.querySelector('.result-email');

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

    // 일: 선택된 년/월에 따라 동적으로 생성
    function updateDays() {
      daySelect.innerHTML = "<option>일</option>"; // 초기화
      const year = parseInt(yearSelect.value);
      const month = parseInt(monthSelect.value);

      if (!year || !month) return;

      // 해당 달의 마지막 일 구하기
      const lastDay = new Date(year, month, 0).getDate(); // 0번째 일 = 이전 달의 마지막 날

      for (let d = 1; d <= lastDay; d++) {
        const option = document.createElement("option");
        option.value = d;
        option.textContent = d + "일";
        daySelect.appendChild(option);
      }
    }

    yearSelect.addEventListener("change", updateDays);
    monthSelect.addEventListener("change", updateDays);

  // '찾기' 버튼 클릭 이벤트
  submitBtn.addEventListener('click', async (e) => {
    e.preventDefault(); // submit 막기
    const email = emailInput.value.trim();
    const name = nameInput.value.trim();
    const birthYear = yearSelect.value;
    const birthMonth = monthSelect.value;
    const birthDay = daySelect.value;

    // 유효성 검사
    if (!email) {
      alert("이메일을 입력해주세요.");
      return;
    }
    if (!name) {
      alert("이름을 입력해주세요.");
      return;
    }

    if (birthYear === "년도" || birthMonth === "월" || birthDay === "일") {
      alert("생년월일을 선택해주세요.");
      return;
    }

    // 생년월일 조합
    const birthDate = `${birthYear}-${birthMonth.padStart(2, '0')}-${birthDay.padStart(2, '0')}`;

    try {
        const response = await fetch('/find-password', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, name, birthDate })
        });

        if (response.ok) {
          // 일치하는 사용자 있음 -> 비밀번호 재설정 페이지로 이동
          location.href = '/reset-password';
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
