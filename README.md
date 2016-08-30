# Kill Bill Payment Plugin to connect to Authorize.net #

## Overview ##

A java payment plugin implementation that uses Authorize.net as a payment gateway.  We had trouble getting
a ruby framework generated plugin to work out of the box, and as a team of java developers,
decided to implement an Authorize.net specific payment plugin in java.

## Requirements ##

The plugin needs tables added to the killbill database.  The maven build sets these (and the raw killbill 
tables) up via flyway, with the migration scripts stored in `src/test/resources/db/migration`

## Configuration ##

The following Tenant Properties are used to configure the Authorize.net environment (see 
`net.authorize.Environment`) and account credentials:
```java
    public static final String ENVIRONMENT = "org.killbill.billing.plugin.killbill-authorize-net.environment";
    public static final String API_LOGIN_ID = "org.killbill.billing.plugin.killbill-authorize-net.api-login-id";
    public static final String TRANSACTION_KEY = "org.killbill.billing.plugin.killbill-authorize-net.transaction-key";
```

The following curl command sets up Authorize.Net plugin to use a Sandbox account for tenant
with api key `foo`:
```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: foo' \
     -H 'X-Killbill-ApiSecret: bar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.killbill-authorize-net.environment=SANDBOX \
     org.killbill.billing.plugin.killbill-authorize-net.api-login-id=MY_API_LOGIN \
     org.killbill.billing.plugin.killbill-authorize-net.transaction-key=MY_TRANSACTION_KEY' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-authorize-net
```

## Implemented Functionality ##

We've currently implemented only a narrow band of functionality to support our use cases, using the Customer
Information Manager (CIM) from Authorize.net. ( See http://www.authorize.net/solutions/merchantsolutions/merchantservices/cim/ ) 
Specifically, we add a CustomerProfile to our Authorize.net account
through a new Servlet endpoint located at `/plugins/killbill-authorize-net`:
```
     * POST method for Authorize.Net KB plugin. Is used for functionality that is not
     * supported through the PaymentPluginApi.
     * Required input parameters:
     *  -- action : specifies which action the POST should perform.
     *              Supported values:
     *              - addAccount : create a Customer Profile for the given merchantLocationId in
     *                             Authorize.Net.
     *                             Required Input Parameters:
     *                             - tenantApiKey : api key for the tenant to whom the account belongs
     *                             - accountData: json for PaymentGatewayAccount, e.g. {"merchantLocationId":"83"}
     *                             Response json:
     *                             SUCCESS:
     *                             {
     *                                 "ok": true,
     *                                 "data": {
     *                                      "merchantLocationId": 83,
     *                                      "customerProfileId": "40572629"
     *                                      }
     *                             }
     *                             FAILURE:
     *                             {
     *                                  "ok": false,
     *                                  "data": null,
     *                                  "error": "Undefined \"action\" parameter."
     *                             }
```

Note that we currently use a `merchantLocationId` field as our unique identifier that maps to a killbill account's 
external key.  This mapping is defined as: `externalKey = 'W_' + merchantLocationId`.

The adding of payment methods is handled somewhat separately from the Authorize.net plugin.  This happens 
using a dedicated service that uses the Authorize.net java client to create a payment profile.  Without persisting the 
credit card information anywhere on our servers, that service then calls killbill to add a payment method
using the payment profile id returned by Authorize.net.  The following properties are expected on the
payment method creation request, with a `pluginName` of `killbill-authorize-net`:
```java
    public static final String PLUGIN_FIELDS_FIRST_NAME = "cc_first_name";
    public static final String PLUGIN_FIELDS_LAST_NAME = "cc_last_name";
    public static final String PLUGIN_FIELDS_TYPE = "cc_type";
    public static final String PLUGIN_FIELDS_EXP_MONTH = "cc_exp_month";
    public static final String PLUGIN_FIELDS_EXP_YEAR = "cc_exp_year";
    public static final String PLUGIN_FIELDS_LAST_FOUR = "cc_last_4";
    public static final String PLUGIN_FIELDS_ADDRESS = "address";
    public static final String PLUGIN_FIELDS_CITY = "city";
    public static final String PLUGIN_FIELDS_STATE = "state";
    public static final String PLUGIN_FIELDS_POSTAL_CODE = "zip";
    public static final String PLUGIN_FIELDS_COUNTRY = "country";
    public static final String PLUGIN_FIELDS_STATUS = "status";
```

Once a card is added to a killbill account, the following operations are supported:
```
purchasePayment()
refundPayment()
getPaymentInfo()
deletePaymentMethod()
getPaymentMethodDetail()
```

Other `PaymentPluginApi` methods will throw an `UnsupportedOperationException` until implemented.

## Errata ##

### Authorize.Net Transactions ###

Current information on Authorize.Net Transaction Request and Response:

http://developer.authorize.net/api/reference/index.html#payment-transactions

Interactive page with error code details (both response and reason error codes):

http://developer.authorize.net/api/reference/responseCodes.html

### Authorize.Net Transaction Response ###

`CreateTransactionResponse.TransactionResponse.responseCode` contains overall status of the transaction.
Possible values:

```
1 = Approved
2 = Declined
3 = Error
4 = Held for Review
```

For declined transactions,
`CreateTransactionResponse.TransactionResponse.errors.error.errorCode` contains the decline reason.

Complete list of all possible response codes (not limited to just error codes) in JSON format is found here:

http://developer.authorize.net/api/reference/dist/json/responseCodes.json

From https://support.authorize.net/authkb/index?page=content&id=A50 :

While Authorize.Net® may not receive specifics on a particular decline, we do include some details on
declines in our transaction responses, if available.

A table of common decline reasons, with the associated Response Reason Code, is listed below:

```
2 	General decline by card issuing bank or by Merchant Service Provider
3 	Referral to card issuing bank for verbal approval
4 	Card reported lost or stolen; pick up card if physically available
27 	Address Verification Service (AVS) mismatch; declined by account settings
44 	Card Code decline by payment processor
45 	AVS and Card Code mismatch; declined by account settings
65 	Card Code mismatch; declined by account settings
250 	Fraud Detection Suite (FDS) blocked IP address
251 	FDS filter triggered--filter set to decline
254 	FDS held for review; transaction declined after manual review
```






