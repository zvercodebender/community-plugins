package com.xebialabs.deployit.plugins.byoc.util;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

public class ByocCloudId {
    public static final String ADDRESS_KEY = "address";
    public static final String SEQUENCE_NUMBER_KEY = "seq";

    private final String address;
    private final int sequenceNumber;

    public ByocCloudId(String address, int sequenceNumber) {
        this.address = address;
        this.sequenceNumber = sequenceNumber;
    }

    public static ByocCloudId fromCloudId(String cloudId) {
        Map<String, String> cloudIdAttributes = Splitter.on("&").withKeyValueSeparator("=").split(cloudId);
        return new ByocCloudId(cloudIdAttributes.get(ADDRESS_KEY), Integer.valueOf(cloudIdAttributes.get(SEQUENCE_NUMBER_KEY)));
    }

    public String getAddress() {
        return address;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String toString() {
        return Joiner.on("&").withKeyValueSeparator("=").join(
                ImmutableMap.of(ADDRESS_KEY, address, SEQUENCE_NUMBER_KEY, sequenceNumber));
    }
}
