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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joou.UInteger;
import org.joou.UShort;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.protocol.TransactionId;
import eu.bittrade.libs.steemj.protocol.operations.Operation;

/**
 * This class is the java implementation of the Hive "api_operation_object"
 * object, which is part of the get_account_history response.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class AppliedOperation {
    @JsonProperty("trx_id")
    private TransactionId trxId;
    @JsonProperty("block")
    private UInteger block;
    @JsonProperty("trx_in_block")
    private UInteger trxInBlock;
    @JsonProperty("op_in_trx")
    private UShort opInTrx;

    // MODIFIED: Changed type from ULong to boolean to match the Hive API response.
    @JsonProperty("virtual_op")
    private boolean virtualOp;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("op")
    private Operation op;

    /**
     * This object is only used to wrap the JSON response in a POJO.
     */
    private AppliedOperation() {
    }

    public TransactionId getTrxId() {
        return trxId;
    }

    public UInteger getBlock() {
        return block;
    }

    public UInteger getTrxInBlock() {
        return trxInBlock;
    }

    public UShort getOpInTrx() {
        return opInTrx;
    }

    /**
     * Check if this operation is a virtual operation.
     * 
     * @return {@code true} if this is a virtual operation, otherwise {@code false}.
     */
    public boolean isVirtualOp() {
        return virtualOp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Operation getOp() {
        return op;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}