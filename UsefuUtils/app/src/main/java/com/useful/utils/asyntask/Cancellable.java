package com.useful.utils.asyntask;

public interface Cancellable {
	void cancel();

	boolean isCancelled();
}
