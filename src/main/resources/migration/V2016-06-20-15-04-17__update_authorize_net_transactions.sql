UPDATE authorize_net_transactions
	SET kb_payment_plugin_status = 'ERROR'
WHERE success = 0;
