####Authorize.Net Transactions####

Current information on Authorize.Net Transaction Request and Response:

http://developer.authorize.net/api/reference/index.html#payment-transactions

Interactive page with error code details (both response and reason error codes):

http://developer.authorize.net/api/reference/responseCodes.html

####Authorize.Net Transaction Response ####

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






