
package saptomsp;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.util.SyncDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author rapnik
 */
public class SapUtil {

    private static final SyncDateFormat dateISO = new SyncDateFormat("yyyy-MM-dd");
    private static final SyncDateFormat timeISO = new SyncDateFormat("HH:mm:ss");
    private static final SyncDateFormat dateTimeISO = new SyncDateFormat("yyyy-MM-ddHH:mm:ss");
    static String DESTINATION = "destination";
    private JCoDestination destination;
    private JCoRepository repository;
    private final Properties connectProperties;

    public SapUtil() {
        connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, "host");
        connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "XYZ");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "001");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "user");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "passwd");
        connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, "group");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
    }

    public SapUtil(Properties connectProperties) {
        this.connectProperties = connectProperties;
    }

    public void connect() {
        CustomDestinationDataProvider provider = new CustomDestinationDataProvider();
        provider.addDestination(DESTINATION, connectProperties);
        com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(provider);
        try {
            destination = JCoDestinationManager.getDestination(DESTINATION);
            System.out.println("Attributes:");
            System.out.println(destination.getAttributes());
            System.out.println();
            destination.ping();
            repository = destination.getRepository(); //repository should be reused for performance reasons
        } catch (JCoException ex) {
            throw new RuntimeException(ex);
        }

    }

    public JCoDestination getDestination() {
        return destination;
    }

    public JCoFunction getFunction(String name) {
        try {
            return repository.getFunction(name);
        } catch (JCoException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JCoFunction runFunction(String functionName, Map<String, Object> parameters) throws JCoException {
        JCoFunction function = getFunction(functionName);
        if (function == null) {
            throw new RuntimeException(functionName + " not found in SAP.");
        }
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            function.getImportParameterList().setValue(parameter.getKey(), parameter.getValue());
        }

        try {
            function.execute(destination);
        } catch (AbapException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
        JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
        if (!(returnStructure.getString("TYPE").equals("") || returnStructure.getString("TYPE").equals("S"))) {
            final String message = returnStructure.getString("MESSAGE");
            final String type = returnStructure.getString("TYPE");
            final String number = returnStructure.getString("NUMBER");
            final String id = returnStructure.getString("ID");

            throw new RuntimeException("Fehler id: " + id + " Number " + number + " type: " + type + " Message: " + message);
        }
        return function;
    }

    public static Date combineDateAndTime(Date date, Date time) {
        try {
            return dateTimeISO.parse(dateISO.format(date) + timeISO.format(time));
        } catch (ParseException ex) {

            throw new RuntimeException(ex);
        }
    }

    public static void printFieldsForFunction(JCoFunction function) {
        if (function == null) {
            throw new RuntimeException("function is null");
        }
        System.out.println("Name: " + function.getName());
        final JCoParameterList importParameterList = function.getImportParameterList();

        if (importParameterList != null) {
            printFieldIter("Inputs", importParameterList.getParameterFieldIterator());
        }
        final JCoParameterList exportParameterList = function.getExportParameterList();
        if (exportParameterList != null) {
            printFieldIter("Outputs", exportParameterList.getParameterFieldIterator());
        }
        final JCoParameterList tableParameterList = function.getTableParameterList();
        if (tableParameterList != null) {
            printFieldIter("Tables", tableParameterList.getParameterFieldIterator());
        }
    }

    public static void printFieldIter(String header, JCoFieldIterator fieldIter) {
        System.out.println(header + ":");
        String name = "Name";
        String type = "Type";
        String desc = "Description";
        System.out.println(name + "|" + type + "|" + desc);
        while (fieldIter.hasNextField()) {
            JCoField field = fieldIter.nextField();
            name = field.getName();
            type = field.getTypeAsString();
            desc = field.getDescription();
            if (desc == null) {
                desc = "";
            }
            System.out.println(name + "|" + type + "|" + desc);
        }
        System.out.println();
    }

    public static void printTableFields(JCoTable table) {
        printFieldIter("Table Fields", table.getRecordFieldIterator());
        for (int fieldIndex = 0; fieldIndex < table.getNumColumns(); fieldIndex++) {
            System.out.print(table.getMetaData().getName(fieldIndex) + "|");
        }
        System.out.println();
        for (int rowIndex = 0; rowIndex < table.getNumRows(); rowIndex++) {
            table.setRow(rowIndex);
            for (int fieldIndex = 0; fieldIndex < table.getNumColumns(); fieldIndex++) {
                System.out.print(table.getValue(fieldIndex) + "|");
            }
            System.out.println();
        }
        table.firstRow();
    }

    public static void printTableFields(JCoTable table, List<String> values) {
        for (String id : values) {
            System.out.print(id + "|");
        }

        System.out.println();
        for (int rowIndex = 0; rowIndex < table.getNumRows(); rowIndex++) {
            table.setRow(rowIndex);

            for (String id : values) {
                System.out.print(table.getValue(id) + "|");
            }
            System.out.println();
        }
        table.firstRow();
    }

    public void printTableFromFunction(String functionName, String tableName, Map<String, Object> parameters) throws JCoException {

        JCoFunction function = runFunction(functionName, parameters);
        JCoTable codes = function.getTableParameterList().getTable(tableName);
        printTableFields(codes);
    }

    public void getStructureFromFunction(String functionName, Map<String, Object> parameters, String structureName) throws JCoException {
        JCoFunction function = runFunction(functionName, parameters);
        JCoStructure detail = function.getExportParameterList().getStructure(structureName);
        printFieldIter("Fields", detail.getFieldIterator());
    }

}
