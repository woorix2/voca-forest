  const wrapper = document.querySelector(".wrapper");
  const floatingWordsContainer = document.querySelector(".floating-words");
  const wordModal = document.getElementById("wordModal");
  const wordModalContent = document.querySelector(".word-modal-content");
  const todayWordModal = document.getElementById("todayWordModal");
  const introModal = document.getElementById("introModal");
  const mypageModal = document.getElementById("mypageModal");
  const randomWordModal = document.getElementById("randomWordModal");
  const toast = document.getElementById("toast");


  // 단어 상제 모달 제어
  function openModal(spanElement) {
    const wordName = spanElement.innerText;
    const word = wordData.find(w => w.word === wordName);

    // 기존 유사어 초기화
    wordModalContent.querySelectorAll(".search-word-info").forEach(el => el.remove());

    if (word) {
      document.querySelector(".word-title").innerText = word.word;
      document.querySelector(".word-type").innerText = word.partSpeech + " · " + word.meaning;
      document.querySelector(".word-example p").innerText = word.exampleSentence;
      const heartImg = document.querySelector(".word-info img");
      heartImg.dataset.word = word.word;

      // 단어장에 있으면 채운 하트, 없으면 빈 하트
      if (userWordbook.includes(word.word)) {
        heartImg.src = '/img/icon/full-heart.png';
        heartImg.alt = '채워진 하트 이미지';
      } else {
        heartImg.src = '/img/icon/empty-heart.png';
        heartImg.alt = '빈 하트 이미지';
      }
    }

    // 유사어 요청
    fetch('/synonyms/by-word', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ word: wordName })
    })
    .then(response => response.json())
    .then(data => {
      data.forEach(syn => {
        const synonymDiv = document.createElement("div");
        synonymDiv.classList.add("search-word-info");

        // 유사어마다 하트 상태 설정
        const heartIconSrc = userWordbook.includes(syn.word)
            ? '/img/icon/full-heart.png'
            : '/img/icon/empty-heart.png';

        synonymDiv.innerHTML = `
          <h2 class="search-word-title">${syn.word}</h2>
          <img src="${heartIconSrc}" class="heart-icon" data-word="${syn.word}" />
          <p class="search-word-type">${syn.partSpeech} · ${syn.meaning}</p>
          <div class="search-word-example">
            <span class="example-tag">예시 문장</span>
            <p>${syn.exampleSentence}</p>
          </div>
        `;
        wordModalContent.appendChild(synonymDiv);
      });
    });

    randomWordModal.classList.remove("show");
    mypageModal.classList.remove("show");
    todayWordModal.classList.remove("show");
    introModal.classList.remove("show");
    wordModal.classList.add("show");
    wrapper.classList.add("modal-open");
  }

  function closeModal() {
    wordModal.classList.remove("show");
    wrapper.classList.remove("modal-open");
  }

  function openIntroModal() {
    randomWordModal.classList.remove("show");
    mypageModal.classList.remove("show");
    todayWordModal.classList.remove("show");
    wordModal.classList.remove("show");
    introModal.classList.add("show");
    wrapper.classList.add("modal-open");
  }

  function closeIntroModal() {
    introModal.classList.remove("show");
    wrapper.classList.remove("modal-open");
  }

  function openMypageModal() {
    randomWordModal.classList.remove("show");
    introModal.classList.remove("show");
    todayWordModal.classList.remove("show");
    wordModal.classList.remove("show");
    wrapper.classList.add("modal-open");
    mypageModal.classList.add("show");

    fetch('/recent-search')
      .then(res => res.json())
      .then(words => {
        const container = document.querySelector("#recent-search-section .word-list");
        container.innerHTML = "";

        if (words.length === 0) {
          container.innerHTML = "<li>최근 검색어가 없습니다.</li>";
        } else {
          words.forEach(word => {
            const li = document.createElement("li");

            // 단어장 저장 상태 세팅(하트)
            const isLiked = userWordbook.includes(word);
            const heartIconSrc = isLiked ? '/img/icon/full-heart.png' : '/img/icon/empty-heart.png';

            li.innerHTML = `
              <span>
                ${word}
                <img src="${heartIconSrc}" class="heart-icon" data-word="${word}" />
              </span>
              <img class="remove-search" src="/img/icon/modal-close.png" alt="최근 검색어 삭제 이미지" data-word="${word}" />
            `;
            container.appendChild(li);
          });

          container.querySelectorAll(".remove-search").forEach(button => {
            button.addEventListener("click", async function() {
              const word = this.dataset.word;
              if (confirm(`'${word}' 검색어를 삭제할까요?`)) {
                const res = await fetch('/remove-search', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ word })
                });
                if (res.ok) {
                  this.parentElement.remove();
                } else if (res.status === 401) {
                    alert('로그인이 필요합니다. 로그인 후 다시 시도해주세요.');
                    location.href = '/login';
                } else {
                    alert('삭제에 실패했습니다.');
                }
              }
            });
          });
        }
      });
  }

  function closeMypageModal() {
    wrapper.classList.remove("modal-open");
    mypageModal.classList.remove("show");
  }

  function openTodayWordModal() {
    randomWordModal.classList.remove("show");
    introModal.classList.remove("show");
    wordModal.classList.remove("show");
    wrapper.classList.add("modal-open");
    mypageModal.classList.remove("show");
    todayWordModal.classList.add("show");
    wrapper.classList.add("modal-open");
  }

  function closeTodayWordModal() {
    wrapper.classList.remove("modal-open");
    todayWordModal.classList.remove("show");
  }

  function openRandomWordModal() {
    fetch('/random-word')
      .then(res => res.json())
      .then(word => {
        if (!word || Object.keys(word).length === 0) {
          alert("단어를 불러올 수 없습니다.");
          return;
        }

        document.querySelector("#randomWordModal .word-title").innerText = word.word;
        document.querySelector("#randomWordModal .word-type").innerText = word.partSpeech + " · " + word.meaning;
        document.querySelector("#randomWordModal .word-example p").innerText = word.exampleSentence;

        const heartImg = document.querySelector("#randomWordModal .word-info img");
        heartImg.dataset.word = word.word;

        // 단어장에 있으면 채운 하트, 없으면 빈 하트
        if (userWordbook.includes(word.word)) {
          heartImg.src = '/img/icon/full-heart.png';
          heartImg.alt = '채워진 하트 이미지';
        } else {
          heartImg.src = '/img/icon/empty-heart.png';
          heartImg.alt = '빈 하트 이미지';
        }
      });

    // 모달 표시 관련 로직
    introModal.classList.remove("show");
    wordModal.classList.remove("show");
    wrapper.classList.add("modal-open");
    mypageModal.classList.remove("show");
    todayWordModal.classList.remove("show");
    randomWordModal.classList.add("show");
    wrapper.classList.add("modal-open");
  }

  function closeRandomWordModal() {
    wrapper.classList.remove("modal-open");
    randomWordModal.classList.remove("show");
  }

  function showToast(message) {
    toast.innerText = message;
    toast.classList.add("show");
    setTimeout(() => {
      toast.classList.remove("show");
    }, 2000);
  }

  // 랜덤 단어 가져오기 및 사용자 권한 체크
  document.addEventListener("DOMContentLoaded", function () {
    const containerWidth = floatingWordsContainer.clientWidth;
    const containerHeight = floatingWordsContainer.clientHeight;

    const positions = [
            { xPercent: 5, yPercent: 10 },
            { xPercent: 20, yPercent: 12 },
            { xPercent: 41.7, yPercent: 18.5 },
            { xPercent: 50, yPercent: 5 },
            { xPercent: 59.8, yPercent: 15 },
            { xPercent: 80, yPercent: 5 },
            { xPercent: 69, yPercent: 28.5 },
            { xPercent: 26, yPercent: 29.8 },
            { xPercent: 90, yPercent: 30 },
            { xPercent: 5, yPercent: 50 },
            { xPercent: 95, yPercent: 50 },
            { xPercent: 20.8, yPercent: 55.6 },
            { xPercent: 69.2, yPercent: 79.2 },
            { xPercent: 5, yPercent: 90 },
            { xPercent: 14.8, yPercent: 78.7 },
            { xPercent: 35, yPercent: 83.3 },
            { xPercent: 50, yPercent: 85.7 },
            { xPercent: 57.3, yPercent: 75.7 },
            { xPercent: 80, yPercent: 95 },
            { xPercent: 79.4, yPercent: 53 },
          ];

    wordData.forEach((word, index) => {
      const span = document.createElement("span");
      span.innerText = word.word;
      const pos = positions[index % positions.length];
      const x = (pos.xPercent / 100) * containerWidth;
      const y = (pos.yPercent / 100) * containerHeight;
      span.style.position = "absolute";
      span.style.left = `${x}px`;
      span.style.top = `${y}px`;
      span.onclick = () => openModal(span);
      floatingWordsContainer.appendChild(span);
    });

//    if (userRole === "ADMIN") {
//      const todayWordAddBtn = document.getElementById("todayWordAdd");
//      if (todayWordAddBtn) todayWordAddBtn.style.display = "block";
//    }

    //오늘의 단어 조회
    loadTodayWord();
  });

  // 입력 유효성 검사
  document.querySelector('.search-btn').addEventListener('click', async (e) => {
    const wordInput = document.getElementById("wordInput").value.trim();
    const notKoreanRegex = /[^가-힣]/;

    if (!wordInput) {
      alert("단어를 입력해주세요!");
      return;
    }
    if (notKoreanRegex.test(wordInput)) {
      alert("한글만 입력 가능합니다.");
      return;
    }
    await findSynonyms();
  });

  document.getElementById("wordInput").addEventListener("keydown", function(e) {
    if (e.key === "Enter") {
      e.preventDefault();
      document.querySelector(".search-btn").click();
    }
  });

  async function findSynonyms() {
    const word = document.getElementById("wordInput").value.trim();
    if (!word) {
      alert("단어를 입력해주세요!");
      return;
    }
    const response = await fetch('/synonyms', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ word })
    });

    if (!response.ok) {
      const errorData = await response.json();
      alert(errorData.error || "오류가 발생했습니다.");
      return;
    }

    const data = await response.json();
    const wordInfo = data.wordInfo;
    const synonymList = data.synonyms;

    document.querySelector(".word-title").innerText = wordInfo.word;
    document.querySelector(".word-type").innerText = wordInfo.partSpeech + " · " + wordInfo.meaning;
    document.querySelector(".word-example p").innerText = wordInfo.exampleSentence;
    const heartImg = document.querySelector(".word-info img");
    heartImg.dataset.word = wordInfo.word;

    //본 단어 하트 세팅
    if (userWordbook.includes(wordInfo.word)) {
      heartImg.src = '/img/icon/full-heart.png';
      heartImg.alt = '채워진 하트 이미지';
    } else {
      heartImg.src = '/img/icon/empty-heart.png';
      heartImg.alt = '빈 하트 이미지';
    }

    wordModalContent.querySelectorAll(".search-word-info").forEach(el => el.remove());

    synonymList.forEach(syn => {
      const synonymDiv = document.createElement("div");
      synonymDiv.classList.add("search-word-info");

      //단어장이 있는 단어면 채운 하트, 아니면 빈 하트
      const heartIconSrc = userWordbook.includes(syn.word)
          ? '/img/icon/full-heart.png'
          : '/img/icon/empty-heart.png';

      synonymDiv.innerHTML = `
        <h2 class="search-word-title">${syn.word}</h2>
        <img src="${heartIconSrc}" class="heart-icon" data-word="${syn.word}" />
        <p class="search-word-type">${syn.partSpeech} · ${syn.meaning}</p>
        <div class="search-word-example">
          <span class="example-tag">예시 문장</span>
          <p>${syn.exampleSentence}</p>
        </div>
      `;
      wordModalContent.appendChild(synonymDiv);
    });

    introModal.classList.remove("show");
    todayWordModal.classList.remove("show");
    mypageModal.classList.remove("show");
    wordModal.classList.add("show");
    wrapper.classList.add("modal-open");
  }

  //단어장 저장 및 삭제 처리(하트버튼)
  document.addEventListener('click', async function(e) {
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
        const res = await fetch('/wordbook/remove', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ word })
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
        const res = await fetch('/wordbook/save', {
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

  //오늘의 단어 조회
  async function loadTodayWord() {
    try {
      const response = await fetch('/todayword');
      if (!response.ok) {
        return;
      }

      const data = await response.json();

      document.querySelector("#todayWordModal .todayword-title").innerText = data.word;
      document.querySelector("#todayWordModal .word-type").innerText = `${data.partSpeech} · ${data.meaning}`;
      document.querySelector("#todayWordModal .todayword-example p").innerText = data.exampleSentence;

      const commentBox = document.querySelector("#todayWordModal .comment");
      commentBox.innerHTML = `
        <div class="second-tag">숲지기의 한마디</div>
        <p>${data.comment}</p>
      `;

      // ⭐ 하트 세팅 추가
      const todayHeartImg = document.querySelector("#todayWordModal .todayword-info img");
      todayHeartImg.dataset.word = data.word;

      if (userWordbook.includes(data.word)) {
        todayHeartImg.src = '/img/icon/full-heart.png';
        todayHeartImg.alt = '채워진 하트 이미지';
      } else {
        todayHeartImg.src = '/img/icon/empty-heart.png';
        todayHeartImg.alt = '빈 하트 이미지';
      }

    } catch (error) {
      console.error("오늘의 단어 불러오기 실패", error);
    }
  }

  // 모달 외부 클릭 시 닫기
  document.addEventListener("click", function (e) {
    const modals = [wordModal, introModal, mypageModal, todayWordModal, randomWordModal];
    const isAnyModalOpen = modals.some(modal => modal.classList.contains("show"));
    const isInsideModal = modals.some(modal => modal.contains(e.target));
    const isButton = e.target.closest("button");

    // 랜덤단어 클릭 시 예외 처리
    const isFloatingWord = e.target.closest(".floating-words span");

    if (isAnyModalOpen && !isInsideModal && !isButton && !isFloatingWord) {
      closeModal();
      closeIntroModal();
      closeMypageModal();
      closeTodayWordModal();
      closeRandomWordModal();
    }
  });