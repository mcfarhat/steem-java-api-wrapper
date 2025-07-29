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
package eu.bittrade.libs.steemj.plugins.apis.account.history;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import org.joou.UInteger;
import org.joou.ULong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.bittrade.libs.steemj.BaseIT;
import eu.bittrade.libs.steemj.IntegrationTest;
import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetAccountHistoryArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockReturn;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.OperationHistoryEntry;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.AnnotatedSignedTransaction;
import eu.bittrade.libs.steemj.protocol.TransactionId;
import eu.bittrade.libs.steemj.protocol.operations.AccountCreateOperation;
import eu.bittrade.libs.steemj.protocol.operations.Operation;
import eu.bittrade.libs.steemj.protocol.operations.VoteOperation;

/**
 * This class contains all tests connected to the updated Hive
 * {@link eu.bittrade.libs.steemj.plugins.apis.account.history.AccountHistoryApi
 * AccountHistoryApi}.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class AccountHistoryApiIT extends BaseIT {
    private static CommunicationHandler COMMUNICATION_HANDLER;

    /**
     * Setup the test environment.
     */
    @BeforeClass
    public static void init() throws SteemCommunicationException {
        setupIntegrationTestEnvironment();
        COMMUNICATION_HANDLER = new CommunicationHandler();
    }

    /**
     * Test the {@link AccountHistoryApi#getOpsInBlock} method. This test verifies
     * that the API call works, the custom deserializer correctly unwraps the 'op'
     * object, and the data models handle the response structure.
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

        // Call the new API method.
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

        // The 'virtual_op' field from get_ops_in_block is a number (0 or 1).
        // Our updated AppliedOperation model handles this.
        assertFalse("This should be a real operation, not a virtual one.", firstOp.isVirtualOp());

        // Check the actual operation type. This verifies the custom deserializer is
        // correctly unwrapping the 'op' object.
        assertThat("The operation should be a VoteOperation.", firstOp.getOp(), instanceOf(VoteOperation.class));
    }

      /**
     * Test the corrected {@link SteemJ#getTransaction(TransactionId)} method.
     * This test validates that the method can fetch a real transaction from the
     * Hive blockchain using the condenser_api.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetTransaction() throws SteemCommunicationException, SteemResponseException {
        // This is a known, irreversible transaction on the Hive blockchain.
        final String transactionIdString = "0c4f420b7a1ff5201b10626353982e56360b3781";
        // This is the CORRECT block number for the transaction above on Hive.
        final long expectedBlockNum = 58734311L; 

        // 1. Create the required TransactionId object for our new method.
        final TransactionId transactionToFetch = new TransactionId(transactionIdString);

        // 2. Call the new method on an instance of the SteemJ class.
        //    (Assuming your test class has a 'steemJ' object initialized).
        final AnnotatedSignedTransaction transactionDetails = steemJ.getTransaction(transactionToFetch);

        // 3. Assert the results are correct.
        assertThat("The returned transaction should not be null.", transactionDetails, notNullValue());
        
        // Assert against the correct block number.
        assertThat("The block number should match the known value for this transaction.",
                transactionDetails.getBlockNum(), equalTo(expectedBlockNum));
                
        // Assert that the returned transaction ID matches the one we requested.
        // We use .getHash() which is the correct method from the Ripemd160 parent class.
        assertThat("The transaction ID in the response should match the requested ID.",
                transactionDetails.getTransactionId().getHashValue(), equalTo(transactionIdString));
    }

    /**
     * Test the updated {@link AccountHistoryApi#getAccountHistory} method,
     * verifying the new List-based response structure.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetAccountHistory() throws SteemCommunicationException, SteemResponseException {
        AccountName testAccount = new AccountName("dez1337");
        ULong start = ULong.valueOf(10);
        UInteger limit = UInteger.valueOf(10); // Request 10 items

        // The method now returns GetAccountHistoryReturn which contains a List.
        final List<OperationHistoryEntry> historyList = AccountHistoryApi
                .getAccountHistory(COMMUNICATION_HANDLER, new GetAccountHistoryArgs(testAccount, start, limit))
                .getHistory();

        assertThat("The history list should not be null.", historyList, notNullValue());
        assertEquals("Expected response to contain 10 results for limit=10.", 10, historyList.size());

        // Access the operation using the new object model.
        Operation firstOperationInList = historyList.get(0).getOperation().getOp();

        // The first operation in an account's history (at index 0) is its creation.
        // As we started from index 0 (by using start=10 and getting 10 results, index
        // 0 is first), we check this.
        if (historyList.get(0).getHistoryIndex() == 0) {
            assertTrue("The first operation for an account should be 'account_create_operation'",
                    firstOperationInList instanceof AccountCreateOperation);
        }
    }

    /**
     * Test the new filtering capabilities of the
     * {@link AccountHistoryApi#getAccountHistory} method.
     */
    @Category({ IntegrationTest.class })
    @Test
    public void testGetAccountHistoryWithFilters() throws SteemCommunicationException, SteemResponseException {
        AccountName testAccount = new AccountName("blocktrades"); // An account with a lot of history
        ULong start = ULong.valueOf(-1); // Start from the most recent
        UInteger limit = UInteger.valueOf(100);

        // Filter for VOTE operations only. Vote operation has ID 0, so its bitmask is
        // 2^0 = 1.
        Long operationFilterLow = 1L;
        Long operationFilterHigh = 0L;

        GetAccountHistoryArgs args = new GetAccountHistoryArgs(testAccount, start, limit, true, operationFilterLow,
                operationFilterHigh);

        final List<OperationHistoryEntry> historyList = AccountHistoryApi
                .getAccountHistory(COMMUNICATION_HANDLER, args).getHistory();

        assertFalse("The filtered list of votes should not be empty for an active account.", historyList.isEmpty());

        // Verify that EVERY operation in the response is a VoteOperation, proving the
        // filter works.
        for (OperationHistoryEntry entry : historyList) {
            assertThat("Each operation in the filtered list should be a VoteOperation.", entry.getOperation().getOp(),
                    instanceOf(VoteOperation.class));
        }
    }
}