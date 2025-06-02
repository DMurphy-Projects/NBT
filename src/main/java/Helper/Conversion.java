package Helper;

import dev.dewy.nbt.tags.array.LongArrayTag;

public class Conversion {

    public static long[] BlockStatesToLongArray(LongArrayTag blockStates)
    {
        long[] arr = new long[blockStates.size()];

        int index = 0;
        for (Long l: blockStates)
        {
            arr[index++] = l;
        }

        return arr;
    }
}
