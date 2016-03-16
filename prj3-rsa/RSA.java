import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class RSA {
    private HugeInt n;
    private HugeInt p;
    private HugeInt q;
    private HugeInt phi;

    private HugeInt e;
    private HugeInt d;
    public boolean hasKeys = false;

    enum errCode {
        NONE, FILENOTEXIST
    }


    /**
     * Creates public key, e. Requires n and phi.
     */
    private void publicKey() {
        HugeInt i = new HugeInt("2");

        while(!HugeInt.equalsHugeInt(i, n)) {
            //System.out.println("\tTrying i = " + i.toString());
            if (HugeInt.equalsHugeInt(HugeInt.gcd(phi, i), new HugeInt("1"))) { // gcd(i, phi) == 1
                e = i;
                break;
            }

            i = HugeInt.addHugeInt(i, new HugeInt("1")); // i++
        }
    }

    /**
     * Creates private key, d. Requires n and phi
     */
    private void privateKey() {
        HugeInt k = new HugeInt("1");
        HugeInt dprime;

        while (true) {
            dprime = HugeInt.addHugeInt(HugeInt.multiplyHugeInt(k, phi), new HugeInt("1")); // k*phi + 1
            if (HugeInt.equalsHugeInt(HugeInt.modHugeInt(dprime, e), new HugeInt("0"))) { // dprime mod e == 0
                d = HugeInt.divideHugeInt(dprime, e); // dprime / e
                break;
            }

            k = HugeInt.addHugeInt(k, new HugeInt("1")); // k++
        }
    }

    /**
     * Writes the public key to an XML file.
     * @param filename Name of the file
     * @return Path of written file
     */
    public String writePublicKey(String filename) {
        String path = null;

        try {
            // Create a file if it does not exist
            File publicKey = new File(".", filename + "-public.xml");
            PrintWriter output = new PrintWriter(new FileWriter(publicKey));

            // Write e and n to file
            output.print("<rsakey><evalue>" + e.bigEndianToString() + "</evalue><nvalue>" + n.bigEndianToString() + "</nvalue></rsakey>");

            output.close();

            path = publicKey.getAbsoluteFile().getParent();
        } catch( IOException ex ) {
            ex.printStackTrace();
        }

        return path;
    }

    /**
     * Writes the private key to an XML file
     * @param filename Name of the file
     * @return Path of written file
     */
    public String writePrivateKey(String filename) {
        String path = null;

        try {
            // Create a file if it does not exist
            File privateKey = new File(".", filename + "-private.xml");
            PrintWriter output = new PrintWriter(new FileWriter(privateKey));

            // Write e and n to file
            output.print("<rsakey><dvalue>" + d.bigEndianToString() + "</dvalue><nvalue>" + n.bigEndianToString() + "</nvalue></rsakey>");

            output.close();

            path = privateKey.getAbsoluteFile().getParent();
        } catch( IOException ex ) {
            ex.printStackTrace();
        }

        return path;
    }

    /**
     * Chunks an input file based on the block size parameter. Blocking size must be an even number.
     * @param fileIn
     * @param fileOut
     * @param blockSize
     * @return Path to the file output
     */
    public String block(File fileIn, String fileOut, int blockSize) {
        int c;
        String x = "";
        String buffer = "";
        long len;
        long numBlocks;
        String path = null;

        // Create a file if it does not exist
        try {
            File out = new File(".", fileOut);
            PrintWriter output = new PrintWriter(new FileWriter(out));
            path = out.getAbsoluteFile().getParent();

            FileReader fr = new FileReader(fileIn);
            len = fileIn.length();
            numBlocks = len / blockSize;

            for (long i = 0; i < numBlocks + 1; i++) {
                buffer = ""; // Clear string each line

                for (int j = 0; j < blockSize; j++) {
                    c = fr.read();

                    if (c == -1) {
                        break;
                    }

                    // Check for special chars
                    if (c == 0) {
                        x = "00";
                    }
                    if (c == 11) {
                        x = "01";
                    }
                    if (c == 9) {
                        x = "02";
                    }
                    if (c == 10) {
                        x = "03";
                    }
                    if (c == 13) {
                        x = "04";
                    }

                    // The rest of the character set
                    x = String.valueOf(c - 27);

                    // Add a zero in front if single digit
                    if (x.length() == 1) {
                        x = "0" + x;
                    }

                    buffer = x + buffer;
                    //output.print(x);
                }

                output.print(buffer); // Print line of converted text

                // If on last iteration
                if (i < numBlocks) {
                    output.println();
                }
            }

            // Check if we need to pad with zeros
            long z = (len*2) % blockSize;
            if (z != 0) {
                // Write z * 2 zeros
                //z *= 2;
                for (long k = 0; k < z; k++) {
                    output.print("0");
                }
            }

            output.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return path;
    }

    /**
     * Reconstructs a blocked file into the original form
     * @param fileIn Input file
     * @param fileOut Output file
     * @return Path of output file
     */
    public String unblock(File fileIn, String fileOut, int blockSize) {
        String line;
        String path = null;
        String buffer = "";
        char c;

        try {
            File out = new File(".", fileOut);
            PrintWriter output = new PrintWriter(new FileWriter(out));
            path = out.getAbsoluteFile().getParent();

            BufferedReader in = new BufferedReader(new FileReader(fileIn));
            line = in.readLine();
            while (line != null) {
                int len = line.length();
                StringBuilder s;

                // Place back leading zeros
                if (len != blockSize*2) {
                    // Add a leading zero
                    line = "0" + line;
                }

                buffer = ""; // Clear buffer for each line

                for (int i = 0; i < len; i += 2) {
                    s = new StringBuilder(line);
                    int temp = Integer.parseInt(s.substring(i, i + 2).toString()); // Get 2 digits and convert to integer

                    // Skip null
                    if (temp == 0)
                        continue;

                    // Check special chars
                    if (temp == 1) {
                        output.print((char) 11);
                        continue;
                    } else if (temp == 2) {
                        output.print((char) 9);
                        continue;
                    } else if (temp == 3) {
                        output.print((char) 10);
                        continue;
                    } else if (temp == 4) {
                        output.print((char) 13);
                        continue;
                    }

                    // Rest of ASCII chars
                    c = (char) (temp + 27);
                    buffer = "" + c + buffer;
                    //output.print(Character.toChars(temp + 27));
                }

                output.print(buffer);
                line = in.readLine(); // Read next line
            }

            output.close();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return path;
    }

    /**
     * Encrypts or decrypts input file based on the given XML key file
     * @param inputFile Message to be encrypted or decrypted
     * @param keyFile XML file containing private or public key
     * @return Path to the output file
     */
    public String encryptOrDecrypt(File inputFile, File keyFile) {
        boolean encrypt = false;
        boolean decrypt = false;
        String outFile = null;

        // Read key file
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(keyFile);
            dom.getDocumentElement().normalize();

            // Check if evalue exists
            if (dom.getElementsByTagName("evalue").item(0) != null) {
                e = new HugeInt(dom.getElementsByTagName("evalue").item(0).getTextContent());
                encrypt = true;
            }

            // Check if dvalue exists
            if (dom.getElementsByTagName("dvalue").item(0) != null) {
                d = new HugeInt(dom.getElementsByTagName("dvalue").item(0).getTextContent());
                decrypt = true;
            }

            n = new HugeInt(dom.getElementsByTagName("nvalue").item(0).getTextContent()); // Get n value

            hasKeys = true;
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (SAXException e1) {
            e1.printStackTrace();
        }

        if (encrypt) {
            outFile = encryptFile(inputFile.getAbsolutePath());
        }

        if (decrypt) {
            outFile = decryptFile(inputFile.getAbsolutePath());
        }

        return outFile;
    }

    /**
     * Encrypts each line of the input file to a file called "encryptedMessage.txt"
     * @param file File path
     * @return The path of the encrypted file
     */
    public String encryptFile(String file) {
        String line;
        String path = null;
        File message = new File(file);

        try {
            File out = new File(".", "encryptedMessage.txt");
            PrintWriter output = new PrintWriter(new FileWriter(out));
            path = out.getAbsoluteFile().getParent();

            BufferedReader in = new BufferedReader(new FileReader(message));
            line = in.readLine();

            // For each line of the blocked file
            while (line != null) {
                output.println(encrypt(new HugeInt(line)).bigEndianToString()); // Write a line to the output file
                line = in.readLine(); // Read next line
            }

            output.close();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return path;
    }

    /**
     * Encrypt a message using the public key
     * @param message Message to be encrypted
     * @return The encrypted message
     */
    private HugeInt encrypt(HugeInt message) {
        return HugeInt.modPow2(message, e, n);
    }

    /**
     * Encrypts each line of the input file to a file called "decryptedMessage.txt"
     * @param file File path
     * @return The path of the decrypted file
     */
    public String decryptFile(String file) {
        String line;
        String path = null;
        File message = new File(file);

        try {
            File out = new File(".", "decryptedMessage.txt");
            PrintWriter output = new PrintWriter(new FileWriter(out));
            path = out.getAbsoluteFile().getParent();

            BufferedReader in = new BufferedReader(new FileReader(message));
            line = in.readLine();

            // For each line of the blocked file
            while (line != null) {
                output.println(decrypt(new HugeInt(line)).bigEndianToString()); // Write a line to the output file
                line = in.readLine(); // Read next line
            }

            output.close();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return path;
    }

    /**
     * Decrypt a message using the private key
     * @param cipher Encrypted message to decrypt
     * @return The original message
     */
    private HugeInt decrypt(HugeInt cipher) {
        return HugeInt.modPow2(cipher, d, n);
    }

    /**
     * Select a random line from a file
     * @param f File to read
     * @return Randomly selected line from the file
     * @throws FileNotFoundException
     */
    private String randomLine(File f) throws FileNotFoundException {
        String result = null;
        Random rand = new Random();
        int n = 0;

        for (Scanner sc = new Scanner(f); sc.hasNext();) {
            n++;
            String line = sc.nextLine();
            if (rand.nextInt(n) == 0) {
                result = line;
            }
        }

        return result;
    }

    /**
     * Selects random primes for p and q. Calculates n and phi.
     */
    public void generatePrimes() {
        boolean pSet = false;
        boolean qSet = false;
        File f = new File(getClass().getResource("primeNumbers.rsc").getPath());

        while (!pSet) {
            try {
                String s = randomLine(f);
                p = new HugeInt(s);
                pSet = true;
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        while (!qSet) {
            try {
                String s = randomLine(f);
                q = new HugeInt(s);

                // Redo if p == q
                if (!HugeInt.equalsHugeInt(p, q))
                    qSet = true;
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        n = HugeInt.multiplyHugeInt(p, q); // Returns p*q
        phi = HugeInt.multiplyHugeInt(HugeInt.subtractHugeInt(p, new HugeInt("1")), HugeInt.subtractHugeInt(q, new HugeInt("1"))); // (p - 1)(q - 1)
    }

    /**
     * Generates the public and private keys.
     */
    public void generateKeys() {
        publicKey(); // Generate public key
        privateKey(); // Generate private key
        hasKeys = true;
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.p = new HugeInt("87178291199");
        rsa.q = new HugeInt("99194853094755497");

        //rsa.n = rsa.p.multiply(rsa.q); // Returns p*q
        //rsa.phi = rsa.p.subtract(BigInteger.ONE).multiply(rsa.q.subtract(BigInteger.ONE)); // (p - 1)(q - 1)

        System.out.println("N = " + rsa.n.toString());
        System.out.println("Phi = " + rsa.phi.toString());

        System.out.println("Generating public key...");
        rsa.publicKey();
        System.out.println("Public Key, e = " + rsa.e.toString());

        System.out.println("Generating private key...");
        rsa.privateKey();
        System.out.println("Private key, d = " + rsa.d.toString());
    }
}
