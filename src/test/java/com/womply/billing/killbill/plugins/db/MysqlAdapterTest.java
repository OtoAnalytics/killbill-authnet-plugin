package com.womply.billing.killbill.plugins.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

import org.testng.annotations.Test;

/**
 * Unit tests for the MysqlAdapter.
 */
public class MysqlAdapterTest {

    @Test
    public void isTransactionSuccessful() {
        AuthorizeNetTransactionsRecord record = createMock(AuthorizeNetTransactionsRecord.class);
        expect(record.getSuccess()).andReturn(MysqlAdapter.TRUE);

        replay(record);
        boolean success = MysqlAdapter.isTransactionSuccessful(record);
        assertThat(success).isTrue();
    }

    @Test
    public void isTransactionSuccessfulFalse() {
        AuthorizeNetTransactionsRecord record = createMock(AuthorizeNetTransactionsRecord.class);
        expect(record.getSuccess()).andReturn(MysqlAdapter.FALSE);

        replay(record);
        boolean success = MysqlAdapter.isTransactionSuccessful(record);
        assertThat(success).isFalse();
    }
}
