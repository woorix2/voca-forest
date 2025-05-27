//단어장 저장 및 삭제 처리(하트버튼)
document.addEventListener('click', async function (e) {
  const target = e.target;

  if (target.classList.contains('heart-icon')) {
    e.stopPropagation();

    if (!isLoggedIn) {
      alert("로그인 후에 단어장을 사용할 수 있습니다.");
      location.href = "/login";
      return;
    }

    const word = target.dataset.word;
    const isLiked = target.src.includes('full-heart');

    if (isLiked) {
      // ✅ DELETE 요청으로 변경 + word를 경로에 포함
      const res = await fetch(`/wordbook/${encodeURIComponent(word)}`, {
        method: 'DELETE'
      });

      if (res.ok) {
        showToast("단어장에서 삭제되었습니다.");
        target.src = '/img/icon/empty-heart.png';
        target.alt = '빈 하트 이미지';
        userWordbook = userWordbook.filter(w => w !== word);
      } else {
        alert("단어장에서 삭제 실패");
      }
    } else {
      // ✅ 저장은 그대로 POST
      const res = await fetch('/wordbook', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ word })
      });

      if (res.ok) {
        target.src = '/img/icon/full-heart.png';
        target.alt = '채워진 하트 이미지';
        userWordbook.push(word);
        showToast("단어장에 저장되었습니다.");
      } else {
        alert("단어장에 저장 실패");
      }
    }
  }
});

function showToast(message) {
  const toast = document.getElementById('toast');
  toast.innerText = message;
  toast.classList.add('show');
  setTimeout(() => {
    toast.classList.remove('show');
  }, 2000);
}

// 랜덤 단어 가져오기 및 사용자 권한 체크
document.addEventListener("DOMContentLoaded", function () {
  const heartIcons = document.querySelectorAll(".heart-icon");

  heartIcons.forEach(icon => {
    const word = icon.dataset.word;
    if (userWordbook.includes(word)) {
      icon.src = '/img/icon/full-heart.png';
      icon.alt = '채워진 하트 이미지';
    } else {
      icon.src = '/img/icon/empty-heart.png';
      icon.alt = '빈 하트 이미지';
    }
  });
});
