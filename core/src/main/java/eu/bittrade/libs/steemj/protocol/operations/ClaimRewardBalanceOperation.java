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
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- IMPORTANT IMPORT

import eu.bittrade.libs.steemj.enums.OperationType;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.enums.ValidationType;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.interfaces.SignatureObject;
import eu.bittrade.libs.steemj.protocol.AccountName;
import eu.bittrade.libs.steemj.protocol.LegacyAsset;
import eu.bittrade.libs.steemj.util.SteemJUtils;

public class ClaimRewardBalanceOperation extends Operation {
    // The @JsonProperty annotations have been REMOVED from these fields.
    private AccountName account;
    private LegacyAsset rewardHive;
    private LegacyAsset rewardHbd;
    private LegacyAsset rewardVests;

    /**
     * This is the original constructor. The @JsonCreator and @JsonProperty annotations
     * have been REMOVED. The parameter names have been changed to match Hive (rewardHive, rewardHbd).
     */
    public ClaimRewardBalanceOperation(AccountName account, LegacyAsset rewardHive,
            LegacyAsset rewardHbd, LegacyAsset rewardVests) {
        super(false);
        this.setAccount(account);
        this.setRewardHive(rewardHive);
        this.setRewardHbd(rewardHbd);
        this.setRewardVests(rewardVests);
    }
    
    /**
     * This new constructor is used exclusively by the JSON parser to handle the
     * nested "value" object sent by the Hive API.
     */
    @JsonCreator
    public ClaimRewardBalanceOperation(@JsonProperty("value") Map<String, Object> value) {
        super(false);
        ObjectMapper mapper = new ObjectMapper();
        this.setAccount(new AccountName((String) value.get("account")));
        this.setRewardHive(mapper.convertValue(value.get("reward_hive"), LegacyAsset.class));
        this.setRewardHbd(mapper.convertValue(value.get("reward_hbd"), LegacyAsset.class));
        this.setRewardVests(mapper.convertValue(value.get("reward_vests"), LegacyAsset.class));
    }

    public AccountName getAccount() {
        return account;
    }

    public void setAccount(AccountName account) {
        this.account = SteemJUtils.setIfNotNull(account, "The account can't be null.");
    }

    public LegacyAsset getRewardHive() {
        return rewardHive;
    }

    public void setRewardHive(LegacyAsset rewardHive) {
        this.rewardHive = SteemJUtils.setIfNotNull(rewardHive, "Reward Hive can't be null.");
    }

    public LegacyAsset getRewardHbd() {
        return rewardHbd;
    }

    public void setRewardHbd(LegacyAsset rewardHbd) {
        this.rewardHbd = SteemJUtils.setIfNotNull(rewardHbd, "Reward HBD can't be null.");
    }

    public LegacyAsset getRewardVests() {
        return rewardVests;
    }

    public void setRewardVests(LegacyAsset rewardVests) {
        this.rewardVests = SteemJUtils.setIfNotNull(rewardVests, "Reward Vests can't be null.");
    }

    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        try (ByteArrayOutputStream serializedClaimRewardBalanceOperation = new ByteArrayOutputStream()) {
            serializedClaimRewardBalanceOperation.write(SteemJUtils
                    .transformIntToVarIntByteArray(OperationType.CLAIM_REWARD_BALANCE_OPERATION.getOrderId()));
            serializedClaimRewardBalanceOperation.write(this.getAccount().toByteArray());
            serializedClaimRewardBalanceOperation.write(this.getRewardHive().toByteArray());
            serializedClaimRewardBalanceOperation.write(this.getRewardHbd().toByteArray());
            serializedClaimRewardBalanceOperation.write(this.getRewardVests().toByteArray());
            return serializedClaimRewardBalanceOperation.toByteArray();
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
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(
            Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        return mergeRequiredAuthorities(requiredAuthoritiesBase, this.getAccount(), PrivateKeyType.POSTING);
    }

    @Override
    public void validate(List<ValidationType> validationsToSkip) {
        if (!validationsToSkip.contains(ValidationType.SKIP_VALIDATION)
                && (rewardHive.getAmount() < 0 || rewardHbd.getAmount() < 0 || rewardVests.getAmount() < 0)) {
            throw new InvalidParameterException("All reward amounts must be non-negative.");
        }
    }
}