package me.ruslanys.vkmusic;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Ignore
public class EncodingTest {

    @Test
    public void test() throws IOException {
        convert("/home/ruslanys/in.txt", "/home/ruslanys/out.txt", "windows-1251", "windows-1251");
    }

    @Test
    public void myMethod() throws UnsupportedEncodingException {
        String x = "Ëÿïèñ Òðóáåöêîé";
        x = "Hello";
        System.out.println(x);

        String utf8String= new String(x.getBytes("ISO-8859-1"), "windows-1251");
        System.out.println(utf8String);
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        String x = "Ëÿïèñ Òðóáåöêîé";
        System.out.println(x);

        byte[] bytes = x.getBytes();
        System.out.println(new String(bytes, "ISO8859_1"));

//        System.out.println(new String(new String(x.getBytes("UTF-8"), "windows-1251").getBytes("UTF-8")));

//        System.out.println(new String("Ляпис Трубецкой".getBytes(), "windows-1251"));
//        String s = new String(x.getBytes(), "cp1251");
//        System.out.println(s);

//        System.out.println(new String(convertEncoding(x.getBytes(), "UTF-8", "utf-8")));
    }

    public static byte[] convertEncoding(byte[] bytes, String from, String to) throws UnsupportedEncodingException {
        return new String(bytes, from).getBytes(to);
    }

//    public static String guessEncoding(byte[] bytes) {
//        String DEFAULT_ENCODING = "UTF-8";
//        org.mozilla.universalchardet.UniversalDetector detector =
//                new org.mozilla.universalchardet.UniversalDetector(null);
//        detector.handleData(bytes, 0, bytes.length);
//        detector.dataEnd();
//        String encoding = detector.getDetectedCharset();
//        System.out.println("Detected encoding: " + encoding);
//        detector.reset();
//        if (encoding == null) {
//            encoding = DEFAULT_ENCODING;
//        }
//        return encoding;
//    }

    public static void convert(
            String infile, //input file name, if null reads from console/stdin
            String outfile, //output file name, if null writes to console/stdout
            String from,   //encoding of input file (e.g. UTF-8/windows-1251, etc)
            String to)     //encoding of output file (e.g. UTF-8/windows-1251, etc)
            throws IOException, UnsupportedEncodingException
    {
        // set up byte streams
        InputStream in;
        if(infile != null)
            in=new FileInputStream(infile);
        else
            in=System.in;
        OutputStream out;
        if(outfile != null)
            out=new FileOutputStream(outfile);
        else
            out=System.out;

        // Use default encoding if no encoding is specified.
        if(from == null) from=System.getProperty("file.encoding");
        if(to == null) to=System.getProperty("file.encoding");

        // Set up character stream
        Reader r=new BufferedReader(new InputStreamReader(in, from));
        Writer w=new BufferedWriter(new OutputStreamWriter(out, to));

        // Copy characters from input to output.  The InputStreamReader
        // converts from the input encoding to Unicode,, and the OutputStreamWriter
        // converts from Unicode to the output encoding.  Characters that cannot be
        // represented in the output encoding are output as '?'
        char[] buffer=new char[4096];
        int len;
        while((len=r.read(buffer)) != -1)
            w.write(buffer, 0, len);
        r.close();
        w.flush();
        w.close();
    }

}
