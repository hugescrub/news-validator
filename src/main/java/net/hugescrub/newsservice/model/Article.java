package net.hugescrub.newsservice.model;

import jdk.jfr.BooleanFlag;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 30)
    private String title;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 800)
    private String body;

    @NotNull
    @UpdateTimestamp
    private LocalDateTime created;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(name = "article_sources",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "source_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "source_id"}))
    private List<Source> sources = new ArrayList<>();

    @BooleanFlag
    private Boolean isFake;

    public Article() {
    }

    public Article(String title, String body, LocalDateTime created, Boolean isFake) {
        this.title = title;
        this.body = body;
        this.created = created;
        this.isFake = isFake;
    }

    public void setFake(Boolean fake) {
        isFake = fake;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "Article{" +
                "\nid=" + id +
                "\ntitle='" + title + '\'' +
                "\nbody='" + body + '\'' +
                "\ncreated=" + created +
                "\n}";
    }
}
