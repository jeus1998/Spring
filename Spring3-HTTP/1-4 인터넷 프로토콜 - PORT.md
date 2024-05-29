
# 인터넷 프로토콜 - PORT

![12- PORT 이해.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F12-%20PORT%20%EC%9D%B4%ED%95%B4.JPG)

- TCP/IP 패킷 정보에는 IP, PORT 정보가 있다.
- 1개의 서버(IP)에는 n개 이상의 애플리케이션이 있다.
- 같은 IP 내에서 어떤 애플리케이션인지 구분 -> PORT

![13- 같은 프로세스 PORT 구분.JPG](%EC%9D%B4%EB%AF%B8%EC%A7%80%2F13-%20%EA%B0%99%EC%9D%80%20%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4%20PORT%20%EA%B5%AC%EB%B6%84.JPG)

비유적 표현
- IP = 아파트
- PORT = XXX동 XXX호

### PORT 번호

- 0 ~ 65535 할당 가능
- 0 ~ 1023: 잘 알려진 포트, 사용하지 않는 것이 좋음 (OS 프로세스 사용 PORT)
  - FTP - 20, 21
  - TELNET - 23
  - HTTP - 80
  - HTTPS - 443

