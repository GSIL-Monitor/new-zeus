package com.taobao.zeus.store;

import java.util.Arrays;
import java.util.List;

public class Super {

	private static final List<String> supers = Arrays.asList("admin");

	public static List<String> getSupers() {
		return supers;
	}
}
