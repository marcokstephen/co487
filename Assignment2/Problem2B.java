import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Problem2B {

    private static final Map<Integer, Integer> sBox = new HashMap<Integer, Integer>();
    static {
        sBox.put(0, 14); sBox.put(1, 4); sBox.put(2, 13); sBox.put(3, 1); sBox.put(4, 2);
        sBox.put(5, 15); sBox.put(6, 11); sBox.put(7, 8); sBox.put(8, 3); sBox.put(9, 10);
        sBox.put(10, 6); sBox.put(11, 12); sBox.put(12, 5); sBox.put(13, 9); sBox.put(14, 0);
        sBox.put(15, 7);
    }

    public static void main(String[] args) throws IOException {

        char[] key = {'0','0','0','0','0','0','0','0'};

        double maximumBias = 0;
        char[] maximumKey = new char[8];

        for (char k5 = '0'; k5 <= '1'; k5++) {
            key[0] = k5;
            for (char k6 = '0'; k6 <= '1'; k6++) {
                key[1] = k6;
                for (char k7 = '0'; k7 <= '1'; k7++) {
                    key[2] = k7;
                    for (char k8 = '0'; k8 <= '1'; k8++) {
                        key[3] = k8;
                        for (char k13 = '0'; k13 <= '1'; k13++) {
                            key[4] = k13;
                            for (char k14 = '0'; k14 <= '1'; k14++) {
                                key[5] = k14;
                                for (char k15 = '0'; k15 <= '1'; k15++) {
                                    key[6] = k15;
                                    for (char k16 = '0'; k16 <= '1'; k16++) {
                                        key[7] = k16;

                                        double currentBias = calculateBias(key);
                                        System.out.println(currentBias);
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
        int equals1 = 0;
        BufferedReader plaintextBR = new BufferedReader(new FileReader("plaintexts.txt"));
        BufferedReader ciphertextBR = new BufferedReader(new FileReader("ciphertext82.txt"));

        for (int i = 0; i < 20000; i++) {
            String plaintext = plaintextBR.readLine();
            String ciphertext = ciphertextBR.readLine();

            String v4 = obtainV4(key, ciphertext);
            String u4 = obtainU4(v4);

            char u46 = u4.charAt(1);
            char u48 = u4.charAt(3);
            char u414 = u4.charAt(5);
            char u416 = u4.charAt(7);
            char p5 = plaintext.charAt(4);
            char p7 = plaintext.charAt(6);
            char p8 = plaintext.charAt(7);

            char output = xor(xor(xor(xor(xor(xor(u46, u48), u414), u416), p5), p7), p8);

            if (output == '0') {
                equals0++;
            } else {
                equals1++;
            }
        }

        double bias = (((double)equals0) / 20000.0) - 0.5;
        return Math.abs(bias);
    }

    /**
     * Returns u4 for bits 5,6,7,8,13,14,15,16 as a binary string
     */
    private static String obtainU4(String v4) {
        int v5to8 = binaryStringToInt(v4.substring(0, 4));
        int v13to16 = binaryStringToInt(v4.substring(4, 8));

        int u5to8 = -1;
        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == v5to8) {
                u5to8 = e.getKey();
                break;
            }
        }

        int u13to16 = -1;
        for (Map.Entry<Integer, Integer> e : sBox.entrySet()) {
            if (e.getValue() == v13to16) {
                u13to16 = e.getKey();
                break;
            }
        }

        if (u5to8 == -1 || u13to16 == -1) {
            throw new RuntimeException("something bad happened");
        }

        return intToBinaryString(u5to8) + intToBinaryString(u13to16);
    }

    /**
     * Returns v4 for bits 5,6,7,8,13,14,15,16 in a string
     */
    private static String obtainV4(char[] key, String ciphertext) {
        String c5to8 = ciphertext.substring(4, 8);
        String c13to16 = ciphertext.substring(12, 16);
        String cipherSubstring = c5to8 + c13to16;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length; i++) {
            sb.append(xor(key[i], cipherSubstring.charAt(i)));
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
