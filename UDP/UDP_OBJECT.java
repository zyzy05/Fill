import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_OBJECT {
    public static void main(String[] args) throws Exception {
        String serverIP = "36.50.135.242";
        int serverPort = 2209;
        String studentCode = "B23DCCN951";
        String questionCode = "JZ2m0SjI";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);
            InetAddress address = InetAddress.getByName(serverIP);

            // 1. GUI YEU CAU
            String request =
                ";" + studentCode + ";" + questionCode;
            byte[] requestBytes =
                request.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                requestBytes, requestBytes.length,
                address, serverPort
            ));

            // 2. NHAN GOI TIN
            byte[] buffer = new byte[65507];
            DatagramPacket packet =
                new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            byte[] data = packet.getData();
            int length = packet.getLength();

            // 3. DOC REQUEST ID (8 BYTE DAU)
            String requestId = new String(
                data, 0, 8, StandardCharsets.UTF_8
            );

            // 4. DOC DOI TUONG
            ByteArrayInputStream bais =
                new ByteArrayInputStream(data, 8, length - 8);

            Object object;
            try (ObjectInputStream ois =
                     new ObjectInputStream(bais)) {
                object = ois.readObject();
            }

            // 5. XU LY DOI TUONG
            // TODO:
            // Ep kieu object sang Student/Customer...
            // Lay thuoc tinh
            // Sua thuoc tinh theo yeu cau

            // 6. DONG GOI DOI TUONG
            ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

            try (ObjectOutputStream oos =
                     new ObjectOutputStream(baos)) {
                oos.writeObject(object);
            }

            byte[] objectBytes = baos.toByteArray();

            // 7. GHEP REQUEST ID + OBJECT
            byte[] idBytes =
                requestId.getBytes(StandardCharsets.UTF_8);

            byte[] result = new byte[8 + objectBytes.length];

            System.arraycopy(
                idBytes, 0, result, 0, 8
            );
            System.arraycopy(
                objectBytes, 0, result, 8,
                objectBytes.length
            );

            // 8. GUI LAI SERVER
            socket.send(new DatagramPacket(
                result, result.length,
                packet.getAddress(), packet.getPort()
            ));
        }
    }
}