package com.sparrowwallet;

public abstract class AppProtocol {
	public final LedgerDevice device;

	public AppProtocol(LedgerDevice device) {
		this.device = device;
	}
}
