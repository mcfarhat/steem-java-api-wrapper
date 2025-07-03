// File: eu/bittrade/libs/steemj/plugins/apis/account/history/models/deserializer/OperationHistoryEntryDeserializer.java
package eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import eu.bittrade.libs.steemj.plugins.apis.account.history.models.AppliedOperation;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.OperationHistoryEntry;

/**
 * A custom Jackson deserializer for the {@link OperationHistoryEntry} class.
 * The Hive API returns account history entries as a JSON array of the form "[int, object]",
 * which cannot be mapped automatically. This deserializer handles that specific structure.
 *
 * This class is the replacement for the old AppliedOperationHashMapDeserializer.
 *
 * @author <a href="https://github.com/AI-Hive">AI-Hive</a>
 */
public class OperationHistoryEntryDeserializer extends JsonDeserializer<OperationHistoryEntry> {
    @Override
    public OperationHistoryEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        if (!node.isArray() || node.size() != 2) {
            throw new IOException("Expected a JSON array with two elements [index, operation_object] for OperationHistoryEntry.");
        }

        // The first element is the history index number.
        long historyIndex = node.get(0).asLong();
        
        // The second element is the operation object.
        // We use the parser's codec to deserialize this part of the tree into an AppliedOperation object.
        AppliedOperation operation = jp.getCodec().treeToValue(node.get(1), AppliedOperation.class);

        return new OperationHistoryEntry(historyIndex, operation);
    }
}
// done
