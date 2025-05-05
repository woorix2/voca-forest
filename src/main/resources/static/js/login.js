<!-- 회원가입 페이지 이동 -->
  document.querySelector('.join-btn').addEventListener('click', (e) => {
    window.location.href = "/signup";
  });

  // 쿠키에서 saveId 꺼내서 이메일 input에 자동입력
  window.addEventListener('DOMContentLoaded', () => {
    const emailInput = document.querySelector('input[name="email"]');
    const rememberCheckbox = document.getElementById('rememberId');
    const savedEmail = getCookie('saveId');

    if (savedEmail) {
      emailInput.value = savedEmail;
      rememberCheckbox.checked = true;
    }
  });

  // 쿠키 가져오는 함수
  function getCookie(name) {
    const value = document.cookie
      .split('; ')
      .find(row => row.startsWith(name + '='));
    return value ? decodeURIComponent(value.split('=')[1]) : null;
  }
