package jpashop.jpabook.sampledomain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    // 양방향 연관관계를 위한 참조
    // * 관례 - ArrayList로 초기화 해놓는 것 for Add 시 null 피하기 위해
    @OneToMany(mappedBy = "team") // * Entity 전체가 아닌 미시적인 필드의 입장에서, 해당 필드가 반대편 어떤 변수와 연관관계가 있는지 명시
    private List<MemberA> members = new ArrayList<>();

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

    public List<MemberA> getMembers() {
        return members;
    }

    public void setMembers(List<MemberA> members) {
        this.members = members;
    }

    public void addMember(MemberA memberA) {
        memberA.setTeam(this);
        members.add(memberA);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", members=" + members + //members는 컬렉션 내부 하나하나의 모든 요소의 toString() 호출
                '}';
    }
}
