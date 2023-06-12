package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Entity
public final class Url extends Model {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "url")
    private List<UrlCheck> urlChecks;

    @WhenCreated
    private Instant createdAt;

    public UrlCheck getLastCheck() {
        return urlChecks.stream()
                .max(Comparator.comparing(UrlCheck::getCreatedAt))
                .orElse(null);
    }

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

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    public void setUrlChecks(List<UrlCheck> newUrlChecks) {
        this.urlChecks = newUrlChecks;
    }

}
