package pl.edu.agh.ki.bd.htmlIndexer.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@DynamicInsert(false)
@DynamicUpdate(false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Sentence {
	
	@Setter private long id;
    @Setter private String content;
    @Setter private ProcessedUrl sourceUrl;
    @Setter private Set<Word> words = new LinkedHashSet<>();

    @Setter
    private int contentLength;

    @Formula("length(content)")
    public int getContentLength() {
        return this.contentLength;
    }

	public Sentence(String content, ProcessedUrl sourceUrl)
	{
		this.setContent(content);
        this.setSourceUrl(sourceUrl);
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

    @Column(name = "content", columnDefinition="TEXT")
    public String getContent() {
		return content;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "URL_ID")
    public ProcessedUrl getSourceUrl() {
        return sourceUrl;
    }

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "sentences")
    public Set<Word> getWords() {
        return this.words;
    }
}
