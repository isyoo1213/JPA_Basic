package jpashop.jpainheritance.mappedsuperclass;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

//Mapping 정보만 받는 슈퍼 클래스
@MappedSuperclass
public abstract class BaseEntity {

    //@Column(name) 속성 지정 시, 이를 상속받는 모든 하위 Entity의 테이블에도 같은 이름의 Column 생성되므로 주의
    //@Column(name = "INSERT_MEMBER")
    private String createdBy;
    private String lastModifiedBy;
    //@Column(name = "UPDATE_MEMBER")
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
