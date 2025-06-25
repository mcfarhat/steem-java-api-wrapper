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

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.bittrade.libs.steemj.fc.TimePointSec;
import eu.bittrade.libs.steemj.protocol.AccountName;

/**
 * This class represents a Hive "decline_voting_rights_request_object" object.
 * 
 * @author <a href="https://hive.blog/@dez1337">dez1337</a>
 */
public class DeclineVotingRightsRequestObject {
    // Original type is "id_type".
    @JsonProperty("id")
    private long id;
    @JsonProperty("account")
    private AccountName account;
    @JsonProperty("effective_date")
    private TimePointSec effectiveDate;

    /**
     * This object is only used to wrap the JSON response in a POJO, so
     * therefore this class should not be instantiated.
     */
    private DeclineVotingRightsRequestObject() {
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the account
     */
    public AccountName getAccount() {
        return account;
    }

    /**
     * @return the effectiveDate
     */
    public TimePointSec getEffectiveDate() {
        return effectiveDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
//done
