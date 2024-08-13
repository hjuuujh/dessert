## 직접 만든 디저트를 판매하고 구매하는 서비스 제공하는 웹애플리케이션

## 기술

## 주요기능
- 고객/셀러 회원가입
- JWT 이용한 기본 권한 및 인증
- 셀러의 매장 등록, 확인, 수정, 삭제
- 제품검색(키워드 검색 - 최신순, 별점순, 구매후기 많은 순, 판매수 많은 순) 상세정보 확인
- 셀러검색 팔로워순
- 장바구니 추가, 확인, 수정, 삭제 
- 장바구니 통한 구매 서비스
- 리뷰 작성, 확인, 수정, 삭제

## 서비스 흐름도
![flow.png](./img/flow.png)

## Erd
![erd.png](./img/erd.png)

## MSA
- Spring Cloud Eureka Server로 마이크로서비스 애플리케이션(MSA) 구현
- Docker 이용
![msa.png](./img/msa.png)
## API
### Member API
1. 회원가입  POST /api/member/singup
- 이메일 중복 불가능
- roles 이용해 고객과 셀러 구분
- 비밀번호 암호화 해서 db에 저장
- 파라미터 : 이메일, 이름, 비밀번호, 핸드폰번호, role
- 성공 : 이메일, 이름, 핸드폰번호, role
- 실패 

|Case|HttpStatus|Error Code|Description|
|------|-------|----------|---------|
|이미 등록된 이메일인 경우| BAD_REQUEST | "이미 가입된 회원입니다." | ALREADY_REGISTERED_USER|	
2. 로그인  POST /api/member/singin
- Jwt token 리턴
- 파라미터 : 이메일, 비밀번호
- 성공 : token
- 실패 

|Case|HttpStatus|Error Code|Description|
|------|-------|----------|---------|
|이메일로 가입된 정보가 없는 경우| BAD_REQUEST |  NOT_FOUND_USER |"일치하는 회원이 없습니다."|
|이메일과 패스워드가 일치하지 않는 경우| BAD_REQUEST | LOGIN_CHECK_FAIL |"이메일과 패스워드를 확인해주세요." |	
3. 고객 정보 : GET - /api/member/id
- 토큰을 파싱해 해당 토큰 발급받은 유저 찾기
- 파라미터 : 토큰
- 결과
- 성공 : user id
4. 잔액 충전 : POST - /api/member/charge
- 멤버의 잔액 충전
- aop를 이용한 계좌 lock
- 파라미터 : 토큰, 금액
- 실패

|Case|HttpStatus|Error Code|Description|
|------|-------|----------|---------|
|이메일로 가입된 정보가 없는 경우| BAD_REQUEST |  NOT_FOUND_USER |"일치하는 회원이 없습니다."|
5. 잔액 변경 : POST - /api/member/order?refund=
- 주문/환불시 잔액 변경
- aop를 이용한 계좌 lock
- transcation table에 성공/실패 결과 저장 
- 파라미터 : 토큰, 사용 쿠폰, 금액, 오더 id
- 실패

|Case|HttpStatus|Error Code|Description|
|------|-------|----------|---------|
|잔액이 부족한 경우| BAD_REQUEST |  NOT_ENOUGH_BALANCE |"잔액이 부족합니다."|
|쿠폰이 부족한 경우| BAD_REQUEST |  NOT_ENOUGH_COUPON |"쿠폰이 부족합니다."|
### Store API
1. 매장정보 등록 POST - /api/store
- 헤더 : token
- 파라미터 : 매장명, 상세정보, 매장섬네일url, 팔로우수(0)
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|매장명 중복인 경우| BAD_REQUEST |  DUPLICATE_STORE_NAME |"매장명은 중복일 수 없습니다."|
2. 제품 등록 POST - /api/store/item
- 헤더 : token
- 파라미터 : 매장id, 제품명, 제품 설명, 제품설명 이미지url, 제품섬네일url, 옵션 리스트, 가격, 별점
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|매장명 중복인 경우| BAD_REQUEST |  DUPLICATE_STORE_NAME |"매장명은 중복일 수 없습니다."|
|가격이 음수인 경우| BAD_REQUEST |  CHECK_ITEM_PRICE |"가격을 확인해주세요."|
3. 제품 옵션 변경 : POST - /api/store/item/option
- 헤더 : token
- 파라미터 : 제품id, 옵션명, 가격, 수량 
- 결과
- 성공 : 옵션 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|옵션명 중복인 경우| BAD_REQUEST |  DUPLICATE_ITEM_NAME |"아이템은 중복일 수 없습니다."|
|가격이 음수인 경우| BAD_REQUEST |  CHECK_ITEM_PRICE |"가격을 확인해주세요."|
|수량이 음수인 경우| BAD_REQUEST |  CHECK_ITEM_QUANTITY |"수량을 확인 해주세요."|
4. 매장정보 수정 PUT - /api/store
- 헤더 : token
- 파라미터 : 매장명, 상세정보, 매장섬네일url, 팔로우수(0)
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|매장명 중복인 경우| BAD_REQUEST |  DUPLICATE_STORE_NAME |"매장명은 중복일 수 없습니다."|
|셀러와 매장 정보가 일치하지 않는 경우| BAD_REQUEST | UNMATCHED_STORE_SELLER |"매장 정보와 판매자 정보가 일치하지 않습니다."|
5. 매장 정보 삭제 : PATCH - /api/store?id=
- 헤더 : 토큰 
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|셀러와 매장 정보가 일치하지 않는 경우| BAD_REQUEST | UNMATCHED_STORE_SELLER |"매장 정보와 판매자 정보가 일치하지 않습니다."|
6. 아이템 삭제 : DELETE - /api/store/item?id=
- 헤더 : 토큰 
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|셀러와 매장 정보가 일치하지 않는 경우| BAD_REQUEST | UNMATCHED_STORE_SELLER |"매장 정보와 판매자 정보가 일치하지 않습니다."|
7. 옵션 삭제 : DELETE - /api/store/option?id=
- 헤더 : 토큰 
- 결과
- 성공 : 매장 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|셀러와 매장 정보가 일치하지 않는 경우| BAD_REQUEST | UNMATCHED_STORE_SELLER |"매장 정보와 판매자 정보가 일치하지 않습니다."|
8. 후기 추가 or 후기 수정시 아이템의 별점 변경 : POST - /api/store/rating
- 헤더 : 토큰
- 파라미터 : itemId, rating
- 결과
- 성공 : ok
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|아이템 정보 없는 경우| BAD_REQUEST | NOT_FOUND_ITEM |"아이템이 존재하지 않습니다."|
8. 후기 삭제시 아이템의 별점 변경 : POST - /api/store/rating
- 헤더 : 토큰
- 파라미터 : itemId, rating
- 결과
- 성공 : ok
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|아이템 정보 없는 경우| BAD_REQUEST | NOT_FOUND_ITEM |"아이템이 존재하지 않습니다."|
9. 제품 찾기 - 키워드 검색 : GET - /api/store/item/search?keyword=
- 헤더 : token
- 대문자, 소문자 상광없이 키워드를 포함하는 아이템 최신순
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 리스트
10. 제품 찾기 - 키워드 검색 + 별점순 : GET - /api/store/item/search?keyword=&type=rating
- 헤더 : token
- 대문자, 소문자 상광없이 키워드를 포함하는 아이템 별점순 내림차순 정렬 
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 리스트
11. 제품 찾기 - 키워드 검색 + 구매후기 많은 순 : GET - /api/store/item/search?keyword=&type=reviewCount
- 헤더 : token
- 대문자, 소문자 상광없이 키워드를 포함하는 아이템 리뷰 순 내림차순 정렬 
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 리스트
10. 제품 찾기 - 키워드 검색 + 판매 많은 순 : GET - /api/store/item/search?keyword=&type=orderCount
- 헤더 : token
- 대문자, 소문자 상광없이 키워드를 포함하는 아이템 주문 순 내림차순 정렬 
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 리스트
11. 제품 찾기 - 특정 매장의 제품 : GET - /api/store?id=
- 헤더 : token
- 특정 매장의 모든 제품 
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 리스트
12. 제품 상세정보 : GET - /api/store/item?id=
- 헤더 : token
- 파라미터 : 키워드, Pageable
- 결과
- 성공 : 아이템 정보
### order API
1. 카트 추가 : POST - /api/order/customer/cart
- 헤더 : 토큰
- 파라미터 : memberId, 아이템 리스트, 메세지 리스트 
- 결과
- 성공 : 카트 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|아이템 정보 없는 경우| BAD_REQUEST | NOT_FOUND_ITEM |"아이템이 존재하지 않습니다."|
|아이템 수량 부족한 경우| BAD_REQUEST | NOT_ENOUGH_ITEM |"아이템 수량이 부족합니다."|
2. 카트 확인 : GET - /api/order/customer/cart
- 헤더 : 토큰
- 결과
- 성공 : 카트 정보
3. 카트 수정 : PUT - /api/order/customer/cart
- 헤더 : 토큰
- 파라미터 : memberId, 아이템 리스트, 메세지 리스트 
- 결과
- 성공 : 카트 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|아이템 정보 없는 경우| BAD_REQUEST | NOT_FOUND_ITEM |"아이템이 존재하지 않습니다."|
|아이템 수량 부족한 경우| BAD_REQUEST | NOT_ENOUGH_ITEM |"아이템 수량이 부족합니다."|
4. 카트 주문 : POST - /api/order/customer/order
- 오더 테이블에 storeId 별 아이템 저장
- 헤더 : 토큰
- 파라미터 : memberId, 아이템 리스트, 메세지 리스트 
- 결과
- 성공 : 카트 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|장바구니에 담은 아이템의 정보 변동이 있는 경우| BAD_REQUEST | ORDER_FAIL_CHECK_CART |"주문 불가, 장바구니를 확인해주세요"|
|고객의 잔액이 부족한 경우| BAD_REQUEST | ORDER_FAIL_NO_MONEY |"주문 불가, 잔액 부족입니다."|
5. 리뷰 작성 : POST - /api/order/customer/review
- 헤더 : token
- 파라미터 : 오더id, 고객id, 후기, 별점
- 결과
- 성공 : 리뷰 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|리뷰 등록하는 고객정보와 구매 고객정보가 일치하지 않는 경우| BAD_REQUEST |  UNMATCHED_CUSTOMER_ORDER |"고객 정보와 구매 정보가 일치하지 않습니다."|
|5점 보다 높은 별점을 준 경우| BAD_REQUEST |  OVER_RATING_LIMIT |"별점은 최대 5점까지 가능합니다."|
|등록 요청 고객이 구매내역에 이미 리뷰 등록한 경우| BAD_REQUEST |  ALREADY_CREATED_REVIEW |"이미 리뷰를 작성하였습니다."|
6. 리뷰 수정 : PUT - /api/order/customer/review
- 헤더 : token
- 파라미터 : 리뷰id, 후기, 별점
- 결과
- 성공 : 리뷰 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|-------
|리뷰 수정하는 고객정보와 구매 고객정보가 일치하지 않는 경우| BAD_REQUEST |  UNMATCHED_CUSTOMER_ORDER |"고객 정보와 구매 정보가 일치하지 않습니다."|
|5점 보다 높은 별점을 준 경우| BAD_REQUEST |  OVER_RATING_LIMIT |"별점은 최대 5점까지 가능합니다."|
|등록 요청 고객이 구매내역에 이미 리뷰 등록한 경우| BAD_REQUEST |  ALREADY_CREATED_REVIEW |"이미 리뷰를 작성하였습니다."|
7. 고객이 자신이 등록한 리뷰 삭제 : DELETE - /api/order/customer/review
- 헤더 : token
- 파라미터 : 리뷰id
- 결과
- 성공 : 리뷰 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|리뷰 수정하는 고객정보와 구매 고객정보가 일치하지 않는 경우| BAD_REQUEST |  UNMATCHED_CUSTOMER_ORDER |"고객 정보와 구매 정보가 일치하지 않습니다."|
8. 고객이 자신이 등록한 리뷰 확인 : GET - /api/order/customer/review
- 헤더 : token
- 파라미터 : Pageable
- 결과
- 성공 : 리뷰 리스트
9. 셀러가 자신의 판매 아이템에 등록된 리뷰 삭제 : DELETE - /api/order/partner/review
- 헤더 : token
- 파라미터 : 리뷰id
- 결과
- 성공 : 리뷰 정보
- 실패

|Case|HttpStatus|Error Code|Description|
|------------|-------|----------|---------|
|리뷰 수정하는 고객정보와 구매 고객정보가 일치하지 않는 경우| BAD_REQUEST |  UNMATCHED_CUSTOMER_ORDER |"고객 정보와 구매 정보가 일치하지 않습니다."| 
10. 특정 아이템에 등록된 모든 리뷰 확인 : GET - /api/order/review?id=
- 파라미터 : 아이템id
- 결과
- 성공 : 리뷰 리스트


























