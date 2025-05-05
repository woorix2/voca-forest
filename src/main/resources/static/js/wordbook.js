document.addEventListener('click', async function(e) {
  const target = e.target;

  // 나만의 단어장 하트 클릭
  if (target.classList.contains('heart-icon')) {
    e.stopPropagation();

    const word = target.dataset.word;
    const isLiked = target.src.includes('full-heart');

    if (isLiked) {
      // 단어장 삭제 요청
      const res = await fetch('/wordbook/remove', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ word })
      });

      if (res.ok) {
        // ✅ 성공적으로 삭제되면
        showToast("단어장에서 삭제되었습니다.");
        setTimeout(() => {
          location.reload(); // 1초 뒤 새로고침
        }, 1000);
      } else {
        alert("삭제 실패했습니다.");
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
