package eu.bittrade.libs.steemj.plugins.apis.block.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.bittrade.libs.steemj.protocol.SignedBlock; // <-- CORRECTED IMPORT

/**
 * A wrapper class to handle the nested response from the block_api.get_block call.
 * The Hive API returns a JSON object like {"block": { ... }}, and this class
 * maps that structure.
 */
public class GetBlockReturn {
    private SignedBlock block;

    /**
     * A public default constructor is required for the Jackson JSON parser to
     * instantiate the object.
     */
    public GetBlockReturn() { }

    public GetBlockReturn(SignedBlock block) {
        this.block = block;
    }

    @JsonProperty("block")
    public SignedBlock getBlock() {
        return block;
    }

    public void setBlock(SignedBlock block) {
        this.block = block;
    }
}