// File: eu/bittrade/libs/steemj/plugins/apis/account/history/models/OperationHistoryEntry.java
package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer.OperationHistoryEntryDeserializer;

/**
 * Represents a single entry in the account history list returned by the Hive API.
 * The API returns history entries as a JSON array of the form "[history_index, applied_operation]",
 * so a custom deserializer is required to map this to a proper Java object.
 *
 * This class replaces the old Map-based approach.
 *
 */
@JsonDeserialize(using = OperationHistoryEntryDeserializer.class)
public class OperationHistoryEntry {

    private long historyIndex;
    private AppliedOperation operation;

    /**
     * This constructor is intended to be used by the custom deserializer.
     * @param historyIndex The sequence number of this entry in the account's history.
     * @param operation The applied operation details.
     */
    public OperationHistoryEntry(long historyIndex, AppliedOperation operation) {
        this.historyIndex = historyIndex;
        this.operation = operation;
    }

    /**
     * @return The sequence number of this operation in the account's overall history.
     */
    public long getHistoryIndex() {
        return historyIndex;
    }

    /**
     * @return The actual operation object, containing details like the block number, timestamp, and operation type.
     */
    public AppliedOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("historyIndex", historyIndex)
                .append("operation", operation)
                .toString();
    }
}
//done 