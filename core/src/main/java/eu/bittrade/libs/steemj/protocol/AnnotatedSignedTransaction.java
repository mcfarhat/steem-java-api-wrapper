package eu.bittrade.libs.steemj.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.bittrade.libs.steemj.communication.CommunicationHandler;
import eu.bittrade.libs.steemj.protocol.operations.Operation;

/**
 * This class represents a Hive "annotated_signed_transaction" object.
 * It is a self-contained class that holds all fields returned by the
 * get_transaction method.
 *
 * THIS VERSION INCLUDES A CUSTOM SETTER FOR "operations" to handle
 * the nested {"type": ..., "value": ...} structure without breaking
 * other API calls.
 */
public class AnnotatedSignedTransaction {

    // Fields from the base transaction
    @JsonProperty("ref_block_num")
    private int refBlockNum;
    @JsonProperty("ref_block_prefix")
    private long refBlockPrefix;
    @JsonProperty("expiration")
    private String expiration;
    
    private List<Operation> operations; // Populated by the custom "unpackOperations" setter
    
    @JsonProperty("extensions")
    private List<Object> extensions;
    @JsonProperty("signatures")
    private List<String> signatures;

    // Annotation fields added by the blockchain
    @JsonProperty("transaction_id")
    private TransactionId transactionId;
    @JsonProperty("block_num")
    private long blockNum;
    @JsonProperty("transaction_num")
    private int transactionNum;
    
    /**
     * This constructor is used by the Jackson JSON deserializer.
     * We don't include "operations" here because we handle it with the custom setter.
     */
    @JsonCreator
    public AnnotatedSignedTransaction(
            @JsonProperty("ref_block_num") int refBlockNum,
            @JsonProperty("ref_block_prefix") long refBlockPrefix,
            @JsonProperty("expiration") String expiration,
            @JsonProperty("extensions") List<Object> extensions,
            @JsonProperty("signatures") List<String> signatures,
            @JsonProperty("transaction_id") TransactionId transactionId,
            @JsonProperty("block_num") long blockNum,
            @JsonProperty("transaction_num") int transactionNum
    ) {
        this.refBlockNum = refBlockNum;
        this.refBlockPrefix = refBlockPrefix;
        this.expiration = expiration;
        this.extensions = extensions;
        this.signatures = signatures;
        this.transactionId = transactionId;
        this.blockNum = blockNum;
        this.transactionNum = transactionNum;
        this.operations = new ArrayList<>(); // Initialize the list
    }

    /**
     * THIS IS THE SPECIAL CUSTOM SETTER. It is the targeted fix for the
     * inconsistent JSON structure from the account_history_api.get_transaction call.
     * <p>
     * Jackson will call this method when it finds the "operations" field. It
     * receives the raw JSON as a List of Maps. We then manually "flatten" the
     * nested "value" object so that the library's existing Operation deserializer
     * can understand it.
     *
     * @param rawOperations The list of operations in their nested format.
     */
    @JsonProperty("operations")
    @SuppressWarnings("unchecked")
    private void unpackOperations(List<Map<String, Object>> rawOperations) {
        this.operations = new ArrayList<>();
        ObjectMapper objectMapper = CommunicationHandler.getObjectMapper();

        for (Map<String, Object> rawOp : rawOperations) {
            // Get the nested "value" object from the JSON.
            Object valueObject = rawOp.get("value");

            if (valueObject instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) valueObject;
                
                // THE KEY FIX: Copy the "type" field from the outer object
                // into the inner "value" object. This creates the "flat" structure
                // that the rest of the library expects.
                valueMap.put("type", rawOp.get("type"));
                
                // Now, convert the corrected map into the proper Operation object.
                // Jackson's existing logic on the Operation class will handle the rest.
                Operation operation = objectMapper.convertValue(valueMap, Operation.class);
                this.operations.add(operation);
            }
        }
    }

    // --- GETTERS for all fields ---

    public int getRefBlockNum() { return refBlockNum; }
    public long getRefBlockPrefix() { return refBlockPrefix; }
    public String getExpiration() { return expiration; }
    public List<Operation> getOperations() { return operations; }
    public List<Object> getExtensions() { return extensions; }
    public List<String> getSignatures() { return signatures; }
    public TransactionId getTransactionId() { return transactionId; }
    public long getBlockNum() { return blockNum; }
    public int getTransactionNum() { return transactionNum; }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("transactionId", transactionId)
                .append("blockNum", blockNum)
                .append("transactionNum", transactionNum)
                .append("expiration", expiration)
                .append("operations", operations)
                .toString();
    }
}