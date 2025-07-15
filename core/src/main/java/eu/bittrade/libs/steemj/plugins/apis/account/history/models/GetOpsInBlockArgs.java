package eu.bittrade.libs.steemj.plugins.apis.account.history.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetOpsInBlockArgs {
    @JsonProperty("block_num")
    private long blockNum;

    @JsonProperty("only_virtual")
    private boolean onlyVirtual;

    // A private constructor for the parser.
    private GetOpsInBlockArgs() {}

    public GetOpsInBlockArgs(long blockNum, boolean onlyVirtual) {
        this.blockNum = blockNum;
        this.onlyVirtual = onlyVirtual;
    }
    
    // Getters for the fields.
    public long getBlockNum() { return blockNum; }
    public boolean getOnlyVirtual() { return onlyVirtual; }
}