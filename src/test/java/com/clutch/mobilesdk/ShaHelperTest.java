package com.clutch.mobilesdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShaHelperTest {

  /**
   * Test SHA-512 hashing.
   */
  @Test
  public void sha512Test() {
    assertEquals("c3b4feda1797f924bc3366b5c8d3ad205559e0dadd9d6e3112b99226bdf143515c86317ee8db5b15f549c22db5cbf7676a407a356e9f78cc199121b555a54d0c",
      ShaHelper.sha512("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sem risus, rhoncus sit amet luctus vitae, mattis ac mi."));
  }

  /**
   * Test HMAC-SHA512 signing.
   */
  @Test
  public void hmacSha512Test() {
    assertEquals("d1641a20bb9a90c5b749050f7ab17283a52bf80bf8b6b9f7ee5e2ac4c59b571337f35bf86dd8976853ace05edaf187092ffc7f1c738e7b447a77b27d08f28a12",
            ShaHelper.hmacSha512("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sem risus, rhoncus sit amet luctus vitae, mattis ac mi.",
                    "supersecretkey123"));
  }

}
