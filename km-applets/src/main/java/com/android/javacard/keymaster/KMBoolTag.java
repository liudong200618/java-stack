/*
 * Copyright(C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.javacard.keymaster;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

/**
 * KMBoolTag represents BOOL TAG type from the android keymaster hal specifications. If it is
 * present in the key parameter list then its value is always true. A KMTag always requires a value
 * because it is a key value pair. The bool tag always has 0x01 as its value. struct{byte TAG_TYPE;
 * short length; struct{short BOOL_TAG; short tagKey; byte value 1}}
 */

public class KMBoolTag extends KMTag {

  private static KMBoolTag prototype;

  // The allowed tag keys of type bool tag.
  private static final short[] tags = {
    CALLER_NONCE,
    INCLUDE_UNIQUE_ID,
    BOOTLOADER_ONLY,
    ROLLBACK_RESISTANCE,
    NO_AUTH_REQUIRED,
    ALLOW_WHILE_ON_BODY,
    TRUSTED_USER_PRESENCE_REQUIRED,
    TRUSTED_CONFIRMATION_REQUIRED,
    UNLOCKED_DEVICE_REQUIRED,
    RESET_SINCE_ID_ROTATION,
    EARLY_BOOT_ONLY,
    DEVICE_UNIQUE_ATTESTATION
  };

  private KMBoolTag() {
  }

  private static KMBoolTag proto(short ptr) {
    if (prototype == null) {
      prototype = new KMBoolTag();
    }
    instanceTable[KM_BOOL_TAG_OFFSET] = ptr;
    return prototype;
  }

  // pointer to an empty instance used as expression
  public static short exp() {
    short ptr = instance(TAG_TYPE, (short) 2);
    Util.setShort(heap, (short) (ptr + TLV_HEADER_SIZE), BOOL_TAG);
    return ptr;
  }

  public static short instance(short key) {
    if (!validateKey(key)) {
      ISOException.throwIt(ISO7816.SW_DATA_INVALID);
    }
    short ptr = KMType.instance(TAG_TYPE, (short) 5);
    Util.setShort(heap, (short) (ptr + TLV_HEADER_SIZE), BOOL_TAG);
    Util.setShort(heap, (short) (ptr + TLV_HEADER_SIZE + 2), key);
    // Value is always 1.
    heap[(short) (ptr + TLV_HEADER_SIZE + 4)] = 0x01;
    return ptr;
  }

  public static KMBoolTag cast(short ptr) {
    if (heap[ptr] != TAG_TYPE) {
      ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
    }
    if (Util.getShort(heap, (short) (ptr + TLV_HEADER_SIZE)) != BOOL_TAG) {
      ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
    }
    return proto(ptr);
  }

  public short getKey() {
    return Util.getShort(heap, (short) (instanceTable[KM_BOOL_TAG_OFFSET] + TLV_HEADER_SIZE + 2));
  }

  public short getTagType() {
    return KMType.BOOL_TAG;
  }

  public byte getVal() {
    return heap[(short) (instanceTable[KM_BOOL_TAG_OFFSET] + TLV_HEADER_SIZE + 4)];
  }

  // validate the tag key.
  private static boolean validateKey(short key) {
    short index = (short) tags.length;
    while (--index >= 0) {
      if (tags[index] == key) {
        return true;
      }
    }
    return false;
  }

  public static short[] getTags() {
    return tags;
  }
}
