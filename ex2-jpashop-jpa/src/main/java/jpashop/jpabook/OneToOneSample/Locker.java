package jpashop.jpabook.OneToOneSample;

import javax.persistence.*;

@Entity
public class Locker {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LOCKER_ID")
    private Long id;

    private String name;

    /** 1 : 1 연관관계 양방향
     * 1:1 연관관계도 다대일/일대다 연관관계처럼 mappedBy 동작은 같은 원리로 진행됨
     * -> 양방향(객체 관점에서는 서로 다른 단방향 2개 / 테이블 관점에서는 이미 양방향)으로 맺어주는 것은 매우 단순함
     */
    @OneToOne(mappedBy = "locker")
    private MemberC member;

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
