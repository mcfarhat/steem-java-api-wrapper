package eu.bittrade.libs.steemj.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.protocol.AccountName;

public class TestGetActiveWitnesses extends BaseIT {

    @BeforeClass
    public static void beforeClass() {
        setupIntegrationTestEnvironment();
    }

    @Test
    public void testGetActiveWitnesses() throws SteemCommunicationException, SteemResponseException {
        // Execute the API call. No parameters are needed.
        List<AccountName> activeWitnesses = steemJ.getActiveWitnesses();

        // Verify that we received a valid, non-empty list.
        assertNotNull("The returned list of witnesses should not be null.", activeWitnesses);
        assertTrue("The list of witnesses should not be empty.", !activeWitnesses.isEmpty());
        
        // A simple check on the first witness in the list.
        assertNotNull("The first witness account name object should not be null.", activeWitnesses.get(0));
        assertNotNull("The first witness account name string should not be null.", activeWitnesses.get(0).getName());

        // Print some of the fetched data to the console for visual confirmation.
        System.out.println("Success! Successfully fetched the active witnesses.");
        System.out.println("Total active witnesses found: " + activeWitnesses.size());
        System.out.println("Top witness: " + activeWitnesses.get(0).getName());
    }
}