package tracelog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class TraceLogUtils {

    private BitSet bitSet = new BitSet(128);

    /**
     * ???????
     * @param traceTag ??????????  0-127
     */
    public void trace(TraceTag traceTag) {
        bitSet.set(traceTag.tag, true);
    }

    /**
     * ????????
     * @return ????1126038419678722,107390959744
     */
    public String traceLog() {
        if (this.bitSet == null) {
            return "0";
        }
        long[] traceArr = this.bitSet.toLongArray();
        if (traceArr.length == 1) {
            return traceArr[0] + "";
        } else if (traceArr.length == 2) {
            return traceArr[0] + "," + traceArr[1];
        } else {
            return "-1";
        }
    }

    /**
     * ??traceLog
     * @param traceLog ????????????1126038419678722,107390959744
     * @return ????traceLog????????
     */
    public static List<String> parseTraceLog(String traceLog) {
        if (StringUtils.isEmpty(traceLog)) {
            return null;
        }

        if (Objects.equals(traceLog, "-1")) {
            throw new RuntimeException("????? traceLog:" + traceLog);
        }

        List<String> res = new ArrayList<>();
        String[] split = StringUtils.split(traceLog, ",");
        // ???
        int move = 0;
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                move += 64;
            }
            BitSet toBitSet = longToBitSet(Long.parseLong(split[i]));
            for (int j = 0; j < toBitSet.size(); j++) {
                if (toBitSet.get(j)) {
                    res.add(TraceTag.getEnumByCode(j + move).getMsg());
                }
            }

        }

        return res;
    }

    public static BitSet longToBitSet(long value) {
        BitSet bitSet = new BitSet(64);  // ????????long????BitSet

        for (int i = 0; i < 64; i++) {
            if ((value & (1L << i)) != 0) {  // ???i????1
                bitSet.set(i);  // ???1????BitSet??i?
            }
        }

        return bitSet;
    }

    public enum TraceTag {
        BIZ_A(100, "bizA")

        ;


        public final int tag;
        public final String msg;

        TraceTag(int tag, String msg) {
            this.tag = tag;
            this.msg = msg;
        }

        public int getTag() {
            return tag;
        }

        public String getMsg() {
            return msg;
        }

        public static TraceTag getEnumByCode(Number number) {
            if (number == null || number.equals(0)) {
                return null;
            }

            int intValue = number.intValue();

            for (TraceTag value : TraceTag.values()) {
                if (intValue == value.tag) {
                    return value;
                }
            }

            return null;
        }
    }

    public static void main(String[] args) {
        TraceLogUtils traceLogUtils = new TraceLogUtils();
        // ????
        traceLogUtils.trace(TraceTag.BIZ_A);

        // ??????????????
        String traceLogContent = traceLogUtils.traceLog();
        System.out.println("biz log: " + traceLogContent);

        // ????????????????(???????????)
        List<String> traceItems = TraceLogUtils.parseTraceLog(traceLogContent);
        for (String traceItem : traceItems) {
            System.out.println(traceItem);
        }

    }
}