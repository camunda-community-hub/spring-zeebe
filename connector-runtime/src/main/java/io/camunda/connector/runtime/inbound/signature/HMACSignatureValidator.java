/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.camunda.connector.runtime.inbound.signature;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: add URL signing and Base64 format
public class HMACSignatureValidator {

  private static final Logger LOG = LoggerFactory.getLogger(HMACSignatureValidator.class);

  private final byte[] requestBody;
  private final Map<String, String> headers;
  private final String hmacHeader;
  private final String hmacSecretKey;
  private final HMACAlgoCustomerChoice hmacAlgo;

  public HMACSignatureValidator(
      final byte[] requestBody,
      final Map<String, String> headers,
      final String hmacHeader,
      final String hmacSecretKey,
      final HMACAlgoCustomerChoice hmacAlgo) {
    this.requestBody = requestBody;
    this.headers = headers;
    this.hmacHeader = hmacHeader;
    this.hmacSecretKey = hmacSecretKey;
    this.hmacAlgo = hmacAlgo;
  }

  public boolean isRequestValid() throws NoSuchAlgorithmException, InvalidKeyException {
    final String providedHmac = headers.get(hmacHeader.toLowerCase());
    LOG.debug("Given HMAC from webhook call: {}", providedHmac);

    if (providedHmac == null || providedHmac.length()==0) {
      return false;
    }

    byte[] signedEntity = requestBody;

    Mac sha256_HMAC = Mac.getInstance(hmacAlgo.getAlgoReference());
    SecretKeySpec secret_key =
        new SecretKeySpec(
            hmacSecretKey.getBytes(StandardCharsets.UTF_8), hmacAlgo.getAlgoReference());
    sha256_HMAC.init(secret_key);
    byte[] expectedHmac = sha256_HMAC.doFinal(signedEntity);

    // Some webhooks produce short HMAC message, e.g. aabbcc...
    String expectedShortHmacString = Hex.encodeHexString(expectedHmac);
    // The other produce longer version, like sha256=aabbcc...
    String expectedLongHmacString = hmacAlgo.getTag() + "=" + expectedShortHmacString;
    LOG.debug(
        "Computed HMAC from webhook body: {}, {}", expectedShortHmacString, expectedLongHmacString);

    return providedHmac.equals(expectedShortHmacString)
        || providedHmac.equals(expectedLongHmacString);
  }
}
