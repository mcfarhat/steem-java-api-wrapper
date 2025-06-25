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
package eu.bittrade.libs.steemj.chain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joou.UInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.fc.TimePointSec;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;

/**
 * This class represents a Hive "escrow_object" object.
 * 
 * HIVE-FORK-NOTE: The asset fields 'sbd_balance' and 'steem_balance' have been
 * renamed to 'hbd_balance' and 'hive_balance' to match the Hive blockchain's
 * currency names.
 * 
 * @author <a href="https://hive.blog/@dez1337">dez1337</a>
 */
public class EscrowObject {
    // Original type is "id_type" so we use long here.
    @JsonProperty("id")
    private long id;
    @JsonProperty("escrow_id")
    private UInteger escrowId;
    @JsonProperty("from")
    private AccountName from;
    @JsonProperty("to")
    private AccountName to;
    @JsonProperty("agent")
    private AccountName agent;
    @JsonProperty("ratification_deadline")
    private TimePointSec ratificationDeadline;
    @JsonProperty("escrow_expiration")
    private TimePointSec escrowExpiration;
    
    // HIVE-FORK-CHANGE: Renamed from sbd_balance
    @JsonProperty("hbd_balance")
    private LegacyAsset hbdBalance;
    // HIVE-FORK-CHANGE: Renamed from steem_balance
    @JsonProperty("hive_balance")
    private LegacyAsset hiveBalance;
    
    @JsonProperty("pending_fee")
    private LegacyAsset pendingFee;
    @JsonProperty("to_approved")
    private boolean toApproved;
    @JsonProperty("agent_approved")
    private boolean agentApproved;
    @JsonProperty("disputed")
    private boolean disputed;
    @JsonProperty("is_approved")
    private boolean isApproved;

    /**
     * This object is only used to wrap the JSON response in a POJO, so
     * therefore this class should not be instantiated.
     */
    private EscrowObject() {
        this.escrowId = UInteger.valueOf(20);
        this.toApproved = false;
        this.agentApproved = false;
        this.disputed = false;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the escrowId
     */
    public UInteger getEscrowId() {
        return escrowId;
    }

    /**
     * @return the from
     */
    public AccountName getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public AccountName getTo() {
        return to;
    }

    /**
     * @return the agent
     */
    public AccountName getAgent() {
        return agent;
    }

    /**
     * @return the ratificationDeadline
     */
    public TimePointSec getRatificationDeadline() {
        return ratificationDeadline;
    }

    /**
     * @return the escrowExpiration
     */
    public TimePointSec getEscrowExpiration() {
        return escrowExpiration;
    }

    /**
     * @return the hbdBalance
     */
    public LegacyAsset getHbdBalance() {
        return hbdBalance;
    }

    /**
     * @return the hiveBalance
     */
    public LegacyAsset getHiveBalance() {
        return hiveBalance;
    }

    /**
     * @return the pendingFee
     */
    public LegacyAsset getPendingFee() {
        return pendingFee;
    }

    /**
     * @return the toApproved
     */
    public boolean isToApproved() {
        return toApproved;
    }

    /**
     * @return the agentApproved
     */
    public boolean isAgentApproved() {
        return agentApproved;
    }

    /**
     * @return the disputed
     */
    public boolean isDisputed() {
        return disputed;
    }

    /**
     * @return the isApproved
     */
    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
//done