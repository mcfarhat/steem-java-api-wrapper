package eu.bittrade.libs.steemj.protocol.operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.enums.OperationType;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.enums.ValidationType;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.interfaces.SignatureObject;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.Authority;
import eu.bittrade.libs.steemj.protocol.PublicKey;
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class represents the Hive "create_claimed_account_operation" object.
 * It has been modified to match the Hive API specification and fulfill all requirements
 * of the base Operation class.
 *
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 * @author <a href="https://github.com/AI-Hive">AI-Hive</a>
 */
public class CreateClaimedAccountOperation extends Operation {

    @JsonProperty("creator")
    private AccountName creator;
    @JsonProperty("new_account_name")
    private AccountName newAccountName;
    @JsonProperty("owner")
    private Authority owner;
    @JsonProperty("active")
    private Authority active;
    @JsonProperty("posting")
    private Authority posting;
    @JsonProperty("memo_key")
    private PublicKey memoKey;
    @JsonProperty("json_metadata")
    private String jsonMetadata;
    @JsonProperty("extensions")
    private List<Object> extensions;

    @JsonCreator
    public CreateClaimedAccountOperation(
            @JsonProperty("creator") AccountName creator,
            @JsonProperty("new_account_name") AccountName newAccountName,
            @JsonProperty("owner") Authority owner,
            @JsonProperty("active") Authority active,
            @JsonProperty("posting") Authority posting,
            @JsonProperty("memo_key") PublicKey memoKey,
            @JsonProperty("json_metadata") String jsonMetadata,
            @JsonProperty("extensions") List<Object> extensions) {
        
        super(false); // This is not a virtual operation
        this.creator = creator;
        this.newAccountName = newAccountName;
        this.owner = owner;
        this.active = active;
        this.posting = posting;
        this.memoKey = memoKey;
        this.jsonMetadata = jsonMetadata;
        this.extensions = extensions;
    }

    @Override
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        // To create a claimed account, the 'creator' must sign with their ACTIVE key.
        requiredAuthoritiesBase.put(this.getCreator(), PrivateKeyType.ACTIVE);
        // The method must return the modified map.
        return requiredAuthoritiesBase;
    }

    @Override
    public void validate(List<ValidationType> validationType) {
        // This method is implemented to fulfill the abstract parent's contract.
        // It is left empty as the contained objects (e.g., AccountName) are validated
        // upon their creation, and no further complex validation is required here.
    }
    
    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        try (ByteArrayOutputStream serializedCreateClaimedAccountOperation = new ByteArrayOutputStream()) {
            serializedCreateClaimedAccountOperation.write(SteemJUtils
                    .transformIntToVarIntByteArray(OperationType.CREATE_CLAIMED_ACCOUNT_OPERATION.getOrderId()));
            
            serializedCreateClaimedAccountOperation.write(this.creator.toByteArray());
            serializedCreateClaimedAccountOperation.write(this.newAccountName.toByteArray());
            serializedCreateClaimedAccountOperation.write(this.owner.toByteArray());
            serializedCreateClaimedAccountOperation.write(this.active.toByteArray());
            serializedCreateClaimedAccountOperation.write(this.posting.toByteArray());
            serializedCreateClaimedAccountOperation.write(this.memoKey.toByteArray());
            serializedCreateClaimedAccountOperation
                    .write(SteemJUtils.transformStringToVarIntByteArray(this.jsonMetadata));
            
            serializedCreateClaimedAccountOperation.write(SteemJUtils.transformIntToVarIntByteArray(this.extensions.size()));

            return serializedCreateClaimedAccountOperation.toByteArray();
        } catch (IOException e) {
            throw new SteemInvalidTransactionException(
                    "A problem occurred while transforming the operation into a byte array.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    public AccountName getCreator() { return creator; }
    public AccountName getNewAccountName() { return newAccountName; }
    public Authority getOwner() { return owner; }
    public Authority getActive() { return active; }
    public Authority getPosting() { return posting; }
    public PublicKey getMemoKey() { return memoKey; }
    public String getJsonMetadata() { return jsonMetadata; }
    public List<Object> getExtensions() { return extensions; }
}