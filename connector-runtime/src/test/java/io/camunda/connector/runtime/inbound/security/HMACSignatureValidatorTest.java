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
package io.camunda.connector.runtime.inbound.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Stream;

import io.camunda.connector.runtime.inbound.signature.HMACAlgoCustomerChoice;
import io.camunda.connector.runtime.inbound.signature.HMACSignatureValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HMACSignatureValidatorTest {

  private static final String GH_SHA1_HEADER = "x-hub-signature";
  private static final String GH_SHA1_VALUE = "sha1=de81c837cc792e7d21d7bf9feb74cd19d714baca";
  private static final String GH_SHA256_HEADER = "x-hub-signature-256";
  private static final String GH_SHA256_LONG_VALUE =
      "sha256=dd22cfb7ae96875d81bd1a695a0244f2b4c32c0938be0b445f520b0b3e0f43fd";
  private static final String GH_SHA256_SHORT_VALUE =
      "dd22cfb7ae96875d81bd1a695a0244f2b4c32c0938be0b445f520b0b3e0f43fd";
  private static final String GH_SECRET_KEY = "mySecretKey";

  @ParameterizedTest
  @MethodSource("provideHMACTestData")
  public void hmacSignatureVerificationParametrizedTest(final HMACTestEntry testEntry)
      throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    HMACSignatureValidator validator =
        new HMACSignatureValidator(
            readString(new File(testEntry.filepathWithBody).toPath(), UTF_8).getBytes(UTF_8),
            testEntry.originalRequestHeaders,
            testEntry.headerWithHmac,
            testEntry.decodedSecretKey,
            testEntry.algo);
    Assertions.assertThat(validator.isRequestValid()).isTrue();
  }

  private static Stream<HMACTestEntry> provideHMACTestData() {
    return Stream.of(
        new HMACTestEntry(
            "src/test/resources/hmac/gh-webhook-request.json",
            Map.of(GH_SHA256_HEADER, GH_SHA256_LONG_VALUE),
            GH_SHA256_HEADER,
            GH_SECRET_KEY,
            HMACAlgoCustomerChoice.sha_256),
        new HMACTestEntry(
            "src/test/resources/hmac/gh-webhook-request.json",
            Map.of(GH_SHA1_HEADER, GH_SHA1_VALUE),
            GH_SHA1_HEADER,
            GH_SECRET_KEY,
            HMACAlgoCustomerChoice.sha_1),
        new HMACTestEntry(
            "src/test/resources/hmac/gh-webhook-request.json",
            Map.of(GH_SHA256_HEADER, GH_SHA256_SHORT_VALUE),
            GH_SHA256_HEADER,
            GH_SECRET_KEY,
            HMACAlgoCustomerChoice.sha_256));
  }

  private static class HMACTestEntry {
    final String filepathWithBody;
    final Map<String, String> originalRequestHeaders;
    final String headerWithHmac;
    final String decodedSecretKey;
    final HMACAlgoCustomerChoice algo;

    public HMACTestEntry(
        String filepathWithBody,
        Map<String, String> originalRequestHeaders,
        String headerWithHmac,
        String decodedSecretKey,
        HMACAlgoCustomerChoice algo) {
      this.filepathWithBody = filepathWithBody;
      this.originalRequestHeaders = originalRequestHeaders;
      this.headerWithHmac = headerWithHmac;
      this.decodedSecretKey = decodedSecretKey;
      this.algo = algo;
    }
  }
}
