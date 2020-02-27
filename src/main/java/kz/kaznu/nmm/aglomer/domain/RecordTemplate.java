package kz.kaznu.nmm.aglomer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import kz.kaznu.nmm.aglomer.domain.enumeration.Language;

/**
 * A RecordTemplate.
 */
@Entity
@Table(name = "record_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recordtemplate")
public class RecordTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 99)
    @Column(name = "name", length = 99, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @OneToMany(mappedBy = "template")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RecordField> recordTemplateRecordFields = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("recordGroupRecordTemplates")
    private RecordGroup group;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public RecordTemplate name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return created;
    }

    public RecordTemplate created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public RecordTemplate updated(Instant updated) {
        this.updated = updated;
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Language getLanguage() {
        return language;
    }

    public RecordTemplate language(Language language) {
        this.language = language;
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Set<RecordField> getRecordTemplateRecordFields() {
        return recordTemplateRecordFields;
    }

    public RecordTemplate recordTemplateRecordFields(Set<RecordField> recordFields) {
        this.recordTemplateRecordFields = recordFields;
        return this;
    }

    public RecordTemplate addRecordTemplateRecordField(RecordField recordField) {
        this.recordTemplateRecordFields.add(recordField);
        recordField.setTemplate(this);
        return this;
    }

    public RecordTemplate removeRecordTemplateRecordField(RecordField recordField) {
        this.recordTemplateRecordFields.remove(recordField);
        recordField.setTemplate(null);
        return this;
    }

    public void setRecordTemplateRecordFields(Set<RecordField> recordFields) {
        this.recordTemplateRecordFields = recordFields;
    }

    public RecordGroup getGroup() {
        return group;
    }

    public RecordTemplate group(RecordGroup recordGroup) {
        this.group = recordGroup;
        return this;
    }

    public void setGroup(RecordGroup recordGroup) {
        this.group = recordGroup;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecordTemplate)) {
            return false;
        }
        return id != null && id.equals(((RecordTemplate) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "RecordTemplate{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
