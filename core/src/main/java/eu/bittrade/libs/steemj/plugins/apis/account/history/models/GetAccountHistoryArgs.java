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
package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joou.UInteger;
import org.joou.ULong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.plugins.apis.account.history.AccountHistoryApi;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class implements the arguments for the "get_account_history" API call, updated for the Hive API.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 * @author <a href="https://github.com/AI-Hive">AI-Hive</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAccountHistoryArgs {
    @JsonProperty("account")
    private AccountName account;
    // For Hive, -1 means oldest. A very large number like ULong.MAX_VALUE means newest.
    @JsonProperty("start")
    private ULong start;
    @JsonProperty("limit")
    private UInteger limit;

    // HIVE-SPECIFIC OPTIONAL PARAMETERS
    @JsonProperty("include_reversible")
    private Boolean includeReversible;
    @JsonProperty("operation_filter_low")
    private Long operationFilterLow;
    @JsonProperty("operation_filter_high")
    private Long operationFilterHigh;

    /**
     * Create a new {@link GetAccountHistoryArgs} instance to be passed to the
     * {@link AccountHistoryApi#getAccountHistory(CommunicationHandler, GetAccountHistoryArgs)} method.
     * This constructor includes all Hive-specific parameters.
     * 
     * @param account
     *            The account name to request the history for. (Required)
     * @param start
     *            The sequence number to start from. For Hive, use -1 for the oldest history, or a large number (e.g. ULong.MAX_VALUE) for the most recent. If null, defaults to -1.
     * @param limit
     *            The maximum number of entries to return (max 1000). If null, defaults to 1000.
     * @param includeReversible
     *            (Optional) If set to true, also operations from reversible blocks will be included.
     * @param operationFilterLow
     *            (Optional) Bitmask for filtering operations 0-63.
     * @param operationFilterHigh
     *            (Optional) Bitmask for filtering operations 64-127.
     */
    @JsonCreator
    public GetAccountHistoryArgs(@JsonProperty("account") AccountName account,
            @Nullable @JsonProperty("start") ULong start, 
            @Nullable @JsonProperty("limit") UInteger limit,
            @Nullable @JsonProperty("include_reversible") Boolean includeReversible,
            @Nullable @JsonProperty("operation_filter_low") Long operationFilterLow,
            @Nullable @JsonProperty("operation_filter_high") Long operationFilterHigh) {
        this.setAccount(account);
        this.setStart(start);
        this.setLimit(limit);
        this.setIncludeReversible(includeReversible);
        this.setOperationFilterLow(operationFilterLow);
        this.setOperationFilterHigh(operationFilterHigh);
    }
    
    /**
     * Convenience constructor without optional Hive filters.
     * 
     * @param account
     *            The account name to request the history for. (Required)
     * @param start
     *            The sequence number to start from. For Hive, use -1 for the oldest history.
     * @param limit
     *            The maximum number of entries to return (max 1000).
     */
    public GetAccountHistoryArgs(AccountName account, ULong start, UInteger limit) {
        this(account, start, limit, null, null, null);
    }

    public AccountName getAccount() {
        return account;
    }

    public void setAccount(AccountName account) {
        this.account = SteemJUtils.setIfNotNull(account, "The account is required.");
    }

    public ULong getStart() {
        return start;
    }

    public void setStart(ULong start) {
        // Default value for Hive API is -1 to get the most recent operations.
        // The API doc is a bit ambiguous. "-1" can mean "latest item" or "oldest item" depending on context.
        // We'll follow the original library's default. The user can explicitly pass ULong.MAX_VALUE for oldest.
        // EDIT: Per Hive API docs, start: -1 means "reverse history" (oldest). Let's stick to that.
        this.start = SteemJUtils.setIfNotNull(start, ULong.valueOf(-1));
    }

    public UInteger getLimit() {
        return limit;
    }

    public void setLimit(@Nullable UInteger limit) {
        UInteger sanitizedLimit = limit;
        if (limit != null && limit.longValue() > 1000) {
            sanitizedLimit = UInteger.valueOf(1000);
        }
        this.limit = SteemJUtils.setIfNotNull(sanitizedLimit, UInteger.valueOf(1000));
    }

    // GETTERS AND SETTERS FOR HIVE-SPECIFIC FIELDS

    public Boolean getIncludeReversible() {
        return includeReversible;
    }

    public void setIncludeReversible(Boolean includeReversible) {
        this.includeReversible = includeReversible;
    }

    public Long getOperationFilterLow() {
        return operationFilterLow;
    }

    public void setOperationFilterLow(Long operationFilterLow) {
        this.operationFilterLow = operationFilterLow;
    }

    public Long getOperationFilterHigh() {
        return operationFilterHigh;
    }

    public void setOperationFilterHigh(Long operationFilterHigh) {
        this.operationFilterHigh = operationFilterHigh;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

//done
