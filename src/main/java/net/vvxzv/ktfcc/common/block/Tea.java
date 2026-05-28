package net.vvxzv.ktfcc.common.block;

public enum Tea {
    TIEGUANYIN,
    OOLONG(true),
    BILUOCHUN(true),
    BLACK;

    final boolean hasTree;

    Tea() {
        this.hasTree = false;
    }

    Tea(boolean hasTree) {
        this.hasTree = hasTree;
    }

    public boolean isHasTree() {
        return this.hasTree;
    }
}
