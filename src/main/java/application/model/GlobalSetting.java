package application.model;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //setting system name
    @Column(nullable = false)
    private String code;

    //setting name
    @Column(nullable = false)
    private String name;

    //setting value
    @Column(nullable = false)
    private String value;

    public GlobalSetting() {
    }

    public GlobalSetting(String code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GlobalSetting{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
