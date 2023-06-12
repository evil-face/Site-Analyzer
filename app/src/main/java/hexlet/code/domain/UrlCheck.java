package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public final class UrlCheck extends Model {
    @Id
    @GeneratedValue
    private long id;

    private short statusCode;

    private String title;

    private String h1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "url_id", referencedColumnName = "id")
    private Url url;

    @Lob
    private String description;

    @WhenCreated
    private Instant createdAt;

    public long getId() {
        return id;
    }

    public void setId(long newId) {
        this.id = newId;
    }

    public short getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(short newStatusCode) {
        this.statusCode = newStatusCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String newH1) {
        this.h1 = newH1;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url newUrl) {
        this.url = newUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant newCreatedAt) {
        this.createdAt = newCreatedAt;
    }
}
