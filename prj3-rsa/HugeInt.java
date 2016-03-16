import java.util.Arrays;

/**
 *
 * @author Lily Lu and Dennis Leancu
 * Implementation of HugeInt to represent numbers of any arbitrary digits
 * Represents positive values only
 * Allows user to add, subtract, multiple, divide, and modulus any two
 * postive numbers
 */

public class HugeInt {

    private int [] littleEndian;  // little endian representation of a number
    private int [] bigEndian;     // big endian representation of a number
    private int numDigits;        // number of digits this HugeInt is using
    // to represent a number
    private int zeroPad;          // number of zero padded to the front of the number in little endian



    public HugeInt(String s){

        // Assumes that s is a valid string that is being passed
        String pattern = "[0-9]+";
        if (!s.matches(pattern)){
            return;
        }
        numDigits = s.length();
        littleEndian = new int[numDigits];
        bigEndian = new int[numDigits];
        for(int i = 0; i < numDigits; i++){
            bigEndian[i] = Character.getNumericValue(s.charAt(i));
        }

        for(int i = 0; i < numDigits; i++){
            littleEndian[numDigits-i-1] = bigEndian[i];
        }

        zeroPad = countZeroPadBigEndian();

    }



    private int countZeroPadBigEndian(){
        int i = 0;
        int count = 0;
        while (i < numDigits && bigEndian[i] == 0){
            count++;
            i++;
        }
        return count;
    }



    /**
     * @return numDigits
     */
    public int getNumDigits(){
        return numDigits;
    }

    /**
     * @return littleEndian
     */
    public int[] getLittleEndian(){
        return littleEndian;
    }

    /**
     * @return bigEndian
     */
    public int[] getBigEndian(){
        return bigEndian;
    }

    /**
     * @return zeroPad
     */
    public int getZeroPad(){
        return zeroPad;
    }

    /**
     * Convert this HugeInt to be represented as length digits
     * length >= HugeInt.numDigits
     * @param length how many digits we want to represent this as
     * @return null if length is < numDigits
     *          HugeInt of the same number with additional zero padding
     */
    public HugeInt convertToSameLength(int length){
        if (length < numDigits){
            return null;
        } else if (length == numDigits){
            return this;
        }
        String bigEndianString = Arrays.toString(bigEndian).replaceAll("[^0-9]","");

        StringBuilder formatBigEndian = new StringBuilder();
        for (int i = 0; i < (length-numDigits); i++){
            formatBigEndian.append('0');
        }
        formatBigEndian.append(bigEndianString);
        return new HugeInt(formatBigEndian.toString());
    }

    /**
     * @return  Returns a string representation of this number
     */
    public String bigEndianToString(){
        if (this.isZero()){
            return "0";
        }
        return Arrays.toString(bigEndian).replaceAll("[^0-9]","").replaceFirst("0*","");
    }

    /**
     * Testing
     */
    public void printDigits(){

        System.out.println("length: " + numDigits);
        System.out.print("Little: " + Arrays.toString(littleEndian).replaceAll("[^0-9]", " "));

        for(int i = 0; i < numDigits; i++){
            System.out.print(littleEndian[i]);
        }
        System.out.println();
        System.out.print("Big: " + Arrays.toString(bigEndian).replaceAll("[^0-9]", " ") );
        System.out.println();
    }

    public boolean isZero(){
        if (numDigits == zeroPad){
            return true;
        }
        return false;
    }

    /**
     * Check is a == b
     * @param a a HugeInt
     * @param b a HugeInt
     * @return true if a==b, false if a != b
     */
    public static boolean equalsHugeInt(HugeInt a, HugeInt b){
        int length;
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // b has more digits or both have the same number of digits
        else if (a.getNumDigits() > b.getNumDigits()){
            length = a.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        else {
            length = a.getNumDigits();
        }

        int [] aVal = a.getBigEndian();
        int [] bVal = b.getBigEndian();
        for(int i = 0; i < length; i++){
            if(aVal[i] != bVal[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * Check to see if the number represented by a is greater than b
     * @param a a HugeInt
     * @param b a HugeInt
     * @return True is a > b, false if a <= b
     */
    public static boolean greaterThan(HugeInt a, HugeInt b){
        int length;
        int [] aVal, bVal;
        int equals = 0;
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // b has more digits or both have the same number of digits
        else if (a.getNumDigits() > b.getNumDigits()){
            length = a.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        else {
            length = a.getNumDigits();
        }
        aVal = a.getBigEndian();
        bVal = b.getBigEndian();
        for (int i = 0; i < length; i++){
            if(aVal[i] > bVal[i]){
                return true;
            }
            else if(aVal[i] == bVal[i]){
                equals++;
            }
            else {
                return false;
            }
        }
        if (equals == length) {
            return false;
        }
        return false;
    }

    /**
     * Check to see if a < b
     * @param a a HugeInt
     * @param b a HugeInt
     * @return true is a < b, false if a >= b
     */
    public static boolean lessThan(HugeInt a, HugeInt b){
        int length;
        int [] aVal, bVal;
        int equals = 0;
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // b has more digits or both have the same number of digits
        else if (a.getNumDigits() > b.getNumDigits()){
            length = a.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        else {
            length = a.getNumDigits();
        }
        aVal = a.getBigEndian();
        bVal = b.getBigEndian();
        for (int i = 0; i < length; i++){
            if(aVal[i] > bVal[i]){
                return false;
            }
            else if(aVal[i] == bVal[i]){
                equals++;
            }
            else {
                return true;
            }
        }
        if (equals == length) {
            return false;
        }
        return false;
    }


    /**
     * Adds a and b
     * @param a a HugeInt
     * @param b a HugeInt
     * @return a HugeInt representing the sum of a,b
     */
    public static HugeInt addHugeInt(HugeInt a, HugeInt b){

        int sum;
        int [] sumArr; // array to hold the new sum, little endian
        int [] xVal;
        int [] yVal;
        int length; // how many digits the new sum can hold
        // which is the greater number of digits of a and b
        // + 1 for the potential carry

        // a has less digits than b
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits() +1;
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // a has more digits or both have the same number of digits
        else{
            length = a.getNumDigits() + 1;
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }

        sumArr = new int [length];
        Arrays.fill(sumArr, 0);
        xVal = a.getLittleEndian();
        yVal = b.getLittleEndian();

        for (int i = 0; i < length-1; i++){

            sum = xVal[i] + yVal [i];
            sumArr[i+1] += sum / 10;
            sumArr[i] += sum % 10; // for a carry value
        }

        // change the number of little endian representation to big endian representation
        for(int i = 0; i < length/2; i++){
            int temp = sumArr[length-i-1];
            sumArr[length-i-1] = sumArr[i];
            sumArr[i] = temp;
        }

        return new HugeInt(Arrays.toString(sumArr).replaceAll("[^0-9]", ""));
    }


    /**
     * Subtracts b from a. a must be greater than b
     * @param a HugeInt
     * @param b HugeInt
     * @return HugeInt representing the difference of a - b
     */
    public static HugeInt subtractHugeInt(HugeInt a, HugeInt b){
        int sum;
        int [] sumArr; // array to hold the new sum, little endian
        int [] aVal;
        int [] bVal;
        int length; // how many digits the new sum can hold
        // which is the greater number of digits of a and b
        // + 1 for the potential carry

        // a has less digits than b
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits() +1;
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // b has more digits or both have the same number of digits
        else{
            length = a.getNumDigits() + 1;
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }

        if (equalsHugeInt(a,b)){
            return new HugeInt("0");
        }
        else if (!greaterThan(a, b)){
            return null;
        }

        sumArr = new int [length];
        Arrays.fill(sumArr, 0);
        aVal = a.getLittleEndian();
        bVal = b.getLittleEndian();

        for (int i = 0; i < length; i++){
            // there needs a borrow
            if (aVal[i] < bVal[i]){
                aVal[i] += 10;
                aVal[i+1] -=1;
            }
            sum = aVal[i] - bVal [i];
            sumArr[i] += sum % 10;
        }

        for(int i = 0; i < length/2; i++){
            int temp = sumArr[length-i-1];
            sumArr[length-i-1] = sumArr[i];
            sumArr[i] = temp;
        }

        return new HugeInt(Arrays.toString(sumArr).replaceAll("[^0-9]", ""));
    }


    /**
     * Multiplies multiplier by multiplicand
     * @param multiplier
     * @param multiplicand
     * @return HugeInt representing the product of multiplier and multiplicand
     */
    public static HugeInt multiplyHugeInt(HugeInt multiplier, HugeInt multiplicand){
        // a has less digits than b
        int length, subproduct;
        int [] product, multiplierVal, multiplicandVal;
        if (multiplier.getNumDigits() < multiplicand.getNumDigits()){
            length = multiplicand.getNumDigits();
            multiplier = multiplier.convertToSameLength(length);
            multiplicand = multiplicand.convertToSameLength(length);
        }
        // a has more digits or both have the same number of digits
        else{
            length = multiplier.getNumDigits();
            multiplier = multiplier.convertToSameLength(length);
            multiplicand = multiplicand.convertToSameLength(length);
        }

        product = new int [(length*2)];
        Arrays.fill(product, 0);
        multiplierVal =  multiplier.getLittleEndian();
        multiplicandVal = multiplicand.getLittleEndian();

        for (int i = 0; i < length; i++){ // the bottom number/multiplicand
            for (int j = 0; j < length; j++){
                subproduct =  multiplierVal[j] * multiplicandVal[i] + product[j+i];

                if (subproduct < 10) {
                    product[i+j] = subproduct;
                }
                // there is a carry
                else {
                    product[j+i] = subproduct % 10;
                    product[j+i+1] += subproduct /10;
                }
            }

        }

        //check to make sure that the second to last digit in little
        // endian representation does not contain a value > 9
        if (product[length*2 - 2 ] > 10){
            product[length*2 - 1 ] = product[length*2 - 2 ] /10;
            product[length*2 - 2 ] = product[length*2 - 2 ] % 10;
        }

        int pLength = product.length;

        // Represent the product from little endian to big endian
        for(int i = 0; i < pLength/2; i++){
            int temp = product[pLength-i-1];
            product[pLength-i-1] = product[i];
            product[i] = temp;
        }

        return new HugeInt(Arrays.toString(product).replaceAll("[^0-9]", ""));

    }

    /**
     * Divide dividend by divisor
     * @param dividend
     * @param divisor
     * @return a HugeInt representing dividend/divisor
     */
    public static HugeInt divideHugeInt(HugeInt dividend, HugeInt divisor) {

        // if dividend/divisor == 1
        if (equalsHugeInt(dividend, divisor)) {
            return new HugeInt("1");
        }
        // dividend < divisor, then return 0
        else if (lessThan(dividend, divisor)) {
            return new HugeInt("0");

        }
        // divide by zero error
        else if (divisor.isZero()) {
            return null;
        }
        // 0 / divisor case
        else if (dividend.isZero()) {
            return dividend;
        }

        int length;
        int[] quotient, dividendVal;

        // convert so they are the same length
        if (dividend.getNumDigits() < divisor.getNumDigits()) {
            length = divisor.getNumDigits();
            dividend = dividend.convertToSameLength(length);
            divisor = divisor.convertToSameLength(length);
        }
        // a has more digits or both have the same number of digits
        else {
            length = dividend.getNumDigits();
            dividend = dividend.convertToSameLength(length);
            divisor = divisor.convertToSameLength(length);
        }

        int dividendZeroPad = dividend.getZeroPad();
        dividendVal = dividend.getBigEndian();

        // grab the value of the divisor without padding zeros
        String divisorString = divisor.bigEndianToString();
        StringBuilder s = new StringBuilder("");
        HugeInt remainder;
        divisor = new HugeInt(divisorString);
        quotient = new int[length];
        Arrays.fill(quotient, 0);

        // implementation of long division
        for (int i = dividendZeroPad; i < length; i++) {
            s.append(Integer.toString(dividendVal[i]));
            remainder = new HugeInt(s.toString());
            while (greaterThan(remainder, divisor) || equalsHugeInt(remainder, divisor)) {
                remainder = subtractHugeInt(remainder, divisor);
                quotient[i] += 1;
            }
            s = new StringBuilder(remainder.bigEndianToString());
        }

        remainder = new HugeInt(s.toString());

        return new HugeInt(Arrays.toString(quotient).replaceAll("[^0-9]", ""));
    }

    /**
     * Determines greatest common denominator
     *
     * @param  m An integer
     * @param  n An integer
     * @return GCD of m and n
     */
    public static HugeInt gcd(HugeInt m, HugeInt n) {
        HugeInt temp;

        if (lessThan(m, n))
            return gcd(n, m);

        while (!equalsHugeInt(n, new HugeInt("0"))) { // n != zero
            temp = n;
            n = modHugeInt(m, n);
            m = temp;
        }

        return m;
    }

    /**
     * a % b
     * @param a
     * @param b
     * @return HugeInt representing a % b
     */
    public static HugeInt modHugeInt(HugeInt a, HugeInt b){
        System.out.println("hello");
        // if a == b , a % b = 0
        if (equalsHugeInt(a, b)) {
            return new HugeInt("0");

        }
        // if  a < b, a % b = a
        else if (lessThan(a, b)) {
            return a;
        }
        // error
        else if (b.isZero()) {
            return null;
        } else if (a.isZero()) {
            return a;
        }

        int length;
        int [] quotient, dividendVal;
        if (a.getNumDigits() < b.getNumDigits()){
            length = b.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        // a has more digits or both have the same number of digits
        else{
            length = a.getNumDigits();
            a = a.convertToSameLength(length);
            b = b.convertToSameLength(length);
        }
        int dividendZeroPad = a.getZeroPad();
        dividendVal = a.getBigEndian();
        // grab the value of the divisor without padding zeros
        String divisorString = b.bigEndianToString();
        StringBuilder s = new StringBuilder("");
        HugeInt remainder;
        b = new HugeInt(divisorString);
        quotient = new int [length];
        Arrays.fill(quotient, 0);
        remainder = new HugeInt("0");
        // long division implementation
        for(int i = dividendZeroPad; i < length; i++){
            s.append(Integer.toString(dividendVal[i]));
            remainder = new HugeInt(s.toString());

            while (greaterThan(remainder,b) || equalsHugeInt(remainder,b) ){

                remainder = subtractHugeInt(remainder, b);
                quotient[i] +=1;
            }
            s = new StringBuilder(remainder.bigEndianToString());
        }

        return remainder;
    }

    /**
     * Computes b^e mod m. e must be positive. This is the binary implementation.
     *
     * @param  b An integer
     * @param  e An integer
     * @param  m An integer
     * @return Result of computation.
     */
    public static HugeInt modPow2(HugeInt b, HugeInt e, HugeInt m) {
        if (equalsHugeInt(m, new HugeInt("1")))
            return new HugeInt("0");

        HugeInt result = new HugeInt("1");
        b = modHugeInt(b, m);
        while (greaterThan(e, new HugeInt("0"))) {
            if (equalsHugeInt(modHugeInt(e, new HugeInt("2")), new HugeInt("1"))) {
                result = modHugeInt(multiplyHugeInt(result, b), m); // result * b mod m
            }

            e = divideHugeInt(e, new HugeInt("2"));
            b = modHugeInt(multiplyHugeInt(b, b), m); // b*b mod m
        }

        return result;
    }

    public static void main(String[] args) {

    }

}
