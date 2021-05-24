package converter;
import java.util.List;
import java.util.Scanner;

class Converter {
    private final int minRadix;
    private final int maxRadix;
    private final String alphabet;

    public Converter() {
        this(1, 36, "0123456789abcdefghijklmnopqrstuvwxyz");
    }

    public Converter(int minRadix, int maxRadix, String alphabet) {
        if (maxRadix > alphabet.length()) {
            String message = String.format("The maximum radix is greater than the length of the alphabet: %d > %d.",
                    maxRadix, alphabet.length());
            throw new IllegalArgumentException(message);
        } else if (minRadix > maxRadix) {
            String message = String.format("The minimum radix is greater than the maximum radix: %d > %d.",
                    minRadix, maxRadix);
            throw new IllegalArgumentException(message);
        }
        this.minRadix = minRadix;
        this.maxRadix = maxRadix;
        this.alphabet = alphabet;
    }

    public String convert(String source, int sourceRadix, int targetRadix) {
        requireRadixInRange(sourceRadix);
        requireRadixInRange(targetRadix);
        if (sourceRadix == 1 || targetRadix == 1) {
            return convertByOne(source, sourceRadix, targetRadix);
        } else if (source.contains(".")) {
            String[] number = source.split("\\.");
            String integerPart = number[0];
            int integer = convertToDecimal(integerPart, sourceRadix);
            String convertedInteger = convertFromDecimal(integer, targetRadix);
            String fractionalPart = number[1];
            double fraction = getFraction(fractionalPart, sourceRadix);
            String convertedFraction = convertFromFraction(fraction, targetRadix);
            return convertedInteger + "." + convertedFraction;
        } else {
            int decimal = convertToDecimal(source, sourceRadix);
            return convertFromDecimal(decimal, targetRadix);
        }
    }

    public String convertByOne(String source, int sourceRadix, int targetRadix) {
        String[] number = source.split("\\.");
        String integerPart = number[0];
        int integer;
        if (sourceRadix == 1) {
            integer = integerPart.length();
        } else {
            integer = convertToDecimal(integerPart, sourceRadix);
        }
        if (targetRadix == 1) {
            return "1".repeat(Math.max(0, integer));
        } else {
            return convertFromDecimal(integer, targetRadix);
        }
    }

    private void requireRadixInRange(int radix) {
        if (radix < minRadix) {
            String message = String.format("The radix is less than the minimum radix: %d < %d.",
                    radix, minRadix);
            throw new IllegalArgumentException(message);
        } else if (radix > maxRadix) {
            String message = String.format("The radix is greater than the maximum radix: %d > %d.",
                    radix, maxRadix);
            throw new IllegalArgumentException(message);
        }
    }

    public int convertToDecimal(String source, int sourceRadix) {
        char[] members = source.toCharArray();
        int sum = 0;
        for (int i = 0, n = members.length - 1; i <= n; i++) {
            char member = members[i];
            int value = getValueFromAlphabet(member);
            int power = (int) Math.pow(sourceRadix, n - i);
            int multi = value * power;
            sum += multi;
        }
        return sum;
    }

    public String convertFromDecimal(int source, int targetRadix) {
        StringBuilder builder = new StringBuilder();
        int remainder;
        int dividend = source;
        do {
            remainder = dividend % targetRadix;
            char member = getMemberFromAlphabet(remainder);
            builder.append(member);
        } while ((dividend /= targetRadix) >= targetRadix);
        if (dividend != 0) {
            builder.append(getMemberFromAlphabet(dividend));
        }
        return builder.reverse().toString();
    }

    public String convertFromFraction(double source, int targetRadix) {
        double multiplier = source;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            multiplier *= targetRadix;
            int value = (int) multiplier;
            char member = getMemberFromAlphabet(value);
            builder.append(member);
            String string = String.valueOf(multiplier);
            int index = string.indexOf('.');
            String fraction = string.substring(index + 1);
            multiplier = Double.parseDouble("0." + fraction);
        }
        return builder.toString();
    }

    private int getValueFromAlphabet(char member) {
        int index = alphabet.indexOf(member);
        if (index >= 0) {
            return index;
        } else {
            String message = String.format("The alphabet doesn't contain member: '%s'.", member);
            throw new IllegalArgumentException(message);
        }
    }

    private char getMemberFromAlphabet(int value) {
        if (value >= 0 && value < alphabet.length()) {
            return alphabet.charAt(value);
        } else {
            String message = String.format("The value is greater than or equal to the length of the alphabet: %d >= %d.",
                    value, alphabet.length());
            throw new IllegalArgumentException(message);
        }
    }

    public double getFraction(String source, int sourceRadix) {
        char[] members = source.toCharArray();
        double sum = 0.0;
        for (int i = 0, n = members.length; i < n; i++) {
            char member = members[i];
            double value = getValueFromAlphabet(member);
            double power = (int) Math.pow(sourceRadix, i + 1);
            sum += value / power;
        }
        return sum;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> lines = new java.util.ArrayList<>(List.of());
        while (scanner.hasNextLine() && lines.size() != 3) {
            lines.add(scanner.nextLine());
        }
        if (lines.size() != 3) {
            error();
            return;
        }
        String sourceRadix = lines.get(0);
        String radixRegex = "([1-9]|1[0-9]|2[0-9]|3[0-6])";
        if (!sourceRadix.matches(radixRegex)) {
            error();
            return;
        }
        String sourceNumber = lines.get(1);
        if (!sourceNumber.matches("([1-9a-z][0-9a-z]*(\\.[0-9a-z]*)?|0\\.[0-9a-z]+)")) {
            error();
            return;
        }
        String targetRadix = lines.get(2);
        if (!targetRadix.matches(radixRegex)) {
            error();
            return;
        }
        Converter converter = new Converter();
        String targetNumber = converter.convert(
                sourceNumber,
                Integer.parseInt(sourceRadix),
                Integer.parseInt(targetRadix));
        System.out.println(targetNumber);
    }

    private static void error() {
        System.out.println("error");
    }
}