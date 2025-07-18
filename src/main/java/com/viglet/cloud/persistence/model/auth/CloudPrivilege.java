package com.viglet.cloud.persistence.model.auth;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
@Table(name = "auth_privilege")
public class CloudPrivilege implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private String id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<CloudRole> turRoles  = new HashSet<>();

    public CloudPrivilege() {
        super();
    }
    public CloudPrivilege(String name) {
        super();
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return prime * result + ((getName() == null) ? 0 : getName().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        com.viglet.cloud.persistence.model.auth.CloudPrivilege other = (com.viglet.cloud.persistence.model.auth.CloudPrivilege) obj;
        if (getName() == null) {
            return other.getName() == null;
        } else return getName().equals(other.getName());
    }

    @Override
    public String toString() {
        return "Privilege [name=" + name + "]" + "[id=" + id + "]";
    }
}
