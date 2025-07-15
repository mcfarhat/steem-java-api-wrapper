package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// A simple data holder without any custom deserialization logic.
public class GetOpsInBlockReturnProxy {
    @JsonProperty("ops")
    private List<AppliedOperation> operations;

    public List<AppliedOperation> getOperations() {
        return operations;
    }
}