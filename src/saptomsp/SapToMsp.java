package saptomsp;

import com.sap.conn.jco.JCoException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.writer.ProjectWriter;

/**
 *
 *
 */
public class SapToMsp {

    public static void main(String[] args) throws JCoException, IOException {
        TaskLoader loader = new TaskLoader();
        
        final Collection<SapTask> tasks = loader.loadTasks("00001");
        writeMsXml(tasks,"example.xml");

    }

    public static void writeMsXml(final Collection<SapTask> tasks, String filename)  {
        try {
            ProjectFile project = new ProjectFile();
            Map<SapTaskId, Task> match = new HashMap<>();
            for (SapTask task : tasks) {
                Task mspTask = project.addTask();
                mspTask.setID(Integer.parseInt(task.getSapId()));
                mspTask.setName(task.getDescription());
                mspTask.setStart(task.getPlanStart());
                mspTask.setFinish(task.getPlanFinish());
                mspTask.setActualStart(task.getActualStart());
                mspTask.setActualFinish(task.getActualFinish());
                match.put(task, mspTask);
            }
            for (SapTask task : tasks) {
                for (Relationship r : task.getRelationships()) {
                    SapTaskId predid = r.getPredecessor();
                    RelationType relationType = null;
                    switch (r.getType()) {
                        case "SF":
                            relationType = RelationType.START_FINISH;
                            break;
                        case "FF":
                            relationType = RelationType.FINISH_FINISH;
                            break;
                        case "FS":
                            relationType = RelationType.FINISH_START;
                            break;
                        case "SS":
                            relationType = RelationType.START_START;
                            break;
                        default:
                            throw new RuntimeException("unknown relationship type " + r.getType());
                            
                    }
                    // TODO read lag unit from relationship
                    final TimeUnit timeunit = TimeUnit.DAYS;
                    match.get(task).addPredecessor(match.get(predid), relationType, Duration.getInstance(r.getDuration(), timeunit));
                }
            }
            ProjectWriter writer = new MSPDIWriter();
            
            writer.write(project, filename);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
