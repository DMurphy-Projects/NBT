public class BinaryTest {

    public static void writeWithPosition(long base, long x, int pos)
    {
        //writes x starting at pos to the base
        long digits = (long) (Math.log(x) / Math.log(2)) + 1;
        long mask = (1 << digits) - 1;
        long maskValue = (base >> pos) & mask;

        boolean split = (pos + digits) > 64;
        if (split)
        {
            long firstHalfMask = (1 << (64 - pos)) - 1;
            long secondHalfMask = mask - firstHalfMask;

            writeWithPosition(base, x & firstHalfMask, pos);
            writeWithPosition(base, (x & secondHalfMask) >> (64 - pos), 0);
        }
        else {
            System.out.println(base + ((x - maskValue) << pos));
        }
    }

    public static long readWithPosition(long base, int digits, int pos)
    {
        boolean split = (pos + digits) > 64;
        if (split)
        {
            long l1 = readWithPosition(base, (64 - pos), pos);
            int l2Size = (pos + digits) - 64;
            long l2 = readWithPosition(base, l2Size, 0);

            return (l1 << l2Size) | l2;
        }
        else
        {
            long mask = (1 << digits) - 1;
            return (base >> pos) & mask;
        }
    }

    public static void main(String[] args)
    {
//        writeWithPosition(0, 100, 61);
        long out = readWithPosition(0, 7, 60);

        System.out.println(out);
    }
}
