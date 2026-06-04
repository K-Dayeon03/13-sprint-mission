# 🎮 Discodeit — Discord 클론 프로젝트

> Java로 구현한 Discord 유사 서비스 (유저 / 채널 / 메시지 CRUD)

---

## 📁 프로젝트 구조

```
com.sprint.mission.discodeit
├── entity                        # 도메인 모델
│   ├── Entity.java               # 공통 추상 클래스 (id, createdAt, updatedAt)
│   ├── User.java                 # 유저
│   ├── Channel.java              # 채널
│   ├── Message.java              # 메시지
│   └── ChannelType.java          # 채널 타입 enum (PUBLIC / PRIVATE)
│
├── service                       # 서비스 레이어
│   ├── UserService.java          # 유저 서비스 인터페이스
│   ├── ChannelService.java       # 채널 서비스 인터페이스
│   ├── MessageService.java       # 메시지 서비스 인터페이스
│   ├── jcf                       # JCF 기반 구현체 (메모리 저장)
│   │   ├── JCFUserService.java
│   │   ├── JCFChannelService.java
│   │   └── JCFMessageService.java
│   ├── file                      # File IO 기반 구현체 (파일 저장)
│   │   ├── FileUserService.java
│   │   ├── FileChannelService.java
│   │   └── FileMessageService.java
│   └── basic                     # Basic 구현체 (비즈니스 로직만)
│       ├── BasicUserService.java
│       ├── BasicChannelService.java
│       └── BasicMessageService.java
│
├── repository                    # 레포지토리 레이어
│   ├── UserRepository.java       # 유저 레포지토리 인터페이스
│   ├── ChannelRepository.java    # 채널 레포지토리 인터페이스
│   ├── MessageRepository.java    # 메시지 레포지토리 인터페이스
│   ├── jcf                       # JCF 기반 구현체 (메모리 저장)
│   │   ├── JCFUserRepository.java
│   │   ├── JCFChannelRepository.java
│   │   └── JCFMessageRepository.java
│   └── file                      # File IO 기반 구현체 (파일 저장)
│       ├── FileUserRepository.java
│       ├── FileChannelRepository.java
│       └── FileMessageRepository.java
│
└── JavaApplication.java          # 메인 클래스 (테스트)
```

---

## ⚙️ 기능 목록

### 👤 User (유저)
| 기능 | 메서드 | 설명 |
|---|---|---|
| 등록 | `create(username, password, email)` | 유저 생성 |
| 단건 조회 | `findById(id)` | UUID로 유저 조회 |
| 전체 조회 | `findAll()` | 전체 유저 목록 조회 |
| 수정 | `update(id, currentPassword, ...)` | 비밀번호 검증 후 수정 |
| 삭제 | `delete(id)` | 유저 삭제 + 연관 채널/메시지 삭제 |

### 📢 Channel (채널)
| 기능 | 메서드 | 설명 |
|---|---|---|
| 등록 | `create(type, name, description, authorId)` | 채널 생성 |
| 단건 조회 | `findById(id)` | UUID로 채널 조회 |
| 전체 조회 | `findAll()` | 전체 채널 목록 조회 |
| 수정 | `update(id, newType, newName, newDescription)` | 채널 정보 수정 |
| 삭제 | `delete(id)` | 채널 삭제 + 연관 메시지 삭제 |

### 💬 Message (메시지)
| 기능 | 메서드 | 설명 |
|---|---|---|
| 등록 | `create(content, channelId, authorId)` | 메시지 생성 |
| 단건 조회 | `findById(id)` | UUID로 메시지 조회 |
| 전체 조회 | `findByAll()` | 전체 메시지 목록 조회 |
| 채널별 조회 | `findByChannelId(channelId)` | 채널별 메시지 조회 |
| 수정 | `update(id, newContent)` | 메시지 내용 수정 |
| 삭제 | `delete(id)` | 단건 메시지 삭제 |

---

## 🏗️ 레이어 아키텍처

```
JavaApplication
      ↓ 호출
Service (비즈니스 로직)
  - 유효성 검사
  - 비밀번호 검증
  - 연관 삭제 규칙
      ↓ 위임
Repository (저장 로직)
  - 데이터 저장 / 조회 / 삭제
      ↓
저장소
  JCFRepository  → 메모리 (HashMap)
  FileRepository → 파일 (data/*.ser)
```

---

## 💡 비즈니스 로직 vs 저장 로직

### 개념 차이
| | 비즈니스 로직 | 저장 로직 |
|---|---|---|
| 역할 | "무엇을 검증하고 처리할지" 결정 | "데이터를 어디에 넣고 빼는지" 처리 |
| 위치 | Service 레이어 | Repository 레이어 |
| 예시 | null 체크, 비밀번호 검증, 연관 삭제 규칙 | `data.put`, `data.get`, `data.remove` |

### 코드로 보는 차이
```java
// JCFUserService — 비즈니스 로직 + 저장 로직이 섞여있음 ❌
public User create(String username, String password, String email) {

    // 비즈니스 로직 — 유효성 검사
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("이름을 입력해주세요.");
    }
    User user = new User(username, password, email);

    // 저장 로직 — 데이터 저장 (섞여있음)
    data.put(user.getId(), user);
    return user;
}

// BasicUserService — 비즈니스 로직만 ✅
public User create(String username, String password, String email) {

    // 비즈니스 로직만
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("이름을 입력해주세요.");
    }
    User user = new User(username, password, email);

    // 저장은 Repository에 위임
    return userRepository.save(user);
}

// JCFUserRepository — 저장 로직만 ✅
public User save(User user) {
    data.put(user.getId(), user);
    return user;
}
```

---

## 🔀 JCFService에서 저장 로직 분리 과정

### Before — JCFUserService (로직 혼재)
```java
public class JCFUserService implements UserService {

    // 저장소를 Service가 직접 들고 있음
    private final Map<UUID, User> data = new HashMap<>();

    public User create(String username, String password, String email) {
        // 비즈니스 로직
        if (username == null) throw new IllegalArgumentException("...");
        User user = new User(username, password, email);

        // 저장 로직 — Service 안에 직접 구현
        data.put(user.getId(), user);
        return user;
    }

    public void delete(UUID id) {
        // 비즈니스 로직 — 연관 삭제 규칙
        messageService.deleteByAuthorId(id);
        channelService.deleteByAuthorId(id);

        // 저장 로직 — Service 안에 직접 구현
        data.remove(id);
    }
}
```

### After — BasicUserService + JCFUserRepository (로직 분리)
```java
// BasicUserService — 비즈니스 로직만
public class BasicUserService implements UserService {
    private final UserRepository userRepository;  // Repository 주입

    public User create(String username, String password, String email) {
        // 비즈니스 로직만
        if (username == null) throw new IllegalArgumentException("...");
        User user = new User(username, password, email);

        // 저장은 Repository에 위임
        return userRepository.save(user);
    }

    public void delete(UUID id) {
        // 비즈니스 로직 — 연관 삭제 규칙
        messageRepository.deleteByAuthorId(id);
        channelRepository.deleteByAuthorId(id);

        // 저장은 Repository에 위임
        userRepository.deleteById(id);
    }
}

// JCFUserRepository — 저장 로직만
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    public User save(User user) {
        data.put(user.getId(), user);  // 저장만
        return user;
    }

    public void deleteById(UUID id) {
        data.remove(id);  // 삭제만
    }
}
```

### 분리 후 장점
| | 분리 전 (JCFService) | 분리 후 (BasicService) |
|---|---|---|
| 단일 책임 | ❌ 두 가지 책임 혼재 | ✅ 각자 하나의 책임 |
| 저장소 교체 | ❌ Service 통째로 수정 | ✅ Repository 3줄만 교체 |
| 유지보수 | ❌ 어려움 | ✅ 쉬움 |
| 테스트 | ❌ 어려움 | ✅ 독립적으로 테스트 가능 |

---

## 🔄 JCF vs File IO 구현체 차이점

### 저장 방식
| | JCF 구현체 | File IO 구현체 |
|---|---|---|
| 저장 위치 | 메모리 (HashMap) | 파일 (data/*.ser) |
| 데이터 유지 | ❌ 재시작 시 사라짐 | ✅ 재시작 후에도 유지 |
| 속도 | ✅ 빠름 | ❌ 상대적으로 느림 |
| 직렬화 | ❌ 불필요 | ✅ Serializable 필수 |

### 코드 차이
```java
// JCFUserRepository — 메모리에 저장
private final Map<UUID, User> data = new HashMap<>();

public User save(User user) {
    data.put(user.getId(), user);  // 메모리에 저장
    return user;
}

// FileUserRepository — 파일에 저장
private static final Path FILE_PATH = Paths.get("data/users.ser");

public User save(User user) {
    Map<UUID, User> data = loadData();  // 파일에서 불러오기
    data.put(user.getId(), user);
    saveData(data);                     // 파일에 저장
    return user;
}
```

### BasicService + Repository 교체
```java
// JCF → File 교체 시 이 3줄만 변경!
MessageRepository messageRepository = new JCFMessageRepository();  // ← 교체
ChannelRepository channelRepository = new JCFChannelRepository();  // ← 교체
UserRepository userRepository = new JCFUserRepository();           // ← 교체
```
---

## 📌 연관 삭제 규칙

```
유저 삭제 시
 ├── 작성한 메시지 삭제
 └── 만든 채널 삭제
       └── 채널의 메시지 삭제

채널 삭제 시
 └── 채널의 메시지 삭제
```