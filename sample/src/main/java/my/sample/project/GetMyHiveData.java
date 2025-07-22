package my.sample.project; // Or eu.bittrade.libs.steemj.sample if that's your package

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.joou.UInteger;
import org.joou.ULong;
import org.slf4j.Logger; // Needed for getKeyReferences test
import org.slf4j.LoggerFactory;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.communication.jrpc.JsonRPCRequest;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.RequestMethod;
import eu.bittrade.libs.steemj.enums.SteemApiType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.OperationHistoryEntry;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedAccount;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.AnnotatedSignedTransaction;
import eu.bittrade.libs.steemj.protocol.PublicKey;
import eu.bittrade.libs.steemj.protocol.TransactionId;


public class GetMyHiveData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetMyHiveData.class);

    public static void main(String[] args) {
        LOGGER.info("Starting GetMyHiveData sample...");

        SteemJConfig myConfig = SteemJConfig.getInstance();
        myConfig.setResponseTimeout(100000); // Set a reasonable timeout

        String hiveNodeHttpUrl = "https://anyx.io"; // Using a reliable Hive node
        try {
            myConfig.getEndpointURIs().clear(); // Clear existing default URIs
            myConfig.addEndpointURI(new URI(hiveNodeHttpUrl));
            LOGGER.info("Configured endpoint URI: {}", hiveNodeHttpUrl);

            // Explicitly set Hive chain ID
            myConfig.setChainId("beeab0de00000000000000000000000000000000000000000000000000000000");
            LOGGER.info("Set Chain ID for Hive.");

        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException for node '{}': {}", hiveNodeHttpUrl, e.getMessage(), e);
            return; // Exit if URI is invalid
        }

        // Your Hive account name - REPLACE THIS IF NEEDED
        AccountName myHiveAccountName = new AccountName("omarghadban");
        myConfig.setDefaultAccount(myHiveAccountName);
        LOGGER.info("Default account set to: {}", myConfig.getDefaultAccount().getName());

        // Private keys are NOT needed for reading data, so we skip adding them.

        try {
            SteemJ steemJ = new SteemJ();
            LOGGER.info("SteemJ instance created.");

            // 1. Fetch Your Account Details (from previous successful test)
            LOGGER.info("Attempting to fetch account details for: {}", myHiveAccountName.getName());
            List<AccountName> accountsToFetch = new ArrayList<>();
            accountsToFetch.add(myHiveAccountName);

            List<ExtendedAccount> extendedAccounts = steemJ.getAccounts(accountsToFetch);

            if (extendedAccounts != null && !extendedAccounts.isEmpty()) {
                ExtendedAccount myAccount = extendedAccounts.get(0);
                LOGGER.info("Successfully fetched account details:");
                LOGGER.info("  Name: {}", myAccount.getName().getName());
                LOGGER.info("  Balance (HIVE): {}", myAccount.getBalance());
                LOGGER.info("  SBD/HBD Balance: {}", myAccount.getSbdBalance()); 
                LOGGER.info("  Vesting Shares (HP): {}", myAccount.getVestingShares());
            } else {
                LOGGER.warn("Could not retrieve account details for {}.", myHiveAccountName.getName());
            }


            // 2. <<< NEW SECTION FOR TESTING getKeyReferences >>>
            LOGGER.info("--------------------------------------------------------------------");
            LOGGER.info("Attempting to get key references for a public key...");

            try {
                // Use one of milk21's actual public keys.
                // Posting Key for "milk21" (from previous successful getAccounts response):
                String testPublicKeyString = "STM6oASGgigTTdn7L4jsdVF7jGRMe8avaDAQZMff7spgro6ov8caw"; 
                // (If "milk21" is not your account, or you want to test a different key, replace this string)
                
                PublicKey publicKeyToLookup = new PublicKey(testPublicKeyString);

                List<PublicKey> keysToLookup = new ArrayList<>();
                keysToLookup.add(publicKeyToLookup);

                LOGGER.info("Looking up accounts for public key: {}", testPublicKeyString);

                // Call the method we updated in SteemJ.java
                List<List<AccountName>> resultsForKeyReferences = steemJ.getKeyReferences(keysToLookup);

                if (resultsForKeyReferences != null && !resultsForKeyReferences.isEmpty()) {
                    LOGGER.info("Successfully fetched key references. Result(s):");
                    for (int i = 0; i < resultsForKeyReferences.size(); i++) {
                        List<AccountName> accountsForThisKey = resultsForKeyReferences.get(i);
                        // Use getAddressFromPublicKey() here:
                        String inputKeyString = (keysToLookup.get(i) != null) ? keysToLookup.get(i).getAddressFromPublicKey() : "N/A";
                        LOGGER.info("  For key {} (input index {}):", inputKeyString, i);
                        if (accountsForThisKey != null && !accountsForThisKey.isEmpty()) {
                            for (AccountName accountNameObj : accountsForThisKey) {
                                LOGGER.info("    - Found account: {}", accountNameObj.getName());
                            }
                        } else {
                            LOGGER.info("    (No accounts found associated with this specific key)");
                        }
                    }
                } else {
                    LOGGER.warn("Could not retrieve key references, or the result was empty for key(s): {}", keysToLookup);
                }

            } catch (SteemCommunicationException | SteemResponseException e) { 
                LOGGER.error("Error during getKeyReferences call: {}", e.getMessage(), e);
            } catch (Exception e) { 
                LOGGER.error("Unexpected error during getKeyReferences test: {}", e.getMessage(), e);
            }
            LOGGER.info("--------------------------------------------------------------------");
            // <<< END OF NEW SECTION for getKeyReferences >>>


                        // 3. <<< NEW SECTION FOR TESTING getAccountHistory >>>
            LOGGER.info("--------------------------------------------------------------------");
            LOGGER.info("Attempting to fetch account history...");

            try {
                // EXAMPLE 1: Get the 10 most recent operations for the account.
                // ULong.valueOf(-1) is the standard way to request the most recent history.
                ULong startFrom = ULong.valueOf(-1);
                UInteger limit = UInteger.valueOf(10);
                
                LOGGER.info("Fetching the {} most recent history items for '{}'...", limit, myHiveAccountName.getName());

                List<OperationHistoryEntry> recentHistory = steemJ.getAccountHistory(myHiveAccountName, startFrom, limit);

                if (recentHistory != null && !recentHistory.isEmpty()) {
                    LOGGER.info("Successfully fetched {} history items:", recentHistory.size());
                    for (OperationHistoryEntry entry : recentHistory) {
                        // Print some details for each operation found.
                        LOGGER.info("  - Index: {}, Timestamp: {}, Operation Type: {}",
                            entry.getHistoryIndex(),
                            entry.getOperation().getTimestamp(),
                            entry.getOperation().getOp().getClass().getSimpleName()
                        );
                    }
                } else {
                    LOGGER.warn("Could not retrieve recent account history for {}.", myHiveAccountName.getName());
                }

                LOGGER.info("---");

                // EXAMPLE 2: Get up to 100 recent VOTE operations using the new filter feature.
                // The bitmask for a vote_operation (ID 0) is 2^0 = 1.
                Long voteFilter = 1L;
                UInteger filterLimit = UInteger.valueOf(100);

                LOGGER.info("Fetching up to {} recent VOTE operations for '{}' using a filter...", filterLimit, myHiveAccountName.getName());
                
                List<OperationHistoryEntry> filteredVoteHistory = steemJ.getAccountHistory(myHiveAccountName, 
                        startFrom, filterLimit, true, voteFilter, 0L);
                
                if (filteredVoteHistory != null && !filteredVoteHistory.isEmpty()) {
                    LOGGER.info("Successfully fetched {} VOTE operations:", filteredVoteHistory.size());
                    // We can log the first few to confirm the filter worked.
                    for (int i = 0; i < Math.min(5, filteredVoteHistory.size()); i++) {
                        OperationHistoryEntry entry = filteredVoteHistory.get(i);
                        LOGGER.info("  - Filtered Vote Op Index: {}, Type: {}",
                            entry.getHistoryIndex(),
                            entry.getOperation().getOp().getClass().getSimpleName());
                    }
                } else {
                    LOGGER.warn("Could not find any recent vote operations for {}.", myHiveAccountName.getName());
                }

            } catch (SteemCommunicationException | SteemResponseException e) {
                LOGGER.error("Error during getAccountHistory call: {}", e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error("An unexpected error occurred during the account history test: {}", e.getMessage(), e);
            }
            LOGGER.info("--------------------------------------------------------------------");
            // <<< END OF NEW SECTION for getAccountHistory >>>
             // 4. <<< NEW SECTION FOR TESTING THE ORIGINAL getOpsInBlock >>>
            LOGGER.info("--------------------------------------------------------------------");
            LOGGER.info("Attempting to fetch operations in a block using the ORIGINAL code...");

            try {
                // A block number known to contain various operations.
                long blockToTest = 5443322;
                boolean onlyVirtualOps = false;

                LOGGER.info("Fetching all operations for block #{}", blockToTest);
                
                
                List<AppliedOperation> opsInBlock = steemJ.getOpsInBlock(blockToTest, onlyVirtualOps);
                
                if (opsInBlock != null && !opsInBlock.isEmpty()) {
                    LOGGER.info("SUCCESS: Successfully fetched {} operations from block #{}:", opsInBlock.size(), blockToTest);
                    for (AppliedOperation op : opsInBlock) {
                        LOGGER.info("  - Op Type: {}", op.getOp().getClass().getSimpleName());
                    }
                } else {
                    LOGGER.warn("Could not retrieve operations for block #{}, or the block was empty.", blockToTest);
                }

            } catch (Exception e) {
                // We expect to land here. The original code is not compatible with Hive.
                LOGGER.error("AN ERROR OCCURRED : The original getOpsInBlock failed. Error: {}", e.getMessage(), e);
            }
            LOGGER.info("--------------------------------------------------------------------");
            // <<< END OF NEW SECTION for getOpsInBlock >>>
                       // 5. <<< NEW SECTION: TESTING account_history_api.get_transaction >>>
            LOGGER.info("--------------------------------------------------------------------");
            LOGGER.info("Attempting to test account_history_api.get_transaction...");

            try {
                // First, get the most recent history for an active account (e.g., your account).
                // This ensures we get a transaction ID that the node has indexed.
                LOGGER.info("Step 1: Fetching recent history to get a valid transaction ID...");
                ULong startFrom = ULong.valueOf(-1);
                UInteger limit = UInteger.valueOf(5); // Get a few recent operations
                
                List<OperationHistoryEntry> recentHistory = steemJ.getAccountHistory(myHiveAccountName, startFrom, limit);
                
                TransactionId transactionToTest = null;
                if (recentHistory != null && !recentHistory.isEmpty()) {
                    // Find the first real transaction in the list (not a virtual operation)
                    for(OperationHistoryEntry entry : recentHistory) {
                        if (!entry.getOperation().isVirtualOp()) {
                            transactionToTest = entry.getOperation().getTrxId();
                            break; // We found one, stop looping
                        }
                    }
                }

                if (transactionToTest != null) {
                    LOGGER.info("Step 2: Found a recent transaction ID to test: {}", transactionToTest);
                    LOGGER.info("Now calling getTransaction with this ID...");

    
                    JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.ACCOUNT_HISTORY_API, RequestMethod.GET_TRANSACTION,
                            transactionToTest.toString());
                            
                    // We need a way to call this. Let's add a temporary helper in SteemJ for this test.
                    AnnotatedSignedTransaction transactionDetails = steemJ.getHistoryTransaction(transactionToTest);

                    if (transactionDetails != null) {
                        LOGGER.info("SUCCESS: Successfully fetched transaction details using account_history_api!");
                        LOGGER.info("  - Transaction ID: {}", transactionDetails.getTransactionId());
                        LOGGER.info("  - Block Number: {}", transactionDetails.getBlockNum());
                    } else {
                        LOGGER.warn("The getTransaction call returned null, even for a known recent transaction.");
                    }
                } else {
                    LOGGER.warn("Could not find a recent, non-virtual transaction to test with.");
                }

            } catch (Exception e) {
                LOGGER.error("An unexpected error occurred during the account_history_api.get_transaction test: {}", e.getMessage(), e);
            }
            LOGGER.info("--------------------------------------------------------------------");
            // <<< END OF NEW SECTION >>>

            LOGGER.info("GetMyHiveData sample finished all processing.");

            // Other tests (account history, global properties) are still commented out
            /*
            // 4. Fetch Your Account History
            // ... (code for account history was here) ...

            // 5. (Optional) Fetch Dynamic Global Properties
            // ... (code for global properties was here) ...
            */

            LOGGER.info("GetMyHiveData sample finished all processing."); // Updated this log message

        } catch (SteemCommunicationException e) {
            LOGGER.error("Steem Communication Exception: {} - Node URI used: {}", e.getMessage(), hiveNodeHttpUrl, e);
        } catch (SteemResponseException e) {
            LOGGER.error("Steem Response Exception: {} - Code: {}", e.getMessage(), e.getCode(), e);
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred: {}", e.getMessage(), e);
        }
    }
}