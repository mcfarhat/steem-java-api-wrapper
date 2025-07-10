/*
 *     This file is part of SteemJ (formerly known as 'Steem-Java-Api-Wrapper')
 * 
 *     This file is a new addition to support Hive's effective_comment_vote_operation.
 */
package eu.bittrade.libs.steemj.protocol.operations.virtual;

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

    @JsonProperty("weight")
    private long weight;

    @JsonProperty("rshares")
    private long rshares;

    @JsonProperty("total_vote_weight")
    private long totalVoteWeight;

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

    // You can add getters for all the fields here if you need to access their data.
    public AccountName getVoter() { return voter; }
    public AccountName getAuthor() { return author; }
    public Permlink getPermlink() { return permlink; }
    public long getRshares() { return rshares; }
    public Asset getPendingPayout() { return pendingPayout; }
}