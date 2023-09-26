package jpashop.jpabook.OneToManySample;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TEAM_B")
public class TeamB {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEAM_B_ID")
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "TEAM_B_ID")
    private List<MemberB> members = new ArrayList<>();

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

    public List<MemberB> getMembers() {
        return members;
    }

    public void setMembers(List<MemberB> members) {
        this.members = members;
    }
}
