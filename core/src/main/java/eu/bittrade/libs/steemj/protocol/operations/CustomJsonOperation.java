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
import java.util.ArrayList;
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
import eu.bittrade.libs.steemj.util.SteemJUtils;

/**
 * This class represents the Steem "custom_json_operation" object.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class CustomJsonOperation extends Operation {
    // The @JsonProperty annotations have been removed from these fields to resolve
    // a conflict with the @JsonCreator constructor.
    private List<AccountName> requiredAuths;
    private List<AccountName> requiredPostingAuths;
    private String id;
    private String json;

    /**
     * This constructor is for creating new operations within the code.
     * The @JsonCreator annotation has been REMOVED from this constructor.
     */
    public CustomJsonOperation(List<AccountName> requiredAuths,
            List<AccountName> requiredPostingAuths,
            String id, String json) {
        super(false);

        this.setRequiredAuths(requiredAuths);
        this.setRequiredPostingAuths(requiredPostingAuths);
        this.setId(id);
        this.setJson(json);
    }

    /**
     * This new constructor is used exclusively by the JSON parser to handle the
     * nested "value" object sent by the Hive API.
     *
     * @param value A map containing the actual operation data from the JSON response.
     */
    @JsonCreator
    @SuppressWarnings("unchecked")
    public CustomJsonOperation(@JsonProperty("value") Map<String, Object> value) {
        super(false);

        List<AccountName> requiredAuthsList = new ArrayList<>();
        if (value.containsKey("required_auths")) {
            for (String auth : (List<String>) value.get("required_auths")) {
                requiredAuthsList.add(new AccountName(auth));
            }
        }
        this.setRequiredAuths(requiredAuthsList);

        List<AccountName> requiredPostingAuthsList = new ArrayList<>();
        if (value.containsKey("required_posting_auths")) {
            for (String auth : (List<String>) value.get("required_posting_auths")) {
                requiredPostingAuthsList.add(new AccountName(auth));
            }
        }
        this.setRequiredPostingAuths(requiredPostingAuthsList);

        this.setId((String) value.get("id"));
        this.setJson((String) value.get("json"));
    }

    public List<AccountName> getRequiredAuths() {
        return requiredAuths;
    }

    public void setRequiredAuths(List<AccountName> requiredAuths) {
        if (requiredAuths == null) {
            this.requiredAuths = new ArrayList<>();
        } else {
            this.requiredAuths = requiredAuths;
        }
    }

    public List<AccountName> getRequiredPostingAuths() {
        return requiredPostingAuths;
    }

    public void setRequiredPostingAuths(List<AccountName> requiredPostingAuths) {
        if (requiredPostingAuths == null) {
            this.requiredPostingAuths = new ArrayList<>();
        } else {
            this.requiredPostingAuths = requiredPostingAuths;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = SteemJUtils.setIfNotNull(id, "An ID is required.");
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public byte[] toByteArray() throws SteemInvalidTransactionException {
        try (ByteArrayOutputStream serializedCustomJsonOperation = new ByteArrayOutputStream()) {
            serializedCustomJsonOperation
                    .write(SteemJUtils.transformIntToVarIntByteArray(OperationType.CUSTOM_JSON_OPERATION.getOrderId()));

            serializedCustomJsonOperation
                    .write(SteemJUtils.transformLongToVarIntByteArray(this.getRequiredAuths().size()));

            for (AccountName accountName : this.getRequiredAuths()) {
                serializedCustomJsonOperation.write(accountName.toByteArray());
            }

            serializedCustomJsonOperation
                    .write(SteemJUtils.transformLongToVarIntByteArray(this.getRequiredPostingAuths().size()));

            for (AccountName accountName : this.getRequiredPostingAuths()) {
                serializedCustomJsonOperation.write(accountName.toByteArray());
            }

            serializedCustomJsonOperation.write(SteemJUtils.transformStringToVarIntByteArray(this.getId()));
            serializedCustomJsonOperation.write(SteemJUtils.transformStringToVarIntByteArray(this.getJson()));

            return serializedCustomJsonOperation.toByteArray();
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
        Map<SignatureObject, PrivateKeyType> requiredAuthorities = requiredAuthoritiesBase;

        requiredAuthorities = mergeRequiredAuthorities(requiredAuthorities, this.getRequiredAuths(),
                PrivateKeyType.ACTIVE);
        requiredAuthorities = mergeRequiredAuthorities(requiredAuthorities, this.getRequiredPostingAuths(),
                PrivateKeyType.POSTING);

        return requiredAuthorities;
    }

    @Override
    public void validate(List<ValidationType> validationsToSkip) {
        if (!validationsToSkip.contains(ValidationType.SKIP_VALIDATION)) {
            if (requiredPostingAuths.isEmpty() && requiredAuths.isEmpty()) {
                throw new InvalidParameterException(
                        "At least one authority type (POSTING or ACTIVE) needs to be provided.");
            } else if (id.length() > 32) {
                throw new InvalidParameterException("The ID must be less than 32 characters long.");
            } else if (json != null && !json.isEmpty() && !SteemJUtils.verifyJsonString(json)) {
                throw new InvalidParameterException("The given String is no valid JSON");
            }
        }
    }
}