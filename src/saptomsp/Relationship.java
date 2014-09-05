
package saptomsp;

import java.util.Objects;

/**
 *
 * @author rapnik
 */
public class Relationship {
    private final SapTaskId predecessor;
    private final SapTaskId successor;
    private final String type; 
    private final String duration_unit;
    private final Double duration;
    private final String duration_unit_iso;

    public Relationship(SapTaskId predecessor, SapTaskId successor, String type, String duration_unit, Double duration, String duration_unit_iso) {
        this.predecessor= predecessor;
        this.successor = successor;
        this.duration=duration;
        this.duration_unit=duration_unit;
        this.duration_unit_iso=duration_unit_iso;
        this.type=type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.getPredecessor());
        hash = 83 * hash + Objects.hashCode(this.getSuccessor());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relationship other = (Relationship) obj;
        if (!Objects.equals(this.predecessor, other.predecessor)) {
            return false;
        }
        if (!Objects.equals(this.successor, other.successor)) {
            return false;
        }
        return true;
    }

    /**
     * @return the predecessor
     */
    public SapTaskId getPredecessor() {
        return predecessor;
    }

    /**
     * @return the successor
     */
    public SapTaskId getSuccessor() {
        return successor;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the duration_unit
     */
    public String getDuration_unit() {
        return duration_unit;
    }

    /**
     * @return the duration
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * @return the duration_unit_iso
     */
    public String getDuration_unit_iso() {
        return duration_unit_iso;
    }
    
}
