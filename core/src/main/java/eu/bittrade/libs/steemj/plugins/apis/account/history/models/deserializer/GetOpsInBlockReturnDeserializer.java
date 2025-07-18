package eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockReturn;
import eu.bittrade.libs.steemj.plugins.apis.account.history.models.GetOpsInBlockReturnProxy;

public class GetOpsInBlockReturnDeserializer extends JsonDeserializer<GetOpsInBlockReturn> {
    @Override
    public GetOpsInBlockReturn deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode rootNode = p.getCodec().readTree(p);

        JsonNode opsArray = rootNode.get("ops");
        if (opsArray != null && opsArray.isArray()) {
            for (JsonNode appliedOpNode : opsArray) {
                JsonNode op = appliedOpNode.get("op");
                JsonNode value = op.get("value");
                if (op instanceof ObjectNode && value != null && value.isObject()) {
                    ObjectNode opObjectNode = (ObjectNode) op;
                    opObjectNode.setAll((ObjectNode) value);
                    opObjectNode.remove("value");
                }
            }
        }

        // THIS IS THE FIX:
        // 1. Parse into the simple PROXY class to avoid the loop.
        GetOpsInBlockReturnProxy proxy = p.getCodec().treeToValue(rootNode, GetOpsInBlockReturnProxy.class);
        // 2. Manually create the REAL object.
        return new GetOpsInBlockReturn(proxy.getOperations());
    }
}