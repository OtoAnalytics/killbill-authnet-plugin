/*
 *  Copyright 2016 Womply
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.womply.billing.killbill.plugins.db;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

/**
 * Utility functions to convert between MySQL datatypes and Java datatypes.
 */
public class MysqlAdapter {

    public static final Byte TRUE = Byte.parseByte("1");
    public static final Byte FALSE = Byte.parseByte("0");

    /**
     * @return True if record.getSuccess() is set to 1. Converts between MySQL byte type
     *      and Java boolean type.
     */
    public static boolean isTransactionSuccessful(AuthorizeNetTransactionsRecord record) {
        return record.getSuccess().equals(TRUE);
    }

    /**
     * Converts the given boolean value to a Byte value.
     */
    public static Byte getValueAsByte(boolean value) {
        if (value == true) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
