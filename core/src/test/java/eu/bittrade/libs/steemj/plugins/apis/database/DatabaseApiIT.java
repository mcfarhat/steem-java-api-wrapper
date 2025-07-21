/*
 *     This file is part of SteemJ (formerly known as 'Steem-Java-Api-Wrapper')
 * 
 *     SteemJ is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SteemJ is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SteemJ.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.bittrade.libs.steemj.plugins.apis.database;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.joou.UInteger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.IntegrationTest;
import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.account.history.AccountHistoryApi;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockReturn;
import eu.bittrade.libs.steemj.plugins.apis.database.models.DynamicGlobalProperty;
import eu.bittrade.libs.steemj.plugins.apis.database.models.HardforkProperty;
import eu.bittrade.libs.steemj.plugins.apis.tags.TagsApi;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.Tag;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.enums.LegacyAssetSymbolType;
import eu.bittrade.libs.steemj.protocol.operations.VoteOperation;

/**
 * This class contains all test connected to the
 * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi
 * DatabaseApi}.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class DatabaseApiIT extends BaseIT {
    private static CommunicationHandler COMMUNICATION_HANDLER;

    /**
     * Setup the test environment.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     */
    @BeforeClass
    public static void init() throws SteemCommunicationException {
        setupIntegrationTestEnvironment();

        COMMUNICATION_HANDLER = new CommunicationHandler();
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getHardforkProperties(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetHardforkProperties() throws SteemCommunicationException, SteemResponseException {
        HardforkProperty hardforkProperty = DatabaseApi.getHardforkProperties(COMMUNICATION_HANDLER);
        assertThat(hardforkProperty.getCurrentHardforkVersion().toString(), matchesPattern("^[0-9]+\\.[0-9]+\\.[0-9]+"));
        assertThat(hardforkProperty.getId(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getLastHardfork().longValue(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getNextHardfork().toString(), matchesPattern("^[0-9]+\\.[0-9]+\\.[0-9]+"));
        assertThat(hardforkProperty.getNextHardforkTime().getDateTimeAsTimestamp(), greaterThanOrEqualTo(0L));
        assertThat(hardforkProperty.getProcessedHardforks().size(), greaterThan(19));
        assertThat(hardforkProperty.getProcessedHardforks().get(0).getDateTimeAsTimestamp(), equalTo(1458835200000L));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getDynamicGlobalProperties(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetDynamicGlobalProperties() throws SteemCommunicationException, SteemResponseException {
        DynamicGlobalProperty dynamicGlobalProperty = DatabaseApi.getDynamicGlobalProperties(COMMUNICATION_HANDLER);

        // TODO: Test all fields.
        assertThat(dynamicGlobalProperty.getCurrentSdbSupply().getAmount(), greaterThan(10000L));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getTrendingTags(CommunicationHandler, String, int)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetTrendingTags() throws SteemCommunicationException, SteemResponseException {
        final String REQUESTED_TAG = "steemit";

        final List<Tag> trendingTags = TagsApi.getTrendingTags(COMMUNICATION_HANDLER, REQUESTED_TAG, 2);

        assertNotNull(trendingTags);
        assertThat(trendingTags.size(), greaterThan(0));
        assertTrue(trendingTags.get(0).getName().equals(REQUESTED_TAG));
        // assertThat(trendingTags.get(0).getComments(), greaterThan(0L));
        // assertThat(trendingTags.get(0).getNetVotes(), greaterThan(0L));
        // assertThat(trendingTags.get(0).getTopPosts(), greaterThan(0L));
        // seems that payout asset report has changed
        // assertThat(trendingTags.get(0).getTotalPayouts().getSymbol(),
        // equalTo(AssetSymbolType.VESTS));
        assertThat(trendingTags.get(0).getTotalPayouts().getSymbol(), equalTo(LegacyAssetSymbolType.SBD));
        assertThat(trendingTags.get(0).getTotalPayouts().getAmount(), greaterThan(0L));
        assertThat(trendingTags.get(0).getTrending().intValue(), greaterThan(0));
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getState(CommunicationHandler, eu.bittrade.libs.steemj.base.models.Permlink)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetState() throws SteemCommunicationException, SteemResponseException {
        // TODO: Implement.
    }

    /**
     * Test the
     * {@link eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi#getActiveWitnesses(CommunicationHandler)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetActiveWitnesses() throws SteemCommunicationException, SteemResponseException {
        final List<AccountName> activeWitnesses = DatabaseApi.getActiveWitnesses(COMMUNICATION_HANDLER);

        // The active witness changes from time to time, so we just check if
        // something is returned.
        assertThat(activeWitnesses.size(), greaterThan(0));
        assertThat(activeWitnesses.get(0).getName(), not(emptyOrNullString()));
    }

    /**
     * Test the
     * {@link AccountHistoryApi#getOpsInBlock(CommunicationHandler, GetOpsInBlockArgs)}
     * method.
     * 
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the response is an error.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetOpsInBlock() throws SteemCommunicationException, SteemResponseException {
        // A known block number with a transaction.
        final long blockNum = 1000L;
        // The known transaction ID in block 1000.
        final String expectedTrxId = "0c4f420b7a1ff5201b10626353982e56360b3781";

        // Request all operations (not just virtual ones).
        final GetOpsInBlockArgs args = new GetOpsInBlockArgs(blockNum, false);

        // Call the API method from the correct Api class.
        final GetOpsInBlockReturn result = AccountHistoryApi.getOpsInBlock(COMMUNICATION_HANDLER, args);

        // Assertions
        assertThat("The result object should not be null.", result, notNullValue());

        List<AppliedOperation> operations = result.getOperations();
        assertThat("The list of operations should not be null.", operations, notNullValue());
        assertFalse("The list of operations for a known block should not be empty.", operations.isEmpty());

        // Examine the first operation in the block.
        AppliedOperation firstOp = operations.get(0);
        assertThat("The block number in the operation should match the requested block number.", firstOp.getBlock(),
                equalTo(UInteger.valueOf(blockNum)));
        assertThat("The transaction ID should match the known value for this block.",
                firstOp.getTrxId().getHashValue().toString(), equalTo(expectedTrxId));
        assertFalse("This should be a real operation, not a virtual one.", firstOp.isVirtualOp());

        // Check the actual operation type. This verifies the custom deserializer is
        // correctly unwrapping the 'op' object.
        assertThat("The operation should be a VoteOperation.", firstOp.getOp(), instanceOf(VoteOperation.class));
    }
}