package spring.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * uploadFileName: 고객이 업로드한 파일명
 * storeFileName: 서버 내부에서 관리하는 파일명
 * 고객이 업로드한 파일명으로 서버 내부에 파일을 저장하면 안된다.
 * 왜냐하면 서로 다른 고객이 같은 파일이름을 업로드하는 경우 기존 파일 이름과 충돌이 날 수 있다.
 */
@Data
@AllArgsConstructor
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;
}
