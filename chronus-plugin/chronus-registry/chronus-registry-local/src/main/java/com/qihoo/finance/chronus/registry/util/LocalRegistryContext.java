package com.qihoo.finance.chronus.registry.util;

import com.qihoo.finance.chronus.registry.api.Node;

public class LocalRegistryContext {
    private static Node node;

    public static Node getNode() {
        return node;
    }

    public static void setNode(Node node) {
        LocalRegistryContext.node = node;
    }
}
