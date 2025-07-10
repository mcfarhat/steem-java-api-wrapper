// File: eu/bittrade/libs/steemj/plugins/apis/account/history/models/deserializer/OperationHistoryEntryDeserializer.java
package eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
 * @author Your Name Here (for the modifications)
 */
public class OperationHistoryEntryDeserializer extends JsonDeserializer<OperationHistoryEntry> {
    @Override
    public OperationHistoryEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = jp.getCodec().readTree(jp);

        if (!rootNode.isArray() || rootNode.size() != 2) {
            throw new IOException("Expected a JSON array with two elements [index, operation_object] for OperationHistoryEntry.");
        }

        // The first element is the history index number.
        long historyIndex = rootNode.get(0).asLong();
        
        // The second element is the operation object.
        JsonNode operationNode = rootNode.get(1);

        // ### START OF HIVE FIX ###
        // The Hive API for get_account_history nests the actual operation data
        // inside a "value" field within the "op" object. We need to restructure this.
        if (operationNode.has("op") && operationNode.get("op").has("value")) {
            JsonNode opField = operationNode.get("op");
            JsonNode valueField = opField.get("value");

            // We need to create a new, flattened JSON object.
            // Start by copying all fields from the "value" object.
            ObjectNode newOpNode = (ObjectNode) valueField.deepCopy();

            // The "value" object does not contain the "type", but the parent "op" object does.
            // We must copy the "type" field from the parent into our new object so that
            // Jackson's type resolver can work correctly.
            if (opField.has("type")) {
                newOpNode.set("type", opField.get("type"));
            }

            // Now, we replace the original "op" field in the main operation object
            // with our newly created, flattened "op" field.
            ((ObjectNode) operationNode).set("op", newOpNode);
        }
        // ### END OF HIVE FIX ###

        // Now that the JSON is in a standard format, we can let Jackson deserialize it automatically.
        AppliedOperation operation = jp.getCodec().treeToValue(operationNode, AppliedOperation.class);

        return new OperationHistoryEntry(historyIndex, operation);
    }
}