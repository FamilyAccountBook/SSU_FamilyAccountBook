# SSU_FamilyAccountBook

# 우리 가족 가계부 - Technical PRD

> **자녀 금융교육을 위한 가족 가계부 서비스**  
> Android Java 개발 명세서

---

## 📋 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [아키텍처 설계](#2-아키텍처-설계)
3. [인증 플로우](#3-인증-플로우)
4. [데이터 모델 설계](#4-데이터-모델-설계)
5. [핵심 기능 구현 명세](#5-핵심-기능-구현-명세)
6. [화면 전환 및 네비게이션](#6-화면-전환-및-네비게이션)
7. [Firebase 연동 전략](#7-firebase-연동-전략)
8. [OCR 구현 전략](#8-ocr-구현-전략)
9. [역할 기반 접근 제어](#9-역할-기반-접근-제어)
10. [예외 처리 및 검증](#10-예외-처리-및-검증)
11. [개발 우선순위](#11-개발-우선순위)

---

## 1. 프로젝트 개요

### 1.1 기술 스택

| 구분 | 기술 |
|------|------|
| 언어 | Java 11+ |
| 플랫폼 | Android (minSdk 24, targetSdk 34) |
| 아키텍처 | Single Activity + Multi-Fragment |
| UI 바인딩 | ViewBinding |
| 백엔드 | Firebase (Authentication, Firestore, Storage, ML Kit) |
| 차트 라이브러리 | MPAndroidChart 3.1.0 |
| 이미지 로딩 | Glide 4.15.0 |
| 날짜 선택 | Material Date Picker |

### 1.2 프로젝트 구조

```
app/src/main/java/com/example/ui_familybook;
├── MainActivity.java                    # 단일 Activity
├── models/                              # 데이터 모델
│   ├── User.java
│   ├── Transaction.java
│   ├── Sticker.java
│   └── Family.java
├── fragments/                           # 화면 Fragment
│   ├── auth/
│   │   ├── LoginFragment.java
│   │   └── RegisterSelectFragment.java
│   │   └── RegisterFormFragment.java
│   ├── parent/
│   │   ├── ParentHomeFragment.java
│   │   ├── ParentStatisticsFragment.java
│   │   └── StickerGiveFragment.java
│   ├── child/
│   │   ├── ChildHomeFragment.java
│   │   ├── ChildStatisticsFragment.java
│   │   └── StickerBoardFragment.java
│   └── common/
│       ├── AddTransactionFragment.java
│       └── SettingsFragment.java
├── adapters/                            # RecyclerView Adapter
│   ├── TransactionAdapter.java
│   ├── StickerAdapter.java
│   └── CategoryAdapter.java
├── utils/                               # 유틸리티 클래스
│   ├── FirebaseHelper.java
│   ├── OCRProcessor.java
│   ├── ChartHelper.java
│   └── ValidationUtils.java
└── interfaces/                          # 콜백 인터페이스
    ├── OnTransactionClickListener.java
    └── OnStickerSelectListener.java
```

---

## 2. 아키텍처 설계

### 2.1 Single Activity 아키텍처

**설계 원칙**
- MainActivity 하나만 사용하고 모든 화면은 Fragment로 구성
- BottomNavigationView를 통한 주요 화면 전환
- FragmentManager를 통한 백스택 관리

**MainActivity 역할**
- Firebase 인증 상태 확인
- 사용자 역할(부모/자녀) 로드 및 저장
- BottomNavigation 설정 및 Fragment 전환 관리
- 전역 상태 관리 (currentUserRole, currentUid 등)

**Fragment 생명주기 관리**
- onCreate: Firebase 인스턴스 초기화, 사용자 UID 로드
- onCreateView: ViewBinding 설정, UI 초기화
- onViewCreated: 데이터 로드, 리스너 설정
- onDestroyView: ViewBinding null 처리, 메모리 누수 방지

### 2.2 레이어 구조

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│    (Fragments, Adapters, UI)        │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│          Business Logic             │
│   (Utils, Helpers, Validators)      │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│           Data Layer                │
│  (Firebase, Models, Repositories)   │
└─────────────────────────────────────┘
```

---

## 3. 인증 플로우

### 3.1 앱 시작 플로우

```
[APP START]
     │
     ▼
MainActivity (Firebase Auth Check)
     │
     ├── (Logged Out) ──────────────────────────┐
     │                                          │
     │ (Logged In)                              ▼
     ▼                                    LoginFragment
Load User Role (Firestore)                      │
     │   ▲                                      │
     │   │                                      ▼
     │   │                            RegisterSelectFragment
     │   │                            (Select Parent/Child)
     │   │                                      │
     │   │                                      ▼
     │   │                            RegisterFormFragment
     │   └─ (Auto Login) ──────────── (Create Auth & Save DB)
     │
     ▼
Check Role
     │
     ├── [PARENT] ──→ ParentHomeFragment
     │
     └── [CHILD]  ──→ ChildHomeFragment
     
     
```

### 3.2 로그인 처리 절차

**LoginFragment 처리 흐름**
1. 이메일/비밀번호 입력 받기
2. ValidationUtils로 입력값 검증
3. Firebase Authentication 호출
4. 성공 시 MainActivity의 리스너가 감지하여 역할(Role) 확인 후 홈 화면으로 이동
5. 실패 시 에러 메시지 표시

**검증 항목**
- 이메일 형식 검증 (정규식)
- 비밀번호 최소 6자 이상
- 빈 값 체크

### 3.3 회원가입 처리 절차

**RegisterSelect 처리 흐름**
1. 역할 선택 (부모/자녀) - ChipGroup 또는 RadioButton 사용
2. '다음' 버튼 클릭 시 Bundle에 선택된 역할(Role) 정보 담기
3. RegisterFormFragment로 트랜잭션 이동

**RegisterForm 처리 흐름**
1. Bundle에서 이전 화면에서 넘겨준 역할(Role) 데이터 수신
2. 이름, 이메일, 비밀번호 입력
3. 입력값 검증
4. Firebase Auth에 계정 생성
5. 성공시 Firestore에 사용자 문서 생성
    - users/{uid} 경로에 저장
    - 역할, 이름, 이메일, 전달받은 역할 초기 데이터 저장
6. 저장 완료 시 MainActivity가 이를 감지하여 즉시 홈 화면으로 이동 (자동 로그인

**Firestore 사용자 문서 초기값**
```
{
  name: "입력값",
  email: "입력값",
  role: "parent" or "child",
  profileImage: "",
  familyId: "",
  savingGoal: 0,
  spendingLimit: 0,
  stickerGoal: role === "child" ? 30 : 0,
  createdAt: serverTimestamp
}
```

---

## 4. 데이터 모델 설계

### 4.1 User 모델

**필드 구성**
```java
- uid: String (Firebase Auth UID)
- name: String (사용자 이름)
- email: String (이메일)
- role: String ("parent" 또는 "child")
- profileImage: String (Storage URL)
- familyId: String (가족 그룹 ID)
- savingGoal: long (저축 목표 금액)
- spendingLimit: long (지출 한도)
- stickerGoal: int (스티커 목표 개수)
- createdAt: Timestamp
```

**주요 메서드**
- `isParent()`: 부모 역할 여부 확인
- `isChild()`: 자녀 역할 여부 확인
- getter/setter 메서드들

### 4.2 Transaction 모델

**필드 구성**
```java
- id: String (Firestore 문서 ID)
- uid: String (작성자 UID)
- type: String ("income" 또는 "expense")
- category: String (카테고리명)
- amount: long (금액)
- date: Timestamp (거래 날짜)
- memo: String (메모)
- imageUrl: String (영수증 이미지 URL)
- createdAt: Timestamp
```

**주요 메서드**
- `isIncome()`: 수입 여부 확인
- `isExpense()`: 지출 여부 확인
- `getFormattedAmount()`: "+10,000원" 형식으로 반환
- `getFormattedDate()`: "2025.01.21" 형식으로 반환

### 4.3 Sticker 모델

**필드 구성**
```java
- id: String (Firestore 문서 ID)
- parentUid: String (부모 UID)
- childUid: String (자녀 UID)
- stickerType: String (스티커 종류 - 이모지)
- message: String (응원 메시지)
- timestamp: Timestamp
```

### 4.4 Family 모델

**필드 구성**
```java
- familyId: String (UUID)
- parentUid: String (부모 UID)
- childUidList: List (자녀 UID 목록)
- createdAt: Timestamp
```

**주요 메서드**
- `addChild(String childUid)`: 자녀 추가

---

## 5. 핵심 기능 구현 명세

### 5.1 홈 화면 (공통 기능)

**표시 정보**
- 이번 달 잔액 (수입 - 지출)
- 이번 달 총 수입
- 이번 달 총 지출
- 저축 목표 달성률 (ProgressBar)
- 지출 한도 현황 (ProgressBar)
- 거래 내역 목록 (RecyclerView)

**데이터 로드 로직**
1. 현재 월의 시작일/종료일 계산 (Calendar 사용)
2. Firestore 쿼리:
   ```
   transactions 컬렉션
   .whereEqualTo("uid", targetUid)
   .whereGreaterThanOrEqualTo("date", startDate)
   .whereLessThan("date", endDate)
   .orderBy("date", DESCENDING)
   ```
3. 결과를 순회하며 수입/지출 합계 계산
4. UI 업데이트 (TextView, ProgressBar, RecyclerView)

**색상 코딩**
- 잔액 양수: 초록색 (#4CAF50)
- 잔액 음수: 빨간색 (#F44336)
- 수입: 초록색 + "+" 접두사
- 지출: 빨간색 + "-" 접두사

### 5.2 홈 화면 (부모 전용 기능)

**추가 기능**
- TabLayout을 통한 부모/자녀 데이터 전환
- 자녀별 탭 동적 생성
- 선택된 탭에 따라 데이터 로드

**구현 절차**
1. 사용자 문서에서 familyId 로드
2. families/{familyId} 문서에서 childUidList 로드
3. 각 자녀 UID로 사용자 이름 조회
4. TabLayout에 "부모" 탭 + 자녀 이름 탭 추가
5. 탭 선택 이벤트 리스너 설정
6. 선택된 UID로 거래 내역 재로드

**탭 전환 시 처리**
```
탭 선택
   ↓
selectedChildUid 변경
   ↓
loadCurrentMonthData(selectedUid)
   ↓
UI 업데이트
```

### 5.3 홈 화면 (자녀 전용 기능)

**제한 사항**
- 본인 데이터만 조회 가능
- 부모 데이터 접근 불가
- 탭 전환 없음

**추가 UI 요소**
- 우상단에 스티커판 버튼 표시
- 클릭 시 StickerBoardFragment로 이동

### 5.4 내역 추가 화면

**입력 항목**
- 수입/지출 선택 (ChipGroup)
- 금액 (EditText, 숫자 입력)
- 카테고리 (ChipGroup, 동적 로드)
- 날짜 (Material Date Picker)
- 메모 (EditText)
- 영수증 이미지 (선택사항)

**카테고리 관리**
1. 사용자별 커스텀 카테고리 지원
2. Firestore 구조:
   ```
   users/{uid}/categories/{autoId}
   {
     name: "식비",
     type: "expense",
     icon: "restaurant"
   }
   ```
3. 수입/지출 타입 변경 시 해당 카테고리만 로드
4. ChipGroup에 동적으로 Chip 추가

**영수증 처리 플로우**
```
영수증 추가 버튼 클릭
   ↓
┌──────────┬──────────┐
│  갤러리   │  카메라   │
└──────────┴──────────┘
      ↓          ↓
   이미지 선택  사진 촬영
      ↓          ↓
   ────────────────
          ↓
   OCRProcessor 호출
          ↓
   텍스트 추출 및 파싱
          ↓
   금액/메모 자동 입력
```

**저장 절차**
1. 입력값 검증 (금액, 카테고리 필수)
2. 영수증 이미지 있을 경우:
    - Firebase Storage에 업로드
    - 경로: `receipts/{uid}/{timestamp}.jpg`
    - 다운로드 URL 획득
3. Transaction 객체 생성
4. Firestore transactions 컬렉션에 추가
5. 성공 시 이전 화면으로 돌아가기

### 5.5 통계 화면

**공통 기능**
- 월 선택 기능 (기본값: 현재 월)
- 선택된 월의 거래 내역 로드
- 3가지 차트 표시

**차트 종류**

**1) 수입/지출 비교 Bar Chart**
- X축: 수입, 지출
- Y축: 금액
- 색상: 수입(초록), 지출(빨강)
- 상단에 총액 표시 카드

**2) 카테고리별 Pie Chart**
- 지출 카테고리만 표시
- 각 카테고리별 금액 비율
- 레이블: 카테고리명 + 금액
- 색상: Material 컬러 팔레트

**3) 일별 추이 Line Chart**
- X축: 날짜 (1일~31일)
- Y축: 금액
- 2개 라인: 수입(초록), 지출(빨강)

**부모 전용 추가 기능**
- TabLayout으로 "나의 통계" / "자녀 통계" 전환
- 자녀 통계 탭 선택 시 자녀 목록 표시
- 선택된 자녀의 통계 표시

**데이터 처리 로직**
1. 선택된 월의 모든 거래 내역 로드
2. 메모리에서 집계 처리:
    - 총 수입/지출 계산
    - 카테고리별 금액 Map 생성
    - 일별 금액 Map 생성
3. MPAndroidChart 데이터셋 생성
4. 차트 렌더링

### 5.6 설정 화면

**공통 기능**
- 프로필 정보 표시 (이름, 이메일, 프로필 사진)
- 프로필 수정 (이름, 프로필 사진)
- 카테고리 관리 (CRUD)
- 로그아웃

**부모 전용 기능**

**1) 가족 연결**
- 자녀 이메일로 검색
- 연결 요청 (Family 문서 생성/업데이트)
- 처리 로직:
  ```
  1. 이메일로 자녀 계정 검색 (role = "child")
  2. 부모의 familyId 확인
  3. familyId 없으면 새로 생성, 있으면 기존 Family에 추가
  4. Family 문서에 childUid 추가
  5. 부모/자녀 문서의 familyId 업데이트
  ```

**2) 자녀 스티커 목표 설정**
- 자녀 선택 (Spinner)
- 목표 개수 입력
- 목표 달성 시 메시지 입력
- Firestore 업데이트:
  ```
  users/{childUid}
  {
    stickerGoal: 입력값,
    stickerGoalMessage: "입력값"
  }
  ```

**자녀 전용 기능**

**1) 저축 목표 설정**
- 목표 금액 입력
- users/{uid}.savingGoal 업데이트

**2) 지출 한도 설정**
- 한도 금액 입력
- users/{uid}.spendingLimit 업데이트

### 5.7 스티커 기능 (부모)

**StickerGiveFragment 기능**

**화면 구성**
- 상단: 자녀 선택 카드 (가로 스크롤)
- 중단: 선택된 자녀의 이번 달 요약 정보
    - 수입/지출 금액
    - 스티커 진행률 (현재/목표)
- 하단: 스티커 선택 + 메시지 입력

**자녀 선택 로직**
1. familyId로 자녀 목록 로드
2. 각 자녀의 프로필 카드 생성
3. 카드 클릭 시 selectedChildUid 설정
4. 선택된 카드 하이라이트 표시

**스티커 종류**
- 👍 칭찬
- ❤️ 사랑
- 💪 응원
- 🎉 축하
- ⭐ 최고

**스티커 부여 절차**
1. 자녀 선택 확인
2. 스티커 종류 선택 확인
3. 응원 메시지 입력 확인
4. Sticker 객체 생성
5. Firestore stickers 컬렉션에 추가
6. 성공 시 입력 초기화 및 진행률 업데이트

### 5.8 스티커 기능 (자녀)

**StickerBoardFragment 기능**

**화면 구성**
- 상단: 스티커 목표 및 진행률
    - "목표: 30개"
    - ProgressBar (현재/목표)
    - 부모가 설정한 목표 메시지 표시
- 중단: 받은 스티커 그리드 (5열)
- 각 스티커 클릭 시 상세 정보 표시
    - 스티커 종류 (이모지)
    - 받은 날짜
    - 부모의 응원 메시지

**데이터 로드**
1. users/{uid}에서 stickerGoal, stickerGoalMessage 로드
2. stickers 컬렉션에서 childUid가 본인인 문서들 조회
3. 최신순 정렬
4. RecyclerView에 GridLayoutManager(5열) 적용

**목표 달성 시 처리**
- 스티커 개수가 목표에 도달하면 축하 Dialog 표시
- "목표를 달성했어요! 🎉" 메시지
- 부모가 설정한 메시지 함께 표시

---

## 6. 화면 전환 및 네비게이션

### 6.1 BottomNavigation 구조

**메뉴 항목**
```
┌──────┬──────┬──────┬──────┐
│  홈  │ 추가 │ 통계 │ 설정 │
└──────┴──────┴──────┴──────┘
```

**네비게이션 처리**
```java
MainActivity에서 처리:
- nav_home 클릭: 역할에 따라 ParentHome/ChildHome
- nav_add 클릭: AddTransactionFragment (공통)
- nav_statistics 클릭: 역할에 따라 ParentStatistics/ChildStatistics
- nav_settings 클릭: SettingsFragment (공통, 내부에서 역할별 UI 분기)
```

### 6.2 Fragment 전환 패턴

**기본 전환 (replace)**
```java
getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .commit();
```

**백스택 추가 전환 (addToBackStack)**
```java
getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .addToBackStack(null)
    .commit();
```

**전환 애니메이션**
```java
.setCustomAnimations(
    R.anim.slide_in_right,
    R.anim.slide_out_left,
    R.anim.slide_in_left,
    R.anim.slide_out_right
)
```

### 6.3 데이터 전달 방법

**Bundle 사용**
```java
// 전송
Bundle bundle = new Bundle();
bundle.putString("childUid", selectedChildUid);
fragment.setArguments(bundle);

// 수신
String childUid = getArguments().getString("childUid");
```

**SharedViewModel 사용 (선택사항)**
- Activity 범위의 ViewModel 생성
- Fragment 간 데이터 공유
- LiveData로 반응형 업데이트

---

## 7. Firebase 연동 전략

### 7.1 Firestore 데이터 구조

```
firestore
├── users/{uid}
│   ├── name, email, role, profileImage
│   ├── familyId, savingGoal, spendingLimit, stickerGoal
│   └── categories/{categoryId}
│       └── name, type, icon
│
├── transactions/{transactionId}
│   └── uid, type, category, amount, date, memo, imageUrl
│
├── stickers/{stickerId}
│   └── parentUid, childUid, stickerType, message, timestamp
│
└── families/{familyId}
    └── parentUid, childUidList[]
```

### 7.2 Firestore 쿼리 패턴

**단일 문서 조회**
```java
db.collection("users")
  .document(uid)
  .get()
  .addOnSuccessListener(doc -> {
      User user = doc.toObject(User.class);
  });
```

**조건 쿼리**
```java
db.collection("transactions")
  .whereEqualTo("uid", currentUid)
  .whereEqualTo("type", "expense")
  .orderBy("date", Query.Direction.DESCENDING)
  .get()
  .addOnSuccessListener(querySnapshot -> {
      // 처리
  });
```

**복합 쿼리 (인덱스 필요)**
```java
// Firestore 콘솔에서 복합 인덱스 생성 필요
db.collection("transactions")
  .whereEqualTo("uid", uid)
  .whereGreaterThanOrEqualTo("date", startDate)
  .whereLessThan("date", endDate)
  .orderBy("date", Query.Direction.DESCENDING)
```

### 7.3 실시간 리스너

**단일 문서 리스너**
```java
db.collection("users").document(uid)
  .addSnapshotListener((snapshot, error) -> {
      if (error != null) return;
      if (snapshot != null && snapshot.exists()) {
          User user = snapshot.toObject(User.class);
          updateUI(user);
      }
  });
```

**컬렉션 리스너**
```java
db.collection("transactions")
  .whereEqualTo("uid", currentUid)
  .addSnapshotListener((querySnapshot, error) -> {
      if (error != null) return;
      // 실시간 업데이트 처리
  });
```

**주의사항**
- Fragment onDestroyView에서 리스너 해제 필요
- 불필요한 읽기 방지를 위해 일회성 조회는 get() 사용

### 7.4 Firebase Storage 전략

**업로드 경로 규칙**
```
receipts/{uid}/{timestamp}.jpg
profiles/{uid}/profile.jpg
```

**업로드 프로세스**
```java
1. Uri를 InputStream으로 변환
2. 이미지 압축 (Bitmap 사용, 최대 1024x1024)
3. StorageReference.putFile() 호출
4. 성공 시 getDownloadUrl() 호출
5. URL을 Firestore 문서에 저장
```

**다운로드 및 표시**
```java
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView);
```

### 7.5 Security Rules

**Firestore Rules 설계**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // 사용자는 본인 문서만 읽기/쓰기 가능
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      
      // 카테고리는 본인 것만 관리
      match /categories/{categoryId} {
        allow read, write: if request.auth.uid == userId;
      }
    }
    
    // 거래내역은 본인 것만 생성/수정 가능
    match /transactions/{transactionId} {
      allow create: if request.auth.uid == request.resource.data.uid;
      allow read, update, delete: if request.auth.uid == resource.data.uid;
    }
    
    // 스티커는 부모가 생성, 자녀가 읽기 가능
    match /stickers/{stickerId} {
      allow create: if request.auth.uid == request.resource.data.parentUid;
      allow read: if request.auth.uid == resource.data.childUid 
                  || request.auth.uid == resource.data.parentUid;
    }
    
    // 가족 정보는 부모만 수정, 구성원은 읽기 가능
    match /families/{familyId} {
      allow read: if request.auth.uid == resource.data.parentUid
                  || request.auth.uid in resource.data.childUidList;
      allow write: if request.auth.uid == resource.data.parentUid;
    }
  }
}
```

**Storage Rules**

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // 영수증은 본인 폴더에만 업로드 가능
    match /receipts/{userId}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    
    // 프로필 사진은 본인 것만 수정 가능
    match /profiles/{userId}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
  }
}
```

---

## 8. OCR 구현 전략

### 8.1 Firebase ML Kit 사용

**의존성 추가**
```gradle
implementation 'com.google.mlkit:text-recognition:16.0.0'
```

**OCRProcessor 클래스 설계**

**주요 메서드**
```java
public class OCRProcessor {
    
    // 이미지에서 텍스트 추출
    public static void extractTextFromImage(
        Uri imageUri, 
        Context context, 
        OCRCallback callback
    )
    
    // 추출된 텍스트에서 금액 파싱
    private static String parseAmount(String text)
    
    // 추출된 텍스트에서 상호명 파싱
    private static String parseMerchantName(String text)
    
    // 콜백 인터페이스
    public interface OCRCallback {
        void onSuccess(String extractedText);
        void onFailure(Exception e);
    }
}
```

### 8.2 텍스트 추출 프로세스

**1단계: 이미지 전처리**
```java
1. Uri를 Bitmap으로 변환
2. 이미지 회전 보정 (Exif 정보 활용)
3. 이미지 크기 조정 (최대 1024x1024)
4. 흑백 변환 (선택사항, 인식률 향상)
```

**2단계: ML Kit 텍스트 인식**
```java
InputImage image = InputImage.fromBitmap(bitmap, 0);
TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

recognizer.process(image)
    .addOnSuccessListener(visionText -> {
        String fullText = visionText.getText();
        callback.onSuccess(fullText);
    })
    .addOnFailureListener(e -> {
        callback.onFailure(e);
    });
```

**3단계: 텍스트 파싱**
```java
// 금액 추출 (정규식 사용)
Pattern amountPattern = Pattern.compile("\\d{1,3}(,\\d{3})*원");
Matcher matcher = amountPattern.matcher(text);
if (matcher.find()) {
    String amount = matcher.group()
                          .replace(",", "")
                          .replace("원", "");
}

// 상호명 추출 (첫 번째 줄 또는 "상호" 키워드 뒤)
String[] lines = text.split("\n");
String merchantName = lines[0]; // 첫 줄을 상호명으로 간주
```

### 8.3 인식률 개선 전략

**이미지 품질 개선**
- 촬영 시 가이드라인 제공 (프레임 표시)
- 조명 충분한 환경 권장 메시지
- 흔들림 방지 안내

**인식 실패 시 대응**
- "영수증 인식에 실패했습니다" 메시지 표시
- "수동으로 입력하시겠습니까?" 선택지 제공
- 재촬영 옵션 제공

**부분 인식 처리**
- 금액만 인식된 경우: 금액 필드만 자동 입력
- 상호명만 인식된 경우: 메모 필드에 입력
- 나머지는 사용자가 수동 입력

### 8.4 OCR 한계 및 위험 관리

**PDF 기획서 p.14 위험 분석 참고**

**위험 요소**
1. Firebase ML Kit 인식률 낮음
2. 다양한 영수증 포맷 대응 어려움
3. 카테고리 자동 분류의 어려움

**대응 방안**
1. **인식률 문제**
    - 사용자에게 OCR은 보조 기능임을 명시
    - 수동 입력을 기본으로, OCR은 편의 기능으로 포지셔닝
    - 인식 결과를 수정 가능한 상태로 제공

2. **포맷 다양성**
    - 주요 체인점 영수증 포맷 우선 대응
    - 전자영수증 텍스트 복사-붙여넣기 기능 제공
    - 점진적 개선 (사용자 피드백 수집)

3. **카테고리 자동 분류**
    - MVP에서는 카테고리 자동 분류 제외
    - 사용자가 직접 카테고리 선택하도록 유도
    - 향후 머신러닝 모델 추가 검토

---

## 9. 역할 기반 접근 제어

### 9.1 역할 분리 원칙

**PDF 기획서 p.8 참고**

| 기능 | 부모 | 자녀 |
|------|------|------|
| 본인 가계부 작성 | ✅ | ✅ |
| 본인 거래내역 조회 | ✅ | ✅ |
| 자녀 거래내역 조회 | ✅ | ❌ |
| 부모 거래내역 조회 | ✅ | ❌ |
| 자녀 통계 조회 | ✅ | ❌ |
| 스티커 부여 | ✅ | ❌ |
| 스티커 받기 | ❌ | ✅ |
| 가족 연결 관리 | ✅ | ❌ |
| 자녀 목표 설정 | ✅ | ❌ |

### 9.2 코드 레벨 접근 제어

**MainActivity에서 역할 저장**
```java
public class MainActivity extends AppCompatActivity {
    private String currentUserRole; // "parent" or "child"
    
    public String getCurrentUserRole() {
        return currentUserRole;
    }
}
```

**Fragment에서 역할 확인**
```java
String role = ((MainActivity) requireActivity()).getCurrentUserRole();

if ("parent".equals(role)) {
    // 부모 전용 기능 표시
    showParentFeatures();
} else {
    // 자녀 전용 기능 표시
    showChildFeatures();
}
```

### 9.3 UI 레벨 분기 처리

**방법 1: 별도 Fragment 사용**
```java
// 권장 방식
Fragment fragment = role.equals("parent") 
    ? new ParentHomeFragment() 
    : new ChildHomeFragment();
```

**방법 2: 동일 Fragment 내부 분기**
```java
// SettingsFragment처럼 공통 부분이 많은 경우
if (currentUser.isParent()) {
    binding.layoutParentSettings.setVisibility(View.VISIBLE);
    binding.layoutChildSettings.setVisibility(View.GONE);
} else {
    binding.layoutParentSettings.setVisibility(View.GONE);
    binding.layoutChildSettings.setVisibility(View.VISIBLE);
}
```

### 9.4 데이터 접근 제어

**Firestore 쿼리 레벨**
```java
// 부모: 자녀 데이터 조회 가능
if (isParent && selectedChildUid != null) {
    targetUid = selectedChildUid;
} else {
    targetUid = currentUid; // 본인 데이터만
}

db.collection("transactions")
  .whereEqualTo("uid", targetUid)
  .get();
```

**UI 표시 레벨**
```java
// 자녀는 부모 데이터 접근 시도 시 에러 처리
if (isChild && attemptingToAccessParentData) {
    Toast.makeText(context, "접근 권한이 없습니다", Toast.LENGTH_SHORT).show();
    return;
}
```

---

## 10. 예외 처리 및 검증

### 10.1 입력 검증 (ValidationUtils)

**이메일 검증**
```java
public static boolean isValidEmail(String email) {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    return email != null && email.matches(emailPattern);
}
```

**비밀번호 검증**
```java
public static boolean isValidPassword(String password) {
    return password != null && password.length() >= 6;
}
```

**금액 검증**
```java
public static boolean isValidAmount(String amount) {
    try {
        long value = Long.parseLong(amount);
        return value > 0 && value  {
      // 성공 처리
      Toast.makeText(context, "저장 완료", Toast.LENGTH_SHORT).show();
  })
  .addOnFailureListener(e -> {
      // 실패 처리
      Log.e(TAG, "Firestore error", e);
      
      String errorMessage;
      if (e instanceof FirebaseNetworkException) {
          errorMessage = "네트워크 연결을 확인해주세요";
      } else if (e instanceof FirebaseAuthException) {
          errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요";
      } else {
          errorMessage = "오류가 발생했습니다: " + e.getMessage();
      }
      
      Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
  });
```

### 10.3 로딩 상태 관리

**ProgressBar 표시 패턴**
```java
// 시작
binding.progressBar.setVisibility(View.VISIBLE);
binding.btnSubmit.setEnabled(false);

// 완료 후
binding.progressBar.setVisibility(View.GONE);
binding.btnSubmit.setEnabled(true);
```

**전체 화면 로딩**
```java
// 로딩 Dialog 사용
ProgressDialog progressDialog = new ProgressDialog(context);
progressDialog.setMessage("처리 중...");
progressDialog.setCancelable(false);
progressDialog.show();

// 완료 후
progressDialog.dismiss();
```

### 10.4 빈 데이터 처리

**RecyclerView 빈 상태**
```java
if (transactionList.isEmpty()) {
    binding.recyclerView.setVisibility(View.GONE);
    binding.emptyView.setVisibility(View.VISIBLE);
    binding.tvEmptyMessage.setText("거래 내역이 없습니다");
} else {
    binding.recyclerView.setVisibility(View.VISIBLE);
    binding.emptyView.setVisibility(View.GONE);
}
```

### 10.5 권한 처리

**카메라 권한**
```java
if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
    != PackageManager.PERMISSION_GRANTED) {
    
    ActivityCompat.requestPermissions(
        activity,
        new String[]{Manifest.permission.CAMERA},
        CAMERA_PERMISSION_REQUEST_CODE
    );
} else {
    openCamera();
}
```

**권한 결과 처리**
```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show();
        }
    }
}
```

---

## 11. 개발 우선순위

### 11.1 Phase 1 - MVP (4주)

**Week 1: 기본 인프라**
- [x] 프로젝트 설정 및 Firebase 연동
- [x] XML 레이아웃 완성 (이미 완료)
- [ ] MainActivity + BottomNavigation 구현
- [ ] LoginFragment, SignupFragment 구현
- [ ] User 모델 및 Firebase Auth 연동

**Week 2: 핵심 기능 (거래 내역)**
- [ ] Transaction 모델 구현
- [ ] AddTransactionFragment 구현
    - 수입/지출 입력
    - 카테고리 선택
    - 날짜 선택
    - Firestore 저장
- [ ] TransactionAdapter 구현
- [ ] ParentHomeFragment / ChildHomeFragment 구현
    - 거래 내역 목록 표시
    - 월별 수입/지출 합계

**Week 3: 가족 연결 및 역할 분리**
- [ ] Family 모델 구현
- [ ] 가족 연결 기능 (이메일 검색)
- [ ] 부모/자녀 역할별 UI 분기
- [ ] 부모의 자녀 데이터 조회 기능
- [ ] TabLayout으로 부모/자녀 전환

**Week 4: 목표 및 기본 통계**
- [ ] SettingsFragment 구현
    - 프로필 수정
    - 저축 목표 설정
    - 지출 한도 설정
- [ ] 홈 화면에 목표 달성률/한도 현황 표시
- [ ] 기본 통계 (총 수입/지출)
- [ ] 버그 수정 및 테스트

### 11.2 Phase 2 - 고급 기능 (3주)

**Week 5: 통계 차트**
- [ ] MPAndroidChart 라이브러리 통합
- [ ] ParentStatisticsFragment / ChildStatisticsFragment 구현
- [ ] Bar Chart (수입/지출 비교)
- [ ] Pie Chart (카테고리별 비율)
- [ ] Line Chart (일별 추이)
- [ ] 월 선택 기능

**Week 6: 스티커 시스템**
- [ ] Sticker 모델 구현
- [ ] StickerGiveFragment 구현 (부모)
    - 자녀 선택
    - 스티커 선택
    - 메시지 입력
- [ ] StickerBoardFragment 구현 (자녀)
    - 스티커 그리드 표시
    - 목표 진행률
    - 스티커 상세 정보
- [ ] StickerAdapter 구현

**Week 7: 카테고리 관리**
- [ ] 카테고리 CRUD 기능
- [ ] 카테고리 아이콘 선택
- [ ] 기본 카테고리 프리셋 제공
- [ ] 사용자별 커스텀 카테고리 저장

### 11.3 Phase 3 - OCR 및 개선 (2주)

**Week 8: OCR 구현**
- [ ] Firebase ML Kit 통합
- [ ] OCRProcessor 클래스 구현
- [ ] 영수증 촬영/업로드 기능
- [ ] 텍스트 추출 및 파싱
- [ ] 자동 입력 기능
- [ ] 인식 실패 시 수동 입력 전환

**Week 9: 최적화 및 배포 준비**
- [ ] Firestore Security Rules 설정
- [ ] 성능 최적화
    - 쿼리 최적화
    - 이미지 압축
    - 메모리 누수 제거
- [ ] UI/UX 개선
- [ ] 에러 처리 강화
- [ ] 최종 테스트
- [ ] Google Play 배포 준비

### 11.4 선택적 기능 (추후 고려)

**알림 기능**
- Firebase Cloud Messaging 사용
- 지출 한도 초과 시 알림
- 부모가 스티커 부여 시 자녀에게 알림
- 월말 통계 요약 알림

**데이터 백업/복원**
- Firestore Export 활용
- CSV 내보내기 기능

**다크 모드**
- Theme 전환 기능
- 시스템 설정 따르기

**위젯**
- 홈 화면 위젯으로 이번 달 요약 표시

---

## 부록: 주요 위험 요소 및 대응 방안

### A. OCR 인식률 문제

**위험도: 중**

**문제점**
- Firebase ML Kit의 한글 인식률이 낮을 수 있음
- 다양한 영수증 포맷 대응 어려움
- 조명, 각도, 해상도에 따라 결과 차이

**대응 방안**
1. OCR을 보조 기능으로 포지셔닝 (필수 아님)
2. 인식 결과를 수정 가능한 상태로 제공
3. 수동 입력을 기본으로 권장
4. 전자영수증 텍스트 복사-붙여넣기 옵션 제공
5. 사용자 피드백 수집하여 점진적 개선

### B. Firebase 데이터 구조 복잡성

**위험도: 중**

**문제점** (PDF p.14 참고)
- 실시간 데이터 동기화 오류 가능성
- 부모-자녀 권한 차등 접근 제어 복잡
- 다양한 통계 제공 시 데이터 관리 복잡성

**대응 방안**
1. **Security Rules 명확히 정의**
    - 문서별 접근 권한 세밀하게 설정
    - 테스트 환경에서 충분히 검증

2. **데이터 구조 최소화**
    - 중복 데이터 최소화
    - 필요 시 subcollection 활용
    - 쿼리 최적화를 위한 인덱스 설계

3. **에러 처리 강화**
    - 모든 Firestore 호출에 onFailure 처리
    - 네트워크 오류 시 재시도 로직
    - 로컬 캐싱 활용 (선택사항)

### C. 역할 분리 UI 복잡성

**위험도: 중**

**문제점** (PDF p.14 참고)
- 부모/자녀가 다른 기능, 화면 사용
- 두 사용자 그룹을 동시에 만족시켜야 함
- 코드 분기 처리 복잡

**대응 방안**
1. **Fragment 분리 전략**
    - 공통 기능: 동일 Fragment 내부 분기
    - 전용 기능: 별도 Fragment 생성
    - BaseFragment로 공통 로직 추상화

2. **명확한 네이밍 컨벤션**
    - ParentXxxFragment / ChildXxxFragment
    - 역할별 패키지 분리 (parent, child, common)

3. **테스트 강화**
    - 부모/자녀 계정으로 각각 테스트
    - 권한 우회 시도 테스트
    - Edge case 시나리오 작성

### D. 성능 이슈

**위험도: 낮**

**잠재적 문제**
- 거래 내역이 많아질 경우 로딩 속도 저하
- 이미지 업로드 시 네트워크 부하
- 차트 렌더링 시 메모리 사용량 증가

**대응 방안**
1. **페이지네이션**
    - Firestore limit() 사용
    - 무한 스크롤 구현

2. **이미지 최적화**
    - 업로드 전 압축 (최대 1MB)
    - Glide 캐싱 활용

3. **차트 데이터 제한**
    - 최대 1년치 데이터만 표시
    - 필요 시 더 긴 기간은 별도 화면

---

## 마무리

이 PRD는 **우리 가족 가계부** 앱의 Java 코드 구현을 위한 기술 명세서입니다.

**핵심 포인트**
1. ✅ Single Activity + Multi-Fragment 아키텍처
2. ✅ Firebase 기반 백엔드 (Auth, Firestore, Storage, ML Kit)
3. ✅ 부모/자녀 역할 기반 접근 제어
4. ✅ RecyclerView 기반 거래 내역 및 스티커 표시
5. ✅ MPAndroidChart를 활용한 통계 시각화
6. ✅ OCR을 통한 영수증 자동 입력 (보조 기능)

**개발 시작 전 체크리스트**
- [ ] Firebase 프로젝트 생성 및 google-services.json 추가
- [ ] 필요한 라이브러리 의존성 추가
- [ ] Firestore Security Rules 설정
- [ ] 개발 환경 설정 (에뮬레이터 또는 실기기)
- [ ] Git 저장소 설정 및 브랜치 전략 수립

**참고 문서**
- 기획서: `사인페발표_문준,이래경,이아린.pdf`
- Firebase 문서: https://firebase.google.com/docs
- MPAndroidChart: https://github.com/PhilJay/MPAndroidChart
- Material Design: https://material.io/develop/android

