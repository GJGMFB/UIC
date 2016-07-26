import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Program extends JFrame implements ActionListener {
    private JButton btnGenerate;
    private JTextField txtGenerate;

    private JButton btnBlockFile;
    private JTextField txtBlockSize;
    private JButton btnBlock;
    private JButton btnUnblock;
    private JLabel lblBlock;
    private File messageFile;
    private String blockedFilePath;
    private JFileChooser fc;

    private JButton btnEncrypt;
    private JButton btnInputFile;
    private File inputFile;
    private JButton btnKey;
    private File keyFile;
//    private JTextField txtOut;
//    private JLabel lblPublicKeyFile;
//    private JLabel lblPrivateKeyFile;
    private String encryptedFile;

    private RSA rsa;

    Program() {
        // Initial set-up
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setTitle("RSA Encryption/Decryption");
        rsa = new RSA();
        fc = new JFileChooser();

        // Panels
        JPanel pGenerate = new JPanel(new FlowLayout());
        JPanel pBlock = new JPanel();
        pBlock.setLayout(new BoxLayout(pBlock, BoxLayout.PAGE_AXIS));
        JPanel pCrypt = new JPanel();
        pCrypt.setLayout(new BoxLayout(pCrypt, BoxLayout.PAGE_AXIS));

        // Generate
        JLabel lblGenerate = new JLabel("Filename (without extension):");
        txtGenerate = new JTextField(10);
        btnGenerate = new JButton("Generate Keys");
        btnGenerate.addActionListener(this);
        pGenerate.add(lblGenerate);
        pGenerate.add(txtGenerate);
        pGenerate.add(btnGenerate);

        // Block / Unblock
        btnBlockFile = new JButton("Choose a file...");
        btnBlockFile.addActionListener(this);
        btnBlockFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        pBlock.add(btnBlockFile);

        JLabel lblBlockSize = new JLabel("Block size:");
        lblBlockSize.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtBlockSize = new JTextField(10);
        txtBlockSize.setMaximumSize(txtBlockSize.getPreferredSize());
        txtBlockSize.setAlignmentX(Component.CENTER_ALIGNMENT);
        pBlock.add(lblBlockSize);
        pBlock.add(txtBlockSize);

        btnBlock = new JButton("Block the file");
        btnBlock.addActionListener(this);
        btnBlock.setAlignmentX(Component.CENTER_ALIGNMENT);
        pBlock.add(btnBlock);

        btnUnblock = new JButton("Unblock the file");
        btnUnblock.addActionListener(this);
        btnUnblock.setAlignmentX(Component.CENTER_ALIGNMENT);
        pBlock.add(btnUnblock);

        lblBlock = new JLabel();
        lblBlock.setAlignmentX(Component.CENTER_ALIGNMENT);
        pBlock.add(lblBlock);

        // Encrypt / Decrypt
        btnInputFile = new JButton("Input file...");
        btnInputFile.addActionListener(this);
        btnInputFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        pCrypt.add(btnInputFile);

        btnKey = new JButton("RSA key file...");
        btnKey.addActionListener(this);
        btnKey.setAlignmentX(Component.CENTER_ALIGNMENT);
        pCrypt.add(btnKey);

//        txtOut = new JTextField(10);
//        txtOut.setMaximumSize(txtBlockSize.getPreferredSize());
//        txtOut.setAlignmentX(Component.CENTER_ALIGNMENT);
//        pCrypt.add(txtOut);

        btnEncrypt = new JButton("Encrypt/Decrypt message");
        btnEncrypt.addActionListener(this);
        btnEncrypt.setAlignmentX(Component.CENTER_ALIGNMENT);
        pCrypt.add(btnEncrypt);

//        lblPublicKeyFile = new JLabel();
//        lblPublicKeyFile.setAlignmentX(Component.CENTER_ALIGNMENT);
//        pCrypt.add(lblPublicKeyFile);

//        lblPrivateKeyFile = new JLabel();
//        lblPrivateKeyFile.setAlignmentX(Component.CENTER_ALIGNMENT);
//        pCrypt.add(lblPrivateKeyFile);

        // Tab interface
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Generate Keys", pGenerate);
        tabbedPane.addTab("Block/Unblock Message File", pBlock);
        tabbedPane.addTab("Encrypt/Decrypt Message", pCrypt);

        add(tabbedPane); // Add tab pane to window
        setSize(600, 500);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // Generate keys
        if (e.getSource() == btnGenerate) {
            rsa.generatePrimes();
            rsa.generateKeys();
            String path = rsa.writePublicKey(txtGenerate.getText());
            rsa.writePrivateKey(txtGenerate.getText());
            JOptionPane.showMessageDialog(Program.this, "<html>Public and private keys generated!<br>File location: " + path + "</html>");
        }

        // File blocking
        if (e.getSource() == btnBlockFile) {
            int returnVal = fc.showOpenDialog(Program.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                messageFile = fc.getSelectedFile();
                lblBlock.setText("<html><b>Using file " + messageFile.getName() + "</b></html>"); // Update label with name of file
            }
        }

        if (e.getSource() == btnBlock) {
            // Check if block size has a value
            if (txtBlockSize.getText().length() == 0) {
                JOptionPane.showMessageDialog(Program.this, "Invalid block size");
                return;
            }

            int blockSize = Integer.parseInt(txtBlockSize.getText()); // Store the block size
            blockedFilePath = rsa.block(messageFile, "blockedMessage.txt", blockSize);
            JOptionPane.showMessageDialog(Program.this, "<html>Successfully blocked message. Path of output file:<br>" + blockedFilePath + "</html>");
        }

        if (e.getSource() == btnUnblock) {
            // Check if block size has a value
            if (txtBlockSize.getText().length() == 0) {
                JOptionPane.showMessageDialog(Program.this, "Invalid block size");
                return;
            }

            int blockSize = Integer.parseInt(txtBlockSize.getText()); // Store the block size
            blockedFilePath = rsa.unblock(messageFile, "unblockedMessage.txt", blockSize);
            JOptionPane.showMessageDialog(Program.this, "<html>Successfully blocked message. Path of output file:<br>" + blockedFilePath + "</html>");
        }

        // Encrypt / Decrypt
        if (e.getSource() == btnInputFile) {
            int returnVal = fc.showOpenDialog(Program.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFile = fc.getSelectedFile();
            }
        }

        if (e.getSource() == btnKey) {
            int returnVal = fc.showOpenDialog(Program.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                keyFile = fc.getSelectedFile();
            }
        }

        if (e.getSource() == btnEncrypt) {
            rsa = new RSA();
            String path = rsa.encryptOrDecrypt(inputFile, keyFile);
            JOptionPane.showMessageDialog(Program.this, "<html>Path of output file:<br>" + path + "</html>");
        }

    }

    public static void main(String args[]) {
        Program program = new Program();
    }
}
