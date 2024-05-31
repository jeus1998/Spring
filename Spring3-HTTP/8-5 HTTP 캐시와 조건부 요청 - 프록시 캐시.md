
# HTTP 캐시와 조건부 요청 - 프록시 캐시

### Cache-Control - 캐시 지시어(directives) - 기타

- Cache-Control: public 
  - 응답이 public 캐시에 저장되어도 됨 (프록시 서버)
- Cache-Control: private 
  - 응답이 해당 사용자만을 위한 것임, private 캐시에 저장해야 함(기본값) - local 웹 브라우저
- Cache-Control: s-maxage 
  - 프록시 캐시에만 적용되는 max-age
- Age: 60 (HTTP 헤더)
  - 오리진 서버에서 응답 후 프록시 캐시 내에 머문 시간(초)

![73.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F73.JPG)

![74.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F74.JPG)

![75.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F75.JPG)

