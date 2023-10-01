package jpashop.jpainheritance.mappedsuperclass;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TEAMM")
public class TeamM extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEAMM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<MemberM> members = new ArrayList<>();

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

    public List<MemberM> getMembers() {
        return members;
    }

    public void setMembers(List<MemberM> members) {
        this.members = members;
    }
}
