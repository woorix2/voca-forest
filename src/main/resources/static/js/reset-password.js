document.addEventListener("DOMContentLoaded", function () {
  const submitBtn = document.querySelector('.submit-btn');
  const passwordInput = document.querySelector('input[name="password"]');
  const checkPasswordInput = document.querySelector('input[name="check-password"]');

  // '설정' 버튼 클릭 이벤트
  submitBtn.addEventListener('click', async (e) => {
    e.preventDefault(); // submit 막기

    const password = passwordInput.value.trim();
    const checkPassword = checkPasswordInput.value.trim();

    // 유효성 검사
    if (!password || !checkPassword) {
      alert("새 비밀번호를 입력해주세요.");
      return;
    }

    if (password !== checkPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      const response = await fetch('/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password })
      });

      if (response.ok) {
        alert("비밀번호가 성공적으로 변경되었습니다.");
        location.href = '/login'; // 비밀번호 변경 후 로그인 페이지로 이동
      } else {
        const errorMessage = await response.text();
        alert(errorMessage);
      }
    } catch (error) {
      console.error('비밀번호 변경 요청 실패:', error);
      alert('서버 오류가 발생했습니다.');
    }
  });
});