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
package eu.bittrade.libs.steemj.base.models.deserializer;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import eu.bittrade.libs.steemj.protocol.LegacyAsset;
import eu.bittrade.libs.steemj.protocol.enums.LegacyAssetSymbolType;

/**
 * A custom deserializer for LegacyAsset types.
 * 
 * This has been updated to handle both the legacy string format (e.g., "1.000 HIVE")
 * and the modern object format used by Hive (e.g., {"amount":"1000", "precision":3, "nai":"@@000000021"}).
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 * @author Your Name Here (for the modifications)
 */
public class AssetDeserializer extends JsonDeserializer<LegacyAsset> {
    @Override
    public LegacyAsset deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.currentToken();

        // Handle the legacy string format (e.g., "1.234 STEEM").
        if (JsonToken.VALUE_STRING.equals(currentToken)) {
            String[] assetFields = jp.getText().split(" ");

            if (assetFields.length == 2) {
                return new LegacyAsset(new BigDecimal(assetFields[0]), LegacyAssetSymbolType.valueOf(assetFields[1]));
            }
        }
        
        // Handle the modern object format (e.g., { "amount": "1234", "precision": 3, "nai": "..." }).
        if (JsonToken.START_OBJECT.equals(currentToken)) {
            JsonNode rootNode = jp.getCodec().readTree(jp);

            // Make sure all required fields are present
            if (rootNode.has("amount") && rootNode.has("precision") && rootNode.has("nai")) {
                long amount = rootNode.get("amount").asLong();
                int precision = rootNode.get("precision").asInt();
                String nai = rootNode.get("nai").asText();
                
                LegacyAssetSymbolType symbol = LegacyAssetSymbolType.fromNai(nai, precision);
                
                // The amount from the API is a long integer (e.g., 1234 for "1.234").
                // We need to convert it to a BigDecimal by dividing by 10^precision.
                BigDecimal realAmount = new BigDecimal(amount).divide(new BigDecimal(Math.pow(10, precision)));
                
                return new LegacyAsset(realAmount, symbol);
            }
        }

        throw new IllegalArgumentException("Cannot deserialize LegacyAsset: Expected a JSON string or object, but found " + currentToken);
    }
}