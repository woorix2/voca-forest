document.addEventListener("DOMContentLoaded", function () {
      const yearSelect = document.querySelector(".birth-row select:nth-child(1)");
      const monthSelect = document.querySelector(".birth-row select:nth-child(2)");
      const daySelect = document.querySelector(".birth-row select:nth-child(3)");

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
});

   <!-- 이메일 인증하기 버튼 클릭-->
   document.getElementById('btnSendCode').addEventListener('click', () => {
    const local = document.getElementById('emailLocal').value.trim();
    const domain = document.getElementById('emailDomain').value.trim();
    const email = `${local}@${domain}`;

    if (!local || !domain) {
      alert("이메일을 모두 입력해주세요.");
      return;
    }

    fetch('/email/send-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    })
    .then(res => res.text())
    .then(msg => {
      alert(msg);
    })
    .catch(err => {
      console.error(err);
      alert("인증코드 전송에 실패했습니다.");
    });
  });

  <!-- 이메일 본인인증 확인 -->
  let isVerified = false; // 이메일 인증 여부

  document.getElementById('btnCheckCode').addEventListener('click', () => {
    const local = document.getElementById('emailLocal').value.trim();
    const domain = document.getElementById('emailDomain').value.trim();
    const email = `${local}@${domain}`;
    const code = document.getElementById('verifyCode').value.trim();

    if (!code) {
      alert("인증번호를 입력해주세요.");
      return;
    }

    fetch('/email/verify-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, code })
    })
    .then(res => res.json())
    .then(data => {
      if (data.verified) {
        isVerified = true; // 인증 완료
        document.getElementById('verifyResult').style.display = 'block';
      } else {
        alert("인증번호가 일치하지 않습니다.");
      }
    })
    .catch(err => {
      console.error(err);
      alert("인증 확인 중 오류가 발생했습니다.");
    });
  });

  <!-- 유효성 검사-->
  document.querySelector('.submit-btn').addEventListener('click', (e) => {
    const password = document.querySelector('input[name="password"]').value.trim();
    const confirmPassword = document.querySelector('input[placeholder="비밀번호를 확인해주세요."]').value.trim();
    const name = document.querySelector('input[name="name"]').value.trim();
    const birthYear = document.querySelector('select[name="birthYear"]').value;
    const birthMonth = document.querySelector('select[name="birthMonth"]').value;
    const birthDay = document.querySelector('select[name="birthDay"]').value;

    // 이메일 인증 여부
    if (!isVerified) {
      e.preventDefault();
      alert("이메일 본인인증 후 가입 가능합니다.");
      return;
    }

    // 비밀번호 검사
    if (!password || !confirmPassword) {
      e.preventDefault();
      alert("비밀번호를 입력해주세요.");
      return;
    }

    if (password !== confirmPassword) {
      e.preventDefault();
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // 이름 검사
    if (!name) {
      e.preventDefault();
      alert("이름을 입력해주세요.");
      return;
    }

    // 생년월일 검사
    if (birthYear === "년도" || birthMonth === "월" || birthDay === "일") {
      e.preventDefault();
      alert("생년월일을 선택해주세요.");
      return;
    }
  });

  <!-- form 넘기기 전 생년월일 처리-->
  document.querySelector('form').addEventListener('submit', function (e) {
    const local = document.getElementById('emailLocal').value.trim();
    const domain = document.getElementById('emailDomain').value.trim();
    const email = `${local}@${domain}`;
    document.getElementById('fullEmail').value = email;
  });

  document.addEventListener('keydown', function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
    };
  }, true);