package eu.bittrade.libs.steemj.protocol.operations;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.enums.ValidationType;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.interfaces.SignatureObject;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;

/**
 * A new virtual operation in Hive. It is generated after an account is successfully created.
 *
 * @author <a href="https://github.com/AI-Hive">AI-Hive</a>
 */
public class AccountCreatedOperation extends Operation {
    @JsonProperty("creator")
    private AccountName creator;
    @JsonProperty("new_account_name")
    private AccountName newAccountName;
    @JsonProperty("initial_vesting_shares")
    private LegacyAsset initialVestingShares;
    @JsonProperty("initial_delegation")
    private LegacyAsset initialDelegation;

    /**
     * Private constructor for Jackson deserialization.
     */
    private AccountCreatedOperation() {
        super(true); // This is a virtual operation
    }
    
    /**
     * This method is implemented to fulfill the abstract parent's contract, but since
     * virtual operations are not signed, it simply returns the provided map unmodified.
     */
    @Override
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        // Virtual operations are not signed, so we return the map as-is.
        return requiredAuthoritiesBase;
    }

    @Override
    public void validate(List<ValidationType> validationType) {
        // No specific validation needed for this virtual operation's fields beyond the default.
    }

    /**
     * This method is not supported for virtual operations as they cannot be serialized.
     * @throws SteemInvalidTransactionException This exception will always be thrown.
     */
    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        throw new SteemInvalidTransactionException(
                "The 'account_created_operation' is a virtual operation and can't be serialized."
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public AccountName getCreator() { return creator; }
    public AccountName getNewAccountName() { return newAccountName; }
    public LegacyAsset getInitialVestingShares() { return initialVestingShares; }
    public LegacyAsset getInitialDelegation() { return initialDelegation; }
}