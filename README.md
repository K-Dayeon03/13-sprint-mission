# Discodeit

Java와 Spring Boot로 구현한 Discord 유사 서비스입니다.  
유저, 채널, 메시지를 중심으로 ReadStatus, UserStatus, BinaryContent 도메인을 함께 관리합니다.

## 기술 스택

- Java 17
- Spring Boot
- Lombok
- Gradle
- JUnit 5, Mockito

## 실행과 테스트

```bash
./gradlew test
./gradlew bootRun
```

Repository 구현체는 설정으로 선택합니다.

```yaml
discodeit:
  repository:
    type: jcf # jcf | file
    file-directory: .discodeit
```

- `jcf`: 메모리 기반 저장소입니다. 애플리케이션을 재시작하면 데이터가 사라집니다.
- `file`: `.ser` 파일 기반 저장소입니다. `discodeit.repository.file-directory` 아래에 데이터를 저장합니다.

## 프로젝트 구조

```text
src/main/java/com/sprint/mission/discodeit
├── DiscodeitApplication.java
├── dto
│   ├── request
│   └── response
├── entity
│   ├── Entity.java
│   ├── User.java
│   ├── Channel.java
│   ├── Message.java
│   ├── ReadStatus.java
│   ├── UserStatus.java
│   ├── BinaryContent.java
│   └── ChannelType.java
├── repository
│   ├── UserRepository.java
│   ├── ChannelRepository.java
│   ├── MessageRepository.java
│   ├── ReadStatusRepository.java
│   ├── UserStatusRepository.java
│   ├── BinaryContentRepository.java
│   ├── jcf
│   └── file
└── service
    ├── UserService.java
    ├── AuthService.java
    ├── ChannelService.java
    ├── MessageService.java
    ├── ReadStatusService.java
    ├── UserStatusService.java
    ├── BinaryContentService.java
    └── basic
```

## 도메인

### 공통 도메인

`User`, `Channel`, `Message`, `ReadStatus`, `UserStatus`는 `Entity`를 상속합니다.

- `id`
- `createdAt`
- `updatedAt`

시간 타입은 `Instant`로 통일했습니다.

### User

사용자 정보를 표현합니다.

- `username`
- `password`
- `email`
- `profileImageId`

응답 DTO에서는 비밀번호를 제외하고, UserStatus를 통해 온라인 여부를 함께 제공합니다.

### Channel

채널 정보를 표현합니다.

- `type`: `PUBLIC` 또는 `PRIVATE`
- `name`
- `description`
- `authorId`

PUBLIC 채널은 모든 유저가 조회할 수 있고, PRIVATE 채널은 ReadStatus가 있는 참여자만 조회할 수 있습니다.

### Message

채널에 작성된 메시지를 표현합니다.

- `content`
- `channelId`
- `authorId`
- `attachmentIds`

첨부파일은 `BinaryContent`로 저장하고, 메시지는 첨부파일 id 목록을 참조합니다.

### ReadStatus

사용자가 채널별로 마지막으로 메시지를 읽은 시간을 표현합니다.

- `userId`
- `channelId`
- `lastReadAt`

PRIVATE 채널의 참여자 목록을 조회할 때도 활용합니다.

### UserStatus

사용자의 마지막 활동 시간을 표현합니다.

- `userId`
- `lastActiveAt`

마지막 활동 시간이 현재 시간 기준 5분 이내이면 온라인 상태로 판단합니다.

### BinaryContent

이미지, 파일 등 바이너리 데이터를 표현합니다.

- `id`
- `createdAt`
- `userId`
- `messageId`
- `fileName`
- `contentType`
- `bytes`

수정 불가능한 도메인으로 간주하여 `updatedAt`을 두지 않습니다.

## 서비스 기능

### UserService

- 유저 생성
- 프로필 이미지 선택 등록
- username, email 중복 방지
- UserStatus 함께 생성
- 유저 단건 조회
- 유저 전체 조회
- 유저 수정
- 프로필 이미지 선택 교체
- 유저 삭제 시 관련 도메인 삭제

유저 삭제 시 정리 대상:

- 프로필 이미지 BinaryContent
- UserStatus
- 작성한 Message
- 작성한 Channel
- Channel에 연결된 Message, ReadStatus

### AuthService

- `LoginRequest`로 username, password를 전달받습니다.
- 일치하는 유저가 있으면 UserResponse를 반환합니다.
- 일치하는 유저가 없으면 예외를 발생시킵니다.

### ChannelService

- PUBLIC 채널 생성
- PRIVATE 채널 생성
- PRIVATE 채널 생성 시 참여자별 ReadStatus 생성
- 채널 단건 조회
- 특정 유저가 볼 수 있는 채널 목록 조회
- 채널 수정
- PRIVATE 채널 수정 방지
- 채널 삭제 시 Message, ReadStatus 함께 삭제

ChannelResponse에는 다음 정보가 포함됩니다.

- 채널 기본 정보
- PRIVATE 채널 참여자 id 목록
- 가장 최근 메시지 시간

메시지가 없는 채널의 최근 메시지 시간은 `null`이며, 콘솔 출력에서는 `없음`으로 표시합니다.

### MessageService

- 메시지 생성
- 첨부파일 여러 개 선택 등록
- 특정 채널의 메시지 목록 조회
- 메시지 수정
- 메시지 삭제 시 첨부파일 BinaryContent 함께 삭제

### ReadStatusService

- ReadStatus 생성
- User, Channel 존재 여부 검증
- 같은 User와 Channel 조합 중복 방지
- id 조회
- userId 조건 조회
- 수정
- 삭제

### UserStatusService

- UserStatus 생성
- User 존재 여부 검증
- 같은 User의 UserStatus 중복 방지
- id 조회
- 전체 조회
- id 기준 수정
- userId 기준 수정
- 삭제

### BinaryContentService

- BinaryContent 생성
- id 조회
- id 목록 조회
- 삭제

## Repository 구현체

모든 Repository 인터페이스는 JCF 구현체와 File 구현체를 모두 가집니다.

| Interface | JCF | File |
|---|---|---|
| `UserRepository` | `JCFUserRepository` | `FileUserRepository` |
| `ChannelRepository` | `JCFChannelRepository` | `FileChannelRepository` |
| `MessageRepository` | `JCFMessageRepository` | `FileMessageRepository` |
| `ReadStatusRepository` | `JCFReadStatusRepository` | `FileReadStatusRepository` |
| `UserStatusRepository` | `JCFUserStatusRepository` | `FileUserStatusRepository` |
| `BinaryContentRepository` | `JCFBinaryContentRepository` | `FileBinaryContentRepository` |

`@ConditionalOnProperty`를 사용하여 `discodeit.repository.type` 값에 따라 하나의 구현체만 Bean으로 등록합니다.

## Spring 구조

`Basic*Service`는 `@Service`와 `@RequiredArgsConstructor`를 사용합니다.  
Service는 다른 Service를 주입하지 않고 필요한 Repository를 직접 주입합니다.

```java
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
}
```

Repository 구현체는 `@Repository`로 Bean 등록됩니다.

## DiscodeitApplication 실행 흐름

`DiscodeitApplication`은 Spring Context에서 Service Bean을 조회한 뒤, 콘솔에서 다음 시나리오를 실행합니다.

```text
애플리케이션 시작
사용자 접속
  유저 생성
  유저 조회
  유저 수정

서버 채널 준비
  PUBLIC 채널 생성
  PRIVATE 채널 생성
  PUBLIC 채널 수정

채널 목록 확인
  현재 유저가 볼 수 있는 채널 목록

채널 입장
  PUBLIC 채널 입장

채널에서 대화
  메시지 생성
  채널 메시지 목록 조회
  메시지 작성 후 채널 상태 확인

메시지 관리
  메시지 수정
  채널 메시지 목록 재조회
  메시지 삭제
  메시지 삭제 후 채널 상태 확인

정리
  유저 삭제
```

## JavaApplication과 DiscodeitApplication의 차이

기존 JavaApplication 방식에서는 개발자가 직접 Repository와 Service 구현체를 생성하고 의존성을 연결합니다.

```java
UserRepository userRepository = new JCFUserRepository();
UserService userService = new BasicUserService(userRepository);
```

DiscodeitApplication에서는 Spring IoC Container가 객체를 생성하고 의존성을 주입합니다.

```java
ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

UserService userService = context.getBean(UserService.class);
ChannelService channelService = context.getBean(ChannelService.class);
MessageService messageService = context.getBean(MessageService.class);
```

### IoC Container

Spring의 `ApplicationContext`가 IoC Container 역할을 합니다.  
객체 생성과 생명주기 관리를 개발자 대신 담당합니다.

### Dependency Injection

Service가 필요한 Repository를 직접 생성하지 않고, 생성자를 통해 외부에서 주입받습니다.  
이 덕분에 Service는 비즈니스 로직에 집중할 수 있습니다.

### Bean

Spring IoC Container가 생성하고 관리하는 객체를 Bean이라고 합니다.  
현재 프로젝트에서는 `@Service`, `@Repository`가 붙은 구현체들이 Bean으로 등록됩니다.

설정값만 바꾸면 Java 코드를 수정하지 않고 JCF 저장소와 File 저장소를 교체할 수 있습니다.
