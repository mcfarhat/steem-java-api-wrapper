package eu.bittrade.libs.steemj.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Map; // <-- IMPORTANT IMPORT
import org.junit.BeforeClass;
import org.junit.Test;
import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.SteemJ; // <-- IMPORTANT IMPORT FOR STATIC CALL
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class TestGetConfig extends BaseIT {

    @BeforeClass
    public static void beforeClass() {
        // This is only needed if you call non-static methods like steemJ.get...
        // For static methods like SteemJ.getConfig(), it's not strictly necessary,
        // but it's good practice to keep it.
        setupIntegrationTestEnvironment();
    }

    @Test
    public void testGetConfig() throws SteemCommunicationException, SteemResponseException {
        // ### EXECUTE THE CALL ###
        // The method is static, so we call it on the class SteemJ directly.
        // It now correctly returns a Map.
        Map<String, Object> configMap = SteemJ.getConfig();

        // ### VERIFY THE RESULTS ###
        assertNotNull("The returned config map should not be null.", configMap);

        // Check for a key that is specific to Hive.
        assertTrue("Config map should contain the HIVE_CHAIN_ID key.", configMap.containsKey("HIVE_CHAIN_ID"));
        
        // Additional sanity checks.
        assertTrue("Config map should contain HIVE_BLOCKCHAIN_HARDFORK_VERSION.", configMap.containsKey("HIVE_BLOCKCHAIN_HARDFORK_VERSION"));
        assertTrue("Config map should contain HBD_SYMBOL.", configMap.containsKey("HBD_SYMBOL"));

        // Log a success message.
        System.out.println("Success! Successfully fetched blockchain config.");
        System.out.println("Chain ID: " + configMap.get("HIVE_CHAIN_ID"));
        System.out.println("Hardfork Version: " + configMap.get("HIVE_BLOCKCHAIN_HARDFORK_VERSION"));
    }
}