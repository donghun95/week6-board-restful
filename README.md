1.리펙토리 전/후 URL 비교표  

 URL 구조 4원칙  
 
URL에 동사를 넣지 않는다. 동작은 HTTP 메서드가 표현/  
컬렉션은 복수형 (board X, boards O)/  
계층 구조는 리소스 관계로/  
검색·필터는 qurey string

리펙토리 전 

### 동적 검색: 제목 + 작성자
GET http://localhost:18080/api/boards/search?title=Cursor&writer=donghun

### foreach: 여러 id를 IN 절로 조회
GET http://localhost:18080/api/boards/bulk?ids=1&ids=3&ids=5

### Offset 페이징
GET http://localhost:18080/api/boards/offset?page=0&size=3

### Cursor 페이징 첫 페이지
GET http://localhost:18080/api/boards/cursor?size=3

### Cursor 페이징 다음 페이지
GET http://localhost:18080/api/boards/cursor?cursor=3&size=3

### 조회수 증가
POST http://localhost:18080/api/boards/1/views

### 게시글 등록
POST http://localhost:18080/api/boards
Content-Type: application/json

{
  "title": "수업 중 등록한 글",
  "content": "Spring Boot + MyBatis 5주차 시연",
  "writer": "mentor"
}

리펙토리 후

### 6주차 RESTful: 목록 조회 + 복합 Cursor 첫 페이지
GET http://localhost:18080/api/boards?size=2

### 6주차 RESTful: 복합 Cursor 다음 페이지
GET http://localhost:18080/api/boards?cursorId=4&cursorCreatedAt=2026-05-21T10:17:25&size=2

페이징 방식이 들어나지 않게 통합했습니다.

### 6주차 RESTful: 검색 조건도 같은 컬렉션 조회 API로 처리
GET http://localhost:18080/api/boards?title=Cursor&writer=donghun

search는 동사. 단순필터는 컬렉션 qurerystring으로 하여야 합니다.

### 6주차 RESTful: 여러 id 조회도 같은 컬렉션 조회 API로 처리
GET http://localhost:18080/api/boards?ids=1&ids=3&ids=5

bulk도 동작을 의미하며 여러 id조회는 컬렉션 필터로 하여야 합니다.

### 조회수 증가: PATCH 방식
PATCH http://localhost:18080/api/boards/1/views

views 일부 상태 수 정이므로 PATCH가 의미상 정확합니다.

### 게시글 등록: 201 Created + Location 헤더 확인
POST http://localhost:18080/api/boards
Content-Type: application/json

{
  "title": "REST 리팩토링",
  "content": "6주차 시연",
  "writer": "donghun"
}
POST 성공 시 Location  헤더 추가하고 생성 API의 표준 응답입니다.


2. 상태 코드 정리
<img width="972" height="598" alt="image" src="https://github.com/user-attachments/assets/b86289b4-b418-43dd-8ca7-074b76e84c6e" />

3. 에러 응답 예시
   
   404 에러 응답
   
<img width="674" height="467" alt="image" src="https://github.com/user-attachments/assets/10d95154-eaed-42a2-bb74-0e990670f17a" />

   
   400 에러 응답
   
<img width="741" height="351" alt="image" src="https://github.com/user-attachments/assets/78fb7d3a-7167-4fc6-8866-bd5e31a62dbd" />


4.복합 Cursor 페이징에서 id와 createdAt 이 둘 다 필요한 이유

응답에 nextCursorId와 nextCursroCreatedAt을 같이 내려야 합니다.   
id만 내려주면 클라이언트가 마지막 item에서 createdAt을 직접 꺼내야 해서 불친절합니다.


   




