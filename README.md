## 도서 대출 시스템
- 이 시스템은 사용자가 회원가입 후 로그인을 통해 도서를 대출하고 반납할 수 있는 기능을 제공합니다. 사용자는 개인 정보를 관리하고, 커뮤니티 내에서 게시글 및 댓글 작성이 가능합니다.

## 요구사항
1) **도서 대출, 도서 반납 API** : 사용자는 도서를 대출하거나 반납할 수 있습니다. 이 과정에서 도서의 상태가 업데이트되어야 하며, 대출 가능 여부를 확인해야 합니다.
2) **사용자 정보 조회 API** : 사용자는 자신의 정보를 조회할 수 있습니다.
3) **공지사항 게시글 작성, 수정, 삭제 API** : 관리자는 게시판에 공지사항을 작성, 삭제, 수정 할 수 있습니다.
4) **댓글 작성, 수정, 삭제 API** : 사용자는 게시글에 대한 댓글을 작성, 수정, 삭제할 수 있습니다. 댓글 작성자만 자신의 댓글을 삭제, 수정 할 수 있으며, 댓글 달린 게시글이 삭제될 경우 연관된 댓글도 함께 삭제되어야 합니다.

## 도메인 설계
- **Member**: 사용자 정보 관리
- **Role**: 권한 관리 관리
- **Book**: 도서 정보 관리
- **BookStatus**: 도서의 상태 정보 관리
- **Loan**: 대출 기록 관리
- **Reservation**: 도서에 대한 예약 관리
- **Article**: 관리자가 작성한 게시글 관리
- **Comment**: 게시글에 대한 댓글 관리
- 시스템은 사용자가 로그인 하면 도서 조회, 대출, 반납, 예약을 할 수 있습니다.
- 관리자는 공지사항을 작성할 수 있고 관리자 및 사용자는 댓글 작성을 할 수 있습니다.

## 활용 기술
- **Spring Boot** 3.2.1 : 웹 애플리케이션 개발을 위한 기본 프레임워크
- **Java 17** : Spring Boot 3.x.x에 대응하는 Java 버전을 사용
- **Spring Data JPA** : 데이터베이스 객체 관리
- **Querydsl** : 타입 안전 쿼리 SQL 쿼리 작성
- **MySQL** : 데이터베이스 서버
- **Spring Security** : 인증 및 인가 처리
- **JWT(JSON Web Tokens)** : 안전한 사용자 인증을 위한 토큰 기반 인증 시스템

## ERD
![LibraryLoan](https://github.com/Dorosiya/LibraryLoan/assets/129160905/837983bd-1c12-4378-9fd8-f106f01a074f)

## API 문서
<details markdown="1">
  <summary>API 문서 목록</summary>

사용자 정보 조회
- URL: `/api/member`
- Method: `GET`
- Description: JWT 토큰 파싱을 통해 사용자 정보 조회합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함

사용자 정보 생성
- URL: `/api/member`
- Method: `POST`
- Description: 회원 가입을 통해 사용자 정보를 생성합니다.
- Request Body:
  - username(String, required)
  - password(String, required)
  - email(String, required)
  - age(int, required)

도서 조회
- URL: `/api/book`
- Method: `GET`
- Description: 조건에 따라 도서를 조회합니다.
- URL Parameters:
  - title(String)
  - author(String)
  - publisher(String)
  - yearOfPublication(String)

대출 내역 조회
- URL: `/api/loan`
- Method: `GET`
- Description: 사용자의 대여 내역 조회합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함

대출 생성
- URL: `/api/loan`
- Method: `POST`
- Description: 선택한 도서를 대여합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Request Body:
  - bookId(Long, required)

도서 반납
- URL: `/api/loan`
- Method: `PATCH`
- Description: 대여한 도서를 반납합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Request Body:
  - bookId(Long, required)

도서 예약
- URL: `/api/reservation`
- Method: `POST`
- Description: 사용자가 대출된 도서를 예약합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Request Body:
  - bookId(Long, required)

예약 조회
- URL: `/api/reservation`
- Method: `GET`
- Description: 사용자의 예약 정보를 조회합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함

대여 권수 및 예약 권수 조회
- URL: `/api/basic`
- Method: `GET`
- Description: 사용자가 대여한 권수 및 예약 권수를 조회합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함

공지사항 생성
- URL: `/api/article`
- Method: `POST`
- Description: 공지사항을 생성합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Request Body:
  - title(String, required)
  - content(String, required)

공지사항 조회
- URL: `/api/article`
- Method: `GET`
- Description: 조건에 따라 작성된 공지사항을 조회합니다. 
- URL Parameters:
  - title(String)
  - content(String)
  - username(String)

특정 공지사항 조회
- URL: `/api/article/{articleId}`
- Method: `GET`
- Description: 
  - 공지사항 목록에서 선택한 공지사항의 정보를 조회합니다. 
  - 해당 공지사항에 달린 댓글도 함께 조회합니다.
- URL Parameters: 
  - articleId(Long, required)

공지사항 수정
- URL: `/api/article/{articleId}`
- Method: `PATCH`
- Description: 공지사항 제목 및 내용을 수정합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- URL Parameters:
  - articleId(Long, required)
- Request Body:
  - title(String, required)
  - content(String, required)

공지사항 삭제
- URL: `/api/article/{articleId}`
- Method: `DELETE`
- Description: 공지사항을 삭제합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- URL Parameters:
  - articleId(Long, required)

댓글 생성
- URL: `/api/comment`
- Method: `POST`
- Description: 댓글을 생성합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Request Body:
  - articleId(Long, required)
  - content(String, required)

댓글 수정
- URL: `/api/comment/{commentId}`
- Method: `PATCH`
- Description: 댓글을 수정합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- URL Parameters:
  - commentId(Long, required)
- Request Body:
  - content(String, required)

댓글 삭제
- URL: `/api/comment/{comment}`
- Method: `DELETE`
- Description: 댓글을 삭제합니다.
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- URL Parameters:
  - commentId(Long, required)

액세스 토큰 재발급
- URL: `/api/reissue`
- Method: `DELETE`
- Description: 리프레시 토큰이 만료되지 않았을 때 액세스 토큰 재발급
- Headers:
  - Authorization: Bearer {token}: 요청 헤더에 JWT 토큰 포함
- Cookie:
  - refresh: {refreshToken}
</details>

## 주요 화면
- 로그인 화면 페이지
  - ![img_11](https://github.com/Dorosiya/LibraryLoan/assets/129160905/4619794a-adf8-4b28-818f-6f4b3c018cdf)
- 메인 화면 페이지
  - ![img_3](https://github.com/Dorosiya/LibraryLoan/assets/129160905/1cff31c6-2091-4b01-8049-39cc0934af62)
- 기본 정보 페이지
  - ![img_5](https://github.com/Dorosiya/LibraryLoan/assets/129160905/721fece4-0d9f-4999-a4ac-0da817395ea0)
- 도서 대출 정보 페이지
  - ![img_2](https://github.com/Dorosiya/LibraryLoan/assets/129160905/04082bbb-a285-4a0b-a746-36597608951d)
- 도서 예약 정보 페이지
  - ![img_6](https://github.com/Dorosiya/LibraryLoan/assets/129160905/820fe88e-c0db-44ba-aa11-18e7081a2295)
- 공지사항 목록 페이지
  - ![img_8](https://github.com/Dorosiya/LibraryLoan/assets/129160905/972d77d3-0855-4eb7-a299-04fc3f7ebb70)
- 공지사항 작성 페이지
  - ![img_7](https://github.com/Dorosiya/LibraryLoan/assets/129160905/bd7ee33f-cbb3-434b-818c-5825daae7c2e)
- 공지사항 조회 페이지
  - ![img_9](https://github.com/Dorosiya/LibraryLoan/assets/129160905/197ecc81-4e18-4d06-af3a-9858b955b552)
- 내정보 페이지
  - ![img_10](https://github.com/Dorosiya/LibraryLoan/assets/129160905/922325d0-2531-4b89-b0cf-c99f093b26ae)
