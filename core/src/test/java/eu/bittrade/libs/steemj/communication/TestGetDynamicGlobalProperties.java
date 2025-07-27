// =================== COPY AND PASTE THIS ENTIRE FILE ===================
package eu.bittrade.libs.steemj.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.database.models.DynamicGlobalProperty;

public class TestGetDynamicGlobalProperties extends BaseIT {

    @BeforeClass
    public static void beforeClass() {
        setupIntegrationTestEnvironment();
    }

    @Test
    public void testGetDynamicGlobalProperties() throws SteemCommunicationException, SteemResponseException {
        // Execute the API call. No parameters are needed.
        DynamicGlobalProperty properties = steemJ.getDynamicGlobalProperties();

        // Verify that we received a valid object.
        assertNotNull("The returned properties object should not be null.", properties);
        
        // Perform logical checks on the data to confirm it's real.
        // The syntax for JUnit assertions is (String message, boolean condition).
assertTrue("Head block number should be a positive number.", properties.getHeadBlockNumber().longValue() > 0);
        assertNotNull("The head block ID should not be null.", properties.getHeadBlockId());
        assertNotNull("The time should not be null.", properties.getTime());
        // We need to get the primitive long value from the TotalVestingShares object before comparing it.
        assertTrue("Total vesting shares should be greater than 0.", properties.getTotalVestingShares().getAmount() > 0);

        // Print some of the fetched data to the console for visual confirmation.
        System.out.println("Success! Successfully fetched dynamic global properties.");
        System.out.println("Head Block Number: " + properties.getHeadBlockNumber());
        System.out.println("Current Witness: " + properties.getCurrentWitness().getName());
    }
}