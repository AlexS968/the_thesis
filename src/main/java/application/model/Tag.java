package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //текст тэга
    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "tag")
    private Set<TagToPost> tagToPosts;
}
