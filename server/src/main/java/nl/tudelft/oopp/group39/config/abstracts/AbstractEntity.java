package nl.tudelft.oopp.group39.config.abstracts;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = AbstractEntity.COL_ID
)
public abstract class AbstractEntity<E extends AbstractEntity<E, D>, D extends IEntity>
    implements Serializable, IEntity {

    public static final String COL_ID = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Changes the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Changes an entity to a Dto object.
     *
     * @return a Dto object
     */
    public abstract D toDto();
}
