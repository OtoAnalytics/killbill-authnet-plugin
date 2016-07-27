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
