public class BitCalc {

    private String a;
    private String b;
    public String add(String a, String b) {
        this.a = a;
        this.b = b;
        return add();
    }
    public String add() {
        int a = Integer.parseInt(this.a, 2);
        int b = Integer.parseInt(this.b, 2);
        int sum = a + b;
        return Integer.toBinaryString(sum);
    }

    public String subtract(String a, String b) {
        this.a = a;
        this.b = b;
        return subtract();
    }
    public String subtract() {
        int a = Integer.parseInt(this.a, 2);
        int b = Integer.parseInt(this.b, 2);
        int diff = a - b;
        return Integer.toBinaryString(diff);
    }

    public String multiply(String a, String b) {
        this.a = a;
        this.b = b;
        return multiply();
    }

    public String multiply() {
        int a = Integer.parseInt(this.a, 2);
        int b = Integer.parseInt(this.b, 2);
        int product = a * b;
        return Integer.toBinaryString(product);
    }

    public String divide(String a, String b) {
        this.a = a;
        this.b = b;
        return divide();
    }

    public String divide() {
        int a = Integer.parseInt(this.a, 2);
        int b = Integer.parseInt(this.b, 2);
        int quotient = a / b;
        return Integer.toBinaryString(quotient);
    }



    public String to16BitBinary(int number) {
        String binaryString = Integer.toBinaryString(number & 0xFFFF);
        return String.format("%16s", binaryString).replace(' ', '0');
    }

    // Tính chuẩn bù 1 cho số nguyên 16 bits
    public String onesComplement(int number) {
        if(number >= 0) {
            return toSignMagnitudeBinary(number);
        }
        return to16BitBinary(~(-number));
    }

    // Convert an integer to binary two's complement
    public String twosComplement(int number) {
        if(number >= 0) {
            return toSignMagnitudeBinary(number);
        }
        return to16BitBinary(~(-number)+1);
    }

    public String decimalToBinary(int number) {
        return Integer.toBinaryString(number);
    }
    public String toSignMagnitudeBinary(int number) {
        String binaryString = Integer.toBinaryString(Math.abs(number));
        if (number < 0) {
            return "1" + String.format("%15s", binaryString).replace(' ', '0');
        } else {
            return "0" + String.format("%15s", binaryString).replace(' ', '0');
        }
    }
    public int smbToInteger(String smb) {
        int sign = smb.charAt(0) == '1' ? -1 : 1;
        String magnitude = smb.substring(1);
        int value = Integer.parseInt(magnitude, 2);
        return sign * value;
    }

    // Convert One's Complement to integer
    public int onesComplementToInteger(String onesComp) {
        if (onesComp.charAt(0) == '1') {
            // Invert bits and add 1
            String inverted = invertBits(onesComp);
            int value = Integer.parseInt(inverted, 2);
            return -(value);
        } else {
            return Integer.parseInt(onesComp, 2);
        }
    }

    // Convert Two's Complement to integer
    public int twosComplementToInteger(String twosComp) {
        if (twosComp.charAt(0) == '1') {
            // Invert bits and add 1
            String inverted = invertBits(twosComp);
            int value = Integer.parseInt(inverted, 2)+1;
            return -(value);
        } else {
            return Integer.parseInt(twosComp, 2);
        }
    }

    // Helper method to invert bits
    private String invertBits(String binary) {
        StringBuilder inverted = new StringBuilder();
        for (char bit : binary.toCharArray()) {
            inverted.append(bit == '0' ? '1' : '0');
        }
        return inverted.toString();
    }
    public double fixedPointToDecimal(String binary) {
        if(binary.charAt(0) == '1') {
            return -fixedPointToDecimal(invertBits(binary));
        }
        // Separate the integer and fractional parts
        String integerPart = binary.substring(0, binary.length() - 4);
        String fractionalPart = binary.substring(binary.length() - 4);

        // Convert integer part to decimal
        int integerValue = Integer.parseInt(integerPart, 2);

        // Convert fractional part to decimal
        double fractionalValue = 0;
        for (int i = 0; i < fractionalPart.length(); i++) {
            if (fractionalPart.charAt(i) == '1') {
                fractionalValue += Math.pow(2, -(i + 1));
            }
        }

        // Combine both parts
        return integerValue + fractionalValue;
    }
        public String decimalToFixedPoint(double number, int integerBits, int fractionalBits) {
            boolean isNegative = number < 0;
            if (isNegative) {
                number = -number;
            }

            // Separate the integer and fractional parts
            int integerPart = (int) number;
            double fractionalPart = number - integerPart;

            // Convert integer part to binary
            String integerBinary = Integer.toBinaryString(integerPart);

            // Convert fractional part to binary
            StringBuilder fractionalBinary = new StringBuilder();
            for (int i = 0; i < fractionalBits; i++) {
                fractionalPart *= 2;
                if (fractionalPart >= 1) {
                    fractionalBinary.append('1');
                    fractionalPart -= 1;
                } else {
                    fractionalBinary.append('0');
                }
            }

            // Combine both parts with a decimal point
            String combinedBinary = integerBinary + "." + fractionalBinary.toString();

            // Ensure the integer part is the correct length
            if (integerBinary.length() > integerBits) {
                integerBinary = integerBinary.substring(integerBinary.length() - integerBits);
            } else {
                integerBinary = String.format("%" + integerBits + "s", integerBinary).replace(' ', '0');
            }

            // Ensure the result is the correct length
            combinedBinary = integerBinary + "." + fractionalBinary.toString();

            if (isNegative) {
                // Convert to two's complement
                int value = Integer.parseInt(integerBinary + fractionalBinary.toString(), 2);
                value = ~value + 1;
                combinedBinary = Integer.toBinaryString(value);
                combinedBinary = combinedBinary.substring(combinedBinary.length() - (integerBits + fractionalBits));
                combinedBinary = combinedBinary.substring(0, integerBits) + "." + combinedBinary.substring(integerBits);
            }

            return combinedBinary;
        }
    public String divideFixedPoint(String binary1, String binary2, int integerBits, int fractionalBits) {
        // Normalize fractional bits
        int maxFractionalBits = Math.max(binary1.length() - binary1.indexOf('.') - 1, binary2.length() - binary2.indexOf('.') - 1);
        binary1 = normalizeFractionalBits(binary1, maxFractionalBits);
        binary2 = normalizeFractionalBits(binary2, maxFractionalBits);

        double decimal1 = fixedPointToDecimal(binary1);
        double decimal2 = fixedPointToDecimal(binary2);
        double result = decimal1 / decimal2;
        return decimalToFixedPoint(result, integerBits, fractionalBits);
    }
    private String normalizeFractionalBits(String binary, int targetFractionalBits) {
        int currentFractionalBits = binary.length() - binary.indexOf('.') - 1;
        if (currentFractionalBits < targetFractionalBits) {
            // Add trailing zeros
            binary += "0".repeat(targetFractionalBits - currentFractionalBits);
        } else if (currentFractionalBits > targetFractionalBits) {
            // Truncate extra bits
            binary = binary.substring(0, binary.indexOf('.') + 1 + targetFractionalBits);
        }
        return binary;
    }
    public static String toIEEE754(String fractionalBinary, int exponentBias, int sign, boolean isDoublePrecision) {
        double number = binaryFractionToDecimal(fractionalBinary);
        if (sign == 1) {
            number = -number;
        }
        return isDoublePrecision ? toIEEE754Double(number, exponentBias) : toIEEE754Single(number, exponentBias);
    }

    private static double binaryFractionToDecimal(String binary) {
        double result = 0;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                result += Math.pow(2, -(i + 1));
            }
        }
        return result;
    }

    private static String toIEEE754Single(double number, int exponentBias) {
        int bits = Float.floatToIntBits((float) number);
        String binaryString = String.format("%32s", Integer.toBinaryString(bits)).replace(' ', '0');
        return formatIEEE754(binaryString, 23, exponentBias);
    }

    private static String toIEEE754Double(double number, int exponentBias) {
        long bits = Double.doubleToLongBits(number);
        String binaryString = String.format("%64s", Long.toBinaryString(bits)).replace(' ', '0');
        return formatIEEE754(binaryString, 52, exponentBias);
    }

    private static String formatIEEE754(String binaryString, int fractionalBits, int exponentBias) {
        String signBit = binaryString.substring(0, 1);
        String exponentBits = binaryString.substring(1, 1 + exponentBias);
        String fractionBits = binaryString.substring(1 + exponentBias, 1 + exponentBias + fractionalBits);
        return signBit + " " + exponentBits + " " + fractionBits;
    }
    public String toIEEE754SinglePrecision(String fraction,int exponentBias, int sign)
    {
        int exp = 127 + exponentBias;
        String expBinary = (8-Integer.toBinaryString(exp).length() > 0 ? "0".repeat(8-Integer.toBinaryString(exp).length()) : "") + Integer.toBinaryString(exp);
        String fracBinary = fraction + (23-fraction.length() > 0 ? "0".repeat(23-fraction.length()) : "");
        String signBit = sign == 1 ? "1" : "0";
        return signBit + " " + expBinary + " " + fracBinary;

    }
    public String toIEEE754DoublePrecision(String fraction,int exponentBias, int sign)
    {
        int exp = 1023 + exponentBias;
        String expBinary = (11-Integer.toBinaryString(exp).length() > 0 ? "0".repeat(11-Integer.toBinaryString(exp).length()) : "") + Integer.toBinaryString(exp);
        String fracBinary = fraction + (52-fraction.length() > 0 ? "0".repeat(52-fraction.length()) : "");
        String signBit = sign == 1 ? "1" : "0";
        return signBit + " " + expBinary + " " + fracBinary;

    }
    public static void main(String[] args) {
        BitCalc bc = new BitCalc();
        System.out.println("Addition: ");
        System.out.println(bc.add("10101010", "110010"));
        System.out.println("Subtraction: ");
        System.out.println(bc.subtract("1000111110", "10101101"));
        System.out.println("Multiplication: ");
        System.out.println(bc.multiply("11001", "100101"));
        System.out.println("Division: ");
        System.out.println(bc.divide("11010110011", "100011"));
        System.out.println("onesComplement: ");
        System.out.println(bc.onesComplement(456));
        System.out.println("twosComplement: ");
        System.out.println(bc.twosComplement(-654));
        System.out.println("SBM: ");
        System.out.println(bc.toSignMagnitudeBinary(-123));
        System.out.println("SBM To Integer: ");
        System.out.println(bc.smbToInteger("1010000011111101"));
        System.out.println("onesComplement To Integer: ");
        System.out.println(bc.onesComplementToInteger("1010000011111101"));
        System.out.println("twosComplement To Integer: ");
        System.out.println(bc.twosComplementToInteger("1010000011111101"));
        System.out.println("Dấu phẩy tĩnh sang thập phân: ");
        System.out.println(bc.fixedPointToDecimal("1100100101110111"));
        System.out.println("Thập phân sang dấu phẩy tĩnh: ");
        System.out.println(bc.decimalToFixedPoint(834.125, 12, 4));
        System.out.println("Chia dấu phẩy tĩnh: ");
        System.out.println("Dấu phẩy động độ chính xác đơn: ");
        System.out.println(bc.toIEEE754SinglePrecision("110011101",41,1));
        System.out.println("Dấu phẩy động độ chính xác kép: ");
        System.out.println(bc.toIEEE754DoublePrecision("10010001",-8,1));
    }

}
