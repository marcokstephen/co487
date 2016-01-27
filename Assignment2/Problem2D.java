import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Problem2D {

    private static final Map<Integer, Integer> sBox = new HashMap<Integer, Integer>();
    static {
        sBox.put(0, 14); sBox.put(1, 4); sBox.put(2, 13); sBox.put(3, 1); sBox.put(4, 2);
        sBox.put(5, 15); sBox.put(6, 11); sBox.put(7, 8); sBox.put(8, 3); sBox.put(9, 10);
        sBox.put(10, 6); sBox.put(11, 12); sBox.put(12, 5); sBox.put(13, 9); sBox.put(14, 0);
        sBox.put(15, 7);
    }

    public static void main(String[] args) throws IOException {
        char[] key = {'0','0','0','0','0','1','0','1','0','0','0','0','0','0','0','0'};

        double maximumBias = 0;
        char[] maximumKey = new char[key.length];

        for (char k5 = '0'; k5 <= '1'; k5++) {
            key[0] = k5;
            for (char k6 = '0'; k6 <= '1'; k6++) {
                key[1] = k6;
                for (char k7 = '0'; k7 <= '1'; k7++) {
                    key[2] = k7;
                    for (char k8 = '0'; k8 <= '1'; k8++) {
                        key[3] = k8;
                        for (char k13 = '0'; k13 <= '1'; k13++) {
                            key[8] = k13;
                            for (char k14 = '0'; k14 <= '1'; k14++) {
                                key[9] = k14;
                                for (char k15 = '0'; k15 <= '1'; k15++) {
                                    key[10] = k15;
                                    for (char k16 = '0'; k16 <= '1'; k16++) {
                                        key[11] = k16;

                                        double currentBias = calculateBias(key);
                                        if (currentBias > maximumBias) {
                                            maximumBias = currentBias;
                                            System.arraycopy(key, 0, maximumKey, 0, key.length);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Maximum bias was " + maximumBias + " and key was ");
        for (int i = 0; i < maximumKey.length; i++) {
            System.out.print(maximumKey[i]);
        }
    }

    private static double calculateBias(char[] key) throws IOException {
        int equals0 = 0;
        BufferedReader plaintextBR = new BufferedReader(new FileReader("plaintexts.txt"));
        BufferedReader ciphertextBR = new BufferedReader(new FileReader("ciphertext82.txt"));

        for (int i = 0; i < 20000; i++) {
            String plaintext = plaintextBR.readLine();
            String ciphertext = ciphertextBR.readLine();

            String v4 = obtainV4(key, ciphertext);
            String u4 = obtainU4(v4);

            char u42 = u4.charAt(1);
            char u46 = u4.charAt(5);
            char u410 = u4.charAt(9);
            char u414 = u4.charAt(13);
            char p1 = plaintext.charAt(0);
            char p4 = plaintext.charAt(3);
            char p9 = plaintext.charAt(8);
            char p12 = plaintext.charAt(11);

            char output = xor(xor(xor(xor(xor(xor(xor(p1, p4), p9), p12), u42), u46), u410), u414);

            if (output == '0') {
                equals0++;
            }
        }

        double bias = (((double)equals0) / 20000.0) - 0.5;
        return Math.abs(bias);
    }

    /**
     * Returns u4 for bits 5,6,7,8,13,14,15,16 as a binary string
     */
    private static String obtainU4(String v4) {
        int section1 = binaryStringToInt(v4.substring(0, 4));
        int section2 = binaryStringToInt(v4.substring(4, 8));
        int section3 = binaryStringToInt(v4.substring(8, 12));
        int section4 = binaryStringToInt(v4.substring(12, 16));

        int u1to4 = -1;
        int u5to8 = -1;
        int u9to12 = -1;
        int u13to16 = -1;
        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == section1) {
                u1to4 = e.getKey();
                break;
            }
        }

        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == section2) {
                u5to8 = e.getKey();
                break;
            }
        }

        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == section3) {
                u9to12 = e.getKey();
                break;
            }
        }

        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == section4) {
                u13to16 = e.getKey();
                break;
            }
        }

        if (u1to4 == -1 || u9to12 == -1 || u5to8 == -1 || u13to16 == -1) {
            throw new RuntimeException("something bad happened");
        }

        return intToBinaryString(u1to4) + intToBinaryString(u5to8) + intToBinaryString(u9to12) + intToBinaryString(u13to16);
    }

    /**
     * Returns v4 for all bits
     */
    private static String obtainV4(char[] key, String ciphertext) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length; i++) {
            sb.append(xor(key[i], ciphertext.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * Returns a xor b, as a char representing '0' or '1'
     */
    private static char xor(char a, char b) {
        return (a == b) ? '0' : '1';
    }

    /**
     * Converts a binary string to its integer value
     */
    private static int binaryStringToInt(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * Converts an integer into its binary string, including up to 4 leading zeroes
     */
    private static String intToBinaryString(int i) {
        return String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0');
    }
}
