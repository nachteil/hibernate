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
public class Word {

    private String content;
    @Setter private Set<Sentence> sentences = new LinkedHashSet<>();

    public Word(String content) {
        this.content = content;
    }

    @Id
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "word_sentence",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "sentence_id"))
    public Set<Sentence> getSentences() {
        return sentences;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!this.getClass().isInstance(o)) {
            return false;
        }

        Word that = (Word) o;
        return that.content.equals(this.content);
    }

    @Override
    public int hashCode() {
        if(this.content == null) {
            return 0;
        } else {
            return content.hashCode();
        }
    }
}
