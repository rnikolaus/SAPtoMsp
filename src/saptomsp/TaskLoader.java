
package saptomsp;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author rapnik
 */
public class TaskLoader {
     SapUtil sap = new SapUtil();
      Map<SapTaskId, SapTask> tasks;
     

    public TaskLoader() {
        sap.connect();
    }
     
    public synchronized Collection<SapTask> loadTasks(String netzplanId) throws JCoException {

        
        
        Map<String, Object> properties = new HashMap<>();

        properties.put("NUMBER", netzplanId);
        JCoFunction function = sap.runFunction("BAPI_NETWORK_GETDETAIL", properties);
        tasks.clear();
        tasks.putAll( getActivities(function));
        getPredecessors(function);
        return new TreeSet(tasks.values());
    }

    private static Map<SapTaskId, SapTask> getActivities(JCoFunction function) {
        HashMap<SapTaskId, SapTask> result = new HashMap<>();
        JCoTable table = function.getTableParameterList().getTable("E_ACTIVITY");
        for (int rowIndex = 0; rowIndex < table.getNumRows(); rowIndex++) {
            table.setRow(rowIndex);

            String network = table.getString("NETWORK");
            String sapId = table.getString("ACTIVITY");
            String description = table.getString("DESCRIPTION");
            String matlgroup = table.getString("MATL_GROUP");
            String psp = table.getString("WBS_ELEMENT");
            Date planStart = table.getDate("EARLY_START_DATE");
            Date planFinish = table.getDate("EARLY_FINISH_DATE");
             Date planStartTime = table.getDate("EARLY_START_TIME");
            Date planFinishTime = table.getDate("EARLY_FINISH_TIME");
            Date actualStart = table.getDate("ACTUAL_START_DATE");
            Date actualFinish = table.getDate("ACTUAL_FINISH_DATE");
            Double duration = table.getDouble("DURATION_NORMAL");
            String duration_unit_iso = table.getString("DURATION_NORMAL_UNIT_ISO");
            SapTaskId taskId = new SapTaskId(network, sapId);
            SapTask task = new SapTask(network, sapId, matlgroup, psp, description,
                    SapUtil.combineDateAndTime(planStart, planStartTime), 
                    SapUtil.combineDateAndTime(planFinish, planFinishTime), 
                    actualStart, actualFinish, duration, duration_unit_iso);
            result.put(taskId, task);
        }
        return result;
    }

    private  void getPredecessors(JCoFunction function) {
        JCoTable table = function.getTableParameterList().getTable("E_ACTIVITY");
        for (int rowIndex = 0; rowIndex < table.getNumRows(); rowIndex++) {
            table.setRow(rowIndex);
            String network_pre = table.getString("NETWORK_PREDECESSOR");
            String sapId_pre = table.getString("ACTIVITY_PREDECESSOR");
            String network_suc = table.getString("NETWORK_SUCCESSOR");
            String sapId_suc = table.getString("ACTIVITY_SUCCESSOR");
            String type = table.getString("RELATION_TYPE");//e.g. FS
            String duration_unit = table.getString("DURATION_RELATION_UNIT");
            Double duration = table.getDouble("DURATION_RELATION");
            String duration_unit_iso = table.getString("DURATION_RELATION_UNIT_ISO");
            SapTaskId predecessor = new SapTaskId(network_pre, sapId_pre);
            SapTaskId successor = new SapTaskId(network_suc, sapId_suc);

            Relationship relationship = new Relationship(predecessor, successor, type, duration_unit, duration, duration_unit_iso);
            
            if (tasks.containsKey(successor)) {
                tasks.get(successor).addPredecessor(relationship);
            }

        }
        

    }
    
}
