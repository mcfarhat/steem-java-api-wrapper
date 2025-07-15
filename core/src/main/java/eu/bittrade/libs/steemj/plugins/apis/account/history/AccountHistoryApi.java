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

import java.util.Collections;
import java.util.Map;

import org.joou.UInteger;
import org.joou.ULong;

import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.communication.jrpc.JsonRPCRequest;
import eu.bittrade.libs.steemj.enums.RequestMethod;
import eu.bittrade.libs.steemj.enums.SteemApiType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetAccountHistoryArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetAccountHistoryReturn;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockReturn;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.AnnotatedSignedTransaction;

/**
 * This class implements the "account_history_api".
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 * @author <a href="https://github.com/AI-Hive">AI-Hive</a>
 */
public class AccountHistoryApi {
    /** Add a private constructor to hide the implicit public one. */
    private AccountHistoryApi() {
    }

    // #########################################################################
    // # HIVE: NEW AND UPDATED get_account_history METHODS                     #
    // #########################################################################

    /**
     * Returns a history of all operations for a given account. This is the most flexible method, providing access
     * to all available filters for the Hive API.
     *
     * @param communicationHandler
     *            A {@link eu.bittrade.libs.steemj.communication.CommunicationHandler CommunicationHandler}
     *            instance that should be used to send the request.
     * @param account
     *            The name of the account to get the history for.
     * @param start
     *            The sequence number of the operation to start from. For Hive, use -1 to start from the oldest history,
     *            or a large number (e.g. {@code ULong.MAX_VALUE}) for the most recent.
     * @param limit
     *            The maximum number of operations to return (must be between 1 and 1000).
     * @param includeReversible
     *            (Optional) If set to true, also operations from reversible blocks will be included. Can be null.
     * @param operationFilterLow
     *            (Optional) A bitmask for filtering operations 0-63. Can be null.
     * @param operationFilterHigh
     *            (Optional) A bitmask for filtering operations 64-127. Can be null.
     * @return The history of operations for the given account.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error or the response cannot be parsed.
     */
    public static GetAccountHistoryReturn getAccountHistory(CommunicationHandler communicationHandler, AccountName account,
            ULong start, UInteger limit, Boolean includeReversible, Long operationFilterLow, Long operationFilterHigh)
            throws SteemCommunicationException, SteemResponseException {

        if (limit.longValue() < 1 || limit.longValue() > 1000) {
            throw new IllegalArgumentException("Limit must be between 1 and 1000.");
        }

        GetAccountHistoryArgs params = new GetAccountHistoryArgs(account, start, limit, includeReversible,
                operationFilterLow, operationFilterHigh);
        
        return getAccountHistory(communicationHandler, params);
    }

    /**
     * A convenience method to get account history without the optional Hive filters.
     *
     * @param communicationHandler A CommunicationHandler instance.
     * @param account The name of the account.
     * @param start The sequence number to start from. For Hive, use -1 for oldest, or {@code ULong.MAX_VALUE} for newest.
     * @param limit The maximum number of operations to return (1-1000).
     * @return The history of operations for the given account.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error.
     */
    public static GetAccountHistoryReturn getAccountHistory(CommunicationHandler communicationHandler, AccountName account,
            ULong start, UInteger limit) throws SteemCommunicationException, SteemResponseException {
        return getAccountHistory(communicationHandler, account, start, limit, null, null, null);
    }
    
    /**
     * Get all operations performed by the specified account by passing a parameter object.
     * This method is kept for backward compatibility but it is recommended to use the more direct methods.
     *
     * @param communicationHandler A CommunicationHandler instance.
     * @param getAccountHistoryArgs The container for all arguments for this call.
     * @return A list of activities for the account.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error.
     */
    public static GetAccountHistoryReturn getAccountHistory(CommunicationHandler communicationHandler,
            GetAccountHistoryArgs getAccountHistoryArgs) throws SteemCommunicationException, SteemResponseException {
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.ACCOUNT_HISTORY_API,
                RequestMethod.GET_ACCOUNT_HISTORY, getAccountHistoryArgs);

        return communicationHandler.performRequest(requestObject, GetAccountHistoryReturn.class).get(0);
    }

    // #########################################################################
    // # OTHER API METHODS                                                     #
    // #########################################################################

     // In AccountHistoryApi.java

        /**
     * Get a sequence of operations included/generated within a particular block.
     */
    public static GetOpsInBlockReturn getOpsInBlock(CommunicationHandler communicationHandler,
           GetOpsInBlockArgs getOpsInBlockArgs) throws SteemCommunicationException, SteemResponseException {
        
        // ########## THE FINAL, CORRECT IMPLEMENTATION ##########
        // The get_ops_in_block API expects a JSON object directly as its parameters,
        // NOT wrapped in an array. We pass the GetOpsInBlockArgs object directly.
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.ACCOUNT_HISTORY_API,
                RequestMethod.GET_OPS_IN_BLOCK, getOpsInBlockArgs);

        return communicationHandler.performRequest(requestObject, GetOpsInBlockReturn.class).get(0);
    }
    /**
     * Find a transaction by its transaction ID.
     *
     * @param communicationHandler A CommunicationHandler instance.
     * @param transactionId The hexadecimal string representation of the transaction ID to search for.
     * @return The annotated signed transaction if found.
     * @throws SteemCommunicationException If a communication error occurs.
     * @throws SteemResponseException If the API returns an error (e.g., transaction not found).
     */
    public static AnnotatedSignedTransaction getTransaction(CommunicationHandler communicationHandler,
            String transactionId) throws SteemCommunicationException, SteemResponseException {
        // The API expects a JSON object like {"id": "..."} as the parameter.
        Map<String, String> params = Collections.singletonMap("id", transactionId);
        
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.ACCOUNT_HISTORY_API,
                RequestMethod.GET_TRANSACTION, params);

        return communicationHandler.performRequest(requestObject, AnnotatedSignedTransaction.class).get(0);
    }
}

//done