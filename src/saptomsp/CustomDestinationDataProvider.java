
package saptomsp;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomDestinationDataProvider
        implements DestinationDataProvider {

    Map<String, Properties> propertiesForDestinationName = new HashMap();

    public void addDestination(String destinationName, Properties properties) {
        propertiesForDestinationName.put(destinationName, properties);
    }

    @Override
    public Properties getDestinationProperties(String destinationName) {
        if (propertiesForDestinationName.containsKey(destinationName)) {
            return propertiesForDestinationName.get(destinationName);
        } else {
            throw new RuntimeException("JCo destination not found: " + destinationName);
        }
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
// nothing to do
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }
}
