
# HTTP 캐시와 조건부 요청 - 캐시 무효화

### Cache-Control - 확실한 캐시 무효화 응답

- Cache-Control: no-cache, no-store, must-revalidate 
- Pragma: no-cache
  - HTTP 1.0 하위 호환

### Cache-Control - 캐시 지시어(directives) - 확실한 캐시 무효화

- Cache-Control: no-cache 
  - 데이터는 캐시해도 되지만, 항상 원 서버에 검증하고 사용
- Cache-Control: no-store 
  - 데이터에 민감한 정보가 있으므로 저장하면 안됨
- Cache-Control: must-revalidate 
  - 캐시 만료후 최초 조회시 원 서버에 검증해야함
  - 원 서버 접근 실패시 반드시 오류가 발생해야함 - 504(Gateway Timeout)
  - must-revalidate는 캐시 유효 시간이라면 캐시를 사용함
- Pragma: no-cache 


###  no-cache  vs must-revalidate 

![76.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F76.JPG)

![77.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F77.JPG)

![78.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F78.JPG)

- no-cache: 원 서버에 접근할 수 없는 경우 캐시 서버 설정에 따라서 캐시 데이터를 반환할 수 있음
- must-revalidate: 원 서버에 접근할 수 없는 경우, 항상 오류가 발생해야 함
  - 통장 장고: 클라이언트가 100만원을 입금 했는데 이전 계좌 잔고가 보임 😱😭😡