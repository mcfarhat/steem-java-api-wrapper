package my.sample.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedAccount;
import eu.bittrade.libs.steemj.protocol.AccountName;
// import java.util.Map; // Not needed as account history is commented out

public class GetMyHiveData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetMyHiveData.class);

    public static void main(String[] args) {
        LOGGER.info("Starting GetMyHiveData sample...");

        SteemJConfig myConfig = SteemJConfig.getInstance();
        myConfig.setResponseTimeout(100000); // Set a reasonable timeout

        String hiveNodeHttpUrl = "https://anyx.io"; // Using a different node, with HTTPS
        // Inside your main method, before creating the SteemJ instance:

        try {
            // Create a list of endpoint URIs for failover
            List<Pair<URI, Boolean>> endpoints = new ArrayList<>();
            // Add multiple, reliable Hive nodes. The library will try them in order.
            endpoints.add(new ImmutablePair<>(new URI("https://api.hive.blog"), true));
            endpoints.add(new ImmutablePair<>(new URI("https://anyx.io"), true));
            endpoints.add(new ImmutablePair<>(new URI("https://api.deathwing.me"), true));
            endpoints.add(new ImmutablePair<>(new URI("https://rpc.ecency.com"), true));
            
            // Set the entire list of endpoints in the configuration
            myConfig.setEndpointURIs(endpoints);
            LOGGER.info("Configured failover endpoints. Primary: {}", endpoints.get(0).getLeft());

            // You still need to set the Chain ID
            myConfig.setChainId("beeab0de00000000000000000000000000000000000000000000000000000000");
            LOGGER.info("Set Chain ID for Hive.");

        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException in one of the predefined nodes.", e);
            return;
        }

        // Your Hive account name - REPLACE THIS IF NEEDED
        AccountName myHiveAccountName = new AccountName("omarghadban");
        myConfig.setDefaultAccount(myHiveAccountName);
        LOGGER.info("Default account set to: {}", myConfig.getDefaultAccount().getName());

        // Private keys are NOT needed for reading data, so we skip adding them.

        try {
            SteemJ steemJ = new SteemJ();
            LOGGER.info("SteemJ instance created.");

            // 3. Fetch Your Account Details - FOCUS ON THIS CALL
            LOGGER.info("Attempting to fetch account details for: {}", myHiveAccountName.getName());
            List<AccountName> accountsToFetch = new ArrayList<>();
            accountsToFetch.add(myHiveAccountName);

            List<ExtendedAccount> extendedAccounts = steemJ.getAccounts(accountsToFetch);

            if (extendedAccounts != null && !extendedAccounts.isEmpty()) {
                ExtendedAccount myAccount = extendedAccounts.get(0);
                LOGGER.info("Successfully fetched account details:");
                LOGGER.info("  Name: {}", myAccount.getName().getName());
                LOGGER.info("  Balance (HIVE): {}", myAccount.getBalance());
                LOGGER.info("  SBD/HBD Balance: {}", myAccount.getSbdBalance()); // Assuming getSbdBalance for HBD
                LOGGER.info("  Vesting Shares (HP): {}", myAccount.getVestingShares());
                // You can add more fields from ExtendedAccount like reputation, created, etc.
                // LOGGER.info("  Reputation: {}", myAccount.getReputation());
                // LOGGER.info("  Creation Date: {}", myAccount.getCreated());
            } else {
                LOGGER.warn("Could not retrieve account details for {}.", myHiveAccountName.getName());
            }

            // Temporarily comment out other calls to isolate issues
            /*
            // 4. Fetch Your Account History
            LOGGER.info("Attempting to fetch account history for: {}", myHiveAccountName.getName());
            Map<UInteger, AppliedOperation> accountHistory = steemJ.getAccountHistory(myHiveAccountName, ULong.valueOf(-1), UInteger.valueOf(10));

            if (accountHistory != null && !accountHistory.isEmpty()) {
                LOGGER.info("Recent account history (newest first):");
                accountHistory.entrySet().stream()
                    .sorted(Map.Entry.<UInteger, AppliedOperation>comparingByKey().reversed())
                    .forEach(entry -> {
                        AppliedOperation opDetails = entry.getValue();
                        LOGGER.info("  Op Index: {}, Timestamp: {}, Type: {}",
                            entry.getKey(),
                            opDetails.getTimestamp(),
                            opDetails.getOp().getClass().getSimpleName()
                        );
                    });
            } else {
                LOGGER.warn("No account history found for {}.", myHiveAccountName.getName());
            }

            // 5. (Optional) Fetch Dynamic Global Properties
            LOGGER.info("Attempting to fetch dynamic global properties...");
            DynamicGlobalProperty globalProperties = steemJ.getDynamicGlobalProperties();
            if (globalProperties != null) {
                LOGGER.info("Current Head Block Number: {}", globalProperties.getHeadBlockNumber());
                LOGGER.info("Current Time: {}", globalProperties.getTime());
            } else {
                LOGGER.warn("Could not retrieve dynamic global properties.");
            }
            */

            LOGGER.info("GetMyHiveData sample finished processing getAccounts.");

        } catch (SteemCommunicationException e) {
            LOGGER.error("Steem Communication Exception: {} - Node URI used: {}", e.getMessage(), hiveNodeHttpUrl, e);
        } catch (SteemResponseException e) {
            // Corrected logging for SteemResponseException
            LOGGER.error("Steem Response Exception: {} - Code: {}", e.getMessage(), e.getCode(), e);
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred: {}", e.getMessage(), e);
        }
    }
}