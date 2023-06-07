package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public final class Url extends Model {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @WhenCreated
    private Instant createdAt;

    public Url() {
    }

    public Url(String newName) {
        this.name = newName;
    }

    public long getId() {
        return id;
    }

    public void setId(long newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant newCreatedAt) {
        this.createdAt = newCreatedAt;
    }
}
