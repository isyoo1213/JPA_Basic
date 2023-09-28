package jpashop.jpabook.OneToOneSample;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_C")
public class MemberC {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_C_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "LOCKER_MEMBER_ID") // **** Join의 대상이 되는 Entity의 필드명이 아닌, DB Table에 Join 후 생성할 FK의 Column 이름 자체
    private Locker locker;
}
