package jpashop.cascade.sample;

import javax.persistence.*;

@Entity
@Table(name = "CHILD")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CHILD_ID")
    private Long id;

    private String name;

    //'다대일' '양방향' 매핑
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;

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

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
