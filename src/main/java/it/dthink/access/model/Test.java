package it.dthink.access.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "Test")
@EqualsAndHashCode()
public class Test {

    @Id
    @Column(name = "ID")
    private String id;
    
    @NotNull
    @Column(name = "campo1")
    @JsonProperty("campo_1")
    private String campo1;
    
    @NotNull
    @Column(name = "campo2")
    @JsonProperty("campo_2")
    private String campo2;
    
    

    public Test() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String partType) {
        this.campo1 = partType;
    }

    public String getCampo2() {
        return campo2;
    }

    public void setCampo2(String subType) {
        this.campo2 = subType;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", campo1='" + campo1 + '\'' +
                ", campo2='" + campo2 + '\'' +
                '}';
    }
}
