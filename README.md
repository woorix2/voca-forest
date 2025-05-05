# 🌳 어휘의 숲 (voca-forest)

**단어를 검색하고, 유사어를 추천받으며, 나만의 단어장을 만들어가는 웹사이트**

---

## 📌 프로젝트 소개

- **프로젝트명**: 어휘의 숲 (voca-forest)
- **목표**:
    - 단어 검색 시 뜻과 유사어를 함께 제공
    - "나만의 단어장" 기능으로 즐겨찾기 관리
    - "오늘의 단어", "아무 단어 뽑기" 기능으로 하루 한 단어 소개
    - 이메일로 아이디 찾기 / 비밀번호 재설정 지원

---

## 🛠️ 개발환경

| 항목 | 내용 |
|:--|:--|
| Language | Java 17 |
| Framework | Spring Boot 3, Spring Security |
| ORM | JPA (Hibernate) |
| Database | MariaDB |
| Frontend | HTML5, CSS3, JavaScript, Thymeleaf |
| Build Tool | Gradle |
| Version Control | Git, GitHub |
| IDE | IntelliJ IDEA (Community Edition), DBeaver |
| API 연동 | OpenAI API (유사어 추천), 표준국어대사전 API (검증) |
| 기타 | Redis (검색 캐시), BCrypt (비밀번호 암호화) |

---

## ✨ 주요 기능

### 🔒 회원 기능
- 이메일 인증 기반 회원가입
- 로그인 / 로그아웃 (Spring Security 기반)
- 이메일/생년월일로 아이디(이메일) 찾기
- 비밀번호 재설정

### 📚 단어 기능
- 단어 검색 및 유사어 추천(Open AI API 기반)
- 표준국어대사전 API 기반 단어 검증
- "나만의 단어장" 저장/삭제 기능
- "최근 검색어" 저장/삭제 기능
- "아무 단어 뽑기"로 랜덤 단어 조회 기능

### 📝 오늘의 단어
- 관리자(ROLE_ADMIN)만 등록 가능
- 매일 1개 단어를 추천 및 관리
- 숲지기 한마디(comment) 추가 가능