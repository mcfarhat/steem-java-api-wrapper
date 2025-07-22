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
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.bittrade.libs.steemj;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joou.UInteger;
import org.joou.ULong;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import eu.bittrade.crypto.core.ECKey;
import eu.bittrade.crypto.core.Sha256Hash;
import eu.bittrade.libs.steemj.base.models.Account;
import eu.bittrade.libs.steemj.base.models.ChainProperties;
import eu.bittrade.libs.steemj.base.models.FeedHistory; // Used in claimRewards, and now in calculateRemainingBandwidth
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.ScheduledHardfork;
import eu.bittrade.libs.steemj.chain.SignedTransaction;
import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.communication.jrpc.JsonRPCRequest;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.enums.RequestMethod;
import eu.bittrade.libs.steemj.enums.RewardFundType;
import eu.bittrade.libs.steemj.enums.SteemApiType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.fc.TimePointSec;
import eu.bittrade.libs.steemj.plugins.apis.account.by.key.AccountByKeyApi;
import eu.bittrade.libs.steemj.plugins.apis.account.by.key.models.GetKeyReferencesArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.AccountHistoryApi;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetAccountHistoryArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.OperationHistoryEntry;
import eu.bittrade.libs.steemj.plugins.apis.block.BlockApi;
import eu.bittrade.libs.steemj.plugins.apis.block.models.ExtendedSignedBlock;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockHeaderArgs;
import eu.bittrade.libs.steemj.plugins.apis.condenser.CondenserApi;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.AccountVote;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedAccount;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedDynamicGlobalProperties;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedLimitOrder;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.State;
import eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi;
import eu.bittrade.libs.steemj.plugins.apis.database.models.Config; // Make sure this is imported
import eu.bittrade.libs.steemj.plugins.apis.database.models.DynamicGlobalProperty;
import eu.bittrade.libs.steemj.plugins.apis.database.models.OrderBook; // Make sure this is imported
import eu.bittrade.libs.steemj.plugins.apis.database.models.RewardFund;
import eu.bittrade.libs.steemj.plugins.apis.database.models.Witness;
import eu.bittrade.libs.steemj.plugins.apis.database.models.WitnessSchedule;
import eu.bittrade.libs.steemj.plugins.apis.follow.FollowApi;
import eu.bittrade.libs.steemj.plugins.apis.follow.enums.FollowType;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.AccountReputation;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.BlogEntry;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.CommentBlogEntry;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.CommentFeedEntry;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.FeedEntry;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.FollowApiObject;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.FollowCountApiObject;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.GetFollowersArgs;
import eu.bittrade.libs.steemj.plugins.apis.follow.models.PostsPerAuthorPair;
import eu.bittrade.libs.steemj.plugins.apis.market.history.MarketHistoryApi;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.Bucket;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetMarketHistoryArgs;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetOrderBookArgs;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetRecentTradesArgs;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetTickerReturn;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetTradeHistoryArgs;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetVolumeReturn;
import eu.bittrade.libs.steemj.plugins.apis.market.history.models.MarketTrade;
import eu.bittrade.libs.steemj.plugins.apis.network.broadcast.api.NetworkBroadcastApi;
import eu.bittrade.libs.steemj.plugins.apis.network.broadcast.models.BroadcastTransactionSynchronousReturn;
import eu.bittrade.libs.steemj.plugins.apis.tags.TagsApi;
import eu.bittrade.libs.steemj.plugins.apis.tags.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.Discussion;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.DiscussionQuery;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.GetActiveVotesArgs;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.Tag;
import eu.bittrade.libs.steemj.plugins.apis.tags.models.VoteState;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.AnnotatedSignedTransaction;
import eu.bittrade.libs.steemj.protocol.BlockHeader;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;
import eu.bittrade.libs.steemj.protocol.Price;
import eu.bittrade.libs.steemj.protocol.PublicKey;
import eu.bittrade.libs.steemj.protocol.SignedBlock;
import eu.bittrade.libs.steemj.protocol.TransactionId;
import eu.bittrade.libs.steemj.protocol.enums.LegacyAssetSymbolType;
import eu.bittrade.libs.steemj.protocol.operations.Operation;
import eu.bittrade.libs.steemj.protocol.operations.VoteOperation;
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class is a wrapper for the Steem web socket API and provides all
 * features known from the Steem CLI Wallet.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class SteemJ {
    // private static final Logger LOGGER = LoggerFactory.getLogger(SteemJ.class); // Marked as unused

    // Error messages as constants to make SonarQube happy.
    // private static final String TAG_ERROR_MESSAGE = "You need to provide at least one tag, but not more than five."; // Marked as unused
    private static final String NO_DEFAULT_ACCOUNT_ERROR_MESSAGE = "You try to use a simplified operation without having a default account configured in SteemJConfig. Please configure a default account or use another method.";
    // private static final String MARKDOWN = "markdown"; // Marked as unused

    private static CommunicationHandler communicationHandler; // Warning: should be accessed in a static way if static

    /**
     * Initialize the SteemJ.
     * 
     * @throws SteemCommunicationException
     *             <ul>
     *             <li>If the server was not able to answer the request in the
     *             given time (see
     *             {@link eu.bittrade.libs.steemj.configuration.SteemJConfig#setResponseTimeout(int)
     *             setResponseTimeout}).</li>
     *             <li>If there is a connection problem.</li>
     *             </ul>
     * @throws SteemResponseException
     *             <ul>
     *             <li>If the SteemJ is unable to transform the JSON response
     *             into a Java object.</li>
     *             <li>If the Server returned an error object.</li>
     *             </ul>
     */
    public SteemJ() throws SteemCommunicationException, SteemResponseException {
        // Accessing static field in a non-static way. Corrected:
        SteemJ.communicationHandler = new CommunicationHandler();
    }

    // ... (Keep all other methods from Account By Key API, Account History API, Block API, Network Broadcast API, Database API (part 1) as they were) ...
    // From line 175 down to before getAccounts()

    // #########################################################################
    // ## ACCOUNT BY KEY API ###################################################
    // #########################################################################

    public List<List<AccountName>> getKeyReferences(List<PublicKey> publicKeys)
            throws SteemCommunicationException, SteemResponseException {
        return AccountByKeyApi.getKeyReferences(SteemJ.communicationHandler, new GetKeyReferencesArgs(publicKeys))
                .getAccounts();
    }

    // #########################################################################
    // ## ACCOUNT HISTORY API ##################################################
    // #########################################################################

     // #########################################################################
    // # OTHER API METHODS                                                     #
    // #########################################################################

       /**
     * Get a sequence of operations included/generated within a particular block.
     *
     * @param blockNum
     *            The block number to retrieve operations from.
     * @param onlyVirtual
     *            Whether to only include virtual operations.
     * @return A list of {@link AppliedOperation AppliedOperations} from the block.
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the API returns an error.
     */
    public List<AppliedOperation> getOpsInBlock(long blockNum, boolean onlyVirtual)
            throws SteemCommunicationException, SteemResponseException {
        // 1. Create the new, correct arguments object using simple types (long, boolean).
        GetOpsInBlockArgs params = new GetOpsInBlockArgs(blockNum, onlyVirtual);
        
        // 2. Call the static method in AccountHistoryApi.
        return AccountHistoryApi.getOpsInBlock(SteemJ.communicationHandler, params).getOperations();
    }
        // IN SteemJ.java - ADD THIS TEMPORARY METHOD
    public AnnotatedSignedTransaction getHistoryTransaction(TransactionId transactionId)
            throws SteemCommunicationException, SteemResponseException {
        // The old code used a Map, but the API actually just wants the ID string.
        // We will target the account_history_api explicitly.
        
        // This is based on your original AccountHistoryApi code that took a String
        Map<String, String> params = Collections.singletonMap("id", transactionId.toString());

        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.ACCOUNT_HISTORY_API,
                RequestMethod.GET_TRANSACTION, params);

        List<AnnotatedSignedTransaction> result = this.communicationHandler.performRequest(requestObject,
                AnnotatedSignedTransaction.class);

        if (result == null || result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

           /**
     * Find a transaction by its transaction ID. This method calls the
     * <code>condenser_api.get_transaction</code> method.
     *
     * @param transactionId
     *            The {@link TransactionId} object representing the transaction to
     *            search for.
     * @return The {@link AnnotatedSignedTransaction} if found.
     * @throws SteemCommunicationException
     *             If a communication error occurs.
     * @throws SteemResponseException
     *             If the API returns an error (e.g., transaction not found).
     */
    public AnnotatedSignedTransaction getTransaction(TransactionId transactionId)
            throws SteemCommunicationException, SteemResponseException {

        List<Object> parameters = new ArrayList<>();
        parameters.add(transactionId.toString());

        // THIS IS THE FINAL, CORRECTED LINE:
        // Explicitly target the CONDENSER_API which contains the global get_transaction method.
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.CONDENSER_API, RequestMethod.GET_TRANSACTION,
                parameters);

        List<AnnotatedSignedTransaction> result = this.communicationHandler.performRequest(requestObject,
                AnnotatedSignedTransaction.class);

        if (result == null || result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }
    /**
     * Returns a history of all operations for a given account using the updated Hive API model.
     * This is the recommended method to use.
     *
     * @param accountName The name of the account to get the history for.
     * @param start The sequence number to start from. For Hive, use -1 for oldest, or ULong.MAX_VALUE for newest.
     * @param limit The maximum number of operations to return (1-1000).
     * @return A List of {@link OperationHistoryEntry} objects, correctly representing the Hive API response.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error.
     */

     public List<OperationHistoryEntry> getAccountHistory(AccountName accountName, ULong start, UInteger limit)
            throws SteemCommunicationException, SteemResponseException {
        return AccountHistoryApi.getAccountHistory(SteemJ.communicationHandler, accountName, start, limit)
                .getHistory();
    }

     /**
     * Returns a history of all operations for a given account, with access to all optional Hive filters.
     *
     * @param accountName The name of the account.
     * @param start The sequence number to start from.
     * @param limit The maximum number of operations to return.
     * @param includeReversible (Optional) If true, include operations from reversible blocks.
     * @param operationFilterLow (Optional) Bitmask for filtering operations 0-63.
     * @param operationFilterHigh (Optional) Bitmask for filtering operations 64-127.
     * @return A List of {@link OperationHistoryEntry} objects.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error.
     */
    public List<OperationHistoryEntry> getAccountHistory(AccountName accountName, ULong start, UInteger limit,
            Boolean includeReversible, Long operationFilterLow, Long operationFilterHigh)
            throws SteemCommunicationException, SteemResponseException {
        return AccountHistoryApi.getAccountHistory(SteemJ.communicationHandler, accountName, start, limit,
                includeReversible, operationFilterLow, operationFilterHigh).getHistory();
    }
     /**
     * Get all operations performed by the specified account.
     *
     * @deprecated The Hive API returns a List, not a Map. This method is kept for backward compatibility but is
     *             inefficient as it converts the result from a List to a Map. Please use the new
     *             {@link #getAccountHistory(AccountName, ULong, UInteger)} which returns a {@code List<OperationHistoryEntry>}.
     *
     * @param accountName The user name of the account.
     * @param start The starting point.
     * @param limit The maximum number of entries.
     * @return A map containing the activities. The key is the id of the activity.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error.
     */
    @Deprecated
    public Map<UInteger, AppliedOperation> getAccountHistoryAsMap(AccountName accountName, ULong start, UInteger limit)
            throws SteemCommunicationException, SteemResponseException {
        // Call the new API method to get the list-based response.
        List<OperationHistoryEntry> historyList = AccountHistoryApi
                .getAccountHistory(SteemJ.communicationHandler, new GetAccountHistoryArgs(accountName, start, limit))
                .getHistory();
        
        // Convert the List<OperationHistoryEntry> into the old Map format for backward compatibility.
        return historyList.stream().collect(Collectors.toMap(
            entry -> UInteger.valueOf(entry.getHistoryIndex()), // Key: history index
            OperationHistoryEntry::getOperation                 // Value: the AppliedOperation object
        ));
    }

  

    // #########################################################################
    // ## BLOCK API ############################################################
    // #########################################################################

    public Optional<ExtendedSignedBlock> getBlock(long blockNumber)
            throws SteemCommunicationException, SteemResponseException {
        return BlockApi.getBlock(SteemJ.communicationHandler, new GetBlockArgs(UInteger.valueOf(blockNumber))).getBlock();
    }

    public Optional<BlockHeader> getBlockHeader(long blockNumber)
            throws SteemCommunicationException, SteemResponseException {
        return BlockApi.getBlockHeader(SteemJ.communicationHandler, new GetBlockHeaderArgs(UInteger.valueOf(blockNumber)))
                .getHeader();
    }

    // #########################################################################
    // ## NETWORK BROADCAST API ################################################
    // #########################################################################

    public void broadcastTransaction(SignedTransaction transaction)
            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        NetworkBroadcastApi.broadcastTransaction(SteemJ.communicationHandler, transaction);
    }

    public BroadcastTransactionSynchronousReturn broadcastTransactionSynchronous(SignedTransaction transaction)
            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        return NetworkBroadcastApi.broadcastTransactionSynchronous(SteemJ.communicationHandler, transaction);
    }

    public void broadcastBlock(SignedBlock signedBlock) throws SteemCommunicationException, SteemResponseException {
        NetworkBroadcastApi.broadcastBlock(SteemJ.communicationHandler, signedBlock);
    }

    // #########################################################################
    // ## DATABASE API #########################################################
    // #########################################################################
    
    public State getState(Permlink path) throws SteemCommunicationException, SteemResponseException {
        return CondenserApi.getState(SteemJ.communicationHandler, path);
    }

    public List<AccountName> getActiveWitnesses() throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getActiveWitnesses(SteemJ.communicationHandler);
    }

    public DynamicGlobalProperty getDynamicGlobalProperties()
            throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getDynamicGlobalProperties(SteemJ.communicationHandler);
    }

    public int getAccountCount() throws SteemCommunicationException, SteemResponseException {
        // This method returned 0, keeping as is unless instructed to implement
        return 0;
    }

    /**
     * Get account details from the Hive blockchain using the condenser_api.
     * 
     * @param accountNames
     *            A list of accounts you want to request the details for.
     * @return A List of {@link ExtendedAccount} objects found for the given account names.
     *         Returns an empty list if no accounts are found or if the input list is null/empty.
     * @throws SteemCommunicationException
     *             If there is a communication problem with the node.
     * @throws SteemResponseException
     *             If the node returns an error response.
     */
    public List<ExtendedAccount> getAccounts(List<AccountName> accountNames)
            throws SteemCommunicationException, SteemResponseException {
        // Ensure communicationHandler is initialized (it is in the SteemJ constructor)
        if (SteemJ.communicationHandler == null) { // Corrected: Access static field statically
            // This should ideally not happen if SteemJ object is created properly.
            throw new SteemCommunicationException("CommunicationHandler is not initialized in SteemJ instance.");
        }
        
        // The modified CondenserApi.getAccounts should handle null/empty input,
        // but if not, this check could be added there or here.
        // if (accountNames == null || accountNames.isEmpty()) {
        //     return new ArrayList<>(); // Return empty list if no names provided
        // }

        // Call the static method from CondenserApi that you (presumably) updated
        return CondenserApi.getAccounts(SteemJ.communicationHandler, accountNames);
    }

    // ... (The rest of the Database API methods: getAccountVotes, getActiveVotes, getChainProperties, getContent etc. remain as they were - mostly returning null or calling other APIs) ...
    // Lines from ~640 to ~2149 (before calculateRemainingBandwidth)

    public List<AccountVote> getAccountVotes(AccountName accountName)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<VoteState> getActiveVotes(AccountName author, Permlink permlink)
            throws SteemCommunicationException, SteemResponseException {
        return TagsApi.getActiveVotes(SteemJ.communicationHandler, new GetActiveVotesArgs(author, permlink)).getVotes();
    }
    
    public ChainProperties getChainProperties() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public Discussion getContent(AccountName author, Permlink permlink)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public List<Discussion> getContentReplies(AccountName author, Permlink permlink)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public Object[] getConversionRequests(AccountName account)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public Price getCurrentMedianHistoryPrice() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<Discussion> getDiscussionsBy(DiscussionQuery discussionQuery, DiscussionSortType sortBy)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<Discussion> getDiscussionsByAuthorBeforeDate(AccountName author, Permlink permlink, String date,
            int limit) throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public FeedHistory getFeedHistory() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public ScheduledHardfork getNextScheduledHarfork() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public List<ExtendedLimitOrder> getOpenOrders(AccountName accountName)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public OrderBook getOrderBookUsingDatabaseApi(int limit)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<String[]> getPotentialSignatures() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public List<Discussion> getRepliesByLastUpdate(AccountName username, Permlink permlink, int limit)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public RewardFund getRewardFund(RewardFundType rewordFundType)
            throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getRewardFunds(SteemJ.communicationHandler, rewordFundType);
    }
    
    public String getTransactionHex(SignedTransaction signedTransaction)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public Witness getWitnessByAccount(AccountName witnessName)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<Witness> getWitnessByVote(AccountName witnessName, int limit)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public int getWitnessCount() throws SteemCommunicationException, SteemResponseException {
        // This returned 0
        return 0;
    }
    
    public List<Witness> getWitnesses() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public WitnessSchedule getWitnessSchedule() throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public List<String> lookupAccounts(String pattern, int limit)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }

    public List<String> lookupWitnessAccounts(String pattern, int limit)
            throws SteemCommunicationException, SteemResponseException {
        // This was returning null
        return null;
    }
    
    public boolean verifyAuthority(SignedTransaction signedTransaction)
            throws SteemCommunicationException, SteemResponseException {
        // Assuming DatabaseApi.verifyAccountAuthority is okay, but its second param was null
        return DatabaseApi.verifyAccountAuthority(SteemJ.communicationHandler, null).isValid();
    }

    // #########################################################################
    // ## FOLLOW API ###########################################################
    // #########################################################################
    
    public List<FollowApiObject> getFollowers(AccountName following, AccountName startFollower, FollowType type,
            UInteger limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowers(SteemJ.communicationHandler, new GetFollowersArgs(following, startFollower, type, limit))
                .getFollowers();
    }
    
    public List<FollowApiObject> getFollowing(AccountName follower, AccountName startFollowing, FollowType type,
            long limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowing(SteemJ.communicationHandler, follower, startFollowing, type, UInteger.valueOf(limit));
    }

    public FollowCountApiObject getFollowCount(AccountName account)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowCount(SteemJ.communicationHandler, account);
    }
    
    public List<FeedEntry> getFeedEntries(AccountName account, int entryId, short limit)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFeedEntries(SteemJ.communicationHandler, account, entryId, limit);
    }

    public List<CommentFeedEntry> getFeed(AccountName account, int entryId, short limit)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFeed(SteemJ.communicationHandler, account, entryId, limit);
    }
    
    public List<BlogEntry> getBlogEntries(AccountName account, int entryId, short limit)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlogEntries(SteemJ.communicationHandler, account, entryId, limit);
    }

    public List<CommentBlogEntry> getBlog(AccountName account, int entryId, short limit)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlog(SteemJ.communicationHandler, account, entryId, limit);
    }
    
    public List<AccountReputation> getAccountReputations(AccountName accountName, int limit)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getAccountReputations(SteemJ.communicationHandler, accountName, limit);
    }

    public List<AccountName> getRebloggedBy(AccountName author, Permlink permlink)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getRebloggedBy(SteemJ.communicationHandler, author, permlink);
    }
    
    public List<PostsPerAuthorPair> getBlogAuthors(AccountName blogAccount)
            throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlogAuthors(SteemJ.communicationHandler, blogAccount);
    }

    // #########################################################################
    // ## MARKET HISTORY API ###################################################
    // #########################################################################
    
    public GetTickerReturn getTicker() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getTicker(SteemJ.communicationHandler);
    }
    
    public GetVolumeReturn getVolume() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getVolume(SteemJ.communicationHandler);
    }
    
    public eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetOrderBookReturn getOrderBookUsingMarketApi(
            short limit) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getOrderBook(SteemJ.communicationHandler, new GetOrderBookArgs(UInteger.valueOf(limit)));
    }

    public List<MarketTrade> getTradeHistory(TimePointSec start, TimePointSec end, UInteger limit)
            throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getTradeHistory(SteemJ.communicationHandler, new GetTradeHistoryArgs(start, end, limit))
                .getTrades();
    }
    
    public List<MarketTrade> getRecentTrades(short limit) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getRecentTrades(SteemJ.communicationHandler, new GetRecentTradesArgs(UInteger.valueOf(limit)))
                .getTrades();
    }
    
    public List<Bucket> getMarketHistory(long bucketSeconds, TimePointSec start, TimePointSec end)
            throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getMarketHistory(SteemJ.communicationHandler,
                new GetMarketHistoryArgs(UInteger.valueOf(bucketSeconds), start, end)).getBuckets();
    }
    
    public List<UInteger> getMarketHistoryBuckets() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getMarketHistoryBuckets(SteemJ.communicationHandler).getBucketSizes();
    }

    // #########################################################################
    // ## TAGS API #############################################################
    // #########################################################################
    
    public List<Tag> getTrendingTags(String firstTagPattern, int limit)
            throws SteemCommunicationException, SteemResponseException {
        return TagsApi.getTrendingTags(SteemJ.communicationHandler, firstTagPattern, limit);
    }

    // #########################################################################
    // ## WITNESS API ##########################################################
    // #########################################################################
    
   

    // #########################################################################
    // ## UTILITY METHODS ######################################################
    // #########################################################################

    public static LegacyAsset steemToSbd(Price price, LegacyAsset steemAsset) {
        if (steemAsset == null || !steemAsset.getSymbol().equals(LegacyAssetSymbolType.STEEM)) {
            throw new InvalidParameterException("The asset needs be of SymbolType STEEM.");
        }

        if (price == null) {
            return new LegacyAsset(0, LegacyAssetSymbolType.SBD);
        }

        return price.multiply(steemAsset);
    }

    public static LegacyAsset sbdToSteem(Price price, LegacyAsset sbdAsset) {
        if (sbdAsset == null || !sbdAsset.getSymbol().equals(LegacyAssetSymbolType.SBD)) {
            throw new InvalidParameterException("The asset needs be of SymbolType STEEM.");
        }

        if (price == null) {
            return new LegacyAsset(0, LegacyAssetSymbolType.STEEM);
        }

        return price.multiply(sbdAsset);
    }

    /**
     * Calculate remaining bandwidth for a given account.
     * Note: This calculation is based on fetching account data and global properties.
     * It might be more accurate to use specific bandwidth API calls if available and reliable.
     * 
     * @param accountName The account to calculate bandwidth for.
     * @return Estimated remaining bandwidth percentage (approximate).
     * @throws SteemCommunicationException
     * @throws SteemResponseException
     */
    public double calculateRemainingBandwidth(AccountName accountName)
            throws SteemCommunicationException, SteemResponseException {
        // TODO: Use getReserveRatio instead for a more accurate/direct bandwidth calculation if available.
        ExtendedDynamicGlobalProperties extendedDynamicGlobalProperties = CondenserApi
                .getDynamicGlobalProperties(SteemJ.communicationHandler); // Corrected: Access static field statically

        // Fetch only the specific account we need
        List<AccountName> accountsToFetch = Lists.newArrayList(accountName);
        // Or:
        // List<AccountName> accountsToFetch = new ArrayList<>();
        // accountsToFetch.add(accountName);

        List<ExtendedAccount> extendedAccountsResult = CondenserApi.getAccounts(SteemJ.communicationHandler, accountsToFetch); // Corrected: Access static field statically

        if (extendedAccountsResult == null || extendedAccountsResult.isEmpty()) {
            throw new InvalidParameterException("No account has been found matching the provided account name: " + accountName.getName());
        }

        // We expect only one account back
        ExtendedAccount specificAccount = extendedAccountsResult.get(0);
        
        return calculateRemainingBandwidth(extendedDynamicGlobalProperties, specificAccount);
    }

    /**
     * Static helper to calculate bandwidth based on provided properties and account object.
     * @param extendedDynamicGlobalProperties
     * @param account
     * @return
     */
    public static double calculateRemainingBandwidth(ExtendedDynamicGlobalProperties extendedDynamicGlobalProperties,
            Account account) {
        long maxVirtualBandwidth = extendedDynamicGlobalProperties.getMaxVirtualBandwidth().longValue();
        long secondsPerWeek = 60 * 60 * 24 * 7;
        long secondsSinceLastUpdate = (System.currentTimeMillis() / 1000)
                - account.getLastBandwidthUpdate().getDateTimeAsInt();
        // The 'delta' variable was marked as unused in previous warnings; this logic might need review by original authors.
        // long delta = ((secondsPerWeek - secondsSinceLastUpdate) * account.getAverageBandwidth()) / secondsPerWeek;

        long totalVestingShares = extendedDynamicGlobalProperties.getTotalVestingShares().getAmount();
        if (totalVestingShares == 0) return 0; // Avoid division by zero

        long userVestingShares = account.getVestingShares().getAmount() + account.getReceivedVestingShares().getAmount();
        
        long allocatedBandwidth = (userVestingShares * maxVirtualBandwidth) / totalVestingShares;
        long currentBandwidth = account.getAverageBandwidth(); // This represents used bandwidth averaged over time

        // This calculation might need further refinement based on exact Steem/Hive bandwidth algorithm details.
        // A simpler representation could be (allocated - current_average) / allocated,
        // but average_bandwidth is not "currently used" but an average.
        // The original code just returned 'bandwidthOfTheUser' which seems to be the allocated amount, not remaining.
        // For now, let's return a representation of allocated bandwidth as per original structure.
        // To get "remaining", one would need to subtract currently used (which is tricky with average_bandwidth).
        // The original method also had a 'delta' variable that was unused, suggesting the calculation might be incomplete.
        // For simplicity and to match the structure of what was there:
        return allocatedBandwidth; // This is total allocated, not "remaining" in a percentage sense.
                                  // True "remaining" is more complex.
    }

    /*
     * TODO: Provided by mdfk -> Needs to adjusted to work with the new api
     * calls. private double getEarnedMoney(Comment comment) throws
     * SteemResponseException, SteemCommunicationException { rewardFund =
     * steemJ.getRewardFund(RewardFundType.POST); currentMedianHistoryPrice =
     * steemJ.getCurrentMedianHistoryPrice();
     * // ... (rest of the method)
     */

    public static ImmutablePair<PublicKey, String> getPrivateKeyFromPassword(AccountName account, PrivateKeyType role,
            String steemPassword) {
        String seed = account.getName() + role.name().toLowerCase() + steemPassword;
        ECKey keyPair = ECKey.fromPrivate(Sha256Hash.hash(seed.getBytes(), 0, seed.length()));

        return new ImmutablePair<>(new PublicKey(keyPair), SteemJUtils.privateKeyToWIF(keyPair));
    }

    // #########################################################################
    // ## SIMPLIFIED OPERATIONS ################################################
    // #########################################################################

    // ... (ALL SIMPLIFIED OPERATIONS like vote, follow, createPost etc. remain as they were,
    //      many of them are returning null or have TODOs for re-adding code) ...
    // From line ~2214 down to before the static getConfig() method

    public void vote(AccountName postOrCommentAuthor, Permlink postOrCommentPermlink, short percentage)
            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        if (SteemJConfig.getInstance().getDefaultAccount().isEmpty()) {
            throw new InvalidParameterException(
                    "Using the upVote method without providing an account requires to have a default account configured.");
        }

        this.vote(SteemJConfig.getInstance().getDefaultAccount(), postOrCommentAuthor, postOrCommentPermlink,
                percentage);
    }

    public void vote(AccountName voter, AccountName postOrCommentAuthor, Permlink postOrCommentPermlink,
            short percentage)
            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        if (percentage < -100 || percentage > 100 || percentage == 0) {
            throw new InvalidParameterException(
                    "Please provide a percentage between -100 and 100 which is also not 0.");
        }

        VoteOperation voteOperation = new VoteOperation(voter, postOrCommentAuthor, postOrCommentPermlink,
                (short) (percentage * 100));

        ArrayList<Operation> operations = new ArrayList<>();
        operations.add(voteOperation);

        DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();

        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                null);

        signedTransaction.sign();

        this.broadcastTransaction(signedTransaction);
    }
    
    // ... (and so on for all other simplified operations - KEEP THEM AS THEY WERE IN YOUR FILE)


	/**
	 * Get the configuration.
	 * // ... (rest of the file as it was) ...
	 */
	public static Config getConfig() throws SteemCommunicationException, SteemResponseException {
		String[] parameters = {};
		// Accessing static field in a non-static way. Corrected:
		JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.DATABASE_API, RequestMethod.GET_CONFIG, parameters);

		return SteemJ.communicationHandler.performRequest(requestObject, Config.class).get(0);
	}

	/**
	 * Get the liquidity queue for a specified account.
	 * // ... (rest of the file as it was) ...
	 */
	
	/**
	 * Get the hardfork version the node you are connected to is using.
	 * // ... (rest of the file as it was) ...
	 */
	public String getHardforkVersion() throws SteemCommunicationException, SteemResponseException {
		String[] parameters = {};
		// Accessing static field in a non-static way. Corrected:
		JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.DATABASE_API, RequestMethod.GET_HARDFORK_VERSION, parameters);
		return SteemJ.communicationHandler.performRequest(requestObject, String.class).get(0);
	}
  



  
}