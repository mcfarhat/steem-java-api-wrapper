package eu.bittrade.libs.steemj.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.database.models.WitnessSchedule;

public class TestGetWitnessSchedule extends BaseIT {

    @BeforeClass
    public static void beforeClass() {
        setupIntegrationTestEnvironment();
    }

    // =================== REPLACE THE OLD @Test METHOD WITH THIS ===================
    @Test
public void testGetWitnessSchedule() throws SteemCommunicationException, SteemResponseException {
    // Execute the API call. No parameters are needed.
    WitnessSchedule witnessSchedule = steemJ.getWitnessSchedule();

    // Verify that we received a valid object.
    assertNotNull("The returned witness schedule should not be null.", witnessSchedule);
    
    // Perform some logical checks to ensure we received real data.
    assertTrue("The ID should be 0.", witnessSchedule.getId() == 0);
    assertNotNull("The current virtual time should not be null.", witnessSchedule.getCurrentVirtualTime());
    
    // THIS IS THE CORRECTED TEST: We are now checking the size of the List.
    assertNotNull("The shuffled witnesses list should not be null.", witnessSchedule.getCurrentShuffledWitnesses());
    assertTrue("There should be more than 20 witnesses in the schedule.", witnessSchedule.getCurrentShuffledWitnesses().size() > 20);

    // Print some of the fetched data to the console for visual confirmation.
    System.out.println("Success! Successfully fetched the witness schedule.");
    System.out.println("Number of shuffled witnesses: " + witnessSchedule.getCurrentShuffledWitnesses().size());
}
}