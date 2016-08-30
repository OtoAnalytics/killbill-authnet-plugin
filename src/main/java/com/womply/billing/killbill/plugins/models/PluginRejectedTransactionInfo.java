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

package com.womply.billing.killbill.plugins.models;

import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;

/**
 * Represents a transaction rejected by the plugin.
 * When, for example, the amount to be refunded exceeds the amount of the
 * original transaction.
 */
public class PluginRejectedTransactionInfo extends AuthorizeNetTransactionInfo {

    protected String errorMessage;

    public PluginRejectedTransactionInfo(AuthorizeNetTransactionInfo transaction) {
        this.customerProfileId = transaction.getCustomerProfileId();
        this.customerPaymentProfileId = transaction.getCustomerPaymentProfileId();
        this.kbAccountId = transaction.getKbAccountId();
        this.kbPaymentId = transaction.getKbPaymentId();
        this.kbPaymentMethodId = transaction.getKbPaymentMethodId();
        this.kbTransactionId = transaction.getKbTransactionId();
        this.kbTransactionType = transaction.getKbTransactionType();
        this.transactionType = transaction.getTransactionType();
        this.tenantId = transaction.getTenantId();
        this.amount = transaction.getAmount();
        this.currency = transaction.getCurrency();
        this.kbReferencedTransactionRecordId = transaction.getKbReferencedTransactionRecordId();
    }

    /**
     * There is no Authorize.Net request for a transaction rejected by the plugin.
     */
    @Override
    public AuthorizeNetRequestsRecord saveInto(AuthorizeNetRequestsRecord record) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizeNetTransactionsRecord saveInto(AuthorizeNetTransactionsRecord record) {
        saveBaseFields(record);
        record.setTransactionError(errorMessage);
        record.setSuccess(MysqlAdapter.getValueAsByte(false));

        record.setKbPaymentPluginStatus(PaymentPluginStatus.CANCELED.name());

        return record;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}