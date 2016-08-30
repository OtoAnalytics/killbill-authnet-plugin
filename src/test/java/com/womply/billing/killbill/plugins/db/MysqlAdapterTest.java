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
