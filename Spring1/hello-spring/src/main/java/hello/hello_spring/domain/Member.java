package hello.hello_spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * @Entity -> JPA Entity 매핑 -> JPA가 관리하는 Entity
 * @Id -> 해당 필드가 테이블의 기본키로 사용됨을 설정
 * @GeneratedValue(strategy = GenerationType.IDENTITY ) -> JPA가 데이터베이스 매핑 처리 & 기본 키의 값을 자동으로 생성
 */
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
