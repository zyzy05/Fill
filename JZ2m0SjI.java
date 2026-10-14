import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class JZ2m0SjI {
    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN951";
        String qCode = "JZ2m0SjI";

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("36.50.135.242");

        // a. Gui ma sinh vien va ma cau hoi
        String message = ";" + studentCode + ";" + qCode;
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, address, 2209
        );
        socket.send(sendPacket);

        // b. Nhan goi tin tu server
        byte[] receiveData = new byte[65507];
        DatagramPacket receivePacket = new DatagramPacket(
                receiveData, receiveData.length
        );
        socket.receive(receivePacket);

        byte[] data = receivePacket.getData();
        int length = receivePacket.getLength();

        // 08 byte dau la requestId
        String requestId = new String(
                data, 0, 8, StandardCharsets.UTF_8
        );

        // Cac byte con lai la doi tuong Student
        ByteArrayInputStream bais = new ByteArrayInputStream(
                data, 8, length - 8
        );
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object student = ois.readObject();

        // c. Chuan hoa ten
        String studentName = (String) student.getClass().getMethod("getName").invoke(student);
        String[] words = studentName
                .trim()
                .toLowerCase(Locale.ROOT)
                .split("\\s+");

        StringBuilder normalizedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (normalizedName.length() > 0) {
                    normalizedName.append(" ");
                }

                normalizedName.append(
                        word.substring(0, 1).toUpperCase(Locale.ROOT)
                ).append(word.substring(1));
            }
        }

        student.getClass().getMethod("setName", String.class).invoke(student, normalizedName.toString());

        // Tao email: ten + chu cai dau cua ho va ten dem
        String emailName = words[words.length - 1];

        for (int i = 0; i < words.length - 1; i++) {
            emailName += words[i].substring(0, 1);
        }

        student.getClass().getMethod("setEmail", String.class).invoke(student, emailName + "@ptit.edu.vn");

        // d. Dong goi requestId + doi tuong Student
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(student);
        oos.flush();

        byte[] objectData = baos.toByteArray();

        byte[] result = new byte[8 + objectData.length];
        System.arraycopy(
                requestId.getBytes(StandardCharsets.UTF_8),
                0, result, 0, 8
        );
        System.arraycopy(
                objectData, 0, result, 8, objectData.length
        );

        DatagramPacket resultPacket = new DatagramPacket(
                result, result.length, address, 2209
        );
        socket.send(resultPacket);

        // Ket thuc
        ois.close();
        oos.close();
        socket.close();
    }
}