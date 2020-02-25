package kz.kaznu.nmm.aglomer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Record.
 */
@Entity
@Table(name = "record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "record")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "code", precision = 21, scale = 2, nullable = false)
    private BigDecimal code;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @OneToMany(mappedBy = "record")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RecordValue> recordRecordValues = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCode() {
        return code;
    }

    public Record code(BigDecimal code) {
        this.code = code;
        return this;
    }

    public void setCode(BigDecimal code) {
        this.code = code;
    }

    public Instant getCreated() {
        return created;
    }

    public Record created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public Record updated(Instant updated) {
        this.updated = updated;
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Set<RecordValue> getRecordRecordValues() {
        return recordRecordValues;
    }

    public Record recordRecordValues(Set<RecordValue> recordValues) {
        this.recordRecordValues = recordValues;
        return this;
    }

    public Record addRecordRecordValue(RecordValue recordValue) {
        this.recordRecordValues.add(recordValue);
        recordValue.setRecord(this);
        return this;
    }

    public Record removeRecordRecordValue(RecordValue recordValue) {
        this.recordRecordValues.remove(recordValue);
        recordValue.setRecord(null);
        return this;
    }

    public void setRecordRecordValues(Set<RecordValue> recordValues) {
        this.recordRecordValues = recordValues;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }
        return id != null && id.equals(((Record) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Record{" +
            "id=" + getId() +
            ", code=" + getCode() +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
