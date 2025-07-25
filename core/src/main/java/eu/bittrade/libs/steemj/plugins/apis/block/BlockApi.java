package eu.bittrade.libs.steemj.plugins.apis.block;

import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.communication.jrpc.JsonRPCRequest;
import eu.bittrade.libs.steemj.enums.RequestMethod;
import eu.bittrade.libs.steemj.enums.SteemApiType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockArgs;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockHeaderArgs;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockHeaderReturn;
import eu.bittrade.libs.steemj.plugins.apis.block.models.GetBlockReturn;
import java.util.HashMap;
import java.util.Map;

public class BlockApi {
    private BlockApi() { }

    // Old methods are left untouched.
    public static GetBlockHeaderReturn getBlockHeader(CommunicationHandler comm, GetBlockHeaderArgs args) throws SteemCommunicationException, SteemResponseException {
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.BLOCK_API, RequestMethod.GET_BLOCK_HEADER, args);
        return comm.performRequest(requestObject, GetBlockHeaderReturn.class).get(0);
    }
    public static GetBlockReturn getBlock(CommunicationHandler comm, GetBlockArgs args) throws SteemCommunicationException, SteemResponseException {
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.BLOCK_API, RequestMethod.GET_BLOCK, args);
        return comm.performRequest(requestObject, GetBlockReturn.class).get(0);
    }

    // ######################################################################
    // ### THIS IS OUR CORRECTED HIVE-COMPATIBLE METHOD ###
    // ######################################################################
    public static GetBlockReturn getBlock(CommunicationHandler communicationHandler, long blockNum) throws SteemCommunicationException, SteemResponseException {
        // Create a Map (which becomes a JSON Object), NOT a List.
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("block_num", blockNum);
        
        // Pass the Map directly. The library will format it as params: { ... }
        JsonRPCRequest requestObject = new JsonRPCRequest(SteemApiType.BLOCK_API, RequestMethod.GET_BLOCK, parameters);
        
        return communicationHandler.performRequest(requestObject, GetBlockReturn.class).get(0);
    }
}