package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.bittrade.libs.steemj.plugins.apis.account.history.models.deserializer.GetOpsInBlockReturnDeserializer;

@JsonDeserialize(using = GetOpsInBlockReturnDeserializer.class)
public class GetOpsInBlockReturn {
    @JsonProperty("ops")
    private List<AppliedOperation> operations;

    public GetOpsInBlockReturn() {}
    
    // NEW constructor for our deserializer.
    public GetOpsInBlockReturn(List<AppliedOperation> operations) {
        this.operations = operations;
    }

    public List<AppliedOperation> getOperations() {
        return operations;
    }
}