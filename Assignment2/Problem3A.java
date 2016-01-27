import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Problem3A {

    private static final Map<Integer, Integer> sBox = new HashMap<Integer, Integer>();
    static {
        sBox.put(0, 14); sBox.put(1, 4); sBox.put(2, 13); sBox.put(3, 1); sBox.put(4, 2);
        sBox.put(5, 15); sBox.put(6, 11); sBox.put(7, 8); sBox.put(8, 3); sBox.put(9, 10);
        sBox.put(10, 6); sBox.put(11, 12); sBox.put(12, 5); sBox.put(13, 9); sBox.put(14, 0);
        sBox.put(15, 7);
    }


    public static void main(String[] args) throws IOException {
        int maxSimilarities = 0;
        char[] key = {'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
        char[] tempKey = new char[key.length];

        for (char k5 = '0'; k5 <= '1'; k5++) {
            key[4] = k5;
            for (char k6 = '0'; k6 <= '1'; k6++) {
                key[5] = k6;
                for (char k7 = '0'; k7 <= '1'; k7++) {
                    key[6] = k7;
                    for (char k8 = '0'; k8 <= '1'; k8++) {
                        key[7] = k8;
                        for (char k13 = '0'; k13 <= '1'; k13++) {
                            key[12] = k13;
                            for (char k14 = '0'; k14 <= '1'; k14++) {
                                key[13] = k14;
                                for (char k15 = '0'; k15 <= '1'; k15++) {
                                    key[14] = k15;
                                    for (char k16 = '0'; k16 <= '1'; k16++) {
                                        key[15] = k16;

                                        int similarities = countSimilarities(key);
                                        System.out.println(similarities + ": " + keyToString(key));
                                        if (similarities > maxSimilarities) {
                                            maxSimilarities = similarities;
                                            System.arraycopy(key, 0, tempKey, 0, key.length);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Maximum similarities was " + maxSimilarities + " and key was ");
        for (int i = 0; i < tempKey.length; i++) {
            System.out.print(tempKey[i]);
        }
    }

    private static String keyToString(char[] key) {
        StringBuilder sb = new StringBuilder();
        for (char k : key) {
            sb.append(k);
        }
        return sb.toString();
    }

    private static int countSimilarities(char[] key) throws IOException {
        int counter = 0;
        BufferedReader br = new BufferedReader(new FileReader("differentialciphertext82.txt"));
        for (int i = 0; i < 5000; i++) {
            String input = br.readLine();
            String[] inputArray = input.split(",");

            String yPrime = inputArray[2];
            String yDoublePrime = inputArray[3];

            String v4yPrime = obtainV4(key, yPrime);
            String u4yPrime = obtainU4(v4yPrime);
            String v4yDoublePrime = obtainV4(key, yDoublePrime);
            String u4yDoublePrime = obtainU4(v4yDoublePrime);

            String deltaU4 = calculateDelta(u4yPrime, u4yDoublePrime);
            if (deltaU4.equals("0000011000000110")) counter++;
        }
        return counter;
    }

    /**
     * Calculates the delta. s XOR sPrime == DELTA S
     */
    private static String calculateDelta(String s, String sPrime) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(xor(s.charAt(i), sPrime.charAt(i)));
        }
        return sb.toString();
    }

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
