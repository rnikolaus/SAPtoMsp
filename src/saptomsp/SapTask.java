

package saptomsp;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rapnik
 */
public class SapTask extends SapTaskId{
    private final String description;
    private final String matlgroup; 
    private final String psp; 
    private final Date planStart; 
    private final Date planFinish;
    private final Date actualStart;
    private final Date actualFinish;
    private final Double duration; 
    private final String duration_unit_iso;
    private Set<Relationship> relationships = new HashSet<>();

    public SapTask(String network, String sapId, String matlgroup, String psp, String description, Date planStart, Date planFinish, Date actualStart, Date actualFinish, Double duration, String duration_unit_iso) {
        super(network, sapId);
        this.description = description;
        this.actualFinish = actualFinish;
        this.actualStart = actualStart;
        this.duration = duration;
        this.duration_unit_iso=duration_unit_iso;
        this.matlgroup=matlgroup;
        this.planFinish=planFinish;
        this.planStart=planStart;
        this.psp=psp;
    }
    
    public void addPredecessor(Relationship relationship){
        if (relationship.getSuccessor().equals(this)){
            relationships.add(relationship);
        }else {
            throw new RuntimeException("Relationship is not a predecessor of this task");
        }
    }
    
    public Set<Relationship> getRelationships(){
        return Collections.unmodifiableSet(relationships);
    }

    public String getDescription(){
        return description;
    }
    

    /**
     * @return the matlgroup
     */
    public String getMatlgroup() {
        return matlgroup;
    }

    /**
     * @return the psp
     */
    public String getPsp() {
        return psp;
    }

    /**
     * @return the planStart
     */
    public Date getPlanStart() {
        return planStart;
    }

    /**
     * @return the planFinish
     */
    public Date getPlanFinish() {
        return planFinish;
    }

    /**
     * @return the actualStart
     */
    public Date getActualStart() {
        return actualStart;
    }

    /**
     * @return the actualFinish
     */
    public Date getActualFinish() {
        return actualFinish;
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
