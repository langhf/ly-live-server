package cn.drelang.live.server.format.flv;

import java.util.List;

/**
 *
 * @author Drelang
 * @date 2021/4/5 20:31
 */

public class FLVFileBody {

    List<Node> content;

    public static class Node {
        FLVTag tag;
        long previousTagSize;

        public void setTag(FLVTag tag) {
            this.tag = tag;
        }

        public void setPreviousTagSize(long previousTagSize) {
            this.previousTagSize = previousTagSize;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "previousTagSize=" + previousTagSize +
                    ", tag=" + tag +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FLVFileBody{" +
                "content=" + content +
                '}';
    }
}

