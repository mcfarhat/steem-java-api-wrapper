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
package eu.bittrade.libs.steemj.protocol.operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map; // <-- ADDED IMPORT
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.OperationType;
import eu.bittrade.libs.steemj.enums.ValidationType;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class represents the Steem "transfer_operation" object.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class TransferOperation extends AbstractTransferOperation {
    // The @JsonProperty annotation has been REMOVED from this field.
    private String memo;

    /**
     * This is the original constructor, used for creating new operations in code.
     * The @JsonCreator annotation has been REMOVED from here.
     * The @JsonProperty annotations on parameters have also been removed.
     */
    public TransferOperation(AccountName from, AccountName to,
            LegacyAsset amount, String memo) {
        super(false);

        this.setFrom(from);
        this.setTo(to);
        this.setAmount(amount);
        this.setMemo(memo);
    }

    /**
     * This new constructor is used exclusively by the JSON parser to handle the
     * nested "value" object sent by the Hive API.
     */
    @JsonCreator
    public TransferOperation(@JsonProperty("value") Map<String, Object> value) {
        super(false);
        this.setFrom(new AccountName((String) value.get("from")));
        this.setTo(new AccountName((String) value.get("to")));

        // ######################################################################
        // ### THIS IS THE CORRECTED PART THAT WILL WORK ###
        // ######################################################################
        // We use an ObjectMapper to correctly convert the nested 'amount' map
        // into a LegacyAsset object. This is the standard, reliable way.
        ObjectMapper mapper = new ObjectMapper();
        LegacyAsset amountAsset = mapper.convertValue(value.get("amount"), LegacyAsset.class);
        this.setAmount(amountAsset);
        // ######################################################################

        this.setMemo((String) value.get("memo"));
    }

    @Override
    public void setAmount(LegacyAsset amount) {
        this.amount = SteemJUtils.setIfNotNull(amount, "The amount can't be null.");
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        try (ByteArrayOutputStream serializedTransferOperation = new ByteArrayOutputStream()) {
            serializedTransferOperation
                    .write(SteemJUtils.transformIntToVarIntByteArray(OperationType.TRANSFER_OPERATION.getOrderId()));
            serializedTransferOperation.write(this.getFrom().toByteArray());
            serializedTransferOperation.write(this.getTo().toByteArray());
            serializedTransferOperation.write(this.getAmount().toByteArray());
            serializedTransferOperation.write(SteemJUtils.transformStringToVarIntByteArray(this.getMemo()));

            return serializedTransferOperation.toByteArray();
        } catch (IOException e) {
            throw new SteemInvalidTransactionException(
                    "A problem occured while transforming the operation into a byte array.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public void validate(List<ValidationType> validationsToSkip) {
        if (!validationsToSkip.contains(ValidationType.SKIP_VALIDATION)) {
            super.validate(validationsToSkip);

            if (!validationsToSkip.contains(ValidationType.SKIP_ASSET_VALIDATION)) {
                if (amount.getSymbol().equals(SteemJConfig.getInstance().getVestsSymbol())) {
                    throw new InvalidParameterException("Transfering Steem Power (VESTS) is not allowed.");
                } else if (amount.getAmount() <= 0) {
                    throw new InvalidParameterException("Must transfer a nonzero amount.");
                }
            }

            if (memo.length() > 2048) {
                throw new InvalidParameterException("The memo is too long. Only 2048 characters are allowed.");
            }
        }
    }
}