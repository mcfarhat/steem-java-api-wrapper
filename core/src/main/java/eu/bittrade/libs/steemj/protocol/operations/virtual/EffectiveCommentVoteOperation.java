/*
 *     This file is part of SteemJ (formerly known as 'Steem-Java-Api-Wrapper')
 * 
 *     This file is a new addition to support Hive's effective_comment_vote_operation.
 */
package eu.bittrade.libs.steemj.protocol.operations.virtual;

import java.math.BigInteger; // Make sure this is imported
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.interfaces.SignatureObject;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.Asset;
import eu.bittrade.libs.steemj.protocol.operations.Operation;

/**
 * Represents the Hive virtual operation "effective_comment_vote_operation".
 *
 * This operation is generated after a vote to show its resulting effect.
 * As a virtual operation, it cannot be signed or broadcasted.
 * 
 * @author Your Name Here
 */
public class EffectiveCommentVoteOperation extends Operation {

    @JsonProperty("voter")
    private AccountName voter;

    @JsonProperty("author")
    private AccountName author;

    @JsonProperty("permlink")
    private Permlink permlink;

    // MODIFIED: Changed to BigInteger to handle large numbers from the API.
    @JsonProperty("weight")
    private BigInteger weight;

    // MODIFIED: Changed to BigInteger to handle large numbers from the API.
    @JsonProperty("rshares")
    private BigInteger rshares;

    // MODIFIED: Changed to BigInteger to handle large numbers from the API.
    @JsonProperty("total_vote_weight")
    private BigInteger totalVoteWeight;

    @JsonProperty("pending_payout")
    private Asset pendingPayout;

    /**
     * Private constructor for Jackson deserialization.
     * It calls the super constructor with 'true' to mark this as a virtual operation.
     */
    private EffectiveCommentVoteOperation() {
        super(true);
    }

    // Since this is a virtual operation, it doesn't require any authorities.
    @Override
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        return requiredAuthoritiesBase;
    }

    @Override
    public void validate(java.util.List<eu.bittrade.libs.steemj.enums.ValidationType> validationType) {
        // No validation needed for virtual operations.
    }

    @Override
    public byte[] toByteArray() throws eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException {
        // Virtual operations are not serialized.
        throw new UnsupportedOperationException("Virtual operations cannot be serialized.");
    }

    // Getters for all fields.
    public AccountName getVoter() { return voter; }
    public AccountName getAuthor() { return author; }
    public Permlink getPermlink() { return permlink; }
    public BigInteger getWeight() { return weight; } // Return type changed
    public BigInteger getRshares() { return rshares; } // Return type changed
    public BigInteger getTotalVoteWeight() { return totalVoteWeight; } // Getter added
    public Asset getPendingPayout() { return pendingPayout; }
}