

package saptomsp;

import java.util.Objects;

/**
 *
 * @author rapnik
 */
public class SapTaskId implements Comparable<SapTaskId>{
    private final String network;
    private final String sapId; 

    public SapTaskId(String network, String sapId) {
        this.network = network;
        this.sapId = sapId;
    }

    /**
     * @return the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @return the sapId
     */
    public String getSapId() {
        return sapId;
    }

    @Override
    public int compareTo(SapTaskId o) {
        int result = this.getNetwork().compareTo(o.getNetwork());
        if (result!=0)return result;
        return this.getSapId().compareTo(o.getSapId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.network);
        hash = 37 * hash + Objects.hashCode(this.sapId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SapTaskId)) {
            return false;
        }
        final SapTaskId other = (SapTaskId) obj;
        if (!Objects.equals(this.network, other.network)) {
            return false;
        }
        if (!Objects.equals(this.sapId, other.sapId)) {
            return false;
        }
        return true;
    }
    
}
