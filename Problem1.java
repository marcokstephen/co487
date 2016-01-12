import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Problem1 {

    // The cipher text, with line breaks and spaces removed
    private static final String ciphertext = "NHQAAIDTNXALFTGDAWMGJGWPALCCOVGKNDNEMBFSQQYPBDTOQPCIPRMMMIIQVBHVEDDEMXVDOACIQVZLNJKFGYCABKQIIDZTMTFXBBUHNIWQJNAHTRKBHVBXXMMDNGSGEWILJHHPVDDFIHBGSBFNJDKBMPVDJKILLUSPCCOUGHGPVXFMCSGOAOCCOUGHGLQWZTGDCOVRMDNDBHMSQQYTWJXEGTLSBKSMBPADJTHRQGGGCDVBKMCCSDTHOIQWSGXPBUAHIUPRJKCQTBCBNHKKQILXVWKLNJKNGGNDQWKLBPSBVHIGAZAMBPJDFWUXLDTHQLQQVHQDNPAEENUXJDSVTDKLAXDLFZIUNDVTLZIQVMOGJLVZFDEDHTFAQGHAIIWFGISDNVMGFXOKLTGDVNALBRWWLHXDAVVHQCWQOBNWBRJGJXTOGPMPVGSJOXTWXNFAWIKVOBUBKMUXVVSGXPLLFZSAIYSMIGGFMFQPAKJHIBELLAUYCJSIIIIEGPFPZDRHLPVGSPUHPFDHNWPDFZCCODDECCZDYLIUNZZBWWIWAGSHBROTQPGRXTHPVLETFPURLAVJBMMLNEWVKBVAGDJTNWIGYHNUIWKYZW";

    // Relative frequencies of English characters, fetched from Wikipedia
    static HashMap<Character, Double> englishFrequencies = new HashMap<Character, Double>();
    static {
        englishFrequencies.put('A', .08167); englishFrequencies.put('B', .01492);
        englishFrequencies.put('C', .02782); englishFrequencies.put('D', .04253);
        englishFrequencies.put('E', .12702); englishFrequencies.put('F', .02228);
        englishFrequencies.put('G', .02015); englishFrequencies.put('H', .06094);
        englishFrequencies.put('I', .06966); englishFrequencies.put('J', .00153);
        englishFrequencies.put('K', .00772); englishFrequencies.put('L', .04025);
        englishFrequencies.put('M', .02406); englishFrequencies.put('N', .06749);
        englishFrequencies.put('O', .07507); englishFrequencies.put('P', .01929);
        englishFrequencies.put('Q', .00095); englishFrequencies.put('R', .05987);
        englishFrequencies.put('S', .06327); englishFrequencies.put('T', .09056);
        englishFrequencies.put('U', .02758); englishFrequencies.put('V', .00978);
        englishFrequencies.put('W', .02361); englishFrequencies.put('X', .00150);
        englishFrequencies.put('Y', .01974); englishFrequencies.put('Z', .00074);
    }

    /**
     * This program is to be run in three steps. The first time, we try to find the length of cipher key,
     * then when length is found, we sub in the value into the KEY_LENGTH variable and re-run to find the
     * actual key. Then when the key is found, we sub it into the KEY array, and re-run to find the actual
     * decrypted text.
     */
    private static int KEY_LENGTH = 7;
    private static char[] KEY = {'V','I','R','T','U','A','L'};
    public static void main(String[] args) {

        // Part 1: Here we are finding the most likely length of the cipher key
        for (int i = 1; i <= 10; i++) {
            List<HashMap<Character, Integer>> textGroups = splitTextIntoGroups(i);
            float averageCoincidence = calculateAverageCoincidence(textGroups);
            System.out.println("Length " + i + ": " + averageCoincidence);
        }

        // Part 2: Here we are finding the key
        // ASSUMES: Key length is already determined
        List<HashMap<Character, Integer>> textGroups = splitTextIntoGroups(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            System.out.println(findMostLikelyKeyCharacter(textGroups.get(i)));
        }

        // Part 3: Here we are printing the decrypted text
        // ASSUMES: Key is determined
        for (int i = 0; i < ciphertext.length(); i++) {
            System.out.print(applyKey(ciphertext.charAt(i), KEY[i % KEY.length]));
        }
    }

    /**
     * Splits ciphertext into n groups, where n is the length of the key
     */
    private static List<HashMap<Character, Integer>> splitTextIntoGroups(int keyLength) {
        // Instantiating the groups
        List<HashMap<Character, Integer>> groupList = new ArrayList<HashMap<Character, Integer>>();
        for (int groups = 0; groups < keyLength; groups++) {
            groupList.add(new HashMap<Character, Integer>());
        }

        // Each "group" contains a hashmap which is a table counting occurrence of letters
        for (int i = 0; i < ciphertext.length(); i++) {
            Character current = ciphertext.charAt(i);
            HashMap<Character, Integer> map = groupList.get(mod(i, keyLength));
            if (map.containsKey(current)) {
                int occurrences = 1 + map.get(current);
                map.put(current, occurrences);
            } else {
                map.put(current, 1);
            }
        }
        return groupList;
    }

    /**
     * Calculate the IC of each group's letter distribution, and returns the average. The letter
     * distributions differ depending on the length of the key. This function's output should
     * be closest to 1.73 (the expected English IC) when we are using the correct key length.
     */
    private static float calculateAverageCoincidence(List<HashMap<Character, Integer>> groups) {
        float icSum = 0;
        for (HashMap group : groups) {
            icSum += computeCoincidenceIndex(group);
        }
        return icSum / groups.size();
    }

    /**
     * Calculates the IC of a letter distribution. Formula taken from Wikipedia: "Index of coincidence"
     */
    private static float computeCoincidenceIndex(HashMap<Character, Integer> distribution) {
        int totalTextLength = 0;
        float summation = 0;
        for (Character c : distribution.keySet()) {
            float occurrences = distribution.get(c);
            totalTextLength += occurrences;
            summation += (occurrences * (occurrences - 1));
        }
        return summation / ((totalTextLength * (totalTextLength - 1)) / 26);
    }

    /**
     * Applies the key to all letters in the group, returns a hashmap table with number of occurrences
     * of the decrypted letters
     */
    private static HashMap<Character, Integer> decipherCharacters(HashMap<Character, Integer> ciphertext, char key) {
        HashMap<Character, Integer> decipheredCharacters = new HashMap<Character, Integer>();
        for (Character c : ciphertext.keySet()) {
            int occurrences = ciphertext.get(c);
            char m = applyKey(c, key);
            decipheredCharacters.put(m, occurrences);
        }
        return decipheredCharacters;
    }

    /**
     * Applies the key to the cipher letter, returns the output
     */
    private static char applyKey(char cipher, char key) {
        int cipherAscii = (int) cipher;
        int keyAscii = (int) key;
        int mAscii = mod((cipherAscii - keyAscii), 26) + 65;
        return (char) mAscii;
    }

    /**
     * Loops through all possible keys and returns the key for which `computeDecipheredCorrelation`
     * returns the highest value
     */
    private static Character findMostLikelyKeyCharacter(HashMap<Character, Integer> cipherText) {
        double currentMax = 0;
        char currentMaxChar = ' ';
        for (char key = 'A'; key <= 'Z'; key++) {
            HashMap<Character, Integer> deciphered = decipherCharacters(cipherText, key);
            double currentGuessCorrelation = computeDecipheredCorrelation(deciphered);
            if (currentGuessCorrelation > currentMax) {
                currentMax = currentGuessCorrelation;
                currentMaxChar = key;
            }
        }
        return currentMaxChar;
    }

    /**
     * Uses formula taken from wikipedia: "Index of coincidence" to assign a likelihood of whether
     * or not this group of letters reflects the english language
     */
    private static double computeDecipheredCorrelation(HashMap<Character, Integer> deciphered) {
        double occurrenceTotal = 0;
        double relativeTotal = 0;
        for (Character c : deciphered.keySet()) {
            occurrenceTotal += deciphered.get(c);
        }
        for (Character c : deciphered.keySet()) {
            double characterFrequency = ((double) deciphered.get(c)) / occurrenceTotal;
            double englishFrequency = englishFrequencies.get(c);
            relativeTotal += characterFrequency * englishFrequency;
        }
        return relativeTotal;
    }

    /**
        Returns a mod that is guaranteed to be positive
     */
    private static int mod(int n, int m) {
        return (((n % m) + m) % m);
    }
}
