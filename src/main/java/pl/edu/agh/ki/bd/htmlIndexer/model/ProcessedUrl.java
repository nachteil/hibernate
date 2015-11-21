package pl.edu.agh.ki.bd.htmlIndexer.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@DynamicInsert(false)
@DynamicUpdate(false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProcessedUrl {

    @Setter private long id;
    @Setter private String sourceUrl;
    @Setter private Date processedDate;

    @Setter private Set<Sentence> sentences = new HashSet<>();

    public ProcessedUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        this.processedDate = Date.valueOf(LocalDate.now());
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceUrl")
    public Set<Sentence> getSentences() {
        return sentences;
    }

    @Override
    public String toString() {
        return this.sourceUrl;
    }
}
