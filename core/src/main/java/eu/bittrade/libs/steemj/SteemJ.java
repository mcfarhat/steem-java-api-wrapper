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
import eu.bittrade.libs.steemj.base.models.FeedHistory;
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
import eu.bittrade.libs.steemj.plugins.apis.condenser.CondenserApi;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.AccountVote;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedAccount;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedDynamicGlobalProperties;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.ExtendedLimitOrder;
import eu.bittrade.libs.steemj.plugins.apis.condenser.models.State;
import eu.bittrade.libs.steemj.plugins.apis.database.DatabaseApi;
import eu.bittrade.libs.steemj.plugins.apis.database.models.Config;
import eu.bittrade.libs.steemj.plugins.apis.database.models.DynamicGlobalProperty;
import eu.bittrade.libs.steemj.plugins.apis.database.models.OrderBook;
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
import eu.bittrade.libs.steemj.protocol.LegacyAsset;
import eu.bittrade.libs.steemj.protocol.Price;
import eu.bittrade.libs.steemj.protocol.PublicKey;
import eu.bittrade.libs.steemj.protocol.SignedBlock;
import eu.bittrade.libs.steemj.protocol.TransactionId;
import eu.bittrade.libs.steemj.protocol.enums.LegacyAssetSymbolType;
import eu.bittrade.libs.steemj.protocol.operations.Operation;
import eu.bittrade.libs.steemj.protocol.operations.VoteOperation;
import eu.bittrade.libs.steemj.util.SteemJUtils;

public class SteemJ {
    private static CommunicationHandler communicationHandler;

    public SteemJ() throws SteemCommunicationException, SteemResponseException {
        SteemJ.communicationHandler = new CommunicationHandler();
    }

    public List<List<AccountName>> getKeyReferences(List<PublicKey> publicKeys) throws SteemCommunicationException, SteemResponseException {
        return AccountByKeyApi.getKeyReferences(SteemJ.communicationHandler, new GetKeyReferencesArgs(publicKeys)).getAccounts();
    }

    // #########################################################################
    // ## ACCOUNT HISTORY API ##################################################
    // #########################################################################

    /**
     * Get a sequence of operations included/generated within a particular block.
     *
     * @param blockNum      The block number to retrieve operations from.
     * @param onlyVirtual   Whether to only include virtual operations.
     * @return A list of {@link AppliedOperation AppliedOperations} from the block.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException      If the API returns an error.
     */
    public List<AppliedOperation> getOpsInBlock(long blockNum, boolean onlyVirtual)
            throws SteemCommunicationException, SteemResponseException {
        GetOpsInBlockArgs params = new GetOpsInBlockArgs(blockNum, onlyVirtual);
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
     * Retrieve a block from the blockchain using the 'block_api'.
     *
     * @param blockNumber The number of the block to retrieve.
     * @return The block.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException      If the API returns an error.
     */
    public SignedBlock getBlock(long blockNumber) throws SteemCommunicationException, SteemResponseException {
        return BlockApi.getBlock(SteemJ.communicationHandler, blockNumber).getBlock();
    }

    public List<OperationHistoryEntry> getAccountHistory(AccountName accountName, ULong start, UInteger limit) throws SteemCommunicationException, SteemResponseException {
        return AccountHistoryApi.getAccountHistory(SteemJ.communicationHandler, accountName, start, limit).getHistory();
    }

    public List<OperationHistoryEntry> getAccountHistory(AccountName accountName, ULong start, UInteger limit, Boolean includeReversible, Long operationFilterLow, Long operationFilterHigh) throws SteemCommunicationException, SteemResponseException {
        return AccountHistoryApi.getAccountHistory(SteemJ.communicationHandler, accountName, start, limit, includeReversible, operationFilterLow, operationFilterHigh).getHistory();
    }

    @Deprecated
    public Map<UInteger, AppliedOperation> getAccountHistoryAsMap(AccountName accountName, ULong start, UInteger limit) throws SteemCommunicationException, SteemResponseException {
        List<OperationHistoryEntry> historyList = AccountHistoryApi.getAccountHistory(SteemJ.communicationHandler, new GetAccountHistoryArgs(accountName, start, limit)).getHistory();
        return historyList.stream().collect(Collectors.toMap(entry -> UInteger.valueOf(entry.getHistoryIndex()), OperationHistoryEntry::getOperation));
    }

    // ... (THE REST OF THE FILE IS UNCHANGED) ...
    public void broadcastTransaction(SignedTransaction transaction) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        NetworkBroadcastApi.broadcastTransaction(SteemJ.communicationHandler, transaction);
    }
    public BroadcastTransactionSynchronousReturn broadcastTransactionSynchronous(SignedTransaction transaction) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        return NetworkBroadcastApi.broadcastTransactionSynchronous(SteemJ.communicationHandler, transaction);
    }
    public void broadcastBlock(SignedBlock signedBlock) throws SteemCommunicationException, SteemResponseException {
        NetworkBroadcastApi.broadcastBlock(SteemJ.communicationHandler, signedBlock);
    }
    public State getState(Permlink path) throws SteemCommunicationException, SteemResponseException {
        return CondenserApi.getState(SteemJ.communicationHandler, path);
    }
    public List<AccountName> getActiveWitnesses() throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getActiveWitnesses(SteemJ.communicationHandler);
    }
    public DynamicGlobalProperty getDynamicGlobalProperties() throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getDynamicGlobalProperties(SteemJ.communicationHandler);
    }
    public int getAccountCount() throws SteemCommunicationException, SteemResponseException {
        return 0;
    }
    public List<ExtendedAccount> getAccounts(List<AccountName> accountNames) throws SteemCommunicationException, SteemResponseException {
        if (SteemJ.communicationHandler == null) {
            throw new SteemCommunicationException("CommunicationHandler is not initialized in SteemJ instance.");
        }
        return CondenserApi.getAccounts(SteemJ.communicationHandler, accountNames);
    }
    public List<AccountVote> getAccountVotes(AccountName accountName) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<VoteState> getActiveVotes(AccountName author, Permlink permlink) throws SteemCommunicationException, SteemResponseException {
        return TagsApi.getActiveVotes(SteemJ.communicationHandler, new GetActiveVotesArgs(author, permlink)).getVotes();
    }
    public ChainProperties getChainProperties() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public Discussion getContent(AccountName author, Permlink permlink) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<Discussion> getContentReplies(AccountName author, Permlink permlink) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public Object[] getConversionRequests(AccountName account) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public Price getCurrentMedianHistoryPrice() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<Discussion> getDiscussionsBy(DiscussionQuery discussionQuery, DiscussionSortType sortBy) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<Discussion> getDiscussionsByAuthorBeforeDate(AccountName author, Permlink permlink, String date, int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public FeedHistory getFeedHistory() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public ScheduledHardfork getNextScheduledHarfork() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<ExtendedLimitOrder> getOpenOrders(AccountName accountName) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public OrderBook getOrderBookUsingDatabaseApi(int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<String[]> getPotentialSignatures() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<Discussion> getRepliesByLastUpdate(AccountName username, Permlink permlink, int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public RewardFund getRewardFund(RewardFundType rewordFundType) throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.getRewardFunds(SteemJ.communicationHandler, rewordFundType);
    }
    public String getTransactionHex(SignedTransaction signedTransaction) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public Witness getWitnessByAccount(AccountName witnessName) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<Witness> getWitnessByVote(AccountName witnessName, int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public int getWitnessCount() throws SteemCommunicationException, SteemResponseException {
        return 0;
    }
    public List<Witness> getWitnesses() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public WitnessSchedule getWitnessSchedule() throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<String> lookupAccounts(String pattern, int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public List<String> lookupWitnessAccounts(String pattern, int limit) throws SteemCommunicationException, SteemResponseException {
        return null;
    }
    public boolean verifyAuthority(SignedTransaction signedTransaction) throws SteemCommunicationException, SteemResponseException {
        return DatabaseApi.verifyAccountAuthority(SteemJ.communicationHandler, null).isValid();
    }
    public List<FollowApiObject> getFollowers(AccountName following, AccountName startFollower, FollowType type, UInteger limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowers(SteemJ.communicationHandler, new GetFollowersArgs(following, startFollower, type, limit)).getFollowers();
    }
    public List<FollowApiObject> getFollowing(AccountName follower, AccountName startFollowing, FollowType type, long limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowing(SteemJ.communicationHandler, follower, startFollowing, type, UInteger.valueOf(limit));
    }
    public FollowCountApiObject getFollowCount(AccountName account) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFollowCount(SteemJ.communicationHandler, account);
    }
    public List<FeedEntry> getFeedEntries(AccountName account, int entryId, short limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFeedEntries(SteemJ.communicationHandler, account, entryId, limit);
    }
    public List<CommentFeedEntry> getFeed(AccountName account, int entryId, short limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getFeed(SteemJ.communicationHandler, account, entryId, limit);
    }
    public List<BlogEntry> getBlogEntries(AccountName account, int entryId, short limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlogEntries(SteemJ.communicationHandler, account, entryId, limit);
    }
    public List<CommentBlogEntry> getBlog(AccountName account, int entryId, short limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlog(SteemJ.communicationHandler, account, entryId, limit);
    }
    public List<AccountReputation> getAccountReputations(AccountName accountName, int limit) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getAccountReputations(SteemJ.communicationHandler, accountName, limit);
    }
    public List<AccountName> getRebloggedBy(AccountName author, Permlink permlink) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getRebloggedBy(SteemJ.communicationHandler, author, permlink);
    }
    public List<PostsPerAuthorPair> getBlogAuthors(AccountName blogAccount) throws SteemCommunicationException, SteemResponseException {
        return FollowApi.getBlogAuthors(SteemJ.communicationHandler, blogAccount);
    }
    public GetTickerReturn getTicker() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getTicker(SteemJ.communicationHandler);
    }
    public GetVolumeReturn getVolume() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getVolume(SteemJ.communicationHandler);
    }
    public eu.bittrade.libs.steemj.plugins.apis.market.history.models.GetOrderBookReturn getOrderBookUsingMarketApi(short limit) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getOrderBook(SteemJ.communicationHandler, new GetOrderBookArgs(UInteger.valueOf(limit)));
    }
    public List<MarketTrade> getTradeHistory(TimePointSec start, TimePointSec end, UInteger limit) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getTradeHistory(SteemJ.communicationHandler, new GetTradeHistoryArgs(start, end, limit)).getTrades();
    }
    public List<MarketTrade> getRecentTrades(short limit) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getRecentTrades(SteemJ.communicationHandler, new GetRecentTradesArgs(UInteger.valueOf(limit))).getTrades();
    }
    public List<Bucket> getMarketHistory(long bucketSeconds, TimePointSec start, TimePointSec end) throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getMarketHistory(SteemJ.communicationHandler, new GetMarketHistoryArgs(UInteger.valueOf(bucketSeconds), start, end)).getBuckets();
    }
    public List<UInteger> getMarketHistoryBuckets() throws SteemCommunicationException, SteemResponseException {
        return MarketHistoryApi.getMarketHistoryBuckets(SteemJ.communicationHandler).getBucketSizes();
    }
    public List<Tag> getTrendingTags(String firstTagPattern, int limit) throws SteemCommunicationException, SteemResponseException {
        return TagsApi.getTrendingTags(SteemJ.communicationHandler, firstTagPattern, limit);
    }
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
    public double calculateRemainingBandwidth(AccountName accountName) throws SteemCommunicationException, SteemResponseException {
        ExtendedDynamicGlobalProperties extendedDynamicGlobalProperties = CondenserApi.getDynamicGlobalProperties(SteemJ.communicationHandler);
        List<AccountName> accountsToFetch = Lists.newArrayList(accountName);
        List<ExtendedAccount> extendedAccountsResult = CondenserApi.getAccounts(SteemJ.communicationHandler, accountsToFetch);
        if (extendedAccountsResult == null || extendedAccountsResult.isEmpty()) {
            throw new InvalidParameterException("No account has been found matching the provided account name: " + accountName.getName());
        }
        ExtendedAccount specificAccount = extendedAccountsResult.get(0);
        return calculateRemainingBandwidth(extendedDynamicGlobalProperties, specificAccount);
    }
    public static double calculateRemainingBandwidth(ExtendedDynamicGlobalProperties extendedDynamicGlobalProperties, Account account) {
        long maxVirtualBandwidth = extendedDynamicGlobalProperties.getMaxVirtualBandwidth().longValue();
        long totalVestingShares = extendedDynamicGlobalProperties.getTotalVestingShares().getAmount();
        if (totalVestingShares == 0) return 0;
        long userVestingShares = account.getVestingShares().getAmount() + account.getReceivedVestingShares().getAmount();
        long allocatedBandwidth = (userVestingShares * maxVirtualBandwidth) / totalVestingShares;
        return allocatedBandwidth;
    }
    public static ImmutablePair<PublicKey, String> getPrivateKeyFromPassword(AccountName account, PrivateKeyType role, String steemPassword) {
        String seed = account.getName() + role.name().toLowerCase() + steemPassword;
        ECKey keyPair = ECKey.fromPrivate(Sha256Hash.hash(seed.getBytes(), 0, seed.length()));
        return new ImmutablePair<>(new PublicKey(keyPair), SteemJUtils.privateKeyToWIF(keyPair));
    }
    public void vote(AccountName postOrCommentAuthor, Permlink postOrCommentPermlink, short percentage) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        if (SteemJConfig.getInstance().getDefaultAccount().isEmpty()) {
            throw new InvalidParameterException("Using the upVote method without providing an account requires to have a default account configured.");
        }
        this.vote(SteemJConfig.getInstance().getDefaultAccount(), postOrCommentAuthor, postOrCommentPermlink, percentage);
    }
    public void vote(AccountName voter, AccountName postOrCommentAuthor, Permlink postOrCommentPermlink, short percentage) throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
        if (percentage < -100 || percentage > 100 || percentage == 0) {
            throw new InvalidParameterException("Please provide a percentage between -100 and 100 which is also not 0.");
        }
        VoteOperation voteOperation = new VoteOperation(voter, postOrCommentAuthor, postOrCommentPermlink, (short) (percentage * 100));
        ArrayList<Operation> operations = new ArrayList<>();
        operations.add(voteOperation);
        DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();
        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations, null);
        signedTransaction.sign();
        this.broadcastTransaction(signedTransaction);
    }
    public static Config getConfig() throws SteemCommunicationException, SteemResponseException {
        String[] parameters = {};
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.DATABASE_API, RequestMethod.GET_CONFIG, parameters);
        return SteemJ.communicationHandler.performRequest(requestObject, Config.class).get(0);
    }
    public String getHardforkVersion() throws SteemCommunicationException, SteemResponseException {
        String[] parameters = {};
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.DATABASE_API, RequestMethod.GET_HARDFORK_VERSION, parameters);
        return SteemJ.communicationHandler.performRequest(requestObject, String.class).get(0);
    }
}