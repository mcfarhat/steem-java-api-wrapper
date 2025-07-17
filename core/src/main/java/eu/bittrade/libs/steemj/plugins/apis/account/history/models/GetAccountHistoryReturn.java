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

import java.util.List; // Changed from Map to List

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
// The old deserializer is no longer needed. A new one will be attached to OperationHistoryEntry.
// import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
// import eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer.AppliedOperationHashMapDeserializer;

/**
 * This class implements the Hive "get_account_history_return" object.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class GetAccountHistoryReturn {
    // MODIFIED: The 'history' field is now a List of a new type, OperationHistoryEntry,
    // to correctly model the API's response structure: [ [index, operation], ... ]
    @JsonProperty("history")
    private List<OperationHistoryEntry> history;

    /**
     * This object is only used to wrap the JSON response in a POJO, so
     * therefore this class should not be instantiated.
     */
    private GetAccountHistoryReturn() {
    }

    /**
     * Get the requested history for the account. The history is represented by a
     * list of all operations. Each entry in the list contains the operation's
     * sequence number in the account's history and the operation object itself.
     * 
     * @return A list of account history entries.
     */
    public List<OperationHistoryEntry> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}